package model;

public class Stats {
    private int elo;
    private int totalPushups;

    // Standardkonstruktor
    public Stats() {
    }

    // Konstruktor mit Werten
    public Stats(int elo, int totalPushups) {
        this.elo = elo;
        this.totalPushups = totalPushups;
    }

    // Getter und Setter
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

    @Override
    public String toString() {
        return "Stats{" +
                "elo=" + elo +
                ", totalPushups=" + totalPushups +
                '}';
    }
}