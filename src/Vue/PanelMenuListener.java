package Vue;

import Modele.Jeu.MusicPlayer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import static Vue.ImageLoader.*;

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
        if (panelMenu.estConfigPartie) {
            return false;
        }
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
        if (panelMenu.estConfigPartie) {
            return false;
        }
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
        if (panelMenu.estConfigPartie) {
            return false;
        }
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
        if (panelMenu.estConfigPartie) {
            return false;
        }
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
            panelMenu.select_gauche2 = false;
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
            panelMenu.select_gauche1 = false;
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
            panelMenu.select_droit2 = false;
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
            panelMenu.select_droit1 = false;
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
            panelMenu.select_Daltonien = false;
            panelMenu.select_Extension = false;
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
            panelMenu.select_PleinEcran = false;
            panelMenu.select_Extension = false;
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
            panelMenu.select_Daltonien = false;
            panelMenu.select_PleinEcran = false;
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

    public boolean estCurseurSurBoutonFermer(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 40;
        int starty = panelMenu.yConfigPanel + 700;
        if(e.getX() >= startx && e.getX() <= startx+ fermer.getWidth() && e.getY() >= starty && e.getY() <= starty+ fermer.getHeight() && !panelMenu.clicOptions) {
            panelMenu.estConfigPartie = false;
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonAddJoueur(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 220;
        int starty = panelMenu.yConfigPanel + 240;
        if(e.getX() >= startx && e.getX() <= startx+ plus.getWidth() && e.getY() >= starty && e.getY() <= starty+ plus.getHeight() && !panelMenu.clicOptions) {

            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);

            if (panelMenu.nbJoueurs == 0) {
                panelMenu.nomJoueur1.setEnabled(true);
                panelMenu.nomJoueur1.setText("");
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur2.setEnabled(true);
                panelMenu.nomJoueur2.setText("");
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur3.setEnabled(true);
                panelMenu.nomJoueur3.setText("");
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur4.setEnabled(true);
                panelMenu.nomJoueur4.setText("");
                panelMenu.nomJoueur4.setVisible(true);
            }


            panelMenu.nbJoueurs = Math.min(panelMenu.nbJoueurs + 1, 4);
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonAddIA(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 440;
        int starty = panelMenu.yConfigPanel + 240;
        if(e.getX() >= startx && e.getX() <= startx+ plus.getWidth() && e.getY() >= starty && e.getY() <= starty+ plus.getHeight() && !panelMenu.clicOptions) {
            System.out.println(e.getButton());

            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);

            if (panelMenu.nbJoueurs == 0) {
                panelMenu.nomJoueur1.setEnabled(false);
                panelMenu.nomJoueur1.setText("IA");
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur2.setEnabled(false);
                panelMenu.nomJoueur2.setText("IA");
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur3.setEnabled(false);
                panelMenu.nomJoueur3.setText("IA");
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur4.setEnabled(false);
                panelMenu.nomJoueur4.setText("IA");
                panelMenu.nomJoueur4.setVisible(true);
            }

            panelMenu.nbJoueurs = Math.min(panelMenu.nbJoueurs + 1, 4);
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonMoins1(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }

        int startx = panelMenu.xConfigPanel + 440 + 80;
        int starty = panelMenu.yConfigPanel + 360;
        if(e.getX() >= startx && e.getX() <= startx+ moins.getWidth() && e.getY() >= starty && e.getY() <= starty+ moins.getHeight() && !panelMenu.clicOptions) {
            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nbJoueurs = 0;
            }

            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);


            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nomJoueur4.setVisible(true);
            }
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonMoins2(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 440 + 80;
        int starty = panelMenu.yConfigPanel + 460;
        if(e.getX() >= startx && e.getX() <= startx+ moins.getWidth() && e.getY() >= starty && e.getY() <= starty+ moins.getHeight() && !panelMenu.clicOptions) {
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nbJoueurs = 1;
            }
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);


            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nomJoueur4.setVisible(true);
            }
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonMoins3(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 440 + 80;
        int starty = panelMenu.yConfigPanel + 560;
        if(e.getX() >= startx && e.getX() <= startx+ moins.getWidth() && e.getY() >= starty && e.getY() <= starty+ moins.getHeight() && !panelMenu.clicOptions) {
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nbJoueurs = 2;
            }
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);


            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nomJoueur4.setVisible(true);
            }
            return true;
        }

        return false;
    }

    public boolean estCurseurSurBoutonMoins4(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 440 + 80;
        int starty = panelMenu.yConfigPanel + 660;
        if(e.getX() >= startx && e.getX() <= startx+ moins.getWidth() && e.getY() >= starty && e.getY() <= starty+ moins.getHeight() && !panelMenu.clicOptions) {
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nbJoueurs = 3;
            }
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);


            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur1.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur2.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nomJoueur4.setVisible(true);
            }
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonValiderConfig(MouseEvent e)  throws CloneNotSupportedException{
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.xConfigPanel + 610;
        int starty = panelMenu.yConfigPanel + 700;
        if(e.getX() >= startx && e.getX() <= startx+ moins.getWidth() && e.getY() >= starty && e.getY() <= starty+ moins.getHeight() && !panelMenu.clicOptions) {
            if (panelMenu.nbJoueurs >= 2) {
                String nomJoueur1 = panelMenu.nomJoueur1.getText();
                String nomJoueur2 = panelMenu.nomJoueur2.getText();
                String nomJoueur3 = panelMenu.nomJoueur3.getText();
                String nomJoueur4 = panelMenu.nomJoueur4.getText();
                int nbJoueur = panelMenu.nbJoueurs;

                //efface tout le contenu de la frame
                panelMenu.layeredPane.removeAll();
                panelMenu.musicPlayer.stop();

                // On passe du menu au jeu
                ImageLoader.loadImages();
                panelMenu.fenetre.initRenduJeu(nomJoueur1, nomJoueur2, nomJoueur3, nomJoueur4, nbJoueur);
                panelMenu.fenetre.panelPlateau.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());
                panelMenu.fenetre.panelVignette.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());
                panelMenu.fenetre.buttonPanel.setBounds(0, 0, panelMenu.getWidth(), panelMenu.getHeight());
                panelMenu.jeu.initialiseMusique();
            }
            return true;
        }
        return false;
    }

    public void verif(MouseEvent e) throws IOException, CloneNotSupportedException {
        if(estCurseurSurBouton_Local(e) || estCurseurSurBouton_Reseau(e)){
            panelMenu.nbJoueurs = 0;
            panelMenu.estConfigPartie = true;
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
        if(estCurseurSurBoutonValider(e)){
            setFullscreen();
            setVolume();
            panelMenu.clicOptions = false;
        }

        estCurseurSurBoutonFermer(e);
        estCurseurSurBoutonAddJoueur(e);
        estCurseurSurBoutonAddIA(e);
        estCurseurSurBoutonMoins1(e);
        estCurseurSurBoutonMoins2(e);
        estCurseurSurBoutonMoins3(e);
        estCurseurSurBoutonMoins4(e);
        estCurseurSurBoutonValiderConfig(e);
    }

    private void setFullscreen(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if(panelMenu.estPleinEcran){
            panelMenu.fenetre.setSize(dim);
            panelMenu.setSize(dim);
            panelMenu.frame.setSize(dim);
            panelMenu.frame.setDefaultCloseOperation(panelMenu.frame.EXIT_ON_CLOSE);
            panelMenu.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }else{
            panelMenu.fenetre.setSize(panelMenu.tailleFenetre);
            panelMenu.setSize(panelMenu.tailleFenetre);
            panelMenu.frame.setSize(panelMenu.tailleFenetre);
            panelMenu.frame.setDefaultCloseOperation(panelMenu.frame.EXIT_ON_CLOSE);
            panelMenu.frame.setExtendedState(Frame.NORMAL);
            panelMenu.frame.setLocationRelativeTo(null);
        }
    }

    private void setVolume(){
        if(panelMenu.index_musique==0) panelMenu.jeu.volumeMusiques = -100000;
        else panelMenu.jeu.volumeMusiques = -(30)+(panelMenu.index_musique*17);
        if(panelMenu.index_musique==0) panelMenu.jeu.volumeSons = -100000;
        else panelMenu.jeu.volumeSons = -(30)+(panelMenu.index_musique*17);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
