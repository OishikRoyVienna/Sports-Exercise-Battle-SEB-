package dao;

import db.DatabaseManager;
import model.Tournament;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TournamentDAO {
    private final DatabaseManager dbManager;

    public TournamentDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Tournament getActiveTournament() {
        String sql = "SELECT * FROM tournaments WHERE status = 'active' LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Tournament t = new Tournament();
                t.setId(rs.getInt("id"));
                t.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                t.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                t.setStatus(rs.getString("status"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createTournament(Tournament tournament) {
        String sql = "INSERT INTO tournaments(start_time, end_time, status) VALUES(?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tournament.getStartTime()));
            pstmt.setTimestamp(2, Timestamp.valueOf(tournament.getEndTime()));
            pstmt.setString(3, tournament.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tournament.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOrUpdateParticipant(int tournamentId, String username, int count) {
        String sql = "INSERT INTO tournament_participants(tournament_id, username, total_count) " +
                "VALUES(?, ?, ?) ON CONFLICT (tournament_id, username) DO UPDATE SET " +
                "total_count = tournament_participants.total_count + EXCLUDED.total_count";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tournamentId);
            pstmt.setString(2, username);
            pstmt.setInt(3, count);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getParticipants(int tournamentId) {
        Map<String, Integer> participants = new HashMap<>();
        String sql = "SELECT username, total_count FROM tournament_participants WHERE tournament_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tournamentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                participants.put(rs.getString("username"), rs.getInt("total_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public void endTournament(int tournamentId) {
        String sql = "UPDATE tournaments SET status = 'ended' WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tournamentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}