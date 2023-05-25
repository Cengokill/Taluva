package Vue;

import Controleur.ControleurMediateur;
import Modele.Jeu.Jeu;
import Modele.Jeu.MusicPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import static Vue.FenetreJeu.customFont;
import static Vue.ImageLoader.*;

public class PanelMenu extends JPanel {
    JFrame frame;
    JLayeredPane layeredPane;
    FenetreJeu fenetre;
    public Jeu jeu;
    ControleurMediateur controleur;
    public MusicPlayer musicPlayer;
    final BufferedImage[] sliders = new BufferedImage[6];
    BufferedImage background, taluvaTitre, credits_background, credits1, credits2, credits3, credits4, credits5, credits6, credits7;
    BufferedImage bouton_Jouer, bouton_Jouer_select, bouton_Local_clic;
    BufferedImage bouton_Options, bouton_Options_select, bouton_Options_clic;
    BufferedImage bouton_Credits, bouton_Credits_select, bouton_Credits_clic;
    BufferedImage bouton_Quitter, bouton_Quitter_select, bouton_Quitter_clic;
    BufferedImage options_background;
    BufferedImage bouton_droit, bouton_gauche;
    BufferedImage btn_valider, btn_annuler;
    BufferedImage fermer, fermer_select;
    BufferedImage coche_non, coche_oui;
    BufferedImage bouton_droit_hover, bouton_gauche_hover;
    BufferedImage btn_valider_hover, btn_annuler_hover;
    BufferedImage coche_non_hover, coche_oui_hover;
    BufferedImage ecriture_Sons;
    BufferedImage ecriture_Musiques;
    BufferedImage ecriture_PleinEcran;
    BufferedImage ecriture_Daltonien;
    BufferedImage ecriture_Extension;
    BufferedImage nuage1, nuage2;
    BufferedImage chargement;
    Dimension tailleEcran, tailleFenetre;
    int screenWidth, screenHeight;
    int largeur, hauteur;
    int largeur_background, hauteur_background;
    int largeur_bouton;
    int largeur_menu_options;
    int hauteur_bouton, hauteur_menu_options, largeur_credits1, hauteur_credits1;
    int posX_credits1, posY_credits1, posX_credits2, posY_credits2, posX_credits3, posY_credits3, posX_credits4, posY_credits4, posX_credits5, posY_credits5,
            posX_credits6, posY_credits6, posX_credits7, posY_credits7;
    int posX_quitter_credits, posY_quitter_credits, largeur_quitter_credits;
    int largeur_nuage1, hauteur_nuage1, posX_nuage1, posY_nuage1, largeur_nuage2, hauteur_nuage2, posX_nuage2, posY_nuage2, decalage_nuage1, decalage_nuage2;
    int largeur_taluvaTitre, hauteur_taluvaTitre, posX_taluvaTitre, posY_taluvaTitre;
    public static int index_sonPanel;
    public int index_sonPanelAvant;
    public static int index_musiquePanel,index_musiquePanelAvant;
    public int posX_boutons, posY_Options;
    public int posY_Jouer;
    public int posX_background, posY_background;
    public int posY_Credits;
    public int posY_Quitter;
    public int posX_menu_options;
    public int posX_droit1, posX_droit2,posX_gauche1, posX_gauche2, posY_slider2, posY_slider1;
    public int taille_btn;
    public int posX_coches, posY_coche1, posY_coche2, posY_coche3;
    public int posX_btnAnnuler;
    public int posX_btnValider;
    public int posY_btnChoix;
    public int posX_bouton_plus_joueur, posY_bouton_plus_joueur, posYChronoList, posXChronoList, posXDifficulteList, posYDifficulteList,  posX_bouton_plus_ia, posX_bouton_moins,
            posY_bouton_moins, posX_cadre, posY_cadre, posX_textJoueur, posY_textJoueur, posX_bouton_valider, posY_bouton_valider, posX_bouton_fermer, posY_bouton_fermer,
    hauteur_textJoueur, largeur_bouton_plus, largeur_bouton_moins, largeur_cadre, largeur_bouton_valider, largeur_bouton_fermer, largeur_textJoueur, largeur_textDifficulte,
            decalageY_couleur;
    private int timerValue;
    private JTextField field_joueur1;
    boolean select_jouer;
    boolean select_credits;
    boolean select_options;
    boolean select_quitter;
    boolean select_quitter_credits;
    boolean clicOptions, clicCredits;
    boolean select_gauche1;
    boolean select_gauche2;
    boolean select_droit1;
    boolean select_droit2;
    boolean select_PleinEcran;
    boolean select_Daltonien;
    boolean select_Extension, chargeParametre;
    public static boolean estPleinEcran;
    boolean Daltonien;
    boolean Extension;
    boolean select_valider, clic_valider, peut_valider;
    boolean select_annuler;
    boolean afficheErreur,peutJouerSon;
    boolean peutAnimerNuage = true, peutAnimerCredits = false;
    boolean select_addJoueur, select_addIA, peut_addIA = true, peut_addJoueur = true;
    public static boolean estEnChargement = false, aAfficheChargement = false;
    static boolean estConfigPartie = false;
    int xConfigPanel, yConfigPanel;
    JTextField nomJoueur1, nomJoueur2, nomJoueur3, nomJoueur4;
    String[] choixChrono = { "Infini", "15 sec", "30 sec", "1 min" };
    String[] choixDifficulte = { "Facile", "Intermediaire", "Difficile"};
    JComboBox<String> listeChrono = new JComboBox<>(choixChrono);
    JComboBox<String> listeDifficulte1 = new JComboBox<>(choixDifficulte);
    JComboBox<String> listeDifficulte2 = new JComboBox<>(choixDifficulte);
    JComboBox<String> listeDifficulte3 = new JComboBox<>(choixDifficulte);
    JComboBox<String> listeDifficulte4 = new JComboBox<>(choixDifficulte);
    public ArrayList<MusicPlayer> sonPlayer = new ArrayList<>();

    int nbJoueurs = 0;
    double rapport_bouton = 179.0/692.0;
    double rapport_menu_options = 1.0980140935297885970531710442024;//rapport de 1714/1561
    double rapport_background = 0.5625;
    double rapport_nuage1 = 400.0/1100.0;
    double rapport_nuage2 = 400.0/1200.0;
    double rapport_taluvaTitre = 500.0/900.0;
    double rapport_credits1 = 196.0/772.0;

    public PanelMenu(JFrame f, JLayeredPane layeredPane, Jeu jeu, ControleurMediateur controleur) throws IOException {
        //Chargement des images
        nuage1 = lisImage("nuage1");
        nuage2 = lisImage("nuage2");
        taluvaTitre = lisImage("taluva_titre");
        background = lisImage("taluva_background");
        credits1 = lisImage("credits1");
        credits2 = lisImage("credits2");
        credits3 = lisImage("credits3");
        credits4 = lisImage("credits4");
        credits5 = lisImage("credits5");
        credits6 = lisImage("credits6");
        credits7 = lisImage("credits7");
        credits_background = lisImage("credits_background");
        bouton_Jouer = lisImage("jouer");
        bouton_Jouer_select = lisImage("jouer_select");
        bouton_Local_clic = lisImage("jouer_clic");
        bouton_Options = lisImage("parametres");
        bouton_Options_select = lisImage("parametres_select");
        bouton_Options_clic = lisImage("parametres_clic");
        bouton_Credits = lisImage("credits");
        bouton_Credits_select = lisImage("credits_select");
        bouton_Credits_clic = lisImage("credits_clic");
        bouton_Quitter = lisImage("quitter");
        bouton_Quitter_select = lisImage("quitter_select");
        bouton_Quitter_clic = lisImage("quitter_clic");
        options_background = lisImage("/Options/Options_background");

        for(int i=0; i < sliders.length;i++){
            sliders[i] = lisImage("/Options/Sliders/slider_"+i);
        }
        fermer = lisImage("fermer");
        fermer_select = lisImage("fermer_select");
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
        configPartieBackground = lisImageBuf("background_choix_partie");
        plus = lisImageBuf("plus");
        plus_select = lisImageBuf("plus_select");
        moins = lisImageBuf("moins");
        moins_select = lisImageBuf("moins_select");
        rouge = lisImageBuf("rouge");
        vert = lisImageBuf("vert");
        bleu = lisImageBuf("bleu");
        violet = lisImageBuf("violet");

        valider = lisImageBuf("valider");
        valider_select = lisImageBuf("valider_select");
        valider_gris = lisImageBuf("valider_gris");
        valider_clic = lisImageBuf("valider_presse");
        fermer = lisImageBuf("fermer");

        chargement = lisImageBuf("chargement");

        // si le curseur est sur les boutons
        bouton_droit_hover = applyRedFilter(bouton_droit);
        bouton_gauche_hover = applyRedFilter(bouton_gauche);
        btn_valider_hover = applyRedFilter(btn_valider);
        btn_annuler_hover = applyRedFilter(btn_annuler);
        coche_non_hover = applyRedFilter(coche_non);
        coche_oui_hover = applyRedFilter(coche_oui);


        //Parametres p = new Parametres();
        //entier
        loadParametre();

        timerValue=0;
        //booléens
        select_jouer = false;
        select_options = false;
        select_quitter = false;
        select_credits = false;
        select_Extension = false;
        select_Daltonien = false;
        select_PleinEcran = false;
        select_valider = false;
        select_annuler = false;
        clicOptions = false;
        clicCredits = false;
        afficheErreur = false;
        chargeParametre = true;
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
        largeur =tailleFenetre.width;
        hauteur =tailleFenetre.width;
        posX_menu_options = largeur;

        listeChrono.setVisible(true);
        listeChrono.setBackground(Color.WHITE);
        //listeChrono.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        Font font = customFont.deriveFont(Font.BOLD,20);
        listeChrono.setFont(font);
        layeredPane.add(listeChrono, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        listeDifficulte1.setVisible(false);
        listeDifficulte1.setBackground(Color.WHITE);
        //listeDifficulte1.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        listeDifficulte1.setFont(font);
        layeredPane.add(listeDifficulte1, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        listeDifficulte2.setVisible(false);
        listeDifficulte2.setBackground(Color.WHITE);
        //listeDifficulte2.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        listeDifficulte2.setFont(font);
        layeredPane.add(listeDifficulte2, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        listeDifficulte3.setVisible(false);
        listeDifficulte3.setBackground(Color.WHITE);
        //listeDifficulte3.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        listeDifficulte3.setFont(font);
        layeredPane.add(listeDifficulte3, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        listeDifficulte4.setVisible(false);
        listeDifficulte4.setBackground(Color.WHITE);
        //listeDifficulte4.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        listeDifficulte4.setFont(font);
        layeredPane.add(listeDifficulte4, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        nomJoueur1 = new JTextField(15);
        nomJoueur1.setVisible(true);
        nomJoueur1.setBackground(Color.WHITE);
        //nomJoueur1.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur1.setFont(font);
        layeredPane.add(nomJoueur1, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        nomJoueur2 = new JTextField(15);
        nomJoueur2.setVisible(true);
        //nomJoueur2.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur2.setFont(font);
        nomJoueur2.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur2, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        nomJoueur3 = new JTextField(15);
        nomJoueur3.setVisible(true);
        //nomJoueur3.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur3.setFont(font);
        nomJoueur3.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur3, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        nomJoueur4 = new JTextField(15);
        //nomJoueur4.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur4.setFont(font);
        nomJoueur4.setVisible(true);
        nomJoueur4.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur4, JLayeredPane.POPUP_LAYER);
        revalidate();
        metAJour();

        nomJoueur1.setVisible(false);
        nomJoueur2.setVisible(false);
        nomJoueur3.setVisible(false);
        nomJoueur4.setVisible(false);

        limiterNombreCaractereNomJoueur();

        //musique
        musicPlayer = new MusicPlayer("Musiques/Merchants_of_Novigrad.wav");
        musicPlayer.setVolume(-50.0f);
        musicPlayer.loop();
        //Ajout d'une interaction avec les boutons
        addMouseListener(new PanelMenuListener(this));
        initialiseSons();
        boucle();//Timer
    }

    public void setFullscreen(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if(estPleinEcran){
            fenetre.setSize(dim);
            setSize(dim);
            frame.setSize(dim);
            frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(frame);
        }else{
            fenetre.setSize(tailleFenetre);
            setSize(tailleFenetre);
            frame.setSize(tailleFenetre);
            frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
            gd.setFullScreenWindow(null);
            frame.setExtendedState(Frame.NORMAL);
            frame.setLocationRelativeTo(null);
        }
    }

    public void initialiseSons(){
        MusicPlayer clicBouton =new MusicPlayer("Musiques/clicBouton.wav");
        sonPlayer.add(clicBouton);
        MusicPlayer selectionBouton =new MusicPlayer("Musiques/selectionBouton.wav");
        sonPlayer.add(selectionBouton);
        MusicPlayer confirmerBouton =new MusicPlayer("Musiques/confirmerBouton.wav");
        sonPlayer.add(confirmerBouton);
    }

    public void playSons(int indexAJouer) {
        int sonVolume;
        if (index_sonPanel == 0) sonVolume = -100000;
        else sonVolume = (-30) + index_sonPanel * 17;
        MusicPlayer sonCourant = sonPlayer.get(indexAJouer);
        if(indexAJouer==0){
            sonCourant.setVolume(-25+sonVolume);
        }else{
            sonCourant.setVolume(-20+sonVolume);
        }
        sonCourant.resetClip();
        sonCourant.play();
    }

    private void limiterNombreCaractereNomJoueur() {
        ((AbstractDocument) nomJoueur1.getDocument()).setDocumentFilter(new DocumentFilter() {
            private int max = 15;
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - max - length;
                if (overLimit <= 0) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (overLimit < text.length()) {
                    String cuttedString = text.substring(0, text.length() - overLimit);
                    super.replace(fb, offset, length, cuttedString, attrs);
                }
            }
        });

        ((AbstractDocument) nomJoueur2.getDocument()).setDocumentFilter(new DocumentFilter() {
            private int max = 15;
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - max - length;
                if (overLimit <= 0) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (overLimit < text.length()) {
                    String cuttedString = text.substring(0, text.length() - overLimit);
                    super.replace(fb, offset, length, cuttedString, attrs);
                }
            }
        });

        ((AbstractDocument) nomJoueur3.getDocument()).setDocumentFilter(new DocumentFilter() {
            private int max = 15;
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - max - length;
                if (overLimit <= 0) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (overLimit < text.length()) {
                    String cuttedString = text.substring(0, text.length() - overLimit);
                    super.replace(fb, offset, length, cuttedString, attrs);
                }
            }
        });

        ((AbstractDocument) nomJoueur4.getDocument()).setDocumentFilter(new DocumentFilter() {
            private int max = 15;
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - max - length;
                if (overLimit <= 0) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (overLimit < text.length()) {
                    String cuttedString = text.substring(0, text.length() - overLimit);
                    super.replace(fb, offset, length, cuttedString, attrs);
                }
            }
        });
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, largeur, hauteur);
        g.drawImage(background, posX_background ,posY_background , largeur_background, hauteur_background, null);
    }

    public void afficheBoutonJouer(Graphics g) {
        if(select_jouer) g.drawImage(bouton_Jouer_select, posX_boutons, posY_Jouer, largeur_bouton, hauteur_bouton, null);
        else g.drawImage(bouton_Jouer, posX_boutons, posY_Jouer, largeur_bouton, hauteur_bouton, null);
    }

    public void afficheBoutonOptions(Graphics g) {
        if(select_options) g.drawImage(bouton_Options_select, posX_boutons, posY_Options, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Options, posX_boutons, posY_Options, largeur_bouton, hauteur_bouton,null);
    }

    public void afficheBoutonCredits(Graphics g) {
        if(select_credits) g.drawImage(bouton_Credits_select, posX_boutons, posY_Credits, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Credits, posX_boutons, posY_Credits, largeur_bouton, hauteur_bouton,null);
    }

    public void afficheBoutonQuitter(Graphics g) {
        if(select_quitter) g.drawImage(bouton_Quitter_select, posX_boutons, posY_Quitter, largeur_bouton, hauteur_bouton,null);
        else g.drawImage(bouton_Quitter, posX_boutons, posY_Quitter, largeur_bouton, hauteur_bouton,null);
    }

    private void afficheSliders(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);
        this.taille_btn = taille_btn;
        int taille_sons = (int) (Math.min(getWidth(),getHeight())*0.1);
        int taille_musiques = (int) (Math.min(getWidth(),getHeight())*0.2);

        int x = (largeur - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        posX_gauche1 = x;
        posX_droit1 = x+taille_slider_x-(taille_slider_x/11);
        posY_slider1 = y;
        g.drawImage(ecriture_Sons,(int) (posX_gauche1+(taille_slider_x/2.33)),(int) (posY_slider1*0.85),taille_sons,(int) (taille_sons/1.91),null);
        if(select_gauche1) g.drawImage(bouton_gauche_hover,posX_gauche1,posY_slider1,taille_btn,taille_btn,null);
        else g.drawImage(bouton_gauche,posX_gauche1,posY_slider1,taille_btn,taille_btn,null);
        g.drawImage(sliders[index_sonPanel],posX_gauche1,posY_slider1,taille_slider_x,taille_slider_y,null);
        if(select_droit1) g.drawImage(bouton_droit_hover,posX_droit1,posY_slider1,taille_btn,taille_btn,null);
        else g.drawImage(bouton_droit,posX_droit1,posY_slider1,taille_btn,taille_btn,null);

        posX_gauche2 = x;
        posX_droit2 = x+taille_slider_x-(taille_slider_x/11);
        posY_slider2 = y+taille_slider_y*2;
        g.drawImage(ecriture_Musiques,(int) (posX_gauche1+(taille_slider_x/2.66)),(int) (posY_slider2*0.9),taille_musiques,(int) (taille_musiques/2.64),null);
        if(select_gauche2) g.drawImage(bouton_gauche_hover,posX_gauche2,posY_slider2,taille_btn,taille_btn,null);
        else g.drawImage(bouton_gauche,posX_gauche2,posY_slider2,taille_btn,taille_btn,null);
        g.drawImage(sliders[index_musiquePanel],x,posY_slider2,taille_slider_x,taille_slider_y,null);
        if(select_droit2) g.drawImage(bouton_droit_hover,posX_droit2,posY_slider2,taille_btn,taille_btn,null);
        else g.drawImage(bouton_droit,posX_droit2,posY_slider2,taille_btn,taille_btn,null);
    }

    private void afficheCochable(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.08);
        int taille_pleinecran = (int) (Math.min(getWidth(),getHeight())*0.25);

        int x = (largeur - taille_slider_x)/2;
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

        /*g.drawImage(ecriture_Daltonien,(int) (posX_coches-taille_pleinecran*1.2),posY_coche2,taille_pleinecran,(int)(taille_pleinecran/4.2),null);
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
        }*/
    }

    private void afficheChoix(Graphics g){
        int taille_slider_x = (int) (Math.min(getWidth(),getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(getWidth(),getHeight())*0.12);


        int x = (largeur - taille_slider_x)/2;
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
        int taille_x = (int) (Math.min(largeur,hauteur)*1.3);
        int taille_y = (int) (taille_x/1.25);
        int x = (largeur - taille_x)/2;
        int y=0-(taille_y/45);
        g.drawImage(options_background, x, y, taille_x,taille_y,null);
        afficheSliders(g);
        afficheCochable(g);
        afficheChoix(g);
    }


    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (estEnChargement) {
            if (!aAfficheChargement) {
                g2d.drawImage(chargement, 0, 0, getWidth(), getHeight(), null);
            }
            this.nomJoueur1.setVisible(false);
            this.nomJoueur2.setVisible(false);
            this.nomJoueur3.setVisible(false);
            this.nomJoueur4.setVisible(false);
            this.listeChrono.setVisible(false);
            this.listeDifficulte1.setVisible(false);
            this.listeDifficulte2.setVisible(false);
            this.listeDifficulte3.setVisible(false);
            this.listeDifficulte4.setVisible(false);

            if (aAfficheChargement) {
                confirmerConfiguration();
            }
            aAfficheChargement = true;
            return;
        }
        calculeRapportsEtPositions();
        afficheBackground(g2d);
        if(clicOptions){
            afficheParametre(g2d);
        } else if (estConfigPartie) {
            afficherConfigPartie(g2d);
            afficheMessageErreur(g2d);
        }else if(clicCredits){
            afficheCredits(g2d);
        } else{
            afficheNuages(g2d);
            afficheBoutonJouer(g2d);
            afficheBoutonOptions(g2d);
            afficheBoutonCredits(g2d);
            afficheBoutonQuitter(g2d);
            afficheTaluvaTitre(g2d);
        }
    }

    private void confirmerConfiguration() {
        String nomJoueur1 = this.nomJoueur1.getText();
        String nomJoueur2 = this.nomJoueur2.getText();
        String nomJoueur3 = this.nomJoueur3.getText();
        String nomJoueur4 = this.nomJoueur4.getText();
        String tempsChrono = (String) this.listeChrono.getSelectedItem();
        String difficulte1 = (String) this.listeDifficulte1.getSelectedItem();
        String difficulte2 = (String) this.listeDifficulte2.getSelectedItem();
        String difficulte3 = (String) this.listeDifficulte3.getSelectedItem();
        String difficulte4 = (String) this.listeDifficulte4.getSelectedItem();
        ArrayList<String> difficultes = new ArrayList<>();
        difficultes.add(difficulte1);
        difficultes.add(difficulte2);
        difficultes.add(difficulte3);
        difficultes.add(difficulte4);
        int nbJoueur = this.nbJoueurs;

        PanelMenu.estConfigPartie = false;


        //efface tout le contenu de la frame
        this.layeredPane.removeAll();
        this.musicPlayer.stop();

        // On passe du menu au jeu
        loadImages();
        try {
            this.fenetre.initRenduJeu(nomJoueur1, nomJoueur2, nomJoueur3, nomJoueur4, nbJoueur, tempsChrono, difficultes);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.fenetre.panelPlateau.setBounds(this.getBounds().x, this.getBounds().y, this.getWidth(), this.getHeight());
        this.fenetre.panelVignette.setBounds(this.getBounds().x, this.getBounds().y, this.getWidth(), this.getHeight());
        this.fenetre.buttonPanel.setBounds(this.getBounds().x, this.getBounds().y, this.getWidth(), this.getHeight());
        this.jeu.indexSon = this.index_sonPanel;
        this.jeu.indexMusique = this.index_musiquePanel;
        this.jeu.initialiseMusique();
        this.jeu.initialiseSons();
    }

    private void afficherConfigPartie(Graphics2D g2d) {
        //affiche un rectangle noir sur tout l'écran
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, largeur, hauteur);
        g2d.drawImage(configPartieBackground, posX_background ,posY_background , largeur_background, hauteur_background, null);
        if(select_addJoueur && peut_addJoueur)
            g2d.drawImage(plus_select, posX_bouton_plus_joueur, posY_bouton_plus_joueur, largeur_bouton_plus, largeur_bouton_plus, null);
        else
            g2d.drawImage(plus, posX_bouton_plus_joueur, posY_bouton_plus_joueur, largeur_bouton_plus, largeur_bouton_plus, null);
        if(select_addIA && peut_addIA)
            g2d.drawImage(plus_select, posX_bouton_plus_ia, posY_bouton_plus_joueur, largeur_bouton_plus, largeur_bouton_plus, null);
        else
            g2d.drawImage(plus, posX_bouton_plus_ia, posY_bouton_plus_joueur, largeur_bouton_plus, largeur_bouton_plus, null);
        BufferedImage[] couleurs = {rouge, bleu, vert, violet};
        for(int i = 0; i < nbJoueurs; i++) {
            g2d.drawImage(couleurs[i], posX_cadre, posY_cadre + i*decalageY_couleur, largeur_cadre, largeur_cadre, null);
        }
        if(nbJoueurs <2 ) peut_valider = false;
        else peut_valider = true;
        if (nbJoueurs >= 1) {
            nomJoueur1.setVisible(true);
        }
        if (nbJoueurs >= 2) {
            nomJoueur2.setVisible(true);
        }
        if (nbJoueurs >= 3) {
            nomJoueur3.setVisible(true);
        }
        if (nbJoueurs == 4) {
            nomJoueur4.setVisible(true);
            peut_addIA = false;
            peut_addJoueur = false;
        }
        if (nbJoueurs < 4){
            peut_addIA = true;
            peut_addJoueur = true;
        }
        if (nbJoueurs == 1) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 2) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 3) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 2*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 4) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 3*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }

        g2d.drawImage(fermer, posX_bouton_fermer, 10, largeur_bouton_fermer, largeur_bouton_fermer, null);
        if(!clic_valider && peut_valider && select_valider)
            g2d.drawImage(valider_select, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else if(peut_valider && !select_valider)
            g2d.drawImage(valider, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else if(!peut_valider)
            g2d.drawImage(valider_gris, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else if(clic_valider && peut_valider) {
            g2d.drawImage(valider_clic, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        }
        nomJoueur1.setBounds(posX_textJoueur, posY_textJoueur, largeur_textJoueur, hauteur_textJoueur);
        nomJoueur2.setBounds(posX_textJoueur, posY_textJoueur+decalageY_couleur, largeur_textJoueur, hauteur_textJoueur);
        nomJoueur3.setBounds(posX_textJoueur, posY_textJoueur+2*decalageY_couleur, largeur_textJoueur, hauteur_textJoueur);
        nomJoueur4.setBounds(posX_textJoueur, posY_textJoueur+3*decalageY_couleur, largeur_textJoueur, hauteur_textJoueur);

        listeChrono.setVisible(true);
        listeChrono.setBounds(posXChronoList, posYChronoList, largeur_textDifficulte, hauteur_textJoueur);
        listeDifficulte1.setBounds(posXDifficulteList, posY_textJoueur, largeur_textDifficulte, hauteur_textJoueur);
        listeDifficulte2.setBounds(posXDifficulteList, posY_textJoueur+decalageY_couleur, largeur_textDifficulte, hauteur_textJoueur);
        listeDifficulte3.setBounds(posXDifficulteList, posY_textJoueur+2*decalageY_couleur, largeur_textDifficulte, hauteur_textJoueur);
        listeDifficulte4.setBounds(posXDifficulteList, posY_textJoueur+3*decalageY_couleur, largeur_textDifficulte, hauteur_textJoueur);
    }

    private void calculeRapportsEtPositions() {
        largeur = layeredPane.getWidth();
        hauteur = layeredPane.getHeight();
        double rapport_actuel = (double) hauteur /(double) largeur;
        if(rapport_actuel>rapport_background) {// si la fenêtre est plus haute que large
            largeur_background= largeur;
            hauteur_background=(int)(largeur_background*rapport_background);
            posX_background=0;
            posY_background=(hauteur -hauteur_background)/2;
        }
        else {
            hauteur_background= hauteur;
            largeur_background=(int)(hauteur_background/rapport_background);
            posX_background=(largeur -largeur_background)/2;
            posY_background=0;
        }
        //image titre Taluva
        largeur_taluvaTitre = (int) (largeur_background*0.2);
        hauteur_taluvaTitre = (int) (largeur_taluvaTitre *rapport_taluvaTitre);
        posX_taluvaTitre = (int) (posX_background + largeur_background/2 - largeur_taluvaTitre/2);
        posY_taluvaTitre = (int) (posY_background + hauteur_background*0.05);
        //animation
        if(peutAnimerNuage){
            decalage_nuage2 = (int) (posX_background + largeur_background*0.5);
            peutAnimerNuage = false;
        }
        largeur_nuage1 = (int) (largeur_background*0.2);
        hauteur_nuage1 = (int) (largeur_nuage1 *rapport_nuage1);
        posY_nuage1 = (int) (posY_background + hauteur_background*0.05);
        largeur_nuage2 = (int) (largeur_background*0.25);
        hauteur_nuage2 = (int) (largeur_nuage2 *rapport_nuage2);
        posY_nuage2 = (int) (posY_background + hauteur_background*0.02);
        //menu crédits
        largeur_quitter_credits = (int) (largeur_background*0.06);
        posX_quitter_credits = (int) (posX_background + largeur_background/2 - largeur_quitter_credits/2);
        posY_quitter_credits = (int) (posY_background + hauteur_background*0.95 - largeur_quitter_credits);
        largeur_credits1 = (int) (largeur_background*0.35);
        hauteur_credits1 = (int) (largeur_credits1*rapport_credits1);
        posY_credits1 = (int) (posY_background);
        posY_credits2 = (int) (posY_credits1 + hauteur_credits1*0.85);
        posY_credits3 = (int) (posY_credits2 + hauteur_credits1*0.85);
        posY_credits4 = (int) (posY_credits3 + hauteur_credits1*0.85);
        posY_credits5 = (int) (posY_credits4 + hauteur_credits1*0.85);
        posY_credits6 = (int) (posY_credits5 + hauteur_credits1*0.85);
        posY_credits7 = (int) (posY_credits6 + hauteur_credits1*0.85);
        if(peutAnimerCredits && clicCredits){
            posX_credits1 = (int) (posX_background + largeur_background* 0.02);
            posX_credits2 = (int) (posX_background + largeur_background - largeur_credits1);
            posX_credits3 = (int) (posX_background + largeur_background* 0.01);
            posX_credits4 = (int) (posX_background + largeur_background - largeur_credits1);
            posX_credits5 = (int) (posX_background + largeur_background* 0.02);
            posX_credits6 = (int) (posX_background + largeur_background - largeur_credits1);
            posX_credits7 = (int) (posX_background + largeur_background* 0.01);
            peutAnimerCredits = false;
        }
        //boutons menu principal du jeu
        largeur_bouton=largeur_background/6;
        hauteur_bouton=(int)(largeur_bouton*rapport_bouton);
        posX_boutons=posX_background+largeur_background/2-largeur_bouton/2;
        posY_Jouer = hauteur_background/4;
        int decalageY = hauteur_background/8;
        posY_Options = posY_Jouer + decalageY;
        posY_Credits = posY_Options + decalageY;
        posY_Quitter = posY_Credits + decalageY;
        largeur_menu_options = decalageY;
        hauteur_menu_options = (int)(largeur_menu_options*rapport_menu_options);
        //boutons menu création partie
        largeur_bouton_plus = (int) (largeur_background*0.045);
        largeur_bouton_moins = (int) (largeur_bouton_plus*0.7);
        largeur_bouton_valider = (int) (largeur_background*0.11);
        largeur_bouton_fermer = (int) (largeur_background*0.08);
        largeur_cadre = (int) (largeur_background*0.05);
        hauteur_textJoueur = (int) (largeur_bouton_moins*0.9);
        largeur_textJoueur = (int) (largeur_background*0.17);
        largeur_textDifficulte = (int) (largeur_textJoueur*0.7);
        posX_bouton_plus_joueur = (int) (posX_background + largeur_background*0.15);
        posX_bouton_plus_ia = (int) (posX_background + largeur_background*0.37);
        posY_bouton_plus_joueur = (int) (posY_background + hauteur_background*0.27);
        posX_textJoueur = (int) (posX_background + largeur_background*0.15);
        posY_textJoueur = (int) (posY_bouton_plus_joueur + hauteur_background*0.14);
        posYChronoList = (int) (posY_textJoueur + hauteur_background*0.05);
        posXChronoList = (int) (posX_bouton_plus_ia+largeur_background*0.36);
        posXDifficulteList = (int) (posX_textJoueur + largeur_textJoueur*1.75);
        posYDifficulteList = posY_cadre + (int) (hauteur_background*0.26);
        posX_cadre = (int) (posX_textJoueur + largeur_background*0.18);
        posY_cadre = (int) (posY_textJoueur - hauteur_background*0.02);
        posX_bouton_moins = (int) (posX_textJoueur - largeur_background*0.05);
        posY_bouton_moins = (int) (posY_cadre + largeur_cadre*0.22);
        decalageY_couleur = (int) (hauteur_background*0.12);
        posX_bouton_valider = (int) (posX_background + largeur_background/2 - largeur_bouton_valider/2);
        posY_bouton_valider = (int) (posY_background + hauteur_background*0.82);
    }

    private void afficheMessageErreur(Graphics g) {
        //Font font = new Font("Bookman Old Style", Font.BOLD, 25);
        Font font = customFont.deriveFont(Font.BOLD,25);
        g.setFont(font);
        g.setColor(Color.WHITE);
        String message = null;
        if(afficheErreur){
            message="Il doit y avoir au moins 2 joueurs";
            largeurMessageErreur = (int)(message.length()*font.getSize());
            g.drawImage(applyColorFilter(joueur_courant,(byte) 9),(int) (xConfigPanel-largeurMessageErreur/11.5), hauteur -(hauteur /4),largeurMessageErreur,(int) (largeurMessageErreur*0.1),null);
            g.drawString(message,(int) (xConfigPanel+largeurMessageErreur/5.5), (int) hauteur -(hauteur /4)+50);
            if(timerValue>=20){
                afficheErreur = false;
                timerValue=0;
            }
        }
    }

    private void afficheNuages(Graphics g) {
        g.drawImage(nuage1, posX_nuage1, posY_nuage1, largeur_nuage1, hauteur_nuage1, null);
        g.drawImage(nuage2, posX_nuage2, posY_nuage2, largeur_nuage2, hauteur_nuage2, null);
    }

    private void afficheTaluvaTitre(Graphics g) {
        g.drawImage(taluvaTitre, posX_taluvaTitre, posY_taluvaTitre, largeur_taluvaTitre, hauteur_taluvaTitre, null);
    }

    private void afficheCredits(Graphics g) {
        g.drawImage(credits_background, 0, 0, largeur, hauteur, null);
        g.drawImage(credits1, posX_credits1, posY_credits1, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits2, posX_credits2, posY_credits2, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits3, posX_credits3, posY_credits3, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits4, posX_credits4, posY_credits4, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits5, posX_credits5, posY_credits5, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits6, posX_credits6, posY_credits6, largeur_credits1, hauteur_credits1, null);
        g.drawImage(credits7, posX_credits7, posY_credits7, largeur_credits1, hauteur_credits1, null);
        if(select_quitter_credits)
            g.drawImage(fermer_select, posX_quitter_credits, posY_quitter_credits, largeur_quitter_credits, largeur_quitter_credits, null);
        else
            g.drawImage(fermer, posX_quitter_credits, posY_quitter_credits, largeur_quitter_credits, largeur_quitter_credits, null);
    }

    public void boucle(){
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metAJour();
                int decalage = Math.max(1,largeur_background/1000);
                decalage_nuage1 = (decalage_nuage1+decalage);
                decalage_nuage2 = (decalage_nuage2+decalage);
                posX_nuage2 = (posX_background+decalage_nuage2)%(largeur_background+1);
                posX_nuage1 = (posX_background+decalage_nuage1)%(largeur_background+1);
                //crédits
                if(clicCredits){
                    decalage = Math.max(1,largeur_background/1000);
                    posX_credits1 = (posX_credits1+decalage)%largeur_background;
                    posX_credits2 = (posX_credits2-decalage)%largeur_background;
                    posX_credits3 = (posX_credits3+decalage)%largeur_background;
                    posX_credits4 = (posX_credits4-decalage)%largeur_background;
                    posX_credits5 = (posX_credits5+decalage)%largeur_background;
                    posX_credits6 = (posX_credits6-decalage)%largeur_background;
                    posX_credits7 = (posX_credits7+decalage)%largeur_background;
                }

            }
        });
        timer.start();
    }
    public void metAJour() {
        if(afficheErreur) timerValue++;
        if(chargeParametre){
            if(fenetre!=null){
                setFullscreen();
                chargeParametre=false;
            }
        }
        repaint();
    }
    public static void setParametre(int volumeMusic, int volumeSon,boolean pleinEcran){
        try{
            File file = new File ("sauvegardeParametres.txt");
            file.delete();
            file.createNewFile();
            FileOutputStream fichierOut2 = new FileOutputStream(file);
            ObjectOutputStream out2 = new ObjectOutputStream(fichierOut2);
            out2.writeInt(volumeSon);
            out2.writeInt(volumeMusic);
            out2.writeBoolean(pleinEcran);
            out2.close();
            fichierOut2.close();
        }catch (Exception e){
            System.out.println("impossible de save les parametres .\n"+e);
        }

    }
    public static void loadParametre() {
        try {
            File f = new File("sauvegardeParametres.txt");
            if(f.exists() && f.canRead()){
                FileInputStream file2 = new FileInputStream(f);
                ObjectInputStream in2 = new ObjectInputStream(file2);
                index_sonPanel =in2.readInt();
                index_musiquePanel =in2.readInt();
                estPleinEcran=in2.readBoolean();
                //System.out.println(estPleinEcran);
                //estPleinEcran=in2.readBoolean();
                in2.close();
                file2.close();
            }else{
                index_sonPanel =3;
                index_musiquePanel =3;
                estPleinEcran=false;
            }
        } catch (Exception e) {
            index_sonPanel = 0;
            index_musiquePanel =0;
            estPleinEcran=false;
            //throw new RuntimeException("Impossible de charger les parametres.\n" + e);
        }
    }



}
