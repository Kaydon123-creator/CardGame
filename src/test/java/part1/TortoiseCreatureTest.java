package part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import interfaces.Displayable;
public class TortoiseCreatureTest {
    private TortoiseCreature tortoiseCreature1;

    @BeforeEach
    public void initialiser() {
        tortoiseCreature1 = new TortoiseCreature("Toto", 15, 250, 100, 0.5f);
    }

    @Test
    public void testCopyConstructor() {
        TortoiseCreature copy = new TortoiseCreature(tortoiseCreature1);
        assertEquals(tortoiseCreature1.toString(), copy.toString());
        assertNotSame(tortoiseCreature1, copy);
    }

    @Test
    public void testArmorGetterSetter() {
        assertEquals(100, tortoiseCreature1.getArmor());
        tortoiseCreature1.setArmor(250);
        assertEquals(250, tortoiseCreature1.getArmor());
    }

    @Test
    public void testArmorReducesDamage() {
        tortoiseCreature1.takeDamage(40);
        assertEquals(80, tortoiseCreature1.getArmor());
        assertEquals(230, tortoiseCreature1.getHealth());
    }

    @Test
    public void testArmorReducesDamage2() {
        tortoiseCreature1.setArmor(-10);
        tortoiseCreature1.takeDamage(20);
        assertEquals(230, tortoiseCreature1.getHealth());

        tortoiseCreature1.addArmor(-50);
        tortoiseCreature1.takeDamage(20);
        assertEquals(210, tortoiseCreature1.getHealth());
    }

    @Test
    public void testHealthReducedWhenArmorIsInsufficient() {
        tortoiseCreature1 = new TortoiseCreature("Toto", 15, 250, 10, 0.5f);
        tortoiseCreature1.takeDamage(40);

        assertEquals(0, tortoiseCreature1.getArmor());
        assertEquals(220, tortoiseCreature1.getHealth());
    }

    @Test
    public void testArmorDoesNotGoBelowZero() {
        tortoiseCreature1.takeDamage(300);
        assertEquals(0, tortoiseCreature1.getArmor());
        assertEquals(50, tortoiseCreature1.getHealth());
    }

    @Test
    public void testArmorWithOverkill() {
        TortoiseCreature tortoise = new TortoiseCreature("Toto", 15, 50, 200, 0.4f);
        tortoise.takeDamage(100);
        assertEquals(10, tortoise.getOverkill());
        assertFalse(tortoise.isAlive());
    }

    @Test
    public void testAddArmor() {
        tortoiseCreature1.setArmor(90);
        tortoiseCreature1.addArmor(15);
        assertEquals(105, tortoiseCreature1.getArmor());
    }

    @Test
    public void testGetSpecies() {
        assertEquals("Tortoise", tortoiseCreature1.getSpecies());
    }

    @Test
    public void testToString() {
        String expected = "[Tortoise  ] Name: Toto                | Damage: 15   | Health: 250 | Armor: 100   | Reduction: 0.50\n";
        assertEquals(expected, tortoiseCreature1.toString());
    }

    @Test
    public void testDisplay() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Displayable displayCreature = tortoiseCreature1;
        displayCreature.display();
        assertEquals(outContent.toString(), tortoiseCreature1.toString());

        System.setOut(originalOut);
    }
}
