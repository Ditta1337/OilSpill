package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class WindGenerator {

    private static final String WINDPATH = "data/windmap.csv";
    private static final String SCRIPTPATH = "python_scripts/generate_windmap_csv.py";

    private WindGenerator() {
        throw new UnsupportedOperationException("UtilityClass should not be instantiated");
    }

    public static WindPoint[][] generateSimpleWindMap(int rows, int cols, double strength, Vector2D direction) {

        WindPoint[][] windMap = new WindPoint[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                windMap[i][j] = new WindPoint(direction, strength);
            }
        }

        return windMap;
    }

    public static WindPoint[][] generateWindMapFromCSV(int rows, int cols) {
        System.out.println("Generating wind map");
        // generate rows x cols wind map csv file
        String command = "python3 " + SCRIPTPATH + " " + rows + " " + cols;
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

            System.out.println("Wind map generated");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read csv file
        WindPoint[][] windMap = new WindPoint[rows][cols];
        try (BufferedReader reader = new BufferedReader(new FileReader(WINDPATH))) {
            reader.readLine();
            for (int i = 0; i < rows; i++) {
                String[] wind = reader.readLine().split(",");
                for (int j = 0; j < cols; j++) {
                    windMap[i][j] = new WindPoint(new Vector2D(Double.parseDouble(wind[3]), Double.parseDouble(wind[4])), Double.parseDouble(wind[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                System.out.println(windMap[i][j]);
//            }
//        }

        return windMap;
    }

}
