package part1;

public class EagleCreature extends Creature {
    private final float dodgeChance;
    private final int maxDodgeStreak;
    private int dodgeStreak;

    // TODO: Constructeur
    // - Initialise avec une esquive max = 1 par défaut
    public EagleCreature(String name, int damage, int health, float dodgeChance) {
        this(name, damage, health, dodgeChance, 1);
    }


    // TODO: Constructeur principal
    public EagleCreature(String name, int damage, int health, float dodgeChance, int maxDodgeStreak) {
        super(name, damage, health);
        this.dodgeChance = dodgeChance;
        this.maxDodgeStreak = maxDodgeStreak;
        dodgeStreak = 0;
    }

    // TODO: Constructeur par copie
    public EagleCreature(EagleCreature other) {
        this(other.name, other.damage, other.health, other.dodgeChance, other.maxDodgeStreak);
    }

    // TODO: toString()
    public String toString() {
        return String.format("[%-10s] Name: %-19s | Damage: %-4d | Health: %-4d| Dodge chance: %.2f\n", getSpecies(), getName(), getDamage(), getHealth(), dodgeChance);
    }

    // TODO: getSpecies()
    public String getSpecies() {
        return "Eagle";
    }

    @Override
    public void takeDamage(int damage) {
        if (dodgeStreak < maxDodgeStreak && Math.random() < dodgeChance) {
            dodgeStreak++;
            return;
        }
        dodgeStreak = 0;
        super.takeDamage(damage);
    }
}
