package org.example;

import java.io.*;

public class Loader {
    private static final String MAPPATH = "src/main/resources/";

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
                    }
                    else {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(MAPPATH + fileName))) {
            File file = new File(MAPPATH + fileName);
            file.delete();
        } catch (IOException e) {
            System.out.println("Error while deleting file");
            e.printStackTrace();
        }
    }
}
