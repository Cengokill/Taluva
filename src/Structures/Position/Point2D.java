package Structures.Position;

public class Point2D {
    public final int x ;
    public final int y ;

    public Point2D(int x, int y){
        this.x= x;
        this.y= y;
    }
    public int getPointX(){
        return x;
    }
    public int getPointY(){
        return y;
    }
    public boolean PionsEquals ( Point2D point){
        return this.x==point.x && this.y == point.y;
    }

}
