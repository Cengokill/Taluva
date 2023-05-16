package Vue;

import Modele.Jeu.MusicPlayer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

public class PanelMenuListener implements MouseListener  {

    final Cursor idle_cursor;
    final Cursor click_cursor;
    public PanelMenu panelMenu;
    public String type_jeu = "local";

    public PanelMenuListener(PanelMenu menu) {
        super();
        panelMenu = menu;
        DetectionSurvol survol = new DetectionSurvol();
        panelMenu.addMouseMotionListener(survol);
        //m.metAJour();

        Image cursorImage = Toolkit.getDefaultToolkit().getImage("ressources/Menu/normal_cursor.png");
        Image cursorImage2 = Toolkit.getDefaultToolkit().getImage("ressources/Menu/click_cursor.png");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotspot = new Point(0, 0); // D�finissez les coordonn�es du point d'ancrage du curseur si n�cessaire
        idle_cursor = toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        click_cursor = toolkit.createCustomCursor(cursorImage2, hotspot, "Custom Cursor");
    }

    public boolean estCurseurSurBouton_Local(MouseEvent e){
        int startx = panelMenu.posX_boutons-(panelMenu.largeur_background/9) ;
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions) {
            panelMenu.select_options = false;
            panelMenu.select_quitter = false;
            panelMenu.select_reseau = false;
            panelMenu.select_local = true;
            this.type_jeu = "local";
            return true;
        }
        panelMenu.select_local = false;
        return false;
    }

    public boolean estCurseurSurBouton_Reseau(MouseEvent e){
        int startx = panelMenu.posX_boutons+(panelMenu.largeur_background/9);
        int starty = 0;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions) {
            panelMenu.select_options = false;
            panelMenu.select_quitter = false;
            panelMenu.select_local = false;
            panelMenu.select_reseau = true;
            this.type_jeu = "reseau";
            return true;
        }
        panelMenu.select_reseau = false;
        return false;
    }

    public boolean estCurseurSurBouton_Options(MouseEvent e){
        int startx = panelMenu.posX_boutons;
        int starty = panelMenu.posY_Options;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions) {
            panelMenu.select_local = false;
            panelMenu.select_reseau = false;
            panelMenu.select_quitter = false;
            panelMenu.select_options = true;
            return true;
        }
        panelMenu.select_options = false;
        return false;
    }

    public boolean estCurseurSurBouton_Quitter(MouseEvent e){
        int startx = panelMenu.posX_boutons;
        int starty = panelMenu.posY_Quitter;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions) {
            panelMenu.select_options = false;
            panelMenu.select_local = false;
            panelMenu.select_reseau = false;
            panelMenu.select_quitter = true;
            return true;
        }
        panelMenu.select_quitter = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_1(MouseEvent e){
        int startx = panelMenu.posX_gauche1;
        int starty = panelMenu.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_gauche1 = true;
            return true;
        }
        panelMenu.select_gauche1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonGauche_2(MouseEvent e){
        int startx = panelMenu.posX_gauche2;
        int starty = panelMenu.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_gauche2 = true;
            return true;
        }
        panelMenu.select_gauche2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_1(MouseEvent e){
        int startx = panelMenu.posX_droit1;
        int starty = panelMenu.posY_slider1;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_droit1 = true;
            return true;
        }
        panelMenu.select_droit1 = false;
        return false;
    }

    public boolean estCurseurSurBoutonDroit_2(MouseEvent e){
        int startx = panelMenu.posX_droit2;
        int starty = panelMenu.posY_slider2;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_droit2 = true;
            return true;
        }
        panelMenu.select_droit2 = false;
        return false;
    }

    public boolean estCurseurSurBoutonPleinEcran(MouseEvent e){
        int startx = panelMenu.posX_coches;
        int starty = panelMenu.posY_coche1;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_PleinEcran = true;
            return true;
        }
        panelMenu.select_PleinEcran = false;
        return false;
    }

    public boolean estCurseurSurBoutonDaltonien(MouseEvent e){
        int startx = panelMenu.posX_coches;
        int starty = panelMenu.posY_coche2;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_Daltonien = true;
            return true;
        }
        panelMenu.select_Daltonien = false;
        return false;
    }

    public boolean estCurseurSurBoutonExtension(MouseEvent e){
        int startx = panelMenu.posX_coches;
        int starty = panelMenu.posY_coche3;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_Extension = true;
            return true;
        }
        panelMenu.select_Extension = false;
        return false;
    }

    public boolean estCurseurSurBoutonAnnuler(MouseEvent e){
        int startx = panelMenu.posX_btnAnnuler;
        int starty = panelMenu.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_annuler = true;
            return true;
        }
        panelMenu.select_annuler = false;
        return false;
    }

    public boolean estCurseurSurBoutonValider(MouseEvent e){
        int startx = panelMenu.posX_btnValider;
        int starty = panelMenu.posY_btnChoix;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.taille_btn && e.getY() >= starty && e.getY() <= starty+ panelMenu.taille_btn && panelMenu.clicOptions) {
            panelMenu.select_valider = true;
            return true;
        }
        panelMenu.select_valider = false;
        return false;
    }

    public void verif(MouseEvent e) throws IOException, CloneNotSupportedException {
        if(estCurseurSurBouton_Local(e) || estCurseurSurBouton_Reseau(e)){
            //efface tout le contenu de la frame
            panelMenu.layeredPane.removeAll();
            panelMenu.musicPlayer.stop();

            // On passe du menu au jeu
            ImageLoader.loadImages();
            panelMenu.fenetre.initRenduJeu();
            panelMenu.fenetre.panelPlateau.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());
            panelMenu.fenetre.panelVignette.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());
            panelMenu.fenetre.buttonPanel.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());

        }
        if(estCurseurSurBouton_Options(e)){
            panelMenu.clicOptions = !panelMenu.clicOptions;
        }
        if(estCurseurSurBouton_Quitter(e)){
            System.exit(0);
        }
        // Options cochables
        if(estCurseurSurBoutonPleinEcran(e)) panelMenu.estPleinEcran = !panelMenu.estPleinEcran;
        if(estCurseurSurBoutonDaltonien(e)) panelMenu.Daltonien = !panelMenu.Daltonien;
        if(estCurseurSurBoutonExtension(e)) panelMenu.Extension = !panelMenu.Extension;
        // Options réglables
        if(estCurseurSurBoutonDroit_1(e) && !(panelMenu.index_son==5)) panelMenu.index_son++;
        if(estCurseurSurBoutonGauche_1(e) && !(panelMenu.index_son==0)) panelMenu.index_son--;
        if(estCurseurSurBoutonDroit_2(e) && !(panelMenu.index_musique==5)) panelMenu.index_musique++;
        if(estCurseurSurBoutonGauche_2(e) && !(panelMenu.index_musique==0)) panelMenu.index_musique--;
        // Choix Confirmer/Annuler
        if(estCurseurSurBoutonAnnuler(e)) panelMenu.clicOptions=false;
        // TODO SAUVEGARDER LES PARAMETRES
        if(estCurseurSurBoutonValider(e)) panelMenu.clicOptions=false;
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
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(estCurseurSurBouton_Local(e)) System.out.println("local");
        if(estCurseurSurBouton_Reseau(e)) System.out.println("reseau");
        if(estCurseurSurBouton_Options(e)) System.out.println("options");
        if(estCurseurSurBouton_Quitter(e)) System.out.println("quitter");
        try {
            verif(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
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
                panelMenu.fenetre.setHandCursor();
                //m.metAJour();
            }else{
                panelMenu.fenetre.setStandardCursor();
            }
        }
    }
}
