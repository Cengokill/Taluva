package Vue;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.io.*;

import static Vue.Camera.*;
import static Modele.Jeu.Plateau.EtatPlateau.*;
import static Vue.ImageLoader.*;


public class FenetreJeuListener extends MouseAdapter implements MouseWheelListener {
    private final FenetreJeu fenetreJeu;

    // TODO enlever bug de KILLIAN qui fait qu'on peut plus bouger la pièce avec les flèches et z marche plus
    public FenetreJeuListener(FenetreJeu fenetreJeu) {
        super();
        this.fenetreJeu = fenetreJeu;

        this.fenetreJeu.panelPlateau.mouseHandler = new MouseHandler();
        this.fenetreJeu.panelPlateau.addMouseListener(this.fenetreJeu.panelPlateau.mouseHandler);
        this.fenetreJeu.panelPlateau.addMouseMotionListener(this.fenetreJeu.panelPlateau.mouseHandler);
        this.fenetreJeu.panelPlateau.addMouseWheelListener(this.fenetreJeu.panelPlateau.mouseHandler);

        this.fenetreJeu.panelPlateau.keyboardListener = new KeyboardListener();
        this.fenetreJeu.panelPlateau.setFocusable(true);
        this.fenetreJeu.panelPlateau.addKeyListener(this.fenetreJeu.panelPlateau.keyboardListener);
        fenetreJeu.panelPlateau.requestFocusInWindow();

    }

     public class KeyboardListener extends KeyAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            // Code à exécuter lorsque la touche est enfoncée
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_E) {
                poseTile = !poseTile;
            }
            if (keyCode == KeyEvent.VK_CONTROL) {
                mode_plateau = false;
            }
            if (keyCode == KeyEvent.VK_A) {
                mode_plateau = !mode_plateau;
            }
            if (keyCode == KeyEvent.VK_Z) {
                mode_numero = !mode_numero;
            }
            if (keyCode == KeyEvent.VK_D) {
                fenetreJeu.jeu.debug = !fenetreJeu.jeu.debug;
            }
            if(keyCode == KeyEvent.VK_S){
                fenetreJeu.jeu.switchIAJoueur(fenetreJeu.jeu.getNumJoueurCourant());
            }
            //touche echap
            if(keyCode == KeyEvent.VK_ESCAPE){
                select_menu_options = !select_menu_options;
                fenetreJeu.afficheOptions = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                shake();
                fenetreJeu.panelPlateau.miseAJour();
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                fenetreJeu.panelPlateau.affichetripletpossible();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                scrollValue = Math.max(1,(scrollValue+1)%7);
                fenetreJeu.panelPlateau.miseAJour();
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if(scrollValue == 1) scrollValue = 6;
                else scrollValue--;
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

        public boolean estSurAnnuler(MouseEvent e) {//ok
            int largeur = posX_annuler + largeur_bouton;
            int hauteur = posY_annuler + largeur_bouton;
            if(e.getX() >= posX_annuler && e.getX() <= largeur && e.getY() >= posY_annuler && e.getY() <= hauteur){
                FenetreJeu.estSurBoutonAnnuler = true;
                return true;
            }
            FenetreJeu.estSurBoutonAnnuler = false;
            return false;
        }

        public boolean estSurRefaire(MouseEvent e) {//ok
            int largeur = posX_refaire + largeur_bouton;
            int hauteur = posY_refaire + largeur_bouton;
            if(e.getX() >= posX_refaire && e.getX() <= largeur && e.getY() >= posY_refaire && e.getY() <= hauteur){
                FenetreJeu.estSurBoutonRefaire = true;
                return true;
            }
            FenetreJeu.estSurBoutonRefaire = false;
            return false;
        }

        public boolean estSurQuitter(MouseEvent e) {
            if(!select_menu_options) return false;
            int largeur = (int)(posX_save*1.12) + largeur_bouton;
            int hauteur = posY_quitter + hauteur_bouton;
            if(e.getX() >= (int)(posX_save*1.12) && e.getX() <= largeur && e.getY() >= posY_quitter && e.getY() <= hauteur){
                select_quitter = true;
                return true;
            }
            select_quitter = false;
            return false;
        }

        public boolean estSurEchap(MouseEvent e) {//bouton d'options dans le jeu //ok
            int largeur = posX_options_echap + largeur_bouton;
            int hauteur = posY_options_echap + largeur_bouton;
            if(e.getX() >= posX_options_echap && e.getX() <= largeur && e.getY() >= posY_options_echap && e.getY() <= hauteur){
                FenetreJeu.estSurBoutonOptions = true;
                return true;
            }
            FenetreJeu.estSurBoutonOptions = false;
            return false;
        }

        public boolean estSurSauvegarder(MouseEvent e) {
            if (!select_menu_options) {
                return false;
            }
            int largeur = posX_save + largeur_bouton_dans_options;
            int hauteur = posY_save + hauteur_bouton_dans_options;
            if(e.getX() >= posX_save && e.getX() <= largeur && e.getY() >= posY_save && e.getY() <= hauteur){
                select_save = true;
                return true;
            }
            select_save = false;
            return false;
        }

        public boolean estSurCharger(MouseEvent e) {
            if (!select_menu_options) {
                return false;
            }
            int largeur = posX_save + largeur_bouton_dans_options;
            int hauteur = posY_load + largeur_bouton_dans_options;
            if(e.getX() >= posX_save && e.getX() <= largeur && e.getY() >= posY_load && e.getY() <= hauteur){
                select_load = true;
                return true;
            }
            select_load = false;
            return false;
        }

        public boolean estSurOption(MouseEvent e) {//menu principal
            if (!select_menu_options) {
                return false;
            }
            int largeur = posX_options_echap + largeur_bouton * 2;
            int hauteur = posY_options_echap + hauteur_bouton * 2;
            if(e.getX() >= posX_options_echap && e.getX() <= largeur && e.getY() >= posY_options_echap && e.getY() <= hauteur){
                select_options = true;
                select_save = false;
                return true;
            }
            select_load = false;
            return false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(fenetreJeu.afficheOptions){
                // PARAMETRES
                // Options cochables
                if(estCurseurSurBoutonPleinEcran(e)) fenetreJeu.estPleinEcran = !fenetreJeu.estPleinEcran;
                if(estCurseurSurBoutonDaltonien(e)) fenetreJeu.Daltonien = !fenetreJeu.Daltonien;
                if(estCurseurSurBoutonExtension(e)) fenetreJeu.Extension = !fenetreJeu.Extension;
                // Options réglables
                if(estCurseurSurBoutonDroit_1(e) && !(fenetreJeu.index_son==5)) fenetreJeu.index_son++;
                if(estCurseurSurBoutonGauche_1(e) && !(fenetreJeu.index_son==0)) fenetreJeu.index_son--;
                if(estCurseurSurBoutonDroit_2(e) && !(fenetreJeu.index_musique==5)) fenetreJeu.index_musique++;
                if(estCurseurSurBoutonGauche_2(e) && !(fenetreJeu.index_musique==0)) fenetreJeu.index_musique--;
                // Choix Confirmer/Annuler
                if(estCurseurSurBoutonAnnuler(e)) fenetreJeu.afficheOptions=false;
                // TODO SAUVEGARDER LES PARAMETRES
                if(estCurseurSurBoutonValider(e)){
                    setFullscreen();
                    setVolume();
                    PanelMenu.setParametre(fenetreJeu.index_musique,fenetreJeu.index_son,fenetreJeu.estPleinEcran);
                    fenetreJeu.afficheOptions = false;
                }
            }else{
                if (estSurTuto(e)) {
                    tuto_on = !tuto_on;
                }
                if (estSurAnnuler(e)) {
                    System.out.println("Annuler");
                    fenetreJeu.annuler();

                }
                if (estSurRefaire(e)) {
                    fenetreJeu.refaire();
                }
                if (estSurQuitter(e)) {
                    fenetreJeu.getJeu().musicPlayer.stop();
                    fenetreJeu.layeredPane.removeAll();
                    select_menu_options = !select_menu_options;

                    // On passe du menu au jeu
                    try {
                        fenetreJeu.initMenuJeu();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    fenetreJeu.revalidate();
                    fenetreJeu.metAJour();

                }
                fenetreJeu.panelPlateau.addToCursor(e);
                fenetreJeu.panelPlateau.annuleConstruction(e);

                if (estSurEchap(e)) {
                    select_menu_options = true;
                }
                if (estSurSauvegarder(e)) {
                    FenetreJeu.sauvegarder();
                }
                if (estSurCharger(e)) {
                    FenetreJeu.charger();
                }
                if (estSurOption(e)) {
                    PanelMenu.loadParametre();
                    fenetreJeu.afficheOptions = true;
                }
            }
        }
        private void setFullscreen(){
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            if(fenetreJeu.estPleinEcran){
                fenetreJeu.panelPlateau.setSize(dim);
                fenetreJeu.setSize(dim);
                fenetreJeu.frame.setSize(dim);
                fenetreJeu.frame.setDefaultCloseOperation(fenetreJeu.frame.EXIT_ON_CLOSE);
                fenetreJeu.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                gd.setFullScreenWindow(fenetreJeu.frame);
            }else{
                gd.setFullScreenWindow(null);
                fenetreJeu.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
                fenetreJeu.frame.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
                fenetreJeu.panelPlateau.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
                fenetreJeu.frame.setLocationRelativeTo(null);
            }
        }

        private void setVolume(){
            fenetreJeu.getJeu().indexMusique = fenetreJeu.index_musique;
            fenetreJeu.getJeu().indexSon = fenetreJeu.index_son;
            fenetreJeu.jeu.initialiseMusique();
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
                if (select_menu_options) {
                    return;
                }
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
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if(estSurAnnuler(e) || estSurRefaire(e) || estSurTuto(e) || estSurQuitter(e) || estSurEchap(e) || estSurSauvegarder(e) || estSurCharger(e) || estSurOption(e) || estCurseurSurBoutonGauche_1(e)||
                    estCurseurSurBoutonGauche_2(e)||estCurseurSurBoutonDroit_1(e)||estCurseurSurBoutonDroit_2(e)|| estCurseurSurBoutonPleinEcran(e)||estCurseurSurBoutonDaltonien(e)||estCurseurSurBoutonExtension(e)
                    ||estCurseurSurBoutonAnnuler(e)||estCurseurSurBoutonValider(e)) {
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
                if(e.getWheelRotation() == 1){
                    provenanceScroll = (byte)2;
                    scrollValue = Math.max(1,(scrollValue+1)%7);
                }else{
                    provenanceScroll = (byte)1;
                    if(scrollValue == 1) scrollValue = 6;
                    else scrollValue--;
                }
                fenetreJeu.panelPlateau.miseAJour();
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
                //fenetreJeu.panelPlateau.miseAJour();
            }
        }

    }

    //// PARAMETRES ////
    public boolean estCurseurSurBoutonGauche_1(MouseEvent e){
        int startx = fenetreJeu.posX_gauche1;
        int starty = fenetreJeu.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_gauche1 = true;
            fenetreJeu.select_gauche2 = false;
            return true;
        }
        fenetreJeu.select_gauche1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_2(MouseEvent e){
        int startx = fenetreJeu.posX_gauche2;
        int starty = fenetreJeu.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_gauche2 = true;
            fenetreJeu.select_gauche1 = false;
            return true;
        }
        fenetreJeu.select_gauche2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_1(MouseEvent e){
        int startx = fenetreJeu.posX_droit1;
        int starty = fenetreJeu.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_droit1 = true;
            fenetreJeu.select_droit2 = false;
            return true;
        }
        fenetreJeu.select_droit1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_2(MouseEvent e){
        int startx = fenetreJeu.posX_droit2;
        int starty = fenetreJeu.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_droit2 = true;
            fenetreJeu.select_droit1 = false;
            return true;
        }
        fenetreJeu.select_droit2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonPleinEcran(MouseEvent e){
        int startx = fenetreJeu.posX_coches;
        int starty = fenetreJeu.posY_coche1;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_PleinEcran = true;
            fenetreJeu.select_Daltonien = false;
            fenetreJeu.select_Extension = false;
            return true;
        }
        fenetreJeu.select_PleinEcran = false;
        return false;
    }

    public boolean estCurseurSurBoutonDaltonien(MouseEvent e){
        int startx = fenetreJeu.posX_coches;
        int starty = fenetreJeu.posY_coche2;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_Daltonien = true;
            fenetreJeu.select_PleinEcran = false;
            fenetreJeu.select_Extension = false;
            return true;
        }
        fenetreJeu.select_Daltonien = false;
        return false;
    }

    public boolean estCurseurSurBoutonExtension(MouseEvent e){
        int startx = fenetreJeu.posX_coches;
        int starty = fenetreJeu.posY_coche3;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_Extension = true;
            fenetreJeu.select_Daltonien = false;
            fenetreJeu.select_PleinEcran = false;
            return true;
        }
        fenetreJeu.select_Extension = false;
        return false;
    }

    public boolean estCurseurSurBoutonAnnuler(MouseEvent e){
        int startx = fenetreJeu.posX_btnAnnuler;
        int starty = fenetreJeu.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            //fenetreJeu.select_annuler2 = true;
            return true;
        }
        //fenetreJeu.select_annuler2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonValider(MouseEvent e){
        int startx = fenetreJeu.posX_btnValider;
        int starty = fenetreJeu.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ fenetreJeu.taille_btnParametre && e.getY() >= starty && e.getY() <= starty+ fenetreJeu.taille_btnParametre && fenetreJeu.afficheOptions) {
            fenetreJeu.select_valider = true;
            return true;
        }
        fenetreJeu.select_valider = false;
        return false;
    }


}
