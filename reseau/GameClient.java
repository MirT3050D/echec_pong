package reseau;

import java.io.IOException;
import java.net.Socket;

/**
 * Client de jeu - se connecte au serveur
 */
public class GameClient {
    private String serverIP;
    private int serverPort;
    private ConnectionHandler connection;
    private ClientListener listener;
    private int playerNumber = -1;  // 0 ou 1, défini par le serveur
    
    public interface ClientListener {
        void onConnected();
        void onConnectionFailed(Exception e);
        void onMessageReceived(Message message);
        void onDisconnected();
        void onError(Exception e);
    }
    
    public GameClient(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    
    /**
     * Se connecte au serveur
     */
    public void connect() {
        new Thread(() -> {
            try {
                Socket socket = new Socket(serverIP, serverPort);
                System.out.println("[CLIENT] Connecté au serveur " + serverIP + ":" + serverPort);
                
                connection = new ConnectionHandler(socket);
                connection.setMessageListener(new ConnectionHandler.MessageListener() {
                    @Override
                    public void onMessageReceived(Message message) {
                        if (listener != null) {
                            listener.onMessageReceived(message);
                        }
                    }
                    
                    @Override
                    public void onDisconnected() {
                        if (listener != null) {
                            listener.onDisconnected();
                        }
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }
                });
                
                if (listener != null) {
                    listener.onConnected();
                }
            } catch (IOException e) {
                System.err.println("[CLIENT] Erreur de connexion : " + e.getMessage());
                if (listener != null) {
                    listener.onConnectionFailed(e);
                }
            }
        }).start();
    }
    
    /**
     * Envoie un message au serveur
     */
    public void sendMessage(Message message) throws IOException {
        if (connection == null || !connection.isConnecte()) {
            throw new IOException("Non connecté au serveur");
        }
        connection.sendMessage(message);
    }
    
    /**
     * Ferme la connexion
     */
    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }
    
    public void setClientListener(ClientListener listener) {
        this.listener = listener;
    }
    
    public boolean isConnected() {
        return connection != null && connection.isConnecte();
    }
    
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }
    
    public int getPlayerNumber() {
        return playerNumber;
    }
}
