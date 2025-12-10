
package affichage;


import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import objet.Piece;
import objet.Plateau;
import objet.Partie;
import save.Save;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class InitialisationViePieceListner implements ActionListener {
    private MaFenetre fenetre;

    public InitialisationViePieceListner(MaFenetre fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Integer> vies = fenetre.getPanelViePiece().getVies();
        boolean ok = true;
        for (int vie : vies) {
            if (vie < 0) ok = false;
        }
        if (!ok) {
            JOptionPane.showMessageDialog(fenetre, "Les vies doivent être positives.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nbPiece = fenetre.getPanelNbPiece().getNbPiece();
        List<Piece> allPieces = Piece.getAll();
        Piece[][] pieces = new Piece[2][16];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                Piece base = allPieces.get(j);
                // System.out.println("taille vies: " + vies.size() + " j: " + j);
                int vie = (j < vies.size()) ? vies.get(j) : 0;
                pieces[i][j] = new Piece(base.getId(), vie, base.getNom(), base.getPosition());
            }
        }
        Plateau plateau = new Plateau(0, nbPiece, pieces);
        Partie partie = new Partie(0, new Date(), plateau);
        Save saveManager = new Save();
        saveManager.sauvegarderConfigurationPartie(partie, plateau);
        JOptionPane.showMessageDialog(fenetre, "Initialisation terminée et sauvegardée !", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
