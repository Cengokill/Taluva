package Vue;

import Modele.Hexagone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import Modele.Hexagone;
import Modele.Plateau;
import Structures.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TEngineListener extends MouseAdapter implements MouseWheelListener {
    private TEngine tengine;

    public TEngineListener(TEngine t) {
        super();
        tengine = t;



        tengine.hexTiles.handler = new MouseHandler();
        tengine.hexTiles.addMouseListener(tengine.hexTiles.handler);
        tengine.hexTiles.addMouseMotionListener(tengine.hexTiles.handler);
        tengine.hexTiles.addMouseWheelListener(tengine.hexTiles.handler);
    }


    ////////////////////////////
    // Mouse, drag and things //
    ////////////////////////////
    class MouseHandler extends MouseAdapter implements MouseWheelListener {
        @Override
        public void mouseClicked(MouseEvent e) {

            tengine.hexTiles.addToCursor(e);
        }


        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON2) {
                tengine.hexTiles.lastMousePosition = e.getPoint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isMiddleMouseButton(e)) {
                int dx = e.getX() - tengine.hexTiles.lastMousePosition.x;
                int dy = e.getY() - tengine.hexTiles.lastMousePosition.y;

                // Ajouter les bornes pour les déplacements de la caméra
                int minX = -2500 - ((int)(10*tengine.hexTiles.zoomFactor) - 2)*(tengine.hexTiles.getWidth());
                int maxX = 10000;
                int minY = -2000 - ((int)(10*tengine.hexTiles.zoomFactor) - 2)*(tengine.hexTiles.getHeight());
                int maxY = 10000;



                    //System.out.println(minY);
                    //System.out.println(tengine.hexTiles.cameraOffset.y);
                    //System.out.println(tengine.hexTiles.zoomFactor);

                tengine.hexTiles.cameraOffset.x = Math.min(Math.max(tengine.hexTiles.cameraOffset.x + dx, minX), maxX);
                tengine.hexTiles.cameraOffset.y = Math.min(Math.max(tengine.hexTiles.cameraOffset.y + dy, minY), maxY);

                // Empêcher la caméra de voir des cases dans le négatif
                if (tengine.hexTiles.cameraOffset.x > 0) {
                    tengine.hexTiles.cameraOffset.x = 0;
                }
                if (tengine.hexTiles.cameraOffset.y > -64) {
                    tengine.hexTiles.cameraOffset.y = -64;
                }

                tengine.hexTiles.lastMousePosition = e.getPoint();
            } else {
                tengine.hexTiles.hoverTilePosition = e.getPoint();
            }
            tengine.hexTiles.miseAJour();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            tengine.hexTiles.hoverTilePosition = e.getPoint();
            tengine.hexTiles.updateCursorPosOnTiles(e);
            tengine.hexTiles.miseAJour();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                // Lorsque le bouton droit est enfoncé, modifiez la valeur de scrollValue
                tengine.hexTiles.scrollValue -= e.getWheelRotation();
                if (tengine.hexTiles.scrollValue < 1) {
                    tengine.hexTiles.scrollValue = 6;
                } else if (tengine.hexTiles.scrollValue > 6) {
                    tengine.hexTiles.scrollValue = 1;
                }
                tengine.hexTiles.repaint();
                //System.out.println("Scroll value: " + tengine.hexTiles.scrollValue);
            } else {
                int wheelRotation = e.getWheelRotation();
                double prevZoomFactor = tengine.hexTiles.zoomFactor;
                tengine.hexTiles.zoomFactor -= (wheelRotation * tengine.hexTiles.zoomIncrement)/5;

                // Limiter le zoom minimum et maximum
                double minZoom = 0.2;
                double maxZoom = 2.0;
                tengine.hexTiles.zoomFactor = Math.max(Math.min(tengine.hexTiles.zoomFactor, maxZoom), minZoom);

                // Ajuster l'offset de la caméra en fonction du zoom pour centrer le zoom sur la position de la souris
                tengine.hexTiles.cameraOffset.x -= (e.getX() - tengine.hexTiles.cameraOffset.x) * (tengine.hexTiles.zoomFactor - prevZoomFactor);
                tengine.hexTiles.cameraOffset.y -= (e.getY() - tengine.hexTiles.cameraOffset.y) * (tengine.hexTiles.zoomFactor - prevZoomFactor);

                // Empêcher la caméra de voir des cases dans le négatif
                if (tengine.hexTiles.cameraOffset.x > 0) {
                    tengine.hexTiles.cameraOffset.x = 0;
                }
                if (tengine.hexTiles.cameraOffset.y > -64) {
                    tengine.hexTiles.cameraOffset.y = -64;
                }
                tengine.hexTiles.miseAJour();
            }
        }
    }

}
