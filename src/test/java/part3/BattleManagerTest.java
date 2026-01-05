package part3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import part1.Creature;

import static org.junit.jupiter.api.Assertions.*;

class BattleManagerTest {

    private BattleManager battleManager;

    private static class TestCreature extends Creature {
        private int health;
        private final int attackDamage;
        private int attackCount;
        private boolean shouldDieAfterAttacks;
        private int attacksBeforeDeath;

        public TestCreature(String name, int health, int attackDamage) {
            super(name, health, attackDamage);
            this.health = health;
            this.attackDamage = attackDamage;
            this.attackCount = 0;
            this.shouldDieAfterAttacks = false;
            this.attacksBeforeDeath = 0;
        }

        public TestCreature(String name, int health, int attackDamage, int attacksBeforeDeath) {
            this(name, health, attackDamage);
            this.shouldDieAfterAttacks = true;
            this.attacksBeforeDeath = attacksBeforeDeath;
        }

        @Override
        public void attackCreature(Creature target) {
            attackCount++;
            if (target instanceof TestCreature testTarget) {
                testTarget.takeDamage(this.attackDamage);
            }

            if (shouldDieAfterAttacks && attackCount >= attacksBeforeDeath) {
                this.health = 0;
            }
        }

        @Override
        public boolean isAlive() {
            return health > 0;
        }

        public void takeDamage(int damage) {
            health -= damage;
            if (health < 0) {
                health = 0;
            }
        }

        public int getAttackCount() {
            return attackCount;
        }

        public int getHealth() {
            return health;
        }
    }

    @BeforeEach
    void setUp() {
        battleManager = new BattleManager();
    }

    @Test
    @DisplayName("Constructor should initialize with null creatures")
    void testConstructor() {
        BattleManager newBattleManager = new BattleManager();
        assertNull(getAttribute(newBattleManager, "currentAttacker"));
        assertNull(getAttribute(newBattleManager, "currentDefender"));
    }

    @Test
    @DisplayName("setAttacker should set both attacker and currentAttacker")
    void testSetAttacker() {
        TestCreature creature = new TestCreature("Attacker", 100, 10);
        battleManager.setAttacker(creature);
        assertEquals(creature, getAttribute(battleManager, "currentAttacker"));
    }

    @Test
    @DisplayName("setDefender should set both defender and currentDefender")
    void testSetDefender() {
        TestCreature creature = new TestCreature("Defender", 100, 10);
        battleManager.setDefender(creature);
        assertEquals(creature, getAttribute(battleManager, "currentDefender"));
    }


    @Test
    @DisplayName("launch should handle battle where attacker wins on first turn")
    void testLaunchAttackerWinsFirstTurn() {
        TestCreature attacker = new TestCreature("Attacker", 100, 50);
        TestCreature defender = new TestCreature("Defender", 30, 10);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertEquals(1, attacker.getAttackCount());
        assertEquals(0, defender.getAttackCount());
        assertTrue(attacker.isAlive());
        assertFalse(defender.isAlive());
        assertEquals(0, defender.getHealth());
    }

    @Test
    @DisplayName("launch should handle battle where defender wins on counter-attack")
    void testLaunchDefenderWinsCounterAttack() {
        TestCreature attacker = new TestCreature("Attacker", 30, 10);
        TestCreature defender = new TestCreature("Defender", 100, 50);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertEquals(1, attacker.getAttackCount());
        assertEquals(1, defender.getAttackCount());
        assertFalse(attacker.isAlive());
        assertTrue(defender.isAlive());
    }

    @Test
    @DisplayName("launch should handle extended battle with multiple turns")
    void testLaunchExtendedBattle() {
        TestCreature attacker = new TestCreature("Attacker", 100, 25);
        TestCreature defender = new TestCreature("Defender", 120, 30);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();


        assertTrue(attacker.getAttackCount() >= 3);
        assertTrue(defender.getAttackCount() >= 3);
        assertFalse(attacker.isAlive());
        assertTrue(defender.isAlive());
    }

    @Test
    @DisplayName("launch should handle battle where both creatures die simultaneously")
    void testLaunchBothCreaturesDieSimultaneously() {
        TestCreature attacker = new TestCreature("Attacker", 10, 50, 1);
        TestCreature defender = new TestCreature("Defender", 50, 10);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertEquals(1, attacker.getAttackCount());
        assertEquals(0, defender.getAttackCount());
        assertFalse(attacker.isAlive());
        assertFalse(defender.isAlive());
    }

    @Test
    @DisplayName("launch should handle battle where one creature is already dead")
    void testLaunchWithAlreadyDeadCreature() {
        TestCreature attacker = new TestCreature("Attacker", 100, 10);
        TestCreature defender = new TestCreature("Defender", 0, 10);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertEquals(0, attacker.getAttackCount());
        assertEquals(0, defender.getAttackCount());
        assertTrue(attacker.isAlive());
        assertFalse(defender.isAlive());
    }

    @Test
    @DisplayName("launch should handle battle where attacker is already dead")
    void testLaunchWithAlreadyDeadAttacker() {
        TestCreature attacker = new TestCreature("Attacker", 0, 10);
        TestCreature defender = new TestCreature("Defender", 100, 10);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertEquals(0, attacker.getAttackCount());
        assertEquals(0, defender.getAttackCount());
        assertFalse(attacker.isAlive());
        assertTrue(defender.isAlive());
    }

    @Test
    @DisplayName("roles should swap correctly during battle")
    void testRoleSwapping() {
        TestCreature attacker = new TestCreature("Attacker", 50, 10);
        TestCreature defender = new TestCreature("Defender", 60, 15);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        assertEquals(attacker,getAttribute(battleManager, "currentAttacker"));
        assertEquals(defender,getAttribute(battleManager, "currentDefender"));

        battleManager.launch();

        assertTrue(attacker.getAttackCount() > 0);
        assertTrue(defender.getAttackCount() > 0);

        assertFalse(attacker.isAlive());
        assertTrue(defender.isAlive());
    }

    @Test
    @DisplayName("battle should continue until one creature dies")
    void testBattleContinuesUntilOneDies() {
        TestCreature attacker = new TestCreature("Attacker", 100, 20);
        TestCreature defender = new TestCreature("Defender", 100, 20);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertTrue((attacker.isAlive() && !defender.isAlive()) ||
                (!attacker.isAlive() && defender.isAlive()));

        int attackDifference = Math.abs(attacker.getAttackCount() - defender.getAttackCount());
        assertTrue(attackDifference <= 1);
    }

    @Test
    @DisplayName("battle with equal stats should eventually end")
    void testBattleWithEqualStats() {
        TestCreature attacker = new TestCreature("Attacker", 60, 20);
        TestCreature defender = new TestCreature("Defender", 60, 20);

        battleManager.setAttacker(attacker);
        battleManager.setDefender(defender);

        battleManager.launch();

        assertTrue(attacker.isAlive());
        assertFalse(defender.isAlive());
        assertEquals(3, attacker.getAttackCount());
        assertEquals(2, defender.getAttackCount());
    }
    
    private Creature getAttribute(BattleManager battleManager, String attributeField) {
        try {
            var field = BattleManager.class.getDeclaredField(attributeField);
            field.setAccessible(true);
            return (Creature) field.get(battleManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}