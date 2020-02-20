package student.crazyeights;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

public class CardCollectionTest {

    private List<Card> deck;
    private Random random;
    private CardCollection cardCollection, targetCollection, expectedOriginCollection, expectedTargetCollection;
    private Card card0, card1, card2, card3, targetCollectCard0, targetCollectCard1;

    @Before
    public void setUp() {
        deck = Card.getDeck();
        random = new Random();

        cardCollection = new CardCollection("CardCollectionTest");
        card0 = new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT);
        card1 = new Card(Card.Suit.CLUBS, Card.Rank.JACK);
        card2 = new Card(Card.Suit.SPADES, Card.Rank.EIGHT);
        card3 = new Card(Card.Suit.CLUBS, Card.Rank.EIGHT);
        cardCollection.addCard(card0);
        cardCollection.addCard(card1);
        cardCollection.addCard(card2);
        cardCollection.addCard(card3);

        targetCollection = new CardCollection("MoveCardsTest");
        targetCollectCard0 = new Card(Card.Suit.DIAMONDS, Card.Rank.TWO);
        targetCollectCard1 = new Card(Card.Suit.HEARTS, Card.Rank.THREE);
        targetCollection.addCard(targetCollectCard0);
        targetCollection.addCard(targetCollectCard1);

        expectedOriginCollection = new CardCollection("ExpectedOriginCollection");
        expectedTargetCollection = new CardCollection("ExpectedTargetCollection");
    }

    @Test
    public void deckIsShuffled() {
        CardCollection shuffledDeck = CardCollection.createDeck("ShuffledDeckTest", random);
        assertNotSame(shuffledDeck, deck);
    }

    @Test
    public void oneCardIsMoved() {
        cardCollection.deal(targetCollection, 1);

        expectedOriginCollection.addCard(card0);
        expectedOriginCollection.addCard(card1);
        expectedOriginCollection.addCard(card2);

        expectedTargetCollection.addCard(targetCollectCard0);
        expectedTargetCollection.addCard(targetCollectCard1);
        expectedTargetCollection.addCard(card3);

        assertEquals(cardCollection.getCards(), expectedOriginCollection.getCards());
        assertEquals(targetCollection.getCards(), expectedTargetCollection.getCards());
    }

    @Test
    public void multipleCardsAreMoved() {
        cardCollection.deal(targetCollection, 3);

        expectedOriginCollection.addCard(card0);

        expectedTargetCollection.addCard(targetCollectCard0);
        expectedTargetCollection.addCard(targetCollectCard1);
        expectedTargetCollection.addCard(card3);
        expectedTargetCollection.addCard(card2);
        expectedTargetCollection.addCard(card1);

        assertEquals(expectedOriginCollection.getCards(), cardCollection.getCards());
        assertEquals(expectedTargetCollection.getCards(), targetCollection.getCards());
    }

    @Test
    public void allCardsAreMoved() {
        cardCollection.dealAll(targetCollection);

        expectedTargetCollection.addCard(targetCollectCard0);
        expectedTargetCollection.addCard(targetCollectCard1);
        expectedTargetCollection.addCard(card3);
        expectedTargetCollection.addCard(card2);
        expectedTargetCollection.addCard(card1);
        expectedTargetCollection.addCard(card0);

        assertEquals(expectedOriginCollection.getCards(), cardCollection.getCards());
        assertEquals(expectedTargetCollection.getCards(), targetCollection.getCards());
    }

    @Test
    public void cardIsAdded() {
        List<Card> cardsAdded = Arrays.asList(card0, card1, card2, card3);
        assertEquals(cardsAdded, cardCollection.getCards());
    }

    @Test
    public void cardIsRemoved() {
        assertEquals(card1, cardCollection.removeCard(1));
    }

    @Test
    public void topCardIsRemoved() {
        assertEquals(card3, cardCollection.removeTopCard());
    }

    @Test
    public void getCorrectTopCard() {
        assertEquals(cardCollection.getTopCard(), card3);
    }

    @Test
    public void collectionIsNotEmpty() {
        assertFalse(cardCollection.isEmpty());
    }

    @Test
    public void collectionIsEmpty() {
        CardCollection cardCollection = new CardCollection("EmptySet");
        assertTrue(cardCollection.isEmpty());
    }

}
