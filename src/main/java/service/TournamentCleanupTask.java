package service;

import java.util.concurrent.TimeUnit;

public class TournamentCleanupTask implements Runnable {
    private final TournamentService tournamentService;

    public TournamentCleanupTask(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Überprüfe alle 30 Sekunden auf beendete Turniere
                TimeUnit.SECONDS.sleep(30);
                //tournamentService.checkAndEndTournaments();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}