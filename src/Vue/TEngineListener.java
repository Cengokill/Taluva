package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.event.*;

public class TEngineListener extends MouseAdapter implements MouseWheelListener {
    private final TEngine tengine;

    public TEngineListener(TEngine t) {
        super();
        tengine = t;



        tengine.hexTiles.handler = new MouseHandler();
        tengine.hexTiles.addMouseListener(tengine.hexTiles.handler);
        tengine.hexTiles.addMouseMotionListener(tengine.hexTiles.handler);
        tengine.hexTiles.addMouseWheelListener(tengine.hexTiles.handler);

        tengine.hexTiles.keyboardlisten = new KeyboardListener();
        tengine.hexTiles.setFocusable(true);
        tengine.hexTiles.addKeyListener(tengine.hexTiles.keyboardlisten);
    }

     class KeyboardListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            // Code à exécuter lorsque la touche est enfoncée
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_E) {
                tengine.poseTile = !tengine.poseTile;
            }
            if (keyCode == KeyEvent.VK_CONTROL) {
                tengine.mode_plateau = !tengine.mode_plateau;
            }
            if (keyCode == KeyEvent.VK_A) {
                tengine.mode_plateau = !tengine.mode_plateau;
            }
            if (keyCode == KeyEvent.VK_Z) {
                tengine.mode_numero = !tengine.mode_numero;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                tengine.hexTiles.shake();
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                tengine.hexTiles.affichetripletpossible();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Code à exécuter lorsque la touche est relâchée
            //int keyCode = e.getKeyCode();

        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Code à exécuter lorsque la touche est tapée (après avoir été enfoncée et relâchée)
            /*
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_E) {

            }
             */
        }
    }

    ////////////////////////////
    // Mouse, drag and things //
    ////////////////////////////
    class MouseHandler extends MouseAdapter implements MouseWheelListener {
        Point lastPosition;
        @Override
        public void mouseClicked(MouseEvent e) {
            tengine.hexTiles.addToCursor(e);
            tengine.hexTiles.annuleConstruction(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                tengine.hexTiles.lastMousePosition = e.getPoint();
                lastPosition = e.getPoint();
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                tengine.hexTiles.lastMousePosition = e.getPoint();
                lastPosition = e.getPoint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                tengine.hexTiles.clicDroiteEnfonce = true;
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
            if (!SwingUtilities.isRightMouseButton(e)) {
                tengine.hexTiles.clicDroiteEnfonce = false;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!e.isControlDown()) {
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
                tengine.hexTiles.zoomFactor -= (wheelRotation * tengine.hexTiles.zoomIncrement) / 5;

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
