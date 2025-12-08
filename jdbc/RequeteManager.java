package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RequeteManager {
    private Connection connection;

    public RequeteManager(Connection connection) {
        this.connection = connection;
    }

    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Erreur d'exécution de requête : " + e.getMessage());
            return null;
        }
    }

    public int executeUpdate(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Erreur d'exécution de mise à jour : " + e.getMessage());
            return -1;
        }
    }
}
