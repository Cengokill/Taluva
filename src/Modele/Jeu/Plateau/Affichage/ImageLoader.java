package Modele.Jeu.Plateau.Affichage;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Modele.Jeu.Plateau.EtatPlateau.couleurs_joueurs;
import static Modele.Jeu.Plateau.Hexagone.*;

public class ImageLoader {
    public static boolean loaded = false;
    public static BufferedImage
            hutteTile,
            maisonTileColor1,
            maisonTileColor2,
            templeJungle,
            templeJungleColor1,
            templeJungleColor2,
            templePierre,
            templePierreColor1,
            templePierreColor2,
            templePrairie,
            templePrairieColor1,
            templePrairieColor2,
            templeSable,
            templeSableColor1,
            templeSableColor2,
            tour,
            tourColor1,
            tourColor2;
    public static BufferedImage constructionMode;
    public static final BufferedImage[] choisirBat = new BufferedImage[12];
    public static BufferedImage waterTile, fenetre_score, background, joueur_courant;
    public static BufferedImage hoverTile, wrongTile1, wrongTile2, wrongTile3, beacons, beacon_1, beacon_2, beacon_3, beacon_4, beacon_5, beacon_6;
    public static BufferedImage voidTile, voidTile_transparent, voidTileOld, whiteTile;
    public static BufferedImage grassTile_0, grassTile_1, grassTile_2;
    public static BufferedImage volcanTile_0, volcanTile_1, volcanTile_2;
    public static BufferedImage foretTile_0, foretTile_1, foretTile_2;
    public static BufferedImage desertTile_0, desertTile_1, desertTile_2;
    public static BufferedImage lacTile_0, lacTile_1, lacTile_2;
    public static BufferedImage montagneTile_0, montagneTile_1, montagneTile_2;
    public static BufferedImage joueurCourant;
    public static BufferedImage plateau_hautGauche, plateau_hautDroite, plateau_Droite, plateau_Gauche, plateau_basDroite, plateau_basGauche;
    public static BufferedImage tuile_hautGauche, tuile_hautDroite, tuile_Droite, tuile_Gauche, tuile_basDroite, tuile_basGauche;
    public static BufferedImage bouton_save, bouton_save_select, bouton_load, bouton_load_select, bouton_annuler, bouton_annuler_select,
            bouton_refaire, bouton_refaire_select, bouton_quitter, bouton_quitter_select, bouton_tuto_off, bouton_tuto_on;
    public static int posX_fenetre_score, posY_fenetre_score, posX_boutons, posX_save, posY_save, posY_annuler, posY_refaire, posY_quitter, posY_tuto,
    posX_joueur_courant, posY_joueur_courant, posX_prenom_j0, posX_prenom_j1, posY_prenom_j0, posY_prenom_j1,
            posX_huttes, posX_tours, posX_temples, posY_scores_j0, posY_scores_j1, posX_pioche, posY_pioche;
    public static int largeur_fenetre_score, hauteur_fenetre_score, largeur, hauteur, largeur_bouton, hauteur_bouton, largeur_joueur_courant,
            hauteur_joueur_courant;
    public static boolean select_save, select_load, select_annuler, select_refaire, tuto_on, select_quitter;

    public static BufferedImage grassTile_0_Red, grassTile_1_Red, grassTile_2_Red;
    public static BufferedImage volcanTile_0_Red, volcanTile_1_Red, volcanTile_2_Red;
    public static BufferedImage foretTile_0_Red, foretTile_1_Red, foretTile_2_Red;
    public static BufferedImage desertTile_0_Red, desertTile_1_Red, desertTile_2_Red;
    public static BufferedImage montagneTile_0_Red, montagneTile_1_Red, montagneTile_2_Red;
    public static BufferedImage lacTile_0_Red, lacTile_1_Red, lacTile_2_Red;
    public static BufferedImage tileErreur;

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
        background = lisImageBuf("/Plateau/background");
        readTilesImages();
        readPlayableTilesImages();
        readHeightImages(lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_1_hex"), lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_2_hex"), lisImageBuf("/Plateau/Hexagones/Textures/Wrong_height_3_hex"));
        readHeightImages(getReducedOpacityImage(wrongTile1, 0.5f), getReducedOpacityImage(wrongTile2, 0.5f), getReducedOpacityImage(wrongTile3, 0.5f));
        readTileOrientationImages();
        readBatimentsImages();
        readSelectionBatimentImage();
        readAndFilterContoursImages();
        filterTiles();
        loaded = true;
    }

    private static void readSelectionBatimentImage() {
        String imageFolder = "Plateau/Batiments/Selecteur/";

        for(int i=0;i<3;i++){
            choisirBat[i] = lisImageBuf(imageFolder + "choisir_bat_" +(i+1));
        }
        choisirBat[3] = lisImageBuf(imageFolder + "choisir_bat_1_sans_2");
        choisirBat[4] = lisImageBuf(imageFolder + "choisir_bat_1_sans_3");
        choisirBat[5] = lisImageBuf(imageFolder + "choisir_bat_2_sans_3");
        choisirBat[6] = lisImageBuf(imageFolder + "choisir_bat_3_sans_2");
        choisirBat[7] = lisImageBuf(imageFolder + "choisir_bat_sans_23");
        choisirBat[8] = lisImageBuf(imageFolder + "choisir_bat_2_sans_1");
        choisirBat[9] = lisImageBuf(imageFolder + "choisir_bat_2_sans_1_3");
        choisirBat[10] = lisImageBuf(imageFolder + "choisir_bat_3_sans_1");
        choisirBat[11] = lisImageBuf(imageFolder + "choisir_bat_3_sans_1_2");

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

        tileErreur = getReducedOpacityImage(grassTile_0_Red, 0.5f);
    }

    private static void readTilesImages() {
        String imageFolder = "/Plateau/Hexagones/Textures/";

        waterTile = lisImageBuf(imageFolder + "Water_Tile");
        voidTile = lisImageBuf(imageFolder + "Void_Tile");
        whiteTile = lisImageBuf(imageFolder + "White_Tile");
        voidTileOld = lisImageBuf(imageFolder + "Void_Tile_old");
        hoverTile = lisImageBuf(imageFolder + "Hover_Tile");
    }

    public static BufferedImage getBatimentFromPlayerId(byte id_player, byte batiment_id,int typeTerrain) {
        if (batiment_id == HUTTE) {
            return getMaison(id_player);
        }
        if (batiment_id == TEMPLE) {
            return getTemple(id_player,typeTerrain);
        }
        if (batiment_id == TOUR) {
            return getTour(id_player);
        }
        return null;
    }

    private static BufferedImage getTempleSable(byte id_player) {
        if (id_player == 0) {
            return templeSableColor1;
        } else {
            return templeSableColor2;
        }
    }

    private static BufferedImage getTour(byte id_player) {
        if (id_player == 0) {
            return tourColor1;
        } else {
            return tourColor2;
        }
    }

    private static BufferedImage getTemple(byte id_player,int typeTerrain) {
        if (id_player == 0) {
            if(typeTerrain==GRASS) return templePrairieColor1;
            if(typeTerrain==MONTAGNE) return templePierreColor1;
            if(typeTerrain==DESERT) return templeSableColor1;
            return templeJungleColor1;
        } else {
            if(typeTerrain==GRASS) return templePrairieColor2;
            if(typeTerrain==MONTAGNE) return templePierreColor2;
            if(typeTerrain==DESERT) return templeSableColor2;
            return templeJungleColor2;
        }
    }

    private static BufferedImage getMaison(byte id_player) {
        if (id_player == 0) {
            return maisonTileColor1;
        } else {
            return maisonTileColor2;
        }
    }

    private static void readPlayableTilesImages() {
        String imageFolder = "/Plateau/Hexagones/Textures/";

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

    private static void readBatimentsImages() {
        String imageFolder = "/Plateau/Batiments/";

        hutteTile = lisImageBuf(imageFolder + "hutte");
        templeJungle = lisImageBuf(imageFolder + "Temple_jungle");
        templePierre = lisImageBuf(imageFolder + "Temple_pierre");
        templePrairie = lisImageBuf(imageFolder + "Temple_prairie");
        templeSable = lisImageBuf(imageFolder + "Temple_sable");
        tour = lisImageBuf(imageFolder + "tour");
        filtreCouleurBatiments();
    }

    private static void filtreCouleurBatiments() {
        maisonTileColor1 = applyColorFilter(hutteTile, (byte) 0);
        maisonTileColor2 = applyColorFilter(hutteTile, (byte) 1);

        templeJungleColor1 = applyColorFilter(templeJungle, (byte) 0);
        templeJungleColor2 = applyColorFilter(templeJungle, (byte) 1);

        templePierreColor1 = applyColorFilter(templePierre, (byte) 0);
        templePierreColor2 = applyColorFilter(templePierre, (byte) 1);

        templePrairieColor1 = applyColorFilter(templePrairie, (byte) 0);
        templePrairieColor2 = applyColorFilter(templePrairie, (byte) 1);

        templeSableColor1 = applyColorFilter(templeSable, (byte) 0);
        templeSableColor2 = applyColorFilter(templeSable, (byte) 1);

        tourColor1 = applyColorFilter(tour, (byte) 0);
        tourColor2 = applyColorFilter(tour, (byte) 1);
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

    private static void readHeightImages(BufferedImage wrong_height_1_hex, BufferedImage wrong_height_2_hex, BufferedImage wrong_height_3_hex) {
        wrongTile1 = wrong_height_1_hex;
        wrongTile2 = wrong_height_2_hex;
        wrongTile3 = wrong_height_3_hex;
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

    public static BufferedImage getTileImageFromId(int id, int numero_texture) {
        if (id == VIDE) {
            return voidTile;
        }
        if (id == GRASS) {
            if (numero_texture == 0) {
                return grassTile_0;
            }
            if (numero_texture == 1) {
                return grassTile_1;
            }
            if (numero_texture == 2) {
                return grassTile_2;
            }
        }
        if (id == VOLCAN) {
            if (numero_texture == 0) {
                return volcanTile_0;
            }
            if (numero_texture == 1) {
                return volcanTile_1;
            }
            if (numero_texture == 2) {
                return volcanTile_2;
            }
        }
        if (id == HUTTE) {
            return hutteTile;
        }
        if (id == DESERT) {
            if (numero_texture == 0) {
                return desertTile_0;
            }
            if (numero_texture == 1) {
                return desertTile_1;
            }
            if (numero_texture == 2) {
                return desertTile_2;
            }
        }
        if (id == MONTAGNE) {
            if (numero_texture == 0) {
                return montagneTile_0;
            }
            if (numero_texture == 1) {
                return montagneTile_1;
            }
            if (numero_texture == 2) {
                return montagneTile_2;
            }
        }
        if (id == FORET) {
            if (numero_texture == 0) {
                return foretTile_0;
            }
            if (numero_texture == 1) {
                return foretTile_1;
            }
            if (numero_texture == 2) {
                return foretTile_2;
            }
        }
        if (id == LAC) {
            if (numero_texture == 0) {
                return lacTile_0;
            }
            if (numero_texture == 1) {
                return lacTile_1;
            }
            if (numero_texture == 2) {
                return lacTile_2;
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
        if (num_joueur < 0 || num_joueur > 3 || image == null) {
            return image;
        }
        //System.out.println(num_joueur);
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(couleurs_joueurs[num_joueur]);
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
