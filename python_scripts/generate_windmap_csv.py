import os
import sys
import pandas as pd
import matplotlib.pyplot as plt
from typing import List, Tuple

csv_file_path = "data/gcoos_2010_01_wind_PROCESSED.csv"
save_file_path = "data/windmap.csv"
NUM_STATIONS_PER_CELL = 3

def create_windmap(df: pd.DataFrame, width: int, height: int):
    """
    Create a windmap from the given dataframe, devide the points evenly into the windmap
    and return the windmap
    """
    windmap = [[[] for _ in range(width)] for _ in range(height)]
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
        wind_speed = row["wind_speed"]
        wind_to_direction_x = row["wind_to_direction_x"]
        wind_to_direction_y = row["wind_to_direction_y"]

        # Get the index of the windmap
        lat_index = int((lat - min_lat) / lat_step)
        lon_index = int((lon - min_lon) / lon_step)

        # Ensure the index is within the valid range
        lat_index = min(lat_index, height - 1)
        lon_index = min(lon_index, width - 1)   

        windmap[lat_index][lon_index] = (wind_speed, wind_to_direction_x, wind_to_direction_y)
        stations.append((lat_index, lon_index))

    fill_wind_map(windmap, stations)

    return windmap

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

def fill_wind_map(windmap: List[List[Tuple[float, float, float]]], stations: List[Tuple[int, int]]):
    """
    Fill the windmap with the nearest stations
    """
    for i in range(len(windmap)):
        for j in range(len(windmap[0])):
            if not windmap[i][j]:
                nearest_stations = get_nearest_stations(i, j, stations)
                wind_speed = 0
                wind_to_direction_x = 0
                wind_to_direction_y = 0
                for station in nearest_stations:
                    wind_speed += windmap[station[0]][station[1]][0]
                    wind_to_direction_x += windmap[station[0]][station[1]][1]
                    wind_to_direction_y += windmap[station[0]][station[1]][2]
                windmap[i][j] = (wind_speed / NUM_STATIONS_PER_CELL, wind_to_direction_x / NUM_STATIONS_PER_CELL, wind_to_direction_y / NUM_STATIONS_PER_CELL)


def plot_windmap(windmap: List[List[Tuple[float, float, float]]], width: int, height: int):
    """
    Plot the windmap
    """
    fig, ax = plt.subplots(figsize=(20, 10))
    for i in range(height):
        for j in range(width):
            if windmap[i][j]:
                _, wind_to_direction_x, wind_to_direction_y = windmap[i][j]
                ax.quiver(j, i, wind_to_direction_x, wind_to_direction_y, angles='xy', scale_units='xy', linewidth=0.01, scale=0.8, color='blue')

    plt.gca().invert_yaxis()
    plt.title(f"Windmap {width}x{height}")
    plt.axis('off')
    plt.show()

def save_windmap(windmap: List[List[Tuple[float, float, float]]], width: int, height: int):
    """
    Save the windmap to a CSV file
    """
    if os.path.exists(save_file_path):
        os.remove(save_file_path)

    with open(save_file_path, "w") as f:
        f.write("index_i,index_j,wind_speed,wind_to_direction_x,wind_to_direction_y\n")
        for i in range(height):
            for j in range(width):
                wind_speed, wind_to_direction_x, wind_to_direction_y = windmap[i][j]
                f.write(f"{i},{j},{wind_speed},{wind_to_direction_x},{wind_to_direction_y}\n")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python generate_windmap_csv.py <width> <height>")
        sys.exit(1)

    width = int(sys.argv[1])
    height = int(sys.argv[2])

    assert width > 0 and height > 0, "Width and height must be positive integers"

    if not os.path.exists(csv_file_path):
        print("Run wind_data_preprocessing.py first...")
        os.system("python python_scripts/wind_data_preprocessing.py")

    df = pd.read_csv(csv_file_path)

    windmap = create_windmap(df, width, height)

    #plot_windmap(windmap, width, height)

    save_windmap(windmap, width, height)

    print(f"Windmap saved to {save_file_path}")

    

   