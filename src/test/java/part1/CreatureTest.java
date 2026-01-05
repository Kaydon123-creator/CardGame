package part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import interfaces.Fighter;
import interfaces.Displayable;

public class CreatureTest {
    private Creature creature1;
    private Creature creature2;

    @BeforeEach
    public void initialiser() {
        creature1 = new Creature("bob", 10, 90);
        creature2 = new Creature("john", 100, 80);
    }

    @Test
    public void testCreatureCreation() {
        assertEquals("bob", creature1.getName());
        assertEquals(10, creature1.getDamage());
        assertEquals(90, creature1.getHealth());
    }

    @Test
    public void testCreatureClone() {
        Creature copy = new Creature(creature1);
        assertEquals(creature1.toString(), copy.toString());
        assertNotSame(creature1, copy);
    }

    @Test
    public void testCreatureCreationInvalid() {
        Creature newCreature1 = new Creature("John", 10000, 100);
        assertEquals(100, newCreature1.getDamage());

        Creature newCreature2 = new Creature("John", 15, -100);
        assertEquals(0, newCreature2.getHealth());
    }

    @Test
    public void testGetters() {
        assertEquals("bob", creature1.getName());
        assertEquals(10, creature1.getDamage());
        assertEquals(90, creature1.getHealth());
    }

    @Test
    void testGetOverkill() {
        assertEquals(0, creature1.getOverkill());
    }

    @Test
    public void testKillCreature() {
        creature1.takeDamage(100);
        assertFalse(creature1.isAlive());
        assertEquals(0, creature1.getHealth());
        assertEquals(0, creature1.getDamage());

        creature1.takeDamage(-10);
        assertEquals(0, creature1.getHealth());
        assertFalse(creature1.isAlive());
    }

    @Test
    public void testDamageCreature() {
        creature1.takeDamage(89);
        assertEquals(1, creature1.getHealth());
        assertTrue(creature1.isAlive());
    }

    @Test
    public void testNoRetaliationWhenDefenderDies() {
        creature2.attackCreature(creature1);
        assertEquals(0, creature1.getHealth());
        assertFalse(creature1.isAlive());

        assertEquals(80, creature2.getHealth());
        assertTrue(creature2.isAlive());
    }

    @Test
    public void testCreatureImplementsCombatant() {
        assertTrue(creature1 instanceof Fighter, "Creature must implements the Fighter interface");
    }

    @Test
    public void testRetaliationWhenDefenderSurvives() {
        creature1.attackCreature(creature2);
        assertEquals(70, creature2.getHealth());
        assertTrue(creature2.isAlive());

        assertEquals(0, creature1.getHealth());
        assertFalse(creature1.isAlive());
        assertEquals(10, creature1.getOverkill());
    }

    @Test
    public void testGetSpecies() {
        assertEquals("Creature", creature1.getSpecies());
    }

    @Test
    public void testToString() {
        assertEquals("[Creature  ] Name: bob                 | Damage: 10   | Health: 90   \n", creature1.toString());
    }

    @Test
    public void testDisplay() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable displayCreature = creature2;
        displayCreature.display();
        assertEquals(outContent.toString(), creature2.toString());

        System.setOut(originalOut);
    }
}
