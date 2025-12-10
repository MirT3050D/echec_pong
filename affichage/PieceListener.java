package affichage;

import java.awt.event.*;
import javax.swing.*;

public class PieceListener implements MouseListener {
    private PiecePanel panel;

    public PieceListener(PiecePanel panel) {
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JOptionPane.showMessageDialog(panel, "Pièce cliquée : " + panel.getPiece().getNom());
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
