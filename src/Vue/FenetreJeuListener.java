package Vue;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.io.IOException;

import static Modele.Jeu.Plateau.Affichage.Camera.*;
import static Modele.Jeu.Plateau.EtatPlateau.*;
import static Modele.Jeu.Plateau.Affichage.ImageLoader.*;


public class FenetreJeuListener extends MouseAdapter implements MouseWheelListener {
    private final FenetreJeu fenetreJeu;

    public FenetreJeuListener(FenetreJeu t) {
        super();
        fenetreJeu = t;

        fenetreJeu.panelPlateau.handler = new MouseHandler();
        fenetreJeu.panelPlateau.addMouseListener(fenetreJeu.panelPlateau.handler);
        fenetreJeu.panelPlateau.addMouseMotionListener(fenetreJeu.panelPlateau.handler);
        fenetreJeu.panelPlateau.addMouseWheelListener(fenetreJeu.panelPlateau.handler);

        fenetreJeu.panelPlateau.keyboardlisten = new KeyboardListener();
        fenetreJeu.panelPlateau.setFocusable(true);
        fenetreJeu.panelPlateau.addKeyListener(fenetreJeu.panelPlateau.keyboardlisten);
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
                fenetreJeu.panelPlateau.miseAJour();
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                fenetreJeu.panelPlateau.affichetripletpossible();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                scrollValue++;
                if (scrollValue < 1) {
                    scrollValue = 6;
                } else if (scrollValue > 6) {
                    scrollValue = 1;
                }
                fenetreJeu.panelPlateau.miseAJour();
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                scrollValue--;
                if (scrollValue < 1) {
                    scrollValue = 6;
                } else if (scrollValue > 6) {
                    scrollValue = 1;
                }
                fenetreJeu.panelPlateau.miseAJour();
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

        public boolean estSurTuto(MouseEvent e) {
            int largeur = posX_boutons + largeur_bouton;
            int hauteur = posY_tuto + hauteur_bouton;
            return e.getX() >= posX_boutons && e.getX() <= largeur && e.getY() >= posY_tuto && e.getY() <= hauteur;
        }

        public boolean estSurAnnuler(MouseEvent e) {
            int largeur = posX_boutons + largeur_bouton;
            int hauteur = posY_annuler+ hauteur_bouton;
            if(e.getX() >= posX_boutons && e.getX() <= largeur && e.getY() >= posY_annuler && e.getY() <= hauteur){
                select_annuler = true;
                return true;
            }
            select_annuler = false;
            return false;
        }

        public boolean estSurRefaire(MouseEvent e) {
            int largeur = posX_boutons + largeur_bouton;
            int hauteur = posY_refaire+ hauteur_bouton;
            if(e.getX() >= posX_boutons && e.getX() <= largeur && e.getY() >= posY_refaire && e.getY() <= hauteur){
                select_refaire = true;
                return true;
            }
            select_refaire = false;
            return false;
        }

        public boolean estSurQuitter(MouseEvent e) {
            int largeur = posX_boutons + largeur_bouton;
            int hauteur = posY_quitter + hauteur_bouton;
            if(e.getX() >= posX_boutons && e.getX() <= largeur && e.getY() >= posY_quitter && e.getY() <= hauteur){
                select_quitter = true;
                return true;
            }
            select_quitter = false;
            return false;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if(estSurTuto(e)) {
                tuto_on = !tuto_on;
            }
            if(estSurAnnuler(e)) {
                System.out.println("Annuler");
                fenetreJeu.panelPlateau.controleur.annuler();
            }
            if(estSurRefaire(e)) {
                fenetreJeu.panelPlateau.controleur.refaire();
            }
            if(estSurQuitter(e)){
                fenetreJeu.layeredPane.removeAll();
                // On passe du menu au jeu
                try {
                    fenetreJeu.initMenuJeu();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                fenetreJeu.revalidate();
                //fenetreJeu.panelMenu.setBounds();
                fenetreJeu.panelMenu.metAJour();

            }
            fenetreJeu.panelPlateau.addToCursor(e);
            fenetreJeu.panelPlateau.annuleConstruction(e);
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
                int minX = -5300;
                int maxX = 10000;
                int minY = -(int)(5300*0.75);
                int maxY = 10000;


                cameraOffset.x = Math.min(Math.max(cameraOffset.x + dx, minX), maxX);
                cameraOffset.y = Math.min(Math.max(cameraOffset.y + dy, minY), maxY);

                // Empêcher la caméra de voir des cases dans le négatif
                if (cameraOffset.x > 100) {
                    cameraOffset.x = 100;
                }
                if (cameraOffset.y > 100) {
                    cameraOffset.y = 100;
                }
                lastMousePosition = e.getPoint();
            } else {
                hoverTilePosition = e.getPoint();
            }
            //fenetreJeu.panelPlateau.miseAJour();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if(estSurAnnuler(e) || estSurRefaire(e) || estSurTuto(e) || estSurQuitter(e)) {
                fenetreJeu.setHandCursor();
            }else{
                fenetreJeu.setStandardCursor();
            }
            hoverTilePosition = e.getPoint();
            fenetreJeu.panelPlateau.updateCursorPosOnTiles(e);
            //fenetreJeu.panelPlateau.miseAJour();
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
                fenetreJeu.panelPlateau.repaint();
            } else {
                int wheelRotation = e.getWheelRotation();
                double prevZoomFactor = zoomFactor;
                //zoomFactor -= (wheelRotation * zoomIncrement) / 5;

                // Limiter le zoom minimum et maximum
                //double minZoom = 0.2;
                //double maxZoom = 2.0;
                //zoomFactor = Math.max(Math.min(zoomFactor, maxZoom), minZoom);

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
                //fenetreJeu.panelPlateau.miseAJour();
            }
        }

    }

}