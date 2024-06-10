import csv

csv_file_path = "data/gcoos_2010_04_sea_water_current_RAW.csv"

columns_to_select = ["latitude", "longitude", "date", "vertical_datum", "direction_of_sea_water_velocity", "sea_water_speed"]

with open(csv_file_path, 'r') as csv_file:
    reader = csv.DictReader(csv_file)
    rows = [row for row in reader]

with open(csv_file_path, 'w', newline='') as csv_file:
    writer = csv.DictWriter(csv_file, fieldnames=columns_to_select)
    writer.writeheader()
    for row in rows:
        writer.writerow({col: row[col] for col in columns_to_select})
