package Modele;

import java.awt.*;

public class GameState {
    public static byte[][] triplet = new byte[3][2]; // [n° tile] [0: tile_type] [1: tile_textureid]
    public static int scrollValue = 1;
    public static int hoveredTile_x;
    public static int hoveredTile_y;
    public static int typeAConstruire = 0, posBat_x, posBat_y;
    public static boolean poseTile, mode_plateau = true, mode_numero = false;
    public static boolean clicDroiteEnfonce = false;
    public static boolean enSelection = false,unefoisIA=false;
    public static Color[] couleurs_joueurs;
    public static String[] nomJoueurs;
}