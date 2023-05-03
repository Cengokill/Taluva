package Vue;

import Controleur.ControleurMediateur;
import Modele.ImageLoader;
import Modele.Jeu;

import javax.swing.*;
import java.awt.*;

import static Modele.ImageLoader.*;


public class FenetreJeu {
    FenetreListener listener;
    public AffichagePlateau affichagePlateau;
    public VignettePanel vignettePanel;
    public JPanel buttonPanel;
    public JLayeredPane layeredPane;

    final ControleurMediateur controleur;

    public static Point LastPosition;
    final Jeu jeu;
    final JFrame f;

    public FenetreJeu(Jeu jeu, ControleurMediateur controleur) {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        this.jeu = jeu;
        this.f = getF();
        initFrame();

        initPanels(controleur);

        initKeyBoardAndMouseListener();

        setBackgroundColor();

        f.setVisible(true);
    }

    private void initKeyBoardAndMouseListener() {
        listener = new FenetreListener(this);
    }

    private void setBackgroundColor() {
        //Définit la couleur d'arrière-plan en bleu océan
        f.getContentPane().setBackground(new Color(64, 164, 223));
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
        affichagePlateau = new AffichagePlateau(this, controleur, this.jeu);
        affichagePlateau.setBounds(0, 0, 1400, 1000);
        layeredPane.add(affichagePlateau, JLayeredPane.DEFAULT_LAYER);
    }

    private void initMultiLayerPanel() {
        // Créer un JLayeredPane pour superposer les éléments
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(f.getWidth(), f.getHeight()));
        f.getContentPane().add(layeredPane);
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
                if (!ImageLoader.loaded) {
                    return;
                }
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g.create();

                largeur = getWidth();
                hauteur = getHeight();
                double rapport = 492.0 / 847.0;
                largeur_bouton = Math.min(Math.max(Math.min(largeur / 8, hauteur / 8), 100), 190);
                hauteur_bouton = (int) (largeur_bouton * rapport);
                posX_boutons = (int) (largeur * 0.97 - largeur_bouton);
                posY_save = 0;
                posX_save = posX_boutons - largeur_bouton - largeur_bouton / 10;
                posY_annuler = posY_save + 2 * hauteur_bouton + hauteur_bouton / 5;
                posY_refaire = posY_annuler + hauteur_bouton + hauteur_bouton / 5;
                posY_tuto = posY_refaire + hauteur_bouton + hauteur_bouton / 5;
                posY_quitter = (int) Math.max(posY_refaire + hauteur_bouton + hauteur_bouton / 5, hauteur - 2 * hauteur_bouton);
                afficheBoutonSave(g2d);
                afficheBoutonLoad(g2d);
                afficheBoutonAnnuler(g2d);
                afficheBoutonRefaire(g2d);
                afficheBoutonTuto(g2d);
                afficheBoutonQuitter(g2d);
            }
        };
    }

    private void initFrame() {
        f.setTitle("Taluva");
        ImageIcon icon = new ImageIcon("ressources/icon.png");
        f.setIconImage(icon.getImage());
        //récupère la taille de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Définit la taille de la fenêtre à 60% de la taille de l'écran
        f.setSize(screenSize.width * 6 / 10, screenSize.height * 6 / 10);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
    }

    private JFrame getF() {
        bouton_save = lisImageBuf("save");
        //bouton_save_select = lisImageBuf("Sauvegarder_select");
        bouton_load = lisImageBuf("load");
        //bouton_load_select = lisImageBuf("Charger_select");
        bouton_annuler = lisImageBuf("Annuler");
        //bouton_annuler_select = lisImageBuf("Annuler_select");
        bouton_refaire = lisImageBuf("Refaire");
        //bouton_refaire_select = lisImageBuf("Refaire_select");
        bouton_tuto_on = lisImageBuf("Tuto_on");
        bouton_tuto_off = lisImageBuf("Tuto_off");
        bouton_quitter = lisImageBuf("Quitter");
        //bouton_quitter_select = lisImageBuf("Quitter_select");
        return new JFrame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                affichagePlateau.setBounds(0, 0, getWidth(), getHeight());
                vignettePanel.setBounds(0, 0, getWidth(), getHeight());
                buttonPanel.setBounds(0, 0, getWidth(), getHeight());
            }
        };
    }
}