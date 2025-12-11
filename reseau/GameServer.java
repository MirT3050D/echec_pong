package reseau;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Serveur de jeu - gère les connexions et la synchronisation
 */
public class GameServer {
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private List<ConnectionHandler> clients = new ArrayList<>();
    private Thread acceptThread;
    private ServerListener listener;
    private ConfigPartie config;
    
    public interface ServerListener {
        void onClientConnected(int clientNumber);
        void onClientDisconnected(int clientNumber);
        void onMessageReceived(int clientNumber, Message message);
        void onServerError(Exception e);
    }
    
    public GameServer(int port) {
        this.port = port;
    }
    
    /**
     * Démarre le serveur
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("[SERVEUR] Démarré sur le port " + port);
        
        // Thread d'acceptation des connexions
        acceptThread = new Thread(() -> {
            int clientCount = 0;
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[SERVEUR] Nouveau client connecté : " + clientSocket.getInetAddress());
                    
                    ConnectionHandler handler = new ConnectionHandler(clientSocket);
                    final int clientNumber = clientCount++;
                    
                    handler.setMessageListener(new ConnectionHandler.MessageListener() {
                        @Override
                        public void onMessageReceived(Message message) {
                            if (listener != null) {
                                listener.onMessageReceived(clientNumber, message);
                            }
                        }
                        
                        @Override
                        public void onDisconnected() {
                            clients.remove(handler);
                            if (listener != null) {
                                listener.onClientDisconnected(clientNumber);
                            }
                        }
                        
                        @Override
                        public void onError(Exception e) {
                            if (listener != null) {
                                listener.onServerError(e);
                            }
                        }
                    });
                    
                    clients.add(handler);
                    if (listener != null) {
                        listener.onClientConnected(clientNumber);
                    }
                    
                    // Maximum 2 clients
                    if (clients.size() >= 2) {
                        // Envoyer GAME_START aux deux clients
                        broadcastMessage(new Message(Message.Type.GAME_START));
                    }
                } catch (IOException e) {
                    if (running && listener != null) {
                        listener.onServerError(e);
                    }
                }
            }
        });
        acceptThread.setName("ServerAccept");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }
    
    /**
     * Arrête le serveur
     */
    public void stop() {
        running = false;
        for (ConnectionHandler client : clients) {
            client.disconnect();
        }
        clients.clear();
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
        System.out.println("[SERVEUR] Arrêté");
    }
    
    /**
     * Envoie un message à un client spécifique
     */
    public synchronized void sendToClient(int clientIndex, Message message) throws IOException {
        if (clientIndex >= 0 && clientIndex < clients.size()) {
            clients.get(clientIndex).sendMessage(message);
        }
    }
    
    /**
     * Envoie un message à tous les clients
     */
    public synchronized void broadcastMessage(Message message) {
        for (ConnectionHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                System.err.println("Erreur envoi broadcast : " + e.getMessage());
            }
        }
    }
    
    /**
     * Envoie un message à tous les clients SAUF un
     */
    public synchronized void broadcastMessageExcept(int excludeIndex, Message message) {
        for (int i = 0; i < clients.size(); i++) {
            if (i != excludeIndex) {
                try {
                    clients.get(i).sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Erreur envoi broadcast : " + e.getMessage());
                }
            }
        }
    }
    
    public void setServerListener(ServerListener listener) {
        this.listener = listener;
    }
    
    public int getClientCount() { return clients.size(); }
    public boolean isRunning() { return running; }
    public void setConfig(ConfigPartie config) { this.config = config; }
    public ConfigPartie getConfig() { return config; }
}
