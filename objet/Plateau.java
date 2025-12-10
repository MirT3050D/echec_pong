package objet;
import java.awt.Rectangle;

public class Plateau {
    private int id;
    private int nbPiece;
    private Piece[][] pieces;
    private Rectangle[][] cases; // Ajouté pour représenter les cases du plateau

    public Plateau(int id, int nbPiece, Piece[][] pieces) {
        this.id = id;
        this.nbPiece = nbPiece;
        this.pieces = pieces;
        this.cases = new Rectangle[2][nbPiece * 4];
    }

    public int getId() { return id; }
    public int getNbPiece() { return nbPiece; }
    public Piece[][] getPieces() { return pieces; }

    public void setNbPiece(int nbPiece) { this.nbPiece = nbPiece; }
    public void setPieces(Piece[][] pieces) { this.pieces = pieces; }
    public Rectangle[][] getCases() { return cases; }
    public void setCases(Rectangle[][] cases) { this.cases = cases; }
}