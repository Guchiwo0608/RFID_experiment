import numpy as np
from .antenna import antenna
from .tag import tag
from .obstacle import obstacle


class environment:
    """
    This environment's size = 20×20[m^2]
    """

    def __init__(
        self,
        ref_coef,
        antennas: list[antenna],
        tags: list[tag],
        obstacles: list[obstacle],
        max_order,
    ) -> None:
        self.ref_coef = ref_coef
        self.antennas = antennas
        self.tags = tags
        self.obstacles = obstacles
        self.max_order = max_order

        self.vertexes = np.array(
            [
                [0, 0],
                [0, 20],
                [20, 20],
                [20, 0],
            ]
        )
