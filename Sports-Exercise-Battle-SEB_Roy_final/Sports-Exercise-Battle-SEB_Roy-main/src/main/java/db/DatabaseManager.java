package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/seb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";

    public DatabaseManager() {
        initializeDatabase();
        clearDatabase();
    }

    public void clearDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE user_badges, tournament_participants, history, tournaments, users CASCADE");
            System.out.println("Database tables cleared successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Erstelle users Tabelle
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "name VARCHAR(100), " +
                    "bio TEXT, " +
                    "image TEXT, " +
                    "elo INT DEFAULT 100, " +
                    "token VARCHAR(100))");

            // Erstelle history Tabelle
            stmt.execute("CREATE TABLE IF NOT EXISTS history (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) REFERENCES users(username), " +
                    "exercise_name VARCHAR(100) NOT NULL, " +
                    "count INT NOT NULL, " +
                    "duration_in_seconds INT NOT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Erstelle tournaments Tabelle
            stmt.execute("CREATE TABLE IF NOT EXISTS tournaments (" +
                    "id SERIAL PRIMARY KEY, " +
                    "start_time TIMESTAMP NOT NULL, " +
                    "end_time TIMESTAMP NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'active')");

            // Erstelle tournament_participants Tabelle
            stmt.execute("CREATE TABLE IF NOT EXISTS tournament_participants (" +
                    "tournament_id INT REFERENCES tournaments(id), " +
                    "username VARCHAR(50) REFERENCES users(username), " +
                    "total_count INT DEFAULT 0, " +
                    "PRIMARY KEY (tournament_id, username))");

            stmt.execute("CREATE TABLE IF NOT EXISTS user_badges (" +
                    "username VARCHAR(50) REFERENCES users(username), " +
                    "badge_name VARCHAR(50), " +
                    "PRIMARY KEY (username, badge_name))");

            System.out.println("Database tables created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}