package Modele;

import Vue.TEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static BufferedImage maisonTile, templeJungle, templePierre, templePrairie, templeSable,tour, constructionMode;
    public static BufferedImage[] choisirBat = new BufferedImage[8];
    public static BufferedImage waterTile;
    public static BufferedImage hoverTile, wrongTile1, wrongTile2, wrongTile3, beacons, beacon_1, beacon_2, beacon_3, beacon_4, beacon_5, beacon_6;
    public static BufferedImage voidTile, voidTile_transparent, voidTileOld, whiteTile;
    public static BufferedImage grassTile_0, grassTile_1, grassTile_2;
    public static BufferedImage volcanTile_0, volcanTile_1, volcanTile_2;
    public static BufferedImage foretTile_0, foretTile_1, foretTile_2;
    public static BufferedImage desertTile_0, desertTile_1, desertTile_2;
    public static BufferedImage montagneTile_0, montagneTile_1, montagneTile_2;
    public static BufferedImage joueurCourant;


    public static void loadImages() {
        joueurCourant = lisImageBuf("Joueur_Courant");
        waterTile = lisImageBuf("Water_Tile");
        voidTile = lisImageBuf("Void_Tile");
        whiteTile = lisImageBuf("White_Tile");
        voidTileOld = lisImageBuf("Void_Tile_old");
        voidTile_transparent = getReducedOpacityImage(voidTileOld, 0.2f);
        whiteTile = getReducedOpacityImage(whiteTile, 0.35f);
        grassTile_0 = lisImageBuf("Grass_0_Tile");
        grassTile_1 = lisImageBuf("Grass_1_Tile");
        grassTile_2 = lisImageBuf("Grass_2_Tile");
        volcanTile_0 = lisImageBuf("Volcan_0_Tile");
        volcanTile_1 = lisImageBuf("Volcan_1_Tile");
        volcanTile_2 = lisImageBuf("Volcan_2_Tile");
        foretTile_0 = lisImageBuf("Foret_0_Tile");
        foretTile_1 = lisImageBuf("Foret_1_Tile");
        foretTile_2 = lisImageBuf("Foret_2_Tile");
        desertTile_0 = lisImageBuf("Desert_0_Tile");
        desertTile_1 = lisImageBuf("Desert_1_Tile");
        desertTile_2 = lisImageBuf("Desert_2_Tile");
        montagneTile_0 = lisImageBuf("Montagne_0_Tile");
        montagneTile_1 = lisImageBuf("Montagne_1_Tile");
        montagneTile_2 = lisImageBuf("Montagne_2_Tile");
        hoverTile = lisImageBuf("Hover_Tile");
        wrongTile1 = lisImageBuf("Wrong_height_1_hex");
        wrongTile2 = lisImageBuf("Wrong_height_2_hex");
        wrongTile3 = lisImageBuf("Wrong_height_3_hex");

        beacons = lisImageBuf("Beacons");
        beacon_1 = lisImageBuf("Beacon_1");
        beacon_2 = lisImageBuf("Beacon_2");
        beacon_3 = lisImageBuf("Beacon_3");
        beacon_4 = lisImageBuf("Beacon_4");
        beacon_5 = lisImageBuf("Beacon_5");
        beacon_6 = lisImageBuf("Beacon_6");

        wrongTile1 = getReducedOpacityImage(wrongTile1, 0.5f);
        wrongTile2 = getReducedOpacityImage(wrongTile2, 0.5f);
        wrongTile3 = getReducedOpacityImage(wrongTile3, 0.5f);

        //wrongTile1 = applyYellowFilter(wrongTile1);
        //wrongTile2 = applyYellowFilter(wrongTile2);
        //wrongTile3 = applyYellowFilter(wrongTile3);

        maisonTile = lisImageBuf("Batiments/maison");
        templeJungle = lisImageBuf("Batiments/Temple_jungle");
        templePierre = lisImageBuf("Batiments/Temple_pierre");
        templePrairie = lisImageBuf("Batiments/Temple_prairie");
        templeSable = lisImageBuf("Batiments/Temple_sable");
        tour = lisImageBuf("Batiments/tour");
        for(int i=0;i<3;i++){
            choisirBat[i] = lisImageBuf("Batiments/Selecteur/choisir_bat_"+(i+1));
        }
        choisirBat[3] = lisImageBuf("Batiments/Selecteur/choisir_bat_1_sans_2");
        choisirBat[4] = lisImageBuf("Batiments/Selecteur/choisir_bat_1_sans_3");
        choisirBat[5] = lisImageBuf("Batiments/Selecteur/choisir_bat_2_sans_3");
        choisirBat[6] = lisImageBuf("Batiments/Selecteur/choisir_bat_3_sans_2");
        choisirBat[7] = lisImageBuf("Batiments/Selecteur/choisir_bat_sans_23");
        constructionMode = lisImageBuf("Batiments/Selecteur/construction");
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
}
