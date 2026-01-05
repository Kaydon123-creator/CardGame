package part3;
import java.util.ArrayList;
import java.util.List;

import interfaces.Displayable;
import part2.Card;

public class PlayerLand implements Displayable {
    static final int MAX_CARDS = 3; // nombre maximum de cartes posées en jeu
    private List<Card> cardsInPlay;
    private Player owner;
    // TODO: CONSTRUCTEUR
    // - Initialise une nouvelle liste vide pour stocker les cartes posées.
    public PlayerLand() {
        this.cardsInPlay = new ArrayList<>();
    }

    public Player getOwner(){
        return owner;
    }

    public void setOwner(Player p){
        owner = p;
    }

    public int getSize(){
        return cardsInPlay.size();
    }

    // TODO: addCard(Card)
    // ajoute une carte sur le terrain
    // retourne si la carte a pu être ajoutée avec succès sur le terrain
    public boolean addCard(Card card) {
        if (isFull()) {
            return false;
        }

        cardsInPlay.add(card);
        return true;
    }

    // TODO: removeCard(Card)
    // - Retire une carte du terrain
    // - Retourne si la carte a bien été retirée avec succès
    public boolean removeCard(Card card) {
        return cardsInPlay.remove(card);
    }

    // TODO: getCardAt(int index)
    // Retourne la carte à la position donnée.
    public Card getCardAt(int index){
        if (index < 0 || index >= getSize()) return null;
        return cardsInPlay.get(index);
    }

    // TODO: isEmpty()
    // Retourne si aucune carte n’est posée sur le terrain.
    public boolean isEmpty(){
        return cardsInPlay.isEmpty();
    }

    // TODO: isFull()
    // Retourne si le terrain est sâturé
    public boolean isFull(){
        return getSize() >= MAX_CARDS;
    }

    // TODO: isAnyCardHidden()
    // - Vérifie si au moins une carte posée est à face cachée
    public boolean isAnyCardHidden(){
        for (int i = 0; i < getSize(); i++) {
            if (!cardsInPlay.get(i).isVisible()) {
                return true;
            }
        }

        return false;
    }

    // TODO: toString()
    // Représentation textuelle du terrain.
    // consuler les tests pour comprendre le format demandé

    public String toString() {
        String format = "--------------------------------------------- %s'S LAND ---------------------------------------------\n" +
                "Total cards: %d\n" +
                "-------------------------------------------------------------------------------------------------------------------\n";

        StringBuilder result = new StringBuilder(String.format(format, getOwner().getName().toUpperCase(), getSize()));
        for (int i = 0; i < cardsInPlay.size(); i++) {
            result.append(String.format("%2d | %-10s", i, cardsInPlay.get(i).toString()));
        }

        result.append("-------------------------------------------------------------------------------------------------------------------\n");
        return result.toString();
    }

    // TODO: display()
    // affiche le terrain du joueur
    public void display() {
        System.out.print(this);
    }
}
