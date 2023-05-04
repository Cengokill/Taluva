package Vue;

import Controleur.ControleurMediateur;
import Modele.ImageLoader;
import Modele.Jeu;
import Modele.Parametres;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MenuGraphiqueListener implements MouseListener  {

    Cursor idle_cursor;
    Cursor click_cursor;
    public MenuGraphique m;
    public String type_jeu = "local";

    public MenuGraphiqueListener(MenuGraphique menu) {
        super();
        m = menu;
        DetectionSurvol survol = new DetectionSurvol();
        m.addMouseMotionListener(survol);
        //m.metAJour();

        Image cursorImage = Toolkit.getDefaultToolkit().getImage("ressources/Menu/normal_cursor.png");
        Image cursorImage2 = Toolkit.getDefaultToolkit().getImage("ressources/Menu/click_cursor.png");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotspot = new Point(0, 0); // Définissez les coordonnées du point d'ancrage du curseur si nécessaire
        idle_cursor = toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        click_cursor = toolkit.createCustomCursor(cursorImage2, hotspot, "Custom Cursor");
    }

    public boolean estCurseurSurBouton_Local(MouseEvent e){
        int startx = m.posX_boutons-(m.largeur_background/9) ;
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton && !m.clicOptions) {
            m.select_local = true;
            this.type_jeu = "local";
            return true;
        }
        m.select_local = false;
        return false;
    }

    public boolean estCurseurSurBouton_Reseau(MouseEvent e){
        int startx = m.posX_boutons+(m.largeur_background/9);
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton && !m.clicOptions) {
            m.select_reseau = true;
            this.type_jeu = "reseau";
            return true;
        }
        m.select_reseau = false;
        return false;
    }

    public boolean estCurseurSurBouton_Options(MouseEvent e){
        int startx = m.posX_boutons;
        int starty = m.posY_Options;
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton && !m.clicOptions) {
            m.select_options = true;
            return true;
        }
        m.select_options = false;
        return false;
    }

    public boolean estCurseurSurBouton_Quitter(MouseEvent e){
        int startx = m.posX_boutons;
        int starty = m.posY_Quitter;
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton && !m.clicOptions) {
            m.select_quitter = true;
            return true;
        }
        m.select_quitter = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_1(MouseEvent e){
        int startx = m.posX_gauche1;
        int starty = m.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_gauche1 = true;
            return true;
        }
        m.select_gauche1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_2(MouseEvent e){
        int startx = m.posX_gauche2;
        int starty = m.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_gauche2 = true;
            return true;
        }
        m.select_gauche2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_1(MouseEvent e){
        int startx = m.posX_droit1;
        int starty = m.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_droit1 = true;
            return true;
        }
        m.select_droit1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_2(MouseEvent e){
        int startx = m.posX_droit2;
        int starty = m.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_droit2 = true;
            return true;
        }
        m.select_droit2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonPleinEcran(MouseEvent e){
        int startx = m.posX_coches;
        int starty = m.posY_coche1;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_PleinEcran = true;
            return true;
        }
        m.select_PleinEcran = false;
        return false;
    }

    public boolean estCurseurSurBoutonDaltonien(MouseEvent e){
        int startx = m.posX_coches;
        int starty = m.posY_coche2;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_Daltonien = true;
            return true;
        }
        m.select_Daltonien = false;
        return false;
    }

    public boolean estCurseurSurBoutonExtension(MouseEvent e){
        int startx = m.posX_coches;
        int starty = m.posY_coche3;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_Extension = true;
            return true;
        }
        m.select_Extension = false;
        return false;
    }

    public boolean estCurseurSurBoutonAnnuler(MouseEvent e){
        int startx = m.posX_btnAnnuler;
        int starty = m.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_annuler = true;
            return true;
        }
        m.select_annuler = false;
        return false;
    }

    public boolean estCurseurSurBoutonValider(MouseEvent e){
        int startx = m.posX_btnValider;
        int starty = m.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+m.taille_btn && e.getY() >= starty && e.getY() <= starty+m.taille_btn && m.clicOptions) {
            m.select_valider = true;
            return true;
        }
        m.select_valider = false;
        return false;
    }

    public void verif(MouseEvent e) throws IOException {
        if(estCurseurSurBouton_Local(e) || estCurseurSurBouton_Reseau(e)){
            //efface tout le contenu de la frame
            m.layeredPane.removeAll();

            // On passe du menu au jeu
            ImageLoader.loadImages();
            m.fenetre.initRenduJeu();
            m.fenetre.affichagePlateau.setBounds(0, 0, m.getWidth(), m.getHeight());
            m.fenetre.vignettePanel.setBounds(0, 0, m.getWidth(), m.getHeight());
            m.fenetre.buttonPanel.setBounds(0, 0, m.getWidth(), m.getHeight());

        }
        if(estCurseurSurBouton_Options(e)){
            if(m.clicOptions) {
                m.clicOptions = false;
            }else {
                m.clicOptions = true;
            }
        }
        if(estCurseurSurBouton_Quitter(e)){
            System.exit(0);
        }
        // Options cochables
        if(estCurseurSurBoutonPleinEcran(e)) m.pleinEcran = m.pleinEcran==false;
        if(estCurseurSurBoutonDaltonien(e)) m.Daltonien = m.Daltonien==false;
        if(estCurseurSurBoutonExtension(e)) m.Extension = m.Extension==false;
        // Options réglables
        if(estCurseurSurBoutonDroit_1(e) && !(m.index_son==5)) m.index_son++;
        if(estCurseurSurBoutonGauche_1(e) && !(m.index_son==0)) m.index_son--;
        if(estCurseurSurBoutonDroit_2(e) && !(m.index_musique==5)) m.index_musique++;
        if(estCurseurSurBoutonGauche_2(e) && !(m.index_musique==0)) m.index_musique--;
        // Choix Confirmer/Annuler
        if(estCurseurSurBoutonAnnuler(e)) m.clicOptions=false;
        // TODO SAUVEGARDER LES PARAMETRES
        if(estCurseurSurBoutonValider(e)) m.clicOptions=false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(estCurseurSurBouton_Local(e)) System.out.println("local");
        if(estCurseurSurBouton_Reseau(e)) System.out.println("reseau");
        if(estCurseurSurBouton_Options(e)) System.out.println("options");
        if(estCurseurSurBouton_Quitter(e)) System.out.println("quitter");
        try {
            verif(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public class DetectionSurvol extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (estCurseurSurBouton_Local(e)||estCurseurSurBouton_Reseau(e)||estCurseurSurBouton_Options(e)||estCurseurSurBouton_Quitter(e)||
                    estCurseurSurBoutonGauche_1(e)||estCurseurSurBoutonGauche_2(e)||estCurseurSurBoutonDroit_1(e)||estCurseurSurBoutonDroit_2(e)||
                    estCurseurSurBoutonPleinEcran(e)||estCurseurSurBoutonDaltonien(e)||estCurseurSurBoutonExtension(e)||estCurseurSurBoutonAnnuler(e)||estCurseurSurBoutonValider(e)) {
                m.setCursor((new Cursor(Cursor.HAND_CURSOR)));
                //m.metAJour();
            }else{
                m.setCursor(idle_cursor);
            }
        }
    }
}
