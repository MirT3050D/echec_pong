package reseau;

import java.io.Serializable;

/**
 * État d'une raquette
 */
public class RaquetteState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int joueur;        // 0 ou 1
    private double xPixel;     // Position X en pixels
    private int angle;         // Angle de la raquette
    private int largeur;       // Largeur de la raquette
    
    public RaquetteState(int joueur, double xPixel, int angle, int largeur) {
        this.joueur = joueur;
        this.xPixel = xPixel;
        this.angle = angle;
        this.largeur = largeur;
    }
    
    public int getJoueur() { return joueur; }
    public double getXPixel() { return xPixel; }
    public int getAngle() { return angle; }
    public int getLargeur() { return largeur; }
    
    @Override
    public String toString() {
        return "RaquetteState{joueur=" + joueur + ", x=" + xPixel + ", angle=" + angle + "}";
    }
}
