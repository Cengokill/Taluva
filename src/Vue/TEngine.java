package Vue;

import Controleur.ControleurMediateur;
import Modele.Hexagone;
import Modele.Plateau;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class TEngine extends JFrame {
    int width = 400;
    int height = 200;
    int tile_size = 148;
    TEngineListener listener;
    HexagonalTiles hexTiles;

    ControleurMediateur controleur;
    public TEngine(ControleurMediateur controleur) {
        this.controleur = controleur;
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
    }


    class HexagonalTiles extends JPanel {
        BufferedImage waterTile;
        BufferedImage hoverTile;
        BufferedImage voidTile;
        BufferedImage grassTile;
        BufferedImage volcanTile;
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

        int[][] triplet = new int[3][2]; // [n° tile] [0: tile_type] [1: tile_height]

        TEngineListener.MouseHandler handler;

        ControleurMediateur controleur;

        public HexagonalTiles(TEngine t, ControleurMediateur controleur) {
            this.tengine = t;
            this.controleur = controleur;
            waterTile = lisImageBuf("Water_Tile");
            voidTile = lisImageBuf("Void_Tile");
            grassTile = lisImageBuf("Grass_Tile");
            hoverTile = lisImageBuf("Hover_Tile");
            volcanTile = lisImageBuf("Water_Tile");
            boutonAnnuler = lisImage("annuler");
            setOpaque(false);

            cameraOffset.x = -2100;
            cameraOffset.y = -1700;

            triplet[0][0] = Hexagone.VOLCAN;
            triplet[1][0] = Hexagone.GRASS;
            triplet[2][0] = Hexagone.GRASS;

            triplet[0][1] = 0;
            triplet[1][1] = 0;
            triplet[2][1] = 0;

            plateau = new Plateau();

            largeur = tengine.getWidth();
            hauteur = tengine.getHeight();
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
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(cameraOffset.x, cameraOffset.y);
            g2d.scale(zoomFactor, zoomFactor);

            //définit la taille des boutons
            double rapport_bouton = (double) 207/603;
            largeur_bouton = (int) Math.min(largeur*.22, hauteur*.22);
            hauteur_bouton = (int) (largeur_bouton*rapport_bouton);

            displayHexagonMap(g);
            displayHoverTile(g);
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
                    BufferedImage tile = getTileImageFromId(tileId);
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
                    heightoffset *= 30;

                    //System.out.println(tileId);
                    BufferedImage tile = getTileImageFromId(tileId);


                    g.drawImage(tile, x , y - heightoffset, null);
                }
            }
        }

        private BufferedImage getTileImageFromId(int id) {
            if (id == Hexagone.VIDE) {
                return voidTile;
            }
            if (id == Hexagone.GRASS) {
                return grassTile;
            }
            if (id == Hexagone.VOLCAN) {
                return volcanTile;
            }
            if (id == Hexagone.WATER) {
                return waterTile;
            }
            return null;
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

                float opacity = 0.5f; // Réduire l'opacité de moitié
                BufferedImage tile1 = getTileImageFromId(triplet[0][0]);
                BufferedImage tile2 = getTileImageFromId(triplet[1][0]);
                BufferedImage tile3 = getTileImageFromId(triplet[2][0]);
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

        private BufferedImage getReducedOpacityImage(BufferedImage originalImage, float opacity) {
            BufferedImage reducedOpacityImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = reducedOpacityImage.createGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            return reducedOpacityImage;
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


                Hexagone[][] map = plateau.getPlateau();

                // S'assurer que les indices i et j sont à l'intérieur des limites de la matrice 'map'
                if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {

                    int x;
                    if (i % 2 == 1) {
                        x = j - 1;
                    } else {
                        x = j;
                    }

                    if (scrollValue == 1) {
                        if (controleur.peutPlacerEtage(i, j, i - 1, x, i - 1, x + 1)) {
                            controleur.placeEtage(i, j, i - 1, x, triplet[1][0], i - 1, x + 1, triplet[2][0]);
                        }
                    }
                    else if (scrollValue == 2){
                        if (controleur.peutPlacerEtage(i, j, i - 1, x + 1, i, j + 1)) {
                            controleur.placeEtage(i, j, i - 1, x + 1, triplet[1][0], i, j + 1, triplet[2][0]);
                        }
                    }
                    else if (scrollValue == 3){
                        if (controleur.peutPlacerEtage(i, j, i, j + 1, i + 1, x + 1)) {
                            controleur.placeEtage(i, j, i, j + 1, triplet[1][0], i + 1, x + 1, triplet[2][0]);
                        }
                    }
                    else if (scrollValue == 4){
                        if (controleur.peutPlacerEtage(i, j, i + 1, x + 1, i + 1, x)) {
                            controleur.placeEtage(i, j, i + 1, x + 1, triplet[1][0], i + 1, x, triplet[2][0]);
                        }
                    }
                    else if (scrollValue == 5){
                        if (controleur.peutPlacerEtage(i, j, i + 1, x, i, j - 1)) {
                            controleur.placeEtage(i, j, i + 1, x, triplet[1][0], i, j - 1, triplet[2][0]);
                        }
                    }
                    else if (scrollValue == 6){
                        if (controleur.peutPlacerEtage(i, j, i, j - 1, i - 1, x)) {
                            controleur.placeEtage(i, j, i, j - 1, triplet[1][0], i - 1, x, triplet[2][0]);
                        }
                    }

                    miseAJour();
                }
            }
        }
    }
}