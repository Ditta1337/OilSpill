import pandas as pd
import numpy as np

EPSILON = 1e-6
csv_file_path = "data/gcoos_2010_04_sea_water_current_RAW.csv"
save_file_path = "data/gcoos_2010_04_sea_water_current_PROCESSED.csv"

# Load the CSV data
df = pd.read_csv(csv_file_path)

# parse the date from this format: 2010-04-01T00:00:00Z
# and select only datapoints from 2010-04-20
df["date"] = pd.to_datetime(df["date"], format="%Y-%m-%dT%H:%M:%SZ")
df = df[df["date"].dt.date == pd.Timestamp("2010-04-28").date()]

# Select only columns latitude, longitude, wind_to_direction, vertical_datum, wind_speed
df = df[["latitude", "longitude", "vertical_datum", "sea_water_speed", "direction_of_sea_water_velocity"]]


# Ensure latitude and longitude columns are numeric
df["latitude"] = pd.to_numeric(df["latitude"], errors="coerce")
df["longitude"] = pd.to_numeric(df["longitude"], errors="coerce")

# Remove wind direction values that are not between 0 and 360
df = df[(df["direction_of_sea_water_velocity"] >= 0) & (df["direction_of_sea_water_velocity"] <= 360)]

# Drop rows with missing latitude or longitude
df = df.dropna(subset=["latitude", "longitude"])

# Select row with lowest vertical_datum value for each pair of latitude and longitude
df = df.loc[df.groupby(["latitude", "longitude"])["vertical_datum"].idxmin()].reset_index()
df.drop(columns=["index"], inplace=True)

# Change the radian angle of wind_to_direction to unit vector and split into two columns
# and if the values are close to 0, set them to 0
df["direction_of_sea_water_velocity_x"] = np.cos(np.radians(df["direction_of_sea_water_velocity"]))
df["direction_of_sea_water_velocity_y"] = np.sin(np.radians(df["direction_of_sea_water_velocity"]))
df.loc[abs(df["direction_of_sea_water_velocity_x"]) < EPSILON, "direction_of_sea_water_velocity_x"] = 0
df.loc[abs(df["direction_of_sea_water_velocity_y"]) < EPSILON, "direction_of_sea_water_velocity_y"] = 0
df.drop(columns=["direction_of_sea_water_velocity"], inplace=True)   


# Save the processed data to a new CSV file
df.to_csv(save_file_path, index=False)