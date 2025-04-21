package dao;

import model.HistoryEntry;
import db.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    public final DatabaseManager dbManager;

    public HistoryDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean addHistoryEntry(HistoryEntry entry) {
        String sql = "INSERT INTO history(username, exercise_name, count, duration_in_seconds) VALUES(?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getUsername());
            pstmt.setString(2, entry.getExerciseName());
            pstmt.setInt(3, entry.getCount());
            pstmt.setInt(4, entry.getDurationInSeconds());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<HistoryEntry> getHistoryForUser(String username) {
        List<HistoryEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE username = ? ORDER BY timestamp DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HistoryEntry entry = new HistoryEntry();
                entry.setId(rs.getInt("id"));
                entry.setUsername(rs.getString("username"));
                entry.setExerciseName(rs.getString("exercise_name"));
                entry.setCount(rs.getInt("count"));
                entry.setDurationInSeconds(rs.getInt("duration_in_seconds"));
                entry.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public int getTotalPushupsForUser(String username) {
        String sql = "SELECT SUM(count) as total FROM history WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}