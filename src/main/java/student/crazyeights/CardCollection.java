package student.crazyeights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CardCollection {

    private final String label;
    private ArrayList<Card> cards;

    /**
     * Generate an empty collection of cards.
     */
    CardCollection(String label) {
        this.label = label;
        this.cards = new ArrayList<>();
    }

    /**
     * Create a new shuffled deck of card at the beginning of each game.
     *
     * @param label  Label of the shuffled deck
     * @param random Instance of Random passed from main()
     * @return       The shuffled deck
     */
    static CardCollection createDeck(String label, Random random) {
        CardCollection shuffledDeck = new CardCollection(label);
        List<Card> unshuffledDeck = Card.getDeck();
        for (Card c : unshuffledDeck) {
            shuffledDeck.addCard(c);
        }
        shuffledDeck.shuffle(random);
        return shuffledDeck;
    }

    /**
     * Shuffle current card collection.
     * If the top card of the deck is an eight, the deck is shuffled again.
     * @param random Instance of Random passed from main()
     */
    private void shuffle(Random random) {
        do {
            for (int i = 0; i < size(); i++) {
                int j = random.nextInt(size());
                swapCards(i, j);
            }
        } while (cardIsEight(getTopCard()));
    }

    /**
     *
     * @param toCompare
     * @return
     */
    static boolean cardIsEight(Card toCompare) {
        List<Card> listOfEights = Arrays.asList(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT)
        );
        for (Card eight : listOfEights) {
            if (toCompare.equals(eight)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Swaps the cards at indexes i and j.
     */
    private void swapCards(int i, int j) {
        Card temp = cards.get(i);
        cards.set(i, cards.get(j));
        cards.set(j, temp);
    }

    /**
     * Add a card.
     */
    void addCard(Card cardToAdd) {
        cards.add(cardToAdd);
    }

    /**
     * Moves n cards from current collection to the parameter collection.
     */
    void deal(CardCollection targetCollection, int n) {
        for (int i = 0; i < n; i++) {
            Card card = removeTopCard();
            targetCollection.addCard(card);
        }
    }

    /**
     * Moves all remaining cards to the parameter collection.
     */
    void dealAll(CardCollection targetCollection) {
        int n = size();
        deal(targetCollection, n);
    }

    /**
     * @return the top card.
     */
    Card getTopCard() {
        int i = size() - 1;
        return cards.get(i);
    }

    /**
     * Remove a card.
     * @return The card that is removed.
     */
    Card removeCard(int index) {
        return cards.remove(index);
    }

    void removeCard(Card toRemove) {
        cards.remove(toRemove);
    }

    /**
     * Remove the top (last) card.
     * @return The card that is removed.
     */
    Card removeTopCard() {
        int index = size() - 1;
        return cards.remove(index);
    }

    /**
     * @return The number of cards.
     */
    int size() {
        return cards.size();
    }

    /**
     * @return True if there's no more card left, false otherwise.
     */
    boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return The label.
     */
    String getLabel() {
        return label;
    }

    /**
     * @return The list of cards.
     */
    ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Prints the label and cards.
     */
    public String toString() {
        return label + " contains: " + cards.toString();
    }
}
