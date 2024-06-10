package org.example;


public class IntersectionOverUnion {
    public static float compare(Board first, Board second, float epsilon){
        int intersection=0;
        int union=0;
        int length=first.getPoints().length;
        int height=first.getPoints()[0].length;
        Point[][] firstPoints=first.getPoints();
        Point[][] secondPoints=second.getPoints();

        for (int x = 0; x < length; ++x) {
            for (int y = 0; y < height; ++y) {
                if(firstPoints[x][y].getOil()>epsilon || secondPoints[x][y].getOil()>epsilon){
                    union+=1;
                }
                if(firstPoints[x][y].getOil()>epsilon && secondPoints[x][y].getOil()>epsilon){
                    intersection+=1;
                }
            }
        }

        return intersection/union;
    }
}