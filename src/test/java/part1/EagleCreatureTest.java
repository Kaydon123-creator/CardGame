package part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import interfaces.Displayable;
public class EagleCreatureTest {
    private EagleCreature eagleCreature1;

    @BeforeEach
    public void initialiser() {
        eagleCreature1 = new EagleCreature("Super", 10, 100, 1.0f, 2);
    }

    @Test
    public void testCopyConstructor() {
        EagleCreature copy = new EagleCreature(eagleCreature1);
        assertEquals(eagleCreature1.toString(), copy.toString());
        assertNotSame(eagleCreature1, copy);
    }

    @Test
    public void testDodgeWorksBelowMaxStreak() {
        eagleCreature1.takeDamage(20);
        eagleCreature1.takeDamage(20);
        assertEquals(100, eagleCreature1.getHealth());
    }

    @Test
    public void testDamageTakenAfterMaxDodgeStreak() {
        eagleCreature1.takeDamage(20);
        eagleCreature1.takeDamage(20);
        assertEquals(100, eagleCreature1.getHealth());

        eagleCreature1.takeDamage(20);
        assertEquals(80, eagleCreature1.getHealth());
        assertEquals(0, eagleCreature1.getOverkill());
    }

    @Test
    public void testDodgeStreakResetsAfterHit() {
        eagleCreature1.takeDamage(20);
        eagleCreature1.takeDamage(20);
        assertEquals(100, eagleCreature1.getHealth());
        eagleCreature1.takeDamage(20);
        assertEquals(80, eagleCreature1.getHealth());

        eagleCreature1.takeDamage(20);
        eagleCreature1.takeDamage(20);
        assertEquals(80, eagleCreature1.getHealth());
        eagleCreature1.takeDamage(20);
        assertEquals(60, eagleCreature1.getHealth());
        assertEquals(0, eagleCreature1.getOverkill());
    }

    @Test
    public void testDodgeChanceWorksWhenNotGuaranteed() {
        EagleCreature eagle = new EagleCreature("RandomEagle", 10, 100, 0.0f);

        eagle.takeDamage(30);
        assertEquals(70, eagle.getHealth());
    }

    @Test
    public void testAttackEagle() {
        Creature creature1 = new Creature("creature", 50, 100);
        creature1.attackCreature(eagleCreature1);

        assertEquals(100, eagleCreature1.getHealth());
    }

    @Test
    public void testGetSpecies() {
        assertEquals("Eagle", eagleCreature1.getSpecies());
    }

    @Test
    public void testToString() {
        assertEquals("[Eagle     ] Name: Super               | Damage: 10   | Health: 100 | Dodge chance: 1.00\n", eagleCreature1.toString());
    }

    @Test
    public void testDisplay() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable displayCreature = eagleCreature1;
        displayCreature.display();
        assertEquals(outContent.toString(), displayCreature.toString());

        System.setOut(originalOut);
    }
}
