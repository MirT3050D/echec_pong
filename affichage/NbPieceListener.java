package affichage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NbPieceListener implements ActionListener {
    private MaFenetre fenetre;

    public NbPieceListener(MaFenetre fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int nbPiece = fenetre.getPanelNbPiece().getNbPiece();
        if (nbPiece <= 0 || nbPiece % 2 != 0) {
            JOptionPane.showMessageDialog(fenetre, "Le nombre de pièces doit être un nombre pair et positif.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        fenetre.setPanelViePiece(new InitialisationViePiece(nbPiece));
        fenetre.setValiderVieBtn(new JButton("Valider les vies"));
        JPanel viePanel = new JPanel(new java.awt.BorderLayout());
        viePanel.add(fenetre.getPanelViePiece(), java.awt.BorderLayout.CENTER);
        viePanel.add(fenetre.getValiderVieBtn(), java.awt.BorderLayout.SOUTH);
        fenetre.getContentPane().removeAll();
        fenetre.add(viePanel, java.awt.BorderLayout.CENTER);
        fenetre.revalidate();
        fenetre.repaint();
        fenetre.getValiderVieBtn().addActionListener(new ViePieceListener(fenetre));
    }
}
