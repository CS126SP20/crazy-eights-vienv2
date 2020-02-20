package student.crazyeights;

/*
 * ========================
 * DO NOT MODIFY THIS FILE.
 * ========================
 */

import java.util.Objects;

/**
 * Represents an player's action on their turn: either they drew a card or they played a card.
 */
public class PlayerTurn {

    /**
     * The ID of the player that this action corresponds to
     */
    public int playerId;

    /**
     * If the player drew a card on their turn
     */
    public boolean drewACard;

    /**
     * The card the player played on their turn, or null if the player didn't play a card.
     */
    public Card playedCard;

    /**
     * When a player plays an "8", they can declare what suit the next player must play to.
     * <p>
     * If the player played an "8", this is the suit that they declared. Otherwise, this is null.
     */
    public Card.Suit declaredSuit;

    // Convenience methods; you might or might not need these.


    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isDrewACard() {
        return drewACard;
    }

    public void setDrewACard(boolean drewACard) {
        this.drewACard = drewACard;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public void setPlayedCard(Card playedCard) {
        this.playedCard = playedCard;
    }

    public Card.Suit getDeclaredSuit() {
        return declaredSuit;
    }


    public void setDeclaredSuit(Card.Suit declaredSuit) {
        this.declaredSuit = declaredSuit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerTurn that = (PlayerTurn) o;
        return playerId == that.playerId &&
            drewACard == that.drewACard &&
            Objects.equals(playedCard, that.playedCard) &&
            declaredSuit == that.declaredSuit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, drewACard, playedCard, declaredSuit);
    }
}
