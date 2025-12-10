
package affichage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import objet.Plateau;



import objet.Balle;

public class MaFenetre extends JFrame {
    private InitialisationNbPiecePanel panelNbPiece;
    private InitialisationViePiecePanel panelViePiece;
    private PlateauPanel plateauPanel;
    private JButton validerNbPieceBtn;
    private JButton validerVieBtn;

    // Ball-related fields
    private Balle balle;
    private BallePanel ballePanel;
    private BalleListener balleListener;
    private BallePanelDecorator ballePanelDecorator;
    private boolean phaseLancement = true; // true: only arrow, false: ball in play

    public MaFenetre() {
        setTitle("Initialisation de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelNbPiece = new InitialisationNbPiecePanel();
        validerNbPieceBtn = new JButton("Valider");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(panelNbPiece);
        topPanel.add(validerNbPieceBtn);
        add(topPanel, BorderLayout.NORTH);

        // Ajout du listener séparé
        validerNbPieceBtn.addActionListener(new InitialisationNbPieceListener(this)); // Listener for validating pieces
    }

    public void afficherPanelViePiece(int nbPiece) {
        setPanelViePiece(new InitialisationViePiecePanel(nbPiece));
        setValiderVieBtn(new JButton("Valider les vies"));
        // Panel vertical pour centrer les deux éléments
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setOpaque(false);
        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(getPanelViePiece());
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(getValiderVieBtn());
        verticalPanel.add(btnPanel);
        verticalPanel.add(Box.createVerticalGlue());
        getContentPane().removeAll();
        add(verticalPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        getValiderVieBtn().addActionListener(new InitialisationViePieceListner(this));
    }

    public void afficherPlateau(Plateau plateau) {
        setSize(1900, 1080);

        // Initialisation de la balle au centre du plateau
        int largeur = plateau.getlargeur();
        int nbPiece = plateau.getNbPiece();
        int caseSize = Math.min(getWidth() / (nbPiece + 2), getHeight() / (largeur + 2));
        int xCentre = getWidth() / 2;
        int yCentre = getHeight() / 2;
        int rayon = Math.max(18, caseSize / 3); // taille raisonnable
        balle = new Balle(xCentre, yCentre, 5, -Math.PI/4, rayon, Color.BLUE);
        ballePanel = new BallePanel(balle);

        // PlateauPanel avec balle
        plateauPanel = new PlateauPanel(plateau, balle);
        // Synchronise l'angle initial et la phase de lancement
        plateauPanel.setAngleFleche(-Math.PI/4);
        plateauPanel.setPhaseLancement(true);

        // Timer pour animer la balle et rafraîchir le plateau
        Timer balleTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (balle.isEnMouvement()) {
                    // La logique de rebond est gérée dans le listener
                    balleListener.actionPerformed(e);
                    plateauPanel.repaint();
                }
            }
        });

        balleListener = new BalleListener(balle, plateauPanel, this) {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (phaseLancement) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) {
                        System.out.println("Touche W pressée : angle--");
                        angleFleche -= Math.PI/36;
                        plateauPanel.setAngleFleche(angleFleche);
                        panel.repaint();
                        ballePanelDecorator.repaint();
                    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_X) {
                        System.out.println("Touche X pressée : angle++");
                        angleFleche += Math.PI/36;
                        plateauPanel.setAngleFleche(angleFleche);
                        panel.repaint();
                        ballePanelDecorator.repaint();
                    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                        System.out.println("Touche ESPACE pressée : lancement");
                        balle.setAngle(angleFleche);
                        balle.setEnMouvement(true);
                        balleTimer.start();
                        pretLancer = false;
                        phaseLancement = false;
                        plateauPanel.setPhaseLancement(false);
                        ballePanel.setVisible(true);
                        panel.repaint();
                        ballePanelDecorator.repaint();
                        ballePanelDecorator.setVisible(true);
                        // Après le lancement, focus sur le plateau pour les raquettes
                        plateauPanel.requestFocusInWindow();
                    }
                    System.out.println("angleFleche=" + angleFleche + ", phaseLancement=" + phaseLancement);
                } else {
                    super.keyPressed(e);
                }
            }
        };
        ballePanelDecorator = new BallePanelDecorator(ballePanel, balleListener) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (phaseLancement) {
                    // Affiche seulement la flèche de lancement (pas la balle)
                    int x = (int) balle.getX();
                    int y = (int) balle.getY();
                    int r = balle.getRayon();
                    int len = r * 4;
                    double angle = balleListener.getAngleFleche();
                    int x2 = x + (int) (len * Math.cos(angle));
                    int y2 = y + (int) (len * Math.sin(angle));
                    g.setColor(java.awt.Color.ORANGE);
                    g.drawLine(x, y, x2, y2);
                    g.fillOval(x2-4, y2-4, 8, 8);
                }
            }
        };
        ballePanel.setVisible(false); // cachée au début

        // ...existing code...

        // Layout superposé : plateau en fond, balle (et flèche) au-dessus
        JPanel layeredPanel = new JPanel(null);
        layeredPanel.setOpaque(false);
        plateauPanel.setBounds(0, 0, getWidth(), getHeight());
        ballePanelDecorator.setBounds(0, 0, getWidth(), getHeight());
        layeredPanel.add(plateauPanel);
        layeredPanel.add(ballePanelDecorator);

        getContentPane().removeAll();
        add(layeredPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        ballePanelDecorator.requestFocusInWindow();
    }
    // Getters et setters pour accès depuis le listener
    public InitialisationNbPiecePanel getPanelNbPiece() { return panelNbPiece; }
    public InitialisationViePiecePanel getPanelViePiece() { return panelViePiece; }
    public JButton getValiderNbPieceBtn() { return validerNbPieceBtn; }
    public JButton getValiderVieBtn() { return validerVieBtn; }
    public void setPanelViePiece(InitialisationViePiecePanel panelViePiece) { this.panelViePiece = panelViePiece; }
    public void setValiderVieBtn(JButton validerVieBtn) { this.validerVieBtn = validerVieBtn; }

    // ...la classe MaFenetre ne contient plus de main...
}
