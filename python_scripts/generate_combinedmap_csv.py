import sys
import os
import csv
import matplotlib.pyplot as plt
from typing import List, Tuple

windmap_path = "data/windmap.csv"
currentmap_path = "data/currentmap.csv"
combinedmap_path = "data/combinedmap.csv"

def combine_maps(windmap_path: str, currentmap_path: str, combinedmap_path: str):
    """
    Combine the windmap and currentmap into a single combinedmap
    """
    windmap = []
    currentmap = []

    with open(windmap_path, 'r') as windmap_file:
        reader = csv.reader(windmap_file)
        for row in reader:
            windmap.append(row)

    with open(currentmap_path, 'r') as currentmap_file:
        reader = csv.reader(currentmap_file)
        for row in reader:
            currentmap.append(row)

    combinedmap = []
    combinedmap.append(windmap[0])
    for i in range(1, len(windmap)):
        wind_row = windmap[i]
        current_row = currentmap[i]

        wind_sea_water_speed = float(wind_row[2])
        wind_velocity_x = float(wind_row[3])
        wind_velocity_y = float(wind_row[4])

        current_sea_water_speed = float(current_row[2])
        current_velocity_x = float(current_row[3])
        current_velocity_y = float(current_row[4])

        sea_water_speed = (wind_sea_water_speed + current_sea_water_speed) / 2
        velocity_x = (wind_velocity_x + current_velocity_x) / 2
        velocity_y = (wind_velocity_y + current_velocity_y) / 2

        combinedmap.append([wind_row[0], wind_row[1], sea_water_speed, velocity_x, velocity_y])

    with open(combinedmap_path, 'w', newline='') as combinedmap_file:
        writer = csv.writer(combinedmap_file)
        writer.writerows(combinedmap)

    print(f"Combined map saved to {combinedmap_path}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python generate_combinedmap_csv.py <width> <height>")
        sys.exit(1)

    width = int(sys.argv[1])
    height = int(sys.argv[2])


    os.system(f"python python_scripts/generate_windmap_csv.py {width} {height}")
    os.system(f"python python_scripts/generate_currentmap_csv.py {width} {height}")

    combine_maps(windmap_path, currentmap_path, combinedmap_path)




