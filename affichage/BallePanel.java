package affichage;

import objet.Balle;
import java.awt.*;
import javax.swing.*;

public class BallePanel extends JPanel {
    private Balle balle;
    public BallePanel(Balle balle) {
        this.balle = balle;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (balle != null) {
            g.setColor(balle.getCouleur());
            int x = (int) (balle.getX() - balle.getRayon());
            int y = (int) (balle.getY() - balle.getRayon());
            int d = balle.getRayon() * 2;
            g.fillOval(x, y, d, d);
        }
    }
    public void setBalle(Balle balle) { this.balle = balle; }
    public Balle getBalle() { return balle; }
}