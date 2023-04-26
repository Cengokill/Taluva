package Modele;

public class Coup {

    public static final byte TUILE = 0;
    public static final byte BATIMENT = 1;

    byte type;//0 : placer une tuile, 1 : placer un bâtiment

    int volcan_x;
    int volcan_y;
    int tile1_x;
    int tile1_y;
    int terrain1;
    int tile2_x;
    int tile2_y;
    int terrain2;
    int batiment_x;
    int batiment_y;

    public Coup(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int terrain1, int tile2_x, int tile2_y, int terrain2) {// placement TUILE
        this.type = TUILE;
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
        this.tile1_x = tile1_x;
        this.tile1_y = tile1_y;
        this.terrain1 = terrain1;
        this.tile2_x = tile2_x;
        this.tile2_y = tile2_y;
        this.terrain2 = terrain2;
    }
    public Coup(int batiment_x, int batiment_y) {// placement BATIMENT
        this.type = BATIMENT;

        this.batiment_x = batiment_x;
        this.batiment_y = batiment_y;
    }
}
