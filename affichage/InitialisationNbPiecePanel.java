package affichage;

import javax.swing.*;
import java.awt.*;

public class InitialisationNbPiecePanel extends JPanel {
    private JLabel label;
    private JTextField nbPieceField;


    public InitialisationNbPiecePanel() {
        setLayout(new GridBagLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());
        label = new JLabel("Nombre de pièces :");
        nbPieceField = new JTextField(5);
        innerPanel.add(label);
        innerPanel.add(nbPieceField);
        innerPanel.setMaximumSize(new Dimension(400, 60));
        add(innerPanel, new GridBagConstraints());
        setOpaque(false);
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