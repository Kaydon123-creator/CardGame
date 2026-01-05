package part1;

public class LionCreature extends Creature {
    private int healing;
    private final int healthMax;

    // TODO: Constructeur principal
    public LionCreature(String name, int damage, int health, int healing) {
        super(name, damage, health);
        this.healing = healing;
        this.healthMax = health;
    }

    // TODO: Constructeur par copie
    // - Copie un autre LionCreature avec son état actuel
    public LionCreature(LionCreature other) {
        this(other.name, other.damage, other.health, other.healing);
    }

    // TODO: getHealing()
    public int getHealing() {
        return healing;
    }

    // TODO: addHealing(int)
    // - Ajoute un bonus de soin > 0
    public void addHealing(int amount) {
        healing += amount;
        healing = Math.max(healing, 0);
    }

    // TODO: setHealing(int)
    public void setHealing(int healing) {
        this.healing = Math.max(0, healing);
    }


    // TODO: toString()
    public String toString() {
        return String.format("[%-10s] Name: %-19s | Damage: %-4d | Health: %-4d| Heal Amount: %d\n", getSpecies(), getName(), getDamage(), getHealth(), getHealing());
    }

    // TODO: getSpecies()
    public String getSpecies() {
        return "Lion";
    }

    @Override
    public int getDamage() {
        int baseDamage = super.damage;
        float modifier = 1 - (float)health / healthMax;
        int newDamage = baseDamage + Math.round(baseDamage * modifier);

        // le damage ne peut pas être plus petit que sa valeur initiale
        newDamage = Math.max(baseDamage, newDamage);
        return Math.min(MAX_DAMAGE, newDamage);
    }

    @Override
    public void attackCreature(Creature otherCreature) {
        otherCreature.takeDamage(this.getDamage());

        if (!otherCreature.isAlive() && this.isAlive()) {
            modifyHealth(-healing);
            health = Math.min(health, healthMax);
        }
    }
}
