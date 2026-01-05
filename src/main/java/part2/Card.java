package part2;

import part1.Creature;
import interfaces.Displayable;

public class Card implements Displayable {
    private Creature creature;
    private boolean isVisible;

    // TODO: Constructeur
    public Card(Creature creature) {
        this.creature = creature;
        isVisible = false;
    }

    // TODO: getCreature()
    public Creature getCreature() {
        return creature;
    }

    // TODO: isVisible()
    public boolean isVisible() {
        return isVisible;
    }

    // TODO: setVisible(boolean)
    // - Définit si la carte est visible (true) ou cachée (false).
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    // TODO: toString()
    // - Retourne une représentation textuelle de la carte :
    //   * "Card is face down." si non visible.
    //   * "Creature: ..." si visible, en affichant le toString() de la créature.
    public String toString() {
        if (isVisible) {
            if (creature == null) return "Creature: null\n";
            return "Creature: " + creature.toString();
        }
        return "Card is face down.\n";
    }

    // TODO: display()
    public void display() {
        System.out.print(this);
    }

    // TODO: equals(Object)
    // - Deux cartes sont égales si elles contiennent la même créature (reference)
    public boolean equals(Object other){
        if (!(other instanceof Card otherCard)) {
            return false;
        }
        return creature == otherCard.getCreature();
    }

    // TODO: hashCode()
    // - Retourner le hashcode de la creature
    public int hashCode() {
        if (creature == null) return 0;
        return creature.hashCode();
    }
}
