package Vue;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import static Modele.Camera.*;
import static Modele.GameState.*;



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

     public class KeyboardListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            // Code à exécuter lorsque la touche est enfoncée
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_E) {
                poseTile = !poseTile;
            }
            if (keyCode == KeyEvent.VK_CONTROL) {
                mode_plateau = !mode_plateau;
            }
            if (keyCode == KeyEvent.VK_A) {
                mode_plateau = !mode_plateau;
            }
            if (keyCode == KeyEvent.VK_Z) {
                mode_numero = !mode_numero;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                shake();
                tengine.hexTiles.repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                tengine.hexTiles.affichetripletpossible();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                scrollValue++;
                if (scrollValue < 1) {
                    scrollValue = 6;
                } else if (scrollValue > 6) {
                    scrollValue = 1;
                }
                tengine.hexTiles.repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                scrollValue--;
                if (scrollValue < 1) {
                    scrollValue = 6;
                } else if (scrollValue > 6) {
                    scrollValue = 1;
                }
                tengine.hexTiles.repaint();
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
        }
    }

    ////////////////////////////
    // Mouse, drag and things //
    ////////////////////////////
    public class MouseHandler extends MouseAdapter implements MouseWheelListener {
        Point lastPosition;
        @Override
        public void mouseClicked(MouseEvent e) {
            tengine.hexTiles.addToCursor(e);
            tengine.hexTiles.annuleConstruction(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                lastMousePosition = e.getPoint();
                lastPosition = e.getPoint();
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                lastMousePosition = e.getPoint();
                lastPosition = e.getPoint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                clicDroiteEnfonce = true;
                int dx = e.getX() - lastMousePosition.x;
                int dy = e.getY() - lastMousePosition.y;

                // Ajouter les bornes pour les déplacements de la caméra
                int minX = -2500 - ((int)(10*zoomFactor) - 2)*(tengine.hexTiles.getWidth());
                int maxX = 10000;
                int minY = -2000 - ((int)(10*zoomFactor) - 2)*(tengine.hexTiles.getHeight());
                int maxY = 10000;


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
            tengine.hexTiles.miseAJour();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            hoverTilePosition = e.getPoint();
            tengine.hexTiles.updateCursorPosOnTiles(e);
            tengine.hexTiles.miseAJour();
            if (!SwingUtilities.isRightMouseButton(e)) {
                clicDroiteEnfonce = false;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!e.isControlDown()) {
                // Lorsque le bouton droit est enfoncé, modifiez la valeur de scrollValue
                scrollValue -= e.getWheelRotation();
                if (scrollValue < 1) {
                    scrollValue = 6;
                } else if (scrollValue > 6) {
                    scrollValue = 1;
                }
                tengine.hexTiles.repaint();
                //System.out.println("Scroll value: " + tengine.hexTiles.scrollValue);
            } else {
                int wheelRotation = e.getWheelRotation();
                double prevZoomFactor = zoomFactor;
                zoomFactor -= (wheelRotation * zoomIncrement) / 5;

                // Limiter le zoom minimum et maximum
                double minZoom = 0.2;
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
                tengine.hexTiles.miseAJour();
            }
        }

    }

}
