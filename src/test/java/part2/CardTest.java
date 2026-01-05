package part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import part1.Creature;
import interfaces.Displayable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CardTest {
    private Card card;
    private Creature creature1;
    private Creature creature2;
    private Creature creature3;

    @BeforeEach
    void setUp() {
        creature1 = new Creature("Dragon", 100, 50);
        creature2 = new Creature("Phoenix", 80, 60);
        creature3 = new Creature("Griffin", 90, 55);

        card = new Card(creature1);
    }

    @Test
    void testConstructor() {
        assertNotNull(card.getCreature(), "Card should have a creature");
        assertEquals(creature1, card.getCreature(), "Card should contain the correct creature");
        assertFalse(card.isVisible(), "Card should initially be face down");
    }

    @Test
    void testConstructorWithNullCreature() {
        Card nullCard = new Card(null);
        assertNull(nullCard.getCreature(), "Card with null creature should have null creature");
        assertFalse(nullCard.isVisible(), "Card should still be face down");
    }

    @Test
    void testGetCreature() {
        assertEquals(creature1, card.getCreature(), "Should return the correct creature");

        Card card2 = new Card(creature2);
        assertEquals(creature2, card2.getCreature(), "Should return the correct creature for different card");
    }


    @Test
    void testIsVisible() {
        assertFalse(card.isVisible(), "Card should initially not be visible");
    }

    @Test
    void testSetVisible() {
        card.setVisible(true);
        assertTrue(card.isVisible(), "Card should be visible after setting to true");

        card.setVisible(false);
        assertFalse(card.isVisible(), "Card should not be visible after setting to false");
    }

    @Test
    void testToStringWhenNotVisible() {
        card.setVisible(false);
        assertEquals("Card is face down.\n", card.toString(),
                "Face down card should show generic message");
    }

    @Test
    void testToStringWhenVisible() {
        card.setVisible(true);
        String expected = String.format("Creature: %s", creature1.toString());
        assertEquals(expected, card.toString(),
                "Visible card should show creature information");
    }

    @Test
    void testToStringVisibilityToggle() {
        card.setVisible(false);
        assertEquals("Card is face down.\n", card.toString());

        card.setVisible(true);
        String expected = String.format("Creature: %s", creature1.toString());
        assertEquals(expected, card.toString());

        card.setVisible(false);
        assertEquals("Card is face down.\n", card.toString());
    }

    @Test
    void testToStringWithNullCreature() {
        Card nullCard = new Card(null);

        nullCard.setVisible(false);
        assertEquals("Card is face down.\n", nullCard.toString(),
                "Face down card with null creature should show generic message");

        nullCard.setVisible(true);
        assertEquals("Creature: null\n", nullCard.toString(),
                "Visible card with null creature should handle gracefully");
    }

    @Test
    void testEquals() {
        Card card2 = new Card(creature1);
        Card card3 = new Card(creature2);
        Card card4 = new Card(creature3);

        assertEquals(card, card, "Card should equal itself");

        assertEquals(card, card2, "Cards with same creature reference should be equal");
        assertEquals(card2, card, "Equality should be symmetric");

        assertNotEquals(card, card3, "Cards with different creatures should not be equal");
        assertNotEquals(card3, card, "Inequality should be symmetric");
        assertNotEquals(card, card4, "Cards with different creatures should not be equal");
        assertNotEquals(card3, card4, "Cards with different creatures should not be equal");
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(card, null, "Card should not equal null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(card, "Not a card", "Card should not equal string");
        assertNotEquals(card, creature1, "Card should not equal creature");
    }

    @Test
    void testEqualsIgnoresModeAndVisibility() {
        Card card2 = new Card(creature1);

        card.setVisible(true);

        card2.setVisible(false);

        assertEquals(card, card2, "Cards should be equal regardless of mode and visibility when they have the same creature");
    }

    @Test
    void testEqualsWithNullCreatures() {
        Card nullCard1 = new Card(null);
        Card nullCard2 = new Card(null);

        assertEquals(nullCard1, nullCard2, "Cards with null creatures should be equal");
        assertNotEquals(card, nullCard1, "Card with creature should not equal card with null creature");
    }

    @Test
    void testHashCode() {
        Card card2 = new Card(creature1);
        Card card3 = new Card(creature2);
        Card card4 = new Card(creature3);

        assertEquals(card.hashCode(), card2.hashCode(),
                "Equal cards should have equal hash codes");

        assertNotEquals(card.hashCode(), card3.hashCode(),
                "Cards with different creatures should have different hash codes");
        assertNotEquals(card.hashCode(), card4.hashCode(),
                "Cards with different creatures should have different hash codes");
    }

    @Test
    void testHashCodeConsistency() {
        int hash1 = card.hashCode();
        int hash2 = card.hashCode();
        assertEquals(hash1, hash2, "Hash code should be consistent across multiple calls");

        card.setVisible(true);
        int hash3 = card.hashCode();
        assertEquals(hash1, hash3, "Hash code should not change when mode/visibility changes");
    }

    @Test
    void testHashCodeWithNullCreature() {
        Card nullCard1 = new Card(null);
        Card nullCard2 = new Card(null);

        assertEquals(nullCard1.hashCode(), nullCard2.hashCode(),
                "Cards with null creatures should have equal hash codes");
    }


    @Test
    void testCardStateIndependence() {
        Card card2 = new Card(creature1);

        card.setVisible(true);

        assertFalse(card2.isVisible(),
                "Other card's visibility should not be affected");

        assertEquals(card, card2, "Cards should still be equal based on creature");
    }

    @Test
    void testCardWithSameCreatureReference() {
        Card card2 = new Card(creature1);

        assertEquals(card, card2, "Cards with same creature reference should be equal");
        assertEquals(card.hashCode(), card2.hashCode(),
                "Cards with same creature should have equal hash codes");
    }

    @Test
    public void testDisplayOffCard() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable card = new Card(creature1);
        card.display();
        assertEquals(outContent.toString(), card.toString());

        System.setOut(originalOut);
    }
}