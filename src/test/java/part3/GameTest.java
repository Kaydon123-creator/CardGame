package part3;

import enums.Event;
import interfaces.Subscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import part1.Creature;
import part2.Card;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
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

    /** Stub Player pour compter la propagation add/removeSubscriber. */
    static class PlayerStub extends Player {
        int addCalls = 0;
        int removeCalls = 0;

        public PlayerStub(String name) {
            super(name);
        }

        @Override
        public void addSubscriber(interfaces.Subscriber s) {
            addCalls++;
            // pas d'appel à super pour ne pas dépendre d'un deck ou des cartes
        }

        @Override
        public void removeSubscriber(interfaces.Subscriber s) {
            removeCalls++;
        }
    }
    private final InputStream originalSystemIn = System.in;
    private Game game;
    private Player player1;
    private Player player2;
    private RecordingSubscriber sub;

    @BeforeEach
    void setUp() {
        game = new Game();
        player1 = new Player("Alice");
        player2 = new Player("Bob");
        sub = new RecordingSubscriber();
    }

    @AfterEach
    public void restoreSystemIn() {
        // Restore System.in after each test
        System.setIn(originalSystemIn);
    }

    @Test
    void testConstructorInitialState() {
        assertNull(getActivePlayer(game));
        assertTrue(isFirstTurn(game));
        assertNotNull(game.getPlayerLand());
    }

    @Test
    void testGetPlayerLand() {
        game.setPlayer1(player1);
        game.setPlayer2(player2);

        assertEquals(game.getPlayerLand(), getPlayerLand1(game));
        game.updateActivePlayer();
        assertEquals(game.getPlayerLand(), getPlayerLand2(game));
    }

    @Test
    void testUpdateActivePlayer() {
        game.setPlayer1(player1);
        game.setPlayer2(player2);

        assertTrue(isFirstTurn(game));
        game.updateActivePlayer();
        assertEquals(player2, getActivePlayer(game));
        assertFalse(isFirstTurn(game));
    }

    @Test
    void testAskYesNoYesResponse() {
        provideInput("1");
        Game g = new Game();
        boolean res = invokeAskYesNo(g);
        assertTrue(res);
    }

    @Test
    void testAskYesNoNoResponse() {
        provideInput("0");
        Game g = new Game();
        boolean res = invokeAskYesNo(g);
        assertFalse(res);
    }

    @Test
    void testSelectCardFromLand() {
        provideInput("0");
        Game g = new Game();
        PlayerLand land = new PlayerLand();
        Card c = new Card(new Creature("Dragon", 5, 5));
        land.addCard(c);

        Card chosen = invokeSelectCardFromLand(g, land);
        assertEquals(c, chosen);
    }

    @Test
    void drawDoesNothingWhenHandAlreadyFull() {
        // Préparer une main pleine (5 cartes)
        game.setPlayer1(player1);
        player1.addCardsToDeck(mkCards(10, "C"));
        player1.drawCard(true); // pioche d'ouverture -> 5

        // Aucun input nécessaire : draw() ne doit pas lire si main pleine
        invokePrivate(game, "draw");
        assertEquals(5, player1.getHandSize(), "La main devait rester pleine (5)");
    }

    @Test
    void drawStopsImmediatelyWhenUserAnswersNo() {
        // Réponse utilisateur: 0 (No)
        provideInput("0");
        Game g = new Game();
        g.setPlayer1(player1);
        // Deck avec des cartes, main vide
        player1.addCardsToDeck(mkCards(3, "C"));
        invokePrivate(g, "draw");
        assertEquals(0, player1.getHandSize(), "Aucune carte ne doit être piochée si l'utilisateur répond No immédiatement");
    }

    @Test
    void drawRepeatsUntilHandIsFullWhenUserKeepsSayingYes() {
        // Entrées: cinq fois "1" (Yes). Le do/while s'arrête quand la main est pleine.
        provideInput("1\n1\n1\n1\n1\n1");
        Game g = new Game();
        // 5 cartes nécessaires pour remplir la main (partant de 0)
        player1.addCardsToDeck(mkCards(15, "C"));
        g.setPlayer1(player1);
        invokePrivate(g, "draw");
        assertEquals(5, player1.getHandSize(), "La main doit être remplie jusqu'à la capacité maximale (5)");
    }

    @Test
    void drawCanTakeSingleCardWhenUserSaysYesThenNo() {
        // Réponses: Yes (1) puis No (0) => doit piocher exactement 1 carte
        provideInput("1\n0");
        Game g = new Game();
        player1.addCardsToDeck(mkCards(10, "C"));
        g.setPlayer1(player1);
        invokePrivate(g, "draw");
        assertEquals(1, player1.getHandSize(), "Une seule carte doit être piochée (Yes puis No)");
    }

    @Test
    void placeCardOneCardUnVisibleThenStopsOnNo() {
        // Entrées:
        // 1) askYesNo("place hidden ?") -> 1 (Yes)
        // 2) Player.placeCard -> demande index -> 0
        // 3) boucle -> askYesNo -> 0 (No) => stop
        provideInput("1\n0\n0\n");
        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        // Préparer la main (au moins 1 carte)
        p.addCardsToDeck(mkCards(3, "C"));
        // on pioche 1 carte manuellement
        p.drawCard(false);

        // Appel de la méthode privée
        invokePrivate(g, "placeCard");

        PlayerLand land = g.getPlayerLand();
        assertFalse(land.isEmpty(), "Après placeCard, le terrain ne doit pas être vide");
        Card c = land.getCardAt(0);
        assertNotNull(c, "Une carte doit avoir été posée");
        assertFalse(c.isVisible(), "La carte posée par placeCard doit être face cachée");
    }

    @Test
    void placeCardUntilLandIsFull() {
        provideInput("1\n0\n1\n0\n1\n0\n1\n0\n");
        Game g = new Game();
        g.setPlayer1(player1);

        // Préparer la main (au moins 1 carte)
        player1.addCardsToDeck(mkCards(5, "C"));
        // on pioche 5 carte
        player1.drawCard(true);

        // Appel de la méthode privée
        invokePrivate(g, "placeCard");

        PlayerLand land = g.getPlayerLand();
        assertFalse(land.isEmpty(), "Après placeCard, le terrain ne doit pas être vide");
        Card c1 = land.getCardAt(0);
        Card c2 = land.getCardAt(1);
        Card c3 = land.getCardAt(2);
        assertEquals(land.getSize(), PlayerLand.MAX_CARDS, "Le terrain doit contenir trois cartes");
        assertNotNull(c1, "Une carte doit avoir été posée");
        assertFalse(c1.isVisible(), "La carte posée par placeCard doit être face cachée");
        assertFalse(c2.isVisible(), "La carte posée par placeCard doit être face cachée");
        assertFalse(c3.isVisible(), "La carte posée par placeCard doit être face cachée");
        assertEquals(player1.getHandSize(), 2, "il doit rester deux cartes dans la main du joueur");
    }

    @Test
    void placeCardUntilHandIsEmpty() {
        provideInput("1\n0\n1\n0\n");
        Game g = new Game();
        g.setPlayer1(player1);

        // Préparer la main (au moins 1 carte)
        player1.addCardsToDeck(mkCards(5, "C"));
        // on pioche 5 carte
        player1.drawCard(false);

        // Appel de la méthode privée
        invokePrivate(g, "placeCard");

        PlayerLand land = g.getPlayerLand();
        assertFalse(land.isEmpty(), "Après placeCard, le terrain ne doit pas être vide");
        Card c1 = land.getCardAt(0);
        Card c2 = land.getCardAt(1);
        assertEquals(land.getSize(), 1, "Le terrain doit contenir une carte");
        assertNotNull(c1, "Une carte doit avoir été posée");
        assertNull(c2, "Il doit avoir une seule carte sur le terrain");
        assertFalse(c1.isVisible(), "La carte posée par placeCard doit être face cachée");
        assertTrue(player1.isHandEmpty(),"La main du joueur doit être vide");
    }


    @Test
    void invokeCardPlacesVisibleCard() {
        // 1) askYesNo -> 1 (Yes)
        // 2) index -> 0
        provideInput("1\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        p.addCardsToDeck(mkCards(2, "I"));
        p.drawCard(false); // 1 carte en main (index 0)

        invokePrivate(g, "invokeCard");

        PlayerLand land = g.getPlayerLand();
        assertFalse(land.isEmpty(), "Après invokeCard, le terrain ne doit pas être vide");
        Card c = land.getCardAt(0);
        assertNotNull(c);
        assertTrue(c.isVisible(), "La carte invoquée doit être face visible");
        assertEquals(0, p.getHandSize(), "La carte jouée ne doit plus être en main");
    }

    @Test
    void invokeCardDoesNothingWhenUserAnswersNo() {
        // askYesNo -> 0 (No)
        provideInput("0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        p.addCardsToDeck(mkCards(2, "I"));
        p.drawCard(false); // 1 carte en main

        invokePrivate(g, "invokeCard");

        PlayerLand land = g.getPlayerLand();
        assertTrue(land.isEmpty(), "Aucune carte ne doit être posée si l'utilisateur répond No");
        assertEquals(1, p.getHandSize(), "La main ne doit pas changer");
    }

    @Test
    void invokeCardDoesNothingWhenLandIsFull() {
        provideInput("1\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        // Terrain plein
        PlayerLand land = g.getPlayerLand();
        land.addCard(new Card(new Creature("A", 10, 10)));
        land.addCard(new Card(new Creature("B", 10, 10)));
        land.addCard(new Card(new Creature("C", 10, 10)));
        assertTrue(land.isFull());

        // Main avec 1 carte
        p.addCardsToDeck(mkCards(2, "I"));
        p.drawCard(false);

        invokePrivate(g, "invokeCard");

        assertTrue(land.isFull(), "Le terrain reste plein");
        assertEquals(1, p.getHandSize(), "Aucune carte ne doit être jouée si le terrain est plein");
    }

    @Test
    void invokeCardUntilLandIsFullStopsAutomatically() {
        provideInput("1\n0\n1\n0\n1\n0\n1\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        // 3 cartes en main pour remplir le terrain
        p.addCardsToDeck(mkCards(5, "I"));
        p.drawCard(true); // 5 cartes; on en jouera 3

        invokePrivate(g, "invokeCard"); // première invocation
        invokePrivate(g, "invokeCard"); // deuxième invocation
        invokePrivate(g, "invokeCard"); // troisième invocation → land plein

        PlayerLand land = g.getPlayerLand();
        assertFalse(land.isEmpty());
        assertNotNull(land.getCardAt(0));
        assertNotNull(land.getCardAt(1));
        assertNotNull(land.getCardAt(2));
        assertTrue(land.isFull(), "Le terrain doit être plein après 3 invocations");
    }

    @Test
    void invokeCardWithEmptyHandDoesNothing() {
        // askYesNo -> 1 (Yes), index -> 0 mais main vide
        provideInput("1\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        // Main vide, deck vide
        invokePrivate(g, "invokeCard");

        PlayerLand land = g.getPlayerLand();
        assertTrue(land.isEmpty(), "Sans carte en main, rien ne doit être joué");
    }

    @Test
    void returnCardRevealsHiddenCardAndStopsWhenNoHiddenLeft() {
        // 1) askYesNo -> 1 (Yes)
        // 2) index -> 0
        provideInput("1\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        PlayerLand land = g.getPlayerLand();
        Card hidden = new Card(new Creature("Hidden", 10, 20));
        hidden.setVisible(false);
        land.addCard(hidden);

        assertTrue(land.isAnyCardHidden(), "Précondition: au moins une carte cachée");

        invokePrivate(g, "returnCard");

        Card c = land.getCardAt(0);
        assertNotNull(c);
        assertTrue(c.isVisible(), "La carte doit être révélée");
        assertFalse(land.isAnyCardHidden(), "Il ne doit plus rester de carte cachée");
    }

    @Test
    void returnCardDoesNothingWhenUserAnswersNo() {
        // askYesNo -> 0 (No)
        provideInput("0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        PlayerLand land = g.getPlayerLand();
        Card hidden = new Card(new Creature("H", 10, 20));
        hidden.setVisible(false);
        land.addCard(hidden);

        invokePrivate(g, "returnCard");

        assertTrue(land.isAnyCardHidden(), "Aucune carte ne doit être révélée si l'utilisateur répond No");
        assertFalse(land.getCardAt(0).isVisible());
    }

    @Test
    void returnCardOnEmptyLandDoesNothing() {
        // Land vide → early return, pas de lecture d'entrée
        provideInput(""); // pas d'input

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        PlayerLand land = g.getPlayerLand();
        assertTrue(land.isEmpty());

        invokePrivate(g, "returnCard"); // ne dois pas planter

        assertTrue(land.isEmpty(), "Terrain vide inchangé");
    }

    @Test
    void returnCardRevealsMultipleHiddenUntilAllCardsAreVisible() {
        // On va révéler index 0 puis index 1 :
        // 1) askYesNo -> 1 (Yes) ; index -> 0
        // 2) askYesNo -> 1 (Yes) ; index -> 1
        provideInput("1\n0\n1\n1\n1\n2");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        PlayerLand land = g.getPlayerLand();
        Card h0 = new Card(new Creature("H0", 10, 20)); h0.setVisible(false);
        Card h1 = new Card(new Creature("H1", 10, 20)); h1.setVisible(false);
        Card h2 = new Card(new Creature("H2", 10, 20)); h1.setVisible(true);
        land.addCard(h0);
        land.addCard(h1);
        land.addCard(h2);

        invokePrivate(g, "returnCard");

        assertTrue(land.getCardAt(0).isVisible(), "La première carte doit être révélée");
        assertTrue(land.getCardAt(1).isVisible(), "La deuxième carte doit être révélée");
        assertFalse(land.isAnyCardHidden(), "Plus aucune carte cachée");
    }

    @Test
    void returnCardStopsEarlyWhenUserSaysNo() {
        provideInput("1\n0\n0\n");

        Game g = new Game();
        Player p = new Player("Alice");
        g.setPlayer1(p);

        PlayerLand land = g.getPlayerLand();
        Card h0 = new Card(new Creature("H0", 10, 20)); h0.setVisible(false);
        Card h1 = new Card(new Creature("H1", 10, 20)); h1.setVisible(false);
        land.addCard(h0);
        land.addCard(h1);

        invokePrivate(g, "returnCard");

        assertTrue(land.getCardAt(0).isVisible(), "La première carte a été révélée");
        assertFalse(land.getCardAt(1).isVisible(), "La deuxième doit rester cachée car l'utilisateur a dit No");
        assertTrue(land.isAnyCardHidden(), "Il reste au moins une carte cachée");
    }

    @Test
    void addSubscriberShouldRegisterInGameAndPropagateToPlayers() {
        RecordingSubscriber sub = new RecordingSubscriber();
        PlayerStub p1 = new PlayerStub("Alice");
        PlayerStub p2 = new PlayerStub("Bob");
        game.setPlayer1(p1);
        game.setPlayer2(p2);
        game.addSubscriber(sub);

        // Propagation vers les deux joueurs
        assertEquals(1, p1.addCalls, "addSubscriber doit être propagé à player1");
        assertEquals(1, p2.addCalls, "addSubscriber doit être propagé à player2");

        // Et le subscriber doit recevoir les notifications de Game
        game.notify(Event.NEW_TURN, p1, p2);
        assertEquals(1, sub.updateCount, "Le subscriber ajouté doit recevoir la notification");
        assertEquals(Event.NEW_TURN, sub.lastEvent);
        assertNotNull(sub.lastArgs);
        assertEquals(2, sub.lastArgs.length, "Les arguments doivent être relayés tels quels");
    }

    @Test
    void removeSubscriberShouldUnregisterFromGameAndPropagateToPlayers() {
        RecordingSubscriber sub = new RecordingSubscriber();
        PlayerStub p1 = new PlayerStub("Alice");
        PlayerStub p2 = new PlayerStub("Bob");
        game.setPlayer1(p1);
        game.setPlayer2(p2);
        game.addSubscriber(sub);
        game.removeSubscriber(sub);

        // Propagation du retrait vers les deux joueurs
        assertEquals(1, p1.removeCalls, "removeSubscriber doit être propagé à player1");
        assertEquals(1, p2.removeCalls, "removeSubscriber doit être propagé à player2");

        // Le subscriber retiré ne doit plus recevoir de notifications
        int before = sub.updateCount;
        game.notify(Event.PLAYER_LAND_CHANGED, game.getPlayerLand());
        assertEquals(before, sub.updateCount, "Le subscriber retiré ne doit plus recevoir de notifications");
    }

    @Test
    void notifyShouldReachAllCurrentSubscribersExactlyOnce() {
        RecordingSubscriber s1 = new RecordingSubscriber();
        RecordingSubscriber s2 = new RecordingSubscriber();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.addSubscriber(s1);
        game.addSubscriber(s2);

        game.notify(Event.CREATURE_DUEL_START, "land1", "land2");

        assertEquals(1, s1.updateCount, "notify doit appeler update une seule fois par abonné");
        assertEquals(Event.CREATURE_DUEL_START, s1.lastEvent);
        assertEquals(1, s2.updateCount, "notify doit appeler update une seule fois par abonné");
        assertEquals(Event.CREATURE_DUEL_START, s2.lastEvent);

        // Retirer s2 et renvoyer un autre événement
        game.removeSubscriber(s2);
        game.notify(Event.BATTLE_END, "winnerName");

        assertEquals(2, s1.updateCount, "s1 encore inscrit doit recevoir la seconde notification");
        assertEquals(1, s2.updateCount, "s2 retiré ne doit pas recevoir la seconde notification");
        assertEquals(Event.BATTLE_END, s1.lastEvent);
    }

    @Test
    void placeCardShouldNotifyPlayerLandChangedWhenUserSaysNo() {
        // askYesNo -> 0 (No)
        provideInput("0\n");
        Game g = new Game();
        g.setPlayer1(player1);
        g.setPlayer2(player2);
        g.addSubscriber(sub);
        invokePrivate(g, "placeCard");
        assertEquals(Event.PLAYER_LAND_CHANGED, sub.lastEvent);
        assertTrue(sub.lastArgs[0] instanceof PlayerLand);
    }

    @Test
    void invokeCardShouldNotifyPlayerLandChangedWhenUserSaysNo() {
        // askYesNo -> 0 (No)
        provideInput("0\n");
        Game g = new Game();
        g.setPlayer1(player1);
        g.setPlayer2(player2);
        g.addSubscriber(sub);
        invokePrivate(g, "invokeCard");
        assertEquals(Event.PLAYER_LAND_CHANGED, sub.lastEvent);
        assertTrue(sub.lastArgs[0] instanceof PlayerLand);
    }

    @Test
    void returnCardShouldNotifyPlayerLandChangedWhenUserSaysNo() {
        // askYesNo -> 0 (No)
        provideInput("0\n");
        // Préparer terrain avec une carte
        Game g = new Game();
        g.setPlayer1(player1);
        g.setPlayer2(player2);
        g.addSubscriber(sub);
        PlayerLand land = g.getPlayerLand();
        Card hidden = new Card(new Creature("Hidden", 10, 10));
        hidden.setVisible(false);
        land.addCard(hidden);
        invokePrivate(g, "returnCard");

        assertEquals(Event.PLAYER_LAND_CHANGED, sub.lastEvent);
        assertEquals(land, sub.lastArgs[0]);
    }

    @Test
    void attackShouldNotifyCreatureDuelStart() {
        // Réponses :
        // askYesNo("attack?") -> 1 (Yes)
        // index attaquant -> 0
        // index défenseur -> 0
        provideInput("1\n0\n0\n");
        Game g = new Game();
        g.setPlayer1(player1);
        g.setPlayer2(player2);
        g.addSubscriber(sub);
        // Préparer 1 carte par joueur
        PlayerLand land = g.getPlayerLand();
        land.addCard(new Card(new Creature("Attacker", 50, 50)));

        g.updateActivePlayer(); // switch to Bob
        PlayerLand oppLand = g.getPlayerLand();
        oppLand.addCard(new Card(new Creature("Defender", 50, 50)));

        invokePrivate(g, "attack");

        assertEquals(Event.CREATURE_DUEL_START, sub.lastEvent);
        assertEquals(2, sub.lastArgs.length);
        assertTrue(sub.lastArgs[0] instanceof PlayerLand);
        assertTrue(sub.lastArgs[1] instanceof PlayerLand);
    }

    @Test
    void launchShouldNotifyNewTurnAndBattleEnd() {
        // Donner un deck vide à player2 pour qu'il meure directement
        player2.setLifePoints(0);
        Game g = new Game();
        g.setPlayer1(player1);
        g.setPlayer2(player2);
        g.addSubscriber(sub);
        // éviter blocages → ne fournit pas d'entrée, juste test de notifications globales
        provideInput("");
        g.launch();
        // La dernière notification doit être BATTLE_END
        assertEquals(Event.BATTLE_END, sub.lastEvent);
        assertEquals(1, sub.lastArgs.length);
        assertTrue(sub.lastArgs[0] instanceof String);
    }


    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    private Player getActivePlayer(Game g) {
        try {
            var field = Game.class.getDeclaredField("activePlayer");
            field.setAccessible(true);
            return (Player) field.get(g);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isFirstTurn(Game g) {
        try {
            var field = Game.class.getDeclaredField("isFirstTurn");
            field.setAccessible(true);
            return (boolean) field.get(g);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PlayerLand getPlayerLand1(Game g) {
        try {
            var field = Game.class.getDeclaredField("playerLand1");
            field.setAccessible(true);
            return (PlayerLand) field.get(g);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PlayerLand getPlayerLand2(Game g) {
        try {
            var field = Game.class.getDeclaredField("playerLand2");
            field.setAccessible(true);
            return (PlayerLand) field.get(g);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeAskYesNo(Game g) {
        try {
            var m = Game.class.getDeclaredMethod("askYesNo", String.class);
            m.setAccessible(true);
            return (boolean) m.invoke(g, "Draw card?");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Card invokeSelectCardFromLand(Game g, PlayerLand land) {
        try {
            var m = Game.class.getDeclaredMethod("selectCardFromLand", String.class, PlayerLand.class);
            m.setAccessible(true);
            return (Card) m.invoke(g, "Pick card", land);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokePrivate(Game g, String functionName) {
        try {
            var m = Game.class.getDeclaredMethod(functionName);
            m.setAccessible(true);
            m.invoke(g);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Card> mkCards(int n, String prefix) {
        List<Card> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(new Card(new Creature(prefix + i, 10 + i, 20 + i)));
        return list;
    }
}
