package affichage;

import objet.Piece;
import java.awt.*;
import javax.swing.*;

public class PiecePanel extends JPanel {
    private Piece piece;
    private Rectangle caseRect;

    public PiecePanel(Piece piece, Rectangle caseRect) {
        this.piece = piece;
        this.caseRect = caseRect;
        setOpaque(false);
        setBounds(caseRect);
        addMouseListener(new PieceListener(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Déterminer le nom de l'image selon le type et la couleur
        String nomImage = piece.getNom().toLowerCase() + "_" + (piece.getPosition() < 8 ? "blanc" : "noir") + ".png";
        ImageIcon icon = new ImageIcon("img/" + nomImage);
        g.drawImage(icon.getImage(), 0, 0, caseRect.width, caseRect.height, null);
    }

    public Piece getPiece() { return piece; }
}
