
package affichage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitialisationNbPieceListener implements ActionListener {
    private MaFenetre fenetre;

    public InitialisationNbPieceListener(MaFenetre fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int nbPiece = fenetre.getPanelNbPiece().getNbPiece();
        if (nbPiece <= 0 || nbPiece % 2 != 0) {
            JOptionPane.showMessageDialog(fenetre, "Le nombre de pièces doit être un nombre pair et positif.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
            fenetre.afficherPanelViePiece(nbPiece);
    }
}
