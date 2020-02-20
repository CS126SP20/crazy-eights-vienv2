package student.crazyeights;

import java.util.*;

import static student.crazyeights.ConsoleUI.*;

public class Tournament {
    private final ConsoleUI ui;
    private final Random random;
    private ArrayList<Player> players;
    private Map<Player, Integer> tournamentScore = new HashMap<>();

    public Tournament(ConsoleUI consoleUI, Random random) {
        this.ui = consoleUI;
        this.random = random;
        this.players = new ArrayList<>();
    }

    /**
     * Starts the tournament.
     */
    public void startTournament() {

        ui.consolePrint(NEW_TOURNAMENT_ANNOUNCEMENT);
        ui.consolePrint(NUMBER_OF_PLAYER_REQUEST);

        int numOfPlayers = Integer.parseInt(ui.getUserInput());
        // There can only be 2-7 players
        while (numOfPlayers > 7 || numOfPlayers < 2) {
            ui.consolePrint(NUMBER_OF_PLAYER_FAIL);
            ui.consolePrint(NUMBER_OF_PLAYER_FAIL_REQUEST);
            numOfPlayers = Integer.parseInt(ui.getUserInput());
        }

        Player newPlayer;

        for (int i = 0; i < numOfPlayers; i++) {
            // Create player and asks for their name
            newPlayer = createPlayer();
            // Creates an empty hand for the player.
            newPlayer.createEmptyHand();
            // Add the new player to the list.
            players.add(newPlayer);
            // Set their score to 0.
            tournamentScore.put(newPlayer, 0);
        }

        for (Player p : players) {
            p.pingAiInit(players);
        }

        do {
            Game game = new Game(ui, random, players);
            game.playGame();
            // Updates the total score after every game
            tournamentScore = game.updateScore(tournamentScore);
            // Announce the tournament score after every game.
            ui.announceScoreOrWinner(tournamentScore, TOURNAMENT_SCORE_ID);
            for (Player p : players) {
                p.reset();
            }
        } while (!tournamentIsOver());

        // Announce the winner at the end of the tournament.
        ui.announceScoreOrWinner(tournamentScore, TOURNAMENT_WINNER_ID);

    }

    /**
     * Adds a player into the game.
     */
    private Player createPlayer() {
        Player newPlayer = new Player(ui, random);
        String playerName = promptUserForName(newPlayer);
        newPlayer.setName(playerName);
        return newPlayer;
    }

    /**
     * Prompts user for their player's name.
     * @return The player's name
     */
    private String promptUserForName(Player newPlayer) {
        String name;
        do {
            ui.consolePrint(PLAYER_NAME_REQUEST);
            newPlayer.pingAiForName();
            name = ui.getUserInput();
        } while (name.equals(""));
        return name;
    }

    /**
     * Checks whether the tournament should be ended.
     * @return True if the current maximum score is over the threshold.
     */
    private boolean tournamentIsOver() {
        int scoreThresholdToEndGame;
        switch (players.size()) {
            case 2:
                scoreThresholdToEndGame = 100;
                break;
            case 3:
                scoreThresholdToEndGame = 150;
                break;
            case 4:
                scoreThresholdToEndGame = 200;
                break;
            case 5:
                scoreThresholdToEndGame = 250;
                break;
            case 6:
                scoreThresholdToEndGame = 300;
                break;
            case 7:
                scoreThresholdToEndGame = 350;
                break;
            default:
                scoreThresholdToEndGame = 0;
        }
        return getCurrentMaxScore() > scoreThresholdToEndGame;
    }

    /**
     * Gets the current maximum score.
     * @return The current maximum score.
     */
    private int getCurrentMaxScore() {
        int currentMaxScore = 0;
        int playerScore;
        for (Map.Entry<Player, Integer> entry : tournamentScore.entrySet()) {
            playerScore = entry.getValue();
            if (playerScore > currentMaxScore) {
                currentMaxScore = playerScore;
            }
        }
        return currentMaxScore;
    }

}
