package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.choices.ChoicesHandlerInterface;
import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.effects.immediateEffects.PayResourceEffect;
import it.polimi.ingsw.model.effects.permanentEffects.AbstractPermanentEffect;
import it.polimi.ingsw.model.resource.Resource;

import java.util.ArrayList;

/**
 * This is the class of all blue cards.
 */
public class CharacterCard extends AbstractCard {
    //blue cards have a cost (usually they cost coins)
    private ArrayList<Resource> cost;
    private static CardColorEnum cardColor = CardColorEnum.BLUE;
    //and a permanent effect, that buffs players actions
    ArrayList<AbstractPermanentEffect> permanentEffect;

    //TODO
    public boolean checkEffect()
    {
        return true;
    }

    public ArrayList<Resource> getCost() {
        return cost;
    }

    @Override
    public ArrayList<Resource> getCostAskChoice(ChoicesHandlerInterface choicesController) {
        return cost;
    }

    public void setCost(ArrayList<Resource> cost) {
        this.cost = cost;
    }

    public ArrayList<AbstractPermanentEffect> getPermanentEffects() {
        return permanentEffect;
    }

    public void setPermanentEffects(ArrayList<AbstractPermanentEffect> permanentEffect) {
        this.permanentEffect = permanentEffect;
    }

    /**
     * this method prints all effects available in this class
     * @return
     */
    public String secondEffect(){
        String temp = new String();
        for(int i=0; i<permanentEffect.size(); i++)
            temp += permanentEffect.get(i).getShortDescription();
        return temp;
    }
    public CardColorEnum getColor(){
        return cardColor;
    }
}
