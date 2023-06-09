package Vue;

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

    public boolean estCurseurSurBouton_Jouer(MouseEvent e){
        if (panelMenu.estConfigPartie || panelMenu.clicCredits) {
            return false;
        }
        int largeur = panelMenu.posX_boutons + panelMenu.largeur_bouton;
        int hauteur = panelMenu.posY_Jouer + panelMenu.hauteur_bouton;
        if(e.getX() >= panelMenu.posX_boutons && e.getX() <= largeur && e.getY() >= panelMenu.posY_Jouer && e.getY() <= hauteur && !panelMenu.clicOptions &&!panelMenu.select_credits) {
            panelMenu.select_options = false;
            panelMenu.select_quitter = false;
            panelMenu.select_credits = false;
            panelMenu.select_jouer = true;
            PanelMenu.estEnChargement = false;
            this.type_jeu = "local";
            return true;
        }
        panelMenu.select_jouer = false;
        return false;
    }

    public boolean estCurseurSurBouton_Options(MouseEvent e){
        if (panelMenu.estConfigPartie || panelMenu.clicCredits) {
            return false;
        }
        int startx = panelMenu.posX_boutons;
        int starty = panelMenu.posY_Options;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions) {
            panelMenu.select_jouer = false;
            panelMenu.select_credits = false;
            panelMenu.select_quitter = false;
            panelMenu.select_options = true;
            return true;
        }
        panelMenu.select_options = false;
        return false;
    }

    public boolean estCurseurSurBouton_Credits(MouseEvent e){
        if (panelMenu.estConfigPartie || panelMenu.clicCredits) {
            return false;
        }
        int largeur = panelMenu.posX_boutons + panelMenu.largeur_bouton;
        int hauteur = panelMenu.posY_Credits + panelMenu.hauteur_bouton;
        if(e.getX() >= panelMenu.posX_boutons && e.getX() <= largeur && e.getY() >= panelMenu.posY_Credits && e.getY() <= hauteur && !panelMenu.clicOptions) {
            panelMenu.select_jouer = false;
            panelMenu.select_options = false;
            panelMenu.select_quitter = false;
            panelMenu.select_credits = true;
            return true;
        }
        panelMenu.select_credits = false;
        return false;
    }

    public boolean estCurseurSurBouton_QuitterCredits(MouseEvent e){
        if (panelMenu.estConfigPartie || panelMenu.clicOptions) {
            return false;
        }
        int largeur = panelMenu.posX_quitter_credits + panelMenu.largeur_quitter_credits;
        int hauteur = panelMenu.posY_quitter_credits + panelMenu.largeur_quitter_credits;
        if(e.getX() >= panelMenu.posX_quitter_credits && e.getX() <= largeur && e.getY() >= panelMenu.posY_quitter_credits && e.getY() <= hauteur) {
            panelMenu.select_quitter_credits = true;
            return true;
        }
        panelMenu.select_quitter_credits = false;
        return false;
    }

    public boolean estCurseurSurBouton_Quitter(MouseEvent e){
        if (panelMenu.estConfigPartie || panelMenu.clicCredits) {
            return false;
        }
        int startx = panelMenu.posX_boutons;
        int starty = panelMenu.posY_Quitter;
        if(e.getX() >= startx && e.getX() <= startx+ panelMenu.largeur_bouton && e.getY() >= starty && e.getY() <= starty+ panelMenu.hauteur_bouton && !panelMenu.clicOptions &&!panelMenu.select_credits) {
            panelMenu.select_options = false;
            panelMenu.select_jouer = false;
            panelMenu.select_credits = false;
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
    /*
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
*/
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

    public boolean estCurseurSurBoutonValider(MouseEvent e){//valider du menu paramètres
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
        int startx = panelMenu.posX_bouton_fermer;
        int starty = panelMenu.posY_bouton_fermer;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_fermer && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_fermer && !panelMenu.clicOptions) {
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonAddJoueur(MouseEvent e){
        if(!panelMenu.estConfigPartie || !panelMenu.peut_addJoueur) return false;
        int startx = panelMenu.posX_bouton_plus_joueur;
        int starty = panelMenu.posY_bouton_plus_joueur;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_plus && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_plus && !panelMenu.clicOptions) {
           panelMenu.select_addJoueur = true;
           return true;
        }
        panelMenu.select_addJoueur = false;
        return false;
    }

    public boolean estCurseurSurBoutonAddIA(MouseEvent e){
        if(!panelMenu.estConfigPartie || !panelMenu.peut_addIA) return false;
        int startx = panelMenu.posX_bouton_plus_ia;
        int starty = panelMenu.posY_bouton_plus_joueur;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_plus && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_plus && !panelMenu.clicOptions) {
            panelMenu.select_addIA = true;
            return true;
        }
        panelMenu.select_addIA = false;
        return false;
    }

    public boolean estCurseurSurBoutonMoins1(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.posX_bouton_moins;
        int starty = panelMenu.posY_bouton_moins;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_moins && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_moins && !panelMenu.clicOptions) {
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonMoins2(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.posX_bouton_moins;
        int starty = panelMenu.posY_bouton_moins+panelMenu.decalageY_couleur;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_moins && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_moins && !panelMenu.clicOptions) {
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonMoins3(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.posX_bouton_moins;
        int starty = panelMenu.posY_bouton_moins+2*panelMenu.decalageY_couleur;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_moins && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_moins && !panelMenu.clicOptions) {
            return true;
        }

        return false;
    }

    public boolean estCurseurSurBoutonMoins4(MouseEvent e){
        if (!panelMenu.estConfigPartie) {
            return false;
        }
        int startx = panelMenu.posX_bouton_moins;
        int starty = panelMenu.posY_bouton_moins+3*panelMenu.decalageY_couleur;
        if(e.getX() >= startx && e.getX() <= startx+panelMenu.largeur_bouton_moins && e.getY() >= starty && e.getY() <= starty+panelMenu.largeur_bouton_moins && !panelMenu.clicOptions) {
            return true;
        }
        return false;
    }

    public boolean estCurseurSurBoutonValiderConfig(MouseEvent e){
        if(!panelMenu.peut_valider) return false;
        int largeur = panelMenu.posX_bouton_valider + panelMenu.largeur_bouton_valider;
        int hauteur = panelMenu.posY_bouton_valider + panelMenu.largeur_bouton_valider;
        if(e.getX() >= panelMenu.posX_bouton_valider && e.getX() <= largeur && e.getY() >= panelMenu.posY_bouton_valider && e.getY() <= hauteur) {
            panelMenu.select_valider = true;
            return true;
        }
        panelMenu.select_valider = false;
        return false;
    }

    public void verif(MouseEvent e) throws IOException, CloneNotSupportedException {
        if(estCurseurSurBouton_Jouer(e)){
            panelMenu.nbJoueurs = 0;
            panelMenu.estConfigPartie = true;
            panelMenu.playSons(0);
        }
        if(estCurseurSurBouton_Options(e)){
            panelMenu.index_sonPanelAvant = panelMenu.index_sonPanel;
            panelMenu.index_musiquePanelAvant = panelMenu.index_musiquePanel;
            panelMenu.clicOptions = !panelMenu.clicOptions;
            panelMenu.playSons(0);
        }
        if(estCurseurSurBouton_Credits(e)){
            panelMenu.clicCredits = !panelMenu.clicCredits;
            panelMenu.peutAnimerCredits = true;
            panelMenu.playSons(0);
        }
        if(estCurseurSurBouton_Quitter(e)){
            panelMenu.playSons(0);
            System.exit(0);
        }
        if(estCurseurSurBouton_QuitterCredits(e)){
            panelMenu.clicCredits = !panelMenu.clicCredits;
            panelMenu.peutAnimerCredits = false;
            panelMenu.playSons(0);
        }
        // Options cochables
        if(estCurseurSurBoutonPleinEcran(e)){
            panelMenu.playSons(0);
            panelMenu.estPleinEcran = !panelMenu.estPleinEcran;
        }
        /*if(estCurseurSurBoutonDaltonien(e)){
            panelMenu.playSons(0);
            panelMenu.Daltonien = !panelMenu.Daltonien;
        }
        if(estCurseurSurBoutonExtension(e)) panelMenu.Extension = !panelMenu.Extension;*/
        // Options réglables
        if(estCurseurSurBoutonDroit_1(e) && !(panelMenu.index_sonPanel ==5)){
            panelMenu.playSons(0);
            panelMenu.index_sonPanel++;
        }
        if(estCurseurSurBoutonGauche_1(e) && !(panelMenu.index_sonPanel ==0)){
            panelMenu.playSons(0);
            panelMenu.index_sonPanel--;
        }
        if(estCurseurSurBoutonDroit_2(e) && !(panelMenu.index_musiquePanel ==5)){
            panelMenu.playSons(0);
            panelMenu.index_musiquePanel++;
        }
        if(estCurseurSurBoutonGauche_2(e) && !(panelMenu.index_musiquePanel ==0)){
            panelMenu.playSons(0);
            panelMenu.index_musiquePanel--;
        }
        // Choix Confirmer/Annuler
        if(estCurseurSurBoutonAnnuler(e)){
            panelMenu.index_sonPanel = panelMenu.index_sonPanelAvant;
            panelMenu.index_musiquePanel = panelMenu.index_musiquePanelAvant;
            panelMenu.playSons(0);
            panelMenu.clicOptions=false;
        }
        // TODO SAUVEGARDER LES PARAMETRES
        if(estCurseurSurBoutonValider(e)){
            panelMenu.playSons(2);
            setFullscreen();
            setVolume();
            PanelMenu.setParametre(panelMenu.index_musiquePanel,panelMenu.index_sonPanel,panelMenu.estPleinEcran);
            panelMenu.clicOptions = false;
        }

        // Creation de la partie
        if (estCurseurSurBoutonFermer(e)){
            panelMenu.playSons(0);
            panelMenu.estConfigPartie = false;
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);
            panelMenu.listeChrono.setVisible(false);
            panelMenu.listeDifficulte1.setVisible(false);
            panelMenu.listeDifficulte2.setVisible(false);
            panelMenu.listeDifficulte3.setVisible(false);
            panelMenu.listeDifficulte4.setVisible(false);
        }
        if(estCurseurSurBoutonAddJoueur(e)){
            panelMenu.playSons(0);
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
        }
        if(estCurseurSurBoutonAddIA(e)){
            panelMenu.playSons(0);
            panelMenu.nomJoueur1.setVisible(false);
            panelMenu.nomJoueur2.setVisible(false);
            panelMenu.nomJoueur3.setVisible(false);
            panelMenu.nomJoueur4.setVisible(false);
            if (panelMenu.nbJoueurs == 0) {
                panelMenu.nomJoueur1.setEnabled(false);
                panelMenu.nomJoueur1.setText("IA");
                panelMenu.nomJoueur1.setVisible(true);
                panelMenu.listeDifficulte1.setVisible(true);

            }
            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nomJoueur2.setEnabled(false);
                panelMenu.nomJoueur2.setText("IA");
                panelMenu.nomJoueur2.setVisible(true);
                panelMenu.listeDifficulte2.setVisible(true);

            }
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nomJoueur3.setEnabled(false);
                panelMenu.nomJoueur3.setText("IA");
                panelMenu.nomJoueur3.setVisible(true);
                panelMenu.listeDifficulte3.setVisible(true);
            }
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nomJoueur4.setEnabled(false);
                panelMenu.nomJoueur4.setText("IA");
                panelMenu.nomJoueur4.setVisible(true);
                panelMenu.listeDifficulte4.setVisible(true);
            }
            panelMenu.nbJoueurs = Math.min(panelMenu.nbJoueurs + 1, 4);
        };
        if(estCurseurSurBoutonMoins1(e)){
            panelMenu.playSons(0);
            if (panelMenu.nbJoueurs == 1) {
                panelMenu.nbJoueurs = 0;
            }
            panelMenu.listeDifficulte1.setVisible(false);
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
        }
        if(estCurseurSurBoutonMoins2(e)){
            panelMenu.playSons(0);
            if (panelMenu.nbJoueurs == 2) {
                panelMenu.nbJoueurs = 1;
            }
            panelMenu.listeDifficulte2.setVisible(false);

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
        }
        if(estCurseurSurBoutonMoins3(e)){
            panelMenu.playSons(0);
            if (panelMenu.nbJoueurs == 3) {
                panelMenu.nbJoueurs = 2;
            }
            panelMenu.listeDifficulte3.setVisible(false);
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
        }
        if(estCurseurSurBoutonMoins4(e)){
            panelMenu.playSons(0);
            if (panelMenu.nbJoueurs == 4) {
                panelMenu.nbJoueurs = 3;
            }
            panelMenu.listeDifficulte4.setVisible(false);

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
        }
        if(estCurseurSurBoutonValiderConfig(e)){
            panelMenu.playSons(2);
            if (panelMenu.nbJoueurs >= 2) {
                PanelMenu.estEnChargement = true;
            }else{
                panelMenu.afficheErreur = true;
            }
        }
    }

    public void setFullscreen(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if(panelMenu.estPleinEcran){
            panelMenu.fenetre.setSize(dim);
            panelMenu.setSize(dim);
            panelMenu.frame.setSize(dim);
            panelMenu.frame.setDefaultCloseOperation(panelMenu.frame.EXIT_ON_CLOSE);
            panelMenu.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(panelMenu.frame);
        }else{
            panelMenu.frame.setDefaultCloseOperation(panelMenu.frame.EXIT_ON_CLOSE);
            gd.setFullScreenWindow(null);
            panelMenu.frame.setExtendedState(Frame.NORMAL);
            panelMenu.fenetre.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
            panelMenu.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
            panelMenu.frame.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
            panelMenu.frame.setLocationRelativeTo(null);
        }
    }

    private void setVolume(){
        panelMenu.jeu.indexMusique = panelMenu.index_musiquePanel;
        panelMenu.jeu.indexSon = panelMenu.index_sonPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
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
        private boolean sonJoue = false;
        @Override
        public void mouseMoved(MouseEvent e) {
            if (estCurseurSurBouton_Jouer(e)||estCurseurSurBouton_Options(e)||estCurseurSurBouton_Credits(e)||estCurseurSurBouton_Quitter(e)||
                    estCurseurSurBouton_QuitterCredits(e)||estCurseurSurBoutonGauche_1(e)||estCurseurSurBoutonGauche_2(e)||estCurseurSurBoutonDroit_1(e)||estCurseurSurBoutonDroit_2(e)||
                    estCurseurSurBoutonPleinEcran(e)||estCurseurSurBoutonAnnuler(e) ||estCurseurSurBoutonValider(e) || estCurseurSurBoutonFermer(e) || estCurseurSurBoutonAddJoueur(e)||estCurseurSurBoutonAddIA(e)
                    ||estCurseurSurBoutonMoins1(e) || estCurseurSurBoutonMoins2(e) || estCurseurSurBoutonMoins3(e) || estCurseurSurBoutonMoins4(e) || estCurseurSurBoutonValiderConfig(e)) {
                if (!sonJoue) {
                    panelMenu.playSons(1);
                    sonJoue = true;
                }
                if(panelMenu!=null && panelMenu.fenetre!=null) panelMenu.fenetre.setHandCursor();
                //m.metAJour();
            }else{
                if (sonJoue) {
                    sonJoue = false;
                }
                if(panelMenu!=null && panelMenu.fenetre!=null) panelMenu.fenetre.setStandardCursor();
            }
        }
    }
}
