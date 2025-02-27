import numpy as np


class environment:
    """
    This environment's size = 5×5[m^2]
    """

    def __init__(
        self,
        antennas_space,
        antennas_population,
        frequency,
        tag: list,
        k,
        sample_count,
    ) -> None:
        self.antennas_space = antennas_space
        self.antennas_population = antennas_population
        self.tag = np.array(tag)
        self.frequency = frequency
        self.k = k
        self.sample_count = sample_count

        self.antennas = np.array(
            [[m * antennas_space, 0] for m in np.arange(antennas_population)]
        )
        self.vectors = self.tag - self.antennas
        self.distances = np.linalg.norm(self.vectors, ord=2, axis=1)

    def get_phases(self):
        rng = np.random.default_rng()
        c = 2.998e8
        actual_phases = (-4 * np.pi * self.distances * self.frequency / c) % (2 * np.pi)
        actual_phases = np.tile(
            actual_phases,
            (self.sample_count, 1),
        )
        phases = (
            actual_phases + self.k * np.arcsin(self.vectors[:, 0] / self.distances)
        ) % (2 * np.pi)
        phases_with_noise = (
            phases
            + rng.normal(
                loc=0.0,
                scale=0.01,
                size=(self.sample_count, self.antennas_population),
            )
        ) % (2 * np.pi)
        return (actual_phases, phases, phases_with_noise)

    def get_propagation_list(self):
        propagations = np.array([[ant, self.tag] for ant in self.antennas])
        return propagations

    def get_n(self):
        rng = np.random.default_rng()
        c = 2.998e8
        actual_n = (-4 * np.pi * self.distances * self.frequency / c) // (2 * np.pi)
        actual_n = np.tile(
            actual_n,
            (self.sample_count, 1),
        )
        n = (
            -4 * np.pi * self.distances * self.frequency / c
            + self.k * np.arcsin(self.vectors[:, 0] / self.distances)
        ) // (2 * np.pi)
        n_with_noise = (
            -4 * np.pi * self.distances * self.frequency / c
            + self.k * np.arcsin(self.vectors[:, 0] / self.distances)
            + rng.normal(
                loc=0.0,
                scale=0.001,
                size=(self.sample_count, self.antennas_population),
            )
        ) // (2 * np.pi)
        return (actual_n, n, n_with_noise)
