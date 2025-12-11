package reseau;

import java.io.Serializable;

/**
 * État d'une pièce (position, vie)
 */
public class PieceState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int joueur;
    private int indexPiece;
    private int vie;
    
    public PieceState(int joueur, int indexPiece, int vie) {
        this.joueur = joueur;
        this.indexPiece = indexPiece;
        this.vie = vie;
    }
    
    public int getJoueur() { return joueur; }
    public int getIndexPiece() { return indexPiece; }
    public int getVie() { return vie; }
    
    @Override
    public String toString() {
        return "PieceState{joueur=" + joueur + ", index=" + indexPiece + ", vie=" + vie + "}";
    }
}
