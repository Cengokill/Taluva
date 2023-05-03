package Vue;

import Modele.Parametres;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static Modele.ImageLoader.applyRedFilter;

public class MenuGraphique extends JPanel {

    JFrame frame;
    BufferedImage background,bouton_Local,bouton_Reseau,bouton_Options,bouton_Quitter,bouton_Local_hover,bouton_Reseau_hover,bouton_Options_hover,bouton_Quitter_hover;
    Dimension tailleEcran, tailleFenetre;
    int screenWidth, screenHeight, frameWidth, frameHeight,largeur_background,largeur_bouton,largeur_menu_options,hauteur_background,hauteur_bouton,hauteur_menu_options;

    public int posX_boutons, posY_jcj, posY_jcia, posY_ia, posY_Options, posY_Local, posX_background, posY_background,posY_Reseau,
            posY_Quitter, posX_menu_options, posY_menu_options;

    private JTextField field_joueur1;
    boolean select_local,select_reseau,select_options,select_quitter;

    public MenuGraphique(JFrame f) throws IOException {
        //Chargement des images
        background = ImageIO.read(new File("Ressources/Menu/background.jpg"));
        bouton_Local = lisImage("bouton_local");
        bouton_Reseau = lisImage("bouton_reseau");
        bouton_Options = lisImage("bouton_options");
        bouton_Quitter = lisImage("bouton_quitter");

        bouton_Local_hover = applyRedFilter(bouton_Local);
        bouton_Reseau_hover = applyRedFilter(bouton_Reseau);
        bouton_Options_hover = applyRedFilter(bouton_Options);
        bouton_Quitter_hover = applyRedFilter(bouton_Quitter);

        //Parametres p = new Parametres();

        //booléens
        select_local = false;
        select_options = false;
        select_quitter = false;
        select_reseau = false;

        // Eléments de l'interface
        frame = f;
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        //centrer la fenetre
        frame.setLocationRelativeTo(null);
        //ajout des éléments à la frame
        field_joueur1 = new JTextField("j'aime la bite");
        //frame.add(field_joueur1);
        frame.setVisible(true);
        //Définition des dimensions de la fenêtre
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

    private BufferedImage lisImage(String nom) throws IOException {
        String CHEMIN = "ressources/Menu/";
        return ImageIO.read(new File(CHEMIN + nom + ".png"));
    }

    public void afficheBackground(Graphics g) {
        g.setColor(new Color(64, 164, 223));
        g.fillRect(0, 0, frameWidth, frameHeight);
        double rapport = 0.5625;// rapport de 2160/3840
        double rapport_actuel = (double)frameHeight/(double)frameWidth;
        if(rapport_actuel>rapport) {// si la fenêtre est plus haute que large
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
        g.drawImage(background, posX_background, posY_background, largeur_background, hauteur_background, null);
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
        afficheBoutonLocal(g2d);
        afficheBoutonReseau(g2d);
        afficheBoutonOptions(g2d);
        afficheBoutonQuitter(g2d);
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
