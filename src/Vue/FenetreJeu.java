package Vue;

import Controleur.ControleurMediateur;
import Modele.Jeu.Jeu;
import Modele.Jeu.Joueur;
import Modele.Jeu.MusicPlayer;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static Vue.ImageLoader.*;


public class FenetreJeu extends Container {
    FenetreJeuListener listener;
    public PanelPlateau panelPlateau;

    Graphics g1;
    public PanelMenu panelMenu;

    final BufferedImage[] sliders = new BufferedImage[6];
    public PanelVignette panelVignette;
    public JPanel backgroundPanel, buttonPanel;
    public JLayeredPane layeredPane;

    final ControleurMediateur controleur;

    public static Point LastPosition;
    public static Jeu jeu;
    final JFrame frame;
    public static ArrayList<Joueur> joueurs_tries;
    public static boolean estFenetreScoreChargee = false, estImageTuilePiocheeFinale=false;

    public ArrayList<MusicPlayer> sonPlayer = new ArrayList<>();
    public boolean afficheOptions,retourDebug;
    public static boolean afficheSave;
    public static boolean afficheLoad;
    public static boolean estSurBoutonOptions, estSurBoutonAnnuler, estSurBoutonRefaire, estSurBoutonQuitterOptions, estSurBoutonQuitterFinPartie;

    public int index_son,index_musique,posX_droit1, posX_droit2,posX_gauche1, posX_gauche2, posY_slider2,posY_slider1, taille_btn, posX_coches, posY_coche1,posY_coche2,posY_coche3,
            posX_btnAnnuler,posX_btnValider,posY_btnChoix, taille_btnParametre;

    public boolean select_gauche1,select_gauche2,select_droit1,select_droit2,select_PleinEcran,
            select_Daltonien,select_Extension, estPleinEcran,Daltonien,Extension, select_valider,select_annuler2;
    public static BufferedImage fenetre_score_courante,options_background,bouton_droit,bouton_gauche,btn_valider, btn_annuler,coche_non,coche_oui,bouton_droit_hover,bouton_gauche_hover,btn_valider_hover, btn_annuler_hover,coche_non_hover,coche_oui_hover
            ,ecriture_Sons,ecriture_Musiques,ecriture_PleinEcran,ecriture_Daltonien,ecriture_Extension, image_save;
    public static int indice_chrono, indice_tuilePiochee;
    public static String tempsFixe;
    static BufferedImage hutte_j0 = null;
    static BufferedImage hutte_j1 = null;
    static BufferedImage tour_j0 = null;
    static BufferedImage tour_j1 = null;
    static BufferedImage temple_j0 = null;
    static BufferedImage temple_j1 = null;
    static BufferedImage hutte_j2 = null;
    static BufferedImage hutte_j3 = null;
    static BufferedImage tour_j2 = null;
    static BufferedImage tour_j3 = null;
    static BufferedImage temple_j2 = null;
    static BufferedImage temple_j3 = null;
    static Color couleur_bleue = new Color(0, 166, 255, 255);
    static int taille_texte_finPartie;
    static Font customFont;

    public static String[]sauvegardes;
    static File dossier;
    static String DOSSIER = System.getProperty("user.home") + File.separator + "Taluva" + File.separator + "sauvegardes";

    public FenetreJeu(Jeu jeu, ControleurMediateur controleur) throws IOException, FontFormatException {
        File dossier = new File(DOSSIER);
        dossier.mkdirs();
        sauvegardes = dossier.list();

        this.controleur = controleur;
        this.controleur.setEngine(this);
        joueurs_tries = new ArrayList<>();
        FenetreJeu.jeu = jeu;
        customFont = Font.createFont(Font.TRUETYPE_FONT, new File("ressources/BOOKOSB.TTF"));
        frame = getFMenu();
        frame.setMinimumSize(new Dimension(1366, 768));
        frame.setMaximumSize(new Dimension(3840, 2160));
        initFrame();
        initLayeredPanel();
        initMenu();
        initPanels(controleur);
        initKeyBoardAndMouseListener();
        setBackgroundColor();
        select_menu_options = false;
        afficheOptions = false;
        retourDebug = false;
        frame.setVisible(true);
        loadImageOption();
        initialiseSons();
    }

    public void initMenuJeu() throws IOException {
        layeredPane.removeAll();
        initFrame();
        initMenu();
        buttonPanel.removeAll();
        panelPlateau.removeAll();

        panelMenu.setFenetre(this);
        panelMenu.setBounds(0, 0, getWidth(), getHeight());
        initVignettePanel();

        layeredPane.revalidate();
        layeredPane.repaint();
        frame.revalidate();
        frame.repaint();
    }

    public void initMenuChoixModeJeu(){
        layeredPane.removeAll();
        initFrame();
    }

    public void initRenduJeu(String nomJoueur1, String nomJoueur2, String nomJoueur3, String nomJoueur4, int nbJoueur, String tempsChrono, ArrayList<String> difficultes) throws CloneNotSupportedException {
        jeu.initPartie(nomJoueur1, nomJoueur2, nomJoueur3, nomJoueur4, nbJoueur, tempsChrono, difficultes);
        select_fin_partie = false;
        ecran_fin_partie = false;
        estPleinEcran = panelMenu.estPleinEcran;
        initFrame();
        initPanels(controleur);
        initKeyBoardAndMouseListener();
        setBackgroundColor();
        boucle();
        tempsDebutPartie = System.currentTimeMillis();
    }

    public void setHandCursor(){
        frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setStandardCursor(){
        Image cursorImage = Toolkit.getDefaultToolkit().getImage("ressources/Menu/normal_cursor.png");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotspot = new Point(0, 0);
        Cursor idle_cursor = toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");

        frame.setCursor(idle_cursor);
    }

    private void initMenu() throws IOException {
        // Ajouter les tuiles hexagonales
        panelMenu = new PanelMenu(frame,layeredPane,jeu,controleur);
        panelMenu.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(panelMenu, JLayeredPane.DEFAULT_LAYER);
    }

    private void initKeyBoardAndMouseListener() {
        listener = new FenetreJeuListener(this);
    }

    private void setBackgroundColor() {
        //Définit la couleur d'arrière-plan en bleu océan
        frame.getContentPane().setBackground(new Color(0, 116, 125));
    }

    private void initPanels(ControleurMediateur controleur) {
        //initBackgroundPanel();
        initHexagonsPanel(controleur);
        initVignettePanel();
        initButtonPanel();
    }

    private void initVignettePanel() {
        // Ajouter la vignette
        panelVignette = new PanelVignette();
        panelVignette.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(panelVignette, JLayeredPane.PALETTE_LAYER);
    }

    private void initHexagonsPanel(ControleurMediateur controleur) {
        // Ajouter les tuiles hexagonales
        panelPlateau = new PanelPlateau(this, controleur, jeu);
        panelPlateau.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(panelPlateau, 50);
    }

    private void initLayeredPanel() {
        // Créer un JLayeredPane pour superposer les éléments
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.getContentPane().add(layeredPane);
    }

    private void initButtonPanel() {
        createButtonPanel();
        buttonPanel.setBackground(new Color(0, 0, 0, 0));
        buttonPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        buttonPanel.setOpaque(false);
        layeredPane.add(buttonPanel, JLayeredPane.POPUP_LAYER);
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                if (!ImageLoader.loaded) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g2d);
                calculeRapports();
                afficheFenetreScore(g2d);
                afficheJoueurCourant(g2d);
                afficheTimer(g2d);
                //afficheBoutonTuto(g);
                afficheMessageErreur(g2d);
                afficheBoutonAnnuler(g2d);
                afficheBoutonRefaire(g2d);
                afficheBoutonOptionsEchap(g);
                afficheTuilePiochee(g2d);
                afficheFinPartie(g2d);
                afficheMenuOptions(g2d);
                if(afficheOptions){
                    afficheParametre(g2d);
                }if(afficheSave){
                    afficheSave(g2d);
                }if(afficheLoad){
                    afficheLoad(g2d);

                }else{
                    index_musique = jeu.indexMusique;
                    index_son = jeu.indexSon;
                }
            }

            private void calculeRapports() {
                largeur = layeredPane.getWidth();
                hauteur = layeredPane.getHeight();
                double rapport = 492.0 / 847.0;
                double rapport_fenetre_score = 1400.0/872.0;
                double rapport_joueur_courant = 280.0/950.0;
                double rapport_bouton_dans_options = 351.0/1572.0;
                double rapport_fin_partie = 9.0/16.0;
                double rapport_cadre = 76.0/1349.0;
                double rapport_timer = 205.0/335.0;
                double rapport_pioche = 650.0/880.0;
                //background
                double rapport_background = 0.5625;// rapport de 2160/3840
                double rapport_actuel = (double)hauteur/(double)largeur;
                if(rapport_actuel>rapport) {// si la fenêtre est plus haute que large
                    largeur_background=largeur;
                    hauteur_background=(int)(largeur_background*rapport);
                    posX_background=0;
                    posY_background=(hauteur-hauteur_background)/2;
                }
                else {
                    hauteur_background=hauteur;
                    largeur_background=(int)(hauteur_background/rapport);
                    posX_background=(largeur-largeur_background)/2;
                    posY_background=0;
                }
                //boutons général
                largeur_bouton = Math.min(Math.max(Math.min(largeur / 11, hauteur / 11), 80), 190);
                hauteur_bouton = largeur_bouton;
                posX_tuto = (int) (largeur / 1.2);
                posY_tuto =  (int)(hauteur / 1.3);

                largeur_bouton_dans_options = (int) (largeur_menu_options * 0.6);
                hauteur_bouton_dans_options = (int) (largeur_bouton_dans_options * rapport_bouton_dans_options);
                posX_save = posX_menu_options + largeur_menu_options/2 - largeur_bouton_dans_options/2;
                posY_retour= (int) (posY_menu_options + hauteur_menu_options * 0.10);
                posY_save = (int) (posY_retour + hauteur_menu_options * 0.16);
                posY_load = (int) (posY_save + hauteur_menu_options * 0.16);
                posY_parametres = (int) (posY_load + hauteur_menu_options * 0.16);
                posY_quitter = (int) (posY_parametres + hauteur_menu_options * 0.16);
                //bouton options en jeu
                posX_options_echap = (int) (largeur - largeur*0.08);
                posY_options_echap = posY_fenetre_score;
                //fenêtre de score
                largeur_fenetre_score = (int) (largeur_bouton * 3.6);
                hauteur_fenetre_score = (int) (largeur_fenetre_score * rapport_fenetre_score);
                largeur_joueur_courant = largeur_fenetre_score;
                hauteur_joueur_courant = (int) (largeur_joueur_courant * rapport_joueur_courant);
                posX_joueur_courant = (largeur/2 - largeur_joueur_courant/2);
                posY_joueur_courant = 2;
                posX_fenetre_score = 2;
                posY_fenetre_score = hauteur_joueur_courant/2;
                posX_prenom = (int) (posX_fenetre_score + largeur_fenetre_score*0.15);
                posY_prenom_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.07);
                posY_prenom_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.25);
                posY_prenom_j2 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.42);
                posY_prenom_j3 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.60);
                posX_huttes = (int) (posX_fenetre_score + largeur_fenetre_score*0.27);
                posX_tours = (int) (posX_fenetre_score + largeur_fenetre_score*0.53);
                posX_temples = (int) (posX_fenetre_score + largeur_fenetre_score*0.78);
                posY_scores_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.15);
                posY_scores_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.33);
                posY_scores_j2 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.505);
                posY_scores_j3 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.685);
                //bâtiments sur la fenêtre de score
                largeur_hutte_score = (int) (largeur_fenetre_score * 0.35);
                largeur_tour_score = (int) (largeur_fenetre_score * 0.22);
                largeur_temple_score = largeur_tour_score;
                posX_huttes_score = (int) (posX_fenetre_score+largeur_fenetre_score*0.03);
                posX_tours_score = (int) (posX_fenetre_score+largeur_fenetre_score*0.33);
                posX_temples_score = (int) (posX_fenetre_score+largeur_fenetre_score*0.56);
                posY_huttes_score_j0 = posY_scores_j0-largeur_hutte_score/2;
                posY_huttes_score_j1 = posY_scores_j1-largeur_hutte_score/2;
                posY_huttes_score_j2 = posY_scores_j2-largeur_hutte_score/2;
                posY_huttes_score_j3 = posY_scores_j3-largeur_hutte_score/2;
                posY_temples_score_j0 = (int) (posY_scores_j0-largeur_temple_score*0.46);
                posY_temples_score_j1 = (int) (posY_scores_j1-largeur_temple_score*0.47);
                posY_temples_score_j2 = (int) (posY_scores_j2-largeur_temple_score*0.47);
                posY_temples_score_j3 = (int) (posY_scores_j3-largeur_temple_score*0.47);
                posY_tours_score_j0 = (int) (posY_scores_j0-largeur_tour_score*0.48);
                posY_tours_score_j1 = (int) (posY_scores_j1-largeur_tour_score*0.50);
                posY_tours_score_j2 = (int) (posY_scores_j2-largeur_tour_score*0.49);
                posY_tours_score_j3 = (int) (posY_scores_j3-largeur_tour_score*0.51);
                //images de la pioche
                largeur_pioche = (int) (largeur_fenetre_score*0.44);
                hauteur_pioche = (int) (largeur_pioche*rapport_pioche);
                posX_pioche = (int) (posX_fenetre_score + largeur_fenetre_score*0.20);
                //6 dernières tuiles de la pioche
                largeur_tuile = (int) (largeur_fenetre_score*0.28);
                hauteur_tuile = largeur_tuile;
                posX_nb_tuiles_pioche = (int) (posX_fenetre_score + largeur_fenetre_score*0.71);
                if(jeu.getNbJoueurs()==2){
                    posY_nb_tuiles_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.49);
                    posY_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.38);
                    posX_annuler = 9;
                    posX_refaire = (int) (posX_annuler + largeur_fenetre_score*0.66);
                }else if(jeu.getNbJoueurs()==3){
                    posY_nb_tuiles_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.67);
                    posY_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.56);
                    posX_annuler = 10;
                    posX_refaire = (int) (posX_annuler + largeur_fenetre_score*0.68);
                }else{
                    posY_nb_tuiles_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.85);
                    posY_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.74);
                    posX_annuler = 11;
                    posX_refaire = (int) (posX_annuler + largeur_fenetre_score*0.67);
                }
                //boutons annuler et refaire
                posY_annuler = (int) (posY_pioche + hauteur_fenetre_score*0.27);
                posY_refaire = posY_annuler;
                //animation tuile piochée
                largeur_tuilePiochee_init = (int) (largeur_fenetre_score*0.32);
                posX_tuilePiochee_init = (int) (posX_fenetre_score+largeur_fenetre_score*0.35);
                posY_tuilePiochee_init = posY_pioche;
                if(posX_tuilePiochee==0) {
                    posX_tuilePiochee = posX_tuilePiochee_init;
                    posY_tuilePiochee = posY_tuilePiochee_init;
                    largeur_tuilePiochee = largeur_tuilePiochee_init;
                }
                //image chronomètre
                largeur_chrono = (int) (largeur_bouton*0.90);
                posX_chrono = (int) (posX_joueur_courant + largeur_joueur_courant*0.92);
                posY_chrono = (int) (posY_joueur_courant + hauteur_joueur_courant*0.09);
                largeur_aiguille = (int) (largeur_bouton*0.70);
                posX_aiguille = posX_chrono + (largeur_chrono/2) - (largeur_aiguille/2);
                posY_aiguille = (int) (posY_chrono + (largeur_chrono*0.55) - (largeur_aiguille/2));
                //timer
                posX_timer = (int) (posX_chrono + largeur_chrono*0.77);
                posY_timer = (int) (posY_chrono + largeur_chrono*0.18);
                largeur_timer = (int) (largeur_fenetre_score*0.30);
                hauteur_timer = (int) (largeur_timer * rapport_timer);
                //fin de partie
                if((double)hauteur/(double)largeur > rapport_fin_partie){
                    largeur_fin_partie = (int) (largeur*0.8);
                    hauteur_fin_partie = (int) (largeur_fin_partie * rapport_fin_partie);
                }else{
                    hauteur_fin_partie = (int) (hauteur*0.8);
                    largeur_fin_partie = (int) (hauteur_fin_partie / rapport_fin_partie);
                }
                taille_texte_finPartie = (int) (hauteur_fin_partie*0.035);
                posX_finPartie = (largeur - largeur_fin_partie)/2;
                posY_finPartie = (hauteur - hauteur_fin_partie)/2;
                //bouton retour fin partie
                hauteur_retour_finPartie = (int) (hauteur_fin_partie*0.12);
                largeur_retour_finPartie = (int) (hauteur_retour_finPartie / rapport_bouton_dans_options);
                posX_retour_finPartie = (int) (posX_finPartie + largeur_fin_partie *0.61);
                posY_retour_finPartie = (int) (posY_finPartie + hauteur_fin_partie*0.83);
                largeur_cadre = (int) (largeur_fin_partie * 0.917);
                hauteur_cadre = (int) (largeur_cadre * rapport_cadre);
                posX_cadre = (int) (posX_finPartie+largeur_fin_partie*0.040);
                posY_cadre = (int) (posY_finPartie+hauteur_fin_partie*0.19);
                decalageY_cadre = (int) (hauteur_cadre+hauteur_fin_partie*0.02);
                posX_joueur_finPartie = (int) (posX_cadre + hauteur_fin_partie*0.03);
                posY_joueur_finPartie = (int) (posY_cadre + hauteur_fin_partie*0.055);
                decalageY_joueur = decalageY_cadre;
                posX_huttes_finPartie = (int) (posX_cadre + largeur_fin_partie*0.332);
                posX_temples_finPartie = (int) (posX_cadre + largeur_fin_partie*0.495);
                posX_tours_finPartie = (int) (posX_cadre + largeur_fin_partie*0.653);
                posX_score_finPartie = (int) (posX_cadre + largeur_fin_partie*0.785);
                posX_temps_partie = (int) (posX_joueur_finPartie + largeur_fin_partie*0.23);
                posY_temps_partie = (int) (posY_cadre + hauteur_fin_partie*0.710);

                //menu d'options
                largeur_boutonSave = (int)(largeur_joueur_courant*1.7);
                hauteur_boutonSave = (int)(largeur_joueur_courant*0.4);
                largeur_optionSave = (int)(largeur_joueur_courant*2.5);
                hauteur_optionSave = (int)(largeur_joueur_courant*2.5);
                posX_optionSave = (largeur/2 - largeur_optionSave/2);;
                posY_optionSave = (hauteur/2 - hauteur_optionSave/2);
                posX_optionSaveBouton = (int) (posX_optionSave + largeur_optionSave/2 - largeur_boutonSave/2);
                posY_optionSaveBouton = (int)(posY_optionSave + hauteur_optionSave*0.15);
                decalageY_save = (int) (hauteur_boutonSave*1.1);

                texte_saveXvide=(int)(posX_optionSaveBouton+hauteur_boutonSave*1.85) ;
                texte_saveX=(int)(posX_optionSaveBouton+hauteur_boutonSave/10);
                texte_savey=(int)(posY_optionSaveBouton+hauteur_boutonSave/2);

                largeur_menu_options = (int) Math.min(Math.max(Math.min(largeur*0.8, hauteur*0.8), 400), 800);
                hauteur_menu_options = largeur_menu_options;
                posX_menu_options = (largeur - largeur_menu_options)/2;
                posY_menu_options = (hauteur - hauteur_menu_options)/2;

                posX_btnAnnulerSave = posX_optionSave + largeur_optionSave - largeur_bouton*2;
                posY_btnAnnulerSave = (int) (posY_optionSave + hauteur_optionSave - largeur_bouton*1.2);

                //message d'erreur
                posX_messageErreur = (int) (largeur/2 - largeur_bouton/2);
                posY_messageErreur = (int) (hauteur*0.8);
                hauteurMessageErreur = (int) (hauteur*0.05);
                largeurMessageErreur = (int) (largeur*0.21);
            }
        };
    }

    private void initFrame() {
        frame.setTitle("Taluva");
        ImageIcon icon = new ImageIcon("ressources/icon.png");
        frame.setIconImage(icon.getImage());
        //récupère la taille de l'écran
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        //Définit la taille de la fenêtre à 60% de la taille de l'écran
        if(panelMenu!=null){
            frame.setBounds(panelMenu.frame.getBounds().x, panelMenu.frame.getBounds().y, panelMenu.getWidth(), panelMenu.getHeight());
        }
        else{
            frame.setSize(dim.width * 6 / 10, dim.height * 6 / 10);
            frame.setLocationRelativeTo(null);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public JFrame getFMenu(){
        finPartie = lisImageBuf("Fin_partie");
        cadreBleu = lisImageBuf("cadre_bleu");
        cadreRouge = lisImageBuf("cadre_rouge");
        cadreVert = lisImageBuf("cadre_vert");
        cadreViolet = lisImageBuf("cadre_violet");
        bouton_save = lisImageBuf("Menu/bouton_sauvegarder");
        bouton_save_select = lisImageBuf("Menu/bouton_sauvegarder_select");
        bouton_load = lisImageBuf("Menu/bouton_charger");
        bouton_load_select = lisImageBuf("Menu/bouton_charger_select");
        bouton_parametres = lisImageBuf("Menu/bouton_parametres");
        bouton_parametres_select = lisImageBuf("Menu/bouton_parametres_select");
        bouton_retour = lisImageBuf("Menu/bouton_retour");
        bouton_retour_select = lisImageBuf("Menu/bouton_retour_select");
        bouton_quitter = lisImageBuf("Menu/bouton_menu_echap_quitter");
        bouton_quitter_select = lisImageBuf("Menu/bouton_menu_echap_quitter_select");
        bouton_annuler = lisImageBuf("Annuler");
        bouton_annuler = lisImageBuf("Annuler");
        bouton_annuler_select = lisImageBuf("Annuler_select");
        bouton_refaire = lisImageBuf("Refaire");
        bouton_refaire_select = lisImageBuf("Refaire_select");
        bouton_tuto_on = lisImageBuf("Tuto_on");
        bouton_tuto_off = lisImageBuf("Tuto_off");
        bouton_options_echap = lisImageBuf("Options");
        bouton_options_echap_select = lisImageBuf("Options_select");
        menu_options = lisImageBuf("Menu_options");
        menu_dark_filter = lisImageBuf("Menu_dark_filter");
        fenetre_score_2 = lisImageBuf("fenetre_score_2");
        fenetre_score_3 = lisImageBuf("fenetre_score_3");
        fenetre_score_4 = lisImageBuf("fenetre_score_4");
        timer = lisImageBuf("Timer");
        joueur_courant = lisImageBuf("Joueur_courant");
        echap_button = lisImageBuf("Menu/Options");
        return new JFrame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                panelMenu.setBounds(0, 0, getWidth(), getHeight());
                if(panelPlateau !=null) panelPlateau.setBounds(0, 0, getWidth(), getHeight());
                if(panelVignette !=null) panelVignette.setBounds(0, 0, getWidth(), getHeight());
                if(buttonPanel!=null) buttonPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        };
    }

    private void afficheMessageErreur(Graphics g) {
        Font font = new Font("Bookman Old Style", Font.BOLD, (int) (hauteurMessageErreur*0.6));
        g.setFont(font);
        g.setColor(Color.WHITE);
        String message=null;
        int indexErreur = panelPlateau.getIndexMessageErreur();
        if(indexErreur==1) message = "Vous avez atteint la limite du plateau";
        if(indexErreur==2) message = "Vous avez atteint la hauteur maximale";
        if(indexErreur==3) message = "Le volcan doit etre sur un autre volcan";
        if(indexErreur==4) message = "Vous ne pouvez pas superposer 2 tuiles";
        if(indexErreur==5) message = "Vous ne pouvez pas ecraser un temple";
        if(indexErreur==6) message = "Vous ne pouvez pas ecraser une tour";
        if(indexErreur==7) message = "Vous ne pouvez pas ecraser un village entier";
        if(indexErreur==8) message = "Un bout de la tuile est dans le vide";
        if(indexErreur==9) message = "Vous ne touchez pas l'ile principale";
        if(indexErreur==10) message = "Vous devez placer la tuile au centre";

        if(message!=null){
            largeurMessageErreur = (int)(message.length()*font.getSize()*0.55);
            posX_messageErreur = posX_messageErreur-(int)(largeurMessageErreur*0.33);
            g.drawImage(applyColorFilter(joueur_courant,(byte) 9),posX_messageErreur,posY_messageErreur,largeurMessageErreur,hauteurMessageErreur,null);
            g.drawString(message,(int) (posX_messageErreur+(largeurMessageErreur/13)), posY_messageErreur+(int)(hauteurMessageErreur/1.5));
            if(panelPlateau.getTimerValue()>=4000){
                panelPlateau.resetIndexMessageErreur();
            }
        }
    }

    public static void afficheTuilePiochee(Graphics g) {
        if(jeu.getEstPiochee())
            g.drawImage(tuile_piochee[indice_tuilePiochee], posX_tuilePiochee, posY_tuilePiochee, largeur_tuilePiochee, largeur_tuilePiochee, null);
    }

    public static void afficheTimer(Graphics g) {
        if(jeu.getTimerActif() && jeu.getJoueurCourantClasse().getTypeJoueur()==Joueur.IA) {
            g.drawImage(timer, posX_timer, posY_timer, largeur_timer, hauteur_timer, null);
            Font font = new Font("Bookman Old Style", Font.BOLD, 29);
            g.setFont(font);
            g.setColor(Color.WHITE);
            double tempsEcoule = System.currentTimeMillis() - jeu.getJoueurCourant().getTempsTemp();
            if (tempsEcoule >= 20000000) {
                tempsEcoule = 0.0;
            }
            double tempsArrondi = tempsEcoule / 1000;// Convertir en secondes
            tempsArrondi = Math.round(tempsArrondi * 10) / 10.0;// Arrondir au dixième
            if (tempsArrondi <= jeu.getTempsTour()) {
                tempsFixe = String.valueOf(tempsArrondi);
            }
            if (tempsArrondi >= jeu.getTempsTour() * 0.75) {
                g.setColor(Color.RED);
                g.drawString(tempsFixe, (int) (posX_timer + largeur_timer * 0.15), (int) (posY_timer + hauteur_timer * 0.65));
                g.drawImage(chronoRouge, posX_chrono, posY_chrono, largeur_chrono, largeur_chrono, null);
            } else {
                g.setColor(Color.WHITE);
                g.drawString(tempsFixe, (int) (posX_timer + largeur_timer * 0.15), (int) (posY_timer + hauteur_timer * 0.65));
                g.drawImage(chronoBleu, posX_chrono, posY_chrono, largeur_chrono, largeur_chrono, null);
            }
            g.drawImage(chrono[indice_chrono], posX_aiguille, posY_aiguille, largeur_aiguille, largeur_aiguille, null);
        }
    }


    public static void afficheFenetreScore(Graphics g) {
        if(!estFenetreScoreChargee) {
            Color couleur_0 = null, couleur_1 = null, couleur_2 = null, couleur_3 = null;
            int nb_joueurs = jeu.getJoueurs().length;
            fenetre_score_courante = fenetre_score_2;
            couleur_0 = jeu.getJoueurs()[0].getCouleur();
            couleur_1 = jeu.getJoueurs()[1].getCouleur();
            if(couleur_0 == Color.RED){
                hutte_j0 = huttes_rouges[0];
                tour_j0 = tours_rouges[0];
                temple_j0 = temples_rouges[0];
            }else if(couleur_0 == Color.BLUE) {
                hutte_j0 = huttes_bleues[0];
                tour_j0 = tours_bleues[0];
                temple_j0 = temples_bleus[0];
            }else if(couleur_0 == Color.GREEN) {
                hutte_j0 = huttes_vertes[0];
                tour_j0 = tours_vertes[0];
                temple_j0 = temples_verts[0];
            }else{
                hutte_j0 = huttes_violettes[0];
                tour_j0 = tours_violettes[0];
                temple_j0 = temples_violets[0];
            }
            if(couleur_1 == Color.RED) {
                hutte_j1 = huttes_rouges[0];
                tour_j1 = tours_rouges[0];
                temple_j1 = temples_rouges[0];
            }else if(couleur_1 == Color.BLUE) {
                hutte_j1 = huttes_bleues[0];
                tour_j1 = tours_bleues[0];
                temple_j1 = temples_bleus[0];
            }else if(couleur_1 == Color.GREEN) {
                hutte_j1 = huttes_vertes[0];
                tour_j1 = tours_vertes[0];
                temple_j1 = temples_verts[0];
            }else{
                hutte_j1 = huttes_violettes[0];
                tour_j1 = tours_violettes[0];
                temple_j1 = temples_violets[0];
            }
            if(nb_joueurs>=3){
                couleur_2 = jeu.getJoueurs()[2].getCouleur();
                if(couleur_2 == Color.RED) {
                    hutte_j2 = huttes_rouges[0];
                    tour_j2 = tours_rouges[0];
                    temple_j2 = temples_rouges[0];
                }else if(couleur_2 == Color.BLUE) {
                    hutte_j2 = huttes_bleues[0];
                    tour_j2 = tours_bleues[0];
                    temple_j2 = temples_bleus[0];
                }else if(couleur_2 == Color.GREEN) {
                    hutte_j2 = huttes_vertes[0];
                    tour_j2 = tours_vertes[0];
                    temple_j2 = temples_verts[0];
                }else{
                    hutte_j2 = huttes_violettes[0];
                    tour_j2 = tours_violettes[0];
                    temple_j2 = temples_violets[0];
                }
                fenetre_score_courante = fenetre_score_3;
            }if(nb_joueurs==4){
                couleur_3 = jeu.getJoueurs()[3].getCouleur();
                if(couleur_3 == Color.RED) {
                    hutte_j3 = huttes_rouges[0];
                    tour_j3 = tours_rouges[0];
                    temple_j3 = temples_rouges[0];
                }else if(couleur_3 == Color.BLUE) {
                    hutte_j3 = huttes_bleues[0];
                    tour_j3 = tours_bleues[0];
                    temple_j3 = temples_bleus[0];
                }else if(couleur_3 == Color.GREEN) {
                    hutte_j3 = huttes_vertes[0];
                    tour_j3 = tours_vertes[0];
                    temple_j3 = temples_verts[0];
                }else{
                    hutte_j3 = huttes_violettes[0];
                    tour_j3 = tours_violettes[0];
                    temple_j3 = temples_violets[0];
                }
                fenetre_score_courante = fenetre_score_4;
            }
            estFenetreScoreChargee = true;
        }
        g.drawImage(fenetre_score_courante, posX_fenetre_score, posY_fenetre_score, largeur_fenetre_score, hauteur_fenetre_score, null);
        //prénom des joueurs 0 et 1
        Font font = new Font("Bookman Old Style", Font.BOLD, 29);
        g.setFont(font);

        String joueur_0 = jeu.getJoueurs()[0].getPrenom();
        Color couleur_0 = jeu.getJoueurs()[0].getCouleur();
        if(couleur_0==Color.BLUE) g.setColor(couleur_bleue);
        else g.setColor(couleur_0);
        g.drawString(joueur_0, posX_prenom, posY_prenom_j0);

        String joueur_1 = jeu.getJoueurs()[1].getPrenom();
        Color couleur_1 = jeu.getJoueurs()[1].getCouleur();
        if(couleur_1==Color.BLUE) g.setColor(couleur_bleue);
        else g.setColor(couleur_1);
        g.drawString(joueur_1, posX_prenom, posY_prenom_j1);

        //nombre de huttes, tours, temples des joueurs 0 et 1
        font = new Font("Bookman Old Style", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawImage(hutte_j0, posX_huttes_score, posY_huttes_score_j0, largeur_hutte_score, largeur_hutte_score, null);
        g.drawImage(tour_j0, posX_tours_score, posY_tours_score_j0, largeur_tour_score, largeur_tour_score, null);
        g.drawImage(temple_j0, posX_temples_score, posY_temples_score_j0, largeur_temple_score, largeur_temple_score, null);
        String huttes_j0 = Integer.toString(jeu.getJoueurs()[0].getNbHuttes());
        g.drawString(huttes_j0, posX_huttes, posY_scores_j0);
        String tours_j0 = Integer.toString(jeu.getJoueurs()[0].getNbTours());
        g.drawString(tours_j0, posX_tours, posY_scores_j0);
        String temples_j0 = Integer.toString(jeu.getJoueurs()[0].getNbTemples());
        g.drawString(temples_j0, posX_temples, posY_scores_j0);
        g.drawImage(hutte_j1, posX_huttes_score, posY_huttes_score_j1, largeur_hutte_score, largeur_hutte_score, null);
        g.drawImage(tour_j1, posX_tours_score, posY_tours_score_j1, largeur_tour_score, largeur_tour_score, null);
        g.drawImage(temple_j1, posX_temples_score, posY_temples_score_j1, largeur_temple_score, largeur_temple_score, null);
        String huttes_j1 = Integer.toString(jeu.getJoueurs()[1].getNbHuttes());
        g.drawString(huttes_j1, posX_huttes, posY_scores_j1);
        String tours_j1 = Integer.toString(jeu.getJoueurs()[1].getNbTours());
        g.drawString(tours_j1, posX_tours, posY_scores_j1);
        String temples_j1 = Integer.toString(jeu.getJoueurs()[1].getNbTemples());
        g.drawString(temples_j1, posX_temples, posY_scores_j1);

        if(jeu.getNbJoueurs()>=3){
            //prénom du joueur 2
            font = new Font("Bookman Old Style", Font.BOLD, 29);
            g.setFont(font);
            String joueur_2 = jeu.getJoueurs()[2].getPrenom();
            Color couleur_2 = jeu.getJoueurs()[2].getCouleur();
            if(couleur_2==Color.BLUE) g.setColor(couleur_bleue);
            else g.setColor(couleur_2);
            g.drawString(joueur_2, posX_prenom, posY_prenom_j2);
            //nombre de huttes, tours, temples du joueur 2
            font = new Font("Bookman Old Style", Font.BOLD, 20);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawImage(hutte_j2, posX_huttes_score, posY_huttes_score_j2, largeur_hutte_score, largeur_hutte_score, null);
            g.drawImage(tour_j2, posX_tours_score, posY_tours_score_j2, largeur_tour_score, largeur_tour_score, null);
            g.drawImage(temple_j2, posX_temples_score, posY_temples_score_j2, largeur_temple_score, largeur_temple_score, null);
            String huttes_j2 = Integer.toString(jeu.getJoueurs()[2].getNbHuttes());
            g.drawString(huttes_j2, posX_huttes, posY_scores_j2);
            String tours_j2 = Integer.toString(jeu.getJoueurs()[2].getNbTours());
            g.drawString(tours_j2, posX_tours, posY_scores_j2);
            String temples_j2 = Integer.toString(jeu.getJoueurs()[2].getNbTemples());
            g.drawString(temples_j2, posX_temples, posY_scores_j2);
        }
        if(jeu.getNbJoueurs()==4){
            //prénom du joueur 3
            font = new Font("Bookman Old Style", Font.BOLD, 29);
            g.setFont(font);
            String joueur_3 = jeu.getJoueurs()[3].getPrenom();
            Color couleur_3 = jeu.getJoueurs()[3].getCouleur();
            if(couleur_3==Color.BLUE) g.setColor(couleur_bleue);
            else g.setColor(couleur_3);
            g.drawString(joueur_3, posX_prenom, posY_prenom_j3);
            //nombre de huttes, tours, temples du joueur 3
            font = new Font("Bookman Old Style", Font.BOLD, 20);
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawImage(hutte_j3, posX_huttes_score, posY_huttes_score_j3, largeur_hutte_score, largeur_hutte_score, null);
            g.drawImage(tour_j3, posX_tours_score, posY_tours_score_j3, largeur_tour_score, largeur_tour_score, null);
            g.drawImage(temple_j3, posX_temples_score, posY_temples_score_j3, largeur_temple_score, largeur_temple_score, null);
            String huttes_j3 = Integer.toString(jeu.getJoueurs()[3].getNbHuttes());
            g.drawString(huttes_j3, posX_huttes, posY_scores_j3);
            String tours_j3 = Integer.toString(jeu.getJoueurs()[3].getNbTours());
            g.drawString(tours_j3, posX_tours, posY_scores_j3);
            String temples_j3 = Integer.toString(jeu.getJoueurs()[3].getNbTemples());
            g.drawString(temples_j3, posX_temples, posY_scores_j3);
        }
        //pioche
        font = new Font("Bookman Old Style", Font.BOLD, 28);
        g.setFont(font);
        g.setColor(Color.BLACK);
        String nb_tuiles_pioche = Integer.toString(jeu.pioche.size());
        g.drawString(nb_tuiles_pioche, posX_nb_tuiles_pioche, posY_nb_tuiles_pioche);
        if(jeu.getPioche().size() > jeu.getTaillePioche()*0.75) {
            g.drawImage(pioche[8], posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche, null);
        }else if(jeu.getPioche().size() > jeu.getTaillePioche()*0.50) {
            g.drawImage(pioche[7], posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche, null);
        }else if(jeu.getPioche().size() > jeu.getTaillePioche()*0.33) {
            g.drawImage(pioche[6], posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche, null);
        }else{
            if(jeu.getPioche().size() > 6){
                g.drawImage(pioche[5], posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche, null);
            }else{
                if(jeu.getPioche().size()>0)
                    g.drawImage(pioche[jeu.getPioche().size()-1], posX_pioche, posY_pioche, largeur_pioche, hauteur_pioche, null);
            }
        }
    }

    public static void afficheFinPartie(Graphics g) {
        if(select_fin_partie){
            //System.out.println("afficheFinPartie");
            if(!ecran_fin_partie) {//évite de recalculer tous les scores des joueurs à chaque actualisation de l'écran
                tempsFinPartie = System.currentTimeMillis();
                tempsPartie = tempsFinPartie - tempsDebutPartie;
                ecran_fin_partie = true;
                ArrayList<Joueur> joueurs_copie = new ArrayList<>();
                joueurs_tries = new ArrayList<>();
                for (int i = 0; i < jeu.getJoueurs().length; i++) {
                    joueurs_copie.add(jeu.getJoueurs()[i]);
                }
                int score_meilleur = Integer.MIN_VALUE;
                int indice_meilleur_joueur = -1;
                int score_courant;
                while (joueurs_copie.size() > 1) {
                    for (int i = 0; i < joueurs_copie.size(); i++) {
                        score_courant = joueurs_copie.get(i).calculScore();
                        if (score_courant > score_meilleur) {
                            score_meilleur = score_courant;
                            indice_meilleur_joueur = i;
                        }
                    }
                    joueurs_tries.add(joueurs_copie.get(indice_meilleur_joueur));
                    joueurs_copie.remove(indice_meilleur_joueur);
                    indice_meilleur_joueur = -1;
                    score_meilleur = Integer.MIN_VALUE;
                }
                joueurs_tries.add(joueurs_copie.get(0));
            }
            g.drawImage(finPartie, posX_finPartie, posY_finPartie, largeur_fin_partie, hauteur_fin_partie, null);
            Font font = new Font("Bookman Old Style", Font.BOLD, taille_texte_finPartie);
            g.setFont(font);
            g.setColor(Color.BLACK);
            for(int i=0; i<joueurs_tries.size(); i++){
                Color couleur_courante = joueurs_tries.get(i).getCouleur();
                BufferedImage img = null;
                if(couleur_courante.equals(Color.RED)){
                    img = cadreRouge;
                }else if(couleur_courante.equals(Color.BLUE)){
                    img = cadreBleu;
                }else if(couleur_courante.equals(Color.GREEN)){
                    img = cadreVert;
                }else if(couleur_courante.equals(Color.MAGENTA)){
                    img = cadreViolet;
                }
                g.drawImage(img, posX_cadre, posY_cadre+decalageY_cadre*i, largeur_cadre, hauteur_cadre, null);
                Joueur joueur_courant = joueurs_tries.get(i);
                String joueur = joueur_courant.getPrenom();
                String nb_huttes = Integer.toString(joueur_courant.getNbHuttesPlacees());
                String nb_temples = Integer.toString(joueur_courant.getNbTemplesPlaces());
                String nb_tours = Integer.toString(joueur_courant.getNbToursPlacees());
                String score = Integer.toString(joueur_courant.calculScore());
                g.drawString(joueur, posX_joueur_finPartie, posY_joueur_finPartie+decalageY_joueur*i);
                g.drawString(nb_huttes, posX_huttes_finPartie, posY_joueur_finPartie+decalageY_joueur*i);
                g.drawString(nb_temples, posX_temples_finPartie, posY_joueur_finPartie+decalageY_joueur*i);
                g.drawString(nb_tours, posX_tours_finPartie, posY_joueur_finPartie+decalageY_joueur*i);
                g.drawString(score, posX_score_finPartie, posY_joueur_finPartie+decalageY_joueur*i);
            }
            int minutes = (int) ((int) tempsPartie/60000.0);
            int secondes = (int) ((int) (tempsPartie-(double)minutes*60000.0)/1000.0);
            String temps_minutes = Integer.toString(minutes);
            String temps_secondes = Integer.toString(secondes);
            String zero_1 = "0";String zero_2 = "0";
            if(minutes>9){
                zero_1 = "";
            }
            if(secondes>9){
                zero_2 = "";
            }
            g.drawString(zero_1+temps_minutes+":"+zero_2+temps_secondes, posX_temps_partie, posY_temps_partie);
            if(select_retour)
                g.drawImage(bouton_retour_select, posX_retour_finPartie, posY_retour_finPartie, largeur_retour_finPartie, hauteur_retour_finPartie,null);
            else
                g.drawImage(bouton_retour, posX_retour_finPartie, posY_retour_finPartie, largeur_retour_finPartie, hauteur_retour_finPartie,null);;
            }
    }

    public static void afficheMenuOptions(Graphics g) {
        if(clicBoutonPauseEchap){
            g.drawImage(menu_dark_filter, 0, 0, 3000, 3000, null);
            g.drawImage(menu_options, posX_menu_options, posY_menu_options, largeur_menu_options, hauteur_menu_options, null);
            afficheBoutonRetour(g);
            afficheBoutonLoad(g);
            afficheBoutonSave(g);
            afficheBoutonParametres(g);
            afficheBoutonQuitter(g);
        }
    }

    public static void afficheBoutonOptionsEchap(Graphics g) {//pendant le jeu
        if(select_menu_options)
            g.drawImage(bouton_options_echap_select, posX_options_echap, posY_options_echap, largeur_bouton, largeur_bouton, null);
        else
            g.drawImage(bouton_options_echap, posX_options_echap, posY_options_echap, largeur_bouton, largeur_bouton, null);
    }

    public static void afficheBoutonRetour(Graphics g) {
        if(select_retour)
            g.drawImage(bouton_retour_select, posX_save, posY_retour, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_retour, posX_save, posY_retour, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);;
    }

    public static void afficheBoutonSave(Graphics g) {
        if(select_save)
            g.drawImage(bouton_save_select, posX_save, posY_save, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_save, posX_save, posY_save, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonLoad(Graphics g) {
        if(select_load)
            g.drawImage(bouton_load_select, posX_save, posY_load, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_load, posX_save, posY_load, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonParametres(Graphics g) {//menu principal
        if(select_parametres)
            g.drawImage(bouton_parametres_select, posX_save, posY_parametres, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_parametres, posX_save, posY_parametres, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonQuitter(Graphics g) {
        if(select_quitter)
            g.drawImage(bouton_quitter_select, posX_save, posY_quitter, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_quitter, posX_save, posY_quitter, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);;
    }

    public static void afficheBoutonAnnuler(Graphics g) {//pendant le jeu
        if(select_annuler)
            g.drawImage(bouton_annuler_select, posX_annuler, posY_annuler, largeur_bouton, largeur_bouton,null);
        else
            g.drawImage(bouton_annuler,  posX_annuler, posY_annuler, largeur_bouton, largeur_bouton,null);
    }

    public static void afficheBoutonRefaire(Graphics g) {//pendant le jeu
        if(select_refaire)
            g.drawImage(bouton_refaire_select, posX_refaire, posY_refaire, largeur_bouton, largeur_bouton,null);
        else
            g.drawImage(bouton_refaire, posX_refaire, posY_refaire,  largeur_bouton, largeur_bouton,null);
    }

    public static void afficheBoutonTuto(Graphics g) {
        if(tuto_on)
            g.drawImage(bouton_tuto_on, posX_tuto, posY_tuto, (int) (largeur_bouton * 1.6), (int) (hauteur_bouton * 1.6), null);
        else
            g.drawImage(bouton_tuto_off, posX_tuto, posY_tuto, (int) (largeur_bouton * 1.6), (int) (hauteur_bouton * 1.6),null);
    }

    public static void annuler() throws CloneNotSupportedException {
        jeu.annuler();
    }

    public static void refaire(){
        jeu.refaire();
    }

    public static void afficheJoueurCourant(Graphics g) {
        //Font font = new Font("Bookman Old Style", Font.BOLD, 29);
        Font font = customFont.deriveFont(Font.PLAIN,29);
        g.setFont(font);
        Color couleur_joueur = jeu.getJoueurCourant().getCouleur();
        if(couleur_joueur == Color.BLUE) couleur_joueur = couleur_bleue;
            g.setColor(couleur_joueur);
        int longueur_prenom = jeu.getPrenomJoueurCourant().length();
        int decalage = longueur_prenom*8;
        g.drawImage(joueur_courant, posX_joueur_courant, posY_joueur_courant, largeur_joueur_courant, hauteur_joueur_courant, null);
        g.drawString(jeu.getPrenomJoueurCourant(), (int) (posX_joueur_courant+largeur_joueur_courant/2-decalage), posY_joueur_courant+hauteur_joueur_courant/2+6);
    }

    public void metAjour(){
        repaint();
    }

    public static void sauvegarder(int sauvegarde) {
        try {
            File fichier;
            SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
            String nom =DOSSIER + File.separator+sauvegarde+"_" +"Partie_du_"+date.format(new Date())+".sauvegarde";
            if (3< sauvegardes.length) {
                fichier = new File(DOSSIER + File.separator + sauvegardes[sauvegarde]);
                fichier.delete();
            }

            fichier = new File(nom);
            fichier.createNewFile();
            FileOutputStream fichierOut = new FileOutputStream(fichier);
            ObjectOutputStream out = new ObjectOutputStream(fichierOut);
            out.writeObject(jeu);
            out.writeObject(jeu.getJoueurs());
            out.close();
            fichierOut.close();
            File dossier = new File(DOSSIER);
            sauvegardes = dossier.list();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de sauvegarder cette partie.\n" + e);
        }
        //TODO reduire les box des save.
    }

    public Jeu getJeu(){
        return jeu;
    }

    public static void charger(int sauvegarde) {
        try {

            if(sauvegarde>=sauvegardes.length){
                return;
            }
            else {
                FileInputStream fichier = new FileInputStream(DOSSIER + File.separator + sauvegardes[sauvegarde]);
                ObjectInputStream in = new ObjectInputStream(fichier);
                Jeu jeu1 = (Jeu) in.readObject();
                jeu.setJeu(jeu1);
                in.close();
                fichier.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger cette sauvegarde.\n" + e);
        }
    }


    public void afficheParametre(Graphics g){
        int taille_x = (int) (Math.min(frame.getWidth(),frame.getHeight())*1.3);
        int taille_y = (int) (taille_x/1.25);
        int x = (frame.getWidth() - taille_x)/2;
        int y=0-(taille_y/45);
        g.drawImage(options_background, x, y, taille_x,taille_y,null);
        afficheSliders(g);
        afficheCochable(g);
        afficheChoix(g);
    }
    public void afficheSave(Graphics g){
        g.drawImage(menu_sauvegarde_chargement, posX_optionSave,posY_optionSave , largeur_optionSave,hauteur_optionSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*1,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*2,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*3,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(btn_annuler,posX_btnAnnulerSave,posY_btnAnnulerSave,largeur_bouton,largeur_bouton,null);
        g.setColor(Color.BLACK);
        for(int i=0 ; i<sauvegardes.length;i++) {
            g.drawString(FenetreJeu.sauvegardes[i],texte_saveX,texte_savey+i*decalageY_save);
        }
        for(int j=sauvegardes.length ;j<4;j++){
            g.drawString("VIDE",texte_saveXvide,texte_savey+j*decalageY_save);
        }
    }

    public void afficheLoad(Graphics g){
        g.drawImage(menu_sauvegarde_chargement, posX_optionSave,posY_optionSave , largeur_optionSave,hauteur_optionSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*1,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*2,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(image_save,posX_optionSaveBouton,posY_optionSaveBouton+decalageY_save*3,largeur_boutonSave,hauteur_boutonSave,null);
        g.drawImage(btn_annuler,posX_btnAnnulerSave,posY_btnAnnulerSave,largeur_bouton,largeur_bouton,null);
        g.setColor(Color.BLACK);
        for(int i=0 ; i<sauvegardes.length;i++){
            g.drawString(FenetreJeu.sauvegardes[i],texte_saveX,texte_savey+i*decalageY_save);
        }
        for(int j=sauvegardes.length ;j<4;j++){
            g.drawString("VIDE",texte_saveXvide,texte_savey+j*decalageY_save);
        }

    }

    private void afficheSliders(Graphics g){
        int taille_slider_x = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.08);
        taille_btnParametre = taille_btn;
        int taille_sons = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.1);
        int taille_musiques = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.2);

        int x = (frame.getWidth() - taille_slider_x)/2;
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
        int taille_slider_x = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.08);
        int taille_pleinecran = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.25);

        int x = (frame.getWidth() - taille_slider_x)/2;
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
        int taille_slider_x = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.8);
        int taille_slider_y = taille_slider_x /10;
        int taille_btn = (int) (Math.min(frame.getWidth(),frame.getHeight())*0.12);


        int x = (frame.getWidth() - taille_slider_x)/2;
        int y=(int) (taille_slider_y*4.25);

        posX_btnValider = (int)(x+taille_slider_x*1.05);
        posY_btnChoix = (int) (y+taille_slider_y*4.75);
        posX_btnAnnuler = x-(taille_slider_x)/6;

        if(select_annuler2) g.drawImage(btn_annuler_hover,posX_btnAnnuler,posY_btnChoix,taille_btn,taille_btn,null);
        else g.drawImage(btn_annuler,posX_btnAnnuler,posY_btnChoix,taille_btn,taille_btn,null);
        if(select_valider) g.drawImage(btn_valider_hover,posX_btnValider,posY_btnChoix,taille_btn,taille_btn,null);
        else g.drawImage(btn_valider,posX_btnValider,posY_btnChoix,taille_btn,taille_btn,null);
    }

    public void loadImageOption() throws IOException {
        options_background = lisImage("/Options/Options_background");
        for(int i=0; i < sliders.length;i++){
            sliders[i] = lisImage("/Options/Sliders/slider_"+i);
        }
        image_save =lisImageBuf("bouton_image_save");
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
        bouton_droit_hover = applyRedFilter(bouton_droit);
        bouton_gauche_hover = applyRedFilter(bouton_gauche);
        btn_valider_hover = applyRedFilter(btn_valider);
        btn_annuler_hover = applyRedFilter(btn_annuler);
        coche_non_hover = applyRedFilter(coche_non);
        coche_oui_hover = applyRedFilter(coche_oui);
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

    public void initialiseSons(){
        MusicPlayer clicBouton = new MusicPlayer("Musiques/clicBouton.wav");
        sonPlayer.add(clicBouton);
        MusicPlayer selectionBouton = new MusicPlayer("Musiques/selectionBouton.wav");
        sonPlayer.add(selectionBouton);
        MusicPlayer confirmerBouton = new MusicPlayer("Musiques/confirmerBouton.wav");
        sonPlayer.add(confirmerBouton);
        MusicPlayer refuser = new MusicPlayer("Musiques/refuser.wav");
        sonPlayer.add(refuser);
    }

    public void playSons(int indexAJouer){
        if(indexAJouer==3 && retourDebug) return;
        int sonVolume;
        if (index_son == 0) sonVolume = -100000;
        else sonVolume = (-30) + index_son * 17;
        MusicPlayer sonCourant = sonPlayer.get(indexAJouer);
        if(indexAJouer==0){
            sonCourant.setVolume(-25+sonVolume);
        }else{
            sonCourant.setVolume(-20+sonVolume);
        }
        sonCourant.resetClip();
        sonCourant.setVolume(sonVolume);
        sonCourant.play();
    }

    public void metAJour(){
        repaint();
    }

    public void boucle(){
        Timer timer = new Timer(10, new ActionListener() {// ici
            @Override
            public void actionPerformed(ActionEvent e) {
                metAJour();
                //animation chronomètre
                if(jeu.getTimerActif()) {
                    double tempsTour = jeu.getTempsTour();
                    double tempsEcoule = (System.currentTimeMillis() - jeu.getJoueurCourant().getTempsTemp()) / 1000;// Convertir en secondes;
                    int indice_chrono_2 = (int) (nb_aiguilles / tempsTour * tempsEcoule);
                    if (indice_chrono_2 < nb_aiguilles) {
                        indice_chrono = indice_chrono_2;
                    } else {
                        indice_chrono = 0;
                    }
                }
                if(jeu.getEstPiochee()) {
                    if(!estImageTuilePiocheeFinale){
                        posX_tuilePiochee += largeur_fenetre_score*0.003;
                        posY_tuilePiochee += largeur_fenetre_score*0.02;
                        largeur_tuilePiochee = (int) Math.min(largeur_tuilePiochee + largeur_fenetre_score*0.05,largeur_fenetre_score*0.5);
                        if(indice_tuilePiochee == taille_tuile_piochee-1){
                            estImageTuilePiocheeFinale = true;
                        }else{
                            indice_tuilePiochee ++;
                        }
                    }
                }else{//position de départ
                    estImageTuilePiocheeFinale = false;
                    posX_tuilePiochee = posX_tuilePiochee_init;
                    posY_tuilePiochee = posY_tuilePiochee_init;
                    largeur_tuilePiochee = largeur_tuilePiochee_init;
                    indice_tuilePiochee = 0;
                }
            }
        });
        timer.start();
    }
}