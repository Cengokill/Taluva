package Modele;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Modele.GameState.couleurs_joueurs;

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
    public static BufferedImage plateau_hautGauche, plateau_hautDroite, plateau_Droite, plateau_Gauche, plateau_basDroite, plateau_basGauche;


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

        plateau_hautGauche = lisImageBuf("plateau_hautGauche");
        plateau_hautDroite = lisImageBuf("plateau_hautDroite");
        plateau_Gauche = lisImageBuf("plateau_Gauche");
        plateau_Droite = lisImageBuf("plateau_Droite");
        plateau_basGauche = lisImageBuf("plateau_basGauche");
        plateau_basDroite = lisImageBuf("plateau_basDroite");


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

    public static void addImage(String nom_image, int x, int y, double rapport, JLayeredPane layeredPane) {
        // Chargez l'image que vous voulez afficher
        Image image = null;
        int largeur = layeredPane.getWidth();
        int hauteur = layeredPane.getHeight();
        int largeur_bouton = (int) Math.min(largeur*.22, hauteur*.22);
        int hauteur_bouton = (int) (largeur_bouton*rapport);
        try {
            image = ImageIO.read(new File("ressources/" + nom_image + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ajoutez un JPanel pour afficher l'image par-dessus l'arrière-plan
        Image finalImage = image;
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalImage != null) {
                    g.drawImage(finalImage, 0, 0, 0, 0, this);
                }
            }
        };
        imagePanel.setBounds(x, y, largeur_bouton, hauteur_bouton);
        imagePanel.setOpaque(false);
        layeredPane.add(imagePanel, JLayeredPane.PALETTE_LAYER);
    }

    public static Image lisImage(String nom) {
        String CHEMIN = "ressources/";
        Image img = null;
        try{
            img = ImageIO.read(new File(CHEMIN + nom + ".png"));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'image " + nom);
        }
        return img;
    }

    public static BufferedImage getTileImageFromId(int id, int numero_texture) {
        if (id == Hexagone.VIDE) {
            return voidTile;
        }
        if (id == Hexagone.GRASS) {
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
        if (id == Hexagone.VOLCAN) {
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
        if (id == Hexagone.WATER) {
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
        if (id == Hexagone.MAISON) {
            return maisonTile;
        }
        if (id == Hexagone.DESERT) {
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
        if (id == Hexagone.MONTAGNE) {
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
        if (id == Hexagone.FORET) {
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
        return null;
    }

    public static BufferedImage applyColorFilter(BufferedImage image, byte num_joueur) {
        if (num_joueur < 0 || num_joueur > 3) {
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
