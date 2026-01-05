package part1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreatureInteractionTest {

    @Test
    public void testLionVsEagle() {
        LionCreature lion = new LionCreature("Leo", 10, 100, 5);
        EagleCreature eagle = new EagleCreature("Aquila", 5, 50, 1.0f);

        int eagleHealthBefore = eagle.getHealth();
        lion.attackCreature(eagle);

        assertEquals(eagleHealthBefore, eagle.getHealth());
    }

    @Test
    public void testLionKillsEagleAndHeals() {
        LionCreature lion = new LionCreature("Leo", 20, 50, 10);
        EagleCreature eagle = new EagleCreature("Aquila", 5, 22, 0.0f);

        lion.takeDamage(25);
        lion.attackCreature(eagle);

        assertEquals(0, eagle.getHealth());
        assertFalse(eagle.isAlive());
        assertEquals(8, eagle.getOverkill());
        assertEquals(35, lion.getHealth());
    }

    @Test
    public void testLionVsTortoiseWithArmor() {
        LionCreature lion = new LionCreature("Leo", 20, 100, 5);
        TortoiseCreature tortoise = new TortoiseCreature("Shelly", 20, 80, 10, 0.5f);

        tortoise.attackCreature(lion);
        assertEquals(80, lion.getHealth());

        assertEquals(0, tortoise.getArmor());
        assertEquals(66, tortoise.getHealth());
    }

    @Test
    public void testTortoiseArmorOverDamage() {
        TortoiseCreature tortoise = new TortoiseCreature("Tank", 5, 80, 50, 1f);
        Creature attacker = new Creature("Enemy", 10, 2);

        attacker.attackCreature(tortoise);

        assertEquals(40, tortoise.getArmor());
        assertEquals(80, tortoise.getHealth());
        assertEquals(0, tortoise.getOverkill());

        assertEquals(0, attacker.getHealth());
        assertEquals(3, attacker.getOverkill());
        assertFalse(attacker.isAlive());
    }
}
