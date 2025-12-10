package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import objet.*;
import java.util.List;

public class MaFenetreListener {
    private MaFenetre fenetre;

    public MaFenetreListener(MaFenetre fenetre) {
        this.fenetre = fenetre;
        initListeners();
    }

    private void initListeners() {
        // Le panel gère son affichage, la fenêtre orchestre les transitions
        fenetre.getValiderNbPieceBtn().addActionListener(new InitialisationNbPieceListener(fenetre));
    }
}
