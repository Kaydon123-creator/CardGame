package part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import interfaces.Displayable;
public class LionCreatureTest {
    private LionCreature lionCreature1;

    @BeforeEach
    public void initialiser() {
        lionCreature1 = new LionCreature("Lio", 10, 100, 5);
    }

    @Test
    public void testCopyConstructor() {
        LionCreature copy = new LionCreature(lionCreature1);
        assertEquals(lionCreature1.toString(), copy.toString());
        assertNotSame(lionCreature1, copy);
    }

    @Test
    public void testHealingGetterSetter() {
        assertEquals(5, lionCreature1.getHealing());
        lionCreature1.setHealing(15);
        assertEquals(15, lionCreature1.getHealing());

        lionCreature1.setHealing(-15);
        assertEquals(0, lionCreature1.getHealing());
    }

    @Test
    public void testAddHealing() {
        assertEquals(5, lionCreature1.getHealing());
        lionCreature1.addHealing(10);
        assertEquals(15, lionCreature1.getHealing());
        lionCreature1.addHealing(-5);
        assertEquals(10, lionCreature1.getHealing());
        lionCreature1.addHealing(-20);
        assertEquals(0, lionCreature1.getHealing());
    }

    @Test
    public void testDamageMultiplier() {
        assertEquals(10, lionCreature1.getDamage());

        lionCreature1.takeDamage(50);
        assertEquals(15, lionCreature1.getDamage());

        lionCreature1.takeDamage(40);
        assertEquals(19, lionCreature1.getDamage());

        lionCreature1.modifyHealth(-200);
        assertEquals(10, lionCreature1.getDamage());
    }

    @Test
    public void testAttackAndHealing() {
        Creature weakCreature = new Creature("Weak", 5, 5);

        lionCreature1.modifyHealth(20);
        assertEquals(80, lionCreature1.getHealth());

        lionCreature1.attackCreature(weakCreature);

        assertEquals(0, weakCreature.getHealth());
        assertFalse(weakCreature.isAlive());
        assertEquals(7, weakCreature.getOverkill());

        assertEquals(85, lionCreature1.getHealth());

        Creature weakCreature2 = new Creature("Weak", 5, 5);
        lionCreature1.setHealing(50);
        assertEquals(50, lionCreature1.getHealing());

        lionCreature1.attackCreature(weakCreature2);
        assertEquals(100, lionCreature1.getHealth());
    }

    @Test
    public void testAttackWithoutKillingDoesNotTriggerHealing() {
        Creature strongCreature = new Creature("Strong", 5, 20);

        lionCreature1.modifyHealth(20);
        assertEquals(80, lionCreature1.getHealth());

        lionCreature1.attackCreature(strongCreature);

        assertEquals(8, strongCreature.getHealth());
        assertTrue(strongCreature.isAlive());

        assertEquals(80, lionCreature1.getHealth());
    }

    @Test
    public void testDeadLionCannotHeal() {
        Creature victim = new Creature("Victim", 0, 0);

        lionCreature1.takeDamage(200);
        assertEquals(0, lionCreature1.getHealth());
        assertFalse(lionCreature1.isAlive());

        lionCreature1.attackCreature(victim);

        assertEquals(0, lionCreature1.getHealth());
        assertFalse(lionCreature1.isAlive());
    }

    @Test
    public void testLionIsNotAttackedBackAfterAttack() {
        LionCreature lion = new LionCreature("Leo", 10, 100, 5);
        Creature opponent = new Creature("Enemy", 5, 50);

        int lionInitialHealth = lion.getHealth();
        int opponentInitialHealth = opponent.getHealth();

        lion.attackCreature(opponent);

        assertEquals(opponentInitialHealth - lion.getDamage(), opponent.getHealth());

        assertEquals(lionInitialHealth, lion.getHealth());
    }

    @Test
    public void testGetSpecies() {
        assertEquals("Lion", lionCreature1.getSpecies());
    }

    @Test
    public void testToString() {
        assertEquals("[Lion      ] Name: Lio                 | Damage: 10   | Health: 100 | Heal Amount: 5\n", lionCreature1.toString());
    }

    @Test
    public void testDisplay() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable displayCreature = lionCreature1;
        displayCreature.display();
        assertEquals(outContent.toString(), lionCreature1.toString());

        System.setOut(originalOut);
    }
}
