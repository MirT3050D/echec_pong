
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
import service.DataClientService;

public class InitialisationViePieceListner implements ActionListener {
    private MaFenetre fenetre;

    public InitialisationViePieceListner(MaFenetre fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            DataClientService dataClientService = new DataClientService();
            ArrayList<Integer> vies_local = fenetre.getPanelViePiece().getVies(fenetre.getPanelNbPiece().getNbPiece());
            dataClientService.saveConfig(vies_local);
            ArrayList<Integer> vies = dataClientService.getConfig();
            boolean ok = true;
            for (int vie : vies) {
                if (vie < 0)
                    ok = false;
            }
            if (!ok) {
                JOptionPane.showMessageDialog(fenetre, "Les vies doivent être positives.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int nbPiece = fenetre.getPanelNbPiece().getNbPiece();

            System.out.println("[SERVEUR] Vies récupérées du panel: " + vies);
            System.out.println("[SERVEUR] nbPiece=" + nbPiece);

            // Stocker les vies individuelles pour J1 (en mode réseau)
            fenetre.setViesIndividuelles(vies);

            // Créer les pièces avec les 16 éléments (comme avant, pour avoir l'ordre
            // correct)
            java.util.List<objet.Piece> allPieces = objet.Piece.getAll();
            objet.Piece[][] pieces = new objet.Piece[2][16];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 16; j++) {
                    objet.Piece base = allPieces.get(j);
                    int vie = (j < vies.size()) ? vies.get(j) : 0;
                    pieces[i][j] = new objet.Piece(base.getId(), vie, base.getNom(), base.getPosition());
                    if (i == 0) { // Affiche une seule fois
                        System.out.println("[SERVEUR] Piece[" + j + "]: " + base.getNom() + ", vie=" + vie);
                    }
                }
            }

            System.out.println("[SERVEUR] Plateau créé: pieces[0].length=" + pieces[0].length +
                    ", pieces[1].length=" + pieces[1].length);

            objet.Plateau plateau = new objet.Plateau(0, nbPiece, pieces);

            // DEBUG: Log des pièces vivantes
            objet.Piece[][] piecesVivantes = plateau.getPiecesVivantesTriees();
            System.out.println(
                    "[SERVEUR] Pièces vivantes J1: " + piecesVivantes[0].length + " et " + piecesVivantes[1].length);
            System.out.println("[SERVEUR] Détail J1[0]: ");
            for (objet.Piece p : piecesVivantes[0]) {
                System.out.println("  - " + p.getNom() + " (vie=" + p.getVie() + ")");
            }

            objet.Partie partie = new objet.Partie(0, new java.util.Date(), plateau);
            Save saveManager = new Save();
            plateau.setPieces(plateau.getPiecesVivantesTriees());
            saveManager.sauvegarderConfigurationPartie(partie, plateau);
            JOptionPane.showMessageDialog(fenetre, "Initialisation terminée et sauvegardée !", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            fenetre.afficherPlateau(plateau);

            // Forcer le layout et le repaint
            fenetre.validate();
            fenetre.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(fenetre, "Erreur : " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return;
        }
    }
}
