package it.polimi.ingsw.model.leaders.leadersabilities.ImmediateLeaderAbility;

import it.polimi.ingsw.choices.ChoicesHandlerInterface;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.resource.ResourceTypeEnum;

/**
 * This ability gives the possibility to build once per round with a certain dice value,
 * This ability will usually be used by "Leonardo Da Vinci"
 */
public class OncePerRoundBuildLeaderAbility extends AbstractImmediateLeaderAbility {

    private int diceValue = 0;

    public OncePerRoundBuildLeaderAbility(int diceValue) {
        super();
        this.diceValue = diceValue;
    }
     /**
     * this method allows player to harvest.
     * @param player is the instance of the player that is activating the cardLeader
     * @param choicesHandlerInterface it's the interface that asks the choice
     * @param cardName it's the name of the card that calls the effect.
     */
    public void applyToPlayer(Player player, ChoicesHandlerInterface choicesHandlerInterface, String cardName)
    {
        int servantsToAdd = choicesHandlerInterface.callbackOnAddingServants(cardName,0, player.getResource(ResourceTypeEnum.SERVANT));
        player.build(diceValue + servantsToAdd, choicesHandlerInterface);
    }


    public String getAbilityDescription() {
        return "Perform a Production action at value " + diceValue + ". (You can increase this action value only by spending servants; you can’t increase it with Farmer or Peasant Development Cards.)";
    }

    public int getDiceValue() {
        return diceValue;
    }
}
