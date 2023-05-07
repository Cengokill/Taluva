package Vue;

import Modele.ImageLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

public class MenuGraphiqueListener implements MouseListener  {

    final Cursor idle_cursor;
    final Cursor click_cursor;
    public MenuGraphique menuGraphique;
    public String type_jeu = "local";

    public MenuGraphiqueListener(MenuGraphique menu) {
        super();
        menuGraphique = menu;
        DetectionSurvol survol = new DetectionSurvol();
        menuGraphique.addMouseMotionListener(survol);
        //m.metAJour();

        Image cursorImage = Toolkit.getDefaultToolkit().getImage("ressources/Menu/normal_cursor.png");
        Image cursorImage2 = Toolkit.getDefaultToolkit().getImage("ressources/Menu/click_cursor.png");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotspot = new Point(0, 0); // D�finissez les coordonn�es du point d'ancrage du curseur si n�cessaire
        idle_cursor = toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        click_cursor = toolkit.createCustomCursor(cursorImage2, hotspot, "Custom Cursor");
    }

    public boolean estCurseurSurBouton_Local(MouseEvent e){
        int startx = menuGraphique.posX_boutons-(menuGraphique.largeur_background/9) ;
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ menuGraphique.hauteur_bouton && !menuGraphique.clicOptions) {
            menuGraphique.select_local = true;
            this.type_jeu = "local";
            return true;
        }
        menuGraphique.select_local = false;
        return false;
    }

    public boolean estCurseurSurBouton_Reseau(MouseEvent e){
        int startx = menuGraphique.posX_boutons+(menuGraphique.largeur_background/9);
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ menuGraphique.hauteur_bouton && !menuGraphique.clicOptions) {
            menuGraphique.select_reseau = true;
            this.type_jeu = "reseau";
            return true;
        }
        menuGraphique.select_reseau = false;
        return false;
    }

    public boolean estCurseurSurBouton_Options(MouseEvent e){
        int startx = menuGraphique.posX_boutons;
        int starty = menuGraphique.posY_Options;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ menuGraphique.hauteur_bouton && !menuGraphique.clicOptions) {
            menuGraphique.select_options = true;
            return true;
        }
        menuGraphique.select_options = false;
        return false;
    }

    public boolean estCurseurSurBouton_Quitter(MouseEvent e){
        int startx = menuGraphique.posX_boutons;
        int starty = menuGraphique.posY_Quitter;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ menuGraphique.hauteur_bouton && !menuGraphique.clicOptions) {
            menuGraphique.select_quitter = true;
            return true;
        }
        menuGraphique.select_quitter = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_1(MouseEvent e){
        int startx = menuGraphique.posX_gauche1;
        int starty = menuGraphique.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_gauche1 = true;
            return true;
        }
        menuGraphique.select_gauche1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_2(MouseEvent e){
        int startx = menuGraphique.posX_gauche2;
        int starty = menuGraphique.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_gauche2 = true;
            return true;
        }
        menuGraphique.select_gauche2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_1(MouseEvent e){
        int startx = menuGraphique.posX_droit1;
        int starty = menuGraphique.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_droit1 = true;
            return true;
        }
        menuGraphique.select_droit1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_2(MouseEvent e){
        int startx = menuGraphique.posX_droit2;
        int starty = menuGraphique.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_droit2 = true;
            return true;
        }
        menuGraphique.select_droit2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonPleinEcran(MouseEvent e){
        int startx = menuGraphique.posX_coches;
        int starty = menuGraphique.posY_coche1;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_PleinEcran = true;
            return true;
        }
        menuGraphique.select_PleinEcran = false;
        return false;
    }

    public boolean estCurseurSurBoutonDaltonien(MouseEvent e){
        int startx = menuGraphique.posX_coches;
        int starty = menuGraphique.posY_coche2;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_Daltonien = true;
            return true;
        }
        menuGraphique.select_Daltonien = false;
        return false;
    }

    public boolean estCurseurSurBoutonExtension(MouseEvent e){
        int startx = menuGraphique.posX_coches;
        int starty = menuGraphique.posY_coche3;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_Extension = true;
            return true;
        }
        menuGraphique.select_Extension = false;
        return false;
    }

    public boolean estCurseurSurBoutonAnnuler(MouseEvent e){
        int startx = menuGraphique.posX_btnAnnuler;
        int starty = menuGraphique.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_annuler = true;
            return true;
        }
        menuGraphique.select_annuler = false;
        return false;
    }

    public boolean estCurseurSurBoutonValider(MouseEvent e){
        int startx = menuGraphique.posX_btnValider;
        int starty = menuGraphique.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ menuGraphique.taille_btn && e.getY() >= starty && e.getY() <= starty+ menuGraphique.taille_btn && menuGraphique.clicOptions) {
            menuGraphique.select_valider = true;
            return true;
        }
        menuGraphique.select_valider = false;
        return false;
    }

    public void verif(MouseEvent e) throws IOException {
        if(estCurseurSurBouton_Local(e) || estCurseurSurBouton_Reseau(e)){
            //efface tout le contenu de la frame
            menuGraphique.layeredPane.removeAll();

            // On passe du menu au jeu
            ImageLoader.loadImages();
            menuGraphique.fenetre.initRenduJeu();
            menuGraphique.fenetre.affichagePlateau.setBounds(0, 0, menuGraphique.getWidth(), menuGraphique.getHeight());
            menuGraphique.fenetre.vignettePanel.setBounds(0, 0, menuGraphique.getWidth(), menuGraphique.getHeight());
            menuGraphique.fenetre.buttonPanel.setBounds(0, 0, menuGraphique.getWidth(), menuGraphique.getHeight());

        }
        if(estCurseurSurBouton_Options(e)){
            menuGraphique.clicOptions = !menuGraphique.clicOptions;
        }
        if(estCurseurSurBouton_Quitter(e)){
            System.exit(0);
        }
        // Options cochables
        if(estCurseurSurBoutonPleinEcran(e)) menuGraphique.estPleinEcran = !menuGraphique.estPleinEcran;
        if(estCurseurSurBoutonDaltonien(e)) menuGraphique.Daltonien = !menuGraphique.Daltonien;
        if(estCurseurSurBoutonExtension(e)) menuGraphique.Extension = !menuGraphique.Extension;
        // Options r�glables
        if(estCurseurSurBoutonDroit_1(e) && !(menuGraphique.index_son==5)) menuGraphique.index_son++;
        if(estCurseurSurBoutonGauche_1(e) && !(menuGraphique.index_son==0)) menuGraphique.index_son--;
        if(estCurseurSurBoutonDroit_2(e) && !(menuGraphique.index_musique==5)) menuGraphique.index_musique++;
        if(estCurseurSurBoutonGauche_2(e) && !(menuGraphique.index_musique==0)) menuGraphique.index_musique--;
        // Choix Confirmer/Annuler
        if(estCurseurSurBoutonAnnuler(e)) menuGraphique.clicOptions=false;
        // TODO SAUVEGARDER LES PARAMETRES
        if(estCurseurSurBoutonValider(e)) menuGraphique.clicOptions=false;
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
                menuGraphique.setCursor((new Cursor(Cursor.HAND_CURSOR)));
                //m.metAJour();
            }else{
                menuGraphique.setCursor(idle_cursor);
            }
        }
    }
}
