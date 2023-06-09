package Modele.Jeu.Plateau;

import java.awt.*;

public class EtatPlateau {
    public static final byte[][] tuileAPoser = new byte[3][2]; // [n° tile] [0: tile_type] [1: tile_textureid]
    public static int scrollValue = 1;
    public static byte provenanceScroll = 0;//0: neutre, 1: provient de la droite vers la gauche, 2: provient de la gauche vers la droite
    public static int hoveredTile_x;
    public static int hoveredTile_y;
    public static int typeAConstruire = 0, posBat_x, posBat_y;
    public static boolean poseTile, mode_plateau = false, mode_numero = false;
    public static boolean clicDroiteEnfonce = false;
    public static boolean enSelection = false;
    public static Color[] couleurs_joueurs;
    public static String[] nomJoueurs;
}
