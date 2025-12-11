package affichage;

import objet.Raquette;
import java.awt.event.*;
import java.awt.Component;

public class RaquetteListener extends KeyAdapter {
    private Raquette raquette;
    private int minCol;
    private int maxCol;
    private Component repaintTarget;
    private javax.swing.Timer animationTimer;
    private double targetXPixel = -1;
    private static final double ANIMATION_SPEED = 0.2; // case par tick
    private int keyLeft = KeyEvent.VK_LEFT;
    private int keyRight = KeyEvent.VK_RIGHT;
    private int keyUp = KeyEvent.VK_UP;
    private int keyDown = KeyEvent.VK_DOWN;
    
    // Pour la synchronisation réseau
    private reseau.GameServer gameServer = null;
    private reseau.GameClient gameClient = null;
    private int localJoueurId = -1;  // ID du joueur local (0 pour J1, 1 pour J2)

    public RaquetteListener(Raquette raquette, int minCol, int maxCol) {
        this(raquette, minCol, maxCol, null, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, null, null, -1);
    }

    public RaquetteListener(Raquette raquette, int minCol, int maxCol, Component repaintTarget) {
        this(raquette, minCol, maxCol, repaintTarget, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, null, null, -1);
    }

    /**
     * Constructeur avec mappage de touches personnalisé.
     * keyLeft/keyRight/keyUp/keyDown sont des constantes KeyEvent.VK_*
     */
    public RaquetteListener(Raquette raquette, int minCol, int maxCol, Component repaintTarget,
                            int keyLeft, int keyRight, int keyUp, int keyDown) {
        this(raquette, minCol, maxCol, repaintTarget, keyLeft, keyRight, keyUp, keyDown, null, null, -1);
    }
    
    /**
     * Constructeur complet avec support réseau
     */
    public RaquetteListener(Raquette raquette, int minCol, int maxCol, Component repaintTarget,
                            int keyLeft, int keyRight, int keyUp, int keyDown,
                            reseau.GameServer gameServer, reseau.GameClient gameClient, int localJoueurId) {
        this.raquette = raquette;
        this.minCol = minCol;
        this.maxCol = maxCol;
        this.repaintTarget = repaintTarget;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.gameServer = gameServer;
        this.gameClient = gameClient;
        this.localJoueurId = localJoueurId;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean changed = false;
        int largeurR = raquette.getLargeur();
        int nbColonnes = maxCol + 1;
        double minXPixel = largeurR / 2.0;
        double maxXPixel = nbColonnes - 1 - largeurR / 2.0;
        int code = e.getKeyCode();
        
        // Debug: afficher quelle touche est pressée et par quel joueur
        String toucheName = KeyEvent.getKeyText(code);
        System.out.println("[RaquetteListener] Joueur " + localJoueurId + " - Touche pressée: " + toucheName + " (code=" + code + ")");
        
        if (code == keyLeft) {
            if (animationTimer != null && animationTimer.isRunning()) return;
            double currentX = raquette.getXPixel();
            double targetLeft = Math.max(minXPixel, currentX - 1);
            int newColLeft = (int)Math.round(targetLeft);
            targetXPixel = targetLeft;
            startAnimationTo(targetXPixel, newColLeft);
            System.out.println("Raquette J" + localJoueurId + ": déplacement gauche, col=" + newColLeft);
        } else if (code == keyRight) {
            if (animationTimer != null && animationTimer.isRunning()) return;
            double currentXr = raquette.getXPixel();
            double targetRight = Math.min(maxXPixel, currentXr + 1);
            int newColRight = (int)Math.round(targetRight);
            targetXPixel = targetRight;
            startAnimationTo(targetXPixel, newColRight);
            System.out.println("Raquette J" + localJoueurId + ": déplacement droite, col=" + newColRight);
        } else if (code == keyUp) {
            raquette.setAngle(raquette.getAngle() + 5); // +5°
            System.out.println("Raquette J" + localJoueurId + ": angle augmenté, angle=" + raquette.getAngle());
            changed = true;
            sendRaquetteUpdate();  // Envoyer update réseau
        } else if (code == keyDown) {
            raquette.setAngle(raquette.getAngle() - 5); // -5°
            System.out.println("Raquette J" + localJoueurId + ": angle diminué, angle=" + raquette.getAngle());
            changed = true;
            sendRaquetteUpdate();  // Envoyer update réseau
        }
        if (changed && repaintTarget != null) {
            repaintTarget.repaint();
        }
    }

    private void startAnimationTo(double target, int targetCol) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        // Envoyer la position de départ
        sendRaquetteUpdate();
        
        animationTimer = new javax.swing.Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                double current = raquette.getXPixel();
                double delta = target - current;
                if (Math.abs(delta) < ANIMATION_SPEED) {
                    raquette.setXPixel(target);
                    raquette.setColMilieu(targetCol);
                    animationTimer.stop();
                    // Envoyer la position finale seulement
                    sendRaquetteUpdate();
                } else {
                    raquette.setXPixel(current + Math.signum(delta) * ANIMATION_SPEED);
                }
                if (repaintTarget != null) repaintTarget.repaint();
                // Ne plus envoyer pendant l'animation pour réduire la latence
            }
        });
        animationTimer.start();
    }
    
    /**
     * Envoie l'état actuel de la raquette au réseau (serveur ou client)
     */
    private void sendRaquetteUpdate() {
        if (localJoueurId < 0) {
            return;  // Pas de joueur local défini
        }
        
        try {
            reseau.RaquetteState state = new reseau.RaquetteState(
                localJoueurId,
                raquette.getXPixel(),
                raquette.getAngle(),
                raquette.getLargeur()
            );
            
            reseau.Message msg = new reseau.Message(
                reseau.Message.Type.RAQUETTE_POSITION,
                state
            );
            
            // Envoyer via serveur (J1) ou client (J2)
            if (gameServer != null) {
                gameServer.broadcastMessage(msg);
            } else if (gameClient != null) {
                gameClient.sendMessage(msg);
            }
        } catch (Exception e) {
            // Ignorer les erreurs réseau pour ne pas bloquer le jeu
            System.err.println("[RaquetteListener] Erreur envoi raquette: " + e.getMessage());
        }
    }
}
