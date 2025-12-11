# Guide d'Implémentation - Synchronisation des Messages

## État Actuel

✅ Infrastructure réseau complète  
✅ GameServer/GameClient fonctionnels  
✅ MenuPrincipal + Dialogs  
✅ MaFenetre.setModeServeur() et setModeClient()  
⏳ **À faire** : Envoi/réception des messages de synchronisation

---

## 1️⃣ Synchronisation de la Balle

### 1.1 Envoyer depuis BalleListener

**Fichier** : `affichage/BalleListener.java` (méthode `actionPerformed()`)

**Objectif** : Après chaque mise à jour de balle, envoyer `BALL_UPDATE`

```java
// À la fin de actionPerformed(), après mise à jour de la position/angle
if (modeServeur && gameServer != null) {
    // Mode serveur: envoyer à tous les clients
    reseau.BalleState state = new reseau.BalleState(
        balle.getX(), 
        balle.getY(), 
        balle.getAngle(), 
        balle.getVitesse(), 
        balle.isEnMouvement()
    );
    reseau.Message msg = new reseau.Message(
        reseau.Message.Type.BALL_UPDATE, 
        state
    );
    gameServer.broadcastMessage(msg);  // Envoyer à tous
}
```

**Fréquence** : À chaque `actionPerformed()` (environ 10ms)

---

### 1.2 Recevoir dans PlateauPanel

**Fichier** : `affichage/PlateauPanel.java` (créer nouvelle méthode)

**Objectif** : Appliquer les mises à jour de balle reçues du serveur

```java
/**
 * Met à jour la position de la balle depuis le serveur
 */
public void updateBallFromNetwork(reseau.BalleState state) {
    if (balle != null) {
        balle.setX(state.getX());
        balle.setY(state.getY());
        balle.setAngle(state.getAngle());
        // Ne pas changer la vitesse, déjà synchronisée au démarrage
    }
    repaint();
}
```

**Appelée depuis** : `MaFenetre.handleServerMessage()` ou `MaFenetre.handleClientMessage()`

---

## 2️⃣ Synchronisation des Raquettes

### 2.1 Envoyer depuis RaquettePanel

**Fichier** : `affichage/RaquettePanel.java` (ajouter logique)

**Objectif** : Envoyer position + angle quand le joueur déplace sa raquette

```java
// Dans la méthode mouseMoved() ou après chaque changement
if (modeServeur && gameServer != null) {
    reseau.RaquetteState state = new reseau.RaquetteState(
        0,  // Joueur 1 (serveur)
        raquette.getX(),
        raquette.getAngle(),
        raquette.getLargeur()
    );
    reseau.Message msg = new reseau.Message(
        reseau.Message.Type.RAQUETTE_POSITION, 
        state
    );
    gameServer.broadcastMessageExcept(0, msg);  // Envoyer à J2 seulement
} else if (modeClient && gameClient != null) {
    reseau.RaquetteState state = new reseau.RaquetteState(
        1,  // Joueur 2 (client)
        raquette.getX(),
        raquette.getAngle(),
        raquette.getLargeur()
    );
    reseau.Message msg = new reseau.Message(
        reseau.Message.Type.RAQUETTE_POSITION, 
        state
    );
    gameClient.sendMessage(msg);  // Envoyer au serveur
}
```

---

### 2.2 Recevoir dans PlateauPanel

**Fichier** : `affichage/PlateauPanel.java`

```java
/**
 * Met à jour la raquette de l'adversaire depuis le réseau
 */
public void updateRaquetteFromNetwork(reseau.RaquetteState state) {
    // state.joueur = 0 ou 1
    if (raquettes != null && state.getJoueur() != joueurLocal) {
        Raquette r = raquettes[state.getJoueur()];
        if (r != null) {
            r.setX(state.getXPixel());
            r.setAngle(state.getAngle());
            // Largeur normalement ne change pas
        }
    }
    repaint();
}
```

---

## 3️⃣ Synchronisation des Collisions (Pièces)

### 3.1 Envoyer depuis BalleListener

**Fichier** : `affichage/BalleListener.java`

**Objectif** : Quand la balle touche une pièce, envoyer `PIECE_HIT`

```java
// Dans actionPerformed(), quand une collision est détectée
if (indexPieceTouch != -1) {
    // Pièce touchée
    if (modeServeur && gameServer != null) {
        reseau.PieceState state = new reseau.PieceState(
            joueurWithPiece,  // Quel joueur a la pièce
            indexPieceTouch,  // Index dans le tableau
            pieces[joueurWithPiece][indexPieceTouch].getVie() - 1
        );
        reseau.Message msg = new reseau.Message(
            reseau.Message.Type.PIECE_HIT, 
            state
        );
        gameServer.broadcastMessage(msg);
    }
}
```

---

### 3.2 Recevoir dans PlateauPanel

**Fichier** : `affichage/PlateauPanel.java`

```java
/**
 * Met à jour les pièces après collision
 */
public void updatePieceFromNetwork(reseau.PieceState state) {
    if (pieces != null && pieces[state.getJoueur()] != null) {
        Piece p = pieces[state.getJoueur()][state.getIndexPiece()];
        if (p != null) {
            p.setVie(state.getVie());
            if (state.getVie() <= 0) {
                p.setEstMort(true);
            }
        }
    }
    repaint();
}
```

---

## 4️⃣ Synchronisation de la Fin de Partie

### 4.1 Envoyer depuis BalleListener

**Fichier** : `affichage/BalleListener.java`

```java
// Après vérification de victoire
if (plateau.getGagnant() != -1) {
    if (modeServeur && gameServer != null) {
        reseau.Message msg = new reseau.Message(
            reseau.Message.Type.GAME_OVER,
            plateau.getGagnant()  // 0 ou 1
        );
        gameServer.broadcastMessage(msg);
    }
    afficherGagnant();
}
```

---

### 4.2 Recevoir dans MaFenetre

**Fichier** : `affichage/MaFenetre.java` (mettre à jour `handleClientMessage()`)

```java
case GAME_OVER:
    if (message.getData() instanceof Integer) {
        int gagnantId = (Integer) message.getData();
        SwingUtilities.invokeLater(() -> {
            // Arrêter les listeners
            if (balleListener != null) balleListener.stop();
            
            // Afficher le gagnant
            String msg = (gagnantId == 0) ? 
                "Joueur 1 (Vous) a gagné!" : 
                "Joueur 2 (Adversaire) a gagné!";
            JOptionPane.showMessageDialog(
                this, 
                msg, 
                "Fin de Partie", 
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    break;
```

---

## 5️⃣ Intégration dans MaFenetre

### 5.1 Mettre à jour handleServerMessage()

```java
private void handleServerMessage(int clientNumber, reseau.Message message) {
    switch (message.getType()) {
        case BALL_UPDATE:
            if (plateauPanel != null && message.getData() instanceof reseau.BalleState) {
                plateauPanel.updateBallFromNetwork((reseau.BalleState) message.getData());
            }
            break;
        case RAQUETTE_POSITION:
            if (plateauPanel != null && message.getData() instanceof reseau.RaquetteState) {
                plateauPanel.updateRaquetteFromNetwork((reseau.RaquetteState) message.getData());
            }
            break;
        case PIECE_HIT:
            if (plateauPanel != null && message.getData() instanceof reseau.PieceState) {
                plateauPanel.updatePieceFromNetwork((reseau.PieceState) message.getData());
            }
            break;
        case GAME_OVER:
            // Voir section 4.2 ci-dessus
            break;
    }
}
```

### 5.2 Mettre à jour handleClientMessage()

```java
private void handleClientMessage(reseau.Message message) {
    switch (message.getType()) {
        case BALL_UPDATE:
            if (plateauPanel != null && message.getData() instanceof reseau.BalleState) {
                plateauPanel.updateBallFromNetwork((reseau.BalleState) message.getData());
            }
            break;
        case RAQUETTE_POSITION:
            if (plateauPanel != null && message.getData() instanceof reseau.RaquetteState) {
                plateauPanel.updateRaquetteFromNetwork((reseau.RaquetteState) message.getData());
            }
            break;
        case PIECE_HIT:
            if (plateauPanel != null && message.getData() instanceof reseau.PieceState) {
                plateauPanel.updatePieceFromNetwork((reseau.PieceState) message.getData());
            }
            break;
        case GAME_OVER:
            // Voir section 4.2 ci-dessus
            break;
    }
}
```

---

## 6️⃣ Ordre d'Implémentation Recommandé

1. **Balle** (impact visuel maximal)
   - [ ] Envoyer de BalleListener
   - [ ] Recevoir dans PlateauPanel
   - [ ] Tester avec compilation

2. **Raquettes** (gameplay direct)
   - [ ] Envoyer de RaquettePanel
   - [ ] Recevoir dans PlateauPanel
   - [ ] Tester visuel (voir raquette adverse)

3. **Pièces** (collisions)
   - [ ] Détecter dans BalleListener
   - [ ] Envoyer PIECE_HIT
   - [ ] Recevoir et appliquer
   - [ ] Tester destruction de pièces

4. **Fin de Partie** (completion)
   - [ ] Envoyer GAME_OVER
   - [ ] Recevoir dans MaFenetre
   - [ ] Afficher gagnant
   - [ ] Arrêter listeners

---

## 7️⃣ Variables à Ajouter à MaFenetre

```java
private reseau.GameServer gameServer;
private reseau.GameClient gameClient;
private boolean modeServeur = false;
private boolean modeClient = false;
private affichage.PlateauPanel plateauPanel;  // Référence pour maj
```

---

## Checklist d'Implémentation

- [ ] BalleListener.actionPerformed() envoie BALL_UPDATE
- [ ] PlateauPanel.updateBallFromNetwork() applique les mises à jour
- [ ] RaquettePanel.mouseMoved() envoie RAQUETTE_POSITION
- [ ] PlateauPanel.updateRaquetteFromNetwork() applique les mises à jour
- [ ] BalleListener détecte les collisions avec PIECE_HIT
- [ ] PlateauPanel.updatePieceFromNetwork() applique vie réduite
- [ ] BalleListener envoie GAME_OVER
- [ ] MaFenetre.handleServerMessage/ClientMessage() routent les messages
- [ ] Compilation sans erreurs
- [ ] Test avec deux instances en réseau

---

**Source d'inspiration** : Les méthodes `setModeServeur()` et `setModeClient()` dans `MaFenetre` montrent déjà comment récupérer les références nécessaires !
