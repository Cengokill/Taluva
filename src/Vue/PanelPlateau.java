package Vue;

import Controleur.ControleurMediateur;
import Modele.IA.AbstractIA;
import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Hexagone;
import Modele.Jeu.Jeu;
import Modele.Jeu.Plateau.Plateau;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Vue.FenetreJeuListener.MouseHandler;
import Vue.FenetreJeuListener.KeyboardListener;

import static Modele.Jeu.Jeu.annule;
import static Vue.Camera.*;
import static Modele.Jeu.Plateau.EtatPlateau.*;
import static Modele.Jeu.Plateau.Hexagone.*;
import static Vue.ImageLoader.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

public class PanelPlateau extends JPanel {
    /////////////////////////////////////////////////////
    // HANDLER                                         //
    /////////////////////////////////////////////////////

    public MouseHandler mouseHandler;
    public KeyboardListener keyboardListener;

    final ControleurMediateur controleur;
    public final FenetreJeu fenetreJeu;
    public final Jeu jeu;
    private int index_bat_precedent=-1,posX_bat_precedent=-1,posY_bat_precedent=-1,indexMessageErreur=0,timerValue=0;

    private ArrayList<Position> emplacementPropagation;

    public final int HAUTEUR_ETAGE = 80;
    public final int HAUTEUR_OCEAN = 50;
    public int posX_selecteur_vert_depart = -10;
    public int posX_selecteur_vert;
    //vitesse en fonction des performances du PC
    Runtime runtime = Runtime.getRuntime();
    public int nombreCoeurs;
    public double memoireLibre;
    public boolean estSurBouton, estSurScoreboard;
    public int vitesse;

    ArrayList<TripletDePosition> afficheEmplacementPosable;


    public PanelPlateau(FenetreJeu t, ControleurMediateur controleur, Jeu jeu) {
        nombreCoeurs = runtime.availableProcessors();
        // Obtenir la référence à l'objet MemoryMXBean
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        // Obtenir les informations sur l'utilisation de la mémoire
        memoireLibre = memoryMXBean.getHeapMemoryUsage().getUsed()/1000000.0;
        if(nombreCoeurs <= 4 || memoireLibre <= 128){
            vitesse = 90;
        }else if(nombreCoeurs <= 6 || memoireLibre <= 256){
            vitesse = 60;
        }else{
            vitesse = 30;
        }
        //System.out.println("Nombre de coeurs : " + nombreCoeurs);
        //System.out.println("Memoire libre : " + memoireLibre + " Mo");
        this.fenetreJeu = t;
        this.controleur = controleur;
        this.jeu = jeu;
        this.setOpaque(false);


        initTripletHover();
        initCouleursJoueurs();
        estSurBouton = false;
        System.out.println("init ??");
        poseTile = true;
        afficheEmplacementPosable = new ArrayList<>();
        jeu.doitCalculerEmplacementPossible = true;
        initCameraPosition();

        boucle();

        cameraOffset.x -= fenetreJeu.frame.getWidth()/2 -1000;
        cameraOffset.y -= fenetreJeu.frame.getHeight()/2 -50;

    }

    @Override
    protected void paintComponent(Graphics g) {

        if (!ImageLoader.loaded) {
            return;
        }
        //if(!jeu.estFinPartie()){
        changerTuileAPoser();
        changerPoseTile();

        super.paintComponent(g);
        afficheBackground(cameraOffset.x, cameraOffset.y, g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(cameraOffset.x, cameraOffset.y);
        g2d.scale(zoomFactor, zoomFactor);
        displayHexagonMap(g);
        //n'affiche pas la tuile sur le curseur si c'est l'IA qui joue
        if (!select_menu_options && jeu.getJoueurs()[jeu.jCourant].getTypeJoueur()!= AbstractIA.IA && !estSurBouton && !clicBoutonPauseEchap && !estSurScoreboard && !jeu.estFinPartie()) {//&& jeu.estPiochee &&jeu.getTuileCourante()!=null
            System.out.println(poseTile);
            if(poseTile){
                if(jeu.aPiocher) displayHoverTile(g);
            }
            else displayHoverMaison(g);
        }

        //}else{
        // TODO Affiche fin de la partie
        //}
    }

    private void afficheBackground(int x, int y, Graphics g) {
        g.drawImage(background, x, y, 6000, 6000, null);
    }


    private void displayHexagonMap(Graphics g) {
        Hexagone[][] map = controleur.getPlateau();
        int tileWidth = voidTile.getWidth();
        int tileHeight = voidTile.getWidth();
        int verticalOffset = (int) (tileHeight * 0.75);

        parcoursPlateau(g, map, tileWidth, verticalOffset);
    }

    private void initCameraPosition() {
        cameraOffset.x = -2600;
        cameraOffset.y = -1750;

        cameraOffset.x += this.fenetreJeu.frame.getWidth() - 1366;
        cameraOffset.y += this.fenetreJeu.frame.getHeight() - 768;
    }

    private void initCouleursJoueurs() {
        couleurs_joueurs = new Color[4];
        couleurs_joueurs[0] = new Color(213, 9, 0, 166);
        couleurs_joueurs[1] = new Color(0, 120, 255, 204);
        couleurs_joueurs[2] = new Color(183, 0, 255, 127);
        couleurs_joueurs[3] = new Color(255, 185, 0, 127);
    }

    private void initTripletHover() {
        tuileAPoser[0][0] = VOLCAN;
        tuileAPoser[1][0] = VIDE;
        tuileAPoser[2][0] = VIDE;
        tuileAPoser[0][1] = 0;
        tuileAPoser[1][1] = 0;
        tuileAPoser[2][1] = 0;
    }


    public void changerTuileAPoser() {
        byte[] tuiles;
        tuiles = controleur.getTuileAPoser();
        tuileAPoser[1][0] = tuiles[0]; // tile 1
        tuileAPoser[2][0] = tuiles[1]; // tile 2


        tuileAPoser[0][1] = tuiles[2]; // volcan
        tuileAPoser[1][1] = tuiles[3]; // tile 1
        tuileAPoser[2][1] = tuiles[4]; // tile 2
    }


    public void affichetripletpossible(){
        System.out.println("on affiche");
        for(TripletDePosition t : jeu.getPlateau().getTripletsPossibles()){
            Plateau plateauCopie = jeu.getPlateau().copie();
            boolean affiche = false;
            if(plateauCopie.getHexagone(t.getVolcan().ligne(),t.getVolcan().colonne()).getBiomeTerrain() == VOLCAN || plateauCopie.getHexagone(t.getTile1().ligne(),t.getTile1().colonne()).getBiomeTerrain() == VOLCAN
                    || plateauCopie.getHexagone(t.getTile2().ligne(),t.getTile2().colonne()).getBiomeTerrain() == VOLCAN){
                plateauCopie.affiche();
                affiche=true;
            }
            if(affiche) System.out.println("("+ t.getVolcan().ligne()+", "+t.getVolcan().colonne()+") "+"("+ t.getTile1().ligne()+", "+t.getTile1().colonne()+") "+"("+ t.getTile2().ligne()+", "+t.getTile2().colonne()+") ");
            plateauCopie.placeEtage(jeu.getNumJoueurCourant(),t.getVolcan().ligne(),t.getVolcan().colonne(),t.getTile1().ligne(),t.getTile1().colonne(),GRASS,t.getTile2().ligne(),t.getTile2().colonne(),MONTAGNE);
            if(affiche) plateauCopie.affiche();
            //System.out.println("libre en : ");
            //System.out.println();
        }
    }

    public void miseAJour() {
        if(scrollValue==0 && jeu.doit_placer_tuile()) scrollValue = 1;
        if (annule) {
            return;
        }
        repaint();
    }

    private void changerPoseTile() {
        poseTile = jeu.doit_placer_tuile();
    }

    private void parcoursPlateau(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset) {
        if(jeu.doitCalculerEmplacementPossible){
            afficheEmplacementPosable = new ArrayList<>();
        }
        for (int ligne = 0; ligne < map.length; ligne++) {
            for (int colonne = 0 - 2; colonne < map[0].length; colonne++) {
                affiche(g, map, tileWidth, verticalOffset, ligne, colonne);
            }
        }
        jeu.doitCalculerEmplacementPossible = false;
    }

    private void affiche(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset, int ligne, int colonne) {
        if (ligne < 2 || colonne < 2 || ligne >= 58 || colonne >= 58) {
            return;
        }
        int tileId = map[ligne][colonne].getBiomeTerrain();
        afficheHexagone(g, map, tileWidth, verticalOffset, ligne, colonne, tileId);

        int x = colonne * tileWidth - (ligne % 2 == 1 ? tileWidth / 2 : 0);
        int y = ligne * verticalOffset;
        g.setFont(new Font("TimesRoman", Font.BOLD, 80));
        if(jeu.debug)  g.drawString("("+ligne+","+colonne+")",x+120, y+250);
    }

    private void afficheHexagone(Graphics g, Hexagone[][] map, int tileWidth, int verticalOffset, int ligne, int colonne, int tileId) {
        int x = colonne * tileWidth - (ligne % 2 == 1 ? tileWidth / 2 : 0);
        int y = ligne * verticalOffset;


        int heightoffset = calculHauteurAffichageHexagone(map, ligne, colonne);

        BufferedImage tile = getTileImageFromId(tileId, map[ligne][colonne].getNum(),true);
        g.drawImage(tile, x , y - heightoffset, null);

        afficheFiltresTileMode(g, map, ligne, colonne, x, y, heightoffset);
        afficheNumerosNiveaux(g, map, ligne, colonne, x, y, heightoffset);
        afficheContourParNiveau(g, map, ligne, colonne, x, y, heightoffset);
        afficheBatiments(g, map, ligne, colonne, x, y, heightoffset);
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

    private void contour(Graphics g, Hexagone[][] map, int ligne, int colonne, int xDrawPosition, int yDrawPosition, int heightoffset, int colonneDirection, Hexagone[] ligneHexagones, String direction) {
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


        if (estCoteTuile(colonneDirection, map[ligne], ligneHexagones[colonne])) {
            g.drawImage(contourTuile, xDrawPosition, yDrawPosition - heightoffset + 55, null);
        }

        if (estBordureHauteur(colonneDirection, map[ligne], ligneHexagones[colonne])) {
            drawContourEtages(g, colonne, xDrawPosition, yDrawPosition, heightoffset, ligneHexagones, contourEtage1, contourEtage2, contourEtage3);
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

    private void drawContourEtages(Graphics g, int colonne, int drawX, int drawY, int heightoffset, Hexagone[] ligneHexagone, BufferedImage contourEtage1, BufferedImage contourEtage2, BufferedImage contourEtage3) {
        if (ligneHexagone[colonne].getHauteur() == 1) {
            g.drawImage(contourEtage1, drawX, drawY - heightoffset + 55, null);
        } else if (ligneHexagone[colonne].getHauteur() == 2) {
            g.drawImage(contourEtage2, drawX, drawY - heightoffset + 55, null);
        } else if (ligneHexagone[colonne].getHauteur() == 3) {
            g.drawImage(contourEtage3, drawX, drawY - heightoffset + 55, null);
        }
    }

    private void afficheBatiments(Graphics g, Hexagone[][] map, int ligne, int colonne, int x, int y, int heightoffset) {
        BufferedImage tile;
        if (map[ligne][colonne].getBatiment() != CHOISIR_BATIMENT) {
            g.drawImage(getBatimentFromPlayerId(map[ligne][colonne].getColorJoueur(), (byte) map[ligne][colonne].getBatiment(),jeu.getPlateau().getHexagone(ligne,colonne).getBiomeTerrain(),jeu.getPlateau().getHauteurTuile(ligne,colonne)), x, y - heightoffset, null);

        } else if (map[ligne][colonne].getBatiment() == CHOISIR_BATIMENT) {
            int pos_x = x-choisirBat[0].getWidth()/2;
            int pos_y = y -300;
            int s = scrollValue%3;//0 : hutte, 1 : temple, 2 : tour
            boolean peutPlacerBatiment = false;
            //System.out.println("value : "+value);
            //if(value==1) value = 0;
            //else if(value==0) value = 1;
            int[] coups = coupJouable(ligne, colonne);
            peutPlacerBatiment = updateScrollValue(s, coups);

            if(coupJouable(ligne, colonne)[0]==0 && coupJouable(ligne, colonne)[1]==0 && coupJouable(ligne, colonne)[2]==0) return;
            if(peutPlacerBatiment && s==0){
                emplacementPropagation = new ArrayList<>();
                emplacementPropagation.add(new Position(ligne,colonne));
                ArrayList<Point2D> aPropager = jeu.getPlateau().previsualisePropagation(ligne,colonne, jeu.getJoueurCourant().getCouleur());
                while(aPropager.size()!=0) {
                    Point2D PosCourante = aPropager.remove(0);
                    int posPrevX = PosCourante.getPointY() * voidTile.getWidth() - (PosCourante.getPointX() % 2 == 1 ? voidTile.getWidth() / 2 : 0);
                    int posPrevY = PosCourante.getPointX() * (int) (voidTile.getWidth() * 0.75);
                    emplacementPropagation.add(new Position(posPrevX,posPrevY));
                }
            }else{
                emplacementPropagation = new ArrayList<>();
            }
            posX_bat_precedent = ligne;
            posY_bat_precedent = colonne;
            //index_bat_precedent = 1;
            g.drawImage(mouse_scroll, pos_x + choisirBat[7].getWidth()/2 - mouse_scroll.getWidth()/2, pos_y - choisirBat[7].getHeight(),mouse_scroll.getWidth()*2,mouse_scroll.getHeight()*2, null);
            afficheSelecteurBatiment(g, pos_x, pos_y, coups);
            afficheSelecteurVert(g, pos_x+posX_selecteur_vert, pos_y-10, coups);
        }
    }

    private void affichePrevisualisationPropagation(Graphics g){
        if(emplacementPropagation==null || emplacementPropagation.size()<=1) return;
        Position posBasic = emplacementPropagation.get(0);
        int nbHuttesDispo = jeu.getPlateau().nbHuttesDisponiblesJoueur -(jeu.getPlateau().getHauteurTuile(posBasic.ligne(),posBasic.colonne()));

        for(int i=0;i<emplacementPropagation.size();i++){
            Position posCourante = emplacementPropagation.get(i);
            // Convertir les coordonnées du système de pixels en coordonnées du système de grille
            int x = posCourante.colonne() / (int) (voidTile.getWidth() * 0.75);
            int y = (posCourante.ligne() + (i % 2 == 1 ? voidTile.getWidth() / 2 : 0)) / voidTile.getWidth();

            int hauteurCourante = jeu.getPlateau().getHauteurTuile(x,y);
            if(nbHuttesDispo>=hauteurCourante){
                g.drawImage(constructionMode, posCourante.ligne(), posCourante.colonne(), null);
                nbHuttesDispo-=hauteurCourante;
            }
        }
    }

    private void afficheSelecteurVert(Graphics g, int pos_x, int pos_y, int[] coups){
        g.drawImage(selecteur_vert, pos_x, pos_y, selecteur_vert.getWidth()*2, selecteur_vert.getHeight()*2, null);
    }

    private void afficheSelecteurBatiment(Graphics g, int pos_x, int pos_y, int[] coups) {
        if(coups[1]==0){//impossible de construire une hutte
            if(coups[0]==0){//impossible de construire un temple
                if(coups[2]==0){//impossible de construire une tour
                    g.drawImage(choisirBat[7], pos_x, pos_y,choisirBat[7].getWidth()*2,choisirBat[7].getHeight()*2, null);
                }else{//tour possible
                    g.drawImage(choisirBat[6], pos_x, pos_y,choisirBat[6].getWidth()*2,choisirBat[6].getHeight()*2, null);
                }
            }else{//temple possible
                if(coups[2]==0){//impossible de construire une tour
                    g.drawImage(choisirBat[5], pos_x, pos_y,choisirBat[5].getWidth()*2,choisirBat[5].getHeight()*2, null);
                }else{//tour possible
                    g.drawImage(choisirBat[4], pos_x, pos_y,choisirBat[4].getWidth()*2,choisirBat[4].getHeight()*2, null);
                }
            }
        }else{//hutte possible
            if(coups[0]==0){//impossible de construire un temple
                if(coups[2]==0){//impossible de construire une tour
                    g.drawImage(choisirBat[3], pos_x, pos_y,choisirBat[3].getWidth()*2,choisirBat[3].getHeight()*2, null);
                }else{//tour possible
                    g.drawImage(choisirBat[2], pos_x, pos_y,choisirBat[2].getWidth()*2,choisirBat[2].getHeight()*2, null);
                }
            }else{//temple possible
                if(coups[2]==0){//impossible de construire une tour
                    g.drawImage(choisirBat[1], pos_x, pos_y,choisirBat[1].getWidth()*2,choisirBat[1].getHeight()*2, null);
                }else{//tour possible
                    g.drawImage(choisirBat[0], pos_x, pos_y,choisirBat[0].getWidth()*2,choisirBat[0].getHeight()*2, null);
                }
            }
        }
    }

    private int calculHauteurAffichageHexagone(Hexagone[][] map, int ligne, int colonne) {
        int heightoffset = map[ligne][colonne].getHauteur();
        heightoffset *= HAUTEUR_ETAGE;

        if (map[ligne][colonne].getBiomeTerrain() == VIDE) {
            heightoffset -= HAUTEUR_OCEAN;
        }
        return heightoffset;
    }

    private void afficheNumerosNiveaux(Graphics g, Hexagone[][] map, int ligne, int colonne, int drawX, int drawY, int heightoffset) {
        if (mode_numero) {
            if (map[ligne][colonne].getHauteur() == 1) {
                g.drawImage(wrongTile1, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 2) {
                g.drawImage(wrongTile2, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 3) {
                g.drawImage(wrongTile3, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 4) {
                g.drawImage(wrongTile4, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 5) {
                g.drawImage(wrongTile5, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 6) {
                g.drawImage(wrongTile6, drawX, drawY - heightoffset + 5, null);
            }
            if (map[ligne][colonne].getHauteur() == 7) {
                g.drawImage(wrongTile7, drawX, drawY - heightoffset + 5, null);
            }
        }
    }

    private void afficheFiltresTileMode(Graphics g, Hexagone[][] map, int ligne, int colonne, int drawX, int drawY, int heightoffset) {
        if (poseTile) {
            afficherFiltreSombre(g, map, ligne, colonne, drawX, drawY, heightoffset);
            if(jeu.getJoueurCourant().getTypeJoueur()==Joueur.HUMAIN) {
                afficherFiltreVolcan(g, map, ligne, colonne, drawX, drawY, heightoffset);
            }
        }
    }

    private void afficherFiltreVolcan(Graphics g, Hexagone[][] map, int ligne, int colonne, int drawX, int drawY, int heightoffset) {
        int j2 = convertionTileMapToHexagonal(ligne, colonne);
        if(jeu.doitCalculerEmplacementPossible){
            calculDirectionsLibres(ligne, colonne, drawX, drawY, heightoffset - 50, j2);
        }
        if (map[ligne][colonne].getBiomeTerrain() == VOLCAN) {
            illumineVolcanLibre(g, ligne, colonne, drawX, drawY, heightoffset - 50, j2);
            afficheDirectionsLibres(g, drawX, drawY, heightoffset - 50);
        } else if (map[ligne][colonne].getBiomeTerrain() == VIDE || map[ligne][colonne].getBiomeTerrain() == WATER) {
            afficheDirectionsLibres(g, drawX, drawY, heightoffset - 50);
        }
        /*if (map[ligne][colonne].placable) {
            g.drawImage(placable, drawX, drawY - heightoffset + 50, null);
        }*/
    }

    private void afficherFiltreSombre(Graphics g, Hexagone[][] map, int ligne, int colonne, int drawX, int drawY, int heightoffset) {
        if (hoveredTile_y < 2 || hoveredTile_x < 2 || hoveredTile_y >= 58 || hoveredTile_x >= 58) {
            return;
        }
        if (mode_plateau) {
            if (map[ligne][colonne].getHauteur() != map[hoveredTile_x][hoveredTile_y].getHauteur()) {
                if (map[hoveredTile_x][hoveredTile_y].getHauteur() != 0) {
                    g.drawImage(voidTile_transparent, drawX, drawY - heightoffset + 5, null);
                }
            }
        }
    }

    private int convertionTileMapToHexagonal(int ligne, int colonne) {
        int colonneAjustee;
        if (ligne % 2 == 1) {
            colonneAjustee = colonne - 1;
        } else {
            colonneAjustee = colonne;
        }
        return colonneAjustee;
    }
    private void afficheDirectionsLibres(Graphics g, int volcanDrawX, int volcanDrawY, int heightoffset) {
        for(TripletDePosition tripletCourant : afficheEmplacementPosable){
            if(tripletCourant.getVolcan().ligne()==volcanDrawX && tripletCourant.getVolcan().colonne()==volcanDrawY && tripletCourant.getTile1().ligne() == heightoffset){
                g.drawImage(placable, tripletCourant.getVolcan().ligne(), tripletCourant.getVolcan().colonne() - tripletCourant.getTile1().ligne(), null);
            }
        }
    }
    private void calculDirectionsLibres(int ligne, int colonne, int volcanDrawX, int volcanDrawY, int heightoffset, int colonneAjustee) {
        if ((controleur.peutPlacerTuile(ligne, colonne, ligne - 1, colonneAjustee, ligne - 1, colonneAjustee + 1)) == 0) {
            jeu.getPlateau().getCarte()[ligne - 1][colonneAjustee].placable = true;
            jeu.getPlateau().getCarte()[ligne - 1][colonneAjustee + 1].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne - 1, colonneAjustee + 1, ligne, colonne + 1)) == 0) {
            jeu.getPlateau().getCarte()[ligne - 1][colonneAjustee + 1].placable = true;
            jeu.getPlateau().getCarte()[ligne][colonneAjustee + 1].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne, colonne + 1, ligne + 1, colonneAjustee + 1)) == 0) {
            jeu.getPlateau().getCarte()[ligne][colonne + 1].placable = true;
            jeu.getPlateau().getCarte()[ligne + 1][colonneAjustee + 1].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne + 1, colonneAjustee + 1, ligne + 1, colonneAjustee)) == 0) {
            jeu.getPlateau().getCarte()[ligne + 1][colonneAjustee + 1].placable = true;
            jeu.getPlateau().getCarte()[ligne + 1][colonneAjustee + 1].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne + 1, colonneAjustee, ligne, colonne - 1)) == 0) {
            jeu.getPlateau().getCarte()[ligne + 1][colonneAjustee].placable = true;
            jeu.getPlateau().getCarte()[ligne][colonne - 1].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne, colonne - 1, ligne - 1, colonneAjustee)) == 0) {
            jeu.getPlateau().getCarte()[ligne][colonne - 1].placable = true;
            jeu.getPlateau().getCarte()[ligne - 1][colonneAjustee].placable = true;
            afficheEmplacementPosable.add(new TripletDePosition(new Position(volcanDrawX, volcanDrawY), new Position(heightoffset, 0), new Position(0, 0)));
            //g.drawImage(placable, volcanDrawX, volcanDrawY - heightoffset, null);
        }
    }

    private void illumineVolcanLibre(Graphics g, int ligne, int colonne, int volcanDrawX, int volcanDrawY, int heightoffset, int colonneAjustee) {
        if ((controleur.peutPlacerTuile(ligne, colonne, ligne - 1, colonneAjustee, ligne - 1, colonneAjustee + 1))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne - 1, colonneAjustee + 1, ligne, colonne + 1))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne, colonne + 1, ligne + 1, colonneAjustee + 1))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne + 1, colonneAjustee + 1, ligne + 1, colonneAjustee))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne + 1, colonneAjustee, ligne, colonne - 1))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        } else if ((controleur.peutPlacerTuile(ligne, colonne, ligne, colonne - 1, ligne - 1, colonneAjustee))==0) {
            g.drawImage(whiteTile, volcanDrawX, volcanDrawY - heightoffset + 5, null);
        }
    }

    private boolean estTemple(int ligne,int colonne){
        return jeu.getPlateau().getBatiment(ligne,colonne) == TEMPLE;
    }

    private boolean estTour(int ligne,int colonne){
        return jeu.getPlateau().getBatiment(ligne, colonne) == TOUR;
    }

    private ArrayList<Point2D> copyPoints(ArrayList<Point2D> tab1) {
        ArrayList<Point2D> tab2 = new ArrayList<>();
        tab2.addAll(tab1);
        return tab2;
    }

    private boolean peutPoserTemple(int i,int j){
        return jeu.getPlateau().peutPoserTemple(i,j,jeu.getJoueurCourant().getCouleur());
    }

    private boolean peutPoserTour(int i,int j){
        return jeu.getPlateau().peutPoserTour(i,j, jeu.getJoueurCourant().getCouleur());
    }

    public int[] coupJouable(int i,int j){
        int[] coups = jeu.getPlateau().getBatimentPlacable(i,j, jeu.getJoueurCourant().getCouleur());

        int hauteurTuile = jeu.getPlateau().getHauteurTuile(i,j);
        if(jeu.getJoueurCourantClasse().getNbTemples()<=0) coups[0] = 0;
        if(jeu.getJoueurCourantClasse().getNbTours()<=0) coups[2] = 0;
        if(jeu.getJoueurCourantClasse().getNbHuttes()<hauteurTuile) coups[1] = 0;

        return coups;
    }

    public void detectionPlusAucunCoupAJouer(){
        jeu.isJoueurCourantPerdu();
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
            BufferedImage tile1 = null;
            BufferedImage tile2 = null;
            BufferedImage tile3 = null;

            if (scrollValue == 1) {
                g.drawImage(ombre_0, x-tileWidth/2-40, y-verticalOffset/2-50, (int) (tileWidth*2.2),(int) (tileHeight*2.2),null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(),false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i - 1][j2].getNum(),false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i - 1][j2 + 1].getNum(),false);
            }
            else if (scrollValue == 2){
                g.drawImage(ombre_1, x-tileWidth/2+175, y-verticalOffset/2-50,(int) (tileWidth*2.2),(int) (tileHeight*2.2), null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(),false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i - 1][j2 + 1].getNum(),false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i][j + 1].getNum(),false);
            }
            else if (scrollValue == 3){
                g.drawImage(ombre_0, x-tileWidth/2+185, y-verticalOffset/2+265,(int) (tileWidth*2.2),(int) (tileHeight*2.2), null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(), false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i][j + 1].getNum(), false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i + 1][j2 + 1].getNum(), false);
            }
            else if (scrollValue == 4){
                g.drawImage(ombre_1, x-tileWidth/2-50, y-verticalOffset/2+300,(int) (tileWidth*2.2),(int) (tileHeight*2.2), null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(), false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i + 1][j2 + 1].getNum(), false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i + 1][j2].getNum(), false);
            }
            else if (scrollValue == 5){
                g.drawImage(ombre_0, x-tileWidth/2-265, y-verticalOffset/2+260,(int) (tileWidth*2.2),(int) (tileHeight*2.2), null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(), false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i + 1][j2].getNum(), false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i][j - 1].getNum(), false);
            }
            else if (scrollValue == 6){
                g.drawImage(ombre_1, x-tileWidth/2-270, y-verticalOffset/2-50,(int) (tileWidth*2.2),(int) (tileHeight*2.2), null);
                tile1 = getTileImageFromId(tuileAPoser[0][0], jeu.getPlateau().getCarte()[i][j].getNum(), false);
                tile2 = getTileImageFromId(tuileAPoser[1][0], jeu.getPlateau().getCarte()[i][j - 1].getNum(), false);
                tile3 = getTileImageFromId(tuileAPoser[2][0], jeu.getPlateau().getCarte()[i - 1][j2].getNum(), false);
            }

            float opacity = 1f;

            if (tile1 != null && tile2 != null && tile3 != null) {
                opacity = updateOpacite(i, j, j2, opacity);
                if (opacity != 1f) {
                    tile1 = reduceOpacity(tile1,opacity);
                    tile1 = applyRedFilter(tile1);
                    tile2 = reduceOpacity(tile2,opacity);
                    tile2 = applyRedFilter(tile2);
                    tile3 = reduceOpacity(tile3,opacity);
                    tile3 = applyRedFilter(tile3);
                }
            }

            y -= jeu.getPlateau().getCarte()[i][j].getHauteur() * HAUTEUR_ETAGE;
            afficheTilesHover(g, tileWidth, verticalOffset, x, y, tile1, tile2, tile3, opacity);
        }
    }

    public static BufferedImage reduceOpacity(BufferedImage image, float opacity) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Crée une nouvelle BufferedImage avec le même type d'image et la même taille
        BufferedImage reducedOpacityImage = new BufferedImage(width, height, image.getType());

        // Obtenir un objet Graphics2D pour la nouvelle image
        Graphics2D g2d = reducedOpacityImage.createGraphics();

        // Spécifie le composite avec la nouvelle opacité
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Dessiner l'image d'origine avec la nouvelle opacité
        g2d.drawImage(image, 0, 0, null);

        // Libérer les ressources du Graphics2D
        g2d.dispose();

        return reducedOpacityImage;
    }

    private void afficheTilesHover(Graphics g, int tileWidth, int verticalOffset, int drawX, int drawY, BufferedImage tile1, BufferedImage tile2, BufferedImage tile3, float opacity) {
        int heightoffset1 = 1;
        int heightoffset2 = 1;
        int heightoffset3 = 1;
        heightoffset1 *= 60;
        heightoffset2 *= 60;
        heightoffset3 *= 60;

        if (scrollValue == 1) {
            g.drawImage(tile2, drawX - tileWidth /2, drawY - verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, drawX + tileWidth /2, drawY - verticalOffset - heightoffset3, null);
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX - tileWidth /2, drawY - verticalOffset -  heightoffset2 + 50, null);
            g.drawImage(placable, drawX + tileWidth /2, drawY - verticalOffset - heightoffset3 + 50, null);
            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);
        }
        else if (scrollValue == 2){
            g.drawImage(tile2, drawX + tileWidth /2, drawY - verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, drawX + tileWidth, drawY - heightoffset3, null);
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX + tileWidth /2, drawY - verticalOffset -  heightoffset2 + 50, null);
            g.drawImage(placable, drawX + tileWidth, drawY - heightoffset3 + 50, null);
            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);

        }
        else if (scrollValue == 3){
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);
            g.drawImage(tile2, drawX + tileWidth, drawY -  heightoffset2, null);
            g.drawImage(tile3, drawX +  tileWidth /2, drawY + verticalOffset - heightoffset3, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);
            g.drawImage(placable, drawX + tileWidth, drawY -  heightoffset2 + 50, null);
            g.drawImage(placable, drawX +  tileWidth /2, drawY + verticalOffset - heightoffset3 + 50, null);
        }
        else if (scrollValue == 4){
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);
            g.drawImage(tile2, drawX + tileWidth /2, drawY + verticalOffset -  heightoffset2, null);
            g.drawImage(tile3, drawX - tileWidth /2, drawY + verticalOffset - heightoffset3, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);
            g.drawImage(placable, drawX + tileWidth /2, drawY + verticalOffset -  heightoffset2 + 50, null);
            g.drawImage(placable, drawX - tileWidth /2, drawY + verticalOffset - heightoffset3 + 50, null);
        }
        else if (scrollValue == 5){
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);
            g.drawImage(tile3, drawX - tileWidth, drawY - heightoffset3, null);
            g.drawImage(tile2, drawX - tileWidth /2, drawY + verticalOffset -  heightoffset2, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);
            g.drawImage(placable, drawX - tileWidth, drawY - heightoffset3 + 50, null);
            g.drawImage(placable, drawX - tileWidth /2, drawY + verticalOffset -  heightoffset2 + 50, null);
        }
        else if (scrollValue == 6){
            g.drawImage(tile3, drawX - tileWidth /2, drawY - verticalOffset - heightoffset3, null);
            g.drawImage(tile2, drawX - tileWidth, drawY -  heightoffset2, null);
            g.drawImage(tile1, drawX, drawY - heightoffset1, null);

            if (tile1 == tileErreur) {
                return;
            }

            g.drawImage(placable, drawX - tileWidth /2, drawY - verticalOffset - heightoffset3 + 50, null);
            g.drawImage(placable, drawX - tileWidth, drawY -  heightoffset2 + 50, null);
            g.drawImage(placable, drawX, drawY - heightoffset1 + 50, null);
        }
    }

    private float updateOpacite(int ligne, int colonne, int colonneAjustee, float opacity) {
        if (scrollValue == 1) {
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonneAjustee, opacity, ligne - 1, ligne - 1, colonneAjustee + 1);
        }
        else if (scrollValue == 2){
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonneAjustee + 1, opacity, ligne - 1, ligne, colonne + 1);
        }
        else if (scrollValue == 3){
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonne + 1, opacity, ligne, ligne + 1, colonneAjustee + 1);
        }
        else if (scrollValue == 4){
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonneAjustee + 1, opacity, ligne + 1, ligne + 1, colonneAjustee);
        }
        else if (scrollValue == 5){
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonneAjustee, opacity, ligne + 1, ligne, colonne - 1);
        }
        else if (scrollValue == 6){
            opacity = changeOpacitePeutPasplacerTuile(ligne, colonne, colonne - 1, opacity, ligne, ligne - 1, colonneAjustee);
        }
        return opacity;
    }

    private float changeOpacitePeutPasplacerTuile(int i, int j, int j2, float opacity, int i2, int i3, int i4) {
        if (controleur.peutPlacerTuile(i, j, i2, j2, i3, i4)!=0){
            opacity = 0.4f;
        }
        return opacity;
    }

    public int getIndexMessageErreur(){
        return indexMessageErreur;
    }

    public void resetIndexMessageErreur(){
        indexMessageErreur=0;
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

            int heightoffset1 = jeu.getPlateau().getCarte()[i][j].getHauteur() * 60;

            if(!enSelection){
                if(coupJouable(i,j)[0]==0 && coupJouable(i,j)[1]==0 && coupJouable(i,j)[2]==0) return;
                if(jeu.getPlateau().getHexagone(i,j).getBatiment()==0 && jeu.getPlateau().getHexagone(i,j).getBiomeTerrain() != VOLCAN && jeu.getPlateau().getHexagone(i,j).getBiomeTerrain() != VIDE){
                    g.drawImage(constructionMode, x+50 , y - heightoffset1, (int)(tileWidth/1.2), (int) (tileWidth/1.2) ,null);
                }
            }
        }
    }

    public void placerTuiles(int i, int j) {
        if(select_menu_options || clicBoutonPauseEchap || estSurBouton) return;
        //System.out.println("placerTuiles");
        int j_modified = convertionTileMapToHexagonal(i, j);

        if (scrollValue == 1) {
            if (placeEtageSiPossible(i, j, j_modified, i - 1, i - 1, j_modified + 1)) scrollValue = 0;
        }
        else if (scrollValue == 2){
            if (placeEtageSiPossible(i, j, j_modified + 1, i - 1, i, j + 1)) scrollValue = 0;
        }
        else if (scrollValue == 3){
            if (placeEtageSiPossible(i, j, j + 1, i, i + 1, j_modified + 1)) scrollValue = 0;
        }
        else if (scrollValue == 4){
            if (placeEtageSiPossible(i, j, j_modified + 1, i + 1, i + 1, j_modified)) scrollValue = 0;
        }
        else if (scrollValue == 5){
            if (placeEtageSiPossible(i, j, j_modified, i + 1, i, j - 1)) scrollValue = 0;
        }
        else if (scrollValue == 6){
            if(placeEtageSiPossible(i, j, j - 1, i, i - 1, j_modified)) scrollValue = 0;
        }
    }

    private boolean placeEtageSiPossible(int i, int j, int j_modified, int i2, int i3, int i4) {
        indexMessageErreur = controleur.peutPlacerTuile(i, j, i2, j_modified, i3, i4);
        timerValue=0;
        if (indexMessageErreur==0) {
            controleur.placeEtage(i, j, i2, j_modified, tuileAPoser[1][0], i3, i4, tuileAPoser[2][0]);
            jeu.playSons(0);
            detectionPlusAucunCoupAJouer();
            return true;
        }else{
            fenetreJeu.playSons(3);
        }
        return false;
    }

    public int getTimerValue(){
        return timerValue;
    }

    private boolean possedeBatiment(int i,int j){
        //return ((jeu.getPlateau().getBatiment(i,j)==TOUR||jeu.getPlateau().getBatiment(i,j)==HUTTE ||jeu.getPlateau().getBatiment(i,j)==TEMPLE)&&(jeu.getPlateau().getHexagone(i,j).getNumJoueur()==jeu.getNumJoueurCourant()));
        return ((jeu.getPlateau().getBatiment(i,j)!=0)&&(jeu.getPlateau().getHexagone(i,j).getColorJoueur()==jeu.getJoueurCourant().getCouleur()));
    }

    private boolean aCiteAutour(int i,int j){
        boolean bool = possedeBatiment(i-1,j)||possedeBatiment(i+1,j)||possedeBatiment(i,j-1)||possedeBatiment(i,j+1);
        if(i%2==1) return (possedeBatiment(i-1,j-1) || possedeBatiment(i+1,j-1) || bool);
        else return (possedeBatiment(i-1,j+1) || possedeBatiment(i+1,j+1) || bool);
    }

    private void decomptePropagation(){
        if(emplacementPropagation==null || emplacementPropagation.size()<=1) return;
        Position posBasic = emplacementPropagation.get(0);
        int nbHuttesDispo = jeu.getPlateau().nbHuttesDisponiblesJoueur -(jeu.getPlateau().getHauteurTuile(posBasic.ligne(),posBasic.colonne()));

        for(int i=0;i<emplacementPropagation.size();i++){
            Position posCourante = emplacementPropagation.get(i);
            // Convertir les coordonnées du système de pixels en coordonnées du système de grille
            int x = posCourante.colonne() / (int) (voidTile.getWidth() * 0.75);
            int y = (posCourante.ligne() + (i % 2 == 1 ? voidTile.getWidth() / 2 : 0)) / voidTile.getWidth();

            int hauteurCourante = jeu.getPlateau().getHauteurTuile(x,y);
            if(nbHuttesDispo>=hauteurCourante){
                jeu.incrementePropagation(x,y);
                nbHuttesDispo-=hauteurCourante;
            }
        }
    }

    private boolean updateScrollValue(int value, int[] coups) {
        //coups[0] : temple, coups[1] : hutte, coups[2] : tour
        if(value==0){//si on veut placer une hutte
            return coups[1]==1;//si on peut placer une hutte
        }else if(value==1){//si on veut placer un temple
            return coups[0]==1;//si on peut placer un temple
        }else{//si on veut placer une tour
            return coups[2]==1;//si on peut placer une tour
        }
    }

    public void placerBatiment(int i, int j) {
        int s = scrollValue%3;
        int[] coupsJouable = coupJouable(i,j);
        if(updateScrollValue(s, coupsJouable)) {
            if (s == 0) { // place une hutte
                if (jeu.getPlateau().getHauteurTuile(i, j) > 1 && !aCiteAutour(i, j)) return;
                enSelection = false;
                decomptePropagation();
                controleur.placeBatiment(i, j, Coup.HUTTE);
            } else if (s == 2) { // place une tour
                System.out.println("PLACE UNE TOUR");
                if (peutPoserTour(i, j)) { // on verifie la condition pour poser une tour
                    enSelection = false;
                    controleur.placeBatiment(i, j, Coup.TOUR);
                }
            } else if (s == 1) { // place un temple
                if (peutPoserTemple(i, j)) {
                    enSelection = false;
                    controleur.placeBatiment(i, j, Coup.TEMPLE);

                }
            }
            scrollValue = 1;//on met la valeur de scrollValue à 1 car si elle est à 0 la prochaine tuile ne s'affichera pas
            jeu.getJoueurCourant().stopChrono();
            jeu.changeJoueur();
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
        //System.out.println("SOURIS i: "+i+" j: "+j);
        //System.out.println("aCiterAutour: "+jeu.getPlateau().aCiteAutour(i,j,jeu.getNumJoueurCourant()));

        if(!fenetreJeu.retourDebug){
            if(poseTile) placerTuiles(i,j);
            else placeBatiment(i, j);
        }
        fenetreJeu.retourDebug = false;
        //jeu.unefoisIA=true; // POUR IA mettre en commentaire
    }


    private void placeBatiment(int i, int j) {
        jeu.isJoueurCourantPerdu();
        if(jeu.estFinPartie()) {
            jeu.getJoueurCourant().stopChrono();
            return;
        }
        if(!enSelection){
            int[] coups = coupJouable(i,j);
            if (controleur.peutPlacerBatiment(i, j)&&(coups[0]!=0||coups[1]!=0||coups[2]!=0)) {
                posBat_x = i;
                posBat_y = j;
                enSelection = true;
                controleur.placeBatiment(posBat_x, posBat_y, Coup.SELECTEUR_BATIMENT);
            }else{//pas de bâtiment à placer, le joueur a perdu
                jeu.getJoueurCourant().stopChrono();
            }
        }else{
            placerBatiment(posBat_x,posBat_y);
            resetPrevisualisationPropagation();
        }
    }

    public void annuleConstruction(MouseEvent e){
        if (SwingUtilities.isRightMouseButton(e)) {
            if(enSelection){
                annulationConstruction();
            }
        }
    }

    private void resetPrevisualisationPropagation(){
        emplacementPropagation = new ArrayList<>();
        posX_bat_precedent=-1;
        posY_bat_precedent=-1;
        index_bat_precedent=-1;
    }

    private void annulationConstruction() {
        Color color_joueur = jeu.getPlateau().getCarte()[posBat_x][posBat_y].getColorJoueur();
        byte hauteur = jeu.getPlateau().getCarte()[posBat_x][posBat_y].getHauteur();
        byte terrain = jeu.getPlateau().getCarte()[posBat_x][posBat_y].getBiomeTerrain();
        int volcan_i = jeu.getPlateau().getCarte()[posBat_x][posBat_y].getLigneVolcan();
        int volcan_j = jeu.getPlateau().getCarte()[posBat_x][posBat_y].getColonneVolcan();
        jeu.getPlateau().getCarte()[posBat_x][posBat_y] = new Hexagone(color_joueur,hauteur,terrain,Hexagone.VIDE,(byte) volcan_i,(byte) volcan_j);
        resetPrevisualisationPropagation();
        enSelection=false;
    }

    public void boucle(){
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miseAJour();
                int s = scrollValue%3;
                if(s==0){//si le sélecteur doit se placer sur la hutte
                    if(provenanceScroll == 1){//si le sélecteur était à droite
                        posX_selecteur_vert = Math.max(posX_selecteur_vert_depart, posX_selecteur_vert-vitesse);
                    }else{
                        posX_selecteur_vert = posX_selecteur_vert_depart;
                    }
                }else if(s==1){//si le sélecteur doit se placer sur le temple
                    if(provenanceScroll == 1){//si le sélecteur était à droite
                        posX_selecteur_vert = Math.max(posX_tiers_selecteur_vert, posX_selecteur_vert-vitesse);
                    }else if(provenanceScroll == 2){//si le sélecteur était à gauche
                        posX_selecteur_vert = Math.min(posX_tiers_selecteur_vert, posX_selecteur_vert+vitesse);
                    }else{
                        posX_selecteur_vert = posX_tiers_selecteur_vert;
                    }
                }else{//si le sélecteur doit se placer sur la tour
                    if(provenanceScroll == 2) {//si le sélecteur était à gauche
                        posX_selecteur_vert = Math.min(posX_tiers_selecteur_vert * 2, posX_selecteur_vert + vitesse);
                    }else{
                        posX_selecteur_vert = posX_tiers_selecteur_vert * 2;
                    }
                }
                timerValue+=10;
            }
        });
        timer.start();
    }

}