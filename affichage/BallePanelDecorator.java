package affichage;

import objet.Balle;
import java.awt.*;
import javax.swing.*;

public class BallePanelDecorator extends JPanel {
    private BallePanel ballePanel;
    private BalleListener balleListener;
    public BallePanelDecorator(BallePanel ballePanel, BalleListener balleListener) {
        this.ballePanel = ballePanel;
        this.balleListener = balleListener;
        setOpaque(false);
        setFocusable(true);
        addKeyListener(balleListener);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ballePanel.paintComponent(g);
        // Affiche la flèche de lancement si la balle n'est pas encore lancée
        if (balleListener.isPretLancer()) {
            Balle balle = ballePanel.getBalle();
            int x = (int) balle.getX();
            int y = (int) balle.getY();
            int r = balle.getRayon();
            int len = r * 4;
            double angle = balleListener.getAngleFleche();
            int x2 = x + (int) (len * Math.cos(angle));
            int y2 = y + (int) (len * Math.sin(angle));
            g.setColor(Color.ORANGE);
            g.drawLine(x, y, x2, y2);
            g.fillOval(x2-4, y2-4, 8, 8);
        }
    }
}