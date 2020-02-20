package student.crazyeights;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAI implements PlayerStrategy {


    private int id;
    private String name;
    private ConsoleUI ui;
    private Random random;

    private List<Integer> opponentIds;
    private List<PlayerTurn> gameHistory;

    private List<Card> availableCards;
    private List<Card> playableCardsNotEight;

    private List<Card> sortedPlayableCardsByValue;
    private List<Card> sortedPlayableCardsByCommonality;

    private int noOfEightCards;
    private List<Card.Suit> mostCommonSuit;

    private Map<Integer, Integer> cardsLeftInOpponentHands = new HashMap<>();
    private int cardsLeftInDrawPile;

    /**
     * Gives the player their assigned id, as well as a list of the opponents' assigned ids.
     * <p>
     * This method will be called by the game engine once at the very beginning (before any games
     * are started), to allow the player to set up any initial state.
     *
     * @param playerId    The id for this player, assigned by the game engine
     * @param opponentIds A list of ids for this player's opponents
     */
    @Override
    public void init(int playerId, List<Integer> opponentIds) {
        this.id = playerId;
        this.opponentIds = opponentIds;
        for (int opponentId : opponentIds) {
            cardsLeftInOpponentHands.put(opponentId, 0);
        }
    }

    /**
     * Called once at the beginning of o game to deal the player their initial cards.
     *
     * @param cards The initial list of cards dealt to this player
     */
    @Override
    public void receiveInitialCards(List<Card> cards) {
        this.availableCards = cards;
    }

    /**
     * Called when this player has chosen to draw a card from the deck.
     *
     * @param drawnCard The card that this player has drawn
     */
    @Override
    public void receiveCard(Card drawnCard) {
        this.availableCards.add(drawnCard);
    }

    /**
     * Called to ask whether the player wants to draw this turn. Gives this player the top card of
     * the discard pile at the beginning of their turn, as well as an optional suit for the pile in
     * case a "8" was played, and the suit was changed.
     * <p>
     * By having this return true, the game engine will then call receiveCard() for this player.
     * Otherwise, playCard() will be called.
     *
     * @param topPileCard The card currently at the top of the pile
     * @param changedSuit The suit that the pile was changed to as the result of an "8" being
     *                    played. Will be null if no "8" was played.
     * @return whether or not the player wants to draw
     */
    @Override
    public boolean shouldDrawCard(Card topPileCard, Card.Suit changedSuit) {

        filterPlayableCards();

        if (shouldPlayEightCard()) {
            return false;
        }

        return playableCardsNotEight.isEmpty();

    }

    /**
     * Called when this player is ready to play a card (will not be called if this player drew on
     * their turn).
     * <p>
     * This will end this player's turn.
     *
     * @return The card this player wishes to put on top of the pile
     */
    @Override
    public Card playCard() {

        Card cardChosen = suggestCard();

        ui.simulateInput(cardChosen.toString());

        availableCards.remove(cardChosen);

        return cardChosen;

    }

    /**
     * Called if this player decided to play a "8" card to ask the player what suit they would like
     * to declare.
     * <p>
     * This player should then return the Card.Suit enum that it wishes to set for the discard
     * pile.
     */
    @Override
    public Card.Suit declareSuit() {

        findMostCommonSuit();

        Card.Suit suitToDeclare;

        for (int i = gameHistory.size() - 1; i > gameHistory.size() - 4; i--) {

            PlayerTurn opponentTurn = gameHistory.get(i);
            int opponentId = opponentTurn.getPlayerId();
            if (opponentId == id) {
                continue;
            }

            Card.Suit opponentDeclaredSuit = opponentTurn.getDeclaredSuit();

            if (opponentDeclaredSuit != null && cardsLeftInOpponentHands.get(opponentId) == 1
                    && (opponentDeclaredSuit == mostCommonSuit.get(0))) {
                suitToDeclare = mostCommonSuit.get(1);
                ui.simulateInput(suitToDeclare.toString());
                return suitToDeclare;
            }

        }

        suitToDeclare = mostCommonSuit.get(0);
        ui.simulateInput(suitToDeclare.toString());
        return suitToDeclare;

    }

    /**
     * Called at the very beginning of this player's turn to give it context of what its opponents
     * chose to do on each of their turns.
     *
     * @param opponentActions A list of what the opponents did on each of their turns
     */
    @Override
    public void processOpponentActions(List<PlayerTurn> opponentActions) throws UnsupportedOperationException {
    }

    private boolean shouldPlayEightCard() {

        calculateCardsLeft();

        if (noOfEightCards > 0) {

            if (availableCards.size() == 2 || cardsLeftInDrawPile < 2) {
                return true;
            }

            for (int noOfCardsInOpponentHand : cardsLeftInOpponentHands.keySet()) {
                if (noOfCardsInOpponentHand == 1) {
                    return true;
                }
            }

        }

        return false;

    }

    private void sortCardsByCommonality() {

        Map<Integer, Card> sortedMap = new TreeMap<>(Comparator.reverseOrder());
        List<Card> playableCardsRest = new ArrayList<>();
        for (Card c1 : playableCardsNotEight) {
            int commonality = 0;
            playableCardsRest.addAll(playableCardsNotEight);
            playableCardsRest.remove(c1);
            for (Card c2 : playableCardsRest) {
                if (c1.getRank() == c2.getRank()
                        && c1.getSuit() == c2.getSuit()) {
                    commonality += 2;
                } else if (canPlayCard(c1, c2, null)) {
                    commonality += 1;
                }
            }
            sortedMap.put(commonality, c1);
        }

        sortedPlayableCardsByCommonality = new ArrayList<>(sortedMap.values());

    }

    private Card suggestCard() {

        if (shouldPlayEightCard()) {
            for (Card c : availableCards) {
                if (c.getRank() == Card.Rank.EIGHT) {
                    return c;
                }
            }
        }

        sortCardsByCommonality();
        sortCardsByValue();

        if (playableCardsNotEight.size() <= 2) {
            return sortedPlayableCardsByValue.get(0);
        }

        sortedPlayableCardsByValue = sortedPlayableCardsByValue.subList(0, 3);
        sortedPlayableCardsByCommonality = sortedPlayableCardsByCommonality.subList(0, 3);
        Card currentHighestValueCard;

        for (int i = 0; i <= 3; i++) {
            currentHighestValueCard = sortedPlayableCardsByValue.get(i);
            if (sortedPlayableCardsByCommonality.contains(currentHighestValueCard)) {
                return currentHighestValueCard;
            }
        }

        return sortedPlayableCardsByValue.get(0);

    }

    private void findMostCommonSuit() {

        int diamonds = 0;
        int hearts = 0;
        int spades = 0;
        int clubs = 0;

        for (Card c : availableCards) {
            switch (c.getSuit()) {
                case DIAMONDS:
                    diamonds += 1;
                    break;
                case HEARTS:
                    hearts += 1;
                    break;
                case SPADES:
                    spades += 1;
                    break;
                case CLUBS:
                    clubs += 1;
                    break;
                default:
                    break;
            }
        }

        Map<Integer, Card.Suit> sortedMap = new TreeMap<>(Comparator.reverseOrder());
        sortedMap.put(diamonds, Card.Suit.DIAMONDS);
        sortedMap.put(hearts, Card.Suit.HEARTS);
        sortedMap.put(spades, Card.Suit.SPADES);
        sortedMap.put(clubs, Card.Suit.CLUBS);

        mostCommonSuit = new ArrayList<>(sortedMap.values());

    }

    private void sortCardsByValue() {

        sortedPlayableCardsByValue = playableCardsNotEight;
        sortedPlayableCardsByValue = sortedPlayableCardsByValue.stream()
                .sorted(Comparator.comparing(Card::getPointValue).reversed())
                .collect(Collectors.toList());

    }

    private void filterPlayableCards() {

        playableCardsNotEight = new ArrayList<>();
        noOfEightCards = 0;

        Card topPileCard = null;
        Card.Suit changedSuit = null;
        PlayerTurn lastTurn;

        for (int i = gameHistory.size() - 1; i >= 0; i--) {
            lastTurn = gameHistory.get(i);
            topPileCard = lastTurn.getPlayedCard();
            if (topPileCard != null) {
                if (CardCollection.cardIsEight(topPileCard)) {
                    changedSuit = lastTurn.getDeclaredSuit();
                }
                break;
            }
        }

        for (Card avcard : availableCards) {
            if (CardCollection.cardIsEight(avcard)) {
                noOfEightCards += 1;
            }
            if (canPlayCard(avcard, topPileCard, changedSuit)) {
                playableCardsNotEight.add(avcard);
            }
        }

    }

    private boolean canPlayCard(Card cardToPlay, Card topPileCard, Card.Suit changedSuit) {
        if (changedSuit != null) {
            return !CardCollection.cardIsEight(cardToPlay) &&
                    (cardToPlay.getSuit() == changedSuit);
        }
        return !CardCollection.cardIsEight(cardToPlay) &&
                (cardToPlay.getSuit() == topPileCard.getSuit()
                        || cardToPlay.getRank() == topPileCard.getRank());
    }

    private void calculateCardsLeft() {

        // Calculates cards left in draw pile.
        if (opponentIds.size() == 1) {
            cardsLeftInDrawPile = 52 - 7 * 2;
        } else {
            cardsLeftInDrawPile = 52 - 5 * (opponentIds.size() + 1);
        }

        // Calculates cards left in opponents' hands.
        int cardsLeftInHand;
        for (PlayerTurn pt : gameHistory) {
            for (int opponentId : opponentIds) {
                if (pt.getPlayerId() == opponentId) {
                    if (pt.isDrewACard()) {
                        cardsLeftInDrawPile -= 1;
                        cardsLeftInHand = cardsLeftInOpponentHands.get(opponentId) + 1;
                    } else {
                        cardsLeftInHand = cardsLeftInOpponentHands.get(opponentId) - 1;
                    }
                    cardsLeftInOpponentHands.replace(opponentId, cardsLeftInHand);
                }
            }
        }

    }

    public void setGameHistory(List<PlayerTurn> gameHistory) {
        this.gameHistory = new ArrayList<>();
        this.gameHistory.addAll(gameHistory);
    }

    public void declareName() {
        ui.simulateInput(generateName());
    }

    public String generateName() {
        this.name = EightsUtils.generateBotNames(random);
        return name;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setUI(ConsoleUI ui) {
        this.ui = ui;
    }

    /**
     * Called before a game begins, to allow for resetting any state between games.
     */
    @Override
    public void reset() {
        this.gameHistory = new ArrayList<>();
    }

}
