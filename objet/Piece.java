package objet;

public class Piece {
    private int id;
    private int vie;
    private String nom;
    private int position;

    public Piece(int id, int vie, String nom, int position) {
        this.id = id;
        this.vie = vie;
        this.nom = nom;
        this.position = position;
    }

    public int getId() { return id; }
    public int getVie() { return vie; }
    public String getNom() { return nom; }
    public int getPosition() { return position; }

    public void setVie(int vie) { this.vie = vie; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPosition(int position) { this.position = position; }
}