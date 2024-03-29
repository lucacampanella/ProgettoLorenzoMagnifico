package it.polimi.ingsw.model.excommunicationTiles;

/**
 * Each time you spend m resource, you spend n less resources.
 * By default, we assume real value spent is one, and it will be handled by the controller
 */
public class PayMoreServantsEffect extends AbstractExcommunicationTileEffect{
    //example: 1 servant. You will need to pay 2 more servants to have 1 dice value
    //so it's resourceSpending + default to have default
    private int exchangeRate;


    /**
     * this method indicates how many servants a player has to pay to have +1 on action value
     * @return the exchage rate
     */
    public int payMoreServant()
    {
        return exchangeRate;
    }

    public String getShortEffectDescription(){
        return "You need to pay "+ exchangeRate + " servants for +1 on dice";
    }
}
