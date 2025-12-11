
package affichage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
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
    
    // Network-related fields
    private reseau.GameServer gameServer;
    private reseau.GameClient gameClient;
    private boolean modeServeur = false;
    private boolean modeClient = false;
    private objet.Plateau plateau;  // Référence au plateau pour les mises à jour
    private java.util.ArrayList<Integer> viesIndividuelles;  // Vies des pièces pour J1

    public MaFenetre() {
        setTitle("Initialisation de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 1080);  // Moitié du width pour centrer le plateau
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
        setSize(950, 1080);
        setLocationRelativeTo(null);  // Recentrer après changement de taille
        
        // Stocker le plateau pour les mises à jour réseau
        this.plateau = plateau;
        
        System.out.println("[afficherPlateau] Plateau créé avec nbPiece=" + plateau.getNbPiece() + 
                          ", joueur=" + plateau.getId());

        // Initialisation de la balle au centre du plateau
        // Utiliser des valeurs FIXES pour garantir la même position entre J1 et J2
        final int WINDOW_WIDTH = 950;  // Moitié de 1900px pour centrer
        final int WINDOW_HEIGHT = 1080;
        int largeur = plateau.getlargeur();
        int nbPiece = plateau.getNbPiece();
        int caseSize = (int)(Math.min(WINDOW_WIDTH / (nbPiece + 2), WINDOW_HEIGHT / (largeur + 2)) * 0.8);  // Réduction de 20%
        int xCentre = WINDOW_WIDTH / 2;
        int yCentre = WINDOW_HEIGHT / 2;
        int rayon = Math.max(18, caseSize / 3); // taille raisonnable
        balle = new Balle(xCentre, yCentre, 5, -Math.PI/4, rayon, Color.BLUE);
        
        System.out.println("[afficherPlateau] Balle initialisée: x=" + xCentre + ", y=" + yCentre + ", rayon=" + rayon);
        ballePanel = new BallePanel(balle);

        // PlateauPanel avec balle ET support réseau
        // J1 (serveur) contrôle joueur 0, J2 (client) contrôle joueur 1
        int localJoueurId = modeServeur ? 0 : (modeClient ? 1 : -1);
        plateauPanel = new PlateauPanel(plateau, balle, gameServer, gameClient, localJoueurId);
        
        // Si mode serveur, envoyer la config au client et ajouter listener
        if (modeServeur && gameServer != null) {
            setTitle("Échec Pong - Serveur (Joueur 1)");
            
            // Créer la config avec les vies individuelles
            int nbVieDefaut = (viesIndividuelles != null && viesIndividuelles.size() > 0) ? 
                              viesIndividuelles.get(0) : 3;
            reseau.ConfigPartie config = new reseau.ConfigPartie(nbPiece, nbVieDefaut, viesIndividuelles);
            reseau.Message msg = new reseau.Message(
                reseau.Message.Type.GAME_START, 
                config
            );
            gameServer.broadcastMessage(msg);
            
            // Ajouter le serveur listener pour synchroniser avec les clients
            gameServer.setServerListener(new reseau.GameServer.ServerListener() {
                @Override
                public void onClientConnected(int clientNumber) {
                    System.out.println("[SERVEUR] Client " + clientNumber + " connecté");
                    // Renvoyer la config au nouveau client
                    int nbVieDefaut = (viesIndividuelles != null && viesIndividuelles.size() > 0) ? 
                                      viesIndividuelles.get(0) : 3;
                    reseau.ConfigPartie config = new reseau.ConfigPartie(plateau.getNbPiece(), nbVieDefaut, viesIndividuelles);
                    reseau.Message msg = new reseau.Message(
                        reseau.Message.Type.GAME_START, 
                        config
                    );
                    try {
                        gameServer.sendToClient(clientNumber, msg);
                    } catch (IOException e) {
                        System.err.println("Erreur envoi config: " + e.getMessage());
                    }
                }
                
                @Override
                public void onClientDisconnected(int clientNumber) {
                    System.out.println("[SERVEUR] Client " + clientNumber + " déconnecté");
                }
                
                @Override
                public void onMessageReceived(int clientNumber, reseau.Message message) {
                    handleServerMessage(clientNumber, message);
                }
                
                @Override
                public void onServerError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
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

        balleListener = new BalleListener(balle, plateauPanel, this, modeServeur ? gameServer : null) {
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
        
        // Reprendre le focus quand on clique sur le ballePanelDecorator pendant la phase de lancement
        ballePanelDecorator.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (phaseLancement) {
                    ballePanelDecorator.requestFocusInWindow();
                    System.out.println("[BallePanelDecorator] Focus repris pour W/X");
                }
            }
        });
        
        ballePanel.setVisible(false); // cachée au début

        // ...existing code...

        // Layout superposé : plateau en fond, balle (et flèche) au-dessus
        // Utiliser dimensions pour centrer le plateau
        final int FIXED_WIDTH = 950;  // Moitié de 1900px
        final int FIXED_HEIGHT = 1080;
        
        JPanel layeredPanel = new JPanel(null);
        layeredPanel.setOpaque(false);
        plateauPanel.setBounds(0, 0, FIXED_WIDTH, FIXED_HEIGHT);
        ballePanelDecorator.setBounds(0, 0, FIXED_WIDTH, FIXED_HEIGHT);
        layeredPanel.add(plateauPanel);
        layeredPanel.add(ballePanelDecorator);

        getContentPane().removeAll();
        add(layeredPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        // Le focus est donné au ballePanelDecorator pour les touches W/X
        // Il sera transféré au plateauPanel après le lancement (touche ESPACE)
        SwingUtilities.invokeLater(() -> {
            ballePanelDecorator.requestFocusInWindow();
            ballePanelDecorator.setFocusable(true);
            System.out.println("[afficherPlateau] Focus donné au ballePanelDecorator pour W/X");
        });
    }
    // Getters et setters pour accès depuis le listener
    public InitialisationNbPiecePanel getPanelNbPiece() { return panelNbPiece; }
    public InitialisationViePiecePanel getPanelViePiece() { return panelViePiece; }
    public JButton getValiderNbPieceBtn() { return validerNbPieceBtn; }
    public JButton getValiderVieBtn() { return validerVieBtn; }
    public void setPanelViePiece(InitialisationViePiecePanel panelViePiece) { this.panelViePiece = panelViePiece; }
    public void setValiderVieBtn(JButton validerVieBtn) { this.validerVieBtn = validerVieBtn; }
    public void setViesIndividuelles(java.util.ArrayList<Integer> vies) { this.viesIndividuelles = vies; }

    /**
     * Configure la fenêtre en mode serveur (Joueur 1)
     * J1 suit le flux normal d'initialisation avec ses panels
     */
    public void setModeServeur(reseau.GameServer server) {
        setTitle("Initialisation de la partie - Serveur (Joueur 1)");
        this.gameServer = server;
        this.modeServeur = true;
        
        // Le constructeur a déjà affiché InitialisationNbPiecePanel
        // Le flux normal s'exécutera : InitialisationNbPieceListener → InitialisationViePiecePanel → InitialisationViePieceListener → afficherPlateau()
        // À la fin du flux, afficherPlateau() vérifiera modeServeur et enverra la config au client
    }
    
    /**
     * Configure la fenêtre en mode client (Joueur 2)
     * J2 reçoit la config du serveur et rejoint directement
     */
    public void setModeClientAvecConfig(reseau.GameClient client, reseau.ConfigPartie config) {
        // Récupère la config du serveur
        int nbPiece = config.getNbPiece();
        int nbVie = config.getNbVie();
        java.util.ArrayList<Integer> vies = config.getVies();
        
        System.out.println("[CLIENT] Config reçue: nbPiece=" + nbPiece + ", nbVie=" + nbVie + 
                          ", vies=" + (vies != null ? vies.size() : "null"));
        if (vies != null) {
            System.out.println("[CLIENT] Vies détail: " + vies);
        }
        
        // Lance directement le plateau sans UI d'initialisation
        // Créer les pièces avec les 16 éléments (comme J1, pour avoir l'ordre correct)
        objet.Piece[][] pieces = new objet.Piece[2][16];
        java.util.List<objet.Piece> allPieces = objet.Piece.getAll();
        
        System.out.println("[CLIENT] Création pieces: allPieces.size()=" + allPieces.size() + 
                          ", vies.size()=" + (vies != null ? vies.size() : "null"));
        
        if (vies != null && !vies.isEmpty()) {
            // Utiliser les vies reçues
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 16; j++) {
                    objet.Piece base = allPieces.get(j);
                    int vie = (j < vies.size()) ? vies.get(j) : 0;
                    pieces[i][j] = new objet.Piece(base.getId(), vie, base.getNom(), base.getPosition());
                    if (i == 0) {  // Affiche une seule fois
                        System.out.println("[CLIENT] Piece[" + j + "]: " + base.getNom() + ", vie=" + vie);
                    }
                }
            }
        } else {
            // Fallback : utiliser nbVie uniforme
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 16; j++) {
                    objet.Piece base = allPieces.get(j);
                    pieces[i][j] = new objet.Piece(base.getId(), nbVie, base.getNom(), base.getPosition());
                }
            }
        }
        
        System.out.println("[CLIENT] Plateau créé: pieces[0].length=" + pieces[0].length + 
                          ", pieces[1].length=" + pieces[1].length);
        
        // J2 crée son plateau avec joueur=0 (lui-même est le joueur 0 localement)
        objet.Plateau plateauJ2 = new objet.Plateau(0, nbPiece, pieces);
        
        // DEBUG: Log des pièces vivantes
        objet.Piece[][] piecesVivantesJ2 = plateauJ2.getPiecesVivantesTriees();
        System.out.println("[CLIENT] Pièces vivantes J2: " + piecesVivantesJ2[0].length + " et " + piecesVivantesJ2[1].length);
        System.out.println("[CLIENT] Détail J2[0]: ");
        for (objet.Piece p : piecesVivantesJ2[0]) {
            System.out.println("  - " + p.getNom() + " (vie=" + p.getVie() + ")");
        }
        
        setTitle("Échec Pong - Client (Joueur 2)");
        this.gameClient = client;
        this.modeClient = true;
        this.plateau = plateauJ2;
        
        afficherPlateau(plateauJ2);
        
        // Le focus reste sur ballePanelDecorator pour observer la phase de lancement
        // Le focus sera automatiquement transféré au plateauPanel quand la balle sera lancée
        
        // Ajouter le client listener pour synchroniser avec le serveur
        client.setClientListener(new reseau.GameClient.ClientListener() {
            @Override
            public void onConnected() {
                System.out.println("[CLIENT] Connecté au serveur");
            }
            
            @Override
            public void onMessageReceived(reseau.Message message) {
                handleClientMessage(message);
            }
            
            @Override
            public void onDisconnected() {
                JOptionPane.showMessageDialog(MaFenetre.this,
                    "Déconnecté du serveur",
                    "Déconnexion", JOptionPane.WARNING_MESSAGE);
            }
            
            @Override
            public void onConnectionFailed(Exception e) {
                JOptionPane.showMessageDialog(MaFenetre.this,
                    "Impossible de se connecter : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
            @Override
            public void onError(Exception e) {
                JOptionPane.showMessageDialog(MaFenetre.this,
                    "Erreur : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Traite les messages reçus par le serveur
     */
    private void handleServerMessage(int clientNumber, reseau.Message message) {
        switch (message.getType()) {
            case PING:
                // Répondre au ping
                try {
                    // À implémenter
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RAQUETTE_POSITION:
                // Mettre à jour la position de la raquette reçue du client
                if (message.getData() instanceof reseau.RaquetteState) {
                    reseau.RaquetteState state = (reseau.RaquetteState) message.getData();
                    updateRaquetteFromNetwork(state);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Traite les messages reçus par le client
     */
    private void handleClientMessage(reseau.Message message) {
        switch (message.getType()) {
            case GAME_START:
                // Réceptionner la config du serveur et afficher le plateau
                if (message.getData() instanceof reseau.ConfigPartie) {
                    reseau.ConfigPartie config = (reseau.ConfigPartie) message.getData();
                    SwingUtilities.invokeLater(() -> {
                        int nbPiece = config.getNbPiece();
                        int nbVie = config.getNbVie();
                        objet.Piece[][] pieces = reseau.PieceFactory.initialiserPieces(nbPiece, nbVie);
                        objet.Plateau plateau = new objet.Plateau(1, nbPiece, pieces);
                        afficherPlateau(plateau);
                    });
                }
                break;
            case BALL_UPDATE:
                // Mettre à jour la position de la balle
                if (message.getData() instanceof reseau.BalleState) {
                    reseau.BalleState state = (reseau.BalleState) message.getData();
                    if (balle != null) {
                        balle.setX(state.getX());
                        balle.setY(state.getY());
                        balle.setAngle(state.getAngle());
                        balle.setVitesse(state.getVitesse());
                        balle.setEnMouvement(state.isEnMouvement());
                        
                        // Si la balle est en mouvement, désactiver la phase de lancement côté client
                        if (state.isEnMouvement() && phaseLancement) {
                            phaseLancement = false;
                            if (plateauPanel != null) {
                                plateauPanel.setPhaseLancement(false);
                                // Transférer le focus au plateau pour les contrôles raquette
                                plateauPanel.requestFocusInWindow();
                                System.out.println("[CLIENT] Balle lancée - Focus transféré au plateau pour contrôles");
                            }
                        }
                        
                        // Debug log (limité pour éviter spam)
                        if (Math.random() < 0.01) {
                            System.out.println("[CLIENT] BALL_UPDATE reçu: x=" + state.getX() + ", y=" + state.getY() + 
                                             ", enMouvement=" + state.isEnMouvement());
                        }
                        
                        // Rendre la balle visible et repaint
                        if (ballePanel != null) ballePanel.setVisible(true);
                        if (ballePanelDecorator != null) ballePanelDecorator.setVisible(true);
                        if (plateauPanel != null) plateauPanel.repaint();
                        if (ballePanelDecorator != null) ballePanelDecorator.repaint();
                    }
                }
                break;
            case PIECE_HIT:
                // Mettre à jour l'état d'une pièce
                if (message.getData() instanceof reseau.PieceState) {
                    reseau.PieceState state = (reseau.PieceState) message.getData();
                    if (plateauPanel != null) {
                        objet.Plateau plateau = plateauPanel.getPlateau();
                        objet.Piece piece = plateau.getPieces()[state.getJoueur()][state.getIndexPiece()];
                        if (piece != null) {
                            piece.setVie(state.getVie());
                            
                            System.out.println("[CLIENT] PIECE_HIT reçu: joueur=" + state.getJoueur() + 
                                             ", index=" + state.getIndexPiece() + 
                                             ", nouvellVie=" + state.getVie());
                            
                            // Repaint pour afficher la mise à jour
                            plateauPanel.repaint();
                        }
                    }
                }
                break;
            case RAQUETTE_POSITION:
                // Mettre à jour la position de la raquette reçue du serveur
                if (message.getData() instanceof reseau.RaquetteState) {
                    reseau.RaquetteState state = (reseau.RaquetteState) message.getData();
                    updateRaquetteFromNetwork(state);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Met à jour une raquette à partir d'un message réseau
     */
    private void updateRaquetteFromNetwork(reseau.RaquetteState state) {
        if (plateauPanel == null) return;
        
        objet.Plateau plateau = plateauPanel.getPlateau();
        objet.Raquette[] raquettes = plateau.getRaquettes();
        
        if (raquettes != null && state.getJoueur() >= 0 && state.getJoueur() < raquettes.length) {
            objet.Raquette raquette = raquettes[state.getJoueur()];
            if (raquette != null) {
                raquette.setXPixel(state.getXPixel());
                raquette.setAngle(state.getAngle());
                
                // Debug log (limité)
                if (Math.random() < 0.05) {
                    System.out.println("[RAQUETTE_UPDATE] Joueur " + state.getJoueur() + 
                                     ": x=" + state.getXPixel() + ", angle=" + state.getAngle());
                }
                
                // Repaint pour afficher
                plateauPanel.repaint();
            }
        }
    }
}
