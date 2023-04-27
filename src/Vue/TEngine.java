package Vue;

import Controleur.ControleurMediateur;
import Modele.Hexagone;
import Modele.Jeu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.util.Random;

public class TEngine extends JFrame {
    int tile_size = 148;
    TEngineListener listener;
    public HexagonalTiles hexTiles;
    ControleurMediateur controleur;

    Point LastPosition;
    boolean poseTile, mode_plateau = true, mode_numero = false;
    Jeu jeu;

    public TEngine(Jeu jeu, ControleurMediateur controleur) {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        this.jeu = jeu;
        setTitle("Taluva");
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Créer un JLayeredPane pour superposer les éléments
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1400, 1000));
        getContentPane().add(layeredPane);

        // Ajouter les tuiles hexagonales
        hexTiles = new HexagonalTiles(this, controleur);
        hexTiles.setBounds(0, 0, 1400, 1000);
        layeredPane.add(hexTiles, JLayeredPane.DEFAULT_LAYER);

        // Ajouter la vignette
        VignettePanel vignettePanel = new VignettePanel();
        vignettePanel.setBounds(0, 0, 1400, 1000);
        layeredPane.add(vignettePanel, JLayeredPane.PALETTE_LAYER);

        // Définir la couleur d'arrière-plan en bleu océan
        getContentPane().setBackground(new Color(64, 164, 223));

        addImage("map_layer_little", 50, 800, layeredPane);

        listener = new TEngineListener(this);
        poseTile = true;
    }

    public void addImage(String nom_image, int x, int y, JLayeredPane layeredPane) {
        // Chargez l'image que vous voulez afficher
        Image image = null;
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
                    g.drawImage(finalImage, 0, 0, this);
                }
            }
        };
        imagePanel.setBounds(x, y, 1000, 1000);
        imagePanel.setOpaque(false);
        layeredPane.add(imagePanel, JLayeredPane.PALETTE_LAYER);
    }


    public class HexagonalTiles extends JPanel {
        private static final int SHAKE_DURATION = 200; // Durée de l'effet en millisecondes
        private static final int SHAKE_INTERVAL = 25; // Intervalle entre les mouvements en millisecondes
        private static final int SHAKE_DISTANCE = 10; // Distance maximale de déplacement en pixels

        BufferedImage maisonTile, templeJungle, templePierre, templePrairie, templeSable,tour, chosirMaison;
        BufferedImage[] choisirBat = new BufferedImage[3];
        BufferedImage waterTile;
        BufferedImage hoverTile, wrongTile1, wrongTile2, wrongTile3;
        BufferedImage voidTile, voidTile_transparent;
        BufferedImage grassTile_0, grassTile_1, grassTile_2;
        BufferedImage volcanTile_0, volcanTile_1, volcanTile_2;
        BufferedImage foretTile_0, foretTile_1, foretTile_2;
        BufferedImage desertTile_0, desertTile_1, desertTile_2;
        BufferedImage montagneTile_0, montagneTile_1, montagneTile_2;
        BufferedImage joueurCourant;
        Image boutonAnnuler;
        int largeur, hauteur, posY_bouton_annuler, posX_bouton_annuler, largeur_bouton, hauteur_bouton;
        int largeur_joueurCourant, hauteur_joueurCourant, posX_joueurCourant, posY_joueurCourant;
        TEngine tengine;
        Point hoverTilePosition = new Point(-tile_size, -tile_size);
        Point cameraOffset = new Point(0, 0);
        Point lastMousePosition;
        double zoomFactor = 0.3;
        double zoomIncrement = 0.1;
        int scrollValue = 1;
        byte[][] triplet = new byte[3][2]; // [n° tile] [0: tile_type] [1: tile_textureid]
        TEngineListener.MouseHandler handler;
        TEngineListener.KeyboardListener keyboardlisten;

        boolean enSelection = false;

        public boolean clicDroiteEnfonce = false;
        int typeAConstruire=0, posBat_x, posBat_y;
        ControleurMediateur controleur;
        int hoveredTile_x;
        int hoveredTile_y;
        Color[] couleurs_joueurs;

        public HexagonalTiles(TEngine t, ControleurMediateur controleur) {
            this.tengine = t;
            this.controleur = controleur;
            joueurCourant = lisImageBuf("Joueur_Courant");
            waterTile = lisImageBuf("Water_Tile");
            voidTile = lisImageBuf("Void_Tile");
            voidTile_transparent = getReducedOpacityImage(voidTile, 0.5f);
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

            wrongTile1 = getReducedOpacityImage(wrongTile1, 0.5f);
            wrongTile2 = getReducedOpacityImage(wrongTile2, 0.5f);
            wrongTile3 = getReducedOpacityImage(wrongTile3, 0.5f);

            wrongTile1 = applyYellowFilter(wrongTile1);
            wrongTile2 = applyYellowFilter(wrongTile2);
            wrongTile3 = applyYellowFilter(wrongTile3);

            boutonAnnuler = lisImage("annuler");
            maisonTile = lisImageBuf("Batiments/maison");
            templeJungle = lisImageBuf("Batiments/Temple_jungle");
            templePierre = lisImageBuf("Batiments/Temple_pierre");
            templePrairie = lisImageBuf("Batiments/Temple_prairie");
            templeSable = lisImageBuf("Batiments/Temple_sable");
            tour = lisImageBuf("Batiments/tour");
            for(int i=0;i<choisirBat.length;i++){
                choisirBat[i] = lisImageBuf("Batiments/choisir_bat_"+(i+1));
            }


            setOpaque(false);

            cameraOffset.x = -2100;
            cameraOffset.y = -1700;

            triplet[0][0] = Hexagone.VOLCAN;
            triplet[1][0] = Hexagone.VIDE;
            triplet[2][0] = Hexagone.VIDE;

            triplet[0][1] = 0;
            triplet[1][1] = 0;
            triplet[2][1] = 0;

            largeur = tengine.getWidth();
            hauteur = tengine.getHeight();

            couleurs_joueurs = new Color[4];
            couleurs_joueurs[0] = new Color(255, 0, 0, 127);
            couleurs_joueurs[1] = new Color(0, 233, 255, 127);
            couleurs_joueurs[2] = new Color(183, 0, 255, 127);
            couleurs_joueurs[3] = new Color(255, 185, 0, 127);
        }

        public void changerTuileAPoser() {
            byte[] tuiles;
            tuiles = controleur.getTuileAPoser();
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2

            //System.out.println(tuiles[2]);
            //System.out.println(tuiles[3]);
            //System.out.println(tuiles[4]);
            triplet[0][1] = tuiles[2]; // volcan
            triplet[1][1] = tuiles[3]; // tile 1
            triplet[2][1] = tuiles[4]; // tile 2
        }

        private Image lisImage(String nom) {
            String CHEMIN = "ressources/";
            Image img = null;
            try{
                img = ImageIO.read(new File(CHEMIN + nom + ".png"));
            } catch (IOException e) {
                System.err.println("Impossible de charger l'image " + nom);
            }
            return img;
        }

        private BufferedImage lisImageBuf(String nom) {
            String CHEMIN = "ressources/";
            BufferedImage img = null;
            try{
                img = ImageIO.read(new File(CHEMIN + nom + ".png"));
            } catch (IOException e) {
                System.err.println("Impossible de charger l'image " + nom);
            }
            return img;
        }

        public void shake() {
            Random random = new Random();
            long startTime = System.currentTimeMillis();
            long endTime = startTime + SHAKE_DURATION;

            // Créez un Timer pour exécuter l'animation en arrière-plan
            Timer timer = new Timer(SHAKE_INTERVAL, null);
            timer.addActionListener(e -> {
                if (System.currentTimeMillis() >= endTime) {
                    // Arrêtez le Timer et réinitialisez les décalages de la caméra
                    timer.stop();

                } else {
                    int deltaX = random.nextInt(SHAKE_DISTANCE * 2) - SHAKE_DISTANCE;
                    int deltaY = random.nextInt(SHAKE_DISTANCE * 2) - SHAKE_DISTANCE;

                    // Mettre à jour les décalages de la caméra
                    cameraOffset.x += deltaX;
                    cameraOffset.y += deltaY;
                    repaint();
                }
            });

            // Démarrez le Timer
            timer.start();
        }

        /*
        public void afficheJoueurCourant(Graphics g){
            posY_joueurCourant = (int) (hauteur*.05);
            posX_joueurCourant = (int) (largeur*.40);
            int x = (int)(posX_joueurCourant/zoomFactor) - (int)(cameraOffset.x/zoomFactor);
            int y = (int)(posY_joueurCourant/zoomFactor) - (int)(cameraOffset.y/zoomFactor);
            int largeur = (int)(largeur_joueurCourant/zoomFactor);
            int hauteur = (int)(hauteur_joueurCourant/zoomFactor);
            tracer((Graphics2D) g, joueurCourant, x, y, largeur, hauteur);
            Font font = new Font("Roboto", Font.BOLD, (int) (40/zoomFactor));
            g.setFont(font);
            g.drawString(jeu.getJoueurCourant(), x, y+hauteur);
        }
         */

        private void tracer(Graphics2D g, Image i, int x, int y, int l, int h) {
            g.drawImage(i, x, y, l, h, null);
        }

        public void miseAJour() {
            repaint();
        }

        private void changerPoseTile() {
            poseTile = jeu.doit_placer_tuile();
        }

        @Override
        protected void paintComponent(Graphics g) {
            changerTuileAPoser();
            changerPoseTile();

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(cameraOffset.x, cameraOffset.y);
            g2d.scale(zoomFactor, zoomFactor);

            //définit la taille des boutons et des encadrés
            double rapport_bouton = (double) 207/603;
            largeur_bouton = (int) Math.min(largeur*.22, hauteur*.22);
            hauteur_bouton = (int) (largeur_bouton*rapport_bouton);

            double rapport_joueurCourant = (double) 131/603;
            largeur_joueurCourant = (int) Math.min(largeur*.22, hauteur*.22);
            hauteur_joueurCourant = (int) (largeur_joueurCourant*rapport_joueurCourant);

            displayHexagonMap(g);

            if(poseTile) displayHoverTile(g);
            else displayHoverMaison(g);

            //affichage des boutons et des encadrés
            //afficheJoueurCourant(g);
        }

        private void displayHexagonMap(Graphics g) {
            Hexagone[][] map = controleur.getPlateau();
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int verticalOffset = (int) (tileHeight * 0.75);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int x = j* tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                    int y = i * verticalOffset;
                    int tileId = map[i][j].getTerrain();

                    int heightoffset = map[i][j].getHauteur();
                    heightoffset *= 50;

                    if (map[i][j].getTerrain() == Hexagone.VIDE) {
                        heightoffset -= 50;
                    }

                    BufferedImage tile = getTileImageFromId(tileId, map[i][j].getNum());
                    g.drawImage(tile, x , y - heightoffset, null);

                    if (poseTile) {
                        if (mode_plateau) {
                            if (map[i][j].getHauteur() != map[hoveredTile_x][hoveredTile_y].getHauteur()) {
                                if (map[hoveredTile_x][hoveredTile_y].getHauteur() != 0) {
                                    g.drawImage(voidTile_transparent, x , y - heightoffset + 5, null);
                                }
                            }
                        } else {
                            if (map[hoveredTile_x][hoveredTile_y].getHauteur() != 0) {
                                g.drawImage(voidTile_transparent, x , y - heightoffset, null);
                            }
                        }
                    }

                    if (mode_numero) {
                        if (map[i][j].getHauteur() == 1) {
                            g.drawImage(wrongTile1, x , y - heightoffset + 5, null);
                        }
                        if (map[i][j].getHauteur() == 2) {
                            g.drawImage(wrongTile2, x , y - heightoffset + 5, null);
                        }
                        if (map[i][j].getHauteur() == 3) {
                            g.drawImage(wrongTile3, x , y - heightoffset + 5, null);
                        }
                    }

                    if (map[i][j].getBatiment() == Hexagone.MAISON) {
                        tile = getTileImageFromId(Hexagone.MAISON, map[i][j].getNum());
                        g.drawImage(applyColorFilter(tile, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_FORET) {
                        g.drawImage(applyColorFilter(templeJungle, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PRAIRIE) {
                        g.drawImage(applyColorFilter(templePrairie, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PIERRE) {
                        g.drawImage(applyColorFilter(templePierre, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_SABLE) {
                        g.drawImage(applyColorFilter(templeSable, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TOUR) {
                        g.drawImage(applyColorFilter(tour, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.CHOISIR_MAISON) {
                        int pos_x = x-150;
                        int pos_y = y -300;
                        int value = scrollValue%3;
                        if(value==1) value = 0;
                        else if(value==0) value = 1;
                        g.drawImage(choisirBat[value], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                    }

                }
            }
        }

        private BufferedImage getTileImageFromId(int id, int numero_texture) {
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
            //System.out.println("Nul bebou: " + id + " - " + numero_texture);
            return null;
        }

        public void updateCursorPosOnTiles(MouseEvent e) {
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int verticalOffset = (int) (tileHeight * 0.75);

            Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                    (int) ((e.getY() - cameraOffset.y) / zoomFactor));
            LastPosition = clickPositionAdjusted;

            // Convertir les coordonnées du système de pixels en coordonnées du système de grille
            int i = clickPositionAdjusted.y / verticalOffset;
            int j = (clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

            hoveredTile_x = i;
            hoveredTile_y = j;
        }

        private void displayHoverTile(Graphics g) {
            if (hoverTile != null && !clicDroiteEnfonce) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth(); // Important !!
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = hoverTilePositionAdjusted.y / verticalOffset;
                int j = (hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                int j2;
                if (i % 2 == 1) {
                    j2 = j - 1;
                } else {
                    j2 = j;
                }
                BufferedImage tile1 = getTileImageFromId(triplet[0][0],triplet[0][1]);
                BufferedImage tile2 = getTileImageFromId(triplet[1][0],triplet[1][1]);
                BufferedImage tile3 = getTileImageFromId(triplet[2][0],triplet[2][1]);

                float opacity = 1f;

                if (scrollValue == 1) {
                    if (!controleur.peutPlacerTuile(i, j, i - 1, j2, i - 1, j2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 2){
                    if (!controleur.peutPlacerTuile(i, j, i - 1, j2 + 1, i, j + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 3){
                    if (!controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 4){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, j2 + 1, i + 1, j2)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 5){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, j2, i, j - 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 6){
                    if (!controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j2)) {
                        opacity = 0.4f;
                    }
                }
                if (opacity != 1f) {
                    tile1 = applyRedFilter(tile1);
                    tile2 = applyRedFilter(tile1);
                    tile3 = applyRedFilter(tile1);
                }
                tile1 = getReducedOpacityImage(tile1, opacity);
                tile2 = getReducedOpacityImage(tile2, opacity);
                tile3 = getReducedOpacityImage(tile3, opacity);

                int heightoffset1 = 1;
                int heightoffset2 = 1;
                int heightoffset3 = 1;
                heightoffset1 *= 30;
                heightoffset2 *= 30;
                heightoffset3 *= 30;

                if (scrollValue == 1) {
                    g.drawImage(tile2, x - tileWidth/2, y - verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x + tileWidth/2, y - verticalOffset - heightoffset3, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);
                }
                else if (scrollValue == 2){
                    g.drawImage(tile2, x + tileWidth/2, y - verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x + tileWidth, y - heightoffset3, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);

                }
                else if (scrollValue == 3){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile2, x + tileWidth, y -  heightoffset2, null);
                    g.drawImage(tile3, x +  tileWidth /2, y + verticalOffset - heightoffset3, null);
                }
                else if (scrollValue == 4){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile2, x + tileWidth/2, y + verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x - tileWidth/2, y + verticalOffset - heightoffset3, null);
                }
                else if (scrollValue == 5){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile3, x - tileWidth, y - heightoffset3, null);
                    g.drawImage(tile2, x - tileWidth/2, y + verticalOffset -  heightoffset2, null);
                }
                else if (scrollValue == 6){
                    g.drawImage(tile3, x - tileWidth/2, y - verticalOffset - heightoffset3, null);
                    g.drawImage(tile2, x - tileWidth, y -  heightoffset2, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);

                }
            }
        }

        public BufferedImage applyRedFilter(BufferedImage image) {
            BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.setComposite(AlphaComposite.SrcAtop);
            g2d.setColor(new Color(255, 0, 0, 127));
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.dispose();
            return outputImage;
        }

        public BufferedImage applyColorFilter(BufferedImage image, byte num_joueur) {
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

        public BufferedImage applyYellowFilter(BufferedImage image) {
            BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.setComposite(AlphaComposite.SrcAtop);
            g2d.setColor(new Color(240, 252, 7, 127));
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.dispose();
            return outputImage;
        }



        private void displayHoverMaison(Graphics g) {
            if (hoverTile != null) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth(); // Important !!
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = hoverTilePositionAdjusted.y / verticalOffset;
                int j = (hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                int heightoffset1 = triplet[0][1];
                heightoffset1 *= 30;

                if(!enSelection){
                    if(jeu.getPlateau().getTuile(i,j).getBatiment()==0 && jeu.getPlateau().getTuile(i,j).getTerrain() != Hexagone.VOLCAN){
                        if(jeu.getPlateau().getHauteurTuile(i,j)==1) g.drawImage(maisonTile, x , y - heightoffset1, null);
                        else if(jeu.getPlateau().getHauteurTuile(i,j)==2){
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.DESERT) g.drawImage(templeSable, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.MONTAGNE) g.drawImage(templePierre, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.GRASS) g.drawImage(templePrairie, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.FORET) g.drawImage(templeJungle, x , y - heightoffset1, null);
                        }else if(jeu.getPlateau().getHauteurTuile(i,j)==3){
                            g.drawImage(tour, x , y - heightoffset1, null);

                        }
                    }
                }
            }
        }


        private BufferedImage getReducedOpacityImage(BufferedImage originalImage, float opacity) {
            BufferedImage reducedOpacityImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = reducedOpacityImage.createGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            return reducedOpacityImage;
        }

        public void placerTuiles(int i, int j) {
            int j_modified;
            if (i % 2 == 1) {
                j_modified = j - 1;
            } else {
                j_modified = j;
            }

            if (scrollValue == 1) {
                if (controleur.peutPlacerTuile(i, j, i - 1, j_modified, i - 1, j_modified + 1)) {
                    controleur.placeEtage(i, j, i - 1, j_modified, triplet[1][0], i - 1, j_modified + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 2){
                if (controleur.peutPlacerTuile(i, j, i - 1, j_modified + 1, i, j + 1)) {
                    controleur.placeEtage(i, j, i - 1, j_modified + 1, triplet[1][0], i, j + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 3){
                if (controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j_modified + 1)) {
                    controleur.placeEtage(i, j, i, j + 1, triplet[1][0], i + 1, j_modified + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 4){
                if (controleur.peutPlacerTuile(i, j, i + 1, j_modified + 1, i + 1, j_modified)) {
                    controleur.placeEtage(i, j, i + 1, j_modified + 1, triplet[1][0], i + 1, j_modified, triplet[2][0]);
                }
            }
            else if (scrollValue == 5){
                if (controleur.peutPlacerTuile(i, j, i + 1, j_modified, i, j - 1)) {
                    controleur.placeEtage(i, j, i + 1, j_modified, triplet[1][0], i, j - 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 6){
                if (controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j_modified)) {
                    controleur.placeEtage(i, j, i, j - 1, triplet[1][0], i - 1, j_modified, triplet[2][0]);
                }
            }

            miseAJour();
        }

        private boolean possedeBatiment(int i,int j){
            return (jeu.getPlateau().getBatiment(i,j)==Hexagone.TOUR||jeu.getPlateau().getBatiment(i,j)==Hexagone.MAISON||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_SABLE||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_FORET
                    ||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_PIERRE||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_PRAIRIE);
        }
        private boolean aCiteAutour(int i,int j){
            boolean bool = possedeBatiment(i-1,j)||possedeBatiment(i+1,j)||possedeBatiment(i,j-1)||possedeBatiment(i,j+1);
            if(i%2==1){
                if(possedeBatiment(i-1,j-1)) {
                    bool = true;
                }
                if(possedeBatiment(i+1,j-1)) {
                    bool = true;
                }
            }else{
                if(possedeBatiment(i-1,j+1)) {
                    bool = true;
                }
                if(possedeBatiment(i+1,j+1)) {
                    bool = true;
                }
            }
            return bool;
        }

        public void placerMaison(int i, int j) {
            int value = scrollValue%3;
            if (value == 1) { // place hut
                enSelection = false;
                controleur.placeBatiment(i,j,(byte) 1);
            }
            else if (value == 2){ // place tour
                if(jeu.getPlateau().getHauteurTuile(i,j)==3){ // on verifie la condition pour poser une tour
                    enSelection = false;
                    controleur.placeBatiment(i,j,(byte) 3);
                }
            }
            else if (value == 0){ // place temple
                if(aCiteAutour(i,j)){
                    enSelection = false;
                    controleur.placeBatiment(i,j,(byte) 2);
                }
            }
        }

        public void addToCursor(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth();
                int verticalOffset = (int) (tileHeight * 0.75);

                Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                        (int) ((e.getY() - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = clickPositionAdjusted.y / verticalOffset;
                int j = (clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;
                //System.out.println("i: " + i);
                //System.out.println("j: " + j);

                if(poseTile) placerTuiles(i,j);
                else{
                    if(!enSelection){
                        if (controleur.peutPlacerBatiment(i, j)) {
                            posBat_x = i;
                            posBat_y = j;
                            enSelection = true;
                            controleur.placeBatiment(posBat_x, posBat_y,(byte) 4);
                        }
                    }else{
                        placerMaison(posBat_x,posBat_y);
                    }
                }

                miseAJour();
            }
        }
    }
}