package affichage;

import objet.Balle;
import objet.Raquette;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Rectangle;

public class BalleListener implements ActionListener, KeyListener {
    private JFrame mainFrame = null;
    protected Balle balle;
    protected PlateauPanel panel;
    protected Timer timer;
    protected double angleFleche; // angle de la flèche de départ
    protected boolean pretLancer = true;
    protected int joueurGagnant = -1; // -1 = pas de gagnant, 0 ou 1 = numéro du gagnant
    protected boolean partieTerminee = false;

    public BalleListener(Balle balle, JPanel panel) {
        this(balle, (PlateauPanel) panel, null);
    }

    public BalleListener(Balle balle, JPanel panel, JFrame mainFrame) {
        this.balle = balle;
        this.panel = (PlateauPanel) panel;
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Vérifier si la partie est terminée
        if (partieTerminee) return;
        
        objet.Plateau plateau = panel.getPlateau();
        int gagnant = plateau.getGagnant();
        if (gagnant != -1) {
            partieTerminee = true;
            joueurGagnant = gagnant;
            timer.stop();
            panel.repaint();
            if (mainFrame != null) mainFrame.repaint();
            return;
        }
        
        // --- Rebond sur case contenant une pièce vivante ---
        objet.Piece[][] pieces = plateau.getPieces();
        Rectangle[][][] cases = plateau.getCases();
        boolean collisionTraitee = false;
        
        boucleCollisions:
        for (int joueur = 0; joueur < pieces.length; joueur++) {
            if (pieces[joueur] == null) continue;
            int nbColonnes = plateau.getNbPiece();
            int lignesParJoueur = plateau.getlargeur() / 2;
            
            // Utilise la même logique que PlateauPanel pour parcourir les pièces
            int colCounter = 0;
            int ligneCounter = (joueur == 0) ? 0 : (lignesParJoueur - 1);
            
            for (int i = 0; i < pieces[joueur].length; i++) {
                if (collisionTraitee) break boucleCollisions;
                
                objet.Piece piece = pieces[joueur][i];
                
                // Sauvegarde la position courante avant d'avancer les compteurs
                int col = colCounter;
                int ligne = ligneCounter;
                
                // Avance les compteurs pour la prochaine itération
                colCounter++;
                if (colCounter >= nbColonnes) {
                    colCounter = 0;
                    if (joueur == 0) {
                        ligneCounter++;
                    } else {
                        ligneCounter--;
                    }
                }
                
                // Ignore si pas de pièce ou pièce morte
                if (piece == null || piece.getVie() <= 0) continue;
                
                // Vérifie les indices de la grille
                if (ligne < 0 || ligne >= cases[joueur].length || col < 0 || col >= cases[joueur][ligne].length) {
                    continue;
                }
                
                Rectangle caseRect = cases[joueur][ligne][col];
                    if (caseRect == null) continue;
                    
                    double balleCenterX = balle.getX();
                    double balleCenterY = balle.getY();
                    int rayonBalle = balle.getRayon();
                    
                    // Calcul de la distance réelle entre la balle et la case
                    double closestX = Math.max(caseRect.x, Math.min(balleCenterX, caseRect.x + caseRect.width));
                    double closestY = Math.max(caseRect.y, Math.min(balleCenterY, caseRect.y + caseRect.height));
                    double distanceX = balleCenterX - closestX;
                    double distanceY = balleCenterY - closestY;
                    double distanceCarree = distanceX * distanceX + distanceY * distanceY;
                    
                    // Collision uniquement si la distance est inférieure au rayon
                    if (distanceCarree <= rayonBalle * rayonBalle) {
                        double inAngle = balle.getAngle();
                        // Détecte le bord le plus proche pour éviter les ambiguïtés aux coins
                        int left = caseRect.x;
                        int right = caseRect.x + caseRect.width;
                        int top = caseRect.y;
                        int bottom = caseRect.y + caseRect.height;
                        
                        double distGauche = Math.abs(balleCenterX - left);
                        double distDroite = Math.abs(balleCenterX - right);
                        double distHaut = Math.abs(balleCenterY - top);
                        double distBas = Math.abs(balleCenterY - bottom);

                        double minDist = Math.min(Math.min(distGauche, distDroite), Math.min(distHaut, distBas));

                        double outAngle = inAngle;
                        boolean repositionne = false;

                        // Seuil pour considérer une collision de coin (en pixels)
                        double cornerThreshold = Math.max(3.0, rayonBalle * 0.6);

                        // Détection coin : si la distance horizontale minimale et la
                        // distance verticale minimale sont proches -> coin
                        double minHoriz = Math.min(distGauche, distDroite);
                        double minVert = Math.min(distHaut, distBas);
                        boolean isCorner = Math.abs(minHoriz - minVert) <= cornerThreshold;

                        if (isCorner) {
                            // Détermine quel coin (gauche/droite et haut/bas)
                            boolean useLeft = (minHoriz == distGauche);
                            boolean useTop = (minVert == distHaut);
                            int cornerX = useLeft ? left : right;
                            int cornerY = useTop ? top : bottom;
                            // Normale du coin : vecteur depuis le coin vers le centre de la balle
                            double nx = balleCenterX - cornerX;
                            double ny = balleCenterY - cornerY;
                            double norm = Math.hypot(nx, ny);
                            if (norm < 1e-6) {
                                // Si la balle est exactement sur le coin, utiliser
                                // la normale diagonale correspondante
                                nx = useLeft ? -0.7071067811865476 : 0.7071067811865476;
                                ny = useTop ? -0.7071067811865476 : 0.7071067811865476;
                            } else {
                                nx /= norm;
                                ny /= norm;
                            }
                            // Réflexion vectorielle de la vitesse sur la normale
                            double vitesse = balle.getVitesse();
                            double vx = vitesse * Math.cos(inAngle);
                            double vy = vitesse * Math.sin(inAngle);
                            double dot = vx * nx + vy * ny;
                            double rx = vx - 2 * dot * nx;
                            double ry = vy - 2 * dot * ny;
                            outAngle = Math.atan2(ry, rx);
                            // Biais léger : ramener légèrement l'angle vers la normale
                            double normalAngle = Math.atan2(ny, nx);
                            double alpha = 0.18; // fraction vers la normale (0.0-1.0)
                            // calcule delta angulaire (normalisé)
                            double delta = normalAngle - outAngle;
                            while (delta > Math.PI) delta -= 2*Math.PI;
                            while (delta < -Math.PI) delta += 2*Math.PI;
                            outAngle = outAngle + alpha * delta;

                            // Repositionne la balle juste en dehors du coin le long
                            // de la normale pour éviter ré-détection immédiate
                            double push = Math.max(1.0, rayonBalle * 0.6);
                            balle.setX(cornerX + nx * (push + rayonBalle));
                            balle.setY(cornerY + ny * (push + rayonBalle));
                            repositionne = true;
                        } else {
                            // Applique le rebond du bord le plus proche uniquement
                            if (minDist == distHaut && balleCenterY + rayonBalle >= top) {
                                outAngle = -inAngle; // rebond vertical haut
                                balle.setY(top - rayonBalle - 0.5);
                                repositionne = true;
                            } else if (minDist == distBas && balleCenterY - rayonBalle <= bottom) {
                                outAngle = -inAngle; // rebond vertical bas
                                balle.setY(bottom + rayonBalle + 0.5);
                                repositionne = true;
                            } else if (minDist == distGauche && balleCenterX + rayonBalle >= left) {
                                outAngle = Math.PI - inAngle; // rebond horizontal gauche
                                balle.setX(left - rayonBalle - 0.5);
                                repositionne = true;
                            } else if (minDist == distDroite && balleCenterX - rayonBalle <= right) {
                                outAngle = Math.PI - inAngle; // rebond horizontal droite
                                balle.setX(right + rayonBalle + 0.5);
                                repositionne = true;
                            }
                        }
                        
                        if (repositionne) {
                            // Décrémente la vie de la pièce touchée
                            try {
                                piece.setVie(piece.getVie() - 1);
                            } catch (Exception ex) {
                                // En cas d'erreur (sécurité), on ignore pour éviter crash
                            }
                            while (outAngle > Math.PI) outAngle -= 2*Math.PI;
                            while (outAngle < -Math.PI) outAngle += 2*Math.PI;
                            balle.setAngle(outAngle);
                            balle.updateVitesse();
                            // Avance la balle immédiatement après rebond pour la sortir de la collision
                            balle.move();
                            System.out.println("DEBUG PIECE: col=" + col + ", ligne=" + ligne + ", joueur=" + joueur + ", piece=" + piece.getNom() + ", vie=" + piece.getVie());
                            collisionTraitee = true;
                            break boucleCollisions;
                        }
                    }
            }
        }
        
        // --- Détection collision balle/raquette ---
        if (!collisionTraitee) {
            Raquette[] raquettes = panel.getPlateau().getRaquettes();
            Rectangle plateauBounds = panel.getPlateauBounds();
            int numJoueurs = raquettes.length;
            int lignesParJoueurRaquette = panel.getPlateau().getlargeur() / 2;
            int cols = panel.getPlateau().getNbPiece();
            int caseWidth = panel.getWidth() / (cols + 2);
            int caseHeight = panel.getHeight() / (numJoueurs * lignesParJoueurRaquette + 2);
            int caseSize = Math.min(caseWidth, caseHeight);
            int offsetX = plateauBounds.x;
            int offsetY = plateauBounds.y;
            int rayonBalle = balle.getRayon();
            double bx = balle.getX();
            double by = balle.getY();
            
            for (Raquette raquette : raquettes) {
                if (raquette == null) continue;
                int joueurRaquette = raquette.getJoueur();
                int largeurR = raquette.getLargeur();
                int angleR = raquette.getAngle();
                double xPixel = raquette.getXPixel();
                int ligneRaquette = (joueurRaquette == 0) ? 2 : 1;
                int yR = offsetY + (joueurRaquette * lignesParJoueurRaquette + ligneRaquette) * caseSize;
                int raquetteWidth = caseSize * largeurR;
                int raquetteHeight = caseSize / 2;
                int xR = (int)(offsetX + xPixel * caseSize - raquetteWidth / 2);
                Rectangle raquetteRect = new Rectangle(xR, yR, raquetteWidth, raquetteHeight);
                
                double balleCenterX = bx;
                double balleCenterY = by;
                
                if (raquetteRect.intersects(new Rectangle((int)(balleCenterX - rayonBalle), (int)(balleCenterY - rayonBalle), 2*rayonBalle, 2*rayonBalle))) {
                    // Détecte le bord le plus proche pour éviter les ambiguïtés aux coins
                    double distGauche = Math.abs(balleCenterX - xR);
                    double distDroite = Math.abs(balleCenterX - (xR + raquetteWidth));
                    double distHaut = Math.abs(balleCenterY - yR);
                    double distBas = Math.abs(balleCenterY - (yR + raquetteHeight));
                    
                    double minDist = Math.min(Math.min(distGauche, distDroite), Math.min(distHaut, distBas));
                    
                    double inAngle = balle.getAngle();
                    double outAngle = inAngle;
                    double vitesseInitiale = balle.getVitesse();
                    boolean repositionne = false;
                    
                    // Traite le bord le plus proche uniquement
                    if ((minDist == distHaut && balleCenterY >= yR) || (minDist == distBas && balleCenterY <= (yR + raquetteHeight))) {
                        // Rebond sur le dessus/dessous avec angle de la raquette
                        double raquetteAngleRad = Math.toRadians(angleR);
                        double impact = (balleCenterX - (xR + raquetteWidth/2)) / (raquetteWidth/2);
                        double normal;
                        if (minDist == distHaut) {
                            normal = raquetteAngleRad - Math.PI/2;
                            balle.setY(yR - rayonBalle - 0.5);
                        } else {
                            normal = raquetteAngleRad + Math.PI/2;
                            balle.setY(yR + raquetteHeight + rayonBalle + 0.5);
                        }
                        outAngle = 2*normal - inAngle;
                        outAngle += impact * Math.PI/4;
                        repositionne = true;
                    } else if (minDist == distGauche) {
                        outAngle = Math.PI - inAngle;
                        balle.setX(xR - rayonBalle - 0.5);
                        repositionne = true;
                    } else if (minDist == distDroite) {
                        outAngle = Math.PI - inAngle;
                        balle.setX(xR + raquetteWidth + rayonBalle + 0.5);
                        repositionne = true;
                    }
                    
                    if (repositionne) {
                        while (outAngle > Math.PI) outAngle -= 2*Math.PI;
                        while (outAngle < -Math.PI) outAngle += 2*Math.PI;
                        balle.setAngle(outAngle);
                        balle.setVitesse(vitesseInitiale);
                        balle.updateVitesse();
                        System.out.println("DEBUG RAQUETTE: joueur=" + joueurRaquette + ", minDist=" + minDist + ", dH=" + distHaut + ", dB=" + distBas + ", dG=" + distGauche + ", dD=" + distDroite + ", pos=(" + balleCenterX + "," + balleCenterY + ")");
                        collisionTraitee = true;
                        break;
                    }
                }
            }
        }
        
        // --- Rebonds sur les murs du plateau ---
        Rectangle bounds = panel.getPlateauBounds();
        int minX = bounds.x;
        int minY = bounds.y;
        int maxX = bounds.x + bounds.width;
        int maxY = bounds.y + bounds.height;
        double nextX = balle.getX() + balle.getVitesse() * Math.cos(balle.getAngle());
        double nextY = balle.getY() + balle.getVitesse() * Math.sin(balle.getAngle());
        int r = balle.getRayon();
        
        // Rebonds sur les murs gauche/droite
        if (nextX - r < minX) {
            balle.setAngle(Math.PI - balle.getAngle());
            balle.updateVitesse();
            balle.setX(minX + r);
        } else if (nextX + r > maxX) {
            balle.setAngle(Math.PI - balle.getAngle());
            balle.updateVitesse();
            balle.setX(maxX - r);
        }
        
        // Rebonds sur les murs haut/bas
        if (nextY - r < minY) {
            balle.setAngle(-balle.getAngle());
            balle.updateVitesse();
            balle.setY(minY + r);
        } else if (nextY + r > maxY) {
            balle.setAngle(-balle.getAngle());
            balle.updateVitesse();
            balle.setY(maxY - r);
        }
        // Bouge la balle seulement si aucune collision pièce/raquette n'a été traitée
        if (!collisionTraitee) {
            balle.move();
        }
        
        // Correction finale : ne jamais sortir
        if (balle.getX() - r < minX) balle.setX(minX + r);
        if (balle.getX() + r > maxX) balle.setX(maxX - r);
        if (balle.getY() - r < minY) balle.setY(minY + r);
        if (balle.getY() + r > maxY) balle.setY(maxY - r);
        
        panel.repaint();
        if (mainFrame != null) mainFrame.repaint();
    }

    // Gestion des touches pour l'angle de la flèche
    @Override
    public void keyPressed(KeyEvent e) {
        if (pretLancer) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                angleFleche -= Math.PI/36;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                angleFleche += Math.PI/36;
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                balle.setAngle(angleFleche);
                balle.setEnMouvement(true);
                timer.start();
                pretLancer = false;
                panel.requestFocusInWindow();
            }
            panel.repaint();
        }
    }
    
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public double getAngleFleche() { return angleFleche; }
    public boolean isPretLancer() { return pretLancer; }
    public boolean isPartieTerminee() { return partieTerminee; }
    public int getJoueurGagnant() { return joueurGagnant; }
    public void reset() {
        pretLancer = true;
        balle.setEnMouvement(false);
        timer.stop();
        partieTerminee = false;
        joueurGagnant = -1;
    }
}

