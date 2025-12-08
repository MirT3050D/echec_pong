package affichage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InitialisationViePiece extends JPanel {
    private ArrayList<JTextField> vieFields;
    private ArrayList<String> nomsPieces;

    public InitialisationViePiece(int nbPieces) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        vieFields = new ArrayList<>();
        nomsPieces = new ArrayList<>();
        String[] nomsStandard = { "Roi", "Reine", "Fou", "Cavalier", "Tour" };
        if (nbPieces == 2) {
            nomsPieces.add("Roi");
            nomsPieces.add("Reine");
            nomsPieces.add("Pion");
        } else if (nbPieces == 4) {
            nomsPieces.add("Roi");
            nomsPieces.add("Reine");
            nomsPieces.add("Fou");
            nomsPieces.add("Pion");
        } else if (nbPieces == 6) {
            nomsPieces.add("Roi");
            nomsPieces.add("Reine");
            nomsPieces.add("Fou");
            nomsPieces.add("Cavalier");
            nomsPieces.add("Pion");
        } else if (nbPieces == 8) {
            nomsPieces.add("Roi");
            nomsPieces.add("Reine");
            nomsPieces.add("Fou");
            nomsPieces.add("Cavalier");
            nomsPieces.add("Tour");
            nomsPieces.add("Pion");
        } else {
            for (int i = 0; i < Math.min(nbPieces, nomsStandard.length); i++) {
                nomsPieces.add(nomsStandard[i]);
            }
            for (int i = nomsStandard.length; i < nbPieces; i++) {
                nomsPieces.add("Pion");
            }
        }
        for (int i = 0; i < nbPieces; i++) {
            String nom = (i < nomsPieces.size()) ? nomsPieces.get(i) : ("Pion");
            add(new JLabel("Vie de " + nom + " (" + (i + 1) + ") :"));
            JTextField field = new JTextField(5);
            field.setPreferredSize(new Dimension(60, 25)); // largeur 60px, hauteur 25px
            vieFields.add(field);
            add(field);
            add(field);
        }
    }

    public ArrayList<Integer> getVies() {
        ArrayList<Integer> vies = new ArrayList<>();
        for (JTextField field : vieFields) {
            try {
                vies.add(Integer.parseInt(field.getText()));
            } catch (NumberFormatException e) {
                vies.add(0);
            }
        }
        return vies;
    }

    public ArrayList<JTextField> getVieFields() {
        return vieFields;
    }

    public ArrayList<String> getNomsPieces() {
        return nomsPieces;
    }
}