package affichage;

import objet.Plateau;
import java.awt.*;
import javax.swing.*;


public class PlateauPanel extends JPanel {
    private Plateau plateau;

    public PlateauPanel(Plateau plateau) {
        this.plateau = plateau;
        setBackground(Color.DARK_GRAY);
        addMouseListener(new PlateauListener(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle[][][] cases = plateau.getCases();
        int joueurs = cases.length;
        int lignesParJoueur = plateau.getlargeur() / 2;
        int cols = plateau.getNbPiece();
        int totalRows = joueurs * lignesParJoueur;
        // Calculer la taille de case maximale pour que le plateau tienne dans le panel
        int caseWidth = getWidth() / (cols + 2);
        int caseHeight = getHeight() / (totalRows + 2);
        int caseSize = Math.min(caseWidth, caseHeight);
        // Centrer le plateau
        int totalWidth = caseSize * cols;
        int totalHeight = caseSize * totalRows;
        int offsetX = (getWidth() - totalWidth) / 2;
        int offsetY = (getHeight() - totalHeight) / 2;
        // Affiche joueur 1 (en haut), puis joueur 2 (en bas)
        for (int joueur = 0; joueur < joueurs; joueur++) {
            for (int ligne = 0; ligne < lignesParJoueur; ligne++) {
                int globalRow = joueur * lignesParJoueur + ligne;
                for (int col = 0; col < cols; col++) {
                    int x = offsetX + col * caseSize;
                    int y = offsetY + globalRow * caseSize;
                    // Alternance des couleurs façon échiquier
                    if ((globalRow + col) % 2 == 0) {
                        g.setColor(new Color(220, 220, 220)); // clair
                    } else {
                        g.setColor(new Color(100, 100, 100)); // foncé
                    }
                    g.fillRect(x, y, caseSize, caseSize);
                    cases[joueur][ligne][col] = new Rectangle(x, y, caseSize, caseSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, caseSize, caseSize);
                    // Affiche la position (ligne*col) au centre de la case
                    String posText = String.valueOf(ligne).concat("*").concat(String.valueOf(col));
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(posText);
                    int textHeight = fm.getAscent();
                    int textX = x + (caseSize - textWidth) / 2;
                    int textY = y + (caseSize + textHeight) / 2 - 2;
                    g.setColor(Color.RED);
                    g.drawString(posText, textX, textY);
                }
            }
        }

        // Affichage des pièces
        objet.Piece[][] piecesVivantes = plateau.getPiecesVivantesTriees();
        System.out.println("length pieces: " + piecesVivantes.length+ "lenght pieces[0]: " + piecesVivantes[0].length);
        for (int i = 0; i < piecesVivantes[0].length; i++) {
            System.out.println("piece joueur 0: " + piecesVivantes[0][i].getNom() + " position: " + piecesVivantes[0][i].getPosition());
        }
        int nbColonnes = plateau.getNbPiece();
        // Affichage des pièces du joueur 0 (bas -> haut)
        for (objet.Piece piece : piecesVivantes[0]) {
            int col = piece.getPosition() % nbColonnes;
            int ligne = (int)(piece.getPosition() / nbColonnes);
            Rectangle rect = cases[0][ligne][col];
            String code = switch (piece.getNom().toLowerCase()) {
                case "roi" -> "k";
                case "reine" -> "q";
                case "tour" -> "r";
                case "fou" -> "b";
                case "cavalier" -> "n";
                case "pion" -> "p";
                default -> "p";
            };
            String couleur = "l";
            String nomImage = "Chess_" + code + couleur + "t60.png";
            System.out.println("Affichage piece: " + piece.getNom() + " | joueur=0 ligne=" + ligne + " col=" + col + " | image=" + nomImage);
            if (rect != null) {
                ImageIcon icon = new ImageIcon("img/" + nomImage);
                g.drawImage(icon.getImage(), rect.x, rect.y, rect.width, rect.height, null);
            }
        }

        // Affichage des pièces du joueur 1 (haut -> bas)
        for (int i = 0; i < piecesVivantes[1].length; i++) {
            System.out.println("length pieces joueur 1: " + piecesVivantes[1].length);
            System.out.println("--------------------------------------------");
            objet.Piece piece = piecesVivantes[1][i];
            int col = piece.getPosition() % nbColonnes;
            int ligne =  3 - ((int) piece.getPosition() / nbColonnes);
            Rectangle rect = cases[1][ligne][col];
            String code = switch (piece.getNom().toLowerCase()) {
                case "roi" -> "k";
                case "reine" -> "q";
                case "tour" -> "r";
                case "fou" -> "b";
                case "cavalier" -> "n";
                case "pion" -> "p";
                default -> "p";
            };
            String couleur = "d";
            String nomImage = "Chess_" + code + couleur + "t60.png";
            System.out.println("Affichage piece: " + piece.getNom() + " | joueur=1 ligne=" + ligne + " col=" + col + " | image=" + nomImage);
            if (rect != null) {
                ImageIcon icon = new ImageIcon("img/" + nomImage);
                g.drawImage(icon.getImage(), rect.x, rect.y, rect.width, rect.height, null);
            }
        }
    }

    public Plateau getPlateau() {
        return plateau;
    }
}
