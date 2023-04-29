package Structures;

import Modele.Position;

public class TripletDePosition {
    private Position x;
    private Position y;
    private Position z;

    public TripletDePosition(Position x, Position y, Position z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position getX() {
        return x;
    }

    public Position getY() {
        return y;
    }

    public Position getZ() {
        return z;
    }

    public void setX(Position x) {
        this.x = x;
    }

    public void setY(Position y) {
        this.y = y;
    }

    public void setZ(Position z) {
        this.z = z;
    }


    public TripletDePosition copy() {
        TripletDePosition tripletDePosition = new TripletDePosition(this.x, this.y, this.z);
        return tripletDePosition;
    }
}
