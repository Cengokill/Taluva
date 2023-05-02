package Modele;

public class Point2 {
    int x ;
    int y ;

    public Point2(int x, int y){
        this.x= x;
        this.y= y;
    }
    public int getPointX(){
        return x;
    }
    public int getPointY(){
        return y;
    }
    public boolean PionsEquals ( Point2 b){
        return this.x==b.x && this.y == b.y;
    }

}
