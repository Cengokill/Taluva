package Modele;

public class Coup {
    byte type;//0 : placer une tuile, 1 : placer un bâtiment

    int volcan_x;
    int volcan_y;
    int tuile2_x;
    int tuile2_y;
    int tuile3_x;
    int tuile3_y;
    public Coup(byte type) {
        this.type = type;
    }
}
