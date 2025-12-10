package affichage;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class PlateauListener implements MouseListener {
    private PlateauPanel panel;

    public PlateauListener(PlateauPanel panel) {
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Détection de la case cliquée
        objet.Plateau plateau = panel.getPlateau();
        Rectangle[][] cases = plateau.getCases();
        for (int i = 0; i < cases.length; i++) {
            for (int j = 0; j < cases[i].length; j++) {
                if (cases[i][j] != null && cases[i][j].contains(e.getPoint())) {
                    JOptionPane.showMessageDialog(panel, "Case cliquée : Joueur " + (i+1) + ", Case " + (j+1));
                    return;
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
