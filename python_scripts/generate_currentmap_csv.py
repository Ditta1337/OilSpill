import os
import sys
import pandas as pd
import matplotlib.pyplot as plt
from typing import List, Tuple

csv_file_path = "data/gcoos_2010_04_sea_water_current_PROCESSED.csv"
save_file_path = "data/currentmap.csv"
NUM_STATIONS_PER_CELL = 3

def create_current(df: pd.DataFrame, width: int, height: int):
    """
    Create a current from the given dataframe, devide the points evenly into the current
    and return the current
    """
    current = [[[] for _ in range(width)] for _ in range(height)]
    stations = []

    # Get the min and max latitude and longitude
    min_lat = df["latitude"].min()
    max_lat = df["latitude"].max()
    min_lon = df["longitude"].min()
    max_lon = df["longitude"].max()

    # Get the range of latitude and longitude
    lat_range = max_lat - min_lat
    lon_range = max_lon - min_lon

    # Get the step size of latitude and longitude
    lat_step = lat_range / (height - 1) if height > 1 else 1
    lon_step = lon_range / (width - 1) if width > 1 else 1

    for _, row in df.iterrows():
        lat = row["latitude"]
        lon = row["longitude"]
        sea_water_speed = row["sea_water_speed"]
        direction_of_sea_water_velocity_x = row["direction_of_sea_water_velocity_x"]
        direction_of_sea_water_velocity_y = row["direction_of_sea_water_velocity_y"]

        # Get the index of the current
        lat_index = int((lat - min_lat) / lat_step)
        lon_index = int((lon - min_lon) / lon_step)

        # Ensure the index is within the valid range
        lat_index = min(lat_index, height - 1)
        lon_index = min(lon_index, width - 1)   

        current[lat_index][lon_index] = (sea_water_speed, direction_of_sea_water_velocity_x, direction_of_sea_water_velocity_y)
        stations.append((lat_index, lon_index))

    fill_current_map(current, stations)

    return current

def get_nearest_stations(i: int, j: int, stations: List[Tuple[int, int]]): 

    """
    Find and return NUM_STATIONS_PER_CELL nearest stations to the given cell
    """
    distances = []
    for station in stations:
        dist = abs(i - station[0]) + abs(j - station[1])
        distances.append((dist, station))

    distances.sort(key=lambda x: x[0])
    return [station for _, station in distances[:NUM_STATIONS_PER_CELL]]

def fill_current_map(current: List[List[Tuple[float, float, float]]], stations: List[Tuple[int, int]]):
    """
    Fill the current with the nearest stations
    """
    for i in range(len(current)):
        for j in range(len(current[0])):
            if not current[i][j]:
                nearest_stations = get_nearest_stations(i, j, stations)
                sea_water_speed = 0
                direction_of_sea_water_velocity_x = 0
                direction_of_sea_water_velocity_y = 0
                for station in nearest_stations:
                    sea_water_speed += current[station[0]][station[1]][0]
                    direction_of_sea_water_velocity_x += current[station[0]][station[1]][1]
                    direction_of_sea_water_velocity_y += current[station[0]][station[1]][2]
                current[i][j] = (sea_water_speed / NUM_STATIONS_PER_CELL, direction_of_sea_water_velocity_x / NUM_STATIONS_PER_CELL, direction_of_sea_water_velocity_y / NUM_STATIONS_PER_CELL)


def plot_current(current: List[List[Tuple[float, float, float]]], width: int, height: int):
    """
    Plot the current
    """
    fig, ax = plt.subplots(figsize=(20, 10))
    for i in range(height):
        for j in range(width):
            if current[i][j]:
                _, direction_of_sea_water_velocity_x, direction_of_sea_water_velocity_y = current[i][j]
                ax.quiver(j, i, direction_of_sea_water_velocity_x, direction_of_sea_water_velocity_y, angles='xy', scale_units='xy', linewidth=0.01, scale=0.8, color='blue')

    plt.gca().invert_yaxis()
    plt.title(f"current {width}x{height}")
    plt.axis('off')
    plt.show()

def save_current(current: List[List[Tuple[float, float, float]]], width: int, height: int):
    """
    Save the current to a CSV file
    """
    if os.path.exists(save_file_path):
        os.remove(save_file_path)

    with open(save_file_path, "w") as f:
        f.write("index_i,index_j,sea_water_speed,direction_of_sea_water_velocity_x,direction_of_sea_water_velocity_y\n")
        for i in range(height):
            for j in range(width):
                sea_water_speed, direction_of_sea_water_velocity_x, direction_of_sea_water_velocity_y = current[i][j]
                f.write(f"{i},{j},{sea_water_speed},{direction_of_sea_water_velocity_x},{direction_of_sea_water_velocity_y}\n")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python generate_current_csv.py <width> <height>")
        sys.exit(1)

    width = int(sys.argv[1])
    height = int(sys.argv[2])

    assert width > 0 and height > 0, "Width and height must be positive integers"

    if not os.path.exists(csv_file_path):
        print("Run water_current_data_preprocessing.py first...")
        os.system("python python_scripts/water_current_data_preprocessing.py")

    df = pd.read_csv(csv_file_path)

    current = create_current(df, width, height)

    # plot_current(current, width, height)

    save_current(current, width, height)

    print(f"Current saved to {save_file_path}")
