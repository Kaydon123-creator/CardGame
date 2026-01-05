package part1;

import java.util.ArrayList;
import java.util.List;

import enums.Event;
import interfaces.*;

public class Creature implements Fighter, Displayable, Publisher {
    static int MAX_DAMAGE = 100;
    static int MIN_HEALTH = 0;
    protected final String name;
    protected int damage;
    protected int health;
    private List<Subscriber> subscribers;

    // TODO: Constructeur principal
    public Creature(String name, int damage, int health) {
        this.name = name;
        this.damage = damage;
        this.health = health;

        subscribers = new ArrayList<Subscriber>();
    }

    // TODO: Constructeur par copie
    public Creature(Creature other) {
        this(other.name, other.damage, other.health);
    }

    // TODO: getName()
    // - Retourne le nom de la créature
    public String getName() {
        return name;
    }

    // TODO: getDamage()
    public int getDamage() {
        if (!isAlive()) {
            return 0;
        }

        return Math.min(MAX_DAMAGE, damage);
    }

    // TODO: getHealth()
    public int getHealth() {
        return Math.max(MIN_HEALTH, health);
    }

    // TODO: attackCreature(Creature other)
    // - Inflige ses dégâts à la cible
    // - Si la cible survit, elle riposte et inflige ses dégâts à l’attaquant
    public void attackCreature(Creature otherCreature) {
        otherCreature.takeDamage(this.getDamage());

        if (otherCreature.isAlive()) {
            this.takeDamage(otherCreature.getDamage());
        }
    }

    // TODO: takeDamage(int)
    // - Applique des dégâts entrants (positifs)
    public void takeDamage(int damage) {
        modifyHealth(damage);
        notify(Event.CREATURE_TAKE_DAMAGE, this);
    }

    // TODO: modifyHealth(int)
    // Diminue health de la valeur passée
    // la valeur passée peut etre positive ou negative
    protected void modifyHealth(int healthValue) {
        health -= healthValue;
    }

    // TODO: isAlive()
    public boolean isAlive() {
        return health > 0;
    }

    // TODO: getOverkill()
    // - Retourne les dégâts excédentaires reçus (dégâts subit sous 0 PV)
    //   Ex: health = -7 => overkill = 7 ; health >= 0 => 0
    public int getOverkill() {
        if (health > 0) {
            return 0;
        }
        return -health;
    }

    // TODO: getSpecies()
    protected String getSpecies() {
        return "Creature";
    }

    // TODO: toString()
    // - Représentation textuelle: espèce, nom, dégâts, PV bruts
    public String toString() {
        return String.format("[%-10s] Name: %-19s | Damage: %-4d | Health: %-4d \n", getSpecies(), getName(), getDamage(), getHealth());
    }

    // TODO: display()
    public void display() {
        System.out.print(this);
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void notify(Event event, Object ...args) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(event, args);
        }
    }
}
