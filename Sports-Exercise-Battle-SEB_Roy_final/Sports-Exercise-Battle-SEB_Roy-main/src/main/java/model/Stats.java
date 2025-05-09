package model;
import java.util.ArrayList;
import java.util.List;

public class Stats {
    private int elo;
    private int totalPushups;
    private List<String> badges = new ArrayList<>();

    public Stats() {}

    public Stats(int elo, int totalPushups) {
        this.elo = elo;
        this.totalPushups = totalPushups;
    }

    // Getter/Setter
    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getTotalPushups() {
        return totalPushups;
    }

    public void setTotalPushups(int totalPushups) {
        this.totalPushups = totalPushups;
    }

    public List<String> getBadges() {
        return badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "elo=" + elo +
                ", totalPushups=" + totalPushups +
                ", badges=" + badges +
                '}';
    }
}