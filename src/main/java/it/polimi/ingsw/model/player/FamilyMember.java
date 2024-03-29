package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.utils.Debug;

import java.io.Serializable;
import java.util.Optional;

/**
 * the family member owned by a player
 */
public class FamilyMember implements Serializable{

    /**
     * the dice that the family member is linked
     */
    private Dice dice;

    /**
     * color of the family member
     */
    private DiceAndFamilyMemberColorEnum color;

    /**
     * player that owned this family member
     */
    private Player player;

    /**
     * this is the value of the family member, it can be the same of the linked dice or can be different if an effect changes it
     */
    private int valueFamilyMember;

    public FamilyMember(Dice dice, Player player){

        this.player= player;
        this.dice=dice;
        valueFamilyMember = dice.getValue();
        color= dice.getColor();
        Debug.printVerbose("New family member created" + color + dice.getValue() + dice.toString());
    }

    /**
     * this method is used to know the value of the family member
     * @return the value of the dice
     */
    public int getValue(){
        int realValue = valueFamilyMember;

        //malus coming from excommunication cards
        realValue -= player.getExcommunicationTilesCollector().reductionOnDice(color);

        if(color == DiceAndFamilyMemberColorEnum.NEUTRAL)
            realValue += player.getPermanentLeaderCardCollector().getBonusNeutralFM();
        else {
            Optional<Integer> fixedValue = player.getPermanentLeaderCardCollector().getFixedFamilyMembersValue();
            if(fixedValue.isPresent()) {
                realValue = fixedValue.get();
            }
            realValue +=  player.getPermanentLeaderCardCollector().getBonusColoredFamilyMembers();
        }

        return realValue;

    }

    /**
     * it align the value of the family member with the value of the linked dice
     */
    public void alignValue(){

        valueFamilyMember = dice.getValue();

    }

    public void setValueFamilyMember(int valueFamilyMember){

        this.valueFamilyMember = valueFamilyMember;

    }

    public DiceAndFamilyMemberColorEnum getColor(){

        return color;

    }

    public Player getPlayer(){

        return player;

    }
}
