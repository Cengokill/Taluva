package Vue;

import Modele.Hexagone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class TEngineListener extends MouseAdapter implements MouseWheelListener {
    private TEngine tengine;

    public TEngineListener(TEngine t) {
        super();
        tengine = t;
    }

    private void addToCursor(MouseEvent e, int tile_type) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int tileWidth = tengine.voidTile.getWidth();
            int tileHeight = tengine.voidTile.getHeight();
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
                map[i][j] = new Hexagone(0, 0, 0, tile_type);
                miseAJour();
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

        tengine.addToCursor(e, Hexagone.GRASS);
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
        } else {
            hoverTilePosition = e.getPoint();
        }
        miseAJour();
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
