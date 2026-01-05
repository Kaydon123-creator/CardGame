package part3;

import java.util.List;

import part1.*;
import part2.Card;

public class Main {
    private static List<Card> createDeck1() {
        return List.of(
            new Card(new EagleCreature("Stormwing Falcon", 320, 180, 0.25f, 2)),
            new Card(new LionCreature("Ironmane", 450, 280, 3)),
            new Card(new TortoiseCreature("Stoneback Guardian", 500, 600, 1, 0.20f)),
            new Card(new Creature("Shadow Beast", 350, 250)),
            new Card(new EagleCreature("Sunfeather Hawk", 280, 220, 0.35f, 2)),
            new Card(new LionCreature("Thunderclaw", 410, 310, 3)),
            new Card(new TortoiseCreature("Granite Shell", 520, 580, 2, 0.20f)),
            new Card(new Creature("Flame Spirit", 370, 200)),
            new Card(new EagleCreature("Mooncry Owl", 260, 240, 0.40f, 1)),
            new Card(new LionCreature("Firefang", 430, 290, 3)),
            new Card(new TortoiseCreature("Iron Fortress", 540, 610, 2, 0.25f)),
            new Card(new Creature("Night Stalker", 360, 240)),
            new Card(new EagleCreature("Windstriker", 300, 200, 0.30f, 2)),
            new Card(new LionCreature("Stonejaw", 480, 270, 3)),
            new Card(new Creature("Soul Reaper", 390, 210))
        );
    }

    private static List<Card> createDeck2() {
        return List.of(
            new Card(new EagleCreature("Ironbeak Condor", 310, 190, 0.32f, 2)),
            new Card(new LionCreature("Bloodmane", 460, 300, 3)),
            new Card(new TortoiseCreature("Shell Titan", 510, 590, 2, 0.20f)),
            new Card(new Creature("Forest Spirit", 340, 260)),
            new Card(new EagleCreature("Stormfeather", 300, 210, 0.27f, 2)),
            new Card(new LionCreature("Nightfang", 420, 310, 3)),
            new Card(new TortoiseCreature("Stone Colossus", 530, 620, 3, 0.25f)),
            new Card(new Creature("Light Bringer", 380, 230)),
            new Card(new EagleCreature("Silverwing", 280, 220, 0.34f, 2)),
            new Card(new LionCreature("Braveclaw", 440, 290, 3)),
            new Card(new TortoiseCreature("Mountain Guardian", 550, 600, 2, 0.20f)),
            new Card(new Creature("Phantom Wolf", 370, 250)),
            new Card(new EagleCreature("Ashbeak", 295, 215, 0.31f, 2)),
            new Card(new LionCreature("Steelheart", 470, 260, 3)),
            new Card(new Creature("Celestial Serpent", 400, 220))
        );
    }

    public static void main(String[] args) {
        // Une simulation peut être faite ici
        // pour vérifier le fonctionnement au fur et à mesure que vous avancerez
        Commentary commentary = new Commentary();
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        player1.addCardsToDeck(createDeck1());
        player2.addCardsToDeck(createDeck2());
        Game area = new Game();
        area.setPlayer1(player1);
        area.setPlayer2(player2);
        area.addSubscriber(commentary);
        area.launch();
    }
}
