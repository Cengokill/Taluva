package Vue;

import Controleur.ControleurMediateur;
import Modele.Hexagone;
import Modele.ImageLoader;
import Modele.Jeu;
import Modele.Point2;
import Structures.TripletDePosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Modele.Camera.*;
import static Modele.GameState.*;
import static Modele.Hexagone.*;
import static Modele.ImageLoader.*;

public class AffichagePlateau extends JPanel {
    /////////////////////////////////////////////////////
    // HANDLER                                         //
    /////////////////////////////////////////////////////
    public FenetreListener.MouseHandler handler;
    public FenetreListener.KeyboardListener keyboardlisten;

    final ControleurMediateur controleur;
    public final FenetreJeu fenetreJeu;
    public final Jeu jeu;

    private int index_water=0;

    public final int HAUTEUR_ETAGE = 80;
    public final int HAUTEUR_OCEAN = 50;

    public AffichagePlateau(FenetreJeu t, ControleurMediateur controleur, Jeu jeu) {
        this.fenetreJeu = t;
        this.controleur = controleur;
        this.jeu = jeu;
        this.setOpaque(false);

        initCameraPosition();

        initTripletHover();
        initCouleursJoueurs();

        poseTile = true;
        boucle();

        System.out.println(fenetreJeu.frame.getWidth());
        System.out.println(fenetreJeu.frame.getHeight());
        cameraOffset.x += fenetreJeu.frame.getWidth();
        cameraOffset.y += 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!ImageLoader.loaded) {
            return;
        }
        changerTuileAPoser();
        changerPoseTile();

        super.paintComponent(g);
        afficheBackground(cameraOffset.x, cameraOffset.y+800, g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(cameraOffset.x, cameraOffset.y);
        g2d.scale(zoomFactor, zoomFactor);
        displayHexagonMap(g);

        if(poseTile) displayHoverTile(g);
        else displayHoverMaison(g);
    }

    private void afficheBackground(int x, int y, Graphics g) {
        g.drawImage(background, x, y, largeur*4, hauteur*4, null);
    }


    private void displayHexagonMap(Graphics g) {
        Hexagone[][] map = controleur.getPlateau();
        int tileWidth = voidTile.getWidth();
        int tileHeight = voidTile.getWidth();
        int verticalOffset = (int) (tileHeight * 0.75);

        parcoursPlateau(g, map, tileWidth, verticalOffset);
    }

    private void initCameraPosition() {
        cameraOffset.x = -2100;
        cameraOffset.y = -1700;
    }

    private void initCouleursJoueurs() {
        couleurs_joueurs = new Color[4];
        couleurs_joueurs[0] = new Color(255, 0, 0, 127);
        couleurs_joueurs[1] = new Color(0, 233, 255, 127);
        couleurs_joueurs[2] = new Color(183, 0, 255, 127);
        couleurs_joueurs[3] = new Color(255, 185, 0, 127);
    }

    private void initTripletHover() {
        triplet[0][0] = VOLCAN;
        triplet[1][0] = VIDE;
        triplet[2][0] = VIDE;
        triplet[0][1] = 0;
        triplet[1][1] = 0;
        triplet[2][1] = 0;
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
            System.out.println("("+ t.getVolcan().ligne()+", "+t.getVolcan().colonne()+") "+"("+ t.getTile1().ligne()+", "+t.getTile1().colonne()+") "+"("+ t.getTile2().ligne()+", "+t.getTile2().colonne()+") ");
            System.out.println();
        }
    }

    public void miseAJour() {
        repaint();
        if(index_water==3){
            index_water=0;
        }else index_water++;
        if(jeu.estJoueurCourantUneIA()){  // Faire jouer l'AbstractIA
            if(unefoisIA){
                jeu.joueIA();
                unefoisIA=false;
            }
        }
    }

    private void changerPoseTile() {
        poseTile = jeu.doit_placer_tuile();
    }

    private void parcoursPlateau(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0 - 2; j < map[0].length; j++) {
                affiche(g, map, tileWidth, verticalOffset, i, j);
            }
        }
    }

    private void affiche(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset, int i, int j) {
        if (i < 2 || j < 2 || i >= 58 || j >= 58) {
            return;
        }
        int tileId = map[i][j].getBiomeTerrain();
        if (tileId != VIDE) {
            afficheHexagone(g, map, tileWidth, verticalOffset, i, j, tileId);
        }
    }

    private void afficheHexagone(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset, int i, int j, int tileId) {
        int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
        int y = i * verticalOffset;


        int heightoffset = calculHauteurAffichageHexagone(map, i, j);

        BufferedImage tile = getTileImageFromId(tileId, map[i][j].getNum());
        g.drawImage(tile, x , y - heightoffset, null);


        // TODO optimiser l'affichage, ces fonctions font lag
        afficheFiltresTileMode(g, map, i, j, x, y, heightoffset);
        afficheNumerosNiveaux(g, map, i, j, x, y, heightoffset);
        afficheBatiments(g, map, i, j, x, y, heightoffset);
        afficheContourParNiveau(g, map, i, j, x, y, heightoffset);
    }

    private void afficheContourParNiveau(Graphics g, Hexagone[][] map, int ligne, int colonne, int xDrawPosition, int yDrawPosition, int heightoffset) {
        int colonneConvertieHex = convertionTileMapToHexagonal(ligne, colonne);
        if (ligne > 1 && colonne > 1 && ligne < map.length - 1 && colonne < map[0].length - 1) {

            String direction = "gauche";
            contour(g, map, ligne, colonne, xDrawPosition, yDrawPosition, heightoffset, colonne - 1, map[ligne],direction);

            direction = "droite";
            contour(g, map, ligne, colonne, xDrawPosition, yDrawPosition, heightoffset, colonne + 1, map[ligne], direction);

            direction = "hautGauche";
            contour(g, map, ligne - 1, colonne, xDrawPosition, yDrawPosition, heightoffset, colonneConvertieHex, map[ligne], direction);

            direction = "hautDroite";
            contour(g, map, ligne - 1, colonne, xDrawPosition, yDrawPosition, heightoffset, colonneConvertieHex + 1, map[ligne], direction);

            direction = "basGauche";
            contour(g, map, ligne + 1, colonne, xDrawPosition, yDrawPosition, heightoffset, colonneConvertieHex, map[ligne], direction);

            direction = "basDroite";
            contour(g, map, ligne + 1, colonne, xDrawPosition, yDrawPosition, heightoffset, colonneConvertieHex + 1, map[ligne], direction);
        }
    }

    private void contour(Graphics g, Hexagone[][] map, int ligne, int colonne, int xDrawPosition, int yDrawPosition, int heightoffset, int colonneDirection, Hexagone[] hexagones, String direction) {
        BufferedImage contourEtage1;
        BufferedImage contourEtage2;
        BufferedImage contourEtage3;
        BufferedImage contourTuile;

        switch (direction) {
            case "gauche":
                contourEtage1 = plateau_Gauche_etage1;
                contourEtage2 = plateau_Gauche_etage2;
                contourEtage3 = plateau_Gauche_etage3;
                contourTuile = tuile_Gauche;
                break;

            case "droite":
                contourEtage1 = plateau_Droite_etage1;
                contourEtage2 = plateau_Droite_etage2;
                contourEtage3 = plateau_Droite_etage3;
                contourTuile = tuile_Droite;
                break;

            case "hautGauche":
                contourEtage1 = plateau_hautGauche_etage1;
                contourEtage2 = plateau_hautGauche_etage2;
                contourEtage3 = plateau_hautGauche_etage3;
                contourTuile = tuile_hautGauche;
                break;

            case "hautDroite":
                contourEtage1 = plateau_hautDroite_etage1;
                contourEtage2 = plateau_hautDroite_etage2;
                contourEtage3 = plateau_hautDroite_etage3;
                contourTuile = tuile_hautDroite;
                break;

            case "basGauche":
                contourEtage1 = plateau_basGauche_etage1;
                contourEtage2 = plateau_basGauche_etage2;
                contourEtage3 = plateau_basGauche_etage3;
                contourTuile = tuile_basGauche;
                break;

            default:
                contourEtage1 = plateau_basDroite_etage1;
                contourEtage2 = plateau_basDroite_etage2;
                contourEtage3 = plateau_basDroite_etage3;
                contourTuile = tuile_basDroite;
                break;
        }


        if (estCoteTuile(colonneDirection, map[ligne], hexagones[colonne])) {
                g.drawImage(contourTuile, xDrawPosition, yDrawPosition - heightoffset + 55, null);
        }

        if (estBordureHauteur(colonneDirection, map[ligne], hexagones[colonne])) {
            drawContourEtages(g, colonne, xDrawPosition, yDrawPosition, heightoffset, hexagones, contourEtage1, contourEtage2, contourEtage3);
        }
    }

    private boolean estBordureHauteur(int colonneDirection, Hexagone[] map, Hexagone hexagoneCourant) {
        return map[colonneDirection].getHauteur() < hexagoneCourant.getHauteur();
    }

    private boolean estCoteTuile(int colonneDirection, Hexagone[] map, Hexagone hexagoneCourant) {
        boolean aMemeVolcan = map[colonneDirection].getColonneVolcan() == hexagoneCourant.getColonneVolcan()
                                &&
                              map[colonneDirection].getLigneVolcan() == hexagoneCourant.getLigneVolcan();

        return hexagoneCourant.getBiomeTerrain() != Hexagone.VIDE && !aMemeVolcan;
    }

    private void drawContourEtages(Graphics g, int j, int x, int y, int heightoffset, Hexagone[] hexagones, BufferedImage contourEtage1, BufferedImage contourEtage2, BufferedImage contourEtage3) {
        if (hexagones[j].getHauteur() == 1) {
            g.drawImage(contourEtage1, x, y - heightoffset + 55, null);
        } else if (hexagones[j].getHauteur() == 2) {
            g.drawImage(contourEtage2, x, y - heightoffset + 55, null);
        } else if (hexagones[j].getHauteur() == 3) {
            g.drawImage(contourEtage3, x, y - heightoffset + 55, null);
        }
    }


    private void afficheBatiments(Graphics g, Hexagone[][] map, int ligne, int colonne, int x, int y, int heightoffset) {
        BufferedImage tile;
        if (map[ligne][colonne].getBatiment() != CHOISIR_BATIMENT) {
            g.drawImage(getBatimentFromPlayerId(map[ligne][colonne].getNumJoueur(), (byte) map[ligne][colonne].getBatiment()), x, y - heightoffset, null);

        } else if (map[ligne][colonne].getBatiment() == CHOISIR_BATIMENT) {
            int pos_x = x -150;
            int pos_y = y -300;
            int value = scrollValue%3;
            if(value==1) value = 0;
            else if(value==0) value = 1;
            int[] coups = coupJouable(ligne, colonne);

            value = updateScrollValue(value, coups);
            if(coupJouable(ligne, colonne)[0]==0 && coupJouable(ligne, colonne)[1]==0 && coupJouable(ligne, colonne)[2]==0) return;
            choixBatiment(g, pos_x, pos_y, value, coups);
        }
    }

    private int updateScrollValue(int value, int[] coups) {
        if (coups[1] == 0 && coups[2] == 0) value = 1;
        else if (coups[1] == 0) {
            value = scrollValue % 2;
            if (value == 0) value = 2;
        } else if (coups[2] == 0) {
            value = scrollValue % 2;
            if (value == 1) value = 0;
            else if (value == 0) value = 1;
        }
        return value;
    }

    private void choixBatiment(Graphics g, int pos_x, int pos_y, int value, int[] coups) {
        if(coups[1]==0){
            if(coups[2]==0) g.drawImage(choisirBat[7], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
            else{
                if(value ==1) g.drawImage(choisirBat[3], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                else g.drawImage(choisirBat[6], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null); // attention ici 2 fois sur 3
            }
        }else{
            if(coups[2]==0){
                if(value ==1) g.drawImage(choisirBat[4], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
                else g.drawImage(choisirBat[5], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
            }else{
                g.drawImage(choisirBat[value], pos_x, pos_y,choisirBat[value].getWidth()*2,choisirBat[value].getWidth()*2, null);
            }
        }
    }

    private int calculHauteurAffichageHexagone(Hexagone[][] map, int i, int j) {
        int heightoffset = map[i][j].getHauteur();
        heightoffset *= HAUTEUR_ETAGE;

        if (map[i][j].getBiomeTerrain() == VIDE) {
            heightoffset -= HAUTEUR_OCEAN;
        }
        return heightoffset;
    }

    private void afficheNumerosNiveaux(Graphics g, Hexagone[][] map, int i, int j, int x, int y, int heightoffset) {
        if (mode_numero) {
            if (map[i][j].getHauteur() == 1) {
                g.drawImage(wrongTile1, x, y - heightoffset + 5, null);
            }
            if (map[i][j].getHauteur() == 2) {
                g.drawImage(wrongTile2, x, y - heightoffset + 5, null);
            }
            if (map[i][j].getHauteur() == 3) {
                g.drawImage(wrongTile3, x, y - heightoffset + 5, null);
            }
        }
    }

    private void afficheFiltresTileMode(Graphics g, Hexagone[][] map, int i, int j, int x, int y, int heightoffset) {
        if (poseTile) {
            afficherFiltreSombre(g, map, i, j, x, y, heightoffset);
            afficherFiltreVolcan(g, map, i, j, x, y, heightoffset);
        }
    }

    private void afficherFiltreVolcan(Graphics g, Hexagone[][] map, int i, int j, int x, int y, int heightoffset) {
        if (map[i][j].getBiomeTerrain() == VOLCAN) {
            int j2 = convertionTileMapToHexagonal(i, j);
            illumineVolcanLibre(g, i, j, x, y, heightoffset, j2);
            afficheDirectionsLibres(g, i, j, x, y, heightoffset, j2);
        }
    }

    private void afficherFiltreSombre(Graphics g, Hexagone[][] map, int i, int j, int x, int y, int heightoffset) {
        if (hoveredTile_y < 2 || hoveredTile_x < 2 || hoveredTile_y >= 58 || hoveredTile_x >= 58) {
            return;
        }
        if (mode_plateau) {
            if (map[i][j].getHauteur() != map[hoveredTile_x][hoveredTile_y].getHauteur()) {
                if (map[hoveredTile_x][hoveredTile_y].getHauteur() != 0) {
                    g.drawImage(voidTile_transparent, x, y - heightoffset + 5, null);
                }
            }
        }
    }

    private int convertionTileMapToHexagonal(int i, int j) {
        int j2;
        if (i % 2 == 1) {
            j2 = j - 1;
        } else {
            j2 = j;
        }
        return j2;
    }

    private void afficheDirectionsLibres(Graphics g, int i, int j, int x, int y, int heightoffset, int j2) {
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

    private void illumineVolcanLibre(Graphics g, int i, int j, int x, int y, int heightoffset, int j2) {
        if (controleur.peutPlacerTuile(i, j, i - 1, j2, i - 1, j2 + 1)) {
            g.drawImage(whiteTile, x, y - heightoffset + 5, null);
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
    }

    private boolean estTemple(int i,int j){
        if(jeu.getPlateau().getBatiment(i,j)==TEMPLE_PRAIRIE) return true;
        if(jeu.getPlateau().getBatiment(i,j)==TEMPLE_FORET) return true;
        if(jeu.getPlateau().getBatiment(i,j)==TEMPLE_PIERRE) return true;
        if(jeu.getPlateau().getBatiment(i,j)==TEMPLE_SABLE) return true;
        return false;
    }

    private boolean estTour(int i,int j){
        if(jeu.getPlateau().getBatiment(i,j)==TOUR) return true;
        return false;
    }

    private ArrayList<Point2> copyPoints(ArrayList<Point2> tab1) {
        ArrayList<Point2> tab2 = new ArrayList<>();
        for(Point2 p : tab1){
            tab2.add(p);
        }
        return tab2;
    }

    private boolean peutPoserTemple(int i,int j){
        ArrayList<Point2> pointsVillage = positionsBatsVillage(i,j);
        if(pointsVillage.size()<=3) return false;
        for(Point2 p : pointsVillage){
            System.out.println("x: "+p.getPointX()+" y: "+p.getPointY());
            if(estTemple(p.getPointX(),p.getPointY())) return false;
        }
        return true;
    }

    private boolean peutPoserTour(int i,int j){
        ArrayList<Point2> pointsVillage = positionsBatsVillage(i,j);
        if(jeu.getPlateau().getHauteurTuile(i,j)<3) return false;       // On verifie que la hauteur est d'au moins 3
        for(Point2 p : pointsVillage){                                  // On verifie que la cité ne possède pas déjà une tour
            if(estTour(p.getPointX(),p.getPointY())) return false;
        }
        return true;
    }

    public int[] coupJouable(int i,int j){
        int[] coups = new int[3];
        coups[0] = 1;
        if(jeu.getPlateau().getHauteurTuile(i,j)>1 && !aCiteAutour(i,j)) coups[0] = 0;  // Peut pas placer hutte a une hauteur > 1 s'il n'y pas de hutte à côté
        if(peutPoserTour(i,j)) coups[2] = 1;
        if(peutPoserTemple(i,j)) coups[1] = 1;
        return coups;
    }

    public void updateCursorPosOnTiles(MouseEvent e) {
        if (!ImageLoader.loaded) {
            return;
        }
        int tileWidth = voidTile.getWidth();
        int tileHeight = voidTile.getWidth();
        int verticalOffset = (int) (tileHeight * 0.75);

        Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                (int) ((e.getY() - cameraOffset.y) / zoomFactor));
        FenetreJeu.LastPosition = clickPositionAdjusted;

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

            if (i < 2 || j < 2 || i >= 58 || j >= 58) {
                return;
            }


            // Convertir les coordonnées du système de grille en coordonnées du système de pixels
            int x = j * tileWidth - (i % 2 == 1 ? tileWidth / 2 : 0);
            int y = i * verticalOffset;

            int j2 = convertionTileMapToHexagonal(i, j);
            BufferedImage tile1 = getTileImageFromId(triplet[0][0],triplet[0][1]);
            BufferedImage tile2 = getTileImageFromId(triplet[1][0],triplet[1][1]);
            BufferedImage tile3 = getTileImageFromId(triplet[2][0],triplet[2][1]);

            float opacity = 1f;


            if (tile1 != null && tile2 != null && tile3 != null) {
                opacity = updateOpacite(i, j, j2, opacity);
                if (opacity != 1f) {
                        tile1 = tileErreur;
                        tile2 = tileErreur;
                        tile3 = tileErreur;
                }
            }


            y -= jeu.getPlateau().getPlateau()[i][j].getHauteur() * HAUTEUR_ETAGE;
            afficheTilesHover(g, tileWidth, verticalOffset, x, y, tile1, tile2, tile3);
        }
    }

    private void afficheTilesHover(Graphics g, int tileWidth, int verticalOffset, int x, int y, BufferedImage tile1, BufferedImage tile2, BufferedImage tile3) {
        int heightoffset1 = 1;
        int heightoffset2 = 1;
        int heightoffset3 = 1;
        heightoffset1 *= 30;
        heightoffset2 *= 30;
        heightoffset3 *= 30;

        if (scrollValue == 1) {
            g.drawImage(tile2, x - tileWidth /2, y - verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, x + tileWidth /2, y - verticalOffset - heightoffset3, null);
            g.drawImage(tile1, x, y - heightoffset1, null);
        }
        else if (scrollValue == 2){
            g.drawImage(tile2, x + tileWidth /2, y - verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, x + tileWidth, y - heightoffset3, null);
            g.drawImage(tile1, x, y - heightoffset1, null);

        }
        else if (scrollValue == 3){
            g.drawImage(tile1, x, y - heightoffset1, null);
            g.drawImage(tile2, x + tileWidth, y -  heightoffset2, null);
            g.drawImage(tile3, x +  tileWidth /2, y + verticalOffset - heightoffset3, null);
        }
        else if (scrollValue == 4){
            g.drawImage(tile1, x, y - heightoffset1, null);
            g.drawImage(tile2, x + tileWidth /2, y + verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, x - tileWidth /2, y + verticalOffset - heightoffset3, null);
        }
        else if (scrollValue == 5){
            g.drawImage(tile1, x, y - heightoffset1, null);
            g.drawImage(tile3, x - tileWidth, y - heightoffset3, null);
            g.drawImage(tile2, x - tileWidth /2, y + verticalOffset -  heightoffset2, null);
        }
        else if (scrollValue == 6){
            g.drawImage(tile3, x - tileWidth /2, y - verticalOffset - heightoffset3, null);
            g.drawImage(tile2, x - tileWidth, y -  heightoffset2, null);
            g.drawImage(tile1, x, y - heightoffset1, null);
        }
    }

    private float updateOpacite(int i, int j, int j2, float opacity) {
        if (scrollValue == 1) {
            opacity = changeOpacitePeutPasplacerTuile(i, j, j2, opacity, i - 1, i - 1, j2 + 1);
        }
        else if (scrollValue == 2){
            opacity = changeOpacitePeutPasplacerTuile(i, j, j2 + 1, opacity, i - 1, i, j + 1);
        }
        else if (scrollValue == 3){
            opacity = changeOpacitePeutPasplacerTuile(i, j, j + 1, opacity, i, i + 1, j2 + 1);
        }
        else if (scrollValue == 4){
            opacity = changeOpacitePeutPasplacerTuile(i, j, j2 + 1, opacity, i + 1, i + 1, j2);
        }
        else if (scrollValue == 5){
            opacity = changeOpacitePeutPasplacerTuile(i, j, j2, opacity, i + 1, i, j - 1);
        }
        else if (scrollValue == 6){
            opacity = changeOpacitePeutPasplacerTuile(i, j, j - 1, opacity, i, i - 1, j2);
        }
        return opacity;
    }

    private float changeOpacitePeutPasplacerTuile(int i, int j, int j2, float opacity, int i2, int i3, int i4) {
        if (!controleur.peutPlacerTuile(i, j, i2, j2, i3, i4)) {
            opacity = 0.4f;
        }
        return opacity;
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
                if(coupJouable(i,j)[0]==0 && coupJouable(i,j)[1]==0 && coupJouable(i,j)[2]==0) return;
                if(jeu.getPlateau().getTuile(i,j).getBatiment()==0 && jeu.getPlateau().getTuile(i,j).getBiomeTerrain() != VOLCAN && jeu.getPlateau().getTuile(i,j).getBiomeTerrain() != VIDE){
                    g.drawImage(constructionMode, x+50 , y - heightoffset1+50, (int)(tileWidth/1.2), (int) (tileWidth/1.2) ,null);
                }
            }
        }
    }

    public void placerTuiles(int i, int j) {
        int j_modified = convertionTileMapToHexagonal(i, j);

        if (scrollValue == 1) {
            placeEtageSiPossible(i, j, j_modified, i - 1, i - 1, j_modified + 1);
        }
        else if (scrollValue == 2){
            placeEtageSiPossible(i, j, j_modified + 1, i - 1, i, j + 1);
        }
        else if (scrollValue == 3){
            placeEtageSiPossible(i, j, j + 1, i, i + 1, j_modified + 1);
        }
        else if (scrollValue == 4){
            placeEtageSiPossible(i, j, j_modified + 1, i + 1, i + 1, j_modified);
        }
        else if (scrollValue == 5){
            placeEtageSiPossible(i, j, j_modified, i + 1, i, j - 1);
        }
        else if (scrollValue == 6){
            placeEtageSiPossible(i, j, j - 1, i, i - 1, j_modified);
        }

        //miseAJour();
    }

    private void placeEtageSiPossible(int i, int j, int j_modified, int i2, int i3, int i4) {
        if (controleur.peutPlacerTuile(i, j, i2, j_modified, i3, i4)) {
            controleur.placeEtage(i, j, i2, j_modified, triplet[1][0], i3, i4, triplet[2][0]);
        }
    }

    private boolean possedeBatiment(int i,int j){
        return (jeu.getPlateau().getBatiment(i,j)==TOUR||jeu.getPlateau().getBatiment(i,j)==HUTTE ||jeu.getPlateau().getBatiment(i,j)==TEMPLE_SABLE||jeu.getPlateau().getBatiment(i,j)==TEMPLE_FORET
                ||jeu.getPlateau().getBatiment(i,j)==TEMPLE_PIERRE||jeu.getPlateau().getBatiment(i,j)==TEMPLE_PRAIRIE)&&(jeu.getPlateau().getTuile(i,j).getNumJoueur()==jeu.getNumJoueurCourant());
    }

    private ArrayList<Point2> positionsBatsVillage(int x,int y){
        ArrayList<Point2> listeDesHutesVoisines = new ArrayList<>();
        Point2 positionHutte = new Point2(x,y);
        listeDesHutesVoisines.add(positionHutte);
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2 HuteCourant = listeDesHutesVoisines.get(i);
            if(jeu.getPlateau().check(HuteCourant.getPointX()-1 ,HuteCourant.getPointY(),jeu.getNumJoueurCourant())){
                Point2 p1 = new Point2(HuteCourant.getPointX()-1 ,HuteCourant.getPointY());
                if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(jeu.getPlateau().check(HuteCourant.getPointX()+1 ,HuteCourant.getPointY(),jeu.getNumJoueurCourant())){
                Point2 p1 = new Point2(HuteCourant.getPointX()+1 ,HuteCourant.getPointY());
                if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(jeu.getPlateau().check(HuteCourant.getPointX() ,HuteCourant.getPointY()-1,jeu.getNumJoueurCourant())){
                Point2 p1 = new Point2(HuteCourant.getPointX() ,HuteCourant.getPointY()-1);
                if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(jeu.getPlateau().check(HuteCourant.getPointX() ,HuteCourant.getPointY()+1,jeu.getNumJoueurCourant())){
                Point2 p1 = new Point2(HuteCourant.getPointX() ,HuteCourant.getPointY()+1);
                if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(HuteCourant.getPointX()%2==1){
                if(jeu.getPlateau().check(HuteCourant.getPointX()-1 ,HuteCourant.getPointY()-1,jeu.getNumJoueurCourant())){
                    Point2 p1 = new Point2(HuteCourant.getPointX()-1 ,HuteCourant.getPointY()-1);
                    if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
                if(jeu.getPlateau().check(HuteCourant.getPointX()+1 ,HuteCourant.getPointY()-1,jeu.getNumJoueurCourant())){
                    Point2 p1 = new Point2(HuteCourant.getPointX()+1 ,HuteCourant.getPointY()-1);
                    if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
            }else{
                if(jeu.getPlateau().check(HuteCourant.getPointX()-1 ,HuteCourant.getPointY()+1,jeu.getNumJoueurCourant())){
                    Point2 p1 = new Point2(HuteCourant.getPointX()-1 ,HuteCourant.getPointY()+1);
                    if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
                if(jeu.getPlateau().check(HuteCourant.getPointX()+1 ,HuteCourant.getPointY()+1,jeu.getNumJoueurCourant())){
                    Point2 p1 = new Point2(HuteCourant.getPointX()+1 ,HuteCourant.getPointY()+1);
                    if(jeu.getPlateau().notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
            }
            //listeDesHutesVoisines.remove(i);
            i++;
        }
        //System.out.println("liste: "+listeDesHutesVoisines.size()+" id: "+jeu.getNumJoueurCourant());
        return listeDesHutesVoisines;
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
        value = updateScrollValue(value, coupsJouable);

        if (value == 1) { // place hut
            if(jeu.getPlateau().getHauteurTuile(i,j)>1 && !aCiteAutour(i,j)) return;
            enSelection = false;
            controleur.placeBatiment(i,j,(byte) 1);
        }
        else if (value == 2){ // place tour
            if(peutPoserTour(i,j)){ // on verifie la condition pour poser une tour
                enSelection = false;
                controleur.placeBatiment(i,j,(byte) 3);
            }
        }
        else if (value == 0){ // place temple
            if(peutPoserTemple(i,j)){
                enSelection = false;
                controleur.placeBatiment(i,j,(byte) 2);
            }
        }
    }

    public void addToCursor(MouseEvent e) {
        if(!jeu.estJoueurCourantUneIA()){
            if (SwingUtilities.isLeftMouseButton(e)) {
                actionsClicGauche(e);
            }
        }
    }

    private void actionsClicGauche(MouseEvent e) {
        int tileWidth = voidTile.getWidth();
        int tileHeight = voidTile.getWidth();
        int verticalOffset = (int) (tileHeight * 0.75);

        Point clickPositionAdjusted = new Point((int) ((e.getX() - cameraOffset.x) / zoomFactor),
                (int) ((e.getY() - cameraOffset.y) / zoomFactor));

        // Convertir les coordonnées du système de pixels en coordonnées du système de grille
        int i = clickPositionAdjusted.y / verticalOffset;
        int j = (clickPositionAdjusted.x + (i % 2 == 1 ? tileWidth / 2 : 0)) / tileWidth;
        //System.out.println("position souris x: "+i+" y: "+j);


        int[] coups = coupJouable(i,j);
        if(poseTile) placerTuiles(i,j);
        else if(coups[0]!=0 || coups[1]!=0 ||coups[2]!=0){
            placeBatiment(i, j);
        }
        unefoisIA=true;
        //miseAJour();
    }

    private void placeBatiment(int i, int j) {
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

    public void annuleConstruction(MouseEvent e){
        if (SwingUtilities.isRightMouseButton(e)) {
            if(enSelection){
                annulationConstruction();
            }
        }
    }

    private void annulationConstruction() {
        byte numJoueur = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getNumJoueur();
        byte hauteur = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getHauteur();
        byte terrain = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getBiomeTerrain();
        int volcan_i = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getLigneVolcan();
        int volcan_j = jeu.getPlateau().getPlateau()[posBat_x][posBat_y].getColonneVolcan();
        jeu.getPlateau().getPlateau()[posBat_x][posBat_y] = new Hexagone(numJoueur,hauteur,terrain,Hexagone.VIDE,(byte) volcan_i,(byte) volcan_j);
        enSelection=false;
    }

    public void boucle(){
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miseAJour();
            }
        });
        timer.start();
    }

}