package service;

import dao.TournamentDAO;
import dao.UserDAO;
import model.Tournament;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

public class TournamentService {
    private static final Logger logger = Logger.getLogger(TournamentService.class.getName());
    private final TournamentDAO tournamentDAO;
    private final UserDAO userDAO;

    public TournamentService(TournamentDAO tournamentDAO, UserDAO userDAO) {
        this.tournamentDAO = tournamentDAO;
        this.userDAO = userDAO;
    }

    public Tournament getActiveTournament() {
        Tournament tournament = tournamentDAO.getActiveTournament();

        if (tournament == null) {
            logger.info("Kein aktives Turnier gefunden.");
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        logger.info("Aktuelles Turnier: ID=" + tournament.getId() +
                ", Endzeit=" + tournament.getEndTime() +
                ", Status=" + tournament.getStatus());

        // Beende Turnier, wenn abgelaufen
        if (now.isAfter(tournament.getEndTime())) {
            logger.info("Turnier " + tournament.getId() + " ist abgelaufen. Beende es...");
            endTournament(tournament);
            return null;
        }

        // Lade Teilnehmerdaten für aktive Turniere
        Map<String, Integer> participants = tournamentDAO.getParticipants(tournament.getId());
        tournament.setParticipants(participants);
        logger.info("Turnier " + tournament.getId() + " hat " + participants.size() + " Teilnehmer");

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
                logger.severe("Fehler beim Erstellen eines neuen Turniers");
                throw new RuntimeException("Failed to create tournament");
            }
            logger.info("Neues Turnier gestartet: ID=" + tournament.getId());
        }

        tournamentDAO.addOrUpdateParticipant(tournament.getId(), username, pushupCount);
        logger.info(username + " hat " + pushupCount + " Liegestütze zum Turnier " + tournament.getId() + " hinzugefügt");

        return tournament;
    }

    public void endTournament(Tournament tournament) {
        Map<String, Integer> participants = tournamentDAO.getParticipants(tournament.getId());
        if (participants == null || participants.isEmpty()) {
            logger.warning("Keine Teilnehmer im Turnier " + tournament.getId() + ". Keine ELO-Updates.");
            tournamentDAO.endTournament(tournament.getId());
            return;
        }

        int maxPushups = participants.values().stream().max(Integer::compare).orElse(0);
        long winners = participants.values().stream().filter(c -> c == maxPushups).count();

        participants.forEach((username, count) -> {
            int eloChange = count == maxPushups ?
                    (winners > 1 ? 1 : 2) : -1;
            userDAO.updateUserElo(username, eloChange);
            logger.info("ELO-Update für " + username + ": " + eloChange + " Punkte");
        });

        tournamentDAO.endTournament(tournament.getId());
        logger.info("Turnier " + tournament.getId() + " beendet");
    }
}