import pandas as pd
import numpy as np
import math

"""
In mock data, the phase data seem to be corrected by the wave which transmited first
in each period for making it easier to analysis.
-> s(t) can be constant value of transmission_power
"""


antenna_poplulation = 3
tags_location = np.array([-1, 2])
tag_population = tags_location.shape[0]
y_distance = 6
sample_count = 1000
frequency = 920.4e6
antenna_space = 0.25
transmission_power = 37.5
light_speed = 2.998e8
wave_length = light_speed / frequency
angular_velocity = 2 * np.pi * frequency

# tag_location[m] is distance between tag and y-axis
tag_angles = np.arctan(tags_location / y_distance)


def signal_arrival_delay(antenna_number: int, antenna_space: float, angles):
    return -(antenna_number - 1) * antenna_space * np.sin(angles) / 2.998e8


actual_phase = np.array(
    [
        (
            -angular_velocity
            * (
                2 * y_distance / np.abs(np.cos(tag_angles)) / light_speed
                + signal_arrival_delay(
                    antenna_number=antenna,
                    antenna_space=antenna_space,
                    angles=tag_angles,
                )
            )
        )
        % (2 * np.pi)
        for antenna in range(1, antenna_poplulation + 1)
    ]
)

print(actual_phase.T)

phase_diff = np.array(
    [
        (
            [0] * tag_population
            if antenna == 0
            else actual_phase[antenna] - actual_phase[antenna - 1]
        )
        for antenna in range(antenna_poplulation)
    ]
)

print(phase_diff.T)

phase_samples = np.array(
    [
        (
            actual_phase
            + np.array(
                [
                    np.random.normal(loc=0, scale=1 / 6, size=tag_population)
                    for antenna in range(antenna_poplulation)
                ]
            )
        ).T
        for i in range(1000)
    ]
)
columns = ["samplingNumber", "tagNumber"]
columns.extend([f"antenna{i}.phase" for i in range(antenna_poplulation)])

df = pd.DataFrame(columns=columns)

for i in range(1000):
    new_data = pd.DataFrame(data=phase_samples[i])
    new_data["samplingNumber"] = i
    new_data["tagNumber"] = [1, 2]
    for antenna in range(antenna_poplulation):
        new_data = new_data.rename(columns={antenna: f"antenna{antenna}.phase"})
    df = pd.concat([df, new_data])


# df.to_csv("analysis/data/example/mock_data.csv")
