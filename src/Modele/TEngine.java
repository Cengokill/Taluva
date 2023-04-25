package Modele;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TEngine extends JFrame {
    int width = 400;
    int height = 200;
    int tile_size = 148;


    public TEngine() {
        setTitle("Ma Fenêtre en Java Swing");
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ajouter les tuiles hexagonales
        HexagonalTiles hexTiles = new HexagonalTiles();
        getContentPane().add(hexTiles);

        // Définir la couleur d'arrière-plan en bleu océan
        getContentPane().setBackground(new Color(64, 164, 223));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TEngine fenetre = new TEngine();
                fenetre.setVisible(true);
            }
        });
    }

    class HexagonalTiles extends JPanel {
        BufferedImage waterTile;
        BufferedImage hoverTile;
        BufferedImage voidTile;
        BufferedImage grassTile;
        Point hoverTilePosition = new Point(-tile_size, -tile_size);
        Point cameraOffset = new Point(0, 0);
        Point lastMousePosition;
        double zoomFactor = 1.0;
        double zoomIncrement = 0.1;

        private Plateau plateau;


        public HexagonalTiles() {
            try {
                waterTile = ImageIO.read(new File("ressources/Water_Tile.png"));
                voidTile = ImageIO.read(new File("ressources/Void_Tile.png"));
                grassTile = ImageIO.read(new File("ressources/Grass_Tile.png"));
                hoverTile = ImageIO.read(new File("ressources/Hover_Tile.png"));
            } catch (IOException e) {
                System.out.println("Erreur lors de l'affichage des tiles");
                e.printStackTrace();
            }
            setOpaque(false);
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
            addMouseWheelListener(handler);

            cameraOffset.x = -750;
            cameraOffset.y = -750;

            plateau = new Plateau();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(cameraOffset.x, cameraOffset.y);
            g2d.scale(zoomFactor, zoomFactor);

            displayHexagonMap(g);
            displayHoverTile(g);
        }


        /////////////////
        // 0 = VOID    //
        // 1 = Grass   //
        /////////////////
        private void displayIntMap(int[][] map, Graphics g) {
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getHeight();
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
            Hexagone[][] map = plateau.plateau;
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getHeight();
            int horizontalOffset = tileWidth;
            int verticalOffset = (int) (tileHeight * 0.75);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int x = j*horizontalOffset - (i % 2 == 1 ? tileWidth / 2 : 0);
                    int y = i * verticalOffset;
                    int tileId = map[i][j].getTypeTion();
                    System.out.println(tileId);
                    BufferedImage tile = getTileImageFromId(tileId);
                    g.drawImage(tile, x , y, null);
                }
            }
        }

        private BufferedImage getTileImageFromId(int id) {
            if (id == 0) {
                return voidTile;
            }
            if (id == 1) {
                return grassTile;
            }
            return null;
        }

        private void displayHoverTile(Graphics g) {
            if (hoverTile != null) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getHeight();
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

                g.drawImage(hoverTile, x , y, null);
            }
        }

        private void addToCursor(MouseEvent e, int tile_type) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getHeight();
                int horizontalOffset = tileWidth;
                int verticalOffset = (int) (tileHeight * 0.75);

                Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                        (int) ((e.getY() - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = (int) (clickPositionAdjusted.y / verticalOffset);
                int j = (int) ((clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / horizontalOffset);


                Hexagone[][] map = plateau.plateau;

                // S'assurer que les indices i et j sont à l'intérieur des limites de la matrice 'map'
                if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
                    map[i][j] = new Hexagone(0, 0, 0, tile_type);
                    repaint();
                }
            }
        }

        ////////////////////////////
        // Mouse, drag and things //
        ////////////////////////////
        class MouseHandler extends MouseAdapter implements MouseWheelListener {

            @Override
            public void mouseClicked(MouseEvent e) {

                addToCursor(e, Hexagone.GRASS);
            }


            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    lastMousePosition = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;

                    // Ajouter les bornes pour les déplacements de la caméra
                    int minX = -1500 - ((int)(10*zoomFactor) - 6)*(getWidth()/4);
                    int maxX = 1000;
                    int minY = -1500 - ((int)(10*zoomFactor) - 6)*(getHeight()/3);
                    int maxY = 1000;

                    /*
                    System.out.println(height);
                    System.out.println(width);
                    System.out.println(minY);
                    System.out.println(cameraOffset.y);
                    System.out.println(zoomFactor);
                    */
                    cameraOffset.x = Math.min(Math.max(cameraOffset.x + dx, minX), maxX);
                    cameraOffset.y = Math.min(Math.max(cameraOffset.y + dy, minY), maxY);

                    // Empêcher la caméra de voir des cases dans le négatif
                    if (cameraOffset.x > 0) {
                        cameraOffset.x = 0;
                    }
                    if (cameraOffset.y > -64) {
                        cameraOffset.y = -64;
                    }

                    lastMousePosition = e.getPoint();
                    repaint();
                } else {
                    hoverTilePosition = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                hoverTilePosition = e.getPoint();
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelRotation = e.getWheelRotation();
                double prevZoomFactor = zoomFactor;
                zoomFactor -= wheelRotation * zoomIncrement;

                // Limiter le zoom minimum et maximum
                double minZoom = 0.6;
                double maxZoom = 2.0;
                zoomFactor = Math.max(Math.min(zoomFactor, maxZoom), minZoom);

                // Ajuster l'offset de la caméra en fonction du zoom pour centrer le zoom sur la position de la souris
                cameraOffset.x -= (e.getX() - cameraOffset.x) * (zoomFactor - prevZoomFactor);
                cameraOffset.y -= (e.getY() - cameraOffset.y) * (zoomFactor - prevZoomFactor);

                // Empêcher la caméra de voir des cases dans le négatif
                if (cameraOffset.x > 0) {
                    cameraOffset.x = 0;
                }
                if (cameraOffset.y > -64) {
                    cameraOffset.y = -64;
                }

                repaint();
            }

        }
    }
}
