package affichage;

import objet.Plateau;
import java.awt.*;
import javax.swing.*;

public class PlateauPanel extends JPanel {
    private Plateau plateau;
    private static final int CASE_SIZE = 50; // Taille fixe des cases

    public PlateauPanel(Plateau plateau) {
        this.plateau = plateau;
        setPreferredSize(new Dimension(CASE_SIZE * plateau.getCases()[0].length, CASE_SIZE * plateau.getCases().length));
        addMouseListener(new PlateauListener(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle[][] cases = plateau.getCases();
        int rows = cases.length;
        int cols = cases[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * CASE_SIZE;
                int y = i * CASE_SIZE;
                // Alternance des couleurs façon échiquier
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(220, 220, 220)); // clair
                } else {
                    g.setColor(new Color(100, 100, 100)); // foncé
                }
                g.fillRect(x, y, CASE_SIZE, CASE_SIZE);
                cases[i][j] = new Rectangle(x, y, CASE_SIZE, CASE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, CASE_SIZE, CASE_SIZE);
            }
        }
    }

    public Plateau getPlateau() {
        return plateau;
    }
}
