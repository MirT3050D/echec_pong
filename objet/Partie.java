package objet;

import java.util.Date;

public class Partie {
    private int id;
    private Date date;
    private Plateau plateau;

    public Partie(int id, Date date, Plateau plateau) {
        this.id = id;
        this.date = date;
        this.plateau = plateau;
    }

    public int getId() { return id; }
    public Date getDate() { return date; }
    public Plateau getPlateau() { return plateau; }

    public void setDate(Date date) { this.date = date; }
    public void setPlateau(Plateau plateau) { this.plateau = plateau; }
}