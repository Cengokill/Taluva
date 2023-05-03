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
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton) {
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
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton) {
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
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton) {
            m.select_options = true;
            return true;
        }
        m.select_options = false;
        return false;
    }

    public boolean estCurseurSurBouton_Quitter(MouseEvent e){
        int startx = m.posX_boutons;
        int starty = m.posY_Quitter;
        if(e.getX() >= startx && e.getX() <= startx+m.largeur_bouton && e.getY() >= starty && e.getY() <= starty+m.hauteur_bouton) {
            m.select_quitter = true;
            return true;
        }
        m.select_quitter = false;
        return false;
    }


    public void verif(MouseEvent e) throws IOException {
        if(estCurseurSurBouton_Local(e) || estCurseurSurBouton_Reseau(e)){
            //efface tout le contenu de la frame
            m.frame.getContentPane().removeAll();
            //ajoute une FenetreJeu à la frame
            Jeu jeu = new Jeu(null);
            ControleurMediateur controler = new ControleurMediateur(jeu);
            FenetreJeu fenetre = new FenetreJeu(jeu, controler,0);
            ImageLoader.loadImages();
            fenetre.affichagePlateau.repaint();
            m.frame.setContentPane(fenetre);
        }
    /*if(estCurseurSurBouton_Options(e)){
        if(m.clicOptions) {
            m.clicOptions = false;
        }else {
            m.clicOptions = true;
        }
    }*/
        if(estCurseurSurBouton_Quitter(e)){
            System.exit(0);
        }
        //m.metAJour();
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
            if (estCurseurSurBouton_Local(e)||estCurseurSurBouton_Reseau(e)||estCurseurSurBouton_Options(e)||estCurseurSurBouton_Quitter(e)) {
                m.setCursor((new Cursor(Cursor.HAND_CURSOR)));
                //m.metAJour();
            }else{
                m.setCursor(idle_cursor);
            }
        }
    }
}
