package dao;

import db.DatabaseManager;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DatabaseManager dbManager;

    public UserDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean createUser(User chupapi) {
        String sql = "INSERT INTO users(username, password, elo) VALUES(?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chupapi.getUsername());
            pstmt.setString(2, chupapi.getPassword());
            pstmt.setInt(3, chupapi.getElo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setElo(rs.getInt("elo"));
                user.setName(rs.getString("name"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setToken(rs.getString("token"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getImage());
            pstmt.setString(4, user.getUsername());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setUserToken(String username, String token) {
        String sql = "UPDATE users SET token = ? WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByToken(String token) {
        String sql = "SELECT * FROM users WHERE token = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setElo(rs.getInt("elo"));
                user.setName(rs.getString("name"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setToken(rs.getString("token"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Neue Methoden in der UserDAO-Klasse:
    public void addBadgeToUser(String username, String badgeName) {
        String sql = "INSERT INTO user_badges(username, badge_name) VALUES(?, ?) " +
                "ON CONFLICT (username, badge_name) DO NOTHING";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, badgeName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUserBadges(String username) {
        List<String> badges = new ArrayList<>();
        String sql = "SELECT badge_name FROM user_badges WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                badges.add(rs.getString("badge_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return badges;
    }

    //getAllUsers()-Methode:
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, elo, name, bio, image, token FROM users";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setElo(rs.getInt("elo"));
                user.setName(rs.getString("name"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setToken(rs.getString("token"));

                // Badges aus der Datenbank laden
                List<String> badges = getUserBadges(user.getUsername());
                badges.forEach(user::addBadge);

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUserElo(String username, int eloChange) {
        String sql = "UPDATE users SET elo = elo + ? WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eloChange);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}