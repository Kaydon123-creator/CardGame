package part3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import part1.Creature;
import interfaces.Displayable;
import part2.Card;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PlayerLandTest {


    private PlayerLand land;
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4; // pour tester dépassement

    @BeforeEach
    void setUp() {
        land = new PlayerLand();
        card1 = new Card(new Creature("A", 10, 20));
        card2 = new Card(new Creature("B", 15, 25));
        card3 = new Card(new Creature("C", 20, 30));
        card4 = new Card(new Creature("D", 25, 35));
    }

    @Test
    void constructorShouldStartEmpty() {
        assertTrue(land.isEmpty(), "Le terrain doit être vide à l’initialisation");
        assertFalse(land.isFull(), "Le terrain ne doit pas être plein au départ");
        assertNull(land.getOwner());
    }

    @Test
    void addCardShouldAddUntilMaxAndReturnFalseIfFull() {
        assertTrue(land.addCard(card1));
        assertTrue(land.addCard(card2));
        assertTrue(land.addCard(card3));

        // Maintenant le terrain est plein
        assertTrue(land.isFull(), "Après 3 cartes, le terrain doit être plein");

        // Ajout d'une 4e doit échouer
        assertFalse(land.addCard(card4), "Ajouter une carte au-delà de la capacité doit échouer");
    }

    @Test
    void removeCardShouldRemoveIfPresent() {
        land.addCard(card1);
        land.addCard(card2);

        assertTrue(land.removeCard(card1), "La carte ajoutée doit pouvoir être retirée");
        assertFalse(land.removeCard(card3), "Retirer une carte non présente doit retourner false");

        assertEquals(1, land.getSize(), "Après retrait, le terrain doit avoir 1 carte");
    }

    @Test
    void getCardAtShouldReturnCardOrNullIfInvalidIndex() {
        land.addCard(card1);
        land.addCard(card2);

        assertSame(card1, land.getCardAt(0), "Index 0 doit retourner card1");
        assertSame(card2, land.getCardAt(1), "Index 1 doit retourner card2");
        assertNull(land.getCardAt(-1), "Index négatif doit retourner null");
        assertNull(land.getCardAt(5), "Index hors borne doit retourner null");
    }

    @Test
    void isAnyCardHiddenShouldDetectHiddenCards() {
        card1.setVisible(true);
        card2.setVisible(false);

        land.addCard(card1);
        land.addCard(card2);

        assertTrue(land.isAnyCardHidden(), "Avec une carte cachée, doit retourner true");

        card2.setVisible(true);
        assertFalse(land.isAnyCardHidden(), "Avec toutes les cartes visibles, doit retourner false");
    }

    @Test
    void displayOneHiddenCard() {
        land.setOwner(new Player("Bob"));
        Card hidden = new Card(new Creature("Eagle",10, 30));
        hidden.setVisible(false);
        land.addCard(hidden);

        String expected =
                "--------------------------------------------- BOB'S LAND ---------------------------------------------\n" +
                "Total cards: 1\n" + 
                "-------------------------------------------------------------------------------------------------------------------\n"+
                " 0 | Card is face down.\n" +
                "-------------------------------------------------------------------------------------------------------------------\n";

        assertEquals(expected, land.toString(),
                "Une carte cachée doit s'afficher comme 'Card is face down.' avec l'index à gauche.");
    }

    @Test
    void displayOneVisibleCard() {
        land.setOwner(new Player("Bob"));
        Card visible = new Card(new Creature("Lion", 40, 150));
        visible.setVisible(true);
        land.addCard(visible);

        // Card.toString() quand visible → "Creature: <creature.toString()>"
        String cardLine = "Creature: " + visible.getCreature().toString();

        String expected =
                "--------------------------------------------- BOB'S LAND ---------------------------------------------\n" +
                "Total cards: 1\n" + 
                "-------------------------------------------------------------------------------------------------------------------\n"+
                " 0 | " + cardLine +
                "-------------------------------------------------------------------------------------------------------------------\n";

        assertEquals(expected, land.toString(),
                "Une carte visible doit afficher 'Creature: <toString de la créature>' avec l'index à gauche.");
    }

    @Test
    void displayMultipleCardsInOrder() {
        land.setOwner(new Player("Bob"));
        Card c0 = new Card(new Creature("Eagle", 30, 120));
        Card c1 = new Card(new Creature("Tortoise", 10, 200));
        Card c2 = new Card(new Creature("Lion", 40, 150));

        // Mêlons visible/cachée pour vérifier les deux formats
        c0.setVisible(true);   // visible
        c1.setVisible(false);  // cachée
        c2.setVisible(true);   // visible

        land.addCard(c0);
        land.addCard(c1);
        land.addCard(c2);

        String line0 = "Creature: " + c0.getCreature().toString();
        String line1 = "Card is face down.\n";
        String line2 = "Creature: " + c2.getCreature().toString();

        String expected =
                "--------------------------------------------- BOB'S LAND ---------------------------------------------\n" +
                "Total cards: 3\n" + 
                "-------------------------------------------------------------------------------------------------------------------\n"+
                String.format("%2d | %-10s", 0, line0) +
                String.format("%2d | %-10s", 1, line1) +
                String.format("%2d | %-10s", 2, line2) +
                "-------------------------------------------------------------------------------------------------------------------\n";

        assertEquals(expected, land.toString(),
                "Les cartes doivent être affichées dans l'ordre d'ajout, indexées 0..n-1, " +
                "avec 'Creature: ...' si visibles, sinon 'Card is face down.'");
    }

    @Test
    public void testDisplay() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable displayCreature = land;
        land.setOwner(new Player("Bob"));
        displayCreature.display();
        assertEquals(outContent.toString(), land.toString());

        System.setOut(originalOut);
    }
}
