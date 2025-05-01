package service;

import dao.HistoryDAO;
import dao.UserDAO;
import model.Stats;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

public class StatsService {
    private final UserDAO userDAO;
    private final HistoryDAO historyDAO;

    public StatsService(UserDAO userDAO, HistoryDAO historyDAO) {
        this.userDAO = userDAO;
        this.historyDAO = historyDAO;
    }

    public Stats getUserStats(String username) {
        Stats stats = new Stats();
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            int totalPushups = historyDAO.getTotalPushupsForUser(username);

            //Update badges based on numbers
            updateBadges(user, totalPushups);

            stats.setElo(user.getElo());
            stats.setTotalPushups(totalPushups);
            stats.setBadges(user.getBadges());
        }
        return stats;
    }

    private void updateBadges(User user, int totalPushups) {
        //Pushup achievements and logik, badges
        if (totalPushups >= 150) {
            user.addBadge("Hulk");
        }
        if (totalPushups >= 100) {
            user.addBadge("Soldier");
        }
        if (totalPushups >= 50) {
            user.addBadge("Fighter");
        }

        //ELO achievement and rank
        if (user.getElo() >= 80);
            user.addBadge("ELO ");
        if (user.getElo() >= 120) {
            user.addBadge("ELO Champ");
        if (user.getElo() >= 150)
            user.addBadge("ELO Maestro");



        }
    }

    public List<Stats> getScoreboard() {
        return userDAO.getAllUsers().stream()
                .map(user -> {
                    Stats stats = new Stats();
                    stats.setElo(user.getElo());
                    stats.setTotalPushups(historyDAO.getTotalPushupsForUser(user.getUsername()));
                    stats.setBadges(user.getBadges());
                    return stats;
                })
                .collect(Collectors.toList());
    }
}