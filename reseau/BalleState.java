package reseau;

import java.io.Serializable;

/**
 * État de la balle (position, angle, vitesse)
 */
public class BalleState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double x;
    private double y;
    private double angle;
    private double vitesse;
    private boolean enMouvement;
    
    public BalleState(double x, double y, double angle, double vitesse, boolean enMouvement) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.vitesse = vitesse;
        this.enMouvement = enMouvement;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public double getVitesse() { return vitesse; }
    public boolean isEnMouvement() { return enMouvement; }
    
    @Override
    public String toString() {
        return "BalleState{x=" + x + ", y=" + y + ", angle=" + angle + ", vitesse=" + vitesse + "}";
    }
}
