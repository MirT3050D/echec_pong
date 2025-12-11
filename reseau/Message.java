package reseau;

import java.io.Serializable;

/**
 * Classe de message sérialisée pour la communication réseau
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        // Initialisation
        INIT_CONFIG,           // J1 envoie config (nbPiece, nbVie)
        INIT_ACK,              // Serveur confirme
        PLAYER_READY,          // Joueur prêt
        GAME_START,            // Jeu démarre
        
        // Gameplay
        BALL_UPDATE,           // Position + angle balle
        RAQUETTE_POSITION,     // Position + angle raquette
        PIECE_HIT,             // Pièce touchée (position + nouvellVie)
        RAQUETTE_MOVE,         // Mouvement raquette (joueur)
        
        // Fin de partie
        GAME_OVER,             // Roi mort (joueur gagnant)
        
        // Contrôle
        PING,                  // Ping pour vérifier connexion
        DISCONNECT             // Déconnexion propre
    }
    
    private Type type;
    private Object data;
    private long timestamp;
    
    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(Type type) {
        this(type, null);
    }
    
    // Getters
    public Type getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return "Message{" + type + ", data=" + data + "}";
    }
}
