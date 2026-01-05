package part2;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class Deck {
    private Stack<Card> cards;

    // TODO: Constructeur
    // - Initialise le deck comme une pile (Stack) vide de cartes.
    public Deck() {
        cards = new Stack<Card>();
    }

    // TODO: addCard(Card)
    // - Ajoute une carte à la pile (en haut du deck).
    public void addCard(Card card) {
        cards.add(card);
    }

    // TODO: drawCard()
    // - Retire la carte du dessus de la pile.
    public Card drawCard() {
        try {
            return cards.pop();
        }
        catch (EmptyStackException e) {
            return null;
        }
    }

    // TODO: getSize()
    // - Retourne le nombre actuel de cartes dans le deck.
    public int getSize() {
        return cards.size();
    }

    // TODO: isEmpty()
    // - Retourne le deck est vide
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Stack<Card> getCards(){
        return cards;
    }

    // TODO: shuffle()
    // - Mélange les cartes avec un riffle shuffle (coupe en deux parties + entremêle).
    // - Étapes:
        //   1. Couper la pile en deux listes (left et right).
        //   2. Alterner les retraits de manière à ce que le retrait des 2 côtés de la pile soit équiprobable
        //   3. Reconstruire la pile mélangée.
    public void shuffle() {
        int milieuDuDeck = cards.size() / 2;
        List<Card> left = new ArrayList<Card>(cards.subList(0, milieuDuDeck));
        List<Card> right = new ArrayList<Card>(cards.subList(milieuDuDeck, cards.size()));

        cards.clear();

        while (!left.isEmpty() && !right.isEmpty()) {
            if (Math.random() < 0.5) {
                cards.add(left.removeLast());
            }
            else {
                cards.add(right.removeLast());
            }
        }

        if (left.isEmpty()) {
            cards.addAll(right);
        }
        else {
            cards.addAll(left);
        }
    }
}
