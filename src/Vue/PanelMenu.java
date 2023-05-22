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

import static Vue.ImageLoader.*;

public class PanelMenu extends JPanel {
    JFrame frame;
    JLayeredPane layeredPane;
    FenetreJeu fenetre;
    public Jeu jeu;
    ControleurMediateur controleur;
    public MusicPlayer musicPlayer;
    final BufferedImage[] sliders = new BufferedImage[6];
    BufferedImage background,bouton_Local,bouton_Reseau,bouton_Options,bouton_Quitter,bouton_Local_hover,bouton_Reseau_hover,bouton_Options_hover,bouton_Quitter_hover,
            options_background,bouton_droit,bouton_gauche,btn_valider, btn_annuler,coche_non,coche_oui,bouton_droit_hover,bouton_gauche_hover,btn_valider_hover, btn_annuler_hover,coche_non_hover,coche_oui_hover
            ,ecriture_Sons,ecriture_Musiques,ecriture_PleinEcran,ecriture_Daltonien,ecriture_Extension;
    Dimension tailleEcran, tailleFenetre;
    int screenWidth;
    int screenHeight;
    int largeur;
    int hauteur;
    int largeur_background;
    int largeur_bouton;
    int largeur_menu_options;
    int hauteur_background;
    int hauteur_bouton;
    int hauteur_menu_options;
    public static int index_sonPanel;
    public static int index_musiquePanel;
    public int posX_boutons, posY_jcj, posY_jcia, posY_ia, posY_Options, posY_Local, posX_background, posY_background,posY_Reseau,
            posY_Quitter, posX_menu_options, posX_droit1, posX_droit2,posX_gauche1, posX_gauche2, posY_slider2,posY_slider1, taille_btn, posX_coches, posY_coche1,posY_coche2,posY_coche3,
            posX_btnAnnuler,posX_btnValider,posY_btnChoix;
    public int posX_bouton_plus_joueur, posY_bouton_plus_joueur, posYChronoList, posXChronoList, posXDifficulteList, posYDifficulteList,  posX_bouton_plus_ia, posX_bouton_moins, posY_bouton_moins, posX_cadre, posY_cadre, posX_textJoueur, posY_textJoueur,
    posX_bouton_valider, posY_bouton_valider, posX_bouton_fermer, posY_bouton_fermer;
    public int largeur_bouton_plus, largeur_bouton_moins, largeur_cadre, largeur_bouton_valider, largeur_bouton_fermer, largeur_textJoueur, decalageY_couleur;
    private int timerValue;
    private JTextField field_joueur1;
    boolean select_local;
    boolean select_reseau;
    boolean select_options;
    boolean select_quitter;
    boolean clicOptions;
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

    boolean select_addJoueur, select_addIA, peut_addIA = true, peut_addJoueur = true;
    boolean afficheErreur;

    static boolean estConfigPartie = false;
    int xConfigPanel, yConfigPanel;
    JTextField nomJoueur1, nomJoueur2, nomJoueur3, nomJoueur4;
    String[] choixChrono = { "Infini", "15 sec", "30 sec", "1 min" };
    String[] choixDifficulte = { "Facile", "Intermediaire", "Difficile"};
    JComboBox<String> listeChrono = new JComboBox<>(choixChrono);
    JComboBox<String> listeDifficulte = new JComboBox<>(choixDifficulte);
    public ArrayList<MusicPlayer> sonPlayer = new ArrayList<>();


    int nbJoueurs = 0;

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
        configPartieBackground = lisImageBuf("background_choix_partie");
        plus = lisImageBuf("plus");
        moins = lisImageBuf("moins");
        rouge = lisImageBuf("rouge");
        vert = lisImageBuf("vert");
        bleu = lisImageBuf("bleu");
        violet = lisImageBuf("violet");

        valider = lisImageBuf("valider");
        fermer = lisImageBuf("fermer");

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
        loadParametre();

        timerValue=0;
        //booléens
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
        listeChrono.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        layeredPane.add(listeChrono, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        listeDifficulte.setVisible(true);
        listeDifficulte.setBackground(Color.WHITE);
        listeDifficulte.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        layeredPane.add(listeDifficulte, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        nomJoueur1 = new JTextField(15);
        nomJoueur1.setVisible(true);
        nomJoueur1.setBackground(Color.WHITE);
        nomJoueur1.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        layeredPane.add(nomJoueur1, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        nomJoueur2 = new JTextField(15);
        nomJoueur2.setVisible(true);
        nomJoueur2.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur2.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur2, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        nomJoueur3 = new JTextField(15);
        nomJoueur3.setVisible(true);
        nomJoueur3.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur3.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur3, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        nomJoueur4 = new JTextField(15);
        nomJoueur4.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nomJoueur4.setVisible(true);
        nomJoueur4.setBackground(Color.WHITE);
        layeredPane.add(nomJoueur4, JLayeredPane.POPUP_LAYER);
        revalidate();
        repaint();

        nomJoueur1.setVisible(false);
        nomJoueur2.setVisible(false);
        nomJoueur3.setVisible(false);
        nomJoueur4.setVisible(false);

        limiterNombreCaractereNomJoueur();

        //musique
        musicPlayer = new MusicPlayer("Musiques\\Merchants_of_Novigrad.wav");
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
        MusicPlayer placerTuile =new MusicPlayer("Musiques/clicBouton.wav");
        sonPlayer.add(placerTuile);
        MusicPlayer placerBatiment =new MusicPlayer("Musiques/selectionBouton.wav");
        sonPlayer.add(placerBatiment);
    }

    public void playSons(int indexAJouer){
        int sonVolume;
        if(index_sonPanel==0) sonVolume=-100000;
        else sonVolume = (-30)+index_sonPanel*20;
        MusicPlayer sonCourant = sonPlayer.get(indexAJouer);
        sonCourant.resetClip();
        sonCourant.setVolume(sonVolume);
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
        int taille_x = (int) (Math.min(getWidth(),getHeight())*1.3);
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
        calculeRapportsEtPositions();
        afficheBackground(g2d);
        if(clicOptions){
            afficheParametre(g2d);
        } else if (estConfigPartie) {
            afficherConfigPartie(g2d);
            afficheMessageErreur(g2d);
        } else{
            afficheBoutonLocal(g2d);
            afficheBoutonReseau(g2d);
            afficheBoutonOptions(g2d);
            afficheBoutonQuitter(g2d);
        }
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
        if(nbJoueurs <2) peut_valider = false;
        else peut_valider = true;
        if (nbJoueurs >= 1) {
            nomJoueur1.setVisible(true);
            peut_addIA = true;
            peut_addJoueur = true;
        }
        if (nbJoueurs >= 2) {
            nomJoueur2.setVisible(true);
        }
        if (nbJoueurs >= 3) {
            nomJoueur3.setVisible(true);
        }
        if (nbJoueurs == 4) {
            nomJoueur4.setVisible(true);
        }
        if (nbJoueurs == 1) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 0*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 2) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 1*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 3) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 2*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if (nbJoueurs == 4) {
            g2d.drawImage(moins, posX_bouton_moins, posY_bouton_moins + 3*decalageY_couleur, largeur_bouton_moins, largeur_bouton_moins, null);
        }
        if(select_valider && peut_valider)
            g2d.drawImage(valider_select, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else if(clic_valider && peut_valider)
            g2d.drawImage(valider_clic, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else if(!select_valider && peut_valider)
            g2d.drawImage(valider, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        else
            g2d.drawImage(valider_gris, posX_bouton_valider, posY_bouton_valider, largeur_bouton_valider, largeur_bouton_valider, null);
        nomJoueur1.setBounds(posX_textJoueur, posY_textJoueur, largeur_textJoueur, 40);
        nomJoueur2.setBounds(posX_textJoueur, posY_textJoueur+decalageY_couleur, largeur_textJoueur, 40);
        nomJoueur3.setBounds(posX_textJoueur, posY_textJoueur+2*decalageY_couleur, largeur_textJoueur, 40);
        nomJoueur4.setBounds(posX_textJoueur, posY_textJoueur+3*decalageY_couleur, largeur_textJoueur, 40);

        listeChrono.setVisible(true);
        listeDifficulte.setVisible(true);
        listeChrono.setBounds(posXChronoList, posYChronoList, largeur_textJoueur + 35, 40);
        listeDifficulte.setBounds(posXDifficulteList, posYDifficulteList, largeur_textJoueur + 35, 40);
    }

    private void calculeRapportsEtPositions() {
        largeur = layeredPane.getWidth();
        hauteur = layeredPane.getHeight();
        double rapport_bouton=1;
        double rapport_menu_options = 1.0980140935297885970531710442024;//rapport de 1714/1561
        double rapport_background = 0.5625;
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
        largeur_bouton=largeur_background/7;
        hauteur_bouton=(int)(largeur_bouton*rapport_bouton);
        posX_boutons=posX_background+largeur_background/2-largeur_bouton/2;
        posY_Local=0;
        posY_Options=posY_Local+hauteur_background/3;
        posY_Quitter=posY_Options+hauteur_background/3;
        largeur_menu_options = hauteur_background/2;
        hauteur_menu_options = (int)(largeur_menu_options*rapport_menu_options);
        //boutons menu création partie
        largeur_bouton_plus = (int) (largeur_background*0.06);
        largeur_bouton_moins = (int) (largeur_bouton_plus*0.6);
        largeur_bouton_valider = (int) (largeur_background*0.09);
        largeur_bouton_fermer = (int) (largeur_background*0.08);
        largeur_cadre = (int) (largeur_background*0.05);
        largeur_textJoueur = (int) (largeur_background*0.16);
        posX_bouton_plus_joueur = (int) (posX_background + largeur_background*0.12);
        posX_bouton_plus_ia = (int) (posX_background + largeur_background*0.31);
        posY_bouton_plus_joueur = (int) (posY_background + hauteur_background*0.29);
        posX_textJoueur = (int) (posX_background + largeur_background*0.055);
        posY_textJoueur = (int) (posY_bouton_plus_joueur + hauteur_background*0.14);
        posYChronoList = posY_bouton_plus_joueur + 5;
        posXChronoList = (int) (largeur_background*0.68);
        posXDifficulteList = posXChronoList;
        posYDifficulteList = posY_cadre + (int) (hauteur_background*0.26);
        posX_cadre = (int) (posX_textJoueur + largeur_background*0.18);
        posY_cadre = (int) (posY_textJoueur - hauteur_background*0.02);
        posX_bouton_moins = (int) (posX_cadre + largeur_cadre*1.5);
        posY_bouton_moins = (int) (posY_cadre + largeur_cadre*0.2);
        decalageY_couleur = (int) (hauteur_background*0.13);
        posX_bouton_valider = (int) (posX_background + largeur_background/2 - largeur_bouton_valider/2);
        posY_bouton_valider = (int) (posY_background + hauteur_background*0.78);
        posX_bouton_fermer = getWidth() - 100;
    }

    private void afficheMessageErreur(Graphics g) {
        Font font = new Font("Bookman Old Style", Font.BOLD, 25);
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


    public void boucle(){
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metAJour();
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
                System.out.println(estPleinEcran);
                //estPleinEcran=in2.readBoolean();
                in2.close();
                file2.close();
            }else{
                index_sonPanel =3;
                index_musiquePanel =3;
                estPleinEcran=false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger les parametres.\n" + e);
        }
    }


}
