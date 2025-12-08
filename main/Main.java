package main;
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            affichage.MaFenetre fen = new affichage.MaFenetre();
            fen.setVisible(true);
        });
    }
}
