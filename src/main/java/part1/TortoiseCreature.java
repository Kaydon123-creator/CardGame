package part1;

public class TortoiseCreature extends Creature {
    private int armor;
    private final float reduction;

    // TODO: Constructeur principal
    public TortoiseCreature(String name, int damage, int health, int armor, float reduction) {
        super(name, damage, health);
        this.armor = armor;
        this.reduction = reduction;
    }

    // TODO: Constructeur par copie
    // - Copie un autre TortoiseCreature avec son état
    public TortoiseCreature(TortoiseCreature other) {
        this(other.name, other.damage, other.health, other.armor, other.reduction);
    }

    // TODO: getArmor()
    public int getArmor() {
        return armor;
    }

    // TODO: addArmor(int)
    public void addArmor(int amount) {
        armor += amount;
    }

    // TODO: setArmor(int)
    public void setArmor(int newArmor) {
        armor = newArmor;
    }


    // TODO: toString()
    public String toString() {
        return String.format("[%-10s] Name: %-19s | Damage: %-4d | Health: %-4d| Armor: %-5d | Reduction: %.2f\n", getSpecies(), getName(), getDamage(), getHealth(), getArmor(), reduction);
    }

    // TODO: getSpecies()
    public String getSpecies() {
        return "Tortoise";
    }

    @Override
    public void takeDamage(int damage) {
        int armorDamageTaken = Math.round(damage * reduction);
        armorDamageTaken = Math.min(armor, armorDamageTaken);

        //le test utilise armor negatif donc on doit considérer ce cas:
        armorDamageTaken = Math.max(armorDamageTaken, 0);

        int damageTaken = damage - armorDamageTaken;
        modifyHealth(damageTaken);
        addArmor(-armorDamageTaken);
    }
}
