// ...existing code...
// Retourne un tableau 2D de pièces vivantes (vie > 0), triées par position croissante pour chaque joueur

package objet;

import java.awt.Rectangle;

public class Plateau {
    private static final int largeur = 8; // largeur fixe (colonnes)
    private int id;
    private int nbPiece;
    private Piece[][] pieces;
    private Piece[][] piecesInitiales; // tableau fixe après filtrage initial
    private Rectangle[][][] cases; // cases[joueur][ligne][colonne]
    private Raquette[] raquettes; // raquettes[0]=joueur0, raquettes[1]=joueur1

    public Plateau(int id, int nbPiece, Piece[][] pieces) {
        this.id = id;
        this.nbPiece = nbPiece;
        this.pieces = pieces;
        // Filtre une seule fois les pièces vivantes au départ
        this.piecesInitiales = new Piece[pieces.length][];
        for (int joueur = 0; joueur < pieces.length; joueur++) {
            if (pieces[joueur] == null) {
                piecesInitiales[joueur] = new Piece[0];
                continue;
            }
            java.util.List<Piece> list = new java.util.ArrayList<>();
            for (Piece p : pieces[joueur]) {
                if (p != null && p.getVie() > 0) {
                    list.add(p);
                }
            }
            piecesInitiales[joueur] = list.toArray(new Piece[0]);
        }
        // Calcul du nombre de rangées par joueur (arrondi supérieur)
        this.cases = new Rectangle[2][largeur][nbPiece];
        this.raquettes = new Raquette[2];
        // Initialisation des raquettes (position minimale, largeur 1, angle 0, couleurs
        // différentes)
        int largeurRaquette = 1;
        int minCol = (int) Math.ceil(largeurRaquette / 2.0);
        this.raquettes[0] = new Raquette(0, 0, minCol, 0, largeurRaquette, java.awt.Color.BLUE);
        this.raquettes[1] = new Raquette(1, 1, minCol, 0, largeurRaquette, java.awt.Color.RED);
        this.raquettes[0].setXPixel(minCol);
        this.raquettes[1].setXPixel(minCol);
    }

    public Raquette[] getRaquettes() {
        return raquettes;
    }

    public void setRaquettes(Raquette[] raquettes) {
        this.raquettes = raquettes;
    }

    public int getId() {
        return id;
    }

    public int getNbPiece() {
        return nbPiece;
    }

    public int getlargeur() {
        return largeur;
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void setNbPiece(int nbPiece) {
        this.nbPiece = nbPiece;
    }

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }

    public Rectangle[][][] getCases() {
        return cases;
    }

    public void setCases(Rectangle[][][] cases) {
        this.cases = cases;
    }

    public Piece[][] getPiecesVivantesTriees() {
        // Retourne le tableau fixe créé au départ (filtré une seule fois)
        // Les pièces qui meurent pendant le jeu restent dans ce tableau avec vie=0
        // L'affichage et la collision les sautent simplement
        return piecesInitiales;
    }

    /**
     * Assigne/réassure la position de chaque pièce en fonction de son index
     * dans le tableau `pieces[joueur]`. Cette méthode est prévue pour être
     * appelée une seule fois à l'initialisation afin d'établir des positions
     * cohérentes sans ré-affecter ces positions pendant le jeu.
     */
    public void normalizePositions() {
        if (pieces == null) return;
        for (int joueur = 0; joueur < pieces.length; joueur++) {
            Piece[] arr = pieces[joueur];
            if (arr == null) continue;
            for (int i = 0; i < arr.length; i++) {
                Piece p = arr[i];
                if (p != null) {
                    p.setPosition(i);
                }
            }
        }
    }

    /**
     * Vérifie si le roi d'un joueur est mort
     * @param joueur numéro du joueur (0 ou 1)
     * @return true si le roi du joueur est mort (vie <= 0)
     */
    public boolean isRoiMort(int joueur) {
        if (joueur < 0 || joueur >= pieces.length || pieces[joueur] == null) {
            return false;
        }
        for (Piece p : pieces[joueur]) {
            if (p != null && p.getNom().equalsIgnoreCase("roi")) {
                return p.getVie() <= 0;
            }
        }
        return false;
    }

    /**
     * Retourne le joueur gagnant (-1 si pas de gagnant, 0 ou 1 si un roi est mort)
     * @return 0 si joueur 1 a gagné (roi du joueur 0 est mort), 1 si joueur 0 a gagné, -1 sinon
     */
    public int getGagnant() {
        if (isRoiMort(0)) return 1; // Joueur 1 a gagné
        if (isRoiMort(1)) return 0; // Joueur 0 a gagné
        return -1; // Pas de gagnant
    }
}