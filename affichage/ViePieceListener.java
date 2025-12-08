package affichage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import objet.Piece;
import objet.Plateau;
import objet.Partie;
import save.Save;
import jdbc.ConnexionManager;
import jdbc.RequeteManager;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ViePieceListener implements ActionListener {
    private MaFenetre fenetre;

    public ViePieceListener(MaFenetre fenetre) {
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
        Piece[][] pieces = new Piece[1][nbPiece];
        ArrayList<String> noms = fenetre.getPanelViePiece().getNomsPieces();
        for (int i = 0; i < nbPiece; i++) {
            String nom = (noms != null && i < noms.size()) ? noms.get(i) : "Pion";
            pieces[0][i] = new Piece(i+1, vies.get(i), nom, i);
        }
        Plateau plateau = new Plateau(1, nbPiece, pieces);
        Partie partie = new Partie(1, new Date(), plateau);
        Save saveManager = new Save("save.txt");
        saveManager.sauvegarderConfigurationPartie(partie, plateau);
        ConnexionManager connexionManager = new ConnexionManager();
        connexionManager.ouvrirConnexion();
        java.sql.Connection conn = connexionManager.getConnexion();
        RequeteManager requeteManager = new RequeteManager(conn);
        String sqlPartie = "INSERT INTO partie (id, date, nbpieces) VALUES (" + partie.getId() + ", '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(partie.getDate()) + "', " + partie.getPlateau().getNbPiece() + ")";
        requeteManager.executeUpdate(sqlPartie);
        for (int i = 0; i < nbPiece; i++) {
            Piece p = pieces[0][i];
            String sqlPiece = "INSERT INTO piece (id, nom, position) VALUES (" + p.getId() + ", '" + p.getNom() + "', " + p.getPosition() + ")";
            requeteManager.executeUpdate(sqlPiece);
            String sqlVie = "INSERT INTO vie_piece (id, piece_id, vie, partie_id) VALUES (" + p.getId() + ", " + p.getId() + ", " + p.getVie() + ", " + partie.getId() + ")";
            requeteManager.executeUpdate(sqlVie);
        }
        connexionManager.fermerConnexion();
        JOptionPane.showMessageDialog(fenetre, "Initialisation terminée et sauvegardée !", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
