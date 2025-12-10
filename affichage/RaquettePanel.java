package affichage;

import objet.Raquette;
import objet.Plateau;
import java.awt.*;
import javax.swing.*;

public class RaquettePanel extends JPanel {
    private Plateau plateau;
    private Raquette[] raquettes; // une par joueur

    public RaquettePanel(Plateau plateau, Raquette[] raquettes) {
        this.plateau = plateau;
        this.raquettes = raquettes;
        setOpaque(false); // pour superposer sur le plateau
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("[RaquettePanel] paintComponent appelé");
        Rectangle[][][] cases = plateau.getCases();
        for (Raquette r : raquettes) {
            if (r == null) continue;
            int joueur = r.getJoueur();
            int colMilieu = r.getColMilieu();
            int largeur = r.getLargeur();
            int angle = r.getAngle();
            int lignesParJoueur = (int) Math.ceil((double) plateau.getNbPiece() / Math.min(plateau.getlargeur(), plateau.getNbPiece()));
            int ligneRaquette = (joueur == 0) ? (lignesParJoueur - 2) : 1; // devant les pions
            int colStart = Math.max(0, colMilieu - largeur/2);
            int colEnd = Math.min(plateau.getlargeur()-1, colMilieu + largeur/2);
            g.setColor(Color.MAGENTA); // couleur vive pour test
            for (int col = colStart; col <= colEnd; col++) {
                Rectangle rect = cases[joueur][ligneRaquette][col];
                if (rect != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.rotate(Math.toRadians(angle), rect.getCenterX(), rect.getCenterY());
                    g2.fillRect(rect.x, rect.y, rect.width, rect.height);
                    g2.dispose();
                }
            }
        }
    }
}
