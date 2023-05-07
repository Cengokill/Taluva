package Vue;

import Controleur.ControleurMediateur;
import Modele.ImageLoader;
import Modele.Jeu;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static Modele.ImageLoader.*;


public class FenetreJeu extends Container {
    FenetreListener listener;
    public AffichagePlateau affichagePlateau;

    Graphics g1;
    public MenuGraphique menuGraphique;
    public VignettePanel vignettePanel;
    public JPanel backgroundPanel, buttonPanel;
    public JLayeredPane layeredPane;

    final ControleurMediateur controleur;

    public static Point LastPosition;
    public static Jeu jeu;
    JFrame frame;

    public FenetreJeu(Jeu jeu, ControleurMediateur controleur) throws IOException {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        this.jeu = jeu;
        this.frame = getFMenu();
        initFrame();
        initMultiLayerPanel();
        initMenu();
        initPanels(controleur);
        initKeyBoardAndMouseListener();
        setBackgroundColor();
        frame.setVisible(true);


    }

    public void initMenuJeu() throws IOException {
        layeredPane.removeAll();
        initFrame();
        initMenu();

        buttonPanel.removeAll();
        affichagePlateau.removeAll();

        menuGraphique.setFenetre(this);
        menuGraphique.setBounds(0, 0, getWidth(), getHeight());
        initVignettePanel();

        layeredPane.revalidate();
        layeredPane.repaint();
        frame.revalidate();
        frame.repaint();
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
        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void initMenu() throws IOException {
        // Ajouter les tuiles hexagonales
        menuGraphique = new MenuGraphique(frame,layeredPane,jeu,controleur);
        menuGraphique.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(menuGraphique, JLayeredPane.DEFAULT_LAYER);
    }

    private void initKeyBoardAndMouseListener() {
        listener = new FenetreListener(this);
    }

    private void setBackgroundColor() {
        //Définit la couleur d'arrière-plan en bleu océan
        frame.getContentPane().setBackground(new Color(4, 13, 19));
    }

    private void initPanels(ControleurMediateur controleur) {
        //initBackgroundPanel();
        initHexagonsPanel(controleur);
        initVignettePanel();
        initButtonPanel();
    }

    private void initVignettePanel() {
        // Ajouter la vignette
        vignettePanel = new VignettePanel();
        vignettePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(vignettePanel, JLayeredPane.PALETTE_LAYER);
    }

    private void initHexagonsPanel(ControleurMediateur controleur) {
        // Ajouter les tuiles hexagonales
        affichagePlateau = new AffichagePlateau(this, controleur, this.jeu);
        affichagePlateau.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(affichagePlateau, 50);
    }

    private void initMultiLayerPanel() {
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
        backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        backgroundPanel.setOpaque(false);
        layeredPane.add(backgroundPanel, JLayeredPane.FRAME_CONTENT_LAYER);
    }

    private void createBackgroundPanel() {
        backgroundPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                afficheBackground(g);
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
                afficheBoutonSave(g2d);
                afficheBoutonLoad(g2d);
                afficheBoutonAnnuler(g2d);
                afficheBoutonRefaire(g2d);
                afficheBoutonTuto(g2d);
                afficheBoutonQuitter(g2d);
                afficheJoueurCourant(g2d);
            }

            private void calculeRapports() {
                largeur = getWidth();
                hauteur = getHeight();
                double rapport = 492.0 / 847.0;
                double rapport_fenetre_score = 700.0/539.0;
                double rapport_joueur_courant = 131.0/603.0;
                largeur_bouton = Math.min(Math.max(Math.min(largeur / 9, hauteur / 9), 80), 190);
                hauteur_bouton = (int) (largeur_bouton * rapport);
                largeur_fenetre_score = (int) (largeur_bouton * 2.2);
                hauteur_fenetre_score = (int) (largeur_fenetre_score * rapport_fenetre_score);
                largeur_joueur_courant = (int) (largeur_bouton * 1.8);
                hauteur_joueur_courant = (int) (largeur_joueur_courant * rapport_joueur_courant);
                posX_fenetre_score = 10;
                posX_boutons = (int) (largeur * 0.97 - largeur_bouton);
                posY_save = 0;
                posX_save = posX_boutons - largeur_bouton - largeur_bouton / 10;
                posY_annuler = posY_save + hauteur_bouton + hauteur_bouton / 5;
                posY_fenetre_score = posY_annuler;
                posX_prenom_j0 = (int) (posX_fenetre_score + largeur_fenetre_score*0.02);
                posX_prenom_j1 = posX_prenom_j0;
                posY_prenom_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.10);
                posY_prenom_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.45);
                posX_huttes = (int) (posX_fenetre_score + largeur_fenetre_score*0.19);
                posX_tours = (int) (posX_fenetre_score + largeur_fenetre_score*0.53);
                posX_temples = (int) (posX_fenetre_score + largeur_fenetre_score*0.88);
                posY_scores_j0 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.31);
                posY_scores_j1 = (int) (posY_fenetre_score + hauteur_fenetre_score*0.66);
                posX_pioche = (int) (posX_fenetre_score + largeur_fenetre_score*0.67);
                posY_pioche = (int) (posY_fenetre_score + hauteur_fenetre_score*0.92);
                posX_joueur_courant = (largeur/2 - largeur_joueur_courant/2);
                posY_joueur_courant = posY_annuler;
                posY_refaire = posY_annuler + hauteur_bouton + hauteur_bouton / 5;
                posY_tuto = posY_refaire + hauteur_bouton + hauteur_bouton / 5;
                posY_quitter = Math.max(posY_tuto + hauteur_bouton + hauteur_bouton / 5, hauteur - 2 * hauteur_bouton);
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
        //bouton_save_select = lisImageBuf("Sauvegarder_select");
        bouton_load = lisImageBuf("Charger");
        //bouton_load_select = lisImageBuf("Charger_select");
        bouton_annuler = lisImageBuf("Annuler");
        //bouton_annuler_select = lisImageBuf("Annuler_select");
        bouton_refaire = lisImageBuf("Refaire");
        //bouton_refaire_select = lisImageBuf("Refaire_select");
        bouton_tuto_on = lisImageBuf("Tuto_on");
        bouton_tuto_off = lisImageBuf("Tuto_off");
        bouton_quitter = lisImageBuf("Quitter");
        //bouton_quitter_select = lisImageBuf("Quitter_select");
        fenetre_score = lisImageBuf("fenetre_score");
        joueur_courant = lisImageBuf("Joueur_courant");
        return new JFrame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                menuGraphique.setBounds(0, 0, getWidth(), getHeight());
                if(affichagePlateau!=null) affichagePlateau.setBounds(0, 0, getWidth(), getHeight());
                if(vignettePanel!=null) vignettePanel.setBounds(0, 0, getWidth(), getHeight());
                if(buttonPanel!=null) buttonPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        };
    }


    public static void afficheFenetreScore(Graphics g) {
        g.drawImage(fenetre_score, posX_fenetre_score, posY_fenetre_score, largeur_fenetre_score, hauteur_fenetre_score, null);
        Font font = new Font("Bookman Old Style", Font.BOLD, 29);
        g.setFont(font);
        g.setColor(Color.RED);
        String joueur_0 = jeu.joueurs[0].getPrenom();
        g.drawString(joueur_0, posX_prenom_j0, posY_prenom_j0);
        g.setColor(new Color(0, 128, 255));
        String joueur_1 = jeu.joueurs[1].getPrenom();
        g.drawString(joueur_1, posX_prenom_j1, posY_prenom_j1);
        g.setColor(Color.BLACK);
        font = new Font("Roboto", Font.BOLD, 20);
        g.setFont(font);
        String huttes_j0 = Integer.toString(jeu.joueurs[0].getNbHuttes());
        g.drawString(huttes_j0, posX_huttes, posY_scores_j0);
        String huttes_j1 = Integer.toString(jeu.joueurs[1].getNbHuttes());
        g.drawString(huttes_j1, posX_huttes, posY_scores_j1);
        String tours_j0 = Integer.toString(jeu.joueurs[0].getNbTours());
        g.drawString(tours_j0, posX_tours, posY_scores_j0);
        String tours_j1 = Integer.toString(jeu.joueurs[1].getNbTours());
        g.drawString(tours_j1, posX_tours, posY_scores_j1);
        String temples_j0 = Integer.toString(jeu.joueurs[0].getNbTemples());
        g.drawString(temples_j0, posX_temples, posY_scores_j0);
        String temples_j1 = Integer.toString(jeu.joueurs[1].getNbTemples());
        g.drawString(temples_j1, posX_temples, posY_scores_j1);
        font = new Font("Roboto", Font.BOLD, 25);
        g.setFont(font);
        String nb_tuiles_pioche = Integer.toString(jeu.pioche.size());
        g.drawString(nb_tuiles_pioche, posX_pioche, posY_pioche);
    }

    public static void afficheBackground(Graphics g) {
        g.drawImage(background, 0, 0, largeur, hauteur, null);
    }

    public static void afficheBoutonLoad(Graphics g) {
        if(select_load)
            g.drawImage(bouton_load_select, posX_boutons, posY_save, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_load, posX_boutons, posY_save, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheBoutonSave(Graphics g) {
        if(select_save)
            g.drawImage(bouton_save_select, posX_save, posY_save, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_save, posX_save, posY_save, largeur_bouton, hauteur_bouton,null);
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

    public static void afficheBoutonTuto(Graphics g) {
        if(tuto_on)
            g.drawImage(bouton_tuto_on, posX_boutons, posY_tuto, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_tuto_off, posX_boutons, posY_tuto, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheBoutonQuitter(Graphics g) {
        if(select_quitter)
            g.drawImage(bouton_quitter_select, posX_boutons, posY_quitter, largeur_bouton, hauteur_bouton,null);
        else
            g.drawImage(bouton_quitter, posX_boutons, posY_quitter, largeur_bouton, hauteur_bouton,null);
    }

    public static void afficheJoueurCourant(Graphics g) {
        Font font = new Font("Roboto", Font.BOLD, 20);
        g.setFont(font);
        g.drawImage(joueur_courant, posX_joueur_courant, posY_joueur_courant, largeur_joueur_courant, hauteur_joueur_courant, null);
        g.drawString(jeu.getJoueurCourant(), posX_joueur_courant+10, posY_joueur_courant+hauteur_joueur_courant/2+3);
    }

    public void metAjour(){
        repaint();
    }
}