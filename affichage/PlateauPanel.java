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
        // Utiliser getWidth() pour s'adapter à la taille de la fenêtre
        final int PANEL_WIDTH = getWidth() > 0 ? getWidth() : 950;
        final int PANEL_HEIGHT = 1080;
        
        Rectangle[][][] cases = plateau.getCases();
        int joueurs = cases.length;
        int lignesParJoueur = plateau.getlargeur() / 2;
        int cols = plateau.getNbPiece();
        int totalRows = joueurs * lignesParJoueur;
        int caseWidth = PANEL_WIDTH / (cols + 2);
        int caseHeight = PANEL_HEIGHT / (totalRows + 2);
        int caseSize = (int)(Math.min(caseWidth, caseHeight) * 0.8);  // Réduction de 20%
        int totalWidth = caseSize * cols;
        int totalHeight = caseSize * totalRows;
        int offsetX = (PANEL_WIDTH - totalWidth) / 2;
        int offsetY = (int)((PANEL_HEIGHT - totalHeight) * 0.2);  // 20% de marge en haut
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
        this(plateau, null, null, null, -1);
    }

    public PlateauPanel(Plateau plateau, Balle balle) {
        this(plateau, balle, null, null, -1);
    }
    
    public PlateauPanel(Plateau plateau, Balle balle, reseau.GameServer gameServer, reseau.GameClient gameClient, int localJoueurId) {
        this.plateau = plateau;
        this.raquettePanel = new RaquettePanel(plateau, plateau.getRaquettes());
        this.balle = balle;
        setFocusable(true);
        setOpaque(true);
        
        // Listener pour les raquettes avec support réseau
        // localJoueurId: 0=J1 contrôle raquette[0], 1=J2 contrôle raquette[1]
        Raquette[] raquettes = plateau.getRaquettes();
        if (raquettes != null) {
            if (raquettes[0] != null) {
                // Joueur 0 : touches fléchées
                // Ne créer le listener que si c'est le joueur local
                if (localJoueurId == 0 || localJoueurId < 0) {
                    addKeyListener(new RaquetteListener(
                        raquettes[0], 0, plateau.getNbPiece(), this,
                        java.awt.event.KeyEvent.VK_LEFT,
                        java.awt.event.KeyEvent.VK_RIGHT,
                        java.awt.event.KeyEvent.VK_UP,
                        java.awt.event.KeyEvent.VK_DOWN,
                        gameServer, gameClient, 0  // Joueur 0
                    ));
                }
            }
            if (raquettes.length > 1 && raquettes[1] != null) {
                // Joueur 1 : touches AZQD
                // Ne créer le listener que si c'est le joueur local
                if (localJoueurId == 1 || localJoueurId < 0) {
                    addKeyListener(new RaquetteListener(
                        raquettes[1], 0, plateau.getNbPiece(), this,
                        java.awt.event.KeyEvent.VK_A,  // gauche
                        java.awt.event.KeyEvent.VK_Z,  // droite
                        java.awt.event.KeyEvent.VK_E,  // angle up
                        java.awt.event.KeyEvent.VK_R,  // angle down
                        gameServer, gameClient, 1  // Joueur 1
                    ));
                }
            }
        }
        // Pour le focus automatique à l'affichage (seulement après la phase de lancement)
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                // Ne pas prendre le focus pendant la phase de lancement (pour garder W/X actifs)
                if (!phaseLancement) {
                    requestFocusInWindow();
                    System.out.println("[PlateauPanel] Focus demandé automatiquement (hierarchy)");
                }
            }
        });
        
        // Reprendre le focus quand on clique sur le plateau (seulement après la phase de lancement)
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Ne pas prendre le focus pendant la phase de lancement (pour garder W/X actifs)
                if (!phaseLancement) {
                    requestFocusInWindow();
                    System.out.println("[PlateauPanel] Focus repris suite au clic souris");
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fond blanc pour bien voir le centrage
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Utiliser getWidth() pour s'adapter à la taille de la fenêtre et centrer le plateau
        final int panelWidth = getWidth() > 0 ? getWidth() : 950;
        final int panelHeight = 1080;
        
        Rectangle[][][] cases = plateau.getCases();
        int joueurs = cases.length;
        int lignesParJoueur = plateau.getlargeur() / 2;
        int cols = plateau.getNbPiece();
        int totalRows = joueurs * lignesParJoueur;
        int caseWidth = panelWidth / (cols + 2);
        int caseHeight = panelHeight / (totalRows + 2);
        int caseSize = (int)(Math.min(caseWidth, caseHeight) * 0.8);  // Réduction de 20%
        int totalWidth = caseSize * cols;
        int totalHeight = caseSize * totalRows;
        int offsetX = (panelWidth - totalWidth) / 2;
        int offsetY = (int)((panelHeight - totalHeight) * 0.2);  // 20% de marge en haut au lieu de centré
        
        // DEBUG (afficher une seule fois pour éviter spam)
        if (Math.random() < 0.01) {  // 1% de chance d'afficher
            System.out.println("[PlateauPanel] paintComponent: panelWidth=" + panelWidth + 
                          ", panelHeight=" + panelHeight + 
                          ", cols=" + cols + ", totalRows=" + totalRows +
                          ", caseSize=" + caseSize +
                          ", totalWidth=" + totalWidth + ", totalHeight=" + totalHeight +
                          ", offsetX=" + offsetX + ", offsetY=" + offsetY);
        }
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
        
        // Affichage du message de fin de partie si un roi est mort
        int gagnant = plateau.getGagnant();
        if (gagnant != -1) {
            String message = "Joueur " + (gagnant + 1) + " a gagné!";
            
            // Fond semi-transparent
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            
            // Texte de victoire
            Font originalFont = g.getFont();
            Font victoryFont = originalFont.deriveFont(Font.BOLD, 60f);
            g.setFont(victoryFont);
            FontMetrics fm = g.getFontMetrics(victoryFont);
            int textWidth = fm.stringWidth(message);
            int textHeight = fm.getAscent();
            int textX = (getWidth() - textWidth) / 2;
            int textY = (getHeight() + textHeight) / 2;
            g.setColor(Color.YELLOW);
            g.drawString(message, textX, textY);
            g.setFont(originalFont);
        }
    }
}
