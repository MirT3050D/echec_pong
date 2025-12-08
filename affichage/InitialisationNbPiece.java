package affichage;

import javax.swing.*;
import java.awt.*;

public class InitialisationNbPiece extends JPanel {
    private JLabel label;
    private JTextField nbPieceField;

    public InitialisationNbPiece() {
        setLayout(new FlowLayout());
        label = new JLabel("Nombre de pièces :");
        nbPieceField = new JTextField(5);
        add(label);
        add(nbPieceField);
    }

    public int getNbPiece() {
        try {
            return Integer.parseInt(nbPieceField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public JTextField getNbPieceField() {
        return nbPieceField;
    }
}