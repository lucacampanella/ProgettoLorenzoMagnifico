package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.network.socket.packet.PlayerPositionEndGamePacket;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.DiceAndFamilyMemberColorEnum;
import it.polimi.ingsw.model.player.PersonalTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   This interface is implemented by {@link ClientMainController} to let {@link it.polimi.ingsw.client.network.AbstractClientType} make calls on controller
 */
public interface NetworkControllerClientInterface {

    /**
     * This method is called by {@link it.polimi.ingsw.client.network.AbstractClientType} to display an incoming chat message (Direction: AbstractClientType -> ClientMainController; general direction: Server -> Client)
     *
     * @param senderNick
     * @param msg
     */
    public void receiveChatMsg(String senderNick, String msg);

    public void setNickname(String nickname);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType} to signal that the game has started
     * and to pass to every client the order of players
     *
     * @param orderPlayers the nicknames of the players, in order
     */
    public void receivedOrderPlayers(ArrayList<String> orderPlayers);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType} to signal that the game has started
     * and to pass the board the server has created
     *
     * @param board the board from the server
     */
    public void receivedStartGameBoard(Board board);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType} to signal that the game has started
     * and the dices already thrown
     *
     * @param dices the board from the server
     */
    public void receivedDices(ArrayList<Dice> dices);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that the player is in turn and should move
     */
    public void receivedStartTurnNotification();

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that the has to pick a leader card between the ones proposed
     *
     * @param leaderCards options
     */
    public void receivedLeaderCards(List<LeaderCard> leaderCards);

    /**
     * this method is used to receive the card to place on the board
     *
     * @param cards the cards the client had to place
     */
    public void receiveCardsToPlace(ArrayList<AbstractCard> cards);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that the has to pick a personal tile between the ones proposed
     * Corresponding callback: {@link ViewControllerCallbackInterface#callbackOnTileChosen}
     *
     * @param standardTile option1
     * @param specialTile  option2
     */
    public void receivedPersonalTiles(PersonalTile standardTile, PersonalTile specialTile);


    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that another player has moved on a tower
     *
     * @param nickname          the nickname of the player performing the action
     * @param familyMemberColor the color of the family member he performed the action with
     * @param towerIndex        the index of the tower he placed the family member in
     * @param floorIndex        the index of the floor he placed the family member in
     * @param playerChoices     the hashmao with his choices correlated with this action
     */
    public void receivedPlaceOnTower(String nickname, DiceAndFamilyMemberColorEnum familyMemberColor,
                                     int towerIndex, int floorIndex, HashMap<String, Integer> playerChoices);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that another player has moved inside the market
     *
     * @param nickname          the nickname of the player performing the action
     * @param familyMemberColor the color of the family member he performed the action with
     * @param marketIndex       the index of the market action space he placed the family member in
     * @param playerChoices     the hashmao with his choices correlated with this action
     */
    public void receivedPlaceOnMarket(String nickname, DiceAndFamilyMemberColorEnum familyMemberColor, int marketIndex, HashMap<String, Integer> playerChoices);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that another player has moved inside the harverst action space
     *
     * @param nickname          the nickname of the player performing the action
     * @param familyMemberColor the color of the family member he performed the action with
     * @param servantsUsed the number of the servants used to perform this action
     * @param playerChoices     the hashmap with his choices correlated with this action
     */
    public void receivedHarvest(String nickname, DiceAndFamilyMemberColorEnum familyMemberColor, int servantsUsed, HashMap<String, Integer> playerChoices);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that another player has moved inside the build action space
     *
     * @param nickname          the nickname of the player performing the action
     * @param familyMemberColor the color of the family member he performed the action with
     * @param servantsUsed the number of the servants used to perform this action
     * @param playerChoices     the hashmap with his choices correlated with this action
     */
    public void receivedBuild(String nickname, DiceAndFamilyMemberColorEnum familyMemberColor, int servantsUsed, HashMap<String, Integer> playerChoices);

    /**
     * this method is called by {@link it.polimi.ingsw.client.network.AbstractClientType}
     * to notify that another player has moved inside the council
     *
     * @param nickname          the nickname of the player performing the action
     * @param familyMemberColor the color of the family member he performed the action with
     * @param playerChoices     the hashmap with his choices correlated with this action
     */
    public void receivedPlaceOnCouncil(String nickname, DiceAndFamilyMemberColorEnum familyMemberColor, HashMap<String, Integer> playerChoices);

    /**
     * this method is used to receive the end phase of other players
     * @param nickname the nicknameof the player that had ended the phase
     */
    public void receiveEndPhase(String nickname);

    /**
     * receve the personal tiles chosen by other players
     * @param nickname the nickname of the player that had chosen the tile
     * @param personalTile the personal tile chosen
     */
    public void receiveFloodPersonalTile(String nickname, PersonalTile personalTile);

    /**
     * this method is called by the connection to deliver the leader card discarded by a player
     * @param nickname the nickname of the player that discarded the leader card
     * @param nameCard the name of the leader card discarded
     * @param resourceGet the resource obtained
     */
    public void receivedDiscardLeaderCard(String nickname, String nameCard, HashMap<String, Integer> resourceGet);

    /**
     * this method is used to receive the leader cards that had been chosen by the other players
     * @param nickname the nickname of the player that had chosen the card
     * @param leaderCard the chosen leader card
     */
    void receiveChosenLeader(String nickname, LeaderCard leaderCard);

    /**
     * this method is called by RMI client to receive the leaderc card played bu another player
     * @param nameCard the name of the leader card played
     * @param choicesOnCurrentActionString the choices done while playing the leader card
     * @param nickname the nickname of the player
     * @param choicesOnCurrentAction
     */
    void receivePlayLeaderCard(String nameCard, HashMap<String, String> choicesOnCurrentActionString, String nickname, HashMap<String, Integer> choicesOnCurrentAction);

    /**
     * this method is called by the network to receive the end game results
     * @param playerPositionEndGamePacket this packet contains all the players with the final victory points and position
     */
    void receiveEndGame(ArrayList<PlayerPositionEndGamePacket> playerPositionEndGamePacket);

    /**
     * this method is called by the network to receive the leader card activated by another player
     * @param nickname the nickname of the player that had activated the leader card abilty
     * @param nameCard the name of the leader card activated
     * @param resourceGet the resources gotten from the leader ability
     */
    void receiveActivateLeaderCard(String nickname, String nameCard, Map<String, Integer> resourceGet);

    /**
     * this method is called by the server to deliver the players excommunicated
     * @param playersExcommunicated the nicknames of the players excommunicated
     * @param numTile the number of excommunication tile to take
     */
    void receiveExcommunicatedPlayers(List<String> playersExcommunicated, int numTile);

    /**
     * this method is called by the network to deliver the choice doe on the excommunication of the other players
     * @param nickname the nickname of the player that had done the choice
     * @param response the response of the player
     * @param numTile the numbcer of tile to take if the player is excommunicated
     */
    void manageExcommunicationChoice(String nickname, String response, int numTile);

    /**
     * this method is called by the network to deliver the fact that a player has disconnected due to the timeout
     *
     * @param nickname the nickname of the player that disconnected
     */
    void receivedNotificationSuspendedPlayer(String nickname);

    /**
     * this method is called by the network to deliver the fact that a player has reconnected
     *
     * @param nickname the nickname of the player that reconnected
     */
    void receivedNotificationReconnectedPlayer(String nickname);

    /**
     * this method is called by the network to deliver the fact that a player has disconnected
     *
     * @param nickname the nickname of the player that disconnected
     */
    void receivedPlayerDisconnected(String nickname);

    /**
     * this method is called to inform the client that the server is no longer connected
     */
    void receivedDisconnectionServer();
}
