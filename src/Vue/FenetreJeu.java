package Vue;

import Controleur.ControleurMediateur;
import Modele.Jeu.Jeu;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static Vue.ImageLoader.*;


public class FenetreJeu extends Container {
    FenetreJeuListener listener;
    public PanelPlateau panelPlateau;

    Graphics g1;
    public PanelMenu panelMenu;
    public PanelVignette panelVignette;
    public JPanel backgroundPanel, buttonPanel;
    public JLayeredPane layeredPane;

    final ControleurMediateur controleur;

    public static Point LastPosition;
    public static Jeu jeu;
    final JFrame frame;

    public FenetreJeu(Jeu jeu, ControleurMediateur controleur) throws IOException {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        FenetreJeu.jeu = jeu;
        this.frame = getFMenu();
        initFrame();
        initLayeredPanel();
        initMenu();
        initPanels(controleur);
        initKeyBoardAndMouseListener();
        setBackgroundColor();
        select_menu_options = false;
        frame.setVisible(true);
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

    public void initRenduJeu(){
        jeu.initPartie();
        initFrame();
        initPanels(controleur);
        initKeyBoardAndMouseListener();
        setBackgroundColor();
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

    private void initBackgroundPanel(){
        createBackgroundPanel();
        //backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        //backgroundPanel.setOpaque(false);
        layeredPane.add(backgroundPanel, JLayeredPane.FRAME_CONTENT_LAYER);
    }

    private void createBackgroundPanel() {
        backgroundPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                //super.paint(g);
                //afficheBackground(g);
            }
        };
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
                afficheBoutonOptions(g2d);
                afficheMessageErreur(g2d);
                afficheBoutonAnnuler(g2d);
                afficheBoutonRefaire(g2d);
                afficheJoueurCourant(g2d);
                afficheMenuOptions(g2d);
            }

            private void calculeRapports() {
                largeur = getWidth();
                hauteur = getHeight();
                double rapport = 492.0 / 847.0;
                double rapport_fenetre_score = 621.0/533.0;
                double rapport_joueur_courant = 131.0/603.0;
                double rapport_bouton_dans_options = 207.0/603.0;
                //boutons général
                largeur_bouton = Math.min(Math.max(Math.min(largeur / 9, hauteur / 9), 80), 190);
                hauteur_bouton = (int) (largeur_bouton * rapport);
                posX_boutons = (int) (largeur * 0.97 - largeur_bouton);
                posY_annuler = hauteur_bouton + hauteur_bouton / 5;
                posY_options = 0;
                //menu d'options
                largeur_menu_options = (int) Math.min(Math.max(Math.min(largeur*0.8, hauteur*0.8), 400), 800);
                hauteur_menu_options = largeur_menu_options;
                posX_menu_options = (largeur - largeur_menu_options)/2;
                posY_menu_options = (hauteur - hauteur_menu_options)/2;
                largeur_bouton_dans_options = (int) (largeur_menu_options * 0.4);
                hauteur_bouton_dans_options = (int) (largeur_bouton_dans_options * rapport_bouton_dans_options);
                posX_save = posX_menu_options + largeur_menu_options/2 - largeur_bouton_dans_options/2;
                posY_save = (int) (posY_menu_options + hauteur_menu_options * 0.15);
                posY_load = (int) (posY_save + hauteur_menu_options * 0.20);
                posY_tuto = (int) (posY_load + hauteur_menu_options * 0.20);
                posY_quitter = (int) (posY_tuto + hauteur_menu_options * 0.20);
                //fenêtre de score
                largeur_fenetre_score = (int) (largeur_bouton * 3.6);
                hauteur_fenetre_score = (int) (largeur_fenetre_score * rapport_fenetre_score);
                largeur_joueur_courant = (int) (largeur_bouton * 1.8);
                hauteur_joueur_courant = (int) (largeur_joueur_courant * rapport_joueur_courant);
                posX_fenetre_score = 2;
                posY_fenetre_score = 2;
                posX_prenom_j0 = (int) (posX_fenetre_score + largeur_fenetre_score*0.18);
                posX_prenom_j1 = posX_prenom_j0;
                posY_prenom_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.16);
                posY_prenom_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.46);
                posX_huttes = (int) (posX_fenetre_score + largeur_fenetre_score*0.29);
                posX_tours = (int) (posX_fenetre_score + largeur_fenetre_score*0.515);
                posX_temples = (int) (posX_fenetre_score + largeur_fenetre_score*0.77);
                posY_scores_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.30);
                posY_scores_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.62);
                posX_pioche = (int) (posX_fenetre_score + largeur_fenetre_score*0.69);
                posY_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.84);
                posX_joueur_courant = (largeur/2 - largeur_joueur_courant/2);
                posY_joueur_courant = posY_annuler;
                posY_refaire = posY_annuler + hauteur_bouton + hauteur_bouton / 5;
                posX_tuile_derriere = (int) (posX_fenetre_score + largeur_fenetre_score*0.25);
                posY_tuile_derriere = (int) (posY_fenetre_score + hauteur_fenetre_score*0.72);
                largeur_tuile = (int) (largeur_fenetre_score*0.20);
                hauteur_tuile = largeur_tuile;
                //message d'erreur
                posX_messageErreur = (int) (largeur * 0.5 - largeur_bouton);
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Définit la taille de la fenêtre à 60% de la taille de l'écran
        frame.setSize(screenSize.width * 6 / 10, screenSize.height * 6 / 10);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    public JFrame getFMenu(){
        bouton_save = lisImageBuf("Sauvegarder");
        bouton_load = lisImageBuf("Charger");
        bouton_quitter = lisImageBuf("Quitter");
        bouton_annuler = lisImageBuf("Annuler");
        //bouton_load_select = lisImageBuf("Charger_select");
        bouton_annuler = lisImageBuf("Annuler");
        //bouton_annuler_select = lisImageBuf("Annuler_select");
        bouton_refaire = lisImageBuf("Refaire");
        //bouton_refaire_select = lisImageBuf("Refaire_select");
        bouton_tuto_on = lisImageBuf("Tuto_on");
        bouton_tuto_off = lisImageBuf("Tuto_off");
        bouton_options = lisImageBuf("Options");
        menu_options = lisImageBuf("Menu_options");
        fenetre_score = lisImageBuf("fenetre_score");
        joueur_courant = lisImageBuf("Joueur_courant");
        tuile_derriere = lisImageBuf("Tuile_derriere");
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


    public static void afficheFenetreScore(Graphics g) {
        g.drawImage(fenetre_score, posX_fenetre_score, posY_fenetre_score, largeur_fenetre_score, hauteur_fenetre_score, null);
        Font font = new Font("Bookman Old Style", Font.BOLD, 29);
        g.setFont(font);
        g.setColor(Color.RED);
        String joueur_0 = jeu.getJoueurs()[0].getPrenom();
        g.drawString(joueur_0, posX_prenom_j0, posY_prenom_j0);
        g.setColor(new Color(0, 128, 255));
        String joueur_1 = jeu.getJoueurs()[1].getPrenom();
        g.drawString(joueur_1, posX_prenom_j1, posY_prenom_j1);
        g.setColor(Color.WHITE);
        font = new Font("Roboto", Font.BOLD, 20);
        g.setFont(font);
        String huttes_j0 = Integer.toString(jeu.getJoueurs()[0].getNbHuttes());
        g.drawString(huttes_j0, posX_huttes, posY_scores_j0);
        String huttes_j1 = Integer.toString(jeu.getJoueurs()[1].getNbHuttes());
        g.drawString(huttes_j1, posX_huttes, posY_scores_j1);
        String tours_j0 = Integer.toString(jeu.getJoueurs()[0].getNbTours());
        g.drawString(tours_j0, posX_tours, posY_scores_j0);
        String tours_j1 = Integer.toString(jeu.getJoueurs()[1].getNbTours());
        g.drawString(tours_j1, posX_tours, posY_scores_j1);
        String temples_j0 = Integer.toString(jeu.getJoueurs()[0].getNbTemples());
        g.drawString(temples_j0, posX_temples, posY_scores_j0);
        String temples_j1 = Integer.toString(jeu.getJoueurs()[1].getNbTemples());
        g.drawString(temples_j1, posX_temples, posY_scores_j1);
        font = new Font("Roboto", Font.BOLD, 25);
        g.setFont(font);
        g.setColor(Color.BLACK);
        String nb_tuiles_pioche = Integer.toString(jeu.pioche.size());
        g.drawString(nb_tuiles_pioche, posX_pioche, posY_pioche);
        int decalage = 65;
        if(jeu.getPioche().size() <= 48) {
            for (int i = jeu.getPioche().size(); i>=0; i--) {
                g.drawImage(tuile_derriere, posX_tuile_derriere + decalage, posY_tuile_derriere, largeur_tuile, hauteur_tuile, null);
                decalage -= 2;
            }
        }
    }

    public static void afficheBackground(Graphics g) {
        System.out.println("afficheBackground");
        g.drawImage(background, 0, 0, largeur, hauteur, null);
    }

    public static void afficheMenuOptions(Graphics g) {
        if (select_menu_options) {
            g.drawImage(menu_options, posX_menu_options, posY_menu_options, largeur_menu_options, hauteur_menu_options, null);
            afficheBoutonLoad(g);
            afficheBoutonSave(g);
            afficheBoutonTuto(g);
            afficheBoutonQuitter(g);
        }
    }

    public static void afficheBoutonOptions(Graphics g) {
        if(select_options)
            g.drawImage(bouton_options_select, posX_boutons, posY_options, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_options, posX_boutons, posY_options, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheBoutonLoad(Graphics g) {
        if(select_load)
            g.drawImage(bouton_load_select, posX_save, posY_load, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_load, posX_save, posY_load, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonSave(Graphics g) {
        if(select_save)
            g.drawImage(bouton_save_select, posX_save, posY_save, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_save, posX_save, posY_save, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonTuto(Graphics g) {
        if(tuto_on)
            g.drawImage(bouton_tuto_on, posX_save, posY_tuto, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_tuto_off, posX_save, posY_tuto, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonQuitter(Graphics g) {
        if(select_quitter)
            g.drawImage(bouton_quitter_select, posX_save, posY_quitter, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
        else
            g.drawImage(bouton_quitter, posX_save, posY_quitter, largeur_bouton_dans_options, hauteur_bouton_dans_options,null);
    }

    public static void afficheBoutonAnnuler(Graphics g) {
        if(select_annuler)
            g.drawImage(bouton_annuler_select, posX_boutons, posY_annuler, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_annuler, posX_boutons, posY_annuler, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheBoutonRefaire(Graphics g) {
        if(select_refaire)
            g.drawImage(bouton_refaire_select, posX_boutons, posY_refaire, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_refaire, posX_boutons, posY_refaire, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheJoueurCourant(Graphics g) {
        Font font = new Font("Roboto", Font.BOLD, 20);
        g.setFont(font);
        g.drawImage(joueur_courant, posX_joueur_courant, posY_joueur_courant, largeur_joueur_courant, hauteur_joueur_courant, null);
        g.drawString(jeu.getPrenomJoueurCourant(), posX_joueur_courant+10, posY_joueur_courant+hauteur_joueur_courant/2+3);
    }

    public void metAjour(){
        repaint();
    }
}