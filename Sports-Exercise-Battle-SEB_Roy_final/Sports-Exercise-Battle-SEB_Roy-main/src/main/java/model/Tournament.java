package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dao.TournamentDAO;
import java.time.LocalDateTime;
import java.util.Map;

public class Tournament {
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String status; // "active" oder "ended"
    private Map<String, Integer> participants; // username -> Gesamtanzahl Liegestütze

    // Entfernen Sie die TournamentDAO-Referenz - das gehört in den Service Layer
    public Tournament() {
    }

    public Tournament(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "active";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Integer> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", participants=" + participants +
                '}';
    }
}