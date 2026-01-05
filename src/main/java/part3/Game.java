package part3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import enums.Event;
import interfaces.Publisher;
import interfaces.Subscriber;
import part1.Creature;
import part2.Card;

public class Game implements Publisher {
    // Scanner unique pour toutes les lectures console du jeu.
    // ⚠ Ne JAMAIS fermer System.in en cours de partie (sinon plus de lecture possible).
    private final Scanner scanner = new Scanner(System.in);

    private Player player1;
    private Player player2;
    private Player activePlayer;
    private PlayerLand playerLand1;
    private PlayerLand playerLand2;
    private PlayerLand activeLand;
    private boolean isFirstTurn;
    private BattleManager battleManager;
    private List<Subscriber> subscribers;

    // TODO: CONSTRUCTEUR
    // initialise les attributs
    // Note : ne pas initialisaer les joueurs, leur initialisation se fait dans le main
    Game(){
        playerLand1 = new PlayerLand();
        playerLand2 = new PlayerLand();
        activeLand = playerLand1;
        isFirstTurn = true;
        battleManager = new BattleManager();
        subscribers = new ArrayList<>();
    };

    // TODO: setPlayer1(Player)
    // initialisation du joueur 1
   public void setPlayer1(Player player1) {
       this.player1 = player1;
       player1.setScanner(scanner);
       playerLand1.setOwner(this.player1);
       activePlayer = this.player1;
    }

    // TODO: setPlayer2(Player)
    // initialisation du joueur 2
    public void setPlayer2(Player player2) {
       this.player2 = player2;
       player2.setScanner(scanner);
       playerLand2.setOwner(this.player2);
    }

    // TODO: getPlayerLand()
    // - Retourne le terrain correspondant au joueur actif
    public PlayerLand getPlayerLand(){
       return activeLand;
    }

    // TODO: updateActivePlayer()
    // - Alterne le tours des joueurs
    public void updateActivePlayer(){
       if (player1.equals(activePlayer)) {
           activePlayer = player2;
           activeLand = playerLand2;
       }
       else {
           activePlayer = player1;
           activeLand = playerLand1;
       }

       isFirstTurn = false;
    }

    // TODO: launch()
    // Execute des tours de jeu dans que la partie est en cours

    public void launch(){

        // TODO : Faire piocher des cartes aux 2 joueurs au début de la partie
        player1.drawCard(isFirstTurn);
        player2.drawCard(isFirstTurn);

        // TODO : definir la condition permettant de verifier que la partie est en cours
        boolean isGameOngoing = player1.isAlive() && player2.isAlive();

        while(isGameOngoing){
            notify(Event.NEW_TURN, player1, player2);

            System.out.printf("================================================== %s BEGINNING TURN ==================================================\n",
                                activePlayer.getName().toUpperCase());
            //TODO : Implémenter le déroulement du tour
            //TODO : Afficher la main du joueur
            activePlayer.showHand();
            draw();
            returnCard();
            placeCard();
            invokeCard();

            if (!isFirstTurn) {
                attack();
            }

            System.out.printf("====================================================== %s END TURN ======================================================\n",
                                        activePlayer.getName().toUpperCase());

            updateActivePlayer();
            isGameOngoing = player1.isAlive() && player2.isAlive();
        }
        // TODO : annonce du gagnant

        Player winningPlayer = player1.isAlive() ? player1 : player2;

        notify(Event.BATTLE_END, winningPlayer.getName().toUpperCase());
        scanner.close();
    }

    // TODO: askYesNo(String)
    // retourne si l'utilisateur a repondu positivement a la question
    private boolean askYesNo(String prompt) {
        System.out.print(prompt + " Yes (1) or No (0)\n");
        // TODO : prendre et valider l'entrée de l'utilisateur
        while (true) {
            int reponse = scanner.nextInt();
            switch (reponse) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    System.out.print(prompt + " Yes (1) or No (0)\n");
                    break;
            }
        }
    }

    // TODO: draw()
    // Demande au joueur de piocher une carte jusqu'a ce qu'il ne soit plus en mesure de piocher
    // Le joueur peut accepter ou refuser de piocher
    // Note : Penser aux differentes situations dans lesquelles il ne peut plus piocher

    // Prompt : "Do you want to draw card ?"

    private void draw(){
       while (!activePlayer.isHandFull()) {
           boolean reponse = askYesNo("Do you want to draw card ?");
           if (reponse) {
               activePlayer.drawCard(false);
           }
           else {
               break;
           }
       }
    }

    // TODO: placeCard()

    // Permet de poser des cartes face cachée sur le terrain du joueur actif
    // en demandant continuellement au joueur jusqu'a ce qu'il veuille arreter
    // ou qu' il n'est plus en mesure d'en poser

    // Note : Penser aux differentes situations dans lesquelles il ne peut plus piocher

    // Si le joueur souhaite arreter de son plein gré, il faut afficher sa main
    // Afficher le terrain du joueur a la fin, peu importe si l'arret est volontaire on non

    // Prompt : Do you want to place hidden card ?

    private void placeCard(){
       while (!activePlayer.isHandEmpty() && !getPlayerLand().isFull()) {
           boolean response = askYesNo("Do you want to place hidden card ?");

           if (response) {
               activePlayer.placeCard(getPlayerLand());
           }
           else {
               activePlayer.showHand();
               break;
           }
       }

       notify(Event.PLAYER_LAND_CHANGED, getPlayerLand());
    }

    // TODO: invokeCard()
    // Permet au joueur d'invoquer une créature s'il le souhaite
    // Si le joueur accepte : Afficher sa main et son terrain de jeu.

    // Prompt : "Do you want to invoke one creature ?"

    private void invokeCard(){
        if (!activePlayer.isHandEmpty() && !getPlayerLand().isFull()) {
            boolean response = askYesNo("Do you want to invoke one creature ?");

            if (response) {
                activePlayer.showHand();
                activePlayer.invokeCard(getPlayerLand());
            }
        }

        notify(Event.PLAYER_LAND_CHANGED, getPlayerLand());
    }

    // TODO: returnCard()
    // Permet de retourner (rendre visible) des cartes sur le terrain en demandant continuellement au joueur
    // jusqu'a ce qu'il souhaite s'arreter ou qu'il ne soit plus en mesure d'en retourner

    // Si le joueur veut arreter : Afficher la main du joueur
    // Afficher le terrain du joueur avant et après qu'il ait fini de retourner des cartes

    // Prompt : "Do you want to return some card ?"

    private void returnCard(){

       getPlayerLand().display();

       while (getPlayerLand().isAnyCardHidden()) {
           boolean response = askYesNo("Do you want to return some card ?");

           if (response) {
               activePlayer.returnCard(getPlayerLand());
           }
           else {
               activePlayer.showHand();
               notify(Event.PLAYER_LAND_CHANGED, getPlayerLand());
               break;
           }
       }
    }

    // TODO: attack()
    private void attack(){
        PlayerLand land = getPlayerLand();
        if(land.isEmpty()) return;
        boolean res = askYesNo("Do you want to attack your opponent ?");
        if(!res) return;

        PlayerLand oppLand = land == playerLand1 ? playerLand2 : playerLand1;
        if(oppLand.isEmpty()){
            System.out.println("Opponent land is empty. No target available.");
            return;
        }


        Card card = selectCardFromLand("Select the index of the card that will attack", land);
        Card oppCard = selectCardFromLand("Select the index of the card you wish to attack", oppLand);


        // TODO : Lancer la bataille entre les 2 créatures
        battleManager.setAttacker(card.getCreature());
        battleManager.setDefender(oppCard.getCreature());

        notify(Event.CREATURE_DUEL_START, land, oppLand);

        battleManager.launch();

        // TODO : Appliquer les mises à jour selon les résultats du combat

        if (card.getCreature().isAlive()) {
            applyBattleOutcome(land.getOwner(), card, land);
        }
        else {
            applyBattleOutcome(oppLand.getOwner(), oppCard, oppLand);
        }
    }

    // TODO
    // Appliquer les mises à jours de fin de combat
    private void applyBattleOutcome( Player loser, Card deadCard, PlayerLand deadLand) {
       loser.setLifePoints(loser.getLifePoints() - deadCard.getCreature().getOverkill());
       deadLand.removeCard(deadCard);
    }

    // TODO: selectCardFromLand(String, PlayerLand)
    // Demander un index de carte au joueur
    // Demander continuellement jusqu'à la saisie d'un index valide
    private Card selectCardFromLand(String prompt, PlayerLand land) {
       while (true) {
           System.out.println(prompt);
           int result = scanner.nextInt();
           if (result < land.getSize() && result >= 0) {
               return land.getCardAt(result);
           }
       }
   }

    public void addSubscriber(Subscriber subscriber) {
       subscribers.add(subscriber);
       player1.addSubscriber(subscriber);
       player2.addSubscriber(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
        player1.removeSubscriber(subscriber);
        player2.removeSubscriber(subscriber);
    }

    public void notify(Event event, Object ...args) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(event, args);
        }
    }
}
