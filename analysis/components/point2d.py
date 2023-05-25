import numpy as np


class Point2d:
    def __init__(self, x, y) -> None:
        self.x = x
        self.y = y

    def culDistance(self, point) -> float:
        return np.sqrt(pow(self.x-point.x, 2) + pow(self.y-point.y, 2))
