package part3;

import part1.Creature;

public class BattleManager {
    private Creature currentAttacker;
    private Creature currentDefender;
    
    BattleManager() {
    }

    // TODO : setter pour la créature en attaque
    public void setAttacker(Creature attacker){
        currentAttacker = attacker;
    }


    // TODO : setter pour pour la créature ne défense
    public void setDefender(Creature defender){
        currentDefender = defender;
    }

    // TODO : Exécuter la bataille entre les 2 créatures

    public void launch(){
        while (currentAttacker.isAlive() && currentDefender.isAlive()) {
            currentAttacker.attackCreature(currentDefender);
            swapAttackerAndDefender();
        }
    }


    // TODO:  Alterner les créatures entre l'attaque et la défense
    private void swapAttackerAndDefender() {
        Creature temp = currentAttacker;
        currentAttacker = currentDefender;
        currentDefender = temp;
    }
}
