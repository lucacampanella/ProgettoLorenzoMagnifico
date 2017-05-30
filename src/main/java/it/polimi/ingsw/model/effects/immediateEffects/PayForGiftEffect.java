package it.polimi.ingsw.model.effects.immediateEffects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.resource.Resource;

import java.util.ArrayList;

/**
 * Created by higla on 30/05/2017.
 */
public class PayForGiftEffect extends AbstractPerformActionEffect {
    ArrayList<Resource> toPay;
    public PayForGiftEffect( ArrayList<Resource> temp){
        toPay = temp;
    }
    public void applyToPlayer(Player player){
        ;
    }
    public String descriptionOfEffect()
    {
        return "Pay "+ toPay.toString() + " to have a gift";
    }

    /**
     * prints short description of the effect
     * @return
     */
    public String descriptionShortOfEffect(){
        int i;
        String temp = new String();
        temp = "Gift ";
        for(int k = 0; k<toPay.size(); k++)
            temp += "-"+this.toPay.get(k).getResourceShortDescript();
        return temp;
    }
}