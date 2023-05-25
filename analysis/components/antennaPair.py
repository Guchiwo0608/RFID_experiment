from components.antenna import Antenna
from components.point3d import Point3d


class AntennaPair:
    def __init__(self, ant1: Antenna, ant2: Antenna) -> None:
        self.ant1 = ant1
        self.ant2 = ant2
        self.mid_point = Point3d(
            x=(self.ant1.point.x + self.ant2.point.x) / 2,
            y=(self.ant1.point.y + self.ant2.point.y) / 2,
            z=(self.ant1.point.z + self.ant2.point.z) / 2,
        )
