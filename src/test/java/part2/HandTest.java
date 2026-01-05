package part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import part1.Creature;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HandTest {
    private Hand hand;
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;
    private Card card5;
    private Card card6;

    @BeforeEach
    void setUp() {
        hand = new Hand();

        Creature creature1 = new Creature("Dragon", 100, 50);
        Creature creature2 = new Creature("Phoenix", 80, 60);
        Creature creature3 = new Creature("Griffin", 90, 55);
        Creature creature4 = new Creature("Unicorn", 70, 40);
        Creature creature5 = new Creature("Basilisk", 85, 45);
        Creature creature6 = new Creature("Kraken", 120, 70);

        card1 = new Card(creature1);
        card2 = new Card(creature2);
        card3 = new Card(creature3);
        card4 = new Card(creature4);
        card5 = new Card(creature5);
        card6 = new Card(creature6);
    }

    @Test
    void testConstructor() {
        assertTrue(hand.isEmpty(), "New hand should be empty");
        assertEquals(0, hand.getSize(), "New hand should have size 0");
        assertFalse(hand.isFull(), "New hand should not be full");
    }

    @Test
    void testMaxCardsConstant() {
        assertEquals(5, Hand.MAX_CARDS, "MAX_CARDS should be 5");
    }

    @Test
    void testAddCardToEmptyHand() {
        boolean result = hand.addCard(card1);

        assertTrue(result, "Adding card to empty hand should return true");
        assertEquals(1, hand.getSize(), "Hand size should be 1");
        assertFalse(hand.isEmpty(), "Hand should not be empty");
        assertFalse(hand.isFull(), "Hand should not be full with 1 card");
    }

    @Test
    void testAddMultipleCards() {
        assertTrue(hand.addCard(card1), "Adding first card should succeed");
        assertTrue(hand.addCard(card2), "Adding second card should succeed");
        assertTrue(hand.addCard(card3), "Adding third card should succeed");

        assertEquals(3, hand.getSize(), "Hand should have 3 cards");
        assertFalse(hand.isEmpty(), "Hand should not be empty");
        assertFalse(hand.isFull(), "Hand should not be full with 3 cards");
    }

    @Test
    void testAddCardsToMaxCapacity() {
        assertTrue(hand.addCard(card1), "Adding card 1 should succeed");
        assertTrue(hand.addCard(card2), "Adding card 2 should succeed");
        assertTrue(hand.addCard(card3), "Adding card 3 should succeed");
        assertTrue(hand.addCard(card4), "Adding card 4 should succeed");
        assertTrue(hand.addCard(card5), "Adding card 5 should succeed");

        assertEquals(5, hand.getSize(), "Hand should have 5 cards");
        assertTrue(hand.isFull(), "Hand should be full with 5 cards");
        assertFalse(hand.isEmpty(), "Hand should not be empty");
    }

    @Test
    void testAddCardWhenFull() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        hand.addCard(card4);
        hand.addCard(card5);

        boolean result = hand.addCard(card6);

        assertFalse(result, "Adding card to full hand should return false");
        assertEquals(5, hand.getSize(), "Hand size should remain 5");
        assertTrue(hand.isFull(), "Hand should still be full");
    }


    @Test
    void testRemoveCard() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card removedCard = hand.removeCard(1);

        assertEquals(card2, removedCard, "Should return the removed card");
        assertEquals(2, hand.getSize(), "Hand size should decrease by 1");
        assertFalse(hand.isEmpty(), "Hand should not be empty");
        assertFalse(hand.isFull(), "Hand should not be full");
    }

    @Test
    void testRemoveCardFromSingleCardHand() {
        hand.addCard(card1);

        Card removedCard = hand.removeCard(0);

        assertEquals(card1, removedCard, "Should return the removed card");
        assertEquals(0, hand.getSize(), "Hand should be empty");
        assertTrue(hand.isEmpty(), "Hand should be empty");
        assertFalse(hand.isFull(), "Empty hand should not be full");
    }

    @Test
    void testRemoveCardInvalidIndex() {
        hand.addCard(card1);
        hand.addCard(card2);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            hand.removeCard(5);
        }, "Should throw IndexOutOfBoundsException for invalid index");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            hand.removeCard(-1);
        }, "Should throw IndexOutOfBoundsException for negative index");

        assertEquals(2, hand.getSize(), "Hand size should remain unchanged after failed removal");
    }

    @Test
    void testPlayCard() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card playedCard = hand.playCard(1);

        assertEquals(card2, playedCard, "Should return the played card");
        assertEquals(2, hand.getSize(), "Hand size should decrease by 1");
    }

    @Test
    void testPlayCardInvalidIndex() {
        hand.addCard(card1);
        hand.addCard(card2);

        Card result1 = hand.playCard(-1);
        assertNull(result1, "Playing card with negative index should return null");

        Card result2 = hand.playCard(5);
        assertNull(result2, "Playing card with index >= size should return null");

        Card result3 = hand.playCard(2);
        assertNull(result3, "Playing card with index == size should return null");

        assertEquals(2, hand.getSize(), "Hand size should remain unchanged after failed plays");
    }

    @Test
    void testPlayCardFromEmptyHand() {
        Card result = hand.playCard(0);
        assertNull(result, "Playing card from empty hand should return null");
        assertTrue(hand.isEmpty(), "Hand should remain empty");
    }

    @Test
    void testIsFull() {
        assertFalse(hand.isFull(), "Empty hand should not be full");

        hand.addCard(card1);
        assertFalse(hand.isFull(), "Hand with 1 card should not be full");

        hand.addCard(card2);
        hand.addCard(card3);
        hand.addCard(card4);
        assertFalse(hand.isFull(), "Hand with 4 cards should not be full");

        hand.addCard(card5);
        assertTrue(hand.isFull(), "Hand with 5 cards should be full");
    }

    @Test
    void testIsEmpty() {
        assertTrue(hand.isEmpty(), "New hand should be empty");

        hand.addCard(card1);
        assertFalse(hand.isEmpty(), "Hand with cards should not be empty");

        hand.removeCard(0);
        assertTrue(hand.isEmpty(), "Hand should be empty after removing all cards");
    }

    @Test
    void testGetSize() {
        assertEquals(0, hand.getSize(), "Empty hand should have size 0");

        hand.addCard(card1);
        assertEquals(1, hand.getSize(), "Hand should have size 1");

        hand.addCard(card2);
        hand.addCard(card3);
        assertEquals(3, hand.getSize(), "Hand should have size 3");

        hand.removeCard(1);
        assertEquals(2, hand.getSize(), "Hand should have size 2 after removal");

        hand.playCard(0);
        assertEquals(1, hand.getSize(), "Hand should have size 1 after playing card");
    }

    @Test
    void testToStringEmptyHand() {
        String result = hand.toString();

        assertTrue(result.contains("------ HAND CONTENTS ------"), "Should contain header");
        assertTrue(result.contains("Total cards: 0"), "Should show 0 cards");
        assertTrue(result.contains("---------------------------"), "Should contain separators");
    }

    @Test
    void testToStringWithCards() {
        hand.addCard(card1);
        hand.addCard(card2);

        String result = hand.toString();

        assertTrue(result.contains("------ HAND CONTENTS ------"), "Should contain header");
        assertTrue(result.contains("Total cards: 2"), "Should show 2 cards");
        assertTrue(result.contains("---------------------------"), "Should contain separators");
        assertTrue(result.contains(" 0 |"), "Should contain index 0");
        assertTrue(result.contains(" 1 |"), "Should contain index 1");
    }

    @Test
    void testToStringWithVisibleAndHiddenCards() {
        card1.setVisible(true);
        card2.setVisible(false);

        hand.addCard(card1);
        hand.addCard(card2);

        String result = hand.toString();

        assertTrue(result.contains("Creature:"), "Should show creature info for visible card");
        assertTrue(result.contains("Card is face down"), "Should show face down message for hidden card");
    }

    @Test
    void testShowHand() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            hand.addCard(card1);
            hand.display();

            String output = outputStream.toString();
            assertTrue(output.contains("------ HAND CONTENTS ------"), "Should print hand contents");
            assertTrue(output.contains("Total cards: 1"), "Should show card count");

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testCardOrderPreservation() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card played = hand.playCard(1);
        assertEquals(card2, played, "Should play the correct card");

        String handString = hand.toString();
        assertTrue(handString.indexOf("0 |") < handString.indexOf("1 |"),
                "Remaining cards should maintain their relative order");
    }

    @Test
    void testMultipleAddRemoveOperations() {
        assertTrue(hand.addCard(card1));
        assertTrue(hand.addCard(card2));
        assertTrue(hand.addCard(card3));

        Card removed = hand.removeCard(1);
        assertEquals(card2, removed);
        assertEquals(2, hand.getSize());

        assertTrue(hand.addCard(card4));
        assertTrue(hand.addCard(card5));
        assertEquals(4, hand.getSize());

        assertTrue(hand.addCard(card6));
        assertTrue(hand.isFull());

        Card extraCard = new Card(new Creature("Extra", 1, 1));
        assertFalse(hand.addCard(extraCard));
    }

    @Test
    void testPlayAllCards() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card played1 = hand.playCard(0);
        assertEquals(card1, played1);
        assertEquals(2, hand.getSize());

        Card played2 = hand.playCard(0);
        assertEquals(card2, played2);
        assertEquals(1, hand.getSize());

        Card played3 = hand.playCard(0);
        assertEquals(card3, played3);
        assertEquals(0, hand.getSize());
        assertTrue(hand.isEmpty());
    }

    @Test
    void testRemoveAndPlayFromEmptyHand() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            hand.removeCard(0);
        }, "Should throw exception when removing from empty hand");

        Card result = hand.playCard(0);
        assertNull(result, "Playing from empty hand should return null");
    }

    @Test
    void testBoundaryConditions() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card played = hand.playCard(2);
        assertEquals(card3, played);

        Card played2 = hand.playCard(0);
        assertEquals(card1, played2);

        Card invalid = hand.playCard(1);
        assertNull(invalid, "Should return null for index out of bounds");
    }

    @Test
    void testHandStateAfterOperations() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        hand.addCard(card4);
        hand.addCard(card5);

        assertTrue(hand.isFull(), "Hand should be full");
        assertEquals(5, hand.getSize(), "Hand should have 5 cards");

        hand.removeCard(2);

        assertFalse(hand.isFull(), "Hand should not be full after removal");
        assertEquals(4, hand.getSize(), "Hand should have 4 cards");

        assertTrue(hand.addCard(card6), "Should be able to add card after removal");
        assertTrue(hand.isFull(), "Hand should be full again");
    }

    @Test
    void testToStringFormat() {
        hand.addCard(card1);
        hand.addCard(card2);

        String result = hand.toString();

        assertTrue(result.contains("------ HAND CONTENTS ------"), "Should have header");
        assertTrue(result.contains("Total cards: 2"), "Should show correct count");
        assertTrue(result.contains("---------------------------"), "Should have separators");
        assertTrue(result.contains(" 0 |"), "Should show index 0");
        assertTrue(result.contains(" 1 |"), "Should show index 1");

        assertTrue(result.endsWith("---------------------------\n"), "Should end with separator");
    }

    @Test
    void testToStringWithDifferentCardVisibility() {
        card1.setVisible(true);
        card2.setVisible(false);
        card3.setVisible(true);

        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        String result = hand.toString();

        assertTrue(result.contains("Creature:"), "Should show creature info for visible cards");
        assertTrue(result.contains("Card is face down"), "Should show face down message for hidden cards");
        assertTrue(result.contains("Total cards: 3"), "Should show correct total");
    }

    @Test
    void testEdgeCaseRemoveLastCard() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card removed = hand.removeCard(2);
        assertEquals(card3, removed);
        assertEquals(2, hand.getSize());

        assertTrue(hand.addCard(card4));
        assertEquals(3, hand.getSize());
    }

    @Test
    void testEdgeCaseRemoveFirstCard() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);

        Card removed = hand.removeCard(0);
        assertEquals(card1, removed);
        assertEquals(2, hand.getSize());

        String result = hand.toString();
        assertTrue(result.contains(" 0 |"), "Should still have index 0");
        assertTrue(result.contains(" 1 |"), "Should still have index 1");
        assertFalse(result.contains(" 2 |"), "Should not have index 2");
    }

    @Test
    void testConsistentStateAfterMixedOperations() {
        hand.addCard(card1);
        hand.addCard(card2);
        hand.addCard(card3);
        hand.addCard(card4);

        assertEquals(4, hand.getSize());
        assertFalse(hand.isFull());

        hand.playCard(2);
        assertEquals(3, hand.getSize());

        hand.addCard(card5);
        hand.addCard(card6);
        assertEquals(5, hand.getSize());
        assertTrue(hand.isFull());

        hand.removeCard(0);
        assertEquals(4, hand.getSize());
        assertFalse(hand.isFull());

        Card newCard = new Card(new Creature("TestCreature", 1, 1));
        assertTrue(hand.addCard(newCard));
        assertTrue(hand.isFull());
    }
}