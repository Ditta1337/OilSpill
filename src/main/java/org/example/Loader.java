package org.example;

import java.io.*;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Loader {
    private static final String MAPPATH = "src/main/resources/saves/";
    private static final String MAPSETTINGSPATH = "src/main/resources/settings/setting.yaml";

    public static Point[][] loadMap(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MAPPATH + fileName))) {
            String[] size = reader.readLine().split(" ");
            int rows = Integer.parseInt(size[0]);
            int cols = Integer.parseInt(size[1]);
            Point[][] mapState = new Point[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    String[] point = reader.readLine().split(" ");
                    if (point[2].equals("OIL") || point[2].equals("WATER")) {
                        mapState[i][j] = new Point(PointType.WATER);
                        mapState[i][j].setOil(Double.parseDouble(point[3]));
                    } else {
                        mapState[i][j] = new Point(PointType.LAND);
                    }
                }
            }
            return mapState;
        } catch (Exception e) {
            System.out.println("Error while loading file");
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getFiles() {
        return new File(MAPPATH).list();
    }

    public static void delete(String fileName) {
        try (BufferedReader ignored = new BufferedReader(new FileReader(MAPPATH + fileName))) {
            File file = new File(MAPPATH + fileName);
            file.delete();
        } catch (IOException e) {
            System.out.println("Error while deleting file");
            e.printStackTrace();
        }
    }

    public static void setMapSettings() {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(MAPSETTINGSPATH)) {
            Map<String, Object> mapSettings = yaml.load(in);
            int cellSize = (int) mapSettings.get("cell_width") * 3 / 4;
            int oilSpawn = (int) (mapSettings.get("oil_per_cell")) / 100;
            Board.setSIZE(cellSize);
            Board.setSPAWNOIL(oilSpawn);

        } catch (Exception e) {
            System.out.println("Error while loading map settings");
            e.printStackTrace();
        }
    }

    public static double[] getAreaCoordinates() {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(MAPSETTINGSPATH)) {
            Map<String, Object> mapSettings = yaml.load(in);
            double lower_right_lat = (double) mapSettings.get("lower_right_lat");
            double lower_right_lon = (double) mapSettings.get("lower_right_lon");
            double upper_left_lat = (double) mapSettings.get("upper_left_lat");
            double upper_left_lon = (double) mapSettings.get("upper_left_lon");

            return new double[]{lower_right_lat, lower_right_lon, upper_left_lat, upper_left_lon};

        } catch (Exception e) {
            System.out.println("Error while loading map settings");
            e.printStackTrace();
        }

        return new double[]{0, 0, 0, 0};
    }
}

