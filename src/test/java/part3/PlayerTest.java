package part3;

import org.junit.jupiter.api.*;

import enums.Event;
import interfaces.Subscriber;
import part1.Creature;
import part2.Card;
import part2.Hand;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

     /** Subscriber de test qui enregistre les notifications reçues. */
    static class RecordingSubscriber implements Subscriber {
        int updateCount = 0;
        Event lastEvent = null;
        Object[] lastArgs = null;

        @Override
        public void update(Event event, Object... args) {
            updateCount++;
            lastEvent = event;
            lastArgs = args;
        }
    }

    /**
     * Creature instrumentée pour vérifier la propagation de add/removeSubscriber
     * depuis Player vers les créatures du deck.
     */
    static class ObservableCreature extends Creature {
        int addCalls = 0;
        int removeCalls = 0;
        final List<Subscriber> localSubs = new ArrayList<>();

        public ObservableCreature(String name, int damage, int health) {
            super(name, damage, health);
        }

        @Override
        protected String getSpecies() { return "Obs"; }

        @Override
        public void addSubscriber(Subscriber s) {
            addCalls++;
            localSubs.add(s);
        }

        @Override
        public void removeSubscriber(Subscriber s) {
            removeCalls++;
            localSubs.remove(s);
        }

        @Override
        public void notify(Event event, Object... args) {
            localSubs.forEach(s -> s.update(event, args));
        }
    }

    static class PlayerStub extends Player {
        int lastIndex = -1;
        PlayerLand lastLand = null;
        Optional<Consumer<Card>> lastBeforeAdd = Optional.empty();

        public PlayerStub(String name) {
            super(name);
        }

        @Override
        public void playCard(int index, PlayerLand land, Optional<java.util.function.Consumer<Card>> beforeAdd) {
            this.lastIndex = index;
            this.lastLand = land;
            this.lastBeforeAdd = beforeAdd;
        }
    }
    
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Alice");
    }

    private Card mkCard(String name, int dmg, int hp) {
        return new Card(new Creature(name, dmg, hp));
    }

    private List<Card> mkCards(int n, String prefix) {
        List<Card> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(mkCard(prefix + i, 10 + i, 20 + i));
        return out;
    }

    @Test
    void constructorInitialState() {
        assertEquals("Alice", player.getName());
        assertEquals(Player.INITIAL_LIFE_POINTS, player.getLifePoints(), "PV initiaux incorrects");
        // Par design, isAlive() est false si deck ET main sont vides (même avec PV > 0)
        assertFalse(player.isAlive(), "Un joueur sans carte ne devrait pas être 'vivant'");
    }

    @Test
    void addCardsToDeckAndDrawAtStartShouldDrawFiveVisible() {
        player.addCardsToDeck(mkCards(10, "C"));
        player.drawCard(true); // début de partie → 5 cartes

        // Après pioche d’ouverture, le joueur doit être "vivant"
        assertTrue(player.isAlive());

        // Vérifie via toString (hand size affichée)
        String s = player.toString();
        assertTrue(s.contains("[HAND SIZE]: 5"), "La main devrait contenir 5 cartes après la pioche d’ouverture");
        assertTrue(s.matches("(?s).*\\[DECK SIZE]:\\s*\\d+.*"), "Le deck size devrait être affiché");
    }

    @Test
    void drawWhenHandFullShouldDoNothing() {
        player.addCardsToDeck(mkCards(10, "C"));
        player.drawCard(true);  // main -> 5
        int before = player.getHandSize();
        player.drawCard(false); // main déjà pleine → rien ne doit se passer
        int after = player.getHandSize();
        assertEquals(before, after, "La main ne doit pas changer si elle est déjà pleine");
    }

    @Test
    void isAliveRequiresCardsOrDeck() {
        // Au départ, faux (pas de cartes)
        assertFalse(player.isAlive());

        // Ajoute des cartes au deck, puis pioche 1 → devient vrai
        player.addCardsToDeck(mkCards(1, "C"));
        player.drawCard(false);
        assertTrue(player.isAlive(), "Après avoir pioché, le joueur doit être vivant");
    }

    @Test
    void playCardSuccessShouldMoveFromHandToLandAndApplyBeforeAdd() {
        player.addCardsToDeck(mkCards(3, "C"));
        player.drawCard(false); // 1 carte en main

        PlayerLand land = new PlayerLand();
        player.playCard(0, land, Optional.of(card -> card.setVisible(false)));

        // La carte doit être sur le terrain, la main vide
        assertFalse(land.isEmpty(), "La carte devrait être sur le terrain");
        assertTrue(player.isHandEmpty(), "La main devrait être vide après avoir joué la carte");

        // Vérifie que la carte posée est bien face cachée
        Card c = land.getCardAt(0);
        assertNotNull(c);
        assertFalse(c.isVisible(), "La carte devrait être posée face cachée (beforeAdd appliqué)");
    }

    @Test
    void playCardInvalidIndexShouldNotThrowAndLeaveStateUnchanged() {
        player.addCardsToDeck(mkCards(2, "C"));
        player.drawCard(false); // 1 carte

        PlayerLand land = new PlayerLand();
        String before = player.toString();

        // Index invalide (ex: 5) -> doit être géré en gracefully
        assertDoesNotThrow(() -> player.playCard(5, land, Optional.empty()));

        String after = player.toString();
        assertEquals(before, after, "L'état du joueur ne doit pas changer après un index invalide");
        assertTrue(land.isEmpty(), "Le terrain ne doit pas changer après un index invalide");
    }

    @Test
    void playCardWhenLandFullShouldRollbackToHand() {
        // Main: 1 carte
        player.addCardsToDeck(mkCards(1, "H"));
        player.drawCard(false);

        // Terrain déjà plein
        PlayerLand land = new PlayerLand();
        land.addCard(mkCard("A0", 10, 10));
        land.addCard(mkCard("A1", 10, 10));
        land.addCard(mkCard("A2", 10, 10));
        assertTrue(land.isFull());

        // Tente de poser → doit échouer et remettre dans la main
        player.playCard(0, land, Optional.empty());

        assertFalse(player.isHandEmpty(), "La carte doit être rendue à la main (rollback)");
        assertTrue(land.isFull(), "Le terrain reste plein, aucune carte ajoutée");
    }

    @Test
    void equalsAndHashCodeByName() {
        Player p1 = new Player("Alice");
        Player p2 = new Player("Alice");
        Player p3 = new Player("Bob");

        assertEquals(p1, p2, "Deux joueurs avec le même nom doivent être égaux");
        assertEquals(p1.hashCode(), p2.hashCode(), "hashCode doit être cohérent avec equals");
        assertNotEquals(p1, p3);
    }

        @Test
    void toStringWhenEmptyWouldShowZeroHandAndDeck() {
        String expected = String.format("[%-15s] [HP]: %-5d [HAND SIZE]: %-5d [DECK SIZE]: %-5d\n",
                "Alice", Player.INITIAL_LIFE_POINTS, 0, 0);

        assertEquals(expected, player.toString(),
                "L’affichage doit correspondre exactement au format attendu (joueur vide)");
    }

    @Test
    void toStringAfterAddingDeckAndDrawingShouldUpdateCounts() {
        // Ajoute 3 cartes au deck et pioche 2
        player.addCardsToDeck(mkCards(3, "X"));
        player.drawCard(false); // pioche 1
        player.drawCard(false); // pioche 1 (total main=2, deck=1)

        String expected = String.format("[%-15s] [HP]: %-5d [HAND SIZE]: %-5d [DECK SIZE]: %-5d\n",
                "Alice", Player.INITIAL_LIFE_POINTS, 2, 1);

        assertEquals(expected, player.toString(),
                "Après avoir pioché, l’affichage doit refléter la taille de la main et du deck");
    }

    @Test
    void toStringAfterLifePointChangeShouldShowUpdatedHp() {
        player.setLifePoints(750);

        String expected = String.format("[%-15s] [HP]: %-5d [HAND SIZE]: %-5d [DECK SIZE]: %-5d\n",
                "Alice", 750, 0, 0);

        assertEquals(expected, player.toString(),
                "L’affichage doit refléter la valeur modifiée des points de vie");
    }

    private List<Card> makeCardsWithObservableCreatures(int n) {
        List<Card> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new Card(new ObservableCreature("C" + i, 10 + i, 20 + i)));
        }
        return list;
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void invokeCardCallsPlayCardAndNotifies() {
        provideInput("2\n"); // index choisi = 2
        PlayerStub p = new PlayerStub("Alice");
        p.setScanner(new Scanner(System.in));
        RecordingSubscriber sub = new RecordingSubscriber();
        p.addSubscriber(sub);
        PlayerLand land = new PlayerLand();

        p.invokeCard(land);

        // Vérifie que playCard a été appelé avec les bons arguments
        assertEquals(2, p.lastIndex);
        assertEquals(land, p.lastLand);
        assertEquals(Optional.empty(), p.lastBeforeAdd);

        // Vérifie que notify a été émis avec le bon Event et la main
        assertEquals(Event.INVOKE_CREATURE, sub.lastEvent);
        assertSame(p.getHandSize(), ((Hand) sub.lastArgs[0]).getSize()); // args[0] est bien la main
    }

    @Test
    void placeCardCallsPlayCardAndNotifies() {
        provideInput("1\n"); // index choisi = 1
        PlayerStub p = new PlayerStub("Bob");
        p.setScanner(new Scanner(System.in));
        RecordingSubscriber sub = new RecordingSubscriber();
        p.addSubscriber(sub);
        PlayerLand land = new PlayerLand();

        p.placeCard(land);

        // Vérifie que playCard a été appelé avec l’Optional contenant un consumer
        assertEquals(1, p.lastIndex);
        assertEquals(land, p.lastLand);
        assertTrue(p.lastBeforeAdd.isPresent(), "placeCard doit fournir un Consumer dans beforeAdd");

        // Vérifie que notify a été émis avec le bon Event et la main
        assertEquals(Event.PLACE_CARD, sub.lastEvent);
        assertNotNull(sub.lastArgs);
        assertTrue(sub.lastArgs[0] instanceof Hand);
    }

    @Test
    void returnCardSetsCardVisibleAndNotifies() {
        provideInput("0\n"); // index choisi = 0
        Player p = new Player("Carol");
        p.setScanner(new Scanner(System.in));
        RecordingSubscriber sub = new RecordingSubscriber();
        p.addSubscriber(sub);

        PlayerLand land = new PlayerLand();
        Card c = new Card(new Creature("Hidden", 10, 10));
        c.setVisible(false);
        land.addCard(c);
        p.returnCard(land);
        // Vérifie que la carte a été rendue visible
        assertTrue(c.isVisible(), "returnCard doit rendre la carte visible");
        // Vérifie que notify a été émis avec le bon Event et le terrain
        assertEquals(Event.RETURN_CARD, sub.lastEvent);
        assertEquals(land, sub.lastArgs[0]);
    }

    @Test
    void addSubscriberShouldRegisterOnPlayerAndPropagateToAllCreaturesInDeck() {
        // Arrange : 3 cartes avec créatures observables dans le deck
        var cards = makeCardsWithObservableCreatures(3);
        player.addCardsToDeck(cards);

        var sub = new RecordingSubscriber();

        // Act
        player.addSubscriber(sub);

        // Assert : chaque créature du deck a bien reçu addSubscriber()
        for (Card c : cards) {
            ObservableCreature oc = (ObservableCreature) c.getCreature();
            assertEquals(1, oc.addCalls, "addSubscriber doit être propagé à chaque créature du deck");
            assertEquals(0, oc.removeCalls, "removeSubscriber ne doit pas être appelé ici");
        }

        // Et notify(player) doit bien atteindre le subscriber
        player.notify(Event.DRAW_CARD, player.getHandSize());
        assertEquals(1, sub.updateCount, "Le subscriber doit recevoir la notification du Player");
        assertEquals(Event.DRAW_CARD, sub.lastEvent);
        assertNotNull(sub.lastArgs);
    }

    @Test
    void removeSubscriberShouldUnregisterFromPlayerAndPropagateRemovalToCreatures() {
        // Arrange
        var cards = makeCardsWithObservableCreatures(2);
        player.addCardsToDeck(cards);
        var sub = new RecordingSubscriber();
        player.addSubscriber(sub);

        // Act
        player.removeSubscriber(sub);

        // Assert : removeSubscriber propagé aux créatures du deck
        for (Card c : cards) {
            ObservableCreature oc = (ObservableCreature) c.getCreature();
            assertEquals(1, oc.addCalls, "addSubscriber doit avoir été appelé une fois avant");
            assertEquals(1, oc.removeCalls, "removeSubscriber doit être propagé à chaque créature");
        }

        // Et il ne doit plus recevoir de notifications du Player
        int before = sub.updateCount;
        player.notify(Event.INVOKE_CREATURE, "test");
        assertEquals(before, sub.updateCount, "Le subscriber retiré ne doit plus recevoir de notifications");
    }

    @Test
    void addSubscriberTwiceThenRemoveOnceShouldKeepOneRegistrationOnCreatures() {
        // Arrange
        var cards = makeCardsWithObservableCreatures(1);
        player.addCardsToDeck(cards);
        var sub = new RecordingSubscriber();

        // ajouter deux fois
        player.addSubscriber(sub);
        player.addSubscriber(sub);
        // puis retirer une fois
        player.removeSubscriber(sub);

        // Assert : sur la créature, on a vu 2 add + 1 remove
        ObservableCreature oc = (ObservableCreature) cards.get(0).getCreature();
        assertEquals(2, oc.addCalls, "Deux ajouts de subscriber doivent être propagés");
        assertEquals(1, oc.removeCalls, "Un retrait doit être propagé");

        // Et le subscriber reste encore enregistré une fois côté Player :
        // on ne peut pas lire la liste interne, mais on peut vérifier via notify()
        player.notify(Event.PLACE_CARD, 42);
        assertEquals(1, sub.updateCount,
                "Après 2 ajouts et 1 retrait, le subscriber doit encore recevoir les notifications (1 occurrence restante)");
    }
}
