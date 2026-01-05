package part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import part1.Creature;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

public class DeckTest {
    private Deck deck;
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        Creature creature1 = new Creature("Dragon", 100, 50);
        Creature creature2 = new Creature("Phoenix", 80, 60);
        Creature creature3 = new Creature("Griffin", 90, 55);
        Creature creature4 = new Creature("Unicorn", 70, 40);

        card1 = new Card(creature1);
        card2 = new Card(creature2);
        card3 = new Card(creature3);
        card4 = new Card(creature4);
    }

    @Test
    void testConstructor() {
        assertEquals(0, deck.getSize(), "New deck should be empty");
        assertTrue(deck.isEmpty(), "New deck should be empty");
    }

    @Test
    void testAddCard() {
        deck.addCard(card1);
        assertEquals(1, deck.getSize(), "Deck size should be 1 after adding one card");
        assertFalse(deck.isEmpty(), "Deck should not be empty after adding a card");

        deck.addCard(card2);
        assertEquals(2, deck.getSize(), "Deck size should be 2 after adding two cards");
    }

    @Test
    void testAddMultipleCards() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
        deck.addCard(card4);

        assertEquals(4, deck.getSize(), "Deck should have 4 cards");
        assertFalse(deck.isEmpty(), "Deck should not be empty");
    }

    @Test
    void testDrawCardFromEmptyDeck() {
        Card drawnCard = deck.drawCard();
        assertNull(drawnCard, "Drawing from empty deck should return null");
        assertEquals(0, deck.getSize(), "Size should remain 0");
        assertTrue(deck.isEmpty(), "Deck should still be empty");
    }

    @Test
    void testDrawCardFromNonEmptyDeck() {
        deck.addCard(card1);
        deck.addCard(card2);

        Card drawnCard = deck.drawCard();
        assertNotNull(drawnCard, "Drawn card should not be null");
        assertEquals(1, deck.getSize(), "Deck size should decrease by 1");

        assertEquals(card2, drawnCard, "Should draw the last added card (card2)");
    }

    @Test
    void testDrawAllCards() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);

        Card first = deck.drawCard();
        Card second = deck.drawCard();
        Card third = deck.drawCard();

        assertEquals(card3, first, "First drawn should be card3 (last added)");
        assertEquals(card2, second, "Second drawn should be card2");
        assertEquals(card1, third, "Third drawn should be card1 (first added)");

        assertTrue(deck.isEmpty(), "Deck should be empty after drawing all cards");
        assertEquals(0, deck.getSize(), "Size should be 0");
    }

    @Test
    void testDrawFromEmptyAfterDrawingAll() {
        deck.addCard(card1);
        deck.drawCard();

        Card drawnCard = deck.drawCard();
        assertNull(drawnCard, "Should return null when drawing from empty deck");
    }

    @Test
    void testGetSize() {
        assertEquals(0, deck.getSize(), "Initial size should be 0");

        deck.addCard(card1);
        assertEquals(1, deck.getSize(), "Size should be 1 after adding one card");

        deck.addCard(card2);
        assertEquals(2, deck.getSize(), "Size should be 2 after adding two cards");

        deck.drawCard();
        assertEquals(1, deck.getSize(), "Size should be 1 after drawing one card");

        deck.drawCard();
        assertEquals(0, deck.getSize(), "Size should be 0 after drawing all cards");
    }

    @Test
    void testIsEmpty() {
        assertTrue(deck.isEmpty(), "New deck should be empty");

        deck.addCard(card1);
        assertFalse(deck.isEmpty(), "Deck with cards should not be empty");

        deck.drawCard();
        assertTrue(deck.isEmpty(), "Deck should be empty after drawing all cards");
    }

    @Test
    void testShuffleEmptyDeck() {
        deck.shuffle();
        assertTrue(deck.isEmpty(), "Empty deck should remain empty after shuffle");
        assertEquals(0, deck.getSize(), "Size should remain 0 after shuffling empty deck");
    }

    @Test
    void testShuffleSingleCard() {
        deck.addCard(card1);
        deck.shuffle();

        assertEquals(1, deck.getSize(), "Size should remain 1 after shuffle");
        Card drawnCard = deck.drawCard();
        assertEquals(card1, drawnCard, "Single card should remain the same after shuffle");
    }

    @Test
    void testShufflePreservesAllCards() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
        deck.addCard(card4);

        deck.shuffle();

        assertEquals(4, deck.getSize(), "Shuffle should preserve deck size");

        Set<Card> drawnCards = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            Card card = deck.drawCard();
            assertNotNull(card, "All cards should be drawable after shuffle");
            drawnCards.add(card);
        }

        assertTrue(drawnCards.contains(card1), "card1 should be present after shuffle");
        assertTrue(drawnCards.contains(card2), "card2 should be present after shuffle");
        assertTrue(drawnCards.contains(card3), "card3 should be present after shuffle");
        assertTrue(drawnCards.contains(card4), "card4 should be present after shuffle");
        assertEquals(4, drawnCards.size(), "Should have exactly 4 unique cards");
    }

    @Test
    void testShuffleRandomness() {

        int nCards = 100;
        for (int i = 0; i < nCards; i++) {
            deck.addCard(new Card(new Creature("Creature_"+i,i,i)));
        }

        Card[] originalOrder = new Card[nCards];
        for (int i = nCards - 1; i >= 0; i--) {
            originalOrder[i] = deck.drawCard();
        }

        for (Card card : originalOrder) {
            deck.addCard(card);
        }

        deck.shuffle();

        Card[] shuffledOrder = new Card[nCards];
        for (int i = nCards - 1; i >= 0; i--) {
            shuffledOrder[i] = deck.drawCard();
        }

        boolean orderChanged = false;
        for (int i = 0; i < nCards; i++) {
            if (!originalOrder[i].equals(shuffledOrder[i])) {
                orderChanged = true;
                break;
            }
        }


        assertTrue(orderChanged, "Shuffle should change the order of cards (may rarely fail due to randomness)");
    }

    @Test
    void testMultipleShuffles() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
        deck.addCard(card4);

        System.out.println("Before shuffle - deck size: " + deck.getSize());

        deck.shuffle();
        System.out.println("After 1st shuffle - deck size: " + deck.getSize());

        deck.shuffle();
        System.out.println("After 2nd shuffle - deck size: " + deck.getSize());

        deck.shuffle();
        System.out.println("After 3rd shuffle - deck size: " + deck.getSize());

        assertEquals(4, deck.getSize(), "Multiple shuffles should preserve deck size");

        Set<Card> drawnCards = new HashSet<>();
        int cardCount = 0;
        while (!deck.isEmpty()) {
            Card drawnCard = deck.drawCard();
            System.out.println("Drew card: " + drawnCard);
            drawnCards.add(drawnCard);
            cardCount++;
        }

        System.out.println("Total cards drawn: " + cardCount);
        System.out.println("Unique cards in set: " + drawnCards.size());

        assertEquals(4, drawnCards.size(), "All cards should be present after multiple shuffles");
        assertTrue(drawnCards.contains(card1), "card1 should be present");
        assertTrue(drawnCards.contains(card2), "card2 should be present");
        assertTrue(drawnCards.contains(card3), "card3 should be present");
        assertTrue(drawnCards.contains(card4), "card4 should be present");
    }

    @Test
    void testSingleShuffleDebug() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
        deck.addCard(card4);

        System.out.println("Before shuffle - deck size: " + deck.getSize());

        deck.shuffle();

        System.out.println("After shuffle - deck size: " + deck.getSize());
        assertEquals(4, deck.getSize(), "Single shuffle should preserve deck size");

        int drawnCount = 0;
        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            System.out.println("Drew card " + (drawnCount + 1) + ": " + card);
            drawnCount++;
        }

        assertEquals(4, drawnCount, "Should be able to draw exactly 4 cards");
    }

    @Test
    void testAddAndDrawAfterShuffle() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.shuffle();

        deck.addCard(card3);
        deck.addCard(card4);

        assertEquals(4, deck.getSize(), "Should have 4 cards total");

        Card first = deck.drawCard();
        Card second = deck.drawCard();

        assertEquals(card4, first, "Should draw card4 first");
        assertEquals(card3, second, "Should draw card3 second");

        assertEquals(2, deck.getSize(), "Should have 2 cards remaining");
    }
}