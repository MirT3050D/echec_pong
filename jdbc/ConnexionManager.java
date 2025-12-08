package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/echec_pong";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";
    private Connection connection;

    public void ouvrirConnexion() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base réussie.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
        }
    }

    public Connection getConnexion() {
        return connection;
    }

    public void fermerConnexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}
