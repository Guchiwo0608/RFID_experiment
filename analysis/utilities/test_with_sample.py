import warnings
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import sys

sys.path.append("..")
from classes import MUSIC


warnings.simplefilter("ignore")

antenna_poplulation = 3
tags_location = np.array([3])
tag_population = tags_location.shape[0]
tag_numbers = [i + 1 for i in range(tag_population)]
y_distance = 6
sample_count = 1000
frequency = 920.4e6
light_speed = 2.998e8
wave_length = light_speed / frequency
antenna_space = 0.07
transmission_power = 37.5
angular_velocity = 2 * np.pi * frequency
theta_range = np.arange(-60, 60.001, 0.001)

"""
In mock data, the phase data seem to be corrected by the wave which transmited first
in each period for making it easier to analyze.
-> s(t) can be constant value of transmission_power
"""

# tag_location[m] is distance between tag and y-axis
tag_angles = np.arctan(tags_location / y_distance)
print(tag_angles * 180 / np.pi)

phase_samples = [
    np.array(
        [
            4
            * np.pi
            * (
                y_distance / np.abs(np.cos(tag_angles))
                - (antenna - 1) * antenna_space * np.sin(tag_angles)
            )
            / wave_length
            % (2 * np.pi)
            + np.random.normal(loc=0, scale=1 / 6, size=tag_population)
            for antenna in range(1, antenna_poplulation + 1)
        ]
    ).T
    for i in range(sample_count)
]

columns = ["samplingNumber", "tagNumber", "frequency"]
phase_columns = [f"antenna{i+1}.phase" for i in range(antenna_poplulation)]
columns.extend(phase_columns)

df = pd.DataFrame(columns=columns)

for i in range(sample_count):
    new_data = pd.DataFrame(data=phase_samples[i])
    new_data["samplingNumber"] = i
    new_data["tagNumber"] = [i + 1 for i in range(tag_population)]
    new_data["frequency"] = frequency
    for antenna in range(antenna_poplulation):
        new_data = new_data.rename(columns={antenna: f"antenna{antenna+1}.phase"})
    df = pd.concat([df, new_data])

df = df.query(f"tagNumber in {tag_numbers}")
data_list = np.empty((0, tag_population, antenna_poplulation))

data_list = np.array(
    [
        df.query(f"samplingNumber == {sampling_number}")
        .sort_values("tagNumber")[phase_columns]
        .to_numpy()
        for sampling_number in range(sample_count)
    ]
)

transmitt_wave = np.full(
    (sample_count, antenna_poplulation, tag_population), transmission_power
)

music = MUSIC(
    antenna_poplulation=antenna_poplulation,
    tag_population=tag_population,
    sampling_count=sample_count,
    antenna_space=antenna_space,
    frequency=frequency,
    tx_power=transmission_power,
    theta_range=theta_range,
)

music_spectrum = music.get_music_spectrum(
    phase_data_list=data_list, transmission_wave_data_list=transmitt_wave
)
estimated_angles = music.peak_search(music_spectrum=music_spectrum)
print(estimated_angles)
