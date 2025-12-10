package save;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

public class Save {
    private final String filePath = "save.txt";

    public Save() {
    }

    // Sauvegarde la configuration initiale de la partie et du plateau
    // Sauvegarde la configuration initiale de la partie et du plateau (typés)
    public boolean sauvegarderConfigurationPartie(objet.Partie partie, objet.Plateau plateau) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // PARTIE
            String partieStr = String.format("PARTIE:id=%d;date=%s;nbpieces=%d",
                partie.getId(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(partie.getDate()),
                partie.getPlateau().getNbPiece()
            );
            writer.write(partieStr);
            writer.newLine();
            // PLATEAU
            String plateauStr = String.format("PLATEAU:id=%d;nbPiece=%d",
                plateau.getId(),
                plateau.getNbPiece()
            );
            writer.write(plateauStr);
            writer.newLine();
            // PIECES
            objet.Piece[][] pieces = plateau.getPieces();
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    objet.Piece p = pieces[i][j];
                    if (p != null) {
                        // On sauvegarde tous les attributs de Piece
                        // Format: PIECE:player=0;index=3;id=5;nom=Roi;vie=3;position=5
                        String pieceStr = String.format(
                            "PIECE:player=%d;index=%d;id=%d;nom=%s;vie=%d;position=%d",
                            i, j, p.getId(), p.getNom(), p.getVie(), p.getPosition()
                        );
                        writer.write(pieceStr);
                        writer.newLine();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ...les méthodes pour pièces, raquettes, balles seront ajoutées plus tard...

    public boolean effacerSauvegarde() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                new FileWriter(filePath, false).close();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
