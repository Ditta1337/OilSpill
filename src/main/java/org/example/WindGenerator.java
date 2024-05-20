package org.example;

public class WindGenerator {

    private WindGenerator() {
        throw new UnsupportedOperationException("UtilityClass should not be instantiated");
    }

    public static WindPoint[][] generateSimpleWindMap(int rows, int cols, double strength, Direction direction) {
        WindPoint[][] windMap = new WindPoint[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                windMap[i][j] = new WindPoint(direction, strength);
            }
        }

        return windMap;
    }

}
