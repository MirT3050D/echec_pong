package affichage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InitialisationViePiece extends JPanel {
    private ArrayList<JTextField> vieFields;

    public InitialisationViePiece(int nbPieces) {
        setLayout(new GridLayout(nbPieces, 2, 5, 5));
        vieFields = new ArrayList<>();
        for (int i = 0; i < nbPieces; i++) {
            add(new JLabel("Vie de la pièce " + (i + 1) + ":"));
            JTextField field = new JTextField(5);
            vieFields.add(field);
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
}