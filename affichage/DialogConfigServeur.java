package affichage;

import reseau.*;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog pour configurer et démarrer le serveur (Joueur 1)
 */
public class DialogConfigServeur extends JDialog {
    private GameServer server;
    private int port = 5555;
    
    public DialogConfigServeur(JFrame parent) {
        super(parent, "Configuration Serveur", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Port
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port serveur :");
        JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(5555, 1024, 65535, 1));
        portPanel.add(portLabel);
        portPanel.add(portSpinner);
        panel.add(portPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnStart = new JButton("Démarrer Serveur");
        btnStart.addActionListener(e -> {
            port = (Integer) portSpinner.getValue();
            demarrerServeur();
        });
        
        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnStart);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel);
        
        add(panel);
    }
    
    private void demarrerServeur() {
        try {
            server = new GameServer(port);
            
            server.setServerListener(new GameServer.ServerListener() {
                @Override
                public void onClientConnected(int clientNumber) {
                    System.out.println("[SERVEUR] Client " + clientNumber + " connecté");
                }
                
                @Override
                public void onClientDisconnected(int clientNumber) {
                    System.out.println("[SERVEUR] Client " + clientNumber + " déconnecté");
                }
                
                @Override
                public void onMessageReceived(int clientNumber, Message message) {
                    System.out.println("[SERVEUR] Message du client " + clientNumber + " : " + message.getType());
                }
                
                @Override
                public void onServerError(Exception e) {
                    JOptionPane.showMessageDialog(DialogConfigServeur.this, 
                        "Erreur serveur : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            server.start();
            
            JOptionPane.showMessageDialog(this,
                "Serveur démarré sur le port " + port,
                "Serveur lancé", JOptionPane.INFORMATION_MESSAGE);
            
            // Lancer MaFenetre en mode serveur
            // J1 suit le flux normal d'initialisation avec ses vrais panels
            MaFenetre fen = new MaFenetre();
            fen.setModeServeur(server);
            fen.setVisible(true);
            
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du démarrage du serveur : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
