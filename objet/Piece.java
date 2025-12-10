package objet;

import java.util.List;
import java.util.ArrayList;

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

    // Retourne toutes les pièces depuis le fichier de données `base/data.sql`.
    // Format attendu : un INSERT INTO piece (...) VALUES ('name', pos),('name2', pos2),...;
    public static List<Piece> getAll() {
        List<Piece> retour = new ArrayList<>();
        retour.add(new Piece(1,0,"Tour",1));
        retour.add(new Piece(2,0,"Cavalier",2));
        retour.add(new Piece(3,0,"Fou",3));
        retour.add(new Piece(4,0,"Reine",4));
        retour.add(new Piece(5,0,"Roi",5));
        retour.add(new Piece(3,0,"Fou",6));
        retour.add(new Piece(2,0,"Cavalier",7));
        retour.add(new Piece(1,0,"Tour",8));
        retour.add(new Piece(5, 0, "Pion", 9));
        retour.add(new Piece(5, 0, "Pion", 10));
        retour.add(new Piece(5, 0, "Pion", 11));
        retour.add(new Piece(5, 0, "Pion", 12));
        retour.add(new Piece(5, 0, "Pion", 13));
        retour.add(new Piece(5, 0, "Pion", 14));
        retour.add(new Piece(5, 0, "Pion", 15));
        retour.add(new Piece(5, 0, "Pion", 16));

        return retour;
        
        

    }
}