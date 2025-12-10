# Guide détaillé : Organisation de l'affichage en Java Swing (MVC)

## 1. Introduction
Ce guide explique comment structurer l'affichage d'une application Java Swing en suivant le modèle MVC (Model-View-Controller), avec des exemples concrets et des conseils pour la maintenabilité.

---

## 2. Structure MVC
- **Model** : Représente les données et la logique métier (ex : `Piece`, `Partie`, `Plateau`).
- **View** : Les composants graphiques, nommés `ObjectPanel` (ex : `PiecePanel`, `PlateauPanel`).
- **Controller** : Les listeners, nommés `ObjectListener` (ex : `PieceListener`, `NbPieceListener`).

### Exemple d'arborescence
```
/objet
    Piece.java
    Partie.
    Plateau.java
/affichage
    PiecePanel.java
    PlateauPanel.java
    NbPiecePanel.java
    PieceListener.java
    NbPieceListener.java
    MaFenetre.java
/main
    Main.java
```

---

## 3. Création d'un Panel (View)
Un panel représente une vue graphique d'un objet métier.

```java
public class PiecePanel extends JPanel {
    private Piece piece;
    public PiecePanel(Piece piece) {
        this.piece = piece;
        setLayout(new FlowLayout());
        add(new JLabel("Nom : " + piece.getNom()));
        add(new JLabel("Vie : " + piece.getVie()));
    }
    // Getters, setters, méthodes d'affichage...
}
```

- Utilise un layout adapté (`FlowLayout`, `BoxLayout`, etc.).
- Ajoute les composants graphiques nécessaires.

---

## 4. Création d'un Listener (Controller)
Un listener gère les interactions utilisateur sur la vue.

```java
public class PieceListener implements MouseListener {
    private PiecePanel panel;
    public PieceListener(PiecePanel panel) {
        this.panel = panel;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        // Action sur clic
    }
    // Autres méthodes MouseListener...
}
```

- Le listener reçoit une référence au panel pour agir sur la vue ou le modèle.
- Implémente l'interface adaptée (`MouseListener`, `ActionListener`, etc.).

---

## 5. Connexion Listener <-> Panel

```java
PiecePanel panel = new PiecePanel(piece);
panel.addMouseListener(new PieceListener(panel));
```

- Ajoute le listener au panel dans le constructeur ou dans la fenêtre principale.

---

## 6. Fenêtre principale et navigation
La fenêtre principale (`MaFenetre`) orchestre l'affichage des panels et la navigation entre eux.

```java
public class MaFenetre extends JFrame {
    public MaFenetre() {
        setTitle("Mon Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        PiecePanel piecePanel = new PiecePanel(new Piece(...));
        piecePanel.addMouseListener(new PieceListener(piecePanel));
        add(piecePanel, BorderLayout.CENTER);
    }
}
```

- Utilise un layout principal (`BorderLayout`, `CardLayout`, etc.).
- Ajoute les panels selon la logique métier ou la navigation souhaitée.

---

## 7. Imbrication et navigation entre panels
Pour afficher plusieurs panels ou naviguer entre eux :

```java
JPanel mainPanel = new JPanel(new CardLayout());
mainPanel.add(new PiecePanel(...), "piece");
mainPanel.add(new PlateauPanel(...), "plateau");
CardLayout cl = (CardLayout) mainPanel.getLayout();
cl.show(mainPanel, "piece"); // pour afficher le panel voulu
```

- Utilise `CardLayout` pour gérer plusieurs vues dans la même fenêtre.
- Change de panel selon les actions utilisateur (bouton, menu, etc.).

---

## 8. Organisation du Main

```java
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MaFenetre fen = new MaFenetre();
            fen.setVisible(true);
        });
    }
}
```

- Lance l'interface graphique dans le thread Swing.
- Instancie la fenêtre principale.

---

## 9. Bonnes pratiques
- Séparer strictement les responsabilités (MVC).
- Utiliser des noms clairs : `ObjectPanel`, `ObjectListener`.
- Centraliser la logique métier dans les listeners.
- Utiliser des layouts adaptés (`BoxLayout`, `GridBagLayout`, `CardLayout`).
- Documenter chaque classe et méthode.
- Prévoir l'extension et la réutilisation des panels/listeners.

---

## 10. Exemple d'intégration imbriquée

```java
public class PlateauPanel extends JPanel {
    public PlateauPanel(Plateau plateau) {
        setLayout(new BorderLayout());
        add(new JLabel("Plateau d'échec"), BorderLayout.NORTH);
        // Ajout dynamique de PiecePanel pour chaque pièce
        JPanel piecesPanel = new JPanel(new FlowLayout());
        for (Piece p : plateau.getPieces()[0]) {
            PiecePanel pp = new PiecePanel(p);
            pp.addMouseListener(new PieceListener(pp));
            piecesPanel.add(pp);
        }
        add(piecesPanel, BorderLayout.CENTER);
    }
}
```

---

## 11. Diagramme UML simplifié

Voir le fichier `Affichage_MVC_UML.png` pour la structure visuelle.

---

## 12. Pour aller plus loin
- Utiliser des interfaces pour les listeners et panels.
- Factoriser les panels communs.
- Utiliser des classes utilitaires pour la navigation entre panels.
- Gérer la persistance et le replay via des classes dédiées (ex : `Save`).

---

**Ce guide est adaptable à tout projet Java Swing structuré en MVC.**
