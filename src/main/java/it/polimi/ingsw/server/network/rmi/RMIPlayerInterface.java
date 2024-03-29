package it.polimi.ingsw.server.network.rmi;

import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.DiceAndFamilyMemberColorEnum;
import it.polimi.ingsw.model.player.PersonalTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * The interface for rmi calls from client to server
 */
public interface RMIPlayerInterface extends Remote {

    /**
     * This method is used to send chat message to all players in the room
     * @param msg The message
     **/
    public void sendChatMsg(String msg) throws RemoteException;

    public String getNickname() throws RemoteException;

    /**
     * Method used to signal that that player has played a leader card
     * @param leaderName the name of the leader card played
     * @param choicesOnCurrentActionString the choices done while playing th leader card
     * @param choicesOnCurrentAction
     * @throws RemoteException if something goes wrong with RMI communication
     */
    public void playLeaderCard(String leaderName, HashMap<String, String> choicesOnCurrentActionString,
                               HashMap<String, Integer> choicesOnCurrentAction) throws RemoteException;

    /**
     * this method is used to deliver the move of a family member on a tower
     * @param familyMemberColor the color of the chosen family member
     * @param numberTower the tower index (from left to right)
     * @param floorTower the number of the floor (from top to bottom)
     * @param playerChoices this is a map that contains all the choices of the client when an effect asks
     * @throws RemoteException if something goes wrong during the connection
     */
    public void placeOnTower(DiceAndFamilyMemberColorEnum familyMemberColor, int numberTower, int floorTower, HashMap<String, Integer> playerChoices) throws RemoteException;

    /**
     * this method is used to deliver the move of a family member on a marketplace
     * @param familyMemberColor is the color of the chosen family member
     * @param marketIndex is the index of the market space  (from left to right)
     * @param playerChoices is the map that cointains all the choices of the client when an effect asks
     * @throws RemoteException if something goes wrong during the connection
     */
    public void placeOnMarket(DiceAndFamilyMemberColorEnum familyMemberColor,int marketIndex, HashMap<String, Integer> playerChoices) throws  RemoteException;

    /**
     * this method is used to deliver the move of a family member on a council
     * @param familyMemberColor is the color of the chosen family member
     * @param playerChoices is the map that cointains all the choices of the client when an effect asks
     * @throws RemoteException if something goes wrong during the connection
     */
    public void placeOnCouncil(DiceAndFamilyMemberColorEnum familyMemberColor, HashMap<String, Integer> playerChoices) throws  RemoteException;

    /**
     * this method is used to harvest
     * @param familyMemberColor is the color of the chosen family member
     * @param servantsUsed is the numberof additional servants used to increase value of the dice
     * @param playerChoices is the map that cointains all the choices of the client when an effect asks
     * @throws RemoteException if something goes wrong during the connection
     */
    public void harvest(DiceAndFamilyMemberColorEnum familyMemberColor,int servantsUsed, HashMap<String, Integer> playerChoices) throws  RemoteException;

    /**
     * this method is used to build
     * @param familyMemberColor is the color of the chosen family member
     * @param servantsUsed is the numberof additional servants used to increase value of the dice
     * @param playerChoices is the map that cointains all the choices of the client when an effect asks
     * @throws RemoteException if something goes wrong during the connection
     */
    public void build(DiceAndFamilyMemberColorEnum familyMemberColor,int servantsUsed, HashMap<String, Integer> playerChoices) throws  RemoteException;

    /**
     * this method is called by the client to deliver the personal tile chosen by the player
     * @param tileChosen the personal tile hosen by the client
     */
    public void receivePersonalTile(PersonalTile tileChosen) throws RemoteException;

    /**
     * this method is called by the client to infor the server that the player had ended his turn
     */
    void receiveEndPhase() throws RemoteException;

    /**
     * this method is called by the client to deliver to the server the leader card activated
     * @param leaderName the name of the leader card
     * @param choicesOnCurrentAction the choices done if the player had to choose recources
     */
    void receiveActivatedLeader(String leaderName, HashMap<String, Integer> choicesOnCurrentAction) throws RemoteException;

    /**
     * this method is called by the client to deliver to the server the leader card choose by the client
     * @param leaderCard the leader card chosen
     * @throws RemoteException if something goes wrong with the network
     */
    void receivedLeaderChosen(LeaderCard leaderCard) throws RemoteException;

    /**
     * this method is called by the client to deliver to the server the choices one on the excommunication
     * @param response the choice of the client
     * @throws RemoteException if something goes wrong with the network
     */
    void receiveExcommunicationChoice(String response) throws RemoteException;

    /**
     * This method is called by the controller when the player suspended makes a new input and thus wants to be
     * reconnected
     * @throws RemoteException if something goes wrong with the network
     */
    void receiveReconnectPlayer() throws RemoteException;

    /**
     * this method is called by the client to deliver the discarded leader card to the other client
     * @param nameLeader the name of the leader card discarded
     * @param resourceChoose the type of resources gotten by the discarded leader card
     */
    void receiveDiscardedLeaderCard(String nameLeader, HashMap<String, Integer> resourceChoose) throws RemoteException;
}

