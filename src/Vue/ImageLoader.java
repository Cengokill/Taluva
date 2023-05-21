package Vue;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Modele.Jeu.Plateau.EtatPlateau.couleurs_joueurs;
import static Modele.Jeu.Plateau.Hexagone.*;

public class ImageLoader {
    public static boolean loaded = false;
    public static int nb_aiguilles = 60, taille_tuile_piochee = 15;
    public static BufferedImage constructionMode;
    public static final BufferedImage[] choisirBat = new BufferedImage[12], temples_rouges = new BufferedImage[5], temples_bleus = new BufferedImage[5], temples_verts = new BufferedImage[5], temples_violets = new BufferedImage[5];
    public static final BufferedImage[] huttes_rouges = new BufferedImage[7], huttes_bleues = new BufferedImage[7], huttes_vertes = new BufferedImage[7], huttes_violettes = new BufferedImage[7];
    public static final BufferedImage[] tours_rouges = new BufferedImage[5], tours_bleues = new BufferedImage[5], tours_vertes = new BufferedImage[5], tours_violettes = new BufferedImage[5];
    public static final BufferedImage[] chrono = new BufferedImage[nb_aiguilles]; public static final BufferedImage[] pioche = new BufferedImage[9];
    public static final BufferedImage[] tuile_piochee = new BufferedImage[taille_tuile_piochee];
    public static BufferedImage waterTile, fenetre_score_2, fenetre_score_3, fenetre_score_4, background, joueur_courant, chronoBleu, chronoRouge;
    public static BufferedImage hoverTile, wrongTile1, wrongTile2, wrongTile3, beacons, beacon_1, beacon_2, beacon_3, beacon_4, beacon_5, beacon_6;
    public static BufferedImage voidTile, voidTile_transparent, voidTileOld, whiteTile;
    public static BufferedImage grassTile_0, grassTile_1, grassTile_2,grassTile_0_SP, grassTile_1_SP, grassTile_2_SP;
    public static BufferedImage volcanTile_0, volcanTile_1, volcanTile_2,volcanTile_0_SP, volcanTile_1_SP, volcanTile_2_SP;
    public static BufferedImage foretTile_0, foretTile_1, foretTile_2,foretTile_0_SP, foretTile_1_SP, foretTile_2_SP;
    public static BufferedImage desertTile_0, desertTile_1, desertTile_2,desertTile_0_SP, desertTile_1_SP, desertTile_2_SP;
    public static BufferedImage lacTile_0, lacTile_1, lacTile_2,lacTile_0_SP, lacTile_1_SP, lacTile_2_SP;
    public static BufferedImage montagneTile_0, montagneTile_1, montagneTile_2,montagneTile_0_SP, montagneTile_1_SP, montagneTile_2_SP;
    public static BufferedImage ombre_0, ombre_1;
    public static BufferedImage timer, joueurCourant, finPartie, cadreBleu, cadreRouge, cadreVert, cadreViolet, selecteur_vert;
    public static BufferedImage plateau_hautGauche, plateau_hautDroite, plateau_Droite, plateau_Gauche, plateau_basDroite, plateau_basGauche;
    public static BufferedImage tuile_hautGauche, tuile_hautDroite, tuile_Droite, tuile_Gauche, tuile_basDroite, tuile_basGauche;
    public static BufferedImage bouton_save, bouton_save_select, bouton_load, bouton_load_select, bouton_annuler, bouton_annuler_select, bouton_suggestion,
            bouton_refaire, bouton_refaire_select, bouton_quitter, bouton_quitter_select, bouton_tuto_off, bouton_tuto_on, bouton_options, bouton_options_select,
            menu_options, menu_dark_filter;
    public static int posX_fenetre_score, posY_fenetre_score, posX_boutons, posX_save, posX_options, posY_options_echap,
            posY_load, posY_save, posY_annuler,posX_annuler, posY_refaire, posY_quitter, posX_tuto, posY_tuto,
            posX_Echap, posY_Echap, posX_timer, posY_timer, largeur_timer, hauteur_timer,
            posX_joueur_courant, posY_joueur_courant, posX_prenom, posY_prenom_j0, posY_prenom_j1, posY_prenom_j2, posY_prenom_j3, posX_huttes, posX_tours, posX_temples,
            posY_scores_j0, posY_scores_j1, posY_scores_j2, posY_scores_j3, posX_nb_tuiles_pioche, posY_nb_tuiles_pioche, posX_messageErreur,posY_messageErreur, posY_options, posX_menu_options, posY_menu_options,
            largeur_tuile, hauteur_tuile, largeur_fin_partie, hauteur_fin_partie;
    public static int largeur_fenetre_score, hauteur_fenetre_score, largeur, hauteur, largeur_bouton, hauteur_bouton, largeur_bouton_dans_options,
            hauteur_bouton_dans_options, largeur_joueur_courant, hauteur_joueur_courant,hauteurMessageErreur,largeurMessageErreur, largeur_menu_options,
            hauteur_menu_options, largeur_chrono, posX_chrono, posY_chrono, largeur_aiguille, posX_aiguille, posY_aiguille, posX_tuilePiochee, posY_tuilePiochee, largeur_tuilePiochee,
            posX_tuilePiochee_init, posY_tuilePiochee_init, largeur_tuilePiochee_init, posX_temps_partie, posY_temps_partie;
    public static int largeur_background, hauteur_background, posX_background, posY_background, posX_finPartie, posY_finPartie,
    largeur_hutte_score, largeur_tour_score, largeur_temple_score, posX_huttes_score, posX_tours_score, posX_temples_score, posY_huttes_score_j0,
            posY_huttes_score_j1, posY_huttes_score_j2, posY_huttes_score_j3, posY_tours_score_j0, posY_tours_score_j1, posY_tours_score_j2, posY_tours_score_j3,
            posY_temples_score_j0, posY_temples_score_j1, posY_temples_score_j2, posY_temples_score_j3;
    public static double tempsDebutPartie, tempsFinPartie, tempsPartie;
    public static int posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche;
    public static boolean select_options;
    public static boolean select_menu_options;
    public static boolean select_save;
    public static boolean select_load;
    public static boolean select_annuler;
    public static boolean select_refaire;
    public static boolean tuto_on;
    public static boolean select_quitter;
    public static boolean select_fin_partie;
    public static BufferedImage echap_button;

    public static int posX_score_finPartie, posX_joueur_finPartie, posY_joueur_finPartie, posY_joueur_deux, posY_joueur_trois, posX_tiers_selecteur_vert;
    public static int posX_cadre, posY_cadre, decalageY_cadre, largeur_cadre, hauteur_cadre, decalageY_joueur, posX_huttes_finPartie, posX_temples_finPartie, posX_tours_finPartie;
    public static boolean ecran_fin_partie;
    public static BufferedImage grassTile_0_Red, grassTile_1_Red, grassTile_2_Red;
    public static BufferedImage volcanTile_0_Red, volcanTile_1_Red, volcanTile_2_Red;
    public static BufferedImage foretTile_0_Red, foretTile_1_Red, foretTile_2_Red;
    public static BufferedImage desertTile_0_Red, desertTile_1_Red, desertTile_2_Red;
    public static BufferedImage montagneTile_0_Red, montagneTile_1_Red, montagneTile_2_Red;
    public static BufferedImage lacTile_0_Red, lacTile_1_Red, lacTile_2_Red;
    public static BufferedImage tileErreur, placable, plus, moins;

    public static BufferedImage configPartieBackground, rouge, vert, violet, bleu, fermer, valider;

    public static BufferedImage
            plateau_hautGauche_etage1,
            plateau_hautDroite_etage1,
            plateau_Gauche_etage1,
            plateau_Droite_etage1,
            plateau_basGauche_etage1,
            plateau_basDroite_etage1,
            plateau_hautGauche_etage2,
            plateau_hautDroite_etage2,
            plateau_Gauche_etage2,
            plateau_Droite_etage2,
            plateau_basGauche_etage2,
            plateau_basDroite_etage2,
            plateau_hautGauche_etage3,
            plateau_hautDroite_etage3,
            plateau_Gauche_etage3,
            plateau_Droite_etage3,
            plateau_basGauche_etage3,
            plateau_basDroite_etage3;

    public static void loadImages() {
        joueurCourant = lisImageBuf("Joueur_Courant");
        background = lisImageBuf("/Plateau/background_plateau_x4_carre_2");
        readTilesImages();
        readChronoImages();
        readTuilePiochee();
        readPiocheImages();
        readPlayableTilesImages();
        readPlayableTilesSansProfondeurImages();
        readHeightImages();
        readTileOrientationImages();
        readBatimentsImages();
        readAndFilterContoursImages();
        readSelectionBatimentImage();
        posX_tiers_selecteur_vert = (choisirBat[0].getWidth()*2)/3-12;
        filterTiles();
        loaded = true;
    }

    private static void readPiocheImages() {
        for(int i=0;i<6;i++){
            pioche[i] = lisImageBuf("Pioche/pioche_"+(i+1));
        }
        pioche[6] = lisImageBuf("Pioche/pioche_50");
        pioche[7] = lisImageBuf("Pioche/pioche_75");
        pioche[8] = lisImageBuf("Pioche/pioche_100");

    }

    private static void readChronoImages(){
        String imageFolder = "Chronometre/";
        chronoBleu = lisImageBuf(imageFolder + "chrono_bleu");
        chronoRouge = lisImageBuf(imageFolder + "chrono_rouge");
        BufferedImage image = lisImageBuf(imageFolder + 0);
        int width = image.getWidth();
        int height = image.getHeight();
        double angle = 0.0;
        double angle_incremente = 360.0/(double)nb_aiguilles;
        for(int i=0;i<nb_aiguilles;i++){
            // Créez une nouvelle image pour stocker l'image tournée
            BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            // Créez une transformation AffineTransform pour effectuer la rotation
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(angle), width / 2, height / 2);
            // Obtenir le contexte graphique 2D de l'image tournée
            Graphics2D g2d = rotatedImage.createGraphics();
            g2d.drawImage(image, transform, null);
            g2d.dispose();
            chrono[i] = rotatedImage;
            angle += angle_incremente;
        }
    }

    private static void readTuilePiochee(){
        BufferedImage image = lisImageBuf("Pioche/tuile_floue");
        int width = image.getWidth();
        int height = image.getHeight();
        double angle = 0.0;
        float opacity = 1.0F;
        double angle_incremente = 80.0/(double)taille_tuile_piochee;
        for(int i=0;i<taille_tuile_piochee;i++){
            double a = 1.0-(double)i/(double)(taille_tuile_piochee-1.0);
            opacity = (float) a;
            //System.out.println("opacity = "+opacity);
            //System.out.println(a);
            // Créez une nouvelle image pour stocker l'image tournée
            BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            // Créez une transformation AffineTransform pour effectuer la rotation
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(angle), width / 2, height / 2);
            // Obtenir le contexte graphique 2D de l'image tournée
            Graphics2D g2d = rotatedImage.createGraphics();
            // Applique l'opacité de l'image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(image, transform, null);
            g2d.dispose();
            tuile_piochee[i] = rotatedImage;
            angle += angle_incremente;
        }
    }

    private static void readSelectionBatimentImage() {
        String imageFolder = "Plateau/Batiments/Selecteur/";
        for(int i=0;i<7;i++){
            choisirBat[i] = lisImageBuf(imageFolder + "choisir_bat_" +i);
        }
        selecteur_vert = lisImageBuf(imageFolder + "selecteur_vert");
        constructionMode = lisImageBuf(imageFolder + "construction");
    }

    private static void filterTiles() {
        voidTile_transparent = getReducedOpacityImage(voidTileOld, 0.2f);
        filterTilesEnRouge();
    }

    private static void filterTilesEnRouge() {
        grassTile_0_Red = applyRedFilter(grassTile_0);
        grassTile_1_Red = applyRedFilter(grassTile_1);
        grassTile_2_Red = applyRedFilter(grassTile_2);
        desertTile_0_Red = applyRedFilter(desertTile_0);
        desertTile_1_Red = applyRedFilter(grassTile_1);
        desertTile_2_Red = applyRedFilter(desertTile_2);
        foretTile_0_Red = applyRedFilter(foretTile_0);
        foretTile_1_Red = applyRedFilter(foretTile_1);
        foretTile_2_Red = applyRedFilter(foretTile_2);
        montagneTile_0_Red = applyRedFilter(montagneTile_0);
        montagneTile_1_Red = applyRedFilter(montagneTile_1);
        montagneTile_2_Red = applyRedFilter(montagneTile_2);
        volcanTile_0_Red = applyRedFilter(volcanTile_0);
        volcanTile_1_Red = applyRedFilter(volcanTile_1);
        volcanTile_2_Red = applyRedFilter(volcanTile_2);
        lacTile_0_Red = applyRedFilter(lacTile_0);
        lacTile_1_Red = applyRedFilter(lacTile_1);
        lacTile_2_Red = applyRedFilter(lacTile_2);

        String imageFolder = "/Plateau/Hexagones/Textures/";
        tileErreur = lisImageBuf(imageFolder + "Wrong_tile");
        placable = lisImageBuf(imageFolder + "Placable");
    }

    private static void readTilesImages() {
        String imageFolder = "/Plateau/Hexagones/Textures/";

        waterTile = lisImageBuf(imageFolder + "Water_Tile");
        voidTile = lisImageBuf(imageFolder + "Void_Tile");
        whiteTile = lisImageBuf(imageFolder + "White_Tile");
        voidTileOld = lisImageBuf(imageFolder + "Void_Tile_old");
        hoverTile = lisImageBuf(imageFolder + "Hover_Tile");
        ombre_0 = lisImageBuf(imageFolder + "ombre_tuile_0");
        ombre_1 = lisImageBuf(imageFolder + "ombre_tuile_1");
    }

    public static BufferedImage getBatimentFromPlayerId(Color color_player, byte batiment_id,int typeTerrain, int hauteurTerrain) {
        if (batiment_id == HUTTE) {
            return getHutte(color_player,hauteurTerrain);//les maisons ne sont plus liées au type de terrain
        }
        if (batiment_id == TEMPLE) {
            return getTemple(color_player,typeTerrain);
        }
        if (batiment_id == TOUR) {
            return getTour(color_player,typeTerrain);
        }
        return null;
    }

    private static BufferedImage getTour(Color color_player,int typeTerrain) {
        if(color_player == Color.RED) {
            if (typeTerrain == DESERT) return tours_rouges[0];
            else if (typeTerrain == FORET) return tours_rouges[1];
            else if (typeTerrain == MONTAGNE) return tours_rouges[2];
            else if (typeTerrain == GRASS) return tours_rouges[3];
            else if (typeTerrain == LAC) return tours_rouges[4];
        }else if(color_player == Color.BLUE) {
            if (typeTerrain == DESERT) return tours_bleues[0];
            else if (typeTerrain == FORET) return tours_bleues[1];
            else if (typeTerrain == MONTAGNE) return tours_bleues[2];
            else if (typeTerrain == GRASS) return tours_bleues[3];
            else if (typeTerrain == LAC) return tours_bleues[4];
        }else if(color_player == Color.GREEN) {
            if (typeTerrain == DESERT) return tours_vertes[0];
            else if (typeTerrain == FORET) return tours_vertes[1];
            else if (typeTerrain == MONTAGNE) return tours_vertes[2];
            else if (typeTerrain == GRASS) return tours_vertes[3];
            else if (typeTerrain == LAC) return tours_vertes[4];
        }else if(color_player == Color.MAGENTA) {
            if (typeTerrain == DESERT) return tours_violettes[0];
            else if (typeTerrain == FORET) return tours_violettes[1];
            else if (typeTerrain == MONTAGNE) return tours_violettes[2];
            else if (typeTerrain == GRASS) return tours_violettes[3];
            else if (typeTerrain == LAC) return tours_violettes[4];
        }
        System.err.println("Erreur : aucune tour ne correspond au type de terrain "+typeTerrain+" et à la couleur "+color_player+".");
        return null;
    }

    private static BufferedImage getTemple(Color color_player,int typeTerrain) {
        if(color_player == Color.RED) {
            if(typeTerrain==DESERT) return temples_rouges[0];
            else if(typeTerrain==FORET) return temples_rouges[1];
            else if(typeTerrain==MONTAGNE) return temples_rouges[2];
            else if(typeTerrain==GRASS) return temples_rouges[3];
            else if (typeTerrain == LAC) return temples_rouges[4];
        }else if(color_player == Color.BLUE) {
            if(typeTerrain==DESERT) return temples_bleus[0];
            else if(typeTerrain==FORET) return temples_bleus[1];
            else if(typeTerrain==MONTAGNE) return temples_bleus[2];
            else if(typeTerrain==GRASS) return temples_bleus[3];
            else if (typeTerrain == LAC) return temples_bleus[4];
        }else if(color_player == Color.GREEN) {
            if(typeTerrain==DESERT) return temples_verts[0];
            else if(typeTerrain==FORET) return temples_verts[1];
            else if(typeTerrain==MONTAGNE) return temples_verts[2];
            else if(typeTerrain==GRASS) return temples_verts[3];
            else if (typeTerrain == LAC) return temples_verts[4];
        }else if(color_player == Color.MAGENTA) {
            if(typeTerrain==DESERT) return temples_violets[0];
            else if(typeTerrain==FORET) return temples_violets[1];
            else if(typeTerrain==MONTAGNE) return temples_violets[2];
            else if(typeTerrain==GRASS) return temples_violets[3];
            else if (typeTerrain == LAC) return temples_violets[4];
        }
        System.err.println("Erreur : aucun temple ne correspond au type de terrain "+typeTerrain+" et à la couleur "+color_player+".");
        return null;
    }

    private static BufferedImage getHutte(Color color_player, int hauteurTerrain) {
        int nbMaison;
        if(hauteurTerrain>=7) nbMaison = 7;
        else nbMaison = hauteurTerrain;
        if(color_player == Color.RED) return huttes_rouges[nbMaison-1];
        else if (color_player == Color.BLUE) return huttes_bleues[nbMaison-1];
        else if (color_player == Color.GREEN) return huttes_vertes[nbMaison-1];
        else if (color_player == Color.MAGENTA) return huttes_violettes[nbMaison-1];
        else{
            System.err.println("Erreur : aucune hutte ne correspond à la couleur "+color_player+".");
            return null;
        }
    }

    private static void readPlayableTilesImages() {
        String imageFolder = "/Plateau/Hexagones/Textures/";
        int simple = 0;
        int complexe = 1;
        int modele_type = simple;
        String modele_simple = "Simples/";
        if(modele_type==simple){
            imageFolder += modele_simple;
        }
        grassTile_0 = lisImageBuf(imageFolder + "Grass_0_Tile");
        grassTile_1 = lisImageBuf(imageFolder + "Grass_1_Tile");
        grassTile_2 = lisImageBuf(imageFolder + "Grass_2_Tile");
        volcanTile_0 = lisImageBuf(imageFolder + "Volcan_0_Tile");
        volcanTile_1 = lisImageBuf(imageFolder + "Volcan_1_Tile");
        volcanTile_2 = lisImageBuf(imageFolder + "Volcan_2_Tile");
        foretTile_0 = lisImageBuf(imageFolder + "Foret_0_Tile");
        foretTile_1 = lisImageBuf(imageFolder + "Foret_1_Tile");
        foretTile_2 = lisImageBuf(imageFolder + "Foret_2_Tile");
        desertTile_0 = lisImageBuf(imageFolder + "Desert_0_Tile");
        desertTile_1 = lisImageBuf(imageFolder + "Desert_1_Tile");
        desertTile_2 = lisImageBuf(imageFolder + "Desert_2_Tile");
        montagneTile_0 = lisImageBuf(imageFolder + "Montagne_0_Tile");
        montagneTile_1 = lisImageBuf(imageFolder + "Montagne_1_Tile");
        montagneTile_2 = lisImageBuf(imageFolder + "Montagne_2_Tile");
        lacTile_0 = lisImageBuf(imageFolder + "Lac_0_Tile");
        lacTile_1 = lisImageBuf(imageFolder + "Lac_1_Tile");
        lacTile_2 = lisImageBuf(imageFolder + "Lac_2_Tile");
    }

    private static void readPlayableTilesSansProfondeurImages() {
        String imageFolder = "/Plateau/Hexagones/Textures/Simples/sans_profondeur/";
        grassTile_0_SP = lisImageBuf(imageFolder + "Grass_0_Tile");
        grassTile_1_SP = lisImageBuf(imageFolder + "Grass_1_Tile");
        grassTile_2_SP = lisImageBuf(imageFolder + "Grass_2_Tile");
        volcanTile_0_SP = lisImageBuf(imageFolder + "Volcan_0_Tile");
        volcanTile_1_SP = lisImageBuf(imageFolder + "Volcan_1_Tile");
        volcanTile_2_SP = lisImageBuf(imageFolder + "Volcan_2_Tile");
        foretTile_0_SP = lisImageBuf(imageFolder + "Foret_0_Tile");
        foretTile_1_SP = lisImageBuf(imageFolder + "Foret_1_Tile");
        foretTile_2_SP = lisImageBuf(imageFolder + "Foret_2_Tile");
        desertTile_0_SP = lisImageBuf(imageFolder + "Desert_0_Tile");
        desertTile_1_SP = lisImageBuf(imageFolder + "Desert_1_Tile");
        desertTile_2_SP = lisImageBuf(imageFolder + "Desert_2_Tile");
        montagneTile_0_SP = lisImageBuf(imageFolder + "Montagne_0_Tile");
        montagneTile_1_SP = lisImageBuf(imageFolder + "Montagne_1_Tile");
        montagneTile_2_SP = lisImageBuf(imageFolder + "Montagne_2_Tile");
        lacTile_0_SP = lisImageBuf(imageFolder + "Lac_0_Tile");
        lacTile_1_SP = lisImageBuf(imageFolder + "Lac_1_Tile");
        lacTile_2_SP = lisImageBuf(imageFolder + "Lac_2_Tile");
    }

    private static void readBatimentsImages() {
        String imageFolder = "/Plateau/Batiments/";
        String dossier_rouge = imageFolder + "Rouge/";
        String dossier_bleu = imageFolder + "Bleu/";
        String dossier_vert = imageFolder + "Vert/";
        String dossier_violet = imageFolder + "Violet/";
        for(int i=0; i<6; i++) {
            huttes_rouges[i] = lisImageBuf(dossier_rouge + "hutte_" + (i+1));
            huttes_bleues[i] = lisImageBuf(dossier_bleu + "hutte_" + (i+1));
            huttes_vertes[i] = lisImageBuf(dossier_vert + "hutte_" + (i+1));
            huttes_violettes[i] = lisImageBuf(dossier_violet + "hutte_" + (i+1));
        }
        for(int i=0; i<5; i++) {
            if(i==0){
                temples_rouges[i] = lisImageBuf(dossier_rouge + "temple_desert");
                temples_bleus[i] = lisImageBuf(dossier_bleu + "temple_desert");
                temples_verts[i] = lisImageBuf(dossier_vert + "temple_desert");
                temples_violets[i] = lisImageBuf(dossier_violet + "temple_desert");
                tours_rouges[i] = lisImageBuf(dossier_rouge + "tour_desert");
                tours_bleues[i] = lisImageBuf(dossier_bleu + "tour_desert");
                tours_vertes[i] = lisImageBuf(dossier_vert + "tour_desert");
                tours_violettes[i] = lisImageBuf(dossier_violet + "tour_desert");
            }else if(i==1){
                temples_rouges[i] = lisImageBuf(dossier_rouge + "temple_jungle");
                temples_bleus[i] = lisImageBuf(dossier_bleu + "temple_jungle");
                temples_verts[i] = lisImageBuf(dossier_vert + "temple_jungle");
                temples_violets[i] = lisImageBuf(dossier_violet + "temple_jungle");
                tours_rouges[i] = lisImageBuf(dossier_rouge + "tour_jungle");
                tours_bleues[i] = lisImageBuf(dossier_bleu + "tour_jungle");
                tours_vertes[i] = lisImageBuf(dossier_vert + "tour_jungle");
                tours_violettes[i] = lisImageBuf(dossier_violet + "tour_jungle");
            }else if(i==2){
                temples_rouges[i] = lisImageBuf(dossier_rouge + "temple_pierre");
                temples_bleus[i] = lisImageBuf(dossier_bleu + "temple_pierre");
                temples_verts[i] = lisImageBuf(dossier_vert + "temple_pierre");
                temples_violets[i] = lisImageBuf(dossier_violet + "temple_pierre");
                tours_rouges[i] = lisImageBuf(dossier_rouge + "tour_pierre");
                tours_bleues[i] = lisImageBuf(dossier_bleu + "tour_pierre");
                tours_vertes[i] = lisImageBuf(dossier_vert + "tour_pierre");
                tours_violettes[i] = lisImageBuf(dossier_violet + "tour_pierre");
            }else if(i==3){
                temples_rouges[i] = lisImageBuf(dossier_rouge + "temple_prairie");
                temples_bleus[i] = lisImageBuf(dossier_bleu + "temple_prairie");
                temples_verts[i] = lisImageBuf(dossier_vert + "temple_prairie");
                temples_violets[i] = lisImageBuf(dossier_violet + "temple_prairie");
                tours_rouges[i] = lisImageBuf(dossier_rouge + "tour_prairie");
                tours_bleues[i] = lisImageBuf(dossier_bleu + "tour_prairie");
                tours_vertes[i] = lisImageBuf(dossier_vert + "tour_prairie");
                tours_violettes[i] = lisImageBuf(dossier_violet + "tour_prairie");
            }else if(i==4){
                temples_rouges[i] = lisImageBuf(dossier_rouge + "temple_pierre");
                temples_bleus[i] = lisImageBuf(dossier_bleu + "temple_pierre");
                temples_verts[i] = lisImageBuf(dossier_vert + "temple_pierre");
                temples_violets[i] = lisImageBuf(dossier_violet + "temple_pierre");
                tours_rouges[i] = lisImageBuf(dossier_rouge + "tour_pierre");
                tours_bleues[i] = lisImageBuf(dossier_bleu + "tour_pierre");
                tours_vertes[i] = lisImageBuf(dossier_vert + "tour_pierre");
                tours_violettes[i] = lisImageBuf(dossier_violet + "tour_pierre");
            }
        }
    }


    private static void readTileOrientationImages() {
        String sourceFolder = "/Plateau/Hexagones/DirectionsPlacables/";
        beacons = lisImageBuf(sourceFolder + "Beacons");
        beacon_1 = lisImageBuf(sourceFolder + "Beacon_1");
        beacon_2 = lisImageBuf(sourceFolder + "Beacon_2");
        beacon_3 = lisImageBuf(sourceFolder + "Beacon_3");
        beacon_4 = lisImageBuf(sourceFolder + "Beacon_4");
        beacon_5 = lisImageBuf(sourceFolder + "Beacon_5");
        beacon_6 = lisImageBuf(sourceFolder + "Beacon_6");
    }

    private static void readHeightImages() {
        wrongTile1 = lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_1");
        wrongTile2 = lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_2");
        wrongTile3 = lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_3");
    }

    private static void readAndFilterContoursImages() {
        readContoursImages();
        filtreCouleursContoursEtages();
    }

    private static void readContoursImages() {
        String imageFolder = "Plateau/Hexagones/HauteurHexagone/";
        plateau_hautGauche = lisImageBuf(imageFolder + "plateau_hautGauche");
        plateau_hautDroite = lisImageBuf(imageFolder + "plateau_hautDroite");
        plateau_Gauche = lisImageBuf(imageFolder + "plateau_Gauche");
        plateau_Droite = lisImageBuf(imageFolder + "plateau_Droite");
        plateau_basGauche = lisImageBuf(imageFolder + "plateau_basGauche");
        plateau_basDroite = lisImageBuf(imageFolder + "plateau_basDroite");

        imageFolder = "Plateau/Hexagones/ContourTuile/";
        tuile_hautGauche = lisImageBuf(imageFolder + "tuile_hautGauche");
        tuile_hautDroite = lisImageBuf(imageFolder + "tuile_hautDroite");
        tuile_Gauche = lisImageBuf(imageFolder + "tuile_Gauche");
        tuile_Droite = lisImageBuf(imageFolder + "tuile_Droite");
        tuile_basGauche = lisImageBuf(imageFolder + "tuile_basGauche");
        tuile_basDroite = lisImageBuf(imageFolder + "tuile_basDroite");
    }

    private static void filtreCouleursContoursEtages() {
        plateau_hautGauche = getReducedOpacityImage(plateau_hautGauche, 0.5f);
        plateau_hautDroite = getReducedOpacityImage(plateau_hautDroite, 0.5f);
        plateau_Gauche = getReducedOpacityImage(plateau_Gauche, 0.5f);
        plateau_Droite = getReducedOpacityImage(plateau_Droite, 0.5f);
        plateau_basGauche = getReducedOpacityImage(plateau_basGauche, 0.5f);
        plateau_basDroite = getReducedOpacityImage(plateau_basDroite, 0.5f);


        plateau_hautGauche_etage1 = applyBlueFilter(plateau_hautGauche);
        plateau_hautDroite_etage1 = applyBlueFilter(plateau_hautDroite);
        plateau_Gauche_etage1 = applyBlueFilter(plateau_Gauche);
        plateau_Droite_etage1 = applyBlueFilter(plateau_Droite);
        plateau_basGauche_etage1 = applyBlueFilter(plateau_basGauche);
        plateau_basDroite_etage1 = applyBlueFilter(plateau_basDroite);

        plateau_hautGauche_etage2 = applyYellowFilter(plateau_hautGauche);
        plateau_hautDroite_etage2 = applyYellowFilter(plateau_hautDroite);
        plateau_Gauche_etage2 = applyYellowFilter(plateau_Gauche);
        plateau_Droite_etage2 = applyYellowFilter(plateau_Droite);
        plateau_basGauche_etage2 = applyYellowFilter(plateau_basGauche);
        plateau_basDroite_etage2 = applyYellowFilter(plateau_basDroite);

        plateau_hautGauche_etage3 = applyRedFilter(plateau_hautGauche);
        plateau_hautDroite_etage3 = applyRedFilter(plateau_hautDroite);
        plateau_Gauche_etage3 = applyRedFilter(plateau_Gauche);
        plateau_Droite_etage3 = applyRedFilter(plateau_Droite);
        plateau_basGauche_etage3 = applyRedFilter(plateau_basGauche);
        plateau_basDroite_etage3 = applyRedFilter(plateau_basDroite);
    }

    public static BufferedImage lisImageBuf(String nom) {
        String CHEMIN = "ressources/";
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(CHEMIN + nom + ".png"));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'image " + nom);
        }
        return img;
    }

    public static BufferedImage getReducedOpacityImage(BufferedImage originalImage, float opacity) {
        BufferedImage reducedOpacityImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = reducedOpacityImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        return reducedOpacityImage;
    }

    public static BufferedImage getTileImageFromId(int id, int numero_texture, boolean onPose) {
        if (id == VIDE) {
            return voidTile;
        }
        if (id == GRASS) {
            if(onPose){
                if (numero_texture == 0) {
                    return grassTile_0;
                }
                if (numero_texture == 1) {
                    return grassTile_1;
                }
                if (numero_texture == 2) {
                    return grassTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return grassTile_0_SP;
                }
                if (numero_texture == 1) {
                    return grassTile_1_SP;
                }
                if (numero_texture == 2) {
                    return grassTile_2_SP;
                }
            }
        }
        if (id == VOLCAN) {
            if(onPose){
                if (numero_texture == 0) {
                    return volcanTile_0;
                }
                if (numero_texture == 1) {
                    return volcanTile_1;
                }
                if (numero_texture == 2) {
                    return volcanTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return volcanTile_0_SP;
                }
                if (numero_texture == 1) {
                    return volcanTile_1_SP;
                }
                if (numero_texture == 2) {
                    return volcanTile_2_SP;
                }
            }

        }
        if (id == DESERT) {
            if(onPose){
                if (numero_texture == 0) {
                    return desertTile_0;
                }
                if (numero_texture == 1) {
                    return desertTile_1;
                }
                if (numero_texture == 2) {
                    return desertTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return desertTile_0_SP;
                }
                if (numero_texture == 1) {
                    return desertTile_1_SP;
                }
                if (numero_texture == 2) {
                    return desertTile_2_SP;
                }
            }
        }
        if (id == MONTAGNE) {
            if(onPose){
                if (numero_texture == 0) {
                    return montagneTile_0;
                }
                if (numero_texture == 1) {
                    return montagneTile_1;
                }
                if (numero_texture == 2) {
                    return montagneTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return montagneTile_0_SP;
                }
                if (numero_texture == 1) {
                    return montagneTile_1_SP;
                }
                if (numero_texture == 2) {
                    return montagneTile_2_SP;
                }
            }

        }
        if (id == FORET) {
            if(onPose){
                if (numero_texture == 0) {
                    return foretTile_0;
                }
                if (numero_texture == 1) {
                    return foretTile_1;
                }
                if (numero_texture == 2) {
                    return foretTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return foretTile_0_SP;
                }
                if (numero_texture == 1) {
                    return foretTile_1_SP;
                }
                if (numero_texture == 2) {
                    return foretTile_2_SP;
                }
            }
        }
        if (id == LAC) {
            if(onPose){
                if (numero_texture == 0) {
                    return lacTile_0;
                }
                if (numero_texture == 1) {
                    return lacTile_1;
                }
                if (numero_texture == 2) {
                    return lacTile_2;
                }
            }else{
                if (numero_texture == 0) {
                    return lacTile_0_SP;
                }
                if (numero_texture == 1) {
                    return lacTile_1_SP;
                }
                if (numero_texture == 2) {
                    return lacTile_2_SP;
                }
            }
        }
        if (id == WATER) {
            if (numero_texture == 0) {
                return waterTile;
            }
            if (numero_texture == 1) {
                return waterTile;
            }
            if (numero_texture == 2) {
                return waterTile;
            }
        }
        return null;
    }

    public static BufferedImage applyColorFilter(BufferedImage image, byte num_joueur) {
        if ((num_joueur < 0 || num_joueur > 3 || image == null)&&num_joueur!=9) {
            return image;
        }
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        if(num_joueur==9) g2d.setColor(Color.GRAY);
        else g2d.setColor(couleurs_joueurs[num_joueur]);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage applyYellowFilter(BufferedImage image) {
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(new Color(240, 252, 7, 127));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage applyBlueFilter(BufferedImage image) {
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(new Color(39, 184, 255, 127));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage applyRedFilter(BufferedImage image) {
        if(image ==null) {
            System.err.println("applyRedFilter : image null");
            return image;
        }
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(new Color(255, 0, 0, 127));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return outputImage;
    }

}
