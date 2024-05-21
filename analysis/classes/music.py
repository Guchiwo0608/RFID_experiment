import cmath
import numpy as np
import pandas as pd
from scipy.signal import find_peaks

import cmath
from scipy.signal import find_peaks


class MUSIC:
    """
    MUSIC Method (Multiple Signal Classification Method)
    The MUSIC (Multiple Signal Classification) algorithm is a well-known technique
    used in signal processing, particularly for estimating the directions of arrival (DOA)
    of signals received by an array of sensors, such as antennas. It is widely used
    in applications like radar, sonar, and wireless communications. The algorithm is
    based on the eigenvalue decomposition of the covariance matrix of the received
    signals and exploits the orthogonality between the signal subspace and the noise subspace.
    """

    def __init__(
        self,
        antenna_poplulation: int,
        tag_population: int,
        sampling_count: int,
        antenna_space: float,
        wave_length: float,
        tx_power: float,
        theta_range: np.ndarray,
    ) -> None:
        self.antenna_population = antenna_poplulation  # The number of Antenna
        self.tag_pupulation = tag_population  # The number of RF-tag
        self.sampling_count = sampling_count  # The number of sample
        self.antenna_space = antenna_space  # The space between antennas
        self.wave_length = wave_length  # The wave length of the signal
        self.tx_power = tx_power  # The transmission power
        self.theta_range = np.radians(theta_range)  # The range of spectrum.

    # Get steering vector of each sampling time.
    def get_steering_vector_data_list(self, data: pd.DataFrame):

        columns = [f"antenna{m}.phase" for m in range(1, self.antenna_population + 1)]
        columns.extend(["samplingNumber", "tagNumber", "frequency"])
        data = data[columns]
        for m in range(1, self.antenna_population + 1):
            if m == 1:
                data[f"antenna{m}.phase_diff"] = 0
            else:
                data[f"antenna{m}.phase_diff"] = (
                    data[f"antenna{m}.phase"] - data[f"antenna{m-1}.phase"]
                )
            data[f"antenna{m}.a_theta"] = data[f"antenna{m}.phase_diff"].map(
                lambda x: cmath.exp(complex(0, -(m - 1) * x))
            )
        data_list = []
        for sample_number in range(self.sampling_count):
            tag_data = data[data["samplingNumber"] == sample_number].sort_values(
                "tagNumber"
            )
            frequency = tag_data["frequency"].mean()
            angular_velocity = 2 * np.pi * frequency
            tag_data = tag_data[
                [f"antenna{m}.a_theta" for m in range(1, self.antenna_population + 1)]
            ].to_numpy(dtype=complex)
            tag_data = np.conjugate(tag_data.T)
            transmission_wave = self.tx_power * cmath.exp(
                complex(0, (angular_velocity * 0) % (2 * np.pi))
            )
            # transmission_wave = self.tx_power * cmath.exp(complex(0, (angular_velocity * sample_number) % (2 * np.pi)))
            data_list.append({"data": tag_data, "transmission_wave": transmission_wave})
        return data_list

    def get_correlation_matrix(self, steering_vector_data_list: list):
        shape = (self.antenna_population, self.antenna_population)
        cor_sum = np.zeros(shape=shape)
        for steering_vector_data in steering_vector_data_list:
            x_t = (
                steering_vector_data["transmission_wave"] * steering_vector_data["data"]
            )
            cor_sum = cor_sum + np.dot(x_t, np.conjugate(x_t.T))
        return cor_sum / self.sampling_count

    # Get noise subspace with eigenvalue decomposition
    def get_noise_subspace(self, correlation_matrix):

        # eigenvalue decomposition
        eig_value, eig_vector = np.linalg.eig(correlation_matrix)

        # sort the eigenvalue
        sort = np.argsort(-np.abs(eig_value))
        eig_value = eig_value[sort]

        # sort the eigenvector by the result of the previous sort.
        eig_vector = eig_vector[:, sort]
        return eig_vector[:, self.tag_pupulation :]

    # Get music spectrum
    def get_music_spectrum(self, data: pd.DataFrame):
        steering_vector_data_list = self.get_steering_vector_data_list(data=data)
        correlation_matrix = self.get_correlation_matrix(
            steering_vector_data_list=steering_vector_data_list
        )
        noise_subspace = self.get_noise_subspace(correlation_matrix=correlation_matrix)
        noise_subspaceH = np.array(noise_subspace).T
        P = []
        for theta in self.theta_range:

            # Get the steering vector of each angle.
            aH = np.array(
                [
                    np.exp(
                        complex(
                            0,
                            2
                            * np.pi
                            * (m - 1)
                            * self.antenna_space
                            * np.sin(theta)
                            / self.wave_length,
                        )
                    )
                    for m in range(1, self.antenna_population + 1)
                ]
            )
            a = np.conjugate(aH.reshape(self.antenna_population, 1))
            P.append(
                np.abs(
                    1 / np.dot(np.dot(np.dot(aH, noise_subspace), noise_subspaceH), a)
                )[0]
            )
        return P

    # Searching the peak of music spectrum, the AoA can be estimated.
    def peak_search(self, music_spectrum):
        estimated_angles = self.theta_range[find_peaks(music_spectrum)[0]] * 180 / np.pi
        return estimated_angles
