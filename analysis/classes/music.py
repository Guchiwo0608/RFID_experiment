import numpy as np
from scipy.signal import find_peaks


class MUSIC:
    """
    The MUSIC (Multiple Signal Classification) algorithm is a well-known technique
    used in signal processing, particularly for estimating the directions of arrival (DOA)
    of signals received by an array of sensors, such as antennas. It is widely used
    in applications like radar, sonar, and wireless communications. The algorithm is
    based on the eigenvalue decomposition of the covariance matrix of the received
    signals and exploits the orthogonality between the signal subspace and the noise subspace.
    This MUSIC algorithm was implemented for Impinj Speedway R420.
    """

    def __init__(
        self,
        antenna_poplulation: int,
        tag_population: int,
        sampling_count: int,
        antenna_space: float,
        frequency: float,
        theta_range: np.ndarray,
    ) -> None:
        self.antenna_population = antenna_poplulation  # The number of Antenna
        self.tag_population = tag_population  # The number of RF-tag
        self.sampling_count = sampling_count  # The number of sample
        self.antenna_space = antenna_space  # The space between antennas
        self.theta_range = np.radians(theta_range)  # The range of spectrum.
        self.wave_length = 2.998e8 / frequency  # The wave length of signal

    """
    Input matrix is sampling_count × tags_population × antenna_population 

    Example
    [[[4.02518728 3.50131308 2.52308457]
    [6.00305148 3.19901615 6.23070511]]

    [[4.50976474 3.64524377 2.42001839]
    [6.25505739 3.14842997 6.26668333]]

    [[4.08961369 3.57270837 2.38391037]
    [6.03067726 2.92550832 6.28117113]]

    ...

    [[4.16041324 3.76208283 2.44170307]
    [6.12129465 3.32331594 5.92956072]]

    [[4.10978059 3.39577105 2.4249574 ]
    [6.13837324 2.92217933 6.13503938]]

    [[4.11434317 3.46757468 2.77127208]
    [5.81831824 2.84609477 6.20842297]]]
    """

    def rfmirror_modeling(self, phase_diff_data: np.ndarray):
        phase_diff_data = np.where(
            (-np.pi <= phase_diff_data) & (phase_diff_data < -np.pi / 2),
            phase_diff_data - np.pi,
            phase_diff_data,
        )

        phase_diff_data = np.where(
            (np.pi / 2 <= phase_diff_data) & (phase_diff_data <= np.pi),
            phase_diff_data + np.pi,
            phase_diff_data,
        )

        return phase_diff_data

    def get_steering_vector_data_list(self, phase_data_list: np.ndarray):

        phase_data_list = -(phase_data_list + (2 * np.pi + 0.5)) % (np.pi) - 0.5

        phase_diff_data_list = np.array(
            [
                np.append(
                    np.zeros((self.tag_population, 1)), np.diff(data, n=1), axis=1
                ).T
                for data in phase_data_list
            ]
        )

        m = np.array(
            [
                [antenna + 1] * self.tag_population
                for antenna in range(self.antenna_population)
            ]
        )

        # phase_diff_data_list = np.array(
        #     [
        #         np.concatenate(
        #             [
        #                 np.zeros((1, self.tag_population)),
        #                 np.diff(
        #                     phase_data_list,
        #                 ),
        #             ]
        #         )(-np.tile(data[:, 0], (1, self.antenna_population))).T
        #         for data in phase_data_list
        #     ]
        # )

        # m = np.array([m for m in range(1, self.antenna_population + 1)] * )

        phase_diff_data_list = self.rfmirror_modeling(phase_diff_data_list)

        steering_vector_data_list = np.exp(-(m - 1) * phase_diff_data_list * 1j)

        return steering_vector_data_list

    def get_correlation_matrix(
        self,
        steering_vector_data_list: list,
    ):
        shape = (self.antenna_population, self.antenna_population)
        cor_sum = np.zeros(shape=shape)

        for s in steering_vector_data_list:
            cor_sum = cor_sum + np.dot(s, np.conjugate(s.T))
        correlation_matrix = cor_sum / self.sampling_count
        return correlation_matrix

    # Get noise subspace with eigenvalue decomposition
    def get_noise_subspace(self, correlation_matrix):

        # eigenvalue decomposition
        eig_value, eig_vector = np.linalg.eig(correlation_matrix)

        # sort the eigenvalue
        sort = np.argsort(-np.abs(eig_value))
        eig_value = eig_value[sort]

        # sort the eigenvector by the result of the previous sort.
        eig_vector = eig_vector[:, sort]
        noise_subspace = eig_vector[:, self.tag_population :]
        return noise_subspace

    # Get music spectrum
    def get_music_spectrum(self, phase_data_list: np.ndarray):
        steering_vector_data_list = self.get_steering_vector_data_list(
            phase_data_list=phase_data_list
        )
        correlation_matrix = self.get_correlation_matrix(
            steering_vector_data_list=steering_vector_data_list,
        )
        noise_subspace = self.get_noise_subspace(correlation_matrix=correlation_matrix)
        noise_subspaceH = np.conjugate(noise_subspace.T)

        music_spectrum = np.empty(0)
        for theta in self.theta_range:

            # Get the steering vector of each angle.
            m = np.array([antenna + 1 for antenna in range(self.antenna_population)])
            aH = np.exp(
                -4j
                * np.pi
                * (m - 1)
                * self.antenna_space
                * np.sin(theta)
                / self.wave_length,
            )

            a = np.conjugate(aH.T)
            spec = (
                1 / np.dot(np.dot(np.dot(aH, noise_subspace), noise_subspaceH), a).real
            )
            music_spectrum = np.append(
                music_spectrum,
                spec,
            )
        return music_spectrum

    # Searching the peak of music spectrum, the AoA can be estimated.
    def peak_search(self, music_spectrum):
        estimated_angles = (
            self.theta_range[
                find_peaks(music_spectrum, prominence=1.0, distance=180)[0]
            ]
            * 180
            / np.pi
        )
        return estimated_angles

    def get_error_list(self, phase_data_list: np.ndarray, real_angle: float):
        result = np.empty(self.sampling_count)
        steering_vector_list = self.get_steering_vector_data_list(phase_data_list)
        for i in range(self.sampling_count):
            steering_vector = steering_vector_list[i]
            cor = np.dot(steering_vector, np.conjugate(steering_vector.T))
            eig_value, eig_vector = np.linalg.eig(cor)
            sort = np.argsort(-np.abs(eig_value))
            eig_value = eig_value[sort]
            eig_vector = eig_vector[:, sort]
            noise_subspace = eig_vector[:, self.tag_population :]
            noise_subspaceH = np.conjugate(noise_subspace.T)
            m = np.array([antenna + 1 for antenna in range(self.antenna_population)])
            music_spectrum = np.empty(0)
            for theta in self.theta_range:
                aH = np.exp(
                    -4j
                    * np.pi
                    * (m - 1)
                    * self.antenna_space
                    * np.sin(theta)
                    / self.wave_length,
                )
                a = np.conjugate(aH.T)
                spec = (
                    1
                    / np.dot(
                        np.dot(np.dot(aH, noise_subspace), noise_subspaceH), a
                    ).real
                )
                music_spectrum = np.append(
                    music_spectrum,
                    spec,
                )
            estimated_angle = (
                self.theta_range[
                    find_peaks(music_spectrum, prominence=1.0, distance=180)[0]
                ]
                * 180
                / np.pi
            )[0]
            error = abs(estimated_angle - real_angle)
            result[i] = error
            print(f"estimated angle: {estimated_angle}, error: {error}")
        return result

    def get_cdf(self, error_list: np.ndarray):
        result = np.empty((400))
        x_list = np.linspace(0, 40, 400)
        for i in range(400):
            result[i] = np.count_nonzero(error_list <= x_list[i]) / self.sampling_count
        return result
