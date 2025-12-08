package objet;

public class Plateau {
    private int id;
    private int nbPiece;
    private Piece[][] pieces;

    public Plateau(int id, int nbPiece, Piece[][] pieces) {
        this.id = id;
        this.nbPiece = nbPiece;
        this.pieces = pieces;
    }

    public int getId() { return id; }
    public int getNbPiece() { return nbPiece; }
    public Piece[][] getPieces() { return pieces; }

    public void setNbPiece(int nbPiece) { this.nbPiece = nbPiece; }
    public void setPieces(Piece[][] pieces) { this.pieces = pieces; }
}