package it.polimi.ingsw.model.effects.permanentEffects;

import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.resource.Resource;

/**
 * This class handles all SecondChoice discounts
 */
public class BonusOnTowerEffectChoice extends BonusOnTowerEffect {
    private Resource discountOnSecondResource;
    public BonusOnTowerEffectChoice(CardColorEnum towerColor, Resource resourceDiscount, Resource secondDiscount, int diceDiscount)
    {
        super(towerColor, resourceDiscount, diceDiscount);
        this.discountOnSecondResource = secondDiscount;
    }
    public String getShortDescription(){
        return "+"+ bonusOnDice + " On " + towerColor.getCardColor() + " -" + discountOnResource.getResourceShortDescript() + " " + "-" + discountOnSecondResource.getResourceShortDescript();
    }
    public String getDescription(){
        return "Get a +" + bonusOnDice + "On " + towerColor.getCardColor() + "towers. And -" + discountOnResource + "discountOnSecondResource";
    }
}
