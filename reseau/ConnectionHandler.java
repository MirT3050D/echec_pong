package reseau;

import java.io.*;
import java.net.Socket;

/**
 * Gestionnaire de communication bidirectionnelle avec un client/serveur
 */
public class ConnectionHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connecte = false;
    private Thread lectureThread;
    private MessageListener listener;
    
    public interface MessageListener {
        void onMessageReceived(Message message);
        void onDisconnected();
        void onError(Exception e);
    }
    
    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        // Désactiver l'algorithme de Nagle pour réduire la latence
        socket.setTcpNoDelay(true);
        // Important : créer l'OutputStream AVANT l'InputStream pour éviter deadlock
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.connecte = true;
        startReader();
    }
    
    /**
     * Démarre le thread de lecture des messages
     */
    private void startReader() {
        lectureThread = new Thread(() -> {
            try {
                while (connecte) {
                    Message message = (Message) in.readObject();
                    if (message != null && listener != null) {
                        listener.onMessageReceived(message);
                    }
                }
            } catch (EOFException e) {
                // Fin de connexion normale
                disconnect();
            } catch (IOException | ClassNotFoundException e) {
                if (connecte && listener != null) {
                    listener.onError(e);
                }
                disconnect();
            }
        });
        lectureThread.setName("ConnectionReader");
        lectureThread.setDaemon(true);
        lectureThread.start();
    }
    
    /**
     * Envoie un message
     */
    public synchronized void sendMessage(Message message) throws IOException {
        if (!connecte) {
            throw new IOException("Connexion fermée");
        }
        out.writeObject(message);
        out.flush();
    }
    
    /**
     * Ferme la connexion
     */
    public void disconnect() {
        connecte = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture : " + e.getMessage());
        }
        if (listener != null) {
            listener.onDisconnected();
        }
    }
    
    /**
     * Définit le listener pour les messages reçus
     */
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }
    
    public boolean isConnecte() { return connecte; }
}
