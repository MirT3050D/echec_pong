    // Retourne un tableau 2D de pièces vivantes (vie > 0), triées par position croissante pour chaque joueur

package objet;
import java.awt.Rectangle;


public class Plateau {
    private static final int largeur = 8; // largeur fixe (colonnes)
    private int id;
    private int nbPiece;
    private Piece[][] pieces;
    private Rectangle[][][] cases; // cases[joueur][ligne][colonne]

    public Plateau(int id, int nbPiece, Piece[][] pieces) {
        this.id = id;
        this.nbPiece = nbPiece;
        this.pieces = pieces;
        // Calcul du nombre de rangées par joueur (arrondi supérieur)
        this.cases = new Rectangle[2][largeur][nbPiece];
    }

    public int getId() { return id; }
    public int getNbPiece() { return nbPiece; }
    public int getlargeur() { return largeur; }
    public Piece[][] getPieces() { return pieces; }

    public void setNbPiece(int nbPiece) { this.nbPiece = nbPiece; }
    public void setPieces(Piece[][] pieces) { this.pieces = pieces; }
    public Rectangle[][][] getCases() { return cases; }
    public void setCases(Rectangle[][][] cases) { this.cases = cases; }

        public Piece[][] getPiecesVivantesTriees() {
        Piece[][] vivantes = new Piece[pieces.length][];
        for (int joueur = 0; joueur < pieces.length; joueur++) {
            java.util.List<Piece> list = new java.util.ArrayList<>();
            for (Piece p : pieces[joueur]) {
                if (p != null && p.getVie() > 0) {
                    
                    list.add(p);
                }
            }
            list.sort(java.util.Comparator.comparingInt(Piece::getPosition));
            // Met à jour la position de chaque pièce selon son index dans la liste filtrée
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPosition(i);
            }
            vivantes[joueur] = list.toArray(new Piece[0]);
        }
        return vivantes;
    }
}