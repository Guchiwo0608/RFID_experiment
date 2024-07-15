import numpy as np


class NFL:
    def __init__(
        self,
        antenna_poplulation: int,
        tag_population: int,
        sampling_count: int,
        antenna_space: float,
        frequency: float,
        theta_range: np.ndarray,
    ):
        self.antenna_population = antenna_poplulation  # The number of Antenna
        self.tag_population = tag_population  # The number of RF-tag
        self.sampling_count = sampling_count  # The number of sample
        self.antenna_space = antenna_space  # The space between antennas
        self.wave_length = 2.998e8 / frequency  # The wave length of signal

    # def
