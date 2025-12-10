package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MaFenetre extends JFrame {
    private InitialisationNbPiecePanel panelNbPiece;
    private InitialisationViePiecePanel panelViePiece;
    private JButton validerNbPieceBtn;
    private JButton validerVieBtn;

    public MaFenetre() {
        setTitle("Initialisation de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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
            JPanel viePanel = new JPanel(new java.awt.BorderLayout());
            viePanel.add(getPanelViePiece(), java.awt.BorderLayout.CENTER);
            viePanel.add(getValiderVieBtn(), java.awt.BorderLayout.SOUTH);
            getContentPane().removeAll();
            add(viePanel, java.awt.BorderLayout.CENTER);
            revalidate();
            repaint();
            getValiderVieBtn().addActionListener(new InitialisationViePieceListner(this));
        // Fin de la méthode afficherPanelViePiece
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
