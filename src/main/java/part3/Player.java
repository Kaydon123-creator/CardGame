package part3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

import enums.Event;
import interfaces.Displayable;
import interfaces.Publisher;
import interfaces.Subscriber;
import part2.*;

public class Player implements Displayable, Publisher {
    static final int INITIAL_LIFE_POINTS = 1000;
    private String name;
    private int lifePoints;
    private final Deck deck;
    private final Hand hand;
    private final List<Subscriber> subscribers;
    private Scanner scanner;

    // TODO: CONSTRUCTEUR
    public Player(String name) {
        deck = new Deck();
        hand = new Hand();
        this.name = name;

        lifePoints = INITIAL_LIFE_POINTS;

        subscribers = new ArrayList<Subscriber>();
    }

    // TODO : setter pour le scanner
    public void setScanner(Scanner sc){
        scanner = sc;
    }

    // TODO: GETTER name
    // - Retourne le nom du joueur.
    public String getName() {
        return name;
    }

    // TODO: GETTER lifePoints
    // - Retourne les points de vie actuels du joueur.
    public int getLifePoints() {
        return lifePoints;
    }

    // TODO: SETTER lifePoints
    // - Met à jour les points de vie.
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    // TODO : retourner si le joueur est en vie
    public boolean isAlive() {
        return lifePoints > 0 && (!isHandEmpty() || !deck.isEmpty());
    }

    // TODO
    // - Ajoute toutes les cartes fournies au deck du joueur.
    // - Mélange le deck après ajout.
    public void addCardsToDeck(List<Card> cards) {
        for (Card card : cards) {
            deck.addCard(card);
        }

        deck.shuffle();
    }

    // TODO
    // Retourne si la main a atteint sa capacité maximale.
    public boolean isHandFull(){
        return hand.isFull();
    }

    // TODO
    // - Retourne si la main ne contient aucune carte.
    public boolean isHandEmpty(){
        return hand.isEmpty();
    }

    // TODO
    // - Pioche des cartes du deck vers la main
    // - 5 cartes pour le premier tour, sinon 1 seule
    // - Les cartes piochees doivent etre mises VISIBLE

    public void drawCard(boolean isBeginningPlay) {
        if (isBeginningPlay) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.drawCard();
                if (card == null) return;
                card.setVisible(true);
                hand.addCard(card);
            }
        }
        else {
            Card card = deck.drawCard();
            if (card == null) return;
            card.setVisible(true);
            hand.addCard(card);
            notify(Event.DRAW_CARD, hand);
        }
    }

    // TODO
    // Tente d’ajouter la carte à un index specifique de la main du joueur vers sur le terrain :
    // Applique un effet en parametre sur la carte
    public void playCard(int index, PlayerLand land, Optional<Consumer<Card>> beforeAdd) {
        if (land.isFull()) return;
        if (index < 0 || index >= hand.getSize()) return;

        Card card = hand.playCard(index);

        if (beforeAdd.isPresent()) {
            beforeAdd.get().accept(card);
        }

        land.addCard(card);
        notify(Event.PLAYER_LAND_CHANGED, land);
    }

    // TODO
    // - Pose la carte d'index spécifiée par l'utilisateur à face visible sur le terrain
    public void invokeCard(PlayerLand land) {
        int index = scanner.nextInt();
        playCard(index, land, Optional.empty());
        notify(Event.INVOKE_CREATURE, hand);
    }

    // TODO
    // - Pose la carte d'index spécifiée par l'utilisateur à face cachée sur le terrain si possilbe
    public void placeCard(PlayerLand land) {
        int index = scanner.nextInt();
        playCard(index, land, Optional.of(card -> card.setVisible(false)));
        notify(Event.PLACE_CARD, hand);
    }

    // TODO
    // Retourne une carte (mettre à face visible) sur le terrain à l'index spécifiée par l'utilisateur
    // Prompt : Choose the index of the card to return
    public void returnCard(PlayerLand land){
        System.out.println("Choose the index of the card to return");
        int index = scanner.nextInt();

        Card card = land.getCardAt(index);
        card.setVisible(true);

        notify(Event.RETURN_CARD, land);
    }

    // TODO
    // retourne la taille actuelle de la main du joueur
    public int getHandSize() {
        return hand.getSize();
    }

    // TODO: toString()
    // - Représentation texte du joueur: nom, PV, taille de main, taille de deck.
    // - consulter les tests pour le format précis
    public String toString() {
        return String.format("[%-15s] [HP]: %-5d [HAND SIZE]: %-5d [DECK SIZE]: %-5d\n", getName(), getLifePoints(), getHandSize(), deck.getSize());
    }

    // TODO
    // - Deux Player sont égaux s’ils ont le même nom.
    public boolean equals(Object other){
        if (!(other instanceof Player otherPlayer)) {
            return false;
        }

        return getName().equals(otherPlayer.getName());
    }

    // TODO: showHand()
    // affiche la representation textuelle de la main
    public void showHand() {
        hand.display();
    }

    // TODO
    // retourne le hashcode du nom
    public int hashCode() {
        return name.hashCode();
    }

    //TODO : display()
    public void display() {
        System.out.println(this.toString());
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
        for (Card card : deck.getCards()) {
            card.getCreature().addSubscriber(subscriber);
        }
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
        for (Card card : deck.getCards()) {
            card.getCreature().removeSubscriber(subscriber);
        }
    }

    public void notify(Event event, Object ...args) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(event, args);
        }
    }
}
