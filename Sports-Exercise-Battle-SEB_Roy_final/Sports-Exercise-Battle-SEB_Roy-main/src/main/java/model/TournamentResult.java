package model;

import java.util.Map;

public class TournamentResult {
    private int tournamentId;
    private String winner;
    private Map<String, Integer> eloChanges;

    // Konstruktor
    public TournamentResult(int tournamentId, String winner, Map<String, Integer> eloChanges) {
        this.tournamentId = tournamentId;
        this.winner = winner;
        this.eloChanges = eloChanges;
    }

    // Getter
    public int getTournamentId() {
        return tournamentId;
    }

    public String getWinner() {
        return winner;
    }

    public Map<String, Integer> getEloChanges() {
        return eloChanges;
    }

    @Override
    public String toString() {
        return "TournamentResult{" +
                "tournamentId=" + tournamentId +
                ", winner='" + winner + '\'' +
                ", eloChanges=" + eloChanges +
                '}';
    }
}