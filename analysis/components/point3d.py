import numpy as np


class Point3d:

    def __init__(self, x, y, z) -> None:
        self.x = x
        self.y = y
        self.z = z

    def culDistance(self, point) -> float:
        return np.sqrt(pow(self.x-point.x, 2) + pow(self.y-point.y, 2) + pow(self.z-point.z, 2))
