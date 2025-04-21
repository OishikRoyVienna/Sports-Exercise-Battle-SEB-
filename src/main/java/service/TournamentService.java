package service;

import dao.TournamentDAO;
import dao.UserDAO;
import model.Tournament;
import java.time.LocalDateTime;
import java.util.Map;

public class TournamentService {
    private final TournamentDAO tournamentDAO;
    private final UserDAO userDAO;

    public TournamentService(TournamentDAO tournamentDAO, UserDAO userDAO) {
        this.tournamentDAO = tournamentDAO;
        this.userDAO = userDAO;
    }

    public Tournament getActiveTournament() {
        Tournament tournament = tournamentDAO.getActiveTournament();
        if (tournament != null && LocalDateTime.now().isAfter(tournament.getEndTime())) {
            endTournament(tournament);
            return null;
        }
        return tournament;
    }

    public Tournament startOrJoinTournament(String username, int pushupCount) {
        Tournament tournament = getActiveTournament();

        if (tournament == null) {
            tournament = new Tournament();
            tournament.setStartTime(LocalDateTime.now());
            tournament.setEndTime(LocalDateTime.now().plusMinutes(2));
            tournament.setStatus("active");

            if (!tournamentDAO.createTournament(tournament)) {
                throw new RuntimeException("Failed to create tournament");
            }
        }

        tournamentDAO.addOrUpdateParticipant(tournament.getId(), username, pushupCount);
        return tournament;
    }

    private void endTournament(Tournament tournament) {
        Map<String, Integer> participants = tournamentDAO.getParticipants(tournament.getId());
        if (participants == null || participants.isEmpty()) return;

        int maxPushups = participants.values().stream().max(Integer::compare).orElse(0);
        long winners = participants.values().stream().filter(c -> c == maxPushups).count();

        participants.forEach((username, count) -> {
            int eloChange = count == maxPushups ?
                    (winners > 1 ? 1 : 2) : -1;
            userDAO.updateUserElo(username, eloChange);
        });

        tournamentDAO.endTournament(tournament.getId());
    }
}