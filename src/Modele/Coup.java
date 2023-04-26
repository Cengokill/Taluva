package Modele;

public class Coup {

    public static final int TUILE = 0;
    public static final int BATIMENT = 1;

    byte type;//0 : placer une tuile, 1 : placer un bâtiment

    int volcan_x;
    int volcan_y;
    int tile1_x;
    int tile1_y;
    int tile2_x;
    int tile2_y;
    int batiment_x;
    int batiment_y;

    public Coup(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int tile2_x, int tile2_y) {
        this.type = Coup.TUILE;
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
        this.tile1_x = tile1_x;
        this.tile1_y = tile1_y;
        this.tile2_x = tile2_x;
        this.tile2_y = tile2_y;
    }
    public Coup(int batiment_x, int batiment_y) {
        this.type = Coup.BATIMENT;

        this.batiment_x = batiment_x;
        this.batiment_y = batiment_y;
    }
}
