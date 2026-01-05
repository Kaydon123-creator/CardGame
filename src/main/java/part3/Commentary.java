package part3;

import enums.Event;
import interfaces.Subscriber;
import part1.Creature;
import part2.Hand;

/**
 * Implémentation de l’interface {@link Subscriber}.
 * Cette classe affiche des commentaires en fonction des événements du jeu
 * (ex. dégâts subis, nouveau tour, fin de bataille, etc.).
 */
public class Commentary implements Subscriber {
    
    /**
     * Constructeur par défaut.
     * Initialise un observateur prêt à recevoir les notifications d’événements.
     */
    Commentary() {}

    /**
     * Réagit aux notifications envoyées par les publishers (Player, Game, Creature).
     *
     * @param event type d’événement déclenché
     * @param args arguments supplémentaires associés à l’événement
     * @return void (effet : affichage console uniquement)
     */
    @Override
    public void update(Event event, Object... args) {
        switch (event) {
            case CREATURE_TAKE_DAMAGE -> {
                Creature c = (Creature) args[0];
                c.display();
            }
            case NEW_TURN -> {
                Player player1 = (Player) args[0];
                Player player2 = (Player) args[1];
                player1.display();
                player2.display();
            }
            case RETURN_CARD, PLAYER_LAND_CHANGED -> {
                PlayerLand land = (PlayerLand) args[0];
                land.display();
            }
            case DRAW_CARD, PLACE_CARD, INVOKE_CREATURE -> {
                Hand hand = (Hand) args[0];
                hand.display();
            }
            case CREATURE_DUEL_START -> {
                PlayerLand attackerLand = (PlayerLand) args[0];
                PlayerLand defenderLand = (PlayerLand) args[1];
                attackerLand.display();
                defenderLand.display();
            }
            case BATTLE_END -> System.out.printf("Battle Ended, The winner is %s", args[0]);
            default -> {
                // Aucun traitement par défaut
            }
        }
    }
}
