package part2;
import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    static final int MAX_CARDS = 5;

    // TODO: Constructeur
    // - Initialise la main comme une liste vide de cartes.
    public Hand() {
        cards = new ArrayList<Card>();
    }

    // TODO: addCard(Card)
    // - Ajoute une carte à la main
    // - Retourne si l’ajout réussit ou pas
    public boolean addCard(Card card) {
        if (isFull()) {
            return false;
        }

        cards.add(card);
        return true;
    }

    // TODO: removeCard(int index)
    // - Retire la carte à la position donnée et la retourne.
    public Card removeCard(int index) {
        if (index < 0 || index >= getSize()) throw new IndexOutOfBoundsException();
        return cards.remove(index);
    }

    // TODO: playCard(int index)
    // - Joue une carte depuis la main
    // - retourne la carte jouée
    public Card playCard(int index) {
        if (index < 0 || index >= getSize()) return null;
        return removeCard(index);
    }

    // TODO: isFull()
    // - Retourne true si la main est saturée
    public boolean isFull() {
        return cards.size() >= MAX_CARDS;
    }

    // TODO: toString()
    // - Représente la main sous forme de texte:
    //   * Nombre total de cartes
    //   * Chaque carte avec son index
    // - Consulter les tests pour le format spécifique
    public String toString() {
        StringBuilder result = new StringBuilder(String.format("------ HAND CONTENTS ------\nTotal cards: %d\n---------------------------\n", cards.size()));

        for (int i = 0; i < cards.size(); i++) {
            result.append(String.format(" %d | %s\n", i, cards.get(i).toString()));
        }

        result.append("---------------------------\n");

        return result.toString();
    }

    // TODO: isEmpty()
    // - Retourne true si la main est vide.
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // TODO: getSize()
    // - Retourne le nombre actuel de cartes dans la main.
    public int getSize() {
        return cards.size();
    }


    // TODO: display()
    public void display() {
        System.out.print(this);
    }
}
