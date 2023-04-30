package Vue;

import Controleur.ControleurMediateur;
import Modele.HexagonalTiles;
import Modele.Jeu;

import javax.swing.*;
import java.awt.*;

import static Modele.ImageLoader.*;


public class TEngine {
    TEngineListener listener;
    public HexagonalTiles hexTiles;
    public VignettePanel vignettePanel;
    public JPanel buttonPanel;
    public JLayeredPane layeredPane;

    ControleurMediateur controleur;

    public static Point LastPosition;
    Jeu jeu;
    JFrame jframe;

    public TEngine(Jeu jeu, ControleurMediateur controleur) {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        this.jeu = jeu;
        this.jframe = getJframe();
        initFrame();

        initPanels(controleur);

        initKeyBoardAndMouseListener();

        setBackgroundColor();

        jframe.setVisible(true);
    }

    private void initKeyBoardAndMouseListener() {
        listener = new TEngineListener(this);
    }

    private void setBackgroundColor() {
        //Définit la couleur d'arrière-plan en bleu océan
        jframe.getContentPane().setBackground(new Color(64, 164, 223));
    }

    private void initPanels(ControleurMediateur controleur) {
        initMultiLayerPanel();
        initHexagonsPanel(controleur);
        initVignettePanel();
        initButtonPanel();
    }

    private void initVignettePanel() {
        // Ajouter la vignette
        vignettePanel = new VignettePanel();
        vignettePanel.setBounds(0, 0, 1400, 1000);
        layeredPane.add(vignettePanel, JLayeredPane.PALETTE_LAYER);
    }

    private void initHexagonsPanel(ControleurMediateur controleur) {
        // Ajouter les tuiles hexagonales
        hexTiles = new HexagonalTiles(this, controleur, this.jeu);
        hexTiles.setBounds(0, 0, 1400, 1000);
        layeredPane.add(hexTiles, JLayeredPane.DEFAULT_LAYER);
    }

    private void initMultiLayerPanel() {
        // Créer un JLayeredPane pour superposer les éléments
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(jframe.getWidth(), jframe.getHeight()));
        jframe.getContentPane().add(layeredPane);
    }

    private void initButtonPanel() {
        createButtonPanel();
        buttonPanel.setBackground(new Color(0,0,0,0));
        buttonPanel.setBounds(0, 0, 1400, 1000);
        buttonPanel.setOpaque(false);
        layeredPane.add(buttonPanel, JLayeredPane.POPUP_LAYER);
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g.create();

                largeur = getWidth();
                hauteur = getHeight();
                double rapport = 207.0 / 603.0;
                largeur_bouton = Math.min(Math.max(Math.min(largeur / 6, hauteur / 6), 100), 190);
                hauteur_bouton = (int) (largeur_bouton * rapport);
                posX_boutons = (int) (largeur * 0.95 - largeur_bouton);
                posY_save = 0;
                posX_save = posX_boutons - largeur_bouton - largeur_bouton / 10;
                posY_annuler = posY_save + 2 * hauteur_bouton + hauteur_bouton / 5;
                posY_refaire = posY_annuler + hauteur_bouton + hauteur_bouton / 5;
                posY_reset = posY_refaire + hauteur_bouton + hauteur_bouton / 5;
                posY_quitter = (int) Math.max(posY_refaire + hauteur_bouton + hauteur_bouton / 5, hauteur - 1.5 * hauteur_bouton);
                afficheBoutonSave(g2d);
                afficheBoutonLoad(g2d);
                afficheBoutonAnnuler(g2d);
                afficheBoutonRefaire(g2d);
                afficheBoutonReset(g2d);
                afficheBoutonQuitter(g2d);
            }
        };
    }

    private void initFrame() {
        jframe.setTitle("Taluva");
        ImageIcon icon = new ImageIcon("ressources/icon.png");
        jframe.setIconImage(icon.getImage());
        //récupère la taille de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Définit la taille de la fenêtre à 60% de la taille de l'écran
        jframe.setSize(screenSize.width * 6 / 10, screenSize.height * 6 / 10);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);
    }

    private JFrame getJframe() {
        bouton_save = lisImageBuf("Sauvegarder");
        //bouton_save_select = lisImageBuf("Sauvegarder_select");
        bouton_load = lisImageBuf("Charger");
        //bouton_load_select = lisImageBuf("Charger_select");
        bouton_annuler = lisImageBuf("Annuler");
        //bouton_annuler_select = lisImageBuf("Annuler_select");
        bouton_refaire = lisImageBuf("Refaire");
        //bouton_refaire_select = lisImageBuf("Refaire_select");
        bouton_reset = lisImageBuf("Reinitialiser");
        //bouton_reset_select = lisImageBuf("Reinitialiser_select");
        bouton_quitter = lisImageBuf("Quitter");
        //bouton_quitter_select = lisImageBuf("Quitter_select");
        return new JFrame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                hexTiles.setBounds(0, 0, getWidth(), getHeight());
                vignettePanel.setBounds(0, 0, getWidth(), getHeight());
                buttonPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        };
    }
}