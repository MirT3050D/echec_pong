package objet;

import java.awt.*;

public class Raquette {
        public static final int ANGLE_MIN = -60;
        public static final int ANGLE_MAX = 60;
    private int id;
    private int joueur; // 0 ou 1
    private int colMilieu; // position centrale sur l'axe des colonnes
    private int angle; // en degrés, 0 = horizontal
    private int largeur; // nombre de cases occupées
    private Color couleur;
    private double xPixel; // position horizontale en pixels

    public Raquette(int id, int joueur, int colMilieu, int angle, int largeur, Color couleur) {
        this.id = id;
        this.joueur = joueur;
        this.colMilieu = colMilieu;
        if (angle < ANGLE_MIN) angle = ANGLE_MIN;
        if (angle > ANGLE_MAX) angle = ANGLE_MAX;
        this.angle = angle;
        this.largeur = largeur;
        this.couleur = couleur;
        this.xPixel = colMilieu; // initialisé à la colonne centrale, sera converti en pixels dans le panel
    }

    public int getId() { return id; }
    public int getJoueur() { return joueur; }
    public int getColMilieu() { return colMilieu; }
    public int getAngle() { return angle; }
    public int getLargeur() { return largeur; }
    public Color getCouleur() { return couleur; }
    public double getXPixel() { return xPixel; }
    public void setXPixel(double xPixel) { this.xPixel = xPixel; }

    public void setColMilieu(int colMilieu) { this.colMilieu = colMilieu; }
    public void setAngle(int angle) { this.angle = angle; }
    public void setCouleur(Color couleur) { this.couleur = couleur; }
}
