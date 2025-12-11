package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Menu de démarrage pour choisir mode local ou réseau
 */
public class MenuPrincipal extends JFrame {
    private JPanel panelMenu;
    
    public MenuPrincipal() {
        setTitle("Échec Pong - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(50, 50, 50));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Titre
        JLabel titre = new JLabel("ÉCHEC PONG");
        titre.setFont(new Font("Arial", Font.BOLD, 32));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMenu.add(titre);
        panelMenu.add(Box.createVerticalStrut(40));
        
        // Bouton Mode Local
        JButton btnLocal = new JButton("Mode Local");
        btnLocal.setFont(new Font("Arial", Font.PLAIN, 16));
        btnLocal.setMaximumSize(new Dimension(200, 50));
        btnLocal.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLocal.addActionListener(e -> lancerModeLocal());
        panelMenu.add(btnLocal);
        panelMenu.add(Box.createVerticalStrut(20));
        
        // Bouton Serveur
        JButton btnServeur = new JButton("Serveur (Joueur 1)");
        btnServeur.setFont(new Font("Arial", Font.PLAIN, 16));
        btnServeur.setMaximumSize(new Dimension(200, 50));
        btnServeur.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnServeur.addActionListener(e -> lancerModeServeur());
        panelMenu.add(btnServeur);
        panelMenu.add(Box.createVerticalStrut(20));
        
        // Bouton Client
        JButton btnClient = new JButton("Client (Joueur 2)");
        btnClient.setFont(new Font("Arial", Font.PLAIN, 16));
        btnClient.setMaximumSize(new Dimension(200, 50));
        btnClient.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClient.addActionListener(e -> lancerModeClient());
        panelMenu.add(btnClient);
        
        add(panelMenu);
    }
    
    private void lancerModeLocal() {
        // Lancer le jeu en mode local (2 joueurs sur le même écran)
        MaFenetre fen = new MaFenetre();
        fen.setVisible(true);
        dispose();
    }
    
    private void lancerModeServeur() {
        // Lancer en mode serveur (J1)
        DialogConfigServeur dialog = new DialogConfigServeur(this);
        dialog.setVisible(true);
    }
    
    private void lancerModeClient() {
        // Lancer en mode client (J2)
        DialogConnexionClient dialog = new DialogConnexionClient(this);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}
