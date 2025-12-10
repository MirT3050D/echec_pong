package affichage;

import objet.Plateau;
import objet.Raquette;
import java.awt.*;
import javax.swing.*;

import objet.Balle;

public class PlateauPanel extends JPanel {
    /**
     * Retourne les limites réelles du plateau de jeu (zone où la balle doit
     * rester).
     * 
     * @return Rectangle (x, y, width, height) de la zone de jeu
     */
    public Rectangle getPlateauBounds() {
        Rectangle[][][] cases = plateau.getCases();
        int joueurs = cases.length;
        int lignesParJoueur = plateau.getlargeur() / 2;
        int cols = plateau.getNbPiece();
        int totalRows = joueurs * lignesParJoueur;
        int caseWidth = getWidth() / (cols + 2);
        int caseHeight = getHeight() / (totalRows + 2);
        int caseSize = Math.min(caseWidth, caseHeight);
        int totalWidth = caseSize * cols;
        int totalHeight = caseSize * totalRows;
        int offsetX = (getWidth() - totalWidth) / 2;
        int offsetY = (getHeight() - totalHeight) / 2;
        return new Rectangle(offsetX, offsetY, totalWidth, totalHeight);
    }

    private Plateau plateau;
    private RaquettePanel raquettePanel;
    private Balle balle;
    private boolean phaseLancement = true;
    private double angleFleche = -Math.PI / 4;

    public void setPhaseLancement(boolean phaseLancement) {
        this.phaseLancement = phaseLancement;
        repaint();
    }

    public boolean isPhaseLancement() {
        return phaseLancement;
    }

    public void setAngleFleche(double angle) {
        this.angleFleche = angle;
        repaint();
    }

    public double getAngleFleche() {
        return angleFleche;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
    }

    public RaquettePanel getRaquettePanel() {
        return raquettePanel;
    }

    public void setRaquettePanel(RaquettePanel raquettePanel) {
        this.raquettePanel = raquettePanel;
    }

    public Balle getBalle() {
        return balle;
    }

    public void setBalle(Balle balle) {
        this.balle = balle;
    }

    public PlateauPanel(Plateau plateau) {
        this(plateau, null);
    }

    public PlateauPanel(Plateau plateau, Balle balle) {
        this.plateau = plateau;
        this.raquettePanel = new RaquettePanel(plateau, plateau.getRaquettes());
        this.balle = balle;
        setFocusable(true);
        setOpaque(true);
        // Listener pour les raquettes : joueur 0 = flèches, joueur 1 = A Z E R
        Raquette[] raquettes = plateau.getRaquettes();
        if (raquettes != null) {
            if (raquettes[0] != null) {
                addKeyListener(new RaquetteListener(raquettes[0], 0, plateau.getNbPiece(), this));
            }
            if (raquettes.length > 1 && raquettes[1] != null) {
                // Mappe A (gauche), Z (droite), E (angle up), R (angle down)
                addKeyListener(new RaquetteListener(
                        raquettes[1], 0, plateau.getNbPiece(), this,
                        java.awt.event.KeyEvent.VK_A,
                        java.awt.event.KeyEvent.VK_Z,
                        java.awt.event.KeyEvent.VK_E,
                        java.awt.event.KeyEvent.VK_R));
            }
        }
        // Pour le focus automatique à l'affichage
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fond blanc pour bien voir le centrage
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        Rectangle[][][] cases = plateau.getCases();
        int joueurs = cases.length;
        int lignesParJoueur = plateau.getlargeur() / 2;
        int cols = plateau.getNbPiece();
        int totalRows = joueurs * lignesParJoueur;
        int caseWidth = getWidth() / (cols + 2);
        int caseHeight = getHeight() / (totalRows + 2);
        int caseSize = Math.min(caseWidth, caseHeight);
        int totalWidth = caseSize * cols;
        int totalHeight = caseSize * totalRows;
        int offsetX = (getWidth() - totalWidth) / 2;
        int offsetY = (getHeight() - totalHeight) / 2;
        for (int joueur = 0; joueur < joueurs; joueur++) {
            for (int ligne = 0; ligne < lignesParJoueur; ligne++) {
                int globalRow = joueur * lignesParJoueur + ligne;
                for (int col = 0; col < cols; col++) {
                    int x = offsetX + col * caseSize;
                    int y = offsetY + globalRow * caseSize;
                    if ((globalRow + col) % 2 == 0) {
                        g.setColor(new Color(220, 220, 220));
                    } else {
                        g.setColor(new Color(100, 100, 100));
                    }
                    g.fillRect(x, y, caseSize, caseSize);
                    cases[joueur][ligne][col] = new Rectangle(x, y, caseSize, caseSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, caseSize, caseSize);
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
        // Affichage des pièces vivantes
        objet.Piece[][] piecesVivantes = plateau.getPiecesVivantesTriees();
        int nbColonnes = plateau.getNbPiece();
        int ligne_0 = 0;
        int col_0 = 0;

        for (objet.Piece piece : piecesVivantes[0]) {
            Rectangle rect = cases[0][ligne_0][col_0];
            // Avance toujours les compteurs pour garder les positions fixes
            int currentCol = col_0;
            int currentLigne = ligne_0;
            col_0++;
            if (col_0 >= nbColonnes) {
                col_0 = 0;
                ligne_0++;
            }
            // Saute l'affichage si pièce morte
            if (piece == null || piece.getVie() <= 0) {
                continue;
            }
            rect = cases[0][currentLigne][currentCol];
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
            if (rect != null) {
                ImageIcon icon = new ImageIcon("img/" + nomImage);
                g.drawImage(icon.getImage(), rect.x, rect.y, rect.width, rect.height, null);
                // Affiche la vie de la pièce en bas à droite de la case
                int vie = piece.getVie();
                Font originalFont = g.getFont();
                float fontSize = Math.max(10f, caseSize * 0.14f);
                Font lifeFont = originalFont.deriveFont(Font.BOLD, fontSize);
                g.setFont(lifeFont);
                String vieText = String.valueOf(vie);
                FontMetrics fm = g.getFontMetrics(lifeFont);
                int tw = fm.stringWidth(vieText);
                int th = fm.getAscent();
                int lx = rect.x + rect.width - tw - 6;
                int ly = rect.y + rect.height - 4;
                Graphics2D g2 = (Graphics2D) g;
                Color bg = new Color(0, 0, 0, 150);
                g2.setColor(bg);
                g2.fillRoundRect(lx - 4, ly - th - 2, tw + 8, th + 6, 6, 6);
                g2.setColor(Color.WHITE);
                g2.drawString(vieText, lx, ly);
                g.setFont(originalFont);
            }

        }
        int index_col = 0;
        int ligne = 3;
        for (objet.Piece piece : piecesVivantes[1]) {
            // Sauvegarde position courante
            int currentCol = index_col;
            int currentLigne = ligne;
            // Avance toujours les compteurs pour garder positions fixes
            index_col++;
            if (index_col >= nbColonnes) {
                index_col = 0;
                ligne--;
            }
            // Saute l'affichage si pièce morte
            if (piece == null || piece.getVie() <= 0) {
                continue;
            }
            Rectangle rect = cases[1][currentLigne][currentCol];
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
            if (rect != null) {
                ImageIcon icon = new ImageIcon("img/" + nomImage);
                g.drawImage(icon.getImage(), rect.x, rect.y, rect.width, rect.height, null);
                // Affiche la vie de la pièce en bas à droite de la case
                int vie = piece.getVie();
                Font originalFont = g.getFont();
                float fontSize = Math.max(10f, caseSize * 0.14f);
                Font lifeFont = originalFont.deriveFont(Font.BOLD, fontSize);
                g.setFont(lifeFont);
                String vieText = String.valueOf(vie);
                FontMetrics fm = g.getFontMetrics(lifeFont);
                int tw = fm.stringWidth(vieText);
                int th = fm.getAscent();
                int lx = rect.x + rect.width - tw - 6;
                int ly = rect.y + rect.height - 4;
                Graphics2D g2 = (Graphics2D) g;
                Color bg = new Color(0, 0, 0, 150);
                g2.setColor(bg);
                g2.fillRoundRect(lx - 4, ly - th - 2, tw + 8, th + 6, 6, 6);
                g2.setColor(Color.WHITE);
                g2.drawString(vieText, lx, ly);
                g.setFont(originalFont);
            }

        }
        // Affichage des raquettes (placement logique)
        Raquette[] raquettes = plateau.getRaquettes();
        if (raquettes != null) {
            for (Raquette raquette : raquettes) {
                if (raquette == null)
                    continue;
                int joueur = raquette.getJoueur();
                int largeurR = raquette.getLargeur();
                int angle = raquette.getAngle();
                Color couleur = raquette.getCouleur();
                int ligneRaquette = (joueur == 0) ? 2 : 1;
                double xPixel = raquette.getXPixel();
                int y = offsetY + (joueur * lignesParJoueur + ligneRaquette) * caseSize;
                int raquetteWidth = caseSize * largeurR;
                int raquetteHeight = caseSize / 2;
                int x = (int) (offsetX + xPixel * caseSize - raquetteWidth / 2);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.rotate(Math.toRadians(angle), x + raquetteWidth / 2.0, y + raquetteHeight / 2.0);
                g2.setColor(couleur != null ? couleur : Color.MAGENTA);
                g2.fillRect(x, y, raquetteWidth, raquetteHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, raquetteWidth, raquetteHeight);
                g2.dispose();
            }
        }
        // Affichage de la balle ou de la flèche de lancement
        if (balle != null) {
            if (phaseLancement && !balle.isEnMouvement()) {
                // Affiche la flèche de lancement
                int x = (int) balle.getX();
                int y = (int) balle.getY();
                int r = balle.getRayon();
                int len = r * 4;
                double angle = angleFleche;
                int x2 = x + (int) (len * Math.cos(angle));
                int y2 = y + (int) (len * Math.sin(angle));
                g.setColor(Color.ORANGE);
                g.drawLine(x, y, x2, y2);
                g.fillOval(x2 - 4, y2 - 4, 8, 8);
            } else {
                g.setColor(balle.getCouleur());
                int x = (int) (balle.getX() - balle.getRayon());
                int y = (int) (balle.getY() - balle.getRayon());
                int d = balle.getRayon() * 2;
                g.fillOval(x, y, d, d);
            }
        }
    }
}
