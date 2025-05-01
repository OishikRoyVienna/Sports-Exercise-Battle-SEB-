package unittest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.User;
import model.HistoryEntry;
import model.Tournament;
import java.time.LocalDateTime;
import java.util.Map;

class UnitTest {

    // User Tests
    @Test
    void testUserDefaultConstructor() {
        User user = new User();
        assertNull(user.getUsername(), "Username should be null by default");
        assertNull(user.getPassword(), "Password should be null by default");
        assertEquals(100, user.getElo(), "Default ELO should be 100");
    }

    @Test
    void testUserParameterizedConstructor() {
        User user = new User("kienboec", "daniel");
        assertEquals("kienboec", user.getUsername(), "Username should match input");
        assertEquals("daniel", user.getPassword(), "Password should match input");
        assertEquals(100, user.getElo(), "ELO should default to 100");
    }

    @Test
    void testSetUserToken() {
        User user = new User();
        user.setToken("kienboec-sebToken");
        assertEquals("kienboec-sebToken", user.getToken(), "Token should be set correctly");
    }

    // HistoryEntry Tests
    @Test
    void testHistoryEntryConstructor() {
        HistoryEntry entry = new HistoryEntry("PushUps", 40, 60);
        assertEquals("PushUps", entry.getExerciseName(), "Exercise name should match");
        assertEquals(40, entry.getCount(), "Count should match input");
        assertEquals(60, entry.getDurationInSeconds(), "Duration should match input");
    }

    @Test
    void testHistoryEntryTimestampAutoSet() {
        HistoryEntry entry = new HistoryEntry("PushUps", 30, 45);
        assertNotNull(entry.getTimestamp(), "Timestamp should be auto-set");
    }

    // Tournament Tests
    @Test
    void testTournamentDefaultConstructor() {
        Tournament tournament = new Tournament();
        assertNull(tournament.getStartTime(), "Start time should be null by default");
        assertEquals("active", tournament.getStatus(), "Default status should be active");
    }

    @Test
    void testTournamentParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Tournament tournament = new Tournament(now, now.plusMinutes(2));
        assertEquals(now, tournament.getStartTime(), "Start time should match");
        assertEquals("active", tournament.getStatus(), "Status should be active");
    }

    @Test
    void testTournamentEndTimeCalculation() {
        LocalDateTime start = LocalDateTime.of(2025, 4, 25, 12, 0);
        Tournament tournament = new Tournament(start, start.plusMinutes(2));
        assertEquals(start.plusMinutes(2), tournament.getEndTime(), "End time should be 2 minutes after start");
    }

    // Edge Cases
    @Test
    void testUserWithNullToken() {
        User user = new User();
        user.setToken(null);
        assertNull(user.getToken(), "Token should allow null");
    }

    @Test
    void testHistoryEntryWithZeroDuration() {
        HistoryEntry entry = new HistoryEntry("PushUps", 50, 0);
        assertEquals(0, entry.getDurationInSeconds(), "Should allow 0 duration");
    }

    @Test
    void testTournamentWithNoParticipants() {
        Tournament tournament = new Tournament();
        tournament.setParticipants(Map.of());
        assertTrue(tournament.getParticipants().isEmpty(), "Should handle empty participants");
    }

    // Equality Tests
    @Test
    void testUserEquality() {
        User user1 = new User("kienboec", "daniel");
        User user2 = new User("kienboec", "daniel");
        assertEquals(user1.getUsername(), user2.getUsername(), "Same usernames should be equal");
    }

    @Test
    void testHistoryEntryInequality() {
        HistoryEntry entry1 = new HistoryEntry("PushUps", 40, 60);
        HistoryEntry entry2 = new HistoryEntry("SitUps", 30, 45);
        assertNotEquals(entry1.getExerciseName(), entry2.getExerciseName(), "Different exercises should not match");
    }

    // Boundary Tests
    @Test
    void testUserMaxElo() {
        User user = new User();
        user.setElo(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, user.getElo(), "Should handle max ELO value");
    }

    @Test
    void testHistoryEntryMaxCount() {
        HistoryEntry entry = new HistoryEntry("PushUps", Integer.MAX_VALUE, 60);
        assertEquals(Integer.MAX_VALUE, entry.getCount(), "Should handle max pushup count");
    }

    // toString() Tests
    @Test
    void testUserToString() {
        User user = new User("kienboec", "daniel");
        assertTrue(user.toString().contains("kienboec"), "toString should contain username");
    }

    @Test
    void testTournamentToString() {
        Tournament tournament = new Tournament();
        assertTrue(tournament.toString().contains("status='active'"), "toString should show status");
    }
}