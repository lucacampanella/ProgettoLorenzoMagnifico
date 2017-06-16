package it.polimi.ingsw.client.cli.notblockingmenus;

import it.polimi.ingsw.client.cli.notblockingmenus.BasicCLIMenu;
import it.polimi.ingsw.client.controller.ControllerCallbackInterface;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.FamilyMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by campus on 11/06/2017.
 */
public class LeaderPickerMenu extends BasicCLIMenu {

    public LeaderPickerMenu(ControllerCallbackInterface controller, List<LeaderCard> options) {
        super("Please select one of the leaders below by typing his name", controller);
        for(LeaderCard leaderIter : options) {
            addOption(leaderIter.getName(), "Take the leader " + leaderIter.getName() + " who has ability: " + leaderIter.getAbility().getAbilityDescription(), () -> controller.callbackOnLeaderCardChosen(leaderIter));
        }
    }
}
