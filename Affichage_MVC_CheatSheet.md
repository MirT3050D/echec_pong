# Cheat Sheet : Gestion d'affichage Java Swing en MVC

## Structure MVC recommandée

- **Model** : Représente les données et la logique métier (ex : `Piece`, `Partie`, `Plateau`)
- **View** : Les composants graphiques, nommés `ObjectPanel` (ex : `PiecePanel`, `PlateauPanel`)
- **Controller** : Les listeners, nommés `ObjectListener` (ex : `PieceListener`, `NbPieceListener`)

---

## Exemple de structure de fichiers

```
/objet
    Piece.java
    Partie.java
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

## Exemple de Panel (View)

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

---

## Exemple de Listener (Controller)

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

---

## Connexion Listener <-> Panel

```java
PiecePanel panel = new PiecePanel(piece);
panel.addMouseListener(new PieceListener(panel));
```

---

## Intégration dans la fenêtre principale

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

---

## Imbrication de pages/panels

Pour afficher plusieurs panels ou naviguer entre eux :

```java
JPanel mainPanel = new JPanel(new CardLayout());
mainPanel.add(new PiecePanel(...), "piece");
mainPanel.add(new PlateauPanel(...), "plateau");
CardLayout cl = (CardLayout) mainPanel.getLayout();
cl.show(mainPanel, "piece"); // pour afficher le panel voulu
```

---

## Exemple d'organisation du Main

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

---

## Bonnes pratiques

- Toujours séparer les responsabilités (MVC)
- Utiliser des noms clairs : `ObjectPanel`, `ObjectListener`
- Centraliser la logique métier dans les listeners
- Utiliser des layouts adaptés (`BoxLayout`, `GridBagLayout`, `CardLayout`)
- Documenter chaque classe et méthode

---

## Schéma visuel (UML simplifié)

```
+-------------------+        +-------------------+        +-------------------+
|    PiecePanel     |<------>|  PieceListener    |<------>|      Piece        |
+-------------------+        +-------------------+        +-------------------+
        ^                          ^                              ^
        |                          |                              |
+-------------------+        +-------------------+        +-------------------+
|   PlateauPanel    |<------>| PlateauListener   |<------>|     Plateau       |
+-------------------+        +-------------------+        +-------------------+
```

---

Pour aller plus loin :
- Utiliser des interfaces pour les listeners
- Factoriser les panels communs
- Utiliser des classes utilitaires pour la navigation entre panels

---

**Ce guide est adaptable à tout projet Java Swing structuré en MVC.**
