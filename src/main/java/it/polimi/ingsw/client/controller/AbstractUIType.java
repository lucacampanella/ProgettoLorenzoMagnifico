package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.cli.CommandLineUI;
import it.polimi.ingsw.client.network.socket.packet.PlayerPositionEndGamePacket;
import it.polimi.ingsw.model.cards.VentureCardMilitaryCost;
import it.polimi.ingsw.model.effects.immediateEffects.GainResourceEffect;
import it.polimi.ingsw.model.effects.immediateEffects.ImmediateEffectInterface;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.DiceAndFamilyMemberColorEnum;
import it.polimi.ingsw.model.player.FamilyMember;
import it.polimi.ingsw.model.player.PersonalTile;
import it.polimi.ingsw.model.resource.MarketWrapper;
import it.polimi.ingsw.model.resource.Resource;
import it.polimi.ingsw.model.resource.TowerWrapper;

import java.util.List;
import java.util.Optional;

/**
 * This is the abstract representation of the user interface
 * It can either be implemented by {@link CommandLineUI} or by {@link it.polimi.ingsw.client.gui.GraphicalUI}
 */
abstract public class AbstractUIType {

    private ViewControllerCallbackInterface controller;

    public AbstractUIType(ViewControllerCallbackInterface controller) {
        this.controller = controller;
    }

    protected ViewControllerCallbackInterface getController() {
        return controller;
    }

    /**
     * Asks the user which connection mode he wants to use
     */
    abstract public void askNetworkType();

    //This method ask the user if he wants to login or to create a new account
    abstract public void askLoginOrCreate();

    /**
     * This method asks the user to pick one of the action spaces to put his family member in
     * Direction: {@link ClientMainController} -> {@link AbstractUIType}
     *
     * @param servantsNeededHarvest The servants needed by the user to harvest, Optional.empty() if the action is not valid
     * @param servantsNeededBuild   The servants needed by the user to build, Optional.empty() if the action is not valid
     * @param servantsNeededCouncil The servants needed by the user to place on cuincil, Optional.empty() if the action is not valid
     * @param activeMarketSpaces    The list of legal action spaces in the market
     * @param activeTowerSpaces     the list of legal action spaces on the towers
     * @param availableServants     the number of servants the user can spend to perform the action
     */
    abstract public void askWhichActionSpace(Optional<Integer> servantsNeededHarvest,
                                             Optional<Integer> servantsNeededBuild,
                                             Optional<Integer> servantsNeededCouncil,
                                             List<MarketWrapper> activeMarketSpaces,
                                             List<TowerWrapper> activeTowerSpaces,
                                             int availableServants);

    /**
     * this method just alerts user that there was an error somewhere. It doesn't handle the error
     * @param title the title of the error
     * @param errorDescription the description of the error
     */
    abstract public void displayError(String title, String errorDescription);

    /**
     * this method alerts user that there was an error somewhere. It doesn't handle the error
     * The error is a fatal one and after the user clicked ok the program terminates     *
     * @param title the title of the error
     * @param errorDescription the description of the error
     */
    public abstract void displayErrorAndExit(String title, String errorDescription);

    /**
     * This method is called by {@link ClientMainController} to display an incoming chat message (Direction: {@link ClientMainController} -> {@link AbstractUIType}; general direction: Server -> Client)
     *
     * @param senderNick Is the nick of the person who's sending the message
     * @param msg is the message sent
     */
    public abstract void displayChatMsg(String senderNick, String msg);


    /**
     * This method is called when the player has joined a room, but the game isn't started yet
     */
    public abstract void showWaitingForGameStart();

    /**
     * This method is called by the controller after a leader is selected and the player has to wait for enemies to choose their
     */
    public abstract void showWaitingForLeaderChoices();

    /**
     * This method is called by the controller after a personal tile is selected and the player
     * has to wait for enemies to choose their
     */
    public abstract void showWaitingForTilesChoices();

    /**
     * Used when it's the turn of the user and he has to choose which action he wants to perform
     * This method will trigger either
     * {@link ViewControllerCallbackInterface(DiceAndFamilyMemberColorEnum, int)}
     */
    public abstract void askInitialAction(boolean playedFamilyMember);

    /**
     * This method is called when a choice on a council gift should be perfomed by the ui
     *
     * @param options is a list of options available
     * @return the index of the selected option, the choice the user made
     */
    public abstract int askCouncilGift(List<GainResourceEffect> options);


    /**
     * Asks what card to take when he has a free action due to a card that has such effect
     * @param towerWrapper the list of available spaces
     * @return the index of the choice
     */
    public abstract int askWhereToPlaceNoDiceFamilyMember(List<TowerWrapper> towerWrapper);
    /**
     * This method is called when a choice on which effect to activate in a yellow card should be perfomed by the ui
     *
     * @param possibleEffectChoices is a list of all options available
     * @return the index of the chosen effect
     */
    public abstract int askYellowBuildingCardEffectChoice(List<ImmediateEffectInterface> possibleEffectChoices);

    /**
     * This method is called when a choice on which cost to pay in a purple card should be perfomed by the ui
     *
     * @param costChoiceResource the list of resources the player will pay if he chooses this option
     * @param costChoiceMilitary the cost he will pay on something conditioned
     * @return 0 if he chooses to pay with resources, 1 with military points
     */
    public abstract int askPurpleVentureCardCostChoice(List<Resource> costChoiceResource, VentureCardMilitaryCost costChoiceMilitary);

    /**
     * This method is called at the beginning of the game to choose one leader card
     * This method should be non-blocking
     *
     * @param leaderCards the list of resources the player will pay if he chooses this option
     */
    public abstract void askLeaderCards(List<LeaderCard> leaderCards);

    /**
     * This method is called at the beginning of the game to choose one personal tile
     * This method should be non-blocking
     *
     * @param standardTile option1
     * @param specialTile  option2
     */
    public abstract void askPersonalTiles(PersonalTile standardTile, PersonalTile specialTile);

    /**
     * This method is called when a player chooses to play a ledear with a COPY ability and he should be asked to choose
     * This method should be blocking
     *
     * @param possibleLeaders the possibilites to choose from
     * @return the index of the choice
     */
    public abstract int askWhichLeaderAbilityToCopy(List<LeaderCard> possibleLeaders);

    /**
     * This method is called when the player it is playing a leader who has a ONCE_PER_ROUND ability
     * to ask the user if he also wants to activate the ability
     * This method should be blocking
     *
     * @return true if he also wants to activate, false otherwise
     */
    public abstract int askAlsoActivateLeaderCard();

    /*
     * This method is called when the player performs an action but from the model we have to ask
     * how many servants he wants to add
     * @param minimum the minimum number of servants he shuold at least add (typically 0)
     * @param maximum the maximum number of servants he can add (typically the ones the player has)
     * @return the number of servants the player wants to add to the action
     */
    public abstract int askAddingServants(int minimum, int maximum);

    /**
     * this method is called when a player pass the phase
     *
     * @param nickname the player that had pass the phase
     */
    public abstract void notifyEndOfPhaseOfPlayer(String nickname);

    /**
     * This method is called when the player activate a leader with a once per round ability that modifies
     * the value of one of his colored family members, he has to choose which one
     *
     * @param availableFamilyMembers the list of available family member, it's useless to modify
     *                               the value of a family member already played
     * @return the color of the family member he chose
     * @throws IllegalArgumentException if the list is empty
     */
    public abstract DiceAndFamilyMemberColorEnum askWhichFamilyMemberBonus(List<FamilyMember> availableFamilyMembers) throws IllegalArgumentException;

    /**
     * this method is used to start the menu used when the player is waiting the other players playing the phase
     */
    public abstract void waitMenu();

    /**
     * this method is called when the game is ended
     * @param playerPositionEndGamePacket the packet with all the information of the end of the game
     */
    public abstract void showEndOfGame(List<PlayerPositionEndGamePacket> playerPositionEndGamePacket);

    /**
     * This method is used by the controller when it receives a place on tower from another player and wants
     * to notify the user that such a move has happened
     * @param fm the family member used for the move
     * @param towerIndex the index of the tower
     * @param floorIndex the index of the floor AS
     */
    public abstract void notifyPlaceOnTower(FamilyMember fm, int towerIndex, int floorIndex);

    /**
     * This method is used by the controller when it receives a place on market from another player and wants
     * to notify the user that such a move has happened
     * @param fm the family member used for the move
     * @param marketIndex the index of the market as
     */
    public abstract void notifyPlaceOnMarket(FamilyMember fm, int marketIndex);

    /**
     * This method is used by the controller when it receives a place on harvest from another player and wants
     * to notify the user that such a move has happened
     * @param fm the family member used for the move
     * @param servantsUsed the number of servants used for the action
     */
    public abstract void notifyPlaceOnHarvest(FamilyMember fm, int servantsUsed);

    /**
     * This method is used by the controller when it receives a place on build from another player and wants
     * to notify the user that such a move has happened
     * @param fm the family member used for the move
     * @param servantsUsed the number of servants used for the action
     */
    public abstract void notifyPlaceOnBuild(FamilyMember fm, int servantsUsed);

    /**
     * This method is used by the controller when it receives a place on the council from another player and wants
     * to notify the user that such a move has happened
     * @param fm the family member used for the move
     */
    public abstract void notifyPlaceOnCouncil(FamilyMember fm);

    /**
     * This method is used by the controller when it receives a discard leader action from another player and wants
     * to notify the user that such a move has happened
     * @param nickname the name of the palyer making the move
     * @param nameCard the name of the leader card involved in the action
     */
    public abstract void notifyDiscardLeaderCard(String nickname, String nameCard);

    /**
     * This method is used by the controller when it receives a play leader action from another player and wants
     * to notify the user that such a move has happened
     * @param nickname the name of the palyer making the move
     * @param nameCard the name of the leader card involved in the action
     */
    public abstract void notifyPlayLeaderCard(String nickname, String nameCard);

    /**
     * This method is used by the controller when it receives a activate leader action from another player and wants
     * to notify the user that such a move has happened
     * @param nickname the name of the palyer making the move
     * @param nameCard the name of the leader card involved in the action
     */
    public abstract void notifyActivateLeaderCard(String nickname, String nameCard);

    /**
     * this method is called by the client to show the player excommunicated
     * @param playersExcommunicated the nickname of the players excommunicated
     */
    public abstract void displayExcommunicationPlayers(List<String> playersExcommunicated);

    /**
     * this method is called by the client to ask the client if he wants to be excommunicated
     * @param numTile the number of the tile to be taken if the player is excommunicated
     */
    public abstract void askExcommunicationChoice(int numTile);

    /**
     * This method is called by controller to signal that another player was suspende due to timeout
     * @param nickname the nick of the player suspended
     */
    public abstract void notifyAnotherPlayerSuspended(String nickname);

    /**
     * This method is called by controller to signal that this player was suspended due to timeout
     */
    public abstract void notifyThisPlayerSuspended();

    /**
     * This method is called by controller to signal that another player was disconnected due to network problems
     * @param nickname the nick of the player disconnected
     */
    public abstract void notifyAnotherPlayerDisconnected(String nickname);

    /**
     * This method is called by controller to signal that another player has reconnected after suspension
     * @param nickname the nick of the player reconnected
     */
    public abstract void notifyPlayerReconnected(String nickname);

    /**
     * Tells the view to remove a card
     * @param towerWrapper contains the coordinates of the card
     */
    public abstract void removeCard(TowerWrapper towerWrapper);
}