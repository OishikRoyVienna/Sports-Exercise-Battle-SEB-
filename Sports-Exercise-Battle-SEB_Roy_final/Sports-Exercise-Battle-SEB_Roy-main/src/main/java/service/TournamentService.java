package service;

import dao.TournamentDAO;
import dao.UserDAO;
import model.Tournament;
import server.Request;
import server.Response;
import http.HttpStatus;
import http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;

public class TournamentService implements server.Service {
    private final TournamentDAO tournamentDAO;
    private final UserDAO userDAO;
    private final ObjectMapper objectMapper;

    public TournamentService(TournamentDAO tournamentDAO, UserDAO userDAO) {
        this.tournamentDAO = tournamentDAO;
        this.userDAO = userDAO;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            String method = request.getMethod().name();
            String path = request.getPathname();

            if ("GET".equalsIgnoreCase(method) && "/tournament".equals(path)) {
                Tournament tournament = getActiveTournament();

                if (tournament == null) {
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "No active tournament");
                }

                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        objectMapper.writeValueAsString(tournament)
                );
            }

            return null;
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal Server Error");
        }
    }

    public Tournament getActiveTournament() {
        return tournamentDAO.getActiveTournament();
    }

    public void addToTournament(String username, int count) {
        Tournament activeTournament = getActiveTournament();

        if (activeTournament == null) {
            LocalDateTime now = LocalDateTime.now();
            activeTournament = new Tournament(now, now.plusMinutes(2));
            tournamentDAO.createTournament(activeTournament);
        }

        tournamentDAO.addOrUpdateParticipant(activeTournament.getId(), username, count);
    }

    public void checkAndEndTournaments() {
        Tournament activeTournament = getActiveTournament();
        if (activeTournament != null && LocalDateTime.now().isAfter(activeTournament.getEndTime())) {
            // Turnier beenden und ELOs anpassen
            tournamentDAO.endTournament(activeTournament.getId());
            // Hier weitere Logik für ELO-Anpassungen
        }
    }
}