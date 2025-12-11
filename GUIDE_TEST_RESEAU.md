# Guide de Test - Mode Serveur/Client

## Architecture du Test

```
Machine A (Serveur - Joueur 1)          Machine B (Client - Joueur 2)
┌─────────────────────────────────┐     ┌──────────────────────────────┐
│ MenuPrincipal                   │     │ MenuPrincipal                │
│  ↓                              │     │  ↓                           │
│ [Serveur] button                │     │ [Client] button              │
│  ↓                              │     │  ↓                           │
│ DialogConfigServeur             │     │ DialogConnexionClient        │
│  • Port: 5000                   │     │  • IP: [Machine A IP]        │
│  • nbPiece: 4                   │     │  • Port: 5000                │
│  • nbVie: 3                     │     │                              │
│  ↓                              │     │  ↓                           │
│ GameServer start()              │◄────┤ GameClient connect()         │
│  ↓                              │     │  ↓                           │
│ MaFenetre.setModeServeur()      │     │ MaFenetre.setModeClient()    │
│  • Lance le jeu                 │     │  • En attente de config      │
│  • Plateau avec J1              │     │  • Reçoit ConfigPartie       │
│  • Envoie: INIT_CONFIG          │────►│  • Crée Plateau avec J2      │
└─────────────────────────────────┘     └──────────────────────────────┘
         ▲                                        │
         │                                        │
         └────────────── Messages réseau ────────┘
```

---

## Prérequis

✅ Compilation réussie de tous les fichiers
✅ Base de données configurée et remplie
✅ Deux machines en réseau local ou deux terminals sur la même machine

---

## Étape 1 : Obtenir l'adresse IP locale

### Sous Windows (PowerShell)
```powershell
ipconfig
```
Cherchez l'adresse IPv4 de votre machine (ex: `192.168.1.100`)

### Sur les deux machines
Notez l'adresse IP de chaque machine pour les tests.

---

## Étape 2 : Tester en Mode Local d'abord

### Sur la Machine A (ou Terminal 1)
1. Lancez le JAR
2. Cliquez sur **"Mode Local"**
3. Testez le gameplay normal (collisions, pièces, victoire)
4. Vérifiez que tout fonctionne

**But** : S'assurer que la base de jeu marche sans réseau

---

## Étape 3 : Lancer le Serveur (Machine A / Joueur 1)

### Étapes
1. Lancez le JAR sur Machine A
2. Cliquez sur le bouton **"Serveur"**
3. La fenêtre **DialogConfigServeur** s'ouvre

### Configuration par défaut recommandée

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| **Port** | `5000` | Port d'écoute du serveur |
| **Nombre de Pièces** | `4` | Nombre de colonnes de pièces |
| **Vie par Pièce** | `3` | Points de vie initiaux |

### Vérification
```
✓ GameServer démarré sur port 5000
✓ [SERVEUR] Démarrage du serveur sur le port 5000
✓ MaFenetre apparaît avec "Échec Pong - Serveur (Joueur 1)"
✓ Plateau visible avec J1 en haut, pièces en bas
✓ Console affiche: "Serveur en attente du client..."
```

---

## Étape 4 : Lancer le Client (Machine B / Joueur 2)

### Étapes
1. Lancez le JAR sur Machine B
2. Cliquez sur le bouton **"Client"**
3. La fenêtre **DialogConnexionClient** s'ouvre

### Configuration

| Paramètre | Valeur | Exemple |
|-----------|--------|---------|
| **Adresse IP** | IP de Machine A | `192.168.1.100` |
| **Port** | `5000` | Doit correspondre au serveur |

### Vérification
```
✓ Connexion établie avec le serveur
✓ [CLIENT] Connexion au serveur réussie
✓ MaFenetre apparaît avec "Échec Pong - Client (Joueur 2)"
✓ Plateau visible avec J2 en bas, pièces en haut
✓ J1 reçoit: "[SERVEUR] Client 1 connecté"
```

---

## Étape 5 : Synchronisation Initiale

### Ce qui devrait se passer

1. **Le Serveur envoie la config au Client**
   - Message: `INIT_CONFIG`
   - Données: `ConfigPartie(nbPiece=4, nbVie=3)`

2. **Le Client reçoit et initialise son plateau**
   ```
   [CLIENT] Réception INIT_CONFIG
   ConfigPartie reçue: nbPiece=4, nbVie=3
   Plateau créé avec J2 comme joueur 1
   ```

3. **Vérification visuelle**
   - Les deux plateaux doivent être identiques
   - Même nombre de pièces
   - Même disposition

### Checklist de Synchronisation
```
□ Machine A: Plateau visible, J1 en haut
□ Machine B: Plateau visible, J2 en bas
□ Nombre de pièces identique (4 colonnes)
□ Positions identiques
□ Pas d'erreur dans la console
```

---

## Étape 6 : Tester la Synchronisation de la Balle

### ⚠️ État actuel
**À IMPLÉMENTER** : Les messages de synchronisation de balle ne sont pas encore envoyés par `BalleListener`

### Pour tester manuellement (debug)
1. Modifiez `BalleListener` pour envoyer les positions
2. Modifiez `PlateauPanel` pour les appliquer

### Commandes debug proposées

Dans `BalleListener.actionPerformed()`, ajoutez après chaque mise à jour:
```java
// Envoyer la position de la balle au serveur
if (modeClient && gameClient != null) {
    reseau.BalleState state = new reseau.BalleState(
        balle.getX(), balle.getY(), 
        balle.getAngle(), balle.getVitesse(), 
        true
    );
    reseau.Message msg = new reseau.Message(
        reseau.Message.Type.BALL_UPDATE, state
    );
    gameClient.sendMessage(msg);
}
```

### Vérification
```
[SYNC] Balle: x=150, y=200, angle=45°, vitesse=5
[SYNC] Balle synchronisée avec l'autre joueur
```

---

## Étape 7 : Tester la Synchronisation des Raquettes

### Comment tester
1. **Machine A** : Bougez votre raquette
2. **Machine B** : Observez la raquette de l'adversaire

### Message attendu
```
Type: RAQUETTE_POSITION
Données: RaquetteState(joueur=1, xPixel=150, angle=30, largeur=50)
```

---

## Étape 8 : Tester la Détection de Collision

### Scénario
1. J1 déplace sa raquette pour intercepter la balle
2. La collision se déclenche
3. Un message `PIECE_HIT` devrait être envoyé
4. La pièce reçoit des dégâts sur les deux machines

### Vérification
```
[COLLISION] Pièce J1[2] touchée
[SYNC] PIECE_HIT envoyé
[SYNC] J2 reçoit et met à jour vie locale
```

---

## Étape 9 : Tester la Fin de Partie

### Scénario
1. Jouez jusqu'à ce que le roi soit tué
2. La victoire s'affiche sur les deux machines
3. Le message `GAME_OVER` est envoyé

### Vérification
```
[VICTOIRE] Joueur X a gagne!
[SYNC] GAME_OVER envoyé à l'adversaire
Message affiché sur les deux écrans
```

---

## Checklist de Test Complet

### Préparation
- [ ] Compilation sans erreurs
- [ ] Deux machines en réseau (ou SSH/VirtualBox)
- [ ] Port 5000 accessible/non bloqué
- [ ] Base de données fonctionnelle

### Test Serveur
- [ ] Bouton "Serveur" fonctionne
- [ ] DialogConfigServeur apparaît
- [ ] Configuration peut être changée
- [ ] Serveur démarre sur port 5000
- [ ] MaFenetre affiche "Serveur (Joueur 1)"

### Test Client
- [ ] Bouton "Client" fonctionne
- [ ] DialogConnexionClient apparaît
- [ ] IP/Port peuvent être entrés
- [ ] Connexion établie avec serveur
- [ ] MaFenetre affiche "Client (Joueur 2)"

### Test Synchronisation
- [ ] Plateau J1 = Plateau J2
- [ ] Configuration reçue correctement
- [ ] Nombre de pièces = 4 (ou configuré)
- [ ] Positions identiques

### Test Gameplay (À implémenter)
- [ ] Balle synchronisée
- [ ] Raquettes synchronisées
- [ ] Collisions détectées
- [ ] Fin de partie synchronisée

---

## Dépannage

### "Connection refused"
```
❌ Problème: Serveur non lancé ou port fermé
✓ Solution: 
  1. Vérifiez que le serveur est lancé
  2. Vérifiez le port (default: 5000)
  3. Vérifiez le pare-feu Windows
```

### "Unknown host"
```
❌ Problème: Adresse IP incorrecte
✓ Solution:
  1. Vérifiez l'IP avec ipconfig
  2. Vérifiez la connectivité: ping 192.168.x.x
  3. Vérifiez que les deux machines sont sur le même réseau
```

### "Plateau ne s'affiche pas"
```
❌ Problème: ConfigPartie non reçue ou erreur d'initialisation
✓ Solution:
  1. Vérifiez les logs: [INIT_CONFIG]
  2. Vérifiez que nbPiece est valide (2, 4, 6, 8)
  3. Vérifiez Piece.getAll() retourne des pièces
```

### "Aucune synchronisation"
```
❌ Problème: Messages non envoyés par BalleListener/RaquetteListener
✓ Solution:
  1. Implémentez l'envoi dans BalleListener.actionPerformed()
  2. Implémentez la réception dans PlateauPanel
  3. Vérifiez que gameClient/gameServer sont initialisés
```

---

## Logs Console à Surveiller

### Serveur
```
[SERVEUR] Démarrage du serveur sur le port 5000
[SERVEUR] Client 1 connecté
[SERVEUR] Message reçu du client 1: BALL_UPDATE
[SERVEUR] Diffusion BALL_UPDATE à tous les clients
```

### Client
```
[CLIENT] Connexion au serveur réussie
[CLIENT] Réception INIT_CONFIG
[CLIENT] ConfigPartie reçue: nbPiece=4, nbVie=3
[CLIENT] Plateau créé avec J2 comme joueur 1
[CLIENT] Message reçu du serveur: BALL_UPDATE
```

---

## Étapes Suivantes (Après ce test)

1. **Implémenter l'envoi de messages** dans `BalleListener` et `RaquetteListener`
2. **Implémenter la réception** dans `PlateauPanel.handleServerMessage()`
3. **Tester la synchronisation complète** avec gameplay réel
4. **Optimiser la latence** (cible: <10ms par échange)
5. **Gérer les déconnexions** (reconnexion, timeout)

---

## Commandes Rapides

### Compiler
```powershell
cd C:\Users\Mir\Desktop\ITU\Info\Programmation\S5\echec_pong
javac -cp ".:mysql-connector-java-8.0.33.jar" **/*.java
```

### Lancer
```powershell
java -cp ".:mysql-connector-java-8.0.33.jar" affichage.MenuPrincipal
```

### Test Connectivité
```powershell
ping 192.168.1.100  # Remplacer par IP serveur
netstat -an | findstr 5000  # Vérifier port
```

---

**Bon test! 🎮**
