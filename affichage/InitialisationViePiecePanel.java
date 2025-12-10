package affichage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InitialisationViePiecePanel extends JPanel {
    private ArrayList<JTextField> vieFields;
    private ArrayList<String> nomsPieces;

    public InitialisationViePiecePanel(int nbPieces) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        vieFields = new ArrayList<>();
        nomsPieces = new ArrayList<>();
        java.util.List<objet.Piece> allPieces = objet.Piece.getAll();
        // Définir les ids à afficher selon la règle
        java.util.Set<Integer> postionsToShow = new java.util.HashSet<>();
        if (nbPieces == 2) {
            postionsToShow.add(4);
            postionsToShow.add(5);
            postionsToShow.add(9);
        } else if (nbPieces == 4) {
            postionsToShow.add(3);
            postionsToShow.add(4);
            postionsToShow.add(5);
            postionsToShow.add(9);
        } else if (nbPieces == 6) {
            postionsToShow.add(2);
            postionsToShow.add(3);
            postionsToShow.add(4);
            postionsToShow.add(5);
            postionsToShow.add(9);
        } else if (nbPieces == 8) {
            postionsToShow.add(1);
            postionsToShow.add(2);
            postionsToShow.add(3);
            postionsToShow.add(4);
            postionsToShow.add(5);
            postionsToShow.add(9);
        }
        for (int i = 0; i < allPieces.size(); i++) {
            objet.Piece piece = allPieces.get(i);
            String nom = piece.getNom();
            nomsPieces.add(nom);
            JTextField field = new JTextField(5);
            field.setPreferredSize(new Dimension(60, 25));
            if (postionsToShow.contains(piece.getPosition())) {
                // System.out.println("--------------------------------------");
                field.setEnabled(true);
                field.setVisible(true);
                // System.out.println("Vie de " + nom + " (" + (i + 1) + ") : " +
                // field.getText());
                add(new JLabel("Vie de " + nom + " (" + (i + 1) + ") :"));
                add(field);
            } else {
                field.setEnabled(false);
                field.setVisible(false);
                field.setText("0");
            }
            // System.out.println("vie = " + field.getText());
            vieFields.add(field);
        }
    }

    public ArrayList<Integer> getVies() {
        ArrayList<Integer> vies = new ArrayList<>();
        for (int i = 0; i < vieFields.size(); i++) {
            System.out.println("Champ de vie " + (i + 1) + " : " + vieFields.get(i).getText());
        }
        for (int i = 0; i < vieFields.size(); i++) {
            vies.add(0);
        }
        for (int i = 0; i <= vieFields.size() / 2 + 1; i++) {
            try {
                if (vies.get(i) == 0) {

                    vies.set(i, Integer.parseInt(vieFields.get(i).getText()));
                    if (Integer.parseInt(vieFields.get(i).getText()) > 0 && i != 3 && i != 4 && i < 8) {
                        // System.out.println("Setting vie for position " + (7 - i));
                        vies.set(7 - i, Integer.parseInt(vieFields.get(i).getText()));
                    }
                    if (i == 8) {
                        for (int j = 0; j < 8; j++) {
                            vies.set(i + j, Integer.parseInt(vieFields.get(i).getText()));
                        }
                    }
                }
            } catch (NumberFormatException e) {
                vies.add(0);
            }
        }
        // System.out.println("Vies récupérées : " + vies);
        return vies;
    }

    public ArrayList<JTextField> getVieFields() {
        return vieFields;
    }

    public ArrayList<String> getNomsPieces() {
        return nomsPieces;
    }
}