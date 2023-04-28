package Vue;

import Controleur.ControleurMediateur;
import Modele.Hexagone;
import Modele.ImageLoader;
import Modele.Jeu;
import Structures.TripletDePosition;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

import static Modele.ImageLoader.*;
import static Modele.Camera.*;
import static Modele.GameState.*;


public class TEngine extends JFrame {
    TEngineListener listener;
    public HexagonalTiles hexTiles;
    ControleurMediateur controleur;

    Point LastPosition;
    Jeu jeu;

    public TEngine(Jeu jeu, ControleurMediateur controleur) {
        this.controleur = controleur;
        this.controleur.setEngine(this);
        this.jeu = jeu;
        setTitle("Taluva");
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Créer un JLayeredPane pour superposer les éléments
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1400, 1000));
        getContentPane().add(layeredPane);

        // Ajouter les tuiles hexagonales
        hexTiles = new HexagonalTiles(this, controleur);
        hexTiles.setBounds(0, 0, 1400, 1000);
        layeredPane.add(hexTiles, JLayeredPane.DEFAULT_LAYER);

        // Ajouter la vignette
        VignettePanel vignettePanel = new VignettePanel();
        vignettePanel.setBounds(0, 0, 1400, 1000);
        layeredPane.add(vignettePanel, JLayeredPane.PALETTE_LAYER);

        //Définit la couleur d'arrière-plan en bleu océan
        getContentPane().setBackground(new Color(64, 164, 223));
        //Définit les boutons et encadrés
        int  largeur_bouton = 0, hauteur_bouton = 0;

        addImage("map_layer_little", 50, 800, 1000, layeredPane);
        addImage("Annuler", 50, 50, hauteur_bouton, layeredPane);

        listener = new TEngineListener(this);
        poseTile = true;
    }


    public class HexagonalTiles extends JPanel {

        /////////////////////////////////////////////////////
        // HANDLER                                         //
        /////////////////////////////////////////////////////
        TEngineListener.MouseHandler handler;
        TEngineListener.KeyboardListener keyboardlisten;


        ControleurMediateur controleur;
        static TEngine tengine;



        public HexagonalTiles(TEngine t, ControleurMediateur controleur) {
            tengine = t;
            this.controleur = controleur;


            setOpaque(false);
            cameraOffset.x = -2100;
            cameraOffset.y = -1700;

            triplet[0][0] = Hexagone.VOLCAN;
            triplet[1][0] = Hexagone.VIDE;
            triplet[2][0] = Hexagone.VIDE;
            triplet[0][1] = 0;
            triplet[1][1] = 0;
            triplet[2][1] = 0;

            couleurs_joueurs = new Color[4];
            couleurs_joueurs[0] = new Color(255, 0, 0, 127);
            couleurs_joueurs[1] = new Color(0, 233, 255, 127);
            couleurs_joueurs[2] = new Color(183, 0, 255, 127);
            couleurs_joueurs[3] = new Color(255, 185, 0, 127);
        }


        public void changerTuileAPoser() {
            byte[] tuiles;
            tuiles = controleur.getTuileAPoser();
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2


            triplet[0][1] = tuiles[2]; // volcan
            triplet[1][1] = tuiles[3]; // tile 1
            triplet[2][1] = tuiles[4]; // tile 2
        }


        public void affichetripletpossible(){
            for(TripletDePosition t : jeu.getPlateau().getTripletsPossibles()){
                System.out.println("libre en : ");
                System.out.println("("+ t.getX().getL()+", "+t.getX().getC()+") "+"("+ t.getY().getL()+", "+t.getY().getC()+") "+"("+ t.getZ().getL()+", "+t.getZ().getC()+") ");
                System.out.println();
            }
        }

        public void miseAJour() {
            repaint();
            if(jeu.estJoueurCourantUneIA()){  // Faire jouer l'IA
                if(unefoisIA){
                    jeu.joueIA();
                    unefoisIA=false;
                }
            }
        }

        private void changerPoseTile() {
            poseTile = jeu.doit_placer_tuile();
        }

        @Override
        protected void paintComponent(Graphics g) {

            changerTuileAPoser();
            changerPoseTile();

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(cameraOffset.x, cameraOffset.y);
            g2d.scale(zoomFactor, zoomFactor);

            displayHexagonMap(g);

            if(poseTile) displayHoverTile(g);
            else displayHoverMaison(g);

            //affichage des boutons et des encadrés
            //afficheJoueurCourant(g);
        }

        private void displayHexagonMap(Graphics g) {
            Hexagone[][] map = controleur.getPlateau();
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int verticalOffset = (int) (tileHeight * 0.75);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int x = j* tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                    int y = i * verticalOffset;
                    int tileId = map[i][j].getTerrain();

                    int heightoffset = map[i][j].getHauteur();
                    heightoffset *= 80;

                    if (map[i][j].getTerrain() == Hexagone.VIDE) {
                        heightoffset -= 50;
                    }

                    BufferedImage tile = getTileImageFromId(tileId, map[i][j].getNum());
                    g.drawImage(tile, x , y - heightoffset, null);

                    if (poseTile) {
                        if (mode_plateau) {
                            if (map[i][j].getHauteur() != map[hoveredTile_x][hoveredTile_y].getHauteur()) {
                                if (map[hoveredTile_x][hoveredTile_y].getHauteur() != 0) {
                                    g.drawImage(voidTile_transparent, x, y - heightoffset + 5, null);
                                }
                            }
                        }
                        if (map[i][j].getTerrain() == Hexagone.VOLCAN) {
                            int j2;
                            if (i % 2 == 1) {
                                j2 = j - 1;
                            } else {
                                j2 = j;
                            }
                            if (controleur.peutPlacerTuile(i, j, i - 1, j2, i - 1, j2 + 1)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                                g.drawImage(beacon_1, x, y - heightoffset + 5, null);
                            } else if (controleur.peutPlacerTuile(i, j, i - 1, j2 + 1, i, j + 1)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                            } else if (controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j2 + 1)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                            } else if (controleur.peutPlacerTuile(i, j, i + 1, j2 + 1, i + 1, j2)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                            } else if (controleur.peutPlacerTuile(i, j, i + 1, j2, i, j - 1)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                            } else if (controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j2)) {
                                g.drawImage(whiteTile, x, y - heightoffset + 5, null);
                            }

                            if (controleur.peutPlacerTuile(i, j, i - 1, j2, i - 1, j2 + 1)) {
                                g.drawImage(beacon_1, x, y - heightoffset + 5, null);
                            }
                            if (controleur.peutPlacerTuile(i, j, i - 1, j2 + 1, i, j + 1)) {
                                g.drawImage(beacon_2, x, y - heightoffset + 5, null);
                            }
                            if (controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j2 + 1)) {
                                g.drawImage(beacon_3, x, y - heightoffset + 5, null);
                            }
                            if (controleur.peutPlacerTuile(i, j, i + 1, j2 + 1, i + 1, j2)) {
                                g.drawImage(beacon_4, x, y - heightoffset + 5, null);
                            }
                            if (controleur.peutPlacerTuile(i, j, i + 1, j2, i, j - 1)) {
                                g.drawImage(beacon_5, x, y - heightoffset + 5, null);
                            }
                            if (controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j2)) {
                                g.drawImage(beacon_6, x, y - heightoffset + 5, null);
                            }
                        }
                    }

                    if (mode_numero) {
                        if (map[i][j].getHauteur() == 1) {
                            g.drawImage(wrongTile1, x , y - heightoffset + 5, null);
                        }
                        if (map[i][j].getHauteur() == 2) {
                            g.drawImage(wrongTile2, x , y - heightoffset + 5, null);
                        }
                        if (map[i][j].getHauteur() == 3) {
                            g.drawImage(wrongTile3, x , y - heightoffset + 5, null);
                        }
                    }

                    if (map[i][j].getBatiment() == Hexagone.MAISON) {
                        tile = getTileImageFromId(Hexagone.MAISON, map[i][j].getNum());
                        g.drawImage(applyColorFilter(tile, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_FORET) {
                        g.drawImage(applyColorFilter(templeJungle, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PRAIRIE) {
                        g.drawImage(applyColorFilter(templePrairie, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_PIERRE) {
                        g.drawImage(applyColorFilter(templePierre, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TEMPLE_SABLE) {
                        g.drawImage(applyColorFilter(templeSable, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.TOUR) {
                        g.drawImage(applyColorFilter(tour, map[i][j].getNumJoueur()), x , y - heightoffset, null);
                    } else if (map[i][j].getBatiment() == Hexagone.CHOISIR_MAISON) {
                        int pos_x = x-150;
                        int pos_y = y -300;
                        int value = scrollValue%3;
                        if(value==1) value = 0;
                        else if(value==0) value = 1;
                        int[] coups = coupJouable(i,j);
                        if(coups[1]==0&&coups[2]==0) value=1;
                        else if(coups[1]==0){
                            value= scrollValue%2;
                            if(value==0) value=2;
                        }
                        else if(coups[2]==0){
                            value= scrollValue%2;
                            if(value==1) value = 0;
                            else if(value==0) value = 1;
                        }
                        if(coups[1]==0){
                            if(coups[2]==0) g.drawImage(choisirBat[7], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                            else{
                                if(value==1) g.drawImage(choisirBat[3], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                                else g.drawImage(choisirBat[6], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null); // attention ici 2 fois sur 3
                            }
                        }else{
                            if(coups[2]==0){
                                if(value==1) g.drawImage(choisirBat[4], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                                else g.drawImage(choisirBat[5], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                            }else{
                                g.drawImage(choisirBat[value], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                            }
                        }
                    }
                }
            }
        }

        public int[] coupJouable(int i,int j){
            int[] coups = new int[3];
            coups[0] = 1;
            if(jeu.getPlateau().getHauteurTuile(i,j)==3) coups[2] = 1;
            if(aCiteAutour(i,j)) coups[1] = 1;
            return coups;
        }

        public void updateCursorPosOnTiles(MouseEvent e) {
            int tileWidth = voidTile.getWidth();
            int tileHeight = voidTile.getWidth();
            int verticalOffset = (int) (tileHeight * 0.75);

            Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                    (int) ((e.getY() - cameraOffset.y) / zoomFactor));
            LastPosition = clickPositionAdjusted;

            // Convertir les coordonnées du système de pixels en coordonnées du système de grille
            int i = clickPositionAdjusted.y / verticalOffset;
            int j = (clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

            hoveredTile_x = i;
            hoveredTile_y = j;
        }

        private void displayHoverTile(Graphics g) {
            if (hoverTile != null && !clicDroiteEnfonce) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth(); // Important !!
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = hoverTilePositionAdjusted.y / verticalOffset;
                int j = (hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                int j2;
                if (i % 2 == 1) {
                    j2 = j - 1;
                } else {
                    j2 = j;
                }
                BufferedImage tile1 = getTileImageFromId(triplet[0][0],triplet[0][1]);
                BufferedImage tile2 = getTileImageFromId(triplet[1][0],triplet[1][1]);
                BufferedImage tile3 = getTileImageFromId(triplet[2][0],triplet[2][1]);

                float opacity = 1f;

                if (scrollValue == 1) {
                    if (!controleur.peutPlacerTuile(i, j, i - 1, j2, i - 1, j2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 2){
                    if (!controleur.peutPlacerTuile(i, j, i - 1, j2 + 1, i, j + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 3){
                    if (!controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j2 + 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 4){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, j2 + 1, i + 1, j2)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 5){
                    if (!controleur.peutPlacerTuile(i, j, i + 1, j2, i, j - 1)) {
                        opacity = 0.4f;
                    }
                }
                else if (scrollValue == 6){
                    if (!controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j2)) {
                        opacity = 0.4f;
                    }
                }
                if (opacity != 1f) {
                    if (tile1 != null) {
                        tile1 = applyRedFilter(tile1);
                    }
                    if (tile1 != null) {
                        tile2 = applyRedFilter(tile1);
                    }
                    if (tile1 != null) {
                        tile3 = applyRedFilter(tile1);
                    }
                }
                if (tile1 != null) {
                    tile1 = ImageLoader.getReducedOpacityImage(tile1, opacity);
                }
                if (tile2 != null) {
                    tile2 = ImageLoader.getReducedOpacityImage(tile2, opacity);
                }
                if (tile3 != null) {
                    tile3 = ImageLoader.getReducedOpacityImage(tile3, opacity);
                }

                int heightoffset1 = 1;
                int heightoffset2 = 1;
                int heightoffset3 = 1;
                heightoffset1 *= 30;
                heightoffset2 *= 30;
                heightoffset3 *= 30;

                if (scrollValue == 1) {
                    g.drawImage(tile2, x - tileWidth/2, y - verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x + tileWidth/2, y - verticalOffset - heightoffset3, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);
                }
                else if (scrollValue == 2){
                    g.drawImage(tile2, x + tileWidth/2, y - verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x + tileWidth, y - heightoffset3, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);

                }
                else if (scrollValue == 3){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile2, x + tileWidth, y -  heightoffset2, null);
                    g.drawImage(tile3, x +  tileWidth /2, y + verticalOffset - heightoffset3, null);
                }
                else if (scrollValue == 4){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile2, x + tileWidth/2, y + verticalOffset -  heightoffset2, null);
                    g.drawImage(tile3, x - tileWidth/2, y + verticalOffset - heightoffset3, null);
                }
                else if (scrollValue == 5){
                    g.drawImage(tile1, x , y - heightoffset1, null);
                    g.drawImage(tile3, x - tileWidth, y - heightoffset3, null);
                    g.drawImage(tile2, x - tileWidth/2, y + verticalOffset -  heightoffset2, null);
                }
                else if (scrollValue == 6){
                    g.drawImage(tile3, x - tileWidth/2, y - verticalOffset - heightoffset3, null);
                    g.drawImage(tile2, x - tileWidth, y -  heightoffset2, null);
                    g.drawImage(tile1, x , y - heightoffset1, null);

                }
            }
        }


        private void displayHoverMaison(Graphics g) {
            if (hoverTile != null) {
                int tileWidth = voidTile.getWidth();
                int tileHeight = voidTile.getWidth(); // Important !!
                int verticalOffset = (int) (tileHeight * 0.75);

                Point hoverTilePositionAdjusted = new Point((int) ((hoverTilePosition.x - cameraOffset.x) / zoomFactor),
                        (int) ((hoverTilePosition.y - cameraOffset.y) / zoomFactor));

                // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                int i = hoverTilePositionAdjusted.y / verticalOffset;
                int j = (hoverTilePositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

                // Convertir les coordonnées du système de grille en coordonnées du système de pixels
                int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
                int y = i * verticalOffset;

                int heightoffset1 = triplet[0][1];
                heightoffset1 *= 30;

                if(!enSelection){
                    if(jeu.getPlateau().getTuile(i,j).getBatiment()==0 && jeu.getPlateau().getTuile(i,j).getTerrain() != Hexagone.VOLCAN){
                        /*if(jeu.getPlateau().getHauteurTuile(i,j)==1) g.drawImage(maisonTile, x , y - heightoffset1, null);
                        else if(jeu.getPlateau().getHauteurTuile(i,j)==2){
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.DESERT) g.drawImage(templeSable, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.MONTAGNE) g.drawImage(templePierre, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.GRASS) g.drawImage(templePrairie, x , y - heightoffset1, null);
                            if (jeu.getPlateau().getTuile(i,j).getTerrain() == Hexagone.FORET) g.drawImage(templeJungle, x , y - heightoffset1, null);
                        }else if(jeu.getPlateau().getHauteurTuile(i,j)==3){
                            g.drawImage(tour, x , y - heightoffset1, null);*/
                        g.drawImage(constructionMode, x+50 , y - heightoffset1+50, (int)(tileWidth/1.2), (int) (tileWidth/1.2) ,null);
                    }
                }
            }
        }


        public void placerTuiles(int i, int j) {
            int j_modified;
            if (i % 2 == 1) {
                j_modified = j - 1;
            } else {
                j_modified = j;
            }

            if (scrollValue == 1) {
                if (controleur.peutPlacerTuile(i, j, i - 1, j_modified, i - 1, j_modified + 1)) {
                    controleur.placeEtage(i, j, i - 1, j_modified, triplet[1][0], i - 1, j_modified + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 2){
                if (controleur.peutPlacerTuile(i, j, i - 1, j_modified + 1, i, j + 1)) {
                    controleur.placeEtage(i, j, i - 1, j_modified + 1, triplet[1][0], i, j + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 3){
                if (controleur.peutPlacerTuile(i, j, i, j + 1, i + 1, j_modified + 1)) {
                    controleur.placeEtage(i, j, i, j + 1, triplet[1][0], i + 1, j_modified + 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 4){
                if (controleur.peutPlacerTuile(i, j, i + 1, j_modified + 1, i + 1, j_modified)) {
                    controleur.placeEtage(i, j, i + 1, j_modified + 1, triplet[1][0], i + 1, j_modified, triplet[2][0]);
                }
            }
            else if (scrollValue == 5){
                if (controleur.peutPlacerTuile(i, j, i + 1, j_modified, i, j - 1)) {
                    controleur.placeEtage(i, j, i + 1, j_modified, triplet[1][0], i, j - 1, triplet[2][0]);
                }
            }
            else if (scrollValue == 6){
                if (controleur.peutPlacerTuile(i, j, i, j - 1, i - 1, j_modified)) {
                    controleur.placeEtage(i, j, i, j - 1, triplet[1][0], i - 1, j_modified, triplet[2][0]);
                }
            }

            miseAJour();
        }

        private boolean possedeBatiment(int i,int j){
            return (jeu.getPlateau().getBatiment(i,j)==Hexagone.TOUR||jeu.getPlateau().getBatiment(i,j)==Hexagone.MAISON||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_SABLE||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_FORET
                    ||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_PIERRE||jeu.getPlateau().getBatiment(i,j)==Hexagone.TEMPLE_PRAIRIE)&&(jeu.getPlateau().getTuile(i,j).getNumJoueur()==jeu.getNumJoueurCourant());
        }

        private boolean aCiteAutour(int i,int j){
            boolean bool = possedeBatiment(i-1,j)||possedeBatiment(i+1,j)||possedeBatiment(i,j-1)||possedeBatiment(i,j+1);
            if(i%2==1){
                if(possedeBatiment(i-1,j-1)) {
                    bool = true;
                }
                if(possedeBatiment(i+1,j-1)) {
                    bool = true;
                }
            }else{
                if(possedeBatiment(i-1,j+1)) {
                    bool = true;
                }
                if(possedeBatiment(i+1,j+1)) {
                    bool = true;
                }
            }
            return bool;
        }

        public void placerMaison(int i, int j) {
            int value = scrollValue%3;
            int[] coupsJouable = coupJouable(i,j);
            if(coupsJouable[1]==0&&coupsJouable[2]==0) value=1;
            else if(coupsJouable[1]==0){
                value= scrollValue%2;
                if(value==0) value=2;
            }
            else if(coupsJouable[2]==0){
                value= scrollValue%2;
                if(value==1) value = 0;
                else if(value==0) value = 1;
            }

            if (value == 1) { // place hut
                enSelection = false;
                controleur.placeBatiment(i,j,(byte) 1);
            }
            else if (value == 2){ // place tour
                if(jeu.getPlateau().getHauteurTuile(i,j)==3){ // on verifie la condition pour poser une tour
                    enSelection = false;
                    controleur.placeBatiment(i,j,(byte) 3);
                }
            }
            else if (value == 0){ // place temple
                if(aCiteAutour(i,j)){
                    enSelection = false;
                    controleur.placeBatiment(i,j,(byte) 2);
                }
            }
        }

        public void addToCursor(MouseEvent e) {
            if(!jeu.estJoueurCourantUneIA()){
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int tileWidth = voidTile.getWidth();
                    int tileHeight = voidTile.getWidth();
                    int verticalOffset = (int) (tileHeight * 0.75);

                    Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                            (int) ((e.getY() - cameraOffset.y) / zoomFactor));

                    // Convertir les coordonnées du système de pixels en coordonnées du système de grille
                    int i = clickPositionAdjusted.y / verticalOffset;
                    int j = (clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;

                    if(poseTile) placerTuiles(i,j);
                    else{
                        if(!enSelection){
                            if (controleur.peutPlacerBatiment(i, j)) {
                                posBat_x = i;
                                posBat_y = j;
                                enSelection = true;
                                controleur.placeBatiment(posBat_x, posBat_y,(byte) 4);
                            }
                        }else{
                            placerMaison(posBat_x,posBat_y);
                        }
                    }
                    unefoisIA=true;
                    miseAJour();
                }
            }
        }

        public void annuleConstruction(MouseEvent e){
            if (SwingUtilities.isRightMouseButton(e)) {
                if(enSelection){
                    byte numJoueur = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getNumJoueur();
                    byte hauteur = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getHauteur();
                    byte terrain = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getTerrain();
                    int volcan_i = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getVolcanI();
                    int volcan_j = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getVolcanJ();
                    jeu.getPlateau().getPlateau()[posBat_x][posBat_y] = new Hexagone(numJoueur,hauteur,terrain,Hexagone.VIDE,(byte) volcan_i,(byte) volcan_j);
                    enSelection=false;
                }
            }
        }
    }
}