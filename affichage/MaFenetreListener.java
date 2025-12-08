package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MaFenetreListener {
    private MaFenetre fenetre;

    public MaFenetreListener(MaFenetre fenetre) {
        this.fenetre = fenetre;
        initListeners();
    }

    private void initListeners() {
        fenetre.getValiderNbPieceBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nbPiece = fenetre.getPanelNbPiece().getNbPiece();
                if (nbPiece <= 0 || nbPiece % 2 != 0) {
                    JOptionPane.showMessageDialog(fenetre, "Le nombre de pièces doit être un nombre pair et positif.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fenetre.setPanelViePiece(new InitialisationViePiece(nbPiece));
                fenetre.setValiderVieBtn(new JButton("Valider les vies"));
                JPanel viePanel = new JPanel(new BorderLayout());
                viePanel.add(fenetre.getPanelViePiece(), BorderLayout.CENTER);
                viePanel.add(fenetre.getValiderVieBtn(), BorderLayout.SOUTH);
                fenetre.getContentPane().removeAll();
                fenetre.add(viePanel, BorderLayout.CENTER);
                fenetre.revalidate();
                fenetre.repaint();

                fenetre.getValiderVieBtn().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        java.util.ArrayList<Integer> vies = fenetre.getPanelViePiece().getVies();
                        boolean ok = true;
                        for (int vie : vies) {
                            if (vie < 0) ok = false;
                        }
                        if (!ok) {
                            JOptionPane.showMessageDialog(fenetre, "Les vies doivent être positives.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int nbPiece = fenetre.getPanelNbPiece().getNbPiece();
                        objet.Piece[][] pieces = new objet.Piece[1][nbPiece];
                        for (int i = 0; i < nbPiece; i++) {
                            pieces[0][i] = new objet.Piece(i+1, vies.get(i), "Pion" + (i+1), i);
                        }
                        objet.Plateau plateau = new objet.Plateau(1, nbPiece, pieces);
                        objet.Partie partie = new objet.Partie(1, new java.util.Date(), plateau);

                        save.Save saveManager = new save.Save("save.txt");
                        saveManager.sauvegarderConfigurationPartie(partie, plateau);

                        jdbc.ConnexionManager connexionManager = new jdbc.ConnexionManager();
                        connexionManager.ouvrirConnexion();
                        java.sql.Connection conn = connexionManager.getConnexion();
                        jdbc.RequeteManager requeteManager = new jdbc.RequeteManager(conn);
                        String sqlPartie = "INSERT INTO partie (id, date, nbpieces) VALUES (" + partie.getId() + ", '" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(partie.getDate()) + "', " + partie.getPlateau().getNbPiece() + ")";
                        requeteManager.executeUpdate(sqlPartie);
                        for (int i = 0; i < nbPiece; i++) {
                            objet.Piece p = pieces[0][i];
                            String sqlPiece = "INSERT INTO piece (id, nom, position) VALUES (" + p.getId() + ", '" + p.getNom() + "', " + p.getPosition() + ")";
                            requeteManager.executeUpdate(sqlPiece);
                            String sqlVie = "INSERT INTO vie_piece (id, piece_id, vie, partie_id) VALUES (" + p.getId() + ", " + p.getId() + ", " + p.getVie() + ", " + partie.getId() + ")";
                            requeteManager.executeUpdate(sqlVie);
                        }
                        connexionManager.fermerConnexion();

                        JOptionPane.showMessageDialog(fenetre, "Initialisation terminée et sauvegardée !", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        });
    }
}
