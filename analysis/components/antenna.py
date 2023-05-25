from components.point3d import Point3d
from components.point2d import Point2d
import numpy as np


class Antenna:

    def __init__(self, point: Point2d, frequency: float, number: int, theta_m: float) -> None:
        self.point = point
        self.frequency = frequency
        self.number = number
        self.theta_m = theta_m * np.pi / 180

    def cul_distance_to_antenna(self, antenna):
        return self.point.culDistance(antenna.point)
