package org.example;


public class IntersectionOverUnion {
    public static double compare(Point[][] first, Point[][] second, int epsilon){
        int intersection=0;
        int union=0;
        int length= Math.min(second.length, first.length);
        int height= Math.min(second[0].length, first[0].length);

        for (int x = 0; x < length; ++x) {
            for (int y = 0; y < height; ++y) {
                if(first[x][y].getOil()>epsilon || second[x][y].getOil()>epsilon){
                    union+=1;
                }
                if(first[x][y].getOil()>epsilon && second[x][y].getOil()>epsilon){
                    intersection+=1;
                }
            }
        }

        return (double) intersection /union;
    }
}