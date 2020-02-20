package student.crazyeights;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {

    public static final String NEW_TOURNAMENT_ANNOUNCEMENT = "Welcome to a new tournament of Crazy Eights! ";
    public static final String NEW_GAME_ANNOUNCEMENT = "Welcome to a new game of Crazy Eights!";

    public static final String NUMBER_OF_PLAYER_REQUEST = "Enter total number of players: ";
    public static final String PLAYER_NAME_REQUEST = "Enter your player's name: ";

    public static final String NUMBER_OF_PLAYER_FAIL = "There can only be 2 to 7 players. ";
    public static final String NUMBER_OF_PLAYER_FAIL_REQUEST = "Re-enter a valid number of players: ";

    public static final String DRAW_PILE_REPORT_0 = "Draw pile: ";
    public static final String DRAW_FILE_REPORT_1 = " cards left.";

    public static final String CARD_PLAYED_NOT_ACE = " just played a ";
    public static final String CARD_PLAYED_ACE = " just played an ";

    public static final String SIZE_OF_HAND_REPORT_0 = " has ";
    public static final String SIZE_OF_HAND_REPORT_1 = " left in their hand.";

    public static final String TOP_CARD_DISPLAY = "The current top card is: ";
    public static final String DECLARED_SUIT_DISPLAY = "The current suit is: ";

    public static final String PLAYER_HAND_DISPLAY = "'s hand: ";
    public static final String CARD_PLAYED_INVALID = "Please enter a valid card: ";

    public static final String NEW_SUIT_DECLARED = " is the new suit.";

    public static final String DRAW_CARD_REPORT = " drew a card.";
    public static final String GAME_SCORE_ANNOUNCEMENT = "Scores for the game: ";
    public static final String WINNER_ANNOUNCEMENT_ONE = "is the winner!";
    public static final String WINNER_ANNOUNCEMENT_MULTIPLE = "are the winners!";

    public static final String END_GAME_ANNOUNCEMENT = "Thank you for playing the game!";

    public static final String TOURNAMENT_SCORE_ANNOUNCEMENT = "Scores for the tournament: ";

    public static final String END_TOURNAMENT_ANNOUNCEMENT = "Thank you for playing Crazy Eights. " +
            "The program will exit now.";

    public static final String CHEATING_REPORT = " attempted to cheat!";

    public static final String TOURNAMENT_SCORE_ID = "ts";
    public static final String TOURNAMENT_WINNER_ID = "tw";
    public static final String GAME_WINNER_ID = "gw";

    private InputStream in;
    private InputStream backupIn;
    private PrintStream out;

    public ConsoleUI(InputStream inputStream, PrintStream outputStream) {
        this.in = inputStream;
        this.backupIn = in;
        this.out = outputStream;
    }

    public void consolePrint(String toPrint) {
        out.println(toPrint);
    }

    public void newTurn() {
        out.println("------------------------------------------------------------------------------------------------");
    }

    public String getUserInput() {
        Scanner scanner = new Scanner(in);
        in = backupIn;
        return scanner.nextLine();
    }

    public void simulateInput(String simulatedInput) {
        in = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        out.println(simulatedInput);
    }

    public Card.Suit getDeclaredSuit() {
        Card.Suit[] suits = Card.Suit.values();
        String suitStr;
        for (Card.Suit suit : suits) {
            suitStr = suit.toString();
            if (getUserInput().equalsIgnoreCase(suitStr)) {
                return suit;
            }
        }
        return null;
    }

    public Card getPlayedCard() {
        Card.Suit[] suits = Card.Suit.values();
        Card.Rank[] ranks = Card.Rank.values();
        String cardPlayed = getUserInput();
        String suitStr;
        String rankStr;
        Card.Suit suitPlayed = null;
        Card.Rank rankPlayed = null;
        for (Card.Suit suit : suits) {
            suitStr = suit.toString().toUpperCase();
            if (cardPlayed.contains(suitStr)) {
                suitPlayed = suit;
                break;
            }
        }
        for (Card.Rank rank : ranks) {
            rankStr = rank.toString().toUpperCase();
            if (cardPlayed.contains(rankStr)) {
                rankPlayed = rank;
                break;
            }
        }
        return new Card(suitPlayed, rankPlayed);
    }

    void reportCheating(String cheaterName) {
        out.println(cheaterName + CHEATING_REPORT);
        out.println(END_TOURNAMENT_ANNOUNCEMENT);
    }

    void reportDrawPile(int drawPileSize) {
        out.println(DRAW_PILE_REPORT_0 + drawPileSize + DRAW_FILE_REPORT_1);
    }

    void displayHand(Player player) {
        String playerName = player.getName();
        List<Card> cards = player.getHand().getCards();
        StringBuilder cardsInHand = new StringBuilder();
        for (Card c : cards) {
            cardsInHand.append(c.toString()).append(", ");
        }
        String cardsInHandStr = cardsInHand.toString();
        cardsInHandStr = cardsInHandStr.substring(0, cardsInHandStr.length() - 2);
        out.println(playerName + PLAYER_HAND_DISPLAY + cardsInHandStr);
    }

    /**
     * Announce the scores and the winner(s).
     *
     * @param scoreLog The map of player scores from the Game or Tournament instance.
     */
    void announceScoreOrWinner(Map<Player, Integer> scoreLog, String type) {
        List<String> winner = new ArrayList<>();
        int winningScore = EightsUtils.maxValueInMap(scoreLog);
        Player player;
        String playerName;
        int playerScore;
        StringBuilder playerScoresToAnnounce = new StringBuilder();
        String playerScoresToAnnounceStr;
        StringBuilder winnerToAnnounce = new StringBuilder();
        String winnerToAnnounceStr;
        String winnerDeclaration = "";

        for (Map.Entry<Player, Integer> entry : scoreLog.entrySet()) {
            player = entry.getKey();
            playerName = player.getName();
            playerScore = scoreLog.get(player);
            playerScoresToAnnounce.append(playerName).append(": ").append(playerScore).append(" ");
            if (playerScore == winningScore) {
                winner.add(playerName);
            }
        }
        playerScoresToAnnounceStr = playerScoresToAnnounce.toString();

        if (winner.size() == 1) {
            winnerToAnnounce.append(winner.get(0)).append(" ");
            winnerDeclaration = WINNER_ANNOUNCEMENT_ONE;
        } else {
            for (String winnerName : winner) {
                winnerToAnnounce.append(winnerName).append(" ");
                winnerDeclaration = WINNER_ANNOUNCEMENT_MULTIPLE;
            }
        }
        winnerToAnnounceStr = winnerToAnnounce.toString();

        switch (type) {
            case GAME_WINNER_ID:
                out.println(GAME_SCORE_ANNOUNCEMENT + playerScoresToAnnounceStr);
                out.println(winnerToAnnounceStr + winnerDeclaration);
                out.println(END_GAME_ANNOUNCEMENT);
                break;
            case TOURNAMENT_SCORE_ID:
                out.println(TOURNAMENT_SCORE_ANNOUNCEMENT + playerScoresToAnnounceStr);
                break;
            case TOURNAMENT_WINNER_ID:
                out.println(TOURNAMENT_SCORE_ANNOUNCEMENT + playerScoresToAnnounceStr);
                out.println(winnerToAnnounceStr + winnerDeclaration);
                out.println(END_TOURNAMENT_ANNOUNCEMENT);
                break;
            default:
                throw new IllegalArgumentException();
        }

    }


    public void reportPlayerTurn(Player player, PlayerTurn playerTurn) {
        String playerName = player.getName();
        Card.Suit newSuit = playerTurn.getDeclaredSuit();
        Card cardPlayed = playerTurn.getPlayedCard();
        String cardPlayedDeclaration;

        if (playerTurn.drewACard) {
            out.println(playerName + DRAW_CARD_REPORT);
        } else {
            if (cardPlayed.getRank() == Card.Rank.ACE) {
                cardPlayedDeclaration = CARD_PLAYED_ACE;
            } else {
                cardPlayedDeclaration = CARD_PLAYED_NOT_ACE;
            }

            out.println(playerName + cardPlayedDeclaration + cardPlayed.toString());

            if (newSuit != null) {
                // Capitalize the first letter
                String newSuitStr = capitalizeFirstLetter(newSuit);
                out.println(newSuitStr + NEW_SUIT_DECLARED);
            }
        }
    }

    private String capitalizeFirstLetter(Object toCapitalize) {
        return toCapitalize.toString().substring(0, 1).toUpperCase()
                + toCapitalize.toString().substring(1);
    }

    public void displayTopCard(Card topPileCard) {
        out.println(TOP_CARD_DISPLAY + topPileCard.toString());
    }

    public void displaySizeOfHand(String playerName, int sizeOfHand) {
        out.println(playerName + SIZE_OF_HAND_REPORT_0 + sizeOfHand + SIZE_OF_HAND_REPORT_1);
    }

    public void displayDeclaredSuit(Card.Suit declaredSuit) {
        out.println(DECLARED_SUIT_DISPLAY + declaredSuit.toString());
    }
}
