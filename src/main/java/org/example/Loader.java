package org.example;

import java.io.*;

public class Loader {
    private static final String PATH = "src/main/resources/";

    public static Point[][] load(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH + fileName))) {
            String[] size = reader.readLine().split(" ");
            int rows = Integer.parseInt(size[0]);
            int cols = Integer.parseInt(size[1]);
            Point[][] mapState = new Point[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    String[] point = reader.readLine().split(" ");
                    mapState[i][j] = new Point(PointType.valueOf(point[2]));
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
        return new File(PATH).list();
    }

    public static void delete(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH + fileName))) {
            File file = new File(PATH + fileName);
            file.delete();
        } catch (IOException e) {
            System.out.println("Error while deleting file");
            e.printStackTrace();
        }
    }
}
