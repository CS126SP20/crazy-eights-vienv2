package student.crazyeights;

import java.util.*;

import static student.crazyeights.ConsoleUI.GAME_WINNER_ID;
import static student.crazyeights.ConsoleUI.NEW_GAME_ANNOUNCEMENT;

public class Game {
    private final ConsoleUI ui;
    private Random random;
    private CardCollection drawPile;
    private CardCollection discardPile;
    private ArrayList<Player> players;
    private Map<Player, Integer> gameScore = new HashMap<>();
    private List<PlayerTurn> gameHistory = new ArrayList<>();

    /**
     * Sets up the initial state of the game.
     *
     * @param consoleUI the input and output streams.
     * @param random    the instance of random passed from main().
     */
    public Game(ConsoleUI consoleUI, Random random, List<Player> players) {

        this.ui = consoleUI;
        this.random = random;
        this.players = (ArrayList<Player>) players;

        // Create a new shuffled deck of cards
        CardCollection deck = CardCollection.createDeck("deck", random);

        // Deal one card into the discard pile.
        // Impossible to be an eight because of shuffle() implementation so don't have to check for that.
        discardPile = new CardCollection("DiscardPile");
        deck.deal(discardPile, 1);

        // The rest of the deck becomes the draw pile.
        drawPile = new CardCollection("DrawPile");
        deck.dealAll(drawPile);

    }

    /**
     * Plays the game.
     */
    public void playGame() {

        ui.consolePrint(NEW_GAME_ANNOUNCEMENT);

        dealHand();

        Player currentPlayer = chooseRandomFirstPlayer();

        while (!isOver()) {
            displayState(currentPlayer);
            takeTurn(currentPlayer);
            currentPlayer = nextPlayer(currentPlayer);
        }

        calculateScore();

        // Announce the game winner and the final scores.
        ui.announceScoreOrWinner(gameScore, GAME_WINNER_ID);

    }

    /**
     * Displays the state of the game.
     */
    public void displayState(Player currentPlayer) {

        // Create a line going across separating turns.
        ui.newTurn();

        // "Bruce has 4 cards left in their hand".
        for (Player p : players) {
            ui.displaySizeOfHand(p.getName(), p.getHand().size());
        }

        // "Draw pile: 29 cards left".
        ui.reportDrawPile(drawPile.size());

        // "The current top card is: QUEEN of HEARTS.
        ui.displayTopCard(getTopPileCard());

        // Gets the suit the previous player declared (if they did).
        if (CardCollection.cardIsEight(getTopPileCard())) {
            Card.Suit declaredSuit = gameHistory.get(gameHistory.size() - 1).getDeclaredSuit();
            ui.displayDeclaredSuit(declaredSuit);
        }

        // "Emily's (you) hand: ACE of SPADES, SEVEN of DIAMONDS, THREE of CLUBS, etc.
        ui.displayHand(currentPlayer);

    }

    /**
     * One player takes a turn.
     */
    private void takeTurn(Player player) {

        Card currentDiscard = getTopPileCard();
        Card nextDiscard;

        // Creates a new turn.
        PlayerTurn newTurn = new PlayerTurn();
        newTurn.setPlayerId(player.getId());

        // Gets declared suit form the last PlayerTurn.
        Card.Suit declaredSuit;

        if (!gameHistory.isEmpty()) {
            // Gets the declared suit from PlayerTurn, if there is none, returns null.
            declaredSuit = gameHistory.get(gameHistory.size() - 1).getDeclaredSuit();
        } else {
            // If it is the first term, create new PlayerTurn to notify first player.
            PlayerTurn firstTurn = new PlayerTurn();
            firstTurn.setPlayedCard(getTopPileCard());
            gameHistory.add(firstTurn);
            player.pingAiGameHistory(gameHistory);
            declaredSuit = null;
        }

        if (player.pingAiShouldDraw(currentDiscard, declaredSuit)) {
            player.pingAiDrawCard(drawFromPile());
            newTurn.setDrewACard(true);
        } else {
            player.pingAiPlayCard();
            nextDiscard = ui.getPlayedCard();

//                if (!player.getHand().getCards().contains(nextDiscard)) {
//                    // The player has attempted to play a card they don't have.
//                    // He is reported for cheating and the game ends.
//                    ui.reportCheating(player.getName());
//                    System.exit(0);
//                }

            player.cardIsPlayed(nextDiscard);
            if (CardCollection.cardIsEight(nextDiscard)) {
                player.pingAiDeclareSuit();
                newTurn.setDeclaredSuit(ui.getDeclaredSuit());
            }

            discardPile.addCard(nextDiscard);
            newTurn.setPlayedCard(nextDiscard);

        }

        gameHistory.add(newTurn);

        for (Player p : players) {
            p.pingAiGameHistory(gameHistory);
        }
        ui.reportPlayerTurn(player, newTurn);

    }

    /**
     * Chooses a random first player.
     */
    private Player chooseRandomFirstPlayer() {
        return players.get(random.nextInt(players.size()));
    }

    /**
     * Creates a new player and deal them their hand.
     */
    private void dealHand() {

        int noOfCardsPerPlayer;

        if (players.size() == 2) {
            // If there are only 2 players, each gets 7 cards.
            noOfCardsPerPlayer = 7;
        } else {
            //If there are from 3 to 7 players inclusive, each gets 5.
            noOfCardsPerPlayer = 5;
        }

        CardCollection playerHand;
        for (Player p : players) {
            playerHand = p.getHand();
            drawPile.deal(playerHand, noOfCardsPerPlayer);
            p.pingAiDealHand();
        }

    }

    /**
     * Totals the value of the player's hand (not their score) at the end of the game.
     *
     * @param player The player
     * @return The value of the player's hand.
     */
    private int countHandValue(Player player) {
        List<Card> hand = player.getHand().getCards();
        int sumOfHand = 0;
        for (Card c : hand) {
            sumOfHand += c.getPointValue();
        }
        return sumOfHand;
    }

    /**
     * Gets index of player from the list.
     */
    private int getPlayerIndex(Player player) {
        return players.indexOf(player);
    }

    /**
     * Returns true if either a player wins (their hand is empty) or the draw pile is empty.
     */
    private boolean isOver() {
        for (Player p : players) {
            if (p.getHand().isEmpty()) {
                return true;
            }
        }
        return drawPile.isEmpty();
    }

    /**
     * Draws one card from the draw pile.
     */
    private Card drawFromPile() {
        Card topCard = drawPile.getTopCard();
        drawPile.removeTopCard();
        return topCard;
    }

    /**
     * Moves on to the next player.
     */
    private Player nextPlayer(Player currentPlayer) {
        int currentPlayerIndex = getPlayerIndex(currentPlayer);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return players.get(currentPlayerIndex);
    }

    /**
     * Get the current card of the discard pile.
     *
     * @return The top discard card.
     */
    private Card getTopPileCard() {
        return discardPile.getTopCard();
    }

    /**
     * Calculates the score of each player at the end of each game.
     */
    private void calculateScore() {
        // Calculates the total score.
        int totalScore = 0;
        for (Player p : players) {
            totalScore = countHandValue(p);
        }
        // Saves the final score of each player.
        int playerScore;
        for (Player p : players) {
            // Save the final score.
            playerScore = totalScore - countHandValue(p);
            gameScore.put(p, playerScore);
        }
    }

    /**
     * Updates the score when a game ends.
     *
     * @param oldScore The scores from the last game.
     * @return The new updated scores after current game.
     */
    Map<Player, Integer> updateScore(Map<Player, Integer> oldScore) {

        Player player;
        int playerScore;
        Map<Player, Integer> newScore = new HashMap<>();

        for (Map.Entry<Player, Integer> entry : gameScore.entrySet()) {
            player = entry.getKey();
            playerScore = entry.getValue() + oldScore.get(player);
            newScore.put(player, playerScore);
        }

        return newScore;

    }

}
