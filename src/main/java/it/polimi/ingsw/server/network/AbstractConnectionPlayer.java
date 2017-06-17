package it.polimi.ingsw.server.network;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.FamilyMember;
import it.polimi.ingsw.server.Room;
import it.polimi.ingsw.client.exceptions.NetworkException;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The abstract class that extends player and handles connections either via socker or via rmi
 */
public abstract class AbstractConnectionPlayer extends Player {

    /**
     * this instance is used to call methods of the room the player is in. There is redundancy
     */
    private Room room;

    public AbstractConnectionPlayer() {
        super();
    }

    public AbstractConnectionPlayer(String nickname)
    {
        super(nickname);
    }


    /**
     * This method is called by the room to send a chat message arrived from another client. (Direction: server -> client)
     * @param msg message
     * @param senderNickname the nickname of the sender
     * @throws NetworkException if something went wrong on the network
     */
    public abstract void receiveChatMsg(String senderNickname, String msg) throws NetworkException;

    /**
     * this method is called by the room to deliver a move on tower of another player
     * @throws NetworkException if something went wrong on the network
     */
    public abstract void receivePlaceOnTower(FamilyMember familyMember, int towerIndex, int floorIndex, HashMap<String, Integer> playerChoices) throws NetworkException;

    /**
     * this method is called by the room to deliver a move on market of another player
     * @throws NetworkException if something went wrong on the network
     */
    public abstract void receivePlaceOnMarket(FamilyMember familyMember, int marketIndex, HashMap<String, Integer> playerChoices) throws NetworkException;

    /**
     * this method is called by the room to deliver a build move of another player
     * @throws NetworkException if something went wrong on the network
     */
    public abstract void receiveBuild(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices) throws NetworkException;

    /**
     * this method is called by the room to deliver a harvest move of another player
     * @throws NetworkException if something went wrong on the network
     */
    public abstract void receiveHarvest(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices) throws NetworkException;

    /**
     * this method is called by the room to deliver a end phase of a different player
     * @param player the player that had ended the phase
     * @throws NetworkException if something goes  wrong with the network
     */
    public abstract void receiveEndPhase(AbstractConnectionPlayer player) throws NetworkException;

    protected Room getRoom() {
        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    /**
     * this method is called by the room to deliver the new dice loaded on the board
     * @throws NetworkException if something goes wrong with the network
     */
    public abstract void receiveDices(ArrayList<Dice> dices) throws NetworkException;

    /**
     * this method is called by the room to deliver the gameboard to the different players
     */
    public abstract void receiveStartGameBoard(Board gameBoard) throws NetworkException;

    /**
     * this method is called by the room to deliver the start of a new turn to the right player
     */
    public abstract void receiveStartOfTurn() throws NetworkException;

    /**
     * this method is used to deliver the nicknames of the player in order of turn
     */
    public abstract void deliverOrderPlayers(ArrayList<String> orderPlayers) throws NetworkException;

    /**
     * this method is used to receive the leader cards from the room
     */
    public abstract void receiveLeaderCards(ArrayList<LeaderCard> cardToPlayer) throws NetworkException;

    /**
     * this method is called by the room to deliver the cards to place on the board to the client
     */
    public abstract void deliverCardToPlace(ArrayList<AbstractCard> cards) throws NetworkException;

    /**
     * this method is called by room to deliver to the other clients the move on council of a player
     */
    public abstract void floodPlaceOnCouncil(FamilyMember familyMember, HashMap<String, Integer> playerChoices);
}
