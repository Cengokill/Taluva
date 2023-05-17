package Vue;

import Controleur.ControleurMediateur;
import Modele.Jeu.Jeu;
import Modele.Jeu.MusicPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Vue.ImageLoader.applyRedFilter;

public class PanelMenu extends JPanel {

    JFrame frame;
    JLayeredPane layeredPane;

    FenetreJeu fenetre;
    Jeu jeu;
    ControleurMediateur controleur;
    public MusicPlayer musicPlayer;

    final BufferedImage[] sliders = new BufferedImage[6];

    BufferedImage background,bouton_Local,bouton_Reseau,bouton_Options,bouton_Quitter,bouton_Local_hover,bouton_Reseau_hover,bouton_Options_hover,bouton_Quitter_hover,
            options_background,bouton_droit,bouton_gauche,btn_valider, btn_annuler,coche_non,coche_oui,bouton_droit_hover,bouton_gauche_hover,btn_valider_hover, btn_annuler_hover,coche_non_hover,coche_oui_hover
            ,ecriture_Sons,ecriture_Musiques,ecriture_PleinEcran,ecriture_Daltonien,ecriture_Extension;
    Dimension tailleEcran, tailleFenetre;
    int screenWidth, screenHeight, frameWidth, frameHeight,largeur_background,largeur_bouton,largeur_menu_options,hauteur_background,hauteur_bouton,hauteur_menu_options,index_son,index_musique;

    public int posX_boutons, posY_jcj, posY_jcia, posY_ia, posY_Options, posY_Local, posX_background, posY_background,posY_Reseau,
            posY_Quitter, posX_menu_options, posX_droit1, posX_droit2,posX_gauche1, posX_gauche2, posY_slider2,posY_slider1, taille_btn, posX_coches, posY_coche1,posY_coche2,posY_coche3,
            posX_btnAnnuler,posX_btnValider,posY_btnChoix;

    private JTextField field_joueur1;
    boolean select_local,select_reseau,select_options,select_quitter,clicOptions,select_gauche1,select_gauche2,select_droit1,select_droit2,select_PleinEcran,
            select_Daltonien,select_Extension, estPleinEcran,Daltonien,Extension, select_valider,select_annuler;

    public PanelMenu(JFrame f, JLayeredPane layeredPane, Jeu jeu, ControleurMediateur controleur) throws IOException {
        //Chargement des images
        background = lisImage("ocean");
        bouton_Local = lisImage("bouton_local");
        bouton_Reseau = lisImage("bouton_reseau");
        bouton_Options = lisImage("bouton_options");
        bouton_Quitter = lisImage("bouton_quitter");
        options_background = lisImage("/Options/Options_background");
        for(int i=0; i < sliders.length;i++){
            sliders[i] = lisImage("/Options/Sliders/slider_"+i);
        }
        bouton_droit = lisImage("/Options/boutons/btn_droit");
        bouton_gauche = lisImage("/Options/boutons/btn_gauche");
        btn_valider = lisImage("/Options/boutons/btn_valider");
        btn_annuler = lisImage("/Options/boutons/btn_annuler");
        coche_non = lisImage("/Options/boutons/coche_non");
        coche_oui = lisImage("/Options/boutons/coche_oui");
        ecriture_Sons = lisImage("/Options/Sons");
        ecriture_Musiques = lisImage("/Options/Musiques");
        ecriture_PleinEcran = lisImage("/Options/plein_ecran");
        ecriture_Daltonien = lisImage("/Options/Mode_daltonien");
        ecriture_Extension = lisImage("/Options/Activer_extension");

        // hover
        bouton_Local_hover = applyRedFilter(bouton_Local);
        bouton_Reseau_hover = applyRedFilter(bouton_Reseau);
        bouton_Options_hover = applyRedFilter(bouton_Options);
        bouton_Quitter_hover = applyRedFilter(bouton_Quitter);
        bouton_droit_hover = applyRedFilter(bouton_droit);
        bouton_gauche_hover = applyRedFilter(bouton_gauche);
        btn_valider_hover = applyRedFilter(btn_valider);
        btn_annuler_hover = applyRedFilter(btn_annuler);
        coche_non_hover = applyRedFilter(coche_non);
        coche_oui_hover = applyRedFilter(coche_oui);


        //Parametres p = new Parametres();
        //entier
        index_son = 3;
        index_musique = 3;
        //bool�ens
        select_local = false;
        select_options = false;
        select_quitter = false;
        select_reseau = false;
        select_Extension = false;
        select_Daltonien = false;
        select_PleinEcran = false;
        select_valider = false;
        select_annuler = false;
        clicOptions = false;

        // Eléments de l'interface
        frame = f;
        this.layeredPane = layeredPane;
        this.jeu = jeu;
        this.controleur = controleur;
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Définition des dimensions de la fenêtre
        tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth=tailleEcran.width;
        screenHeight=tailleEcran.height;
        tailleFenetre=frame.getSize();
        frameWidth=tailleFenetre.width;
        frameHeight=tailleFenetre.width;
        posX_menu_options = frameWidth;
        //musique
        musicPlayer = new MusicPlayer("Musiques\\Merchants_of_Novigrad.wav");
        musicPlayer.setVolume(-50.0f);
        musicPlayer.loop();
        //Ajout d'une interaction avec les boutons
        addMouseListener(new PanelMenuListener(this));
        boucle();//Timer
    }

    public void setFenetre(FenetreJeu fenetre) {
        this.fenetre = fenetre;
    }

    private BufferedImage lisImage(String nom) throws IOException {
        String CHEMIN = "ressources/Menu/";
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(CHEMIN + nom + ".png"));

        } catch (IOException e) {
            System.err.println("Impossible de charger l'image " + nom);
        }
        return image;
    }

    public void afficheBackground(Graphics g) {
        Image scaledImage = background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        g.setColor(new Color(64, 164, 223));
        g.fillRect(0, 0, frameWidth, frameHeight);
        double rapport = 0.5625;// rapport de 2160/3840
        double rapport_actuel = (double)frameHeight/(double)frameWidth;
        if(rapport_actuel>rapport) {// si la fen�tre est plus haute que large
            largeur_background=frameWidth;
            hauteur_background=(int)(largeur_background*rapport);
            posX_background=0;
            posY_background=(frameHeight-hauteur_background)/2;
        }
        else {
            hauteur_background=frameHeight;
            largeur_background=(int)(hauteur_background/rapport);
            posX_background=(frameWidth-largeur_background)/2;
            posY_background=0;
        }
        g.drawImage(scaledImage, 0, 0, null);
    }

    public void afficheBoutonLocal(Graphics g) {
        if(select_local) g.drawImage(bouton_Local_hover, posX_boutons-(largeur_background/9), posY_Local, largeur_bouton, hauteur_bouton, null);
        else g.drawImage(bouton_Local, posX_boutons-(largeur_background/9), posY_Local, largeur_bouton, hauteur_bouton, null);
    }
    public void afficheBoutonReseau(Graphics g) {
        if(select_reseau) g.drawImage(bouton_Reseau_hover, posX_boutons+(largeur_background/9), posY_Local, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Reseau, posX_boutons+(largeur_background/9), posY_Local, largeur_bouton, hauteur_bouton,null);
    }
    public void afficheBoutonOptions(Graphics g) {
        if(select_options) g.drawImage(bouton_Options_hover, posX_boutons, posY_Options, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Options, posX_boutons, posY_Options, largeur_bouton, hauteur_bouton,null);
    }

    public void afficheBoutonQuitter(Graphics g) {
        if(select_quitter) g.drawImage(bouton_Quitter_hover, posX_boutons, posY_Quitter, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Quitter, posX_boutons, posY_Quitter, largeur_bouton, hauteur_bouton,null);
    }

    private void afficheSliders(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);
        this.taille_btn = taille_btn;
        int taille_sons = (int) (Math.min(getWidth(),getHeight())*0.1);
        int taille_musiques = (int) (Math.min(getWidth(),getHeight())*0.2);

        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        posX_gauche1 = x;
        posX_droit1 = x+taille_slider_x-(taille_slider_x/11);
        posY_slider1 = y;
        g.drawImage(ecriture_Sons,(int) (posX_gauche1+(taille_slider_x/2.33)),(int) (posY_slider1*0.85),taille_sons,(int) (taille_sons/1.91),null);
        if(select_gauche1) g.drawImage(bouton_gauche_hover,posX_gauche1,posY_slider1,taille_btn,taille_btn,null);
        else g.drawImage(bouton_gauche,posX_gauche1,posY_slider1,taille_btn,taille_btn,null);
        g.drawImage(sliders[index_son],posX_gauche1,posY_slider1,taille_slider_x,taille_slider_y,null);
        if(select_droit1) g.drawImage(bouton_droit_hover,posX_droit1,posY_slider1,taille_btn,taille_btn,null);
        else g.drawImage(bouton_droit,posX_droit1,posY_slider1,taille_btn,taille_btn,null);

        posX_gauche2 = x;
        posX_droit2 = x+taille_slider_x-(taille_slider_x/11);
        posY_slider2 = y+taille_slider_y*2;
        g.drawImage(ecriture_Musiques,(int) (posX_gauche1+(taille_slider_x/2.66)),(int) (posY_slider2*0.9),taille_musiques,(int) (taille_musiques/2.64),null);
        if(select_gauche2) g.drawImage(bouton_gauche_hover,posX_gauche2,posY_slider2,taille_btn,taille_btn,null);
        else g.drawImage(bouton_gauche,posX_gauche2,posY_slider2,taille_btn,taille_btn,null);
        g.drawImage(sliders[index_musique],x,posY_slider2,taille_slider_x,taille_slider_y,null);
        if(select_droit2) g.drawImage(bouton_droit_hover,posX_droit2,posY_slider2,taille_btn,taille_btn,null);
        else g.drawImage(bouton_droit,posX_droit2,posY_slider2,taille_btn,taille_btn,null);
    }

    private void afficheCochable(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);
        int taille_pleinecran = (int) (Math.min(getWidth(),getHeight())*0.25);

        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        posX_coches = (int)(x+taille_slider_x*0.72);
        posY_coche1 = (int) (y+taille_slider_y*3.4);
        posY_coche2 = (int) (y+taille_slider_y*4.4);
        posY_coche3 = (int) (y+taille_slider_y*5.4);

        g.drawImage(ecriture_PleinEcran,(int) (posX_coches-taille_pleinecran*1.2),posY_coche1,taille_pleinecran,(int)(taille_pleinecran/4.2),null);
        if(estPleinEcran){
            if(select_PleinEcran) g.drawImage(coche_oui_hover,posX_coches,posY_coche1,taille_btn,taille_btn,null);
            else g.drawImage(coche_oui,posX_coches,posY_coche1,taille_btn,taille_btn,null);
        }else{
            if(select_PleinEcran) g.drawImage(coche_non_hover,posX_coches,posY_coche1,taille_btn,taille_btn,null);
            else g.drawImage(coche_non,posX_coches,posY_coche1,taille_btn,taille_btn,null);
        }

        g.drawImage(ecriture_Daltonien,(int) (posX_coches-taille_pleinecran*1.2),posY_coche2,taille_pleinecran,(int)(taille_pleinecran/4.2),null);
        if(Daltonien){
            if(select_Daltonien) g.drawImage(coche_oui_hover,posX_coches,posY_coche2,taille_btn,taille_btn,null);
            else g.drawImage(coche_oui,posX_coches,posY_coche2,taille_btn,taille_btn,null);
        }else{
            if(select_Daltonien) g.drawImage(coche_non_hover,posX_coches,posY_coche2,taille_btn,taille_btn,null);
            else g.drawImage(coche_non,posX_coches,posY_coche2,taille_btn,taille_btn,null);
        }
        g.drawImage(ecriture_Extension,(int) (posX_coches-taille_pleinecran*1.2),posY_coche3,taille_pleinecran,(int)(taille_pleinecran/4.2),null);
        if(Extension){
            if(select_Extension) g.drawImage(coche_oui_hover,posX_coches,posY_coche3,taille_btn,taille_btn,null);
            else g.drawImage(coche_oui,posX_coches,posY_coche3,taille_btn,taille_btn,null);
        }else{
            if(select_Extension) g.drawImage(coche_non_hover,posX_coches,posY_coche3,taille_btn,taille_btn,null);
            else g.drawImage(coche_non,posX_coches,posY_coche3,taille_btn,taille_btn,null);
        }
    }

    private void afficheChoix(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.12);


        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        posX_btnValider = (int)(x+taille_slider_x*1.05);
        posY_btnChoix = (int) (y+taille_slider_y*4.75);
        posX_btnAnnuler = x-(taille_slider_x)/6;

        if(select_annuler) g.drawImage(btn_annuler_hover,posX_btnAnnuler,posY_btnChoix,taille_btn,taille_btn,null);
        else g.drawImage(btn_annuler,posX_btnAnnuler,posY_btnChoix,taille_btn,taille_btn,null);
        if(select_valider) g.drawImage(btn_valider_hover,posX_btnValider,posY_btnChoix,taille_btn,taille_btn,null);
        else g.drawImage(btn_valider,posX_btnValider,posY_btnChoix,taille_btn,taille_btn,null);
    }

    public void afficheParametre(Graphics g){
        int taille_x = (int) (Math.min(getWidth(),getHeight())*1.3);
        int taille_y = (int) (taille_x/1.25);
        int x = (frameWidth - taille_x)/2;
        int y=0-(taille_y/45);
        g.drawImage(options_background, x, y, taille_x,taille_y,null);
        afficheSliders(g);
        afficheCochable(g);
        afficheChoix(g);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        calculeRapportsEtPositions();
        afficheBackground(g2d);
        if(clicOptions){
            afficheParametre(g2d);
        }else{
            afficheBoutonLocal(g2d);
            afficheBoutonReseau(g2d);
            afficheBoutonOptions(g2d);
            afficheBoutonQuitter(g2d);
        }
    }

    private void calculeRapportsEtPositions() {
        frameWidth=frame.getWidth();
        frameHeight=frame.getHeight();
        double rapport_bouton=1;//rapport de 86/522
        largeur_bouton=Math.min(largeur_background/7, frameWidth/7);
        hauteur_bouton=(int)(largeur_bouton*rapport_bouton);
        posX_boutons=posX_background+largeur_background/2-largeur_bouton/2;
        posY_Local=0;
        posY_Options=posY_Local+hauteur_background/3;
        posY_Quitter=posY_Options+hauteur_background/3;
        double rapport_menu_options = 1.0980140935297885970531710442024;//rapport de 1714/1561
        largeur_menu_options = hauteur_background/2;
        hauteur_menu_options = (int)(largeur_menu_options*rapport_menu_options);
    }

    public void boucle(){
        Timer timer = new Timer(2, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metAJour();
            }
        });
        timer.start();
    }

    public void metAJour() {
        repaint();
    }


}
