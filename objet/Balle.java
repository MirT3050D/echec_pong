package objet;
import java.awt.Color;

public class Balle {
    private double x, y; // position en pixels
    private double vx, vy; // vitesse en pixels/frame
    private double angle; // angle de déplacement (radian)
    private double vitesse; // module de la vitesse
    private int rayon;
    private Color couleur;
    private boolean enMouvement;

    public Balle(double x, double y, double vitesse, double angle, int rayon, Color couleur) {
        this.x = x;
        this.y = y;
        this.vitesse = vitesse;
        this.angle = angle;
        this.rayon = rayon;
        this.couleur = couleur;
        this.enMouvement = false;
        updateVitesse();
    }

    public void updateVitesse() {
        this.vx = vitesse * Math.cos(angle);
        this.vy = vitesse * Math.sin(angle);
    }

    public void setAngle(double angle) {
        this.angle = angle;
        updateVitesse();
    }
    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
        updateVitesse();
    }
    public void setEnMouvement(boolean enMouvement) { this.enMouvement = enMouvement; }
    public boolean isEnMouvement() { return enMouvement; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getRayon() { return rayon; }
    public Color getCouleur() { return couleur; }
    public double getAngle() { return angle; }
    public double getVitesse() { return vitesse; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void move() {
        if (enMouvement) {
            x += vx;
            y += vy;
        }
    }
}