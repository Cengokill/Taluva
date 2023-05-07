package Structures.Position;

import java.io.Serializable;

public class TripletDePosition implements Serializable {
    private Position volcan;
    private Position tile1;
    private Position tile2;

    public TripletDePosition(Position volcan, Position tile1, Position tile2) {
        this.volcan = volcan;
        this.tile1 = tile1;
        this.tile2 = tile2;
    }

    public Position getVolcan() {
        return volcan;
    }

    public Position getTile1() {
        return tile1;
    }

    public Position getTile2() {
        return tile2;
    }

    public void setVolcan(Position volcan) {
        this.volcan = volcan;
    }

    public void setTile1(Position tile1) {
        this.tile1 = tile1;
    }

    public void setTile2(Position tile2) {
        this.tile2 = tile2;
    }


    public TripletDePosition copy() {
        return new TripletDePosition(this.volcan, this.tile1, this.tile2);
    }
}
