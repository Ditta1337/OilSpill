package org.example;
import java.io.PrintWriter;
import java.io.IOException;

public class Saver {
    private static final String PATH = "src/main/resources/";

    public static void save(Point[][] mapState, String fileName) {
        try (PrintWriter writer = new PrintWriter(PATH + fileName)) {
            writer.println(mapState.length + " " + mapState[0].length); // save map size
            for (int i = 0; i < mapState.length; i++) {
                for (int j = 0; j < mapState[i].length; j++) {
                    writer.println(i + " " + j + " " + mapState[i][j].getType());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while saving file");
            e.printStackTrace();
        }
    }
}
