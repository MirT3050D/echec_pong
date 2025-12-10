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
        Rectangle[][][] cases = plateau.getCases();
        for (int joueur = 0; joueur < cases.length; joueur++) {
            for (int ligne = 0; ligne < cases[joueur].length; ligne++) {
                for (int col = 0; col < cases[joueur][ligne].length; col++) {
                    if (cases[joueur][ligne][col] != null && cases[joueur][ligne][col].contains(e.getPoint())) {
                        JOptionPane.showMessageDialog(panel, "Case cliquée : Joueur " + (joueur+1) + ", Ligne " + (ligne+1) + ", Colonne " + (col+1));
                        return;
                    }
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
