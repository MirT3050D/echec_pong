package affichage;

import reseau.*;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog pour se connecter au serveur (Joueur 2)
 */
public class DialogConnexionClient extends JDialog {
    private GameClient client;
    private String serverIP = "localhost";
    private int serverPort = 5555;
    
    public DialogConnexionClient(JFrame parent) {
        super(parent, "Connexion au Serveur", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // IP du serveur
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipLabel = new JLabel("IP du serveur :");
        JTextField ipField = new JTextField("localhost", 15);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        panel.add(ipPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Port du serveur
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port serveur :");
        JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(5555, 1024, 65535, 1));
        portPanel.add(portLabel);
        portPanel.add(portSpinner);
        panel.add(portPanel);
        panel.add(Box.createVerticalStrut(30));
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnConnect = new JButton("Se Connecter");
        btnConnect.addActionListener(e -> {
            serverIP = ipField.getText();
            serverPort = (Integer) portSpinner.getValue();
            connecterAuServeur();
        });
        
        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnConnect);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel);
        
        add(panel);
    }
    
    private void connecterAuServeur() {
        try {
            client = new GameClient(serverIP, serverPort);
            
            client.setClientListener(new GameClient.ClientListener() {
                @Override
                public void onConnected() {
                    System.out.println("[CLIENT] Connecté au serveur");
                }
                
                @Override
                public void onConnectionFailed(Exception e) {
                    JOptionPane.showMessageDialog(DialogConnexionClient.this,
                        "Connexion échouée : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                
                @Override
                public void onMessageReceived(Message message) {
                    // J2 attend le GAME_START avec la ConfigPartie
                    if (message.getType() == Message.Type.GAME_START && 
                        message.getData() instanceof ConfigPartie) {
                        
                        ConfigPartie config = (ConfigPartie) message.getData();
                        System.out.println("[CLIENT] Réception config: nbPiece=" + 
                            config.getNbPiece() + ", nbVie=" + config.getNbVie());
                        
                        SwingUtilities.invokeLater(() -> {
                            // Lancer MaFenetre en mode client avec config
                            MaFenetre fen = new MaFenetre();
                            fen.setModeClientAvecConfig(client, config);
                            
                            // Forcer le layout avant d'afficher
                            fen.validate();
                            fen.pack();
                            fen.setSize(950, 1080);  // Moitié du width pour centrer le plateau
                            fen.setLocationRelativeTo(null);  // Recentrer
                            
                            fen.setVisible(true);
                            
                            dispose();  // Fermer le dialog
                        });
                    }
                }
                
                @Override
                public void onDisconnected() {
                    System.out.println("[CLIENT] Déconnecté du serveur");
                }
                
                @Override
                public void onError(Exception e) {
                    JOptionPane.showMessageDialog(DialogConnexionClient.this,
                        "Erreur : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            client.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la connexion : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
