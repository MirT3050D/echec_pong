package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MaFenetre extends JFrame {
    private InitialisationNbPiece panelNbPiece;
    private InitialisationViePiece panelViePiece;
    private JButton validerNbPieceBtn;
    private JButton validerVieBtn;

    public MaFenetre() {
        setTitle("Initialisation de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelNbPiece = new InitialisationNbPiece();
        validerNbPieceBtn = new JButton("Valider");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(panelNbPiece);
        topPanel.add(validerNbPieceBtn);
        add(topPanel, BorderLayout.NORTH);

        // Ajout du listener séparé
        validerNbPieceBtn.addActionListener(new NbPieceListener(this));
    }

    // Getters et setters pour accès depuis le listener
    public InitialisationNbPiece getPanelNbPiece() { return panelNbPiece; }
    public InitialisationViePiece getPanelViePiece() { return panelViePiece; }
    public JButton getValiderNbPieceBtn() { return validerNbPieceBtn; }
    public JButton getValiderVieBtn() { return validerVieBtn; }
    public void setPanelViePiece(InitialisationViePiece panelViePiece) { this.panelViePiece = panelViePiece; }
    public void setValiderVieBtn(JButton validerVieBtn) { this.validerVieBtn = validerVieBtn; }

    // ...la classe MaFenetre ne contient plus de main...
}
