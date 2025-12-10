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

    public RaquetteListener(Raquette raquette, int minCol, int maxCol) {
        this(raquette, minCol, maxCol, null);
    }

    public RaquetteListener(Raquette raquette, int minCol, int maxCol, Component repaintTarget) {
        this.raquette = raquette;
        this.minCol = minCol;
        this.maxCol = maxCol;
        this.repaintTarget = repaintTarget;
    }

    /**
     * Constructeur avec mappage de touches personnalisé.
     * keyLeft/keyRight/keyUp/keyDown sont des constantes KeyEvent.VK_*
     */
    public RaquetteListener(Raquette raquette, int minCol, int maxCol, Component repaintTarget,
                            int keyLeft, int keyRight, int keyUp, int keyDown) {
        this.raquette = raquette;
        this.minCol = minCol;
        this.maxCol = maxCol;
        this.repaintTarget = repaintTarget;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean changed = false;
        int largeurR = raquette.getLargeur();
        int nbColonnes = maxCol + 1;
        double minXPixel = largeurR / 2.0;
        double maxXPixel = nbColonnes - 1 - largeurR / 2.0;
        int code = e.getKeyCode();
        if (code == keyLeft) {
            if (animationTimer != null && animationTimer.isRunning()) return;
            double currentX = raquette.getXPixel();
            double targetLeft = Math.max(minXPixel, currentX - 1);
            int newColLeft = (int)Math.round(targetLeft);
            targetXPixel = targetLeft;
            startAnimationTo(targetXPixel, newColLeft);
            System.out.println("Raquette: déplacement gauche, col=" + newColLeft);
        } else if (code == keyRight) {
            if (animationTimer != null && animationTimer.isRunning()) return;
            double currentXr = raquette.getXPixel();
            double targetRight = Math.min(maxXPixel, currentXr + 1);
            int newColRight = (int)Math.round(targetRight);
            targetXPixel = targetRight;
            startAnimationTo(targetXPixel, newColRight);
            System.out.println("Raquette: déplacement droite, col=" + newColRight);
        } else if (code == keyUp) {
            raquette.setAngle(raquette.getAngle() + 5); // +5°
            System.out.println("Raquette: angle augmenté, angle=" + raquette.getAngle());
            changed = true;
        } else if (code == keyDown) {
            raquette.setAngle(raquette.getAngle() - 5); // -5°
            System.out.println("Raquette: angle diminué, angle=" + raquette.getAngle());
            changed = true;
        }
        if (changed && repaintTarget != null) {
            repaintTarget.repaint();
        }
    }

    private void startAnimationTo(double target, int targetCol) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        animationTimer = new javax.swing.Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                double current = raquette.getXPixel();
                double delta = target - current;
                if (Math.abs(delta) < ANIMATION_SPEED) {
                    raquette.setXPixel(target);
                    raquette.setColMilieu(targetCol);
                    animationTimer.stop();
                } else {
                    raquette.setXPixel(current + Math.signum(delta) * ANIMATION_SPEED);
                }
                if (repaintTarget != null) repaintTarget.repaint();
            }
        });
        animationTimer.start();
    }
}
