package student.crazyeights;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {

    private String name;
    private int id;
    private ConsoleUI ui;
    private Random random;
    private CardCollection hand;
    private List<Integer> score;

    private PlayerAI ai;


    /**
     * Creates a new player with an empty hand and an empty history.
     */
    Player(ConsoleUI ui, Random random) {

        this.ui = ui;

        this.random = random;

        this.score = new ArrayList<>();

        this.ai = new PlayerAI();
        ai.setUI(ui);

        this.id = hashCode();

    }

    public void pingAiInit(List<Player> players) {

        List<Integer> opponentIds = new ArrayList<>();
        for (Player p : players) {
            opponentIds.add(p.getId());
        }
        opponentIds.remove((Integer) id);

        ai.init(this.id, opponentIds);

    }

    public void pingAiGameHistory(List<PlayerTurn> gameHistory) {
        ai.setGameHistory(gameHistory);
    }

    public Card.Suit pingAiDeclareSuit() {
        return ai.declareSuit();
    }

    public Card pingAiPlayCard() {
        return ai.playCard();
    }

    public void cardIsPlayed(Card played) {
        hand.removeCard(played);
    }

    public void pingAiDrawCard(Card drawedCard) {
        hand.addCard(drawedCard);
        ai.receiveCard(drawedCard);
    }

    public boolean pingAiShouldDraw(Card topPileCard, Card.Suit changedSuit) {
        return ai.shouldDrawCard(topPileCard, changedSuit);
    }

    public void pingAiDealHand() {
        ai.receiveInitialCards(hand.getCards());
    }

    public void pingAiForName() {
        ai.setRandom(random);
        ai.declareName();
    }

    public void createEmptyHand() {
        String handLabel = name + "_Hand";
        this.hand = new CardCollection(handLabel);
    }

    /**
     * Gets the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set player's name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID (hashcode) of the player.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the player's hand.
     */
    public CardCollection getHand() {
        return hand;
    }

    /**
     * Gets the player's score history.
     */
    public List<Integer> getScore() {
        return score;
    }

    /**
     * Called before a game begins, to allow for resetting any state between games.
     */
    public void reset() {
        String handLabel = hand.getLabel();
        this.hand = new CardCollection(handLabel);
        ai.reset();
    }
}

