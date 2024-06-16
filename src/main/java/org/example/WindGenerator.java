package org.example;

import java.io.*;

public class WindGenerator {

    private static final String WINDPATH = "data/combinedmap.csv";
    private static final String SCRIPTPATH = "python_scripts/generate_combinedmap_csv.py";

    private WindGenerator() {
        throw new UnsupportedOperationException("UtilityClass should not be instantiated");
    }

    public static WindPoint[][] generateSimpleWindMap(int rows, int cols, double speed, Vector2D direction) {

        WindPoint[][] windMap = new WindPoint[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                windMap[i][j] = new WindPoint(direction, speed);
            }
        }

        // set DT
        Board.setDT(Board.getSIZE() / (speed * 2));

        return windMap;
    }

    public static WindPoint[][] generateWindMapFromCSV(int rows, int cols) {
        double[] coordinates = Loader.getAreaCoordinates();
        System.out.println("Generating combined map");
        // generate rows x cols wind map csv file
        String command = "python3 " + SCRIPTPATH + " " + rows + " " + cols + " " + coordinates[0] + " " + coordinates[1] + " " + coordinates[2] + " " + coordinates[3];
        try {
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // Capture the output of the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double maxSpeed = 0;

        // read csv file
        WindPoint[][] windMap = new WindPoint[rows][cols];
        try (BufferedReader reader = new BufferedReader(new FileReader(WINDPATH))) {
            reader.readLine();
            for (int i = 0; i < rows; i++) {
                String[] wind = reader.readLine().split(",");
                for (int j = 0; j < cols; j++) {
                    double speed = Double.parseDouble(wind[2]);
                    maxSpeed = Math.max(maxSpeed, speed);
                    windMap[i][j] = new WindPoint(new Vector2D(Double.parseDouble(wind[3]), Double.parseDouble(wind[4])), speed);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set DT
        Board.setDT(Board.getSIZE() / (maxSpeed * 20));
        Board.setDTirl(Board.getDT() * 4000 * 20 / 3);

        return windMap;
    }

}


