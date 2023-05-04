package Vue;

import Controleur.ControleurMediateur;
import Modele.Jeu;
import Modele.Parametres;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Modele.ImageLoader.applyRedFilter;

public class MenuGraphique extends JPanel {

    JFrame frame;
    JLayeredPane layeredPane;

    FenetreJeu fenetre;
    Jeu jeu;
    ControleurMediateur controleur;

    BufferedImage[] sliders = new BufferedImage[5];
    BufferedImage background,bouton_Local,bouton_Reseau,bouton_Options,bouton_Quitter,bouton_Local_hover,bouton_Reseau_hover,bouton_Options_hover,bouton_Quitter_hover,
            options_background,bouton_droit,bouton_gauche,btn_valider, btn_annuler,coche_non,coche_oui;
    Dimension tailleEcran, tailleFenetre;
    int screenWidth, screenHeight, frameWidth, frameHeight,largeur_background,largeur_bouton,largeur_menu_options,hauteur_background,hauteur_bouton,hauteur_menu_options;

    public int posX_boutons, posY_jcj, posY_jcia, posY_ia, posY_Options, posY_Local, posX_background, posY_background,posY_Reseau,
            posY_Quitter, posX_menu_options;

    private JTextField field_joueur1;
    boolean select_local,select_reseau,select_options,select_quitter,clicOptions;

    public MenuGraphique(JFrame f, JLayeredPane layeredPane,Jeu jeu, ControleurMediateur controleur) throws IOException {
        //Chargement des images
        background = lisImage("ocean");
        bouton_Local = lisImage("bouton_local");
        bouton_Reseau = lisImage("bouton_reseau");
        bouton_Options = lisImage("bouton_options");
        bouton_Quitter = lisImage("bouton_quitter");
        options_background = lisImage("/Options/Options_background");
        for(int i=0; i<sliders.length;i++){
            sliders[i] = lisImage("/Options/Sliders/slider_"+i);
        }
        bouton_droit = lisImage("/Options/boutons/btn_droit");
        bouton_gauche = lisImage("/Options/boutons/btn_gauche");
        btn_valider = lisImage("/Options/boutons/btn_valider");
        btn_annuler = lisImage("/Options/boutons/btn_annuler");
        coche_non = lisImage("/Options/boutons/coche_non");
        coche_oui = lisImage("/Options/boutons/coche_oui");

        bouton_Local_hover = applyRedFilter(bouton_Local);
        bouton_Reseau_hover = applyRedFilter(bouton_Reseau);
        bouton_Options_hover = applyRedFilter(bouton_Options);
        bouton_Quitter_hover = applyRedFilter(bouton_Quitter);

        //Parametres p = new Parametres();

        //bool�ens
        select_local = false;
        select_options = false;
        select_quitter = false;
        select_reseau = false;
        clicOptions = false;

        // El�ments de l'interface
        frame = f;
        this.layeredPane = layeredPane;
        this.jeu = jeu;
        this.controleur = controleur;
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*frame.setSize(1280, 720);
        //centrer la fenetre
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);*/
        //D�finition des dimensions de la fen�tre
        tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth=tailleEcran.width;
        screenHeight=tailleEcran.height;
        tailleFenetre=frame.getSize();
        frameWidth=tailleFenetre.width;
        frameHeight=tailleFenetre.width;
        posX_menu_options = frameWidth;

        //Ajout d'une interaction avec les boutons
        addMouseListener(new MenuGraphiqueListener(this));

        boucle();
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

    private void afficheliders(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = (int) taille_slider_x/10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);


        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        g.drawImage(bouton_gauche,x,y,taille_btn,taille_btn,null);
        g.drawImage(sliders[4],x,y,taille_slider_x,taille_slider_y,null);
        g.drawImage(bouton_droit,x+taille_slider_x-(taille_slider_x/11),y,taille_btn,taille_btn,null);

        g.drawImage(bouton_gauche,x,y+taille_slider_y*2,taille_btn,taille_btn,null);
        g.drawImage(sliders[2],x,y+taille_slider_y*2,taille_slider_x,taille_slider_y,null);
        g.drawImage(bouton_droit,x+taille_slider_x-(taille_slider_x/11),y+taille_slider_y*2,taille_btn,taille_btn,null);
    }

    private void afficheCochable(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = (int) taille_slider_x/10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);


        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        g.drawImage(coche_non,(int)(x+taille_slider_x*0.72),(int) (y+taille_slider_y*3.4),taille_btn,taille_btn,null);
        g.drawImage(coche_oui,(int)(x+taille_slider_x*0.72),(int) (y+taille_slider_y*4.4),taille_btn,taille_btn,null);
        g.drawImage(coche_non,(int)(x+taille_slider_x*0.72),(int) (y+taille_slider_y*5.4),taille_btn,taille_btn,null);
    }

    private void afficheChoix(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = (int) taille_slider_x/10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.12);


        int x = (frameWidth - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        g.drawImage(btn_valider,(int)(x+taille_slider_x*1.05),(int) (y+taille_slider_y*4.75),taille_btn,taille_btn,null);
        g.drawImage(btn_annuler,(int)(x-(taille_slider_x)/6),(int) (y+taille_slider_y*4.75),taille_btn,taille_btn,null);
    }

    public void afficheParametre(Graphics g){
        int taille_x = (int) (Math.min(getWidth(),getHeight())*1.3);
        int taille_y = (int) (taille_x/1.25);
        int x = (frameWidth - taille_x)/2;
        int y=0-(taille_y/45);
        g.drawImage(options_background, x, y, taille_x,taille_y,null);
        afficheliders(g);
        afficheCochable(g);
        afficheChoix(g);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        frameWidth=frame.getWidth();
        frameHeight=frame.getHeight();
        double rapport_bouton=1;//rapport de 86/522
        largeur_bouton=Math.min(largeur_background/5, frameWidth/5);
        hauteur_bouton=(int)(largeur_bouton*rapport_bouton);
        posX_boutons=posX_background+largeur_background/2-largeur_bouton/2;
        posY_Local=0;
        posY_Options=posY_Local+hauteur_background/3;
        posY_Quitter=posY_Options+hauteur_background/3;
        double rapport_menu_options = 1.0980140935297885970531710442024;//rapport de 1714/1561
        largeur_menu_options = hauteur_background/2;
        hauteur_menu_options = (int)(largeur_menu_options*rapport_menu_options);

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
