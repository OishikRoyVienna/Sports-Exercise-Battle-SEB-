package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class HistoryEntry {
    @JsonProperty("Name")
    private String exerciseName;

    @JsonProperty("Id")
    private int id;
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Count")
    private int count;
    @JsonProperty("DurationInSeconds")
    private int durationInSeconds;
    @JsonProperty("Timestamp")
    private LocalDateTime timestamp;

    // Standardkonstruktor
    public HistoryEntry() {
    }

    // Konstruktor für neue Einträge
    public HistoryEntry(String exerciseName, int count, int durationInSeconds) {
        this.exerciseName = exerciseName;
        this.count = count;
        this.durationInSeconds = durationInSeconds;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                ", count=" + count +
                ", durationInSeconds=" + durationInSeconds +
                ", timestamp=" + timestamp +
                '}';
    }
}