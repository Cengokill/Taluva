package Vue;

import Controleur.ControleurMediateur;
import Modele.Hexagone;
import Modele.Jeu;
import Modele.Plateau;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseEvent;

public class TEngine extends JFrame {
    int width = 400;
    int height = 200;
    int tile_size = 148;
    TEngineListener listener;
    public HexagonalTiles hexTiles;
    ControleurMediateur controleur;

    boolean poseTile, mode_plateau = false, mode_numero = false;
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

        listener = new TEngineListener(this);
        poseTile = true;
    }


    public class HexagonalTiles extends JPanel {
        BufferedImage maisonTile, templeJungle, templePierre, templePrairie, templeSable,tour;
        BufferedImage waterTile;
        BufferedImage hoverTile, wrongTile1, wrongTile2, wrongTile3;
        BufferedImage voidTile, voidTile_transparent;
        BufferedImage grassTile_0, grassTile_1, grassTile_2;
        BufferedImage volcanTile_0, volcanTile_1, volcanTile_2;
        BufferedImage foretTile_0, foretTile_1, foretTile_2;
        BufferedImage desertTile_0, desertTile_1, desertTile_2;
        BufferedImage montagneTile_0, montagneTile_1, montagneTile_2;
        Image boutonAnnuler;
        int largeur, hauteur, posY_bouton_annuler, posX_bouton_annuler, largeur_bouton, hauteur_bouton;
        TEngine tengine;
        Point hoverTilePosition = new Point(-tile_size, -tile_size);
        Point cameraOffset = new Point(0, 0);
        Point lastMousePosition;
        double zoomFactor = 0.3;
        double zoomIncrement = 0.1;

        private Plateau plateau;

        int scrollValue = 1;

        byte[][] triplet = new byte[3][2]; // [n° tile] [0: tile_type] [1: tile_textureid]

        TEngineListener.MouseHandler handler;
        TEngineListener.KeyboardListener keyboardlisten;

        ControleurMediateur controleur;

        int hoveredTile_x;
        int hoveredTile_y;

        public HexagonalTiles(TEngine t, ControleurMediateur controleur) {
            this.tengine = t;
            this.controleur = controleur;
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

        public void afficherBoutonAnnuler(Graphics g){
            posY_bouton_annuler = (int) (hauteur*.15);
            posX_bouton_annuler = (int) (largeur*.80);

            int x = (int)(posX_bouton_annuler/zoomFactor) - (int)(cameraOffset.x/zoomFactor);
            int y = (int)(posY_bouton_annuler/zoomFactor) - (int)(cameraOffset.y/zoomFactor);
            int largeur = (int)(largeur_bouton/zoomFactor);
            int longueur = (int)(hauteur_bouton/zoomFactor);

            tracer((Graphics2D) g, boutonAnnuler, x, y, largeur, longueur);
        }

        private void tracer(Graphics2D g, Image i, int x, int y, int l, int h) {
            g.drawImage(i, x, y, l, h, null);
        }

        public void miseAJour() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            changerTuileAPoser();

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(cameraOffset.x, cameraOffset.y);
            g2d.scale(zoomFactor, zoomFactor);

            //définit la taille des boutons
            double rapport_bouton = (double) 207/603;
            largeur_bouton = (int) Math.min(largeur*.22, hauteur*.22);
            hauteur_bouton = (int) (largeur_bouton*rapport_bouton);

            displayHexagonMap(g);

            if(poseTile) displayHoverTile(g);
            else displayHoverMaison(g);

            afficherBoutonAnnuler(g);
        }


        /////////////////
        // 0 = VOID    //
        // 1 = Grass   //
        /////////////////
        private void displayIntMap(int[][] map, Graphics g) {
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int horizontalOffset = tileWidth;
            int verticalOffset = (int) (tileHeight * 0.75);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int x = j*horizontalOffset - (i % 2 == 1 ? tileWidth / 2 : 0);
                    int y = i * verticalOffset;
                    int tileId = map[i][j];
                    BufferedImage tile = getTileImageFromId(tileId, 0);
                    g.drawImage(tile, x , y, null);
                }
            }
        }

        private void displayHexagonMap(Graphics g) {
            Hexagone[][] map = controleur.getPlateau();
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int horizontalOffset = tileWidth;
            int verticalOffset = (int) (tileHeight * 0.75);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int x = j*horizontalOffset - (i % 2 == 1 ? tileWidth / 2 : 0);
                    int y = i * verticalOffset;
                    int tileId = map[i][j].getTerrain();

                    int heightoffset = map[i][j].getHauteur();
                    heightoffset *= 50;

                    if (map[i][j].getTerrain() == Hexagone.VIDE) {
                        heightoffset -= 50;
                    }




                    BufferedImage tile = getTileImageFromId(tileId, map[i][j].getNum());
                    g.drawImage(tile, x , y - heightoffset, null);

                    if (map[i][j].getBatiment() == Hexagone.MAISON) {
                        tile = getTileImageFromId(Hexagone.MAISON, map[i][j].getNum());
                        g.drawImage(tile, x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_FORET) {
                        g.drawImage(templeJungle, x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PRAIRIE) {
                        g.drawImage(templePrairie, x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PIERRE) {
                        g.drawImage(templePierre, x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_SABLE) {
                        g.drawImage(templeSable, x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TOUR) {
                        g.drawImage(tour, x , y - heightoffset, null);
                    }

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
            int horizontalOffset = tileWidth;
            int verticalOffset = (int) (tileHeight * 0.75);

            Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                    (int) ((e.getY() - cameraOffset.y) / zoomFactor));

            // Convertir les coordonnées du système de pixels en coordonnées du système de grille
            int i = (int) (clickPositionAdjusted.y / verticalOffset);
            int j = (int) ((clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / horizontalOffset);

            hoveredTile_x = i;
            hoveredTile_y = j;
        }

        private void displayHoverTile(Graphics g) {
            if (hoverTile != null) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth(); // Important !!
                int horizontalOffset = tileWidth;
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = (int) (hoverTilePositionAdjusted.y / verticalOffset);
                int j = (int) ((hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / horizontalOffset);

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * horizontalOffset - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                int x2;
                if (i % 2 == 1) {
                    x2 = j - 1;
                } else {
                    x2 = j;
                }


                BufferedImage tile1 = getTileImageFromId(triplet[0][0],triplet[0][1]);
                BufferedImage tile2 = getTileImageFromId(triplet[1][0],triplet[1][1]);
                BufferedImage tile3 = getTileImageFromId(triplet[2][0],triplet[2][1]);

                float opacity = 1f;

                if (scrollValue == 1) {
                    if (!controleur.peutPlacerTuile(i, j, i - 1, x2, i - 1, x2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 2){
                    if (!controleur.peutPlacerTuile(i, j, i - 1, x2 + 1, i, j + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 3){
                    if (!controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, x2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 4){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, x2 + 1, i + 1, x2)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 5){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, x2, i, j - 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 6){
                    if (!controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, x2)) {
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

                int heightoffset1 = triplet[0][1];
                int heightoffset2 = triplet[1][1];
                int heightoffset3 = triplet[2][1];
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
                    g.drawImage(tile3, x +  + tileWidth/2, y + verticalOffset - heightoffset3, null);
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
                int horizontalOffset = tileWidth;
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = (int) (hoverTilePositionAdjusted.y / verticalOffset);
                int j = (int) ((hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / horizontalOffset);

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * horizontalOffset - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                float opacity = 0.5f; // Réduire l'opacité de moitié
                int heightoffset1 = triplet[0][1];
                heightoffset1 *= 30;

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


        private BufferedImage getReducedOpacityImage(BufferedImage originalImage, float opacity) {
            BufferedImage reducedOpacityImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = reducedOpacityImage.createGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            return reducedOpacityImage;
        }

        public void placerTuiles(int i, int j) {
            int x;
            if (i % 2 == 1) {
                x = j - 1;
            } else {
                x = j;
            }

            if (scrollValue == 1) {
                if (controleur.peutPlacerTuile(i, j, i - 1, x, i - 1, x + 1)) {
                    controleur.placeEtage(i, j, i - 1, x, triplet[1][0], i - 1, x + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 2){
                if (controleur.peutPlacerTuile(i, j, i - 1, x + 1, i, j + 1)) {
                    controleur.placeEtage(i, j, i - 1, x + 1, triplet[1][0], i, j + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 3){
                if (controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, x + 1)) {
                    controleur.placeEtage(i, j, i, j + 1, triplet[1][0], i + 1, x + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 4){
                if (controleur.peutPlacerTuile(i, j, i + 1, x + 1, i + 1, x)) {
                    controleur.placeEtage(i, j, i + 1, x + 1, triplet[1][0], i + 1, x, triplet[2][0]);
                }
            }
            else if (scrollValue == 5){
                if (controleur.peutPlacerTuile(i, j, i + 1, x, i, j - 1)) {
                    controleur.placeEtage(i, j, i + 1, x, triplet[1][0], i, j - 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 6){
                if (controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, x)) {
                    controleur.placeEtage(i, j, i, j - 1, triplet[1][0], i - 1, x, triplet[2][0]);
                }
            }

            miseAJour();
        }

        public void placerMaison(int i, int j){
            int x = hoveredTile_x;
            if (i % 2 == 1) {
                x = j - 1;
            } else {
                x = j;
            }
            j = hoveredTile_y;
            if (controleur.peutPlacerMaison(i, j)) {
                controleur.placeMaison(i, j);
            }
        }


        public void addToCursor(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth();
                int horizontalOffset = tileWidth;
                int verticalOffset = (int) (tileHeight * 0.75);

                Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                        (int) ((e.getY() - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = (int) (clickPositionAdjusted.y / verticalOffset);
                int j = (int) ((clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / horizontalOffset);

                Hexagone[][] map = jeu.getPlateau().getPlateau();

                if(poseTile) placerTuiles(i,j);
                else placerMaison(i,j);

                miseAJour();
            }
        }
    }
}