class antenna:
    def __init__(
        self,
        name,
        position,
        frequency,
        tx_power,
        antenna_gain,
    ) -> None:
        self.name = name
        self.position = position
        self.frequency = frequency
        self.tx_power = tx_power
        self.antenna_gain = antenna_gain
