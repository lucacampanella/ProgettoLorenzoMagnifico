package it.polimi.ingsw.model.excommunicationTiles;

import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.player.DiceAndFamilyMemberColor;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.resource.Resource;

/**
 * this class contains all the excommunication effects
 *
 */
public abstract class AbstractExcommunicationTileEffect {
    //The following list is a list all of the first period malus effects
    //it returns a POSITIVE resource. Then it will be subbed from Player
    int gainFewResource(Resource resource){
        return 0;
    }

    /**
     * whenever you harvest, you harvest with a malus on the dice.
     * @return the malusValue
     */
    public int harvestMalusEffect()
    {
        return 0;
    }

    /**
     * whenever you build, you build with a malus on the dice.
     * @return the malusValue
     */
    public int buildMalusEffect()
    {
        return 0;
    }

    /**
     * whenever you take a card using a certain
     * @param familyMemberColor you dice has a
     * @return malusValue
     */
    public int reductionOnDice(DiceAndFamilyMemberColor familyMemberColor)
    {
        return 0;
    }
    //The following list is a list all of the second period malus effects

    /**
     * this method returns the malusValue of the dice on a certain
     * @param colorOfTower
     * @return
     */
    public int reductionOnDiceTowerColor(CardColorEnum colorOfTower)
    {
        return 0;
    }

    /**
     * this method doesn't allow a player to place an action player on market
     * @return true if you can't place it
     */
    public boolean marketNotAvailable()
    {
        return false;
    }

    /**
     * this method indicates how many more servants a player has to pay to have +1 on action value
     * @return
     */
    public int payMoreServant()
    {
        return 0;
    }

    /**
     * this method signals that player has to skip the round.
     * @return
     */
    public boolean skipRound(){
        return false;
    }

    //3rd period excommunication

    /**
     * this method returns true if you don't score any point for a certain
     * @param color ed card
     * @return s true if you don't get VP. False if you get VP
     */
    public boolean noVPColoredCard(CardColorEnum color)
    {
        return false;
    }

    /**
     todo:update comments
     */
    public int getMalusVP()
    {
        return 0;
    }
}
