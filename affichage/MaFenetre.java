
package affichage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import objet.Plateau;


public class MaFenetre extends JFrame {
    private InitialisationNbPiecePanel panelNbPiece;
    private InitialisationViePiecePanel panelViePiece;
    private PlateauPanel plateauPanel;
    private JButton validerNbPieceBtn;
    private JButton validerVieBtn;


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
        // setExtendedState(JFrame.MAXIMIZED_HORIZ); // Plein écran
        setSize(1900, 1080

        );
        plateauPanel = new PlateauPanel(plateau);
        getContentPane().removeAll();
        add(plateauPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
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
