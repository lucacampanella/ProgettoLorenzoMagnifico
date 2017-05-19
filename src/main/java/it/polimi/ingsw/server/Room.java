package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.Debug;

import java.util.ArrayList;

/**
 * This is the class that handles a room and offers a layer between the network part of the server and the actual game
 */
public class Room {

    /**
     * Array of players in the room, its dimension is set in the constructor
     */
    ArrayList<AbstractConnectionPlayer> players;

    GameController gameController;

    /**
     * timeout that starts when the second player joins the room. When time is up game starts. Set by the constructor
     */
    private int timeoutInSec;

    private int maxNOfPlayers;
    private int currNOfPlayers;
    private boolean isGameStarted;
    /**
     * Constructor
     * @param maxNOfPlayers max number of players for this room
     * @param timeoutInSec timeout that starts when the second player joins the room. When time is up game starts
     */
    public Room(int maxNOfPlayers, int timeoutInSec)
    {
        this.timeoutInSec = timeoutInSec;
        this.maxNOfPlayers = maxNOfPlayers;
        currNOfPlayers = 0;
        isGameStarted = false;
        players = new ArrayList<AbstractConnectionPlayer>(maxNOfPlayers);
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public boolean canJoin(AbstractConnectionPlayer player) {
        for(AbstractConnectionPlayer i : players) {
            if(i.getNickname().equals(player.getNickname()))
                return false;
        }
        return true;
    }


    /**
     * adds new player to the room
     * @param player the istance of the player to add
     */
    public void addNewPlayer(AbstractConnectionPlayer player)
    {
        players.add(player);
        currNOfPlayers++;
        Debug.printDebug("*Room*: added player " + player.getNickname());
        if(currNOfPlayers == maxNOfPlayers) //GameController should start
        {
            isGameStarted = true;
            gameController = new GameController(currNOfPlayers, this);
        }
    }

    public static void sendChatMsg(RMIPlayer rmiPlayer, String msg) {
        //TODO implement
    }
}
