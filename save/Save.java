package save;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

public class Save {
    private String filePath;

    public Save(String filePath) {
        this.filePath = filePath;
    }

    // Sauvegarde la configuration initiale de la partie et du plateau
    // Sauvegarde la configuration initiale de la partie et du plateau (typés)
    public boolean sauvegarderConfigurationPartie(objet.Partie partie, objet.Plateau plateau) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("PARTIE:" + partie.toString());
            writer.newLine();
            writer.write("PLATEAU:" + plateau.toString());
            writer.newLine();
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
