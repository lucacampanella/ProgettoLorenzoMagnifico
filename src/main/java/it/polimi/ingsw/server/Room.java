package it.polimi.ingsw.server;

import it.polimi.ingsw.client.exceptions.IllegalMoveException;
import it.polimi.ingsw.client.exceptions.NetworkException;
import it.polimi.ingsw.client.network.socket.packet.PlayerPositionEndGamePacket;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.server.network.AbstractConnectionPlayer;
import it.polimi.ingsw.utils.Debug;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the class that handles a room and offers a layer between the network part of the server and the actual game
 */
public class Room {

    /**
     * Array of order of the players in the room, its dimension is set in the constructor
     */
    private ArrayList<AbstractConnectionPlayer> players;

    /**
     * This is the lis of players for which timeout for move has ended, they can still connect and restart playing
     */
    private List<AbstractConnectionPlayer> suspendedPlayers;

    /**
     * This is the list of players for which an error on the network was encountered, they cannot reconnect
     */
    private List<AbstractConnectionPlayer> disconnectedPlayers;

    private ControllerGame controllerGame;

    private ArrayList<LeaderCard> cardToPlayer;

    /**
     * This pool is used to schedule timeouts for players move
     */
    private ScheduledThreadPoolExecutor timersPool;

    /**
     * timeout that starts when the second player joins the room. When time is up game starts. Set by the constructor
     */
    private int timeoutInSec;

    /**
     * the maximum time a user can spend playing his turn
     */
    private int timeoutMoveInSec;

    private int maxNOfPlayers;
    private int currNOfPlayers;
    private boolean isGameStarted;
    private ScheduledFuture<?> currentTimerMoveTask;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final String ILLEGAL_MOVE_MESSAGE = "someone performed an illegal move";

    /**
     * Timer for the beginning of the game
     */
    private  Timer beginningTimer;


    /**
     * Constructor
     *  @param maxNOfPlayers max number of players for this room
     * @param timeoutInSec  timeout that starts when the second player joins the room. When time is up game starts
     * @param timeoutMoveInSec is the maximum time a user can spend playing his turn
     */
    public Room(int maxNOfPlayers, int timeoutInSec, int timeoutMoveInSec) {
        this.timeoutInSec = timeoutInSec;
        this.maxNOfPlayers = maxNOfPlayers;
        this.timeoutMoveInSec = timeoutMoveInSec;
        currNOfPlayers = 0;
        isGameStarted = false;
        players = new ArrayList<>(maxNOfPlayers);
        timersPool = new ScheduledThreadPoolExecutor(1); //we need just one timeout
    }


    public boolean isGameStarted() {
        return isGameStarted;
    }

    public boolean canJoin(AbstractConnectionPlayer player) {
        for (AbstractConnectionPlayer i : players) {
            if (i.getNickname().equals(player.getNickname()))
                return false;
        }
        return true;
    }


    /**
     * adds new player to the room, it also binds the player with the instance of the room
     *
     * @param player the istance of the player to add
     */
    public void addNewPlayer(AbstractConnectionPlayer player) {
        player.setPlayerColor(PlayerColorEnum.valueOf(players.size()));
        players.add(player);
        player.setRoom(this);
        currNOfPlayers++;
        Debug.printDebug("*Room*: added player " + player.getNickname() + "of color: " + player.getPlayerColor().getStringValue());
        if (currNOfPlayers == maxNOfPlayers) //ModelController should start
        {
            Debug.printVerbose("Room capacity reached, starting new game");
            beginningTimer.cancel();
            new Thread( () -> startGame()).start(); // we don't want to start a game on the server thread
            Debug.printVerbose("Room capacity reached, returned from start function");
        } else if (currNOfPlayers == 2) {
            Debug.printVerbose("2 players reached ");
            beginningTimer = new Timer();
            beginningTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Debug.printVerbose("Timeout reached, starting new game");
                    startGame();
                    Debug.printVerbose("Timeout reached, returned from start function");
                }
            }, (long) (timeoutInSec) * 1000);
        }
    }

    /**
     * when the time out is ended or the room is full the game start to prepare all the object needed on the game
     */
    private void startGame() {
        Debug.printVerbose("Game on room started");
        //we initialize the array of possible suspended players here, so we know the maximum number
        suspendedPlayers = new ArrayList<>(players.size());
        disconnectedPlayers = new ArrayList<>(players.size());
        isGameStarted = true;
        try {
            Debug.printVerbose("just before constructor");
            controllerGame = new ControllerGame(players, this);
            Debug.printVerbose("after constructor");
            controllerGame.startNewGame();
            Debug.printDebug("New game started, waiting for first player to move");
        } catch (Exception e) {
            Debug.printError("Connection Error", e);
            LOGGER.log(Level.SEVERE, "problem of connection at the starting of the game", e);
        }
    }

    /**
     * reload the order of player when it changes
     *
     * @param orderPlayers the order of players
     */
    public void updateOrderPlayer(ArrayList<AbstractConnectionPlayer> orderPlayers) {

        this.players = new ArrayList<>(orderPlayers);

    }

    /**
     * This method is called by a the (@link AbstractConnectionPlayer) that wants to send a message (Direction: AbstractConnectionPlayer -> Room)
     *
     * @param player the sender
     * @param msg
     */
    public synchronized void floodChatMsg(AbstractConnectionPlayer player, String msg) {
        for (AbstractConnectionPlayer i : players) {
            if (player != i) {//the message should not be sent to the sender
                try {

                    i.receiveChatMsg(player.getNickname(), msg);
                    System.out.println("send message to " + player.getNickname());

                } catch (NetworkException e) { //not a big problem if a chat message is not sent
                    Debug.printError("Unable to sent chat message to " + i.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "Unable to sent chat message", e);
                }
            }
        }
    }

    /**
     * call the method on controller game to place a family member on the council
     */
    public synchronized void placeOnCouncil(FamilyMember familyMember, HashMap<String, Integer> playerChoices){

        try{
            controllerGame.placeOnCouncil(familyMember, playerChoices);
            floodPlaceOnCouncil(familyMember, playerChoices);
        }
        catch (IllegalMoveException e){
            AbstractConnectionPlayer player = getAbstractPlayer(familyMember.getPlayer().getNickname());
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }


    }

    /**
     * call the method on controller game to place a family member on the tower
     */
    public synchronized void placeOnTower(FamilyMember familyMember, int towerIndex, int floorIndex, HashMap<String, Integer> playerChoices){

        try{
            controllerGame.placeOnTower(familyMember, towerIndex, floorIndex, playerChoices);
            floodPlaceOnTower(familyMember, towerIndex, floorIndex, playerChoices);
        }
        catch (IllegalMoveException e){

            AbstractConnectionPlayer player = getAbstractPlayer(familyMember.getPlayer().getNickname());
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }
    }

    /**
     * the method called by the client to do place a family member on the market
     */
    public synchronized void placeOnMarket(FamilyMember familyMember, int marketIndex, HashMap<String, Integer> playerChoices){

        try{
            controllerGame.placeOnMarket(familyMember, marketIndex, playerChoices);
            floodPlaceOnMarket(familyMember, marketIndex, playerChoices);
        }
        catch (IllegalMoveException e){

            AbstractConnectionPlayer player = getAbstractPlayer(familyMember.getPlayer().getNickname());
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }

    }

    /**
     * call the method on controller game to build
     */
    public synchronized void build(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices){

        try{
            controllerGame.build(familyMember, servant, playerChoices);
            floodBuild(familyMember, servant, playerChoices);
        }
        catch (IllegalMoveException e){
            LOGGER.log(Level.INFO, ILLEGAL_MOVE_MESSAGE, e);
            AbstractConnectionPlayer player = getAbstractPlayer(familyMember.getPlayer().getNickname());
            handleErrorMove(player);
        }
    }

    /**
     * This method handles when a client performed an illegal move, this should never happen, he's cheating
     * @param player the player who cheated
     */
    private void handleErrorMove(AbstractConnectionPlayer player) {
        try{
            player.deliverErrorMove();
        }
        catch (NetworkException e){
            Debug.printError("cannot deliver the error on the move to the player " + player.getNickname());
            LOGGER.log(Level.SEVERE, "failed to deliver the error on the move", e);
            addToDisconnectedPlayers(player);
            floodPlayerDisconnected(player);
        }
    }

    /**
     * call the method on controller game to harvest
     */
    public synchronized void harvest(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices){

        try{
            controllerGame.harvest(familyMember, servant, playerChoices);
            floodHarvest(familyMember, servant, playerChoices);
        }
        catch (IllegalMoveException e){

            AbstractConnectionPlayer player = getAbstractPlayer(familyMember.getPlayer().getNickname());
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }
    }

    /**
     * this method is used to find the player connection knowing the nickname
     * @param nickname the nickname of the connection player you want to get
     */
    private AbstractConnectionPlayer getAbstractPlayer(String nickname) {

        for(AbstractConnectionPlayer player : players){
            if(player.getNickname().equals(nickname))
                return player;
        }
        return players.get(0);
    }

    /**
     * launch the move of a player to the other players
     */
    private void floodPlaceOnTower(FamilyMember familyMember, int towerIndex, int floorIndex, HashMap<String, Integer> playerChoices) {

        for (AbstractConnectionPlayer player : players) {
            if (!player.getNickname().equals(familyMember.getPlayer().getNickname())
                    && !disconnectedPlayers.contains(player)) {
                try {
                    player.receivePlaceOnTower(familyMember, towerIndex, floorIndex, playerChoices);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent move on tower to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to send move on tower to", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * launch the move of a player to the other players
     */
    private void floodBuild(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices) {

        for (AbstractConnectionPlayer player : players) {
            if (!player.getNickname().equals(familyMember.getPlayer().getNickname())
                    && !disconnectedPlayers.contains(player)){
                try {
                    player.receiveBuild(familyMember, servant, playerChoices);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent move on build to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to send move on build to", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * launch the move of a player to the other players
     */
    private void floodHarvest(FamilyMember familyMember, int servant, HashMap<String, Integer> playerChoices) {

        for (AbstractConnectionPlayer player : players) {
            if (!player.getNickname().equals(familyMember.getPlayer().getNickname())
                    && !disconnectedPlayers.contains(player)){
                try {
                    player.receiveHarvest(familyMember, servant, playerChoices);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent move on harvest to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to send move on harvest to", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * launch the move of a player to the other players
     */
    private void floodPlaceOnMarket(FamilyMember familyMember, int marketIndex, HashMap<String, Integer> playerChoices) {

        for (AbstractConnectionPlayer player : players) {
            if (!player.getNickname().equals(familyMember.getPlayer().getNickname())
                    && !disconnectedPlayers.contains(player)){
                try {
                    player.receivePlaceOnMarket(familyMember, marketIndex, playerChoices);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent move on market to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to send move on market to", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is used to perform the end of the turn of the player on the server and inform the other players
     * @param player the player that had ended the phase
     */
    public synchronized void endPhase(AbstractConnectionPlayer player){

        //we disable the timeout for the player who passed and only if the timeout hasn't run already we end his phase
        if(currentTimerMoveTask.cancel(false)) {
            try {
                floodEndPhase(player);
                controllerGame.endPhase(player);
            } catch (IllegalMoveException e) {
                LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
                handleErrorMove(player);
            }
        }

    }

    /**
     * launch the end of a player' phase to the other players
     */
    private void floodEndPhase(AbstractConnectionPlayer playerEndPhase) {

        for (AbstractConnectionPlayer player : players) {
            if (player != playerEndPhase && !disconnectedPlayers.contains(player)) {
                try {
                    player.receiveEndPhase(playerEndPhase);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent end phase message to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to sent end phase message", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * deliver the new dices on the board to all the player
     */
    public void deliverDices(ArrayList<Dice> dices) {

        for (AbstractConnectionPlayer player : players) {
            if(!disconnectedPlayers.contains(player)) {
                try {
                    player.receiveDices(dices);
                } catch (NetworkException e) {
                    Debug.printError("Unable to sent new dices to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to sent new dices", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * deliver the initial game board to the players
     */
    public void receiveStartGameBoard(Board gameBoard) {

        for (AbstractConnectionPlayer player : players) {
            if (!disconnectedPlayers.contains(player)) {
                try {
                    player.receiveStartGameBoard(gameBoard);
                } catch (NetworkException e) {
                    Debug.printError("ERROR on the deliver of the board ", e);
                    LOGGER.log(Level.SEVERE, "cannot deliver board", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }


    /**
     * inform the player that is his turn to play
     */
    public void playersTurn(AbstractConnectionPlayer player) {

        //if the player is suspended we don't ask him, but we just pass the turn for him
        if(suspendedPlayers.contains(player) || disconnectedPlayers.contains(player)) {
            try {
                controllerGame.endPhase(player); //we automatically make him pass
            } catch (IllegalMoveException e) {
                Debug.printVerbose("This should never happen: the server cheated (?)");
                LOGGER.log(Level.INFO, "the server cheated", e);
                handleErrorMove(player);
            }
        } else {
            //otherwise we ask him for a move
            try {
                //starts the timer for player suspension if he doesn't move in time
                currentTimerMoveTask = timersPool.schedule(() -> suspendPlayer(player), (long) (timeoutMoveInSec), TimeUnit.SECONDS);
                player.receiveStartOfTurn();
            } catch (NetworkException e) {
                Debug.printError("ERROR on the deliver of the token ", e);
                LOGGER.log(Level.SEVERE, "cannot deliver start of turn", e);
                addToDisconnectedPlayers(player);
                floodPlayerDisconnected(player);
            }
        }

    }

    /**
     * This method is called when the timeout for player's move is ended and we should notify the player and all the
     * other players
     * @param playerToSuspend the player that has to be suspended, not played in time
     */
    private void suspendPlayer(AbstractConnectionPlayer playerToSuspend) {
        addToSuspendedPlayers(playerToSuspend);

        for(AbstractConnectionPlayer playerIter : players) {
            try {
                playerIter.notifySuspendedPlayer(playerToSuspend.getNickname());
            } catch (NetworkException e) {
                Debug.printError("ERROR in notifying the suspension of the player" +
                        playerToSuspend.getNickname() + " to player " + playerIter.getNickname(), e);
                LOGGER.log(Level.SEVERE, "player disconnected", e);
                addToDisconnectedPlayers(playerIter); //we suspend the player that disconnected, so that the game can go on
                floodPlayerDisconnected(playerIter);
            }
        }

        //we make him pass
        try {
            controllerGame.endPhase(playerToSuspend); //we automatically make him pass
        } catch (IllegalMoveException e) {
            Debug.printVerbose("This should never happen: the server cheated (?)");
            LOGGER.log(Level.INFO, "the server cheated ?", e);
        }
    }

    /**
     * This method adds a plyer to the suspended players list only if he's not already inside
     * @param player the player to add to the list
     */
    private void addToSuspendedPlayers(AbstractConnectionPlayer player) {
        if(!suspendedPlayers.contains(player))
            suspendedPlayers.add(player);
    }

    /**
     * This method adds a plyer to the suspended players list only if he's not already inside
     * @param player the player to add to the list
     */
    private void addToDisconnectedPlayers(AbstractConnectionPlayer player) {
        if(!disconnectedPlayers.contains(player))
            disconnectedPlayers.add(player);
    }
    /**
     * this method is called by the controller game to deliver all the players to the different players
     */
    public void deliverOrderPlayers(ArrayList<String> orderPlayers) {

            for (AbstractConnectionPlayer player : this.players) {
                if (!disconnectedPlayers.contains(player)) {
                    try {
                        player.deliverOrderPlayers(orderPlayers);
                    } catch (NetworkException e) {
                        Debug.printError("ERROR on the deliver of the players ", e);
                        LOGGER.log(Level.SEVERE, "failed to deliver of the players", e);
                        addToDisconnectedPlayers(player);
                        floodPlayerDisconnected(player);
                    }
                }
            }
    }

    /**
     * this method is used to deliver the leader cards to the different players
     */
    public void initiateLeaderChoice(ArrayList<LeaderCard> leaderCards) {

        for (LeaderCard card : leaderCards) {
            Debug.printVerbose(card.getName());
        }
        cardToPlayer = leaderCards;
        deliverLeaderCardsToPlayers();
    }

    private void deliverLeaderCardsToPlayers() {

        if (cardToPlayer.isEmpty()) {
            controllerGame.choseAllTheLeadersCards();
            return;
        }
        if (cardToPlayer.size() % players.size() != 0)
            return;
        int index = 0;

        ArrayList<LeaderCard> leaderCardsToDeliver = new ArrayList<>(cardToPlayer);
        // numberOfTimesTheChoiceIsDone is the number of times the round of choices of the leaders
        for (int numberOfTimesTheChoiceIsDone = 4 * players.size() - (leaderCardsToDeliver.size() / players.size()); index < leaderCardsToDeliver.size(); numberOfTimesTheChoiceIsDone++) {
            AbstractConnectionPlayer player = players.get(numberOfTimesTheChoiceIsDone % players.size());
            int numberCardToDeliver = leaderCardsToDeliver.size() / players.size();
            ArrayList<LeaderCard> cardToDeliver = new ArrayList<>(4);
            for (int i = 0; i < numberCardToDeliver; i++) {

                cardToDeliver.add(leaderCardsToDeliver.get(index++));

            }
            try {
                player.receiveLeaderCards(cardToDeliver);
                cardToDeliver.clear();
            } catch (NetworkException e) {
                Debug.printError("ERROR: cannot deliver the leader cards to " + player);
                LOGGER.log(Level.SEVERE, "cannot deliver the leader cards", e);
                addToDisconnectedPlayers(player);
                floodPlayerDisconnected(player);
            }

        }
    }

    /**
     * this method is used to receive the leader card chose by the player
     * @param leaderCard the leader card chosen
     */
    public synchronized void receiveLeaderCards(LeaderCard leaderCard, AbstractConnectionPlayer player) {
        Debug.printVerbose("[Room] receiveLeaderCards called");
        controllerGame.choiceLeaderCard(leaderCard, player);
        for (LeaderCard card : cardToPlayer) {
            if (card.getName().equals(leaderCard.getName())) {
                cardToPlayer.remove(card);
                Debug.printVerbose("eliminated " + leaderCard.getName());
                break;
            }
        }

        floodReceiveLeaderCard(leaderCard, player);

        deliverLeaderCardsToPlayers();

    }

    /**
     * this method is called to deliver to the other player the leader card chose by a player
     * @param leaderCard the chosen leader card by the player
     * @param player the player that had chosen the leader card
     */
    private void floodReceiveLeaderCard(LeaderCard leaderCard, AbstractConnectionPlayer player) {

        for(AbstractConnectionPlayer playerIter : players){
            if(!player.getNickname().equals(playerIter.getNickname()) && !disconnectedPlayers.contains(playerIter)){
                try{
                    playerIter.deliverLeaderChose(leaderCard, player);
                }
                catch (NetworkException e){
                    Debug.printError("tried to deliver the chosen leader cards to " + player.getNickname());
                    LOGGER.log(Level.SEVERE, "failed to deliver the chosen leader", e);
                    addToDisconnectedPlayers(playerIter);
                    floodPlayerDisconnected(playerIter);
                }
            }
        }
    }

    /**
     * this method is called by controllerGame and deliver to all the clients on the room the cards to place on the board
     */
    public synchronized void deliverCardToPlace(ArrayList<AbstractCard> cards) {
        for (AbstractConnectionPlayer player : players) {
            if(!disconnectedPlayers.contains(player)) {
                try {
                    player.deliverCardToPlace(cards);
                } catch (IOException e) {
                    Debug.printError("tried to deliver the cards to " + player.getNickname());
                    LOGGER.log(Level.SEVERE, "failed to deliver the chosen leader", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }

        }
    }

    /**
     * this method is used to inform the other players that a player had placed a family member on the council
     * @param familyMember the family member place on the council
     */
    private void floodPlaceOnCouncil(FamilyMember familyMember, HashMap<String, Integer> playerChoices) {

        for (AbstractConnectionPlayer player : players) {
            if (!familyMember.getPlayer().getNickname().equals(player.getNickname())
                    && !disconnectedPlayers.contains(player)) { //we don't want to sent it to a player disconnected
                try {
                    player.receivePlaceOnCouncil(familyMember, playerChoices);
                } catch (NetworkException e) {
                    Debug.printError("tried to deliver move on council to " + player.getNickname());
                    LOGGER.log(Level.SEVERE, "failed to deliver move on council", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is used to deliver to the clients the personale tiles he can choose
     */
    public void deliverPersonalTiles(ArrayList<PersonalTile> personalTiles) {

        ArrayList<PersonalTile> personalTilesToDeliver = new ArrayList<>(2);
        int takeStandard = 0;
        int takeSpecial = 0;
        PersonalTile specialTile = null;
        for (AbstractConnectionPlayer player : players){
            if (!disconnectedPlayers.contains(player)) {
                for (PersonalTile personalTile : personalTiles) {

                    if (personalTile.getPersonalTileEnum() == PersonalTileEnum.STANDARD && takeStandard == 0) {
                        personalTilesToDeliver.add(personalTile);
                        takeStandard = 1;
                    } else if (personalTile.getPersonalTileEnum() == PersonalTileEnum.SPECIAL && takeSpecial == 0) {
                        personalTilesToDeliver.add(personalTile);
                        specialTile = personalTile;
                        takeSpecial = 1;
                    }
                }
                personalTiles.remove(specialTile);
                try {

                    player.deliverPersonalTiles(personalTilesToDeliver);
                    takeSpecial = 0;
                    takeStandard = 0;
                    personalTilesToDeliver.clear();

                } catch (NetworkException e) {
                    Debug.printError("Cannot deliver the personal tiles to player : " + player, e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the personal tiles", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            } else {
                chosePersonalTile(personalTiles.get(0), player);
            }
        }
    }

    /**
     * this method is called by the connection to deliver the choice of the personal tile of the player
     * it deliver the choice to the controller of the game
     * @param personalTile the personal tile chose by the client
     */
    public synchronized void chosePersonalTile(PersonalTile personalTile, AbstractConnectionPlayer player) {

        controllerGame.choosePersonalTile(personalTile, player);
        floodChosenPersonalTile(personalTile, player);

    }

    private void floodChosenPersonalTile(PersonalTile personalTile, AbstractConnectionPlayer player){

        for(AbstractConnectionPlayer player1 : players){
            if((!player.getNickname().equals(player1.getNickname()))&& (!disconnectedPlayers.contains(player))){
                try{
                    player1.otherPlayerPersonalTile(player.getNickname(), personalTile);
                }
                catch (NetworkException e){
                    Debug.printError(e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the the chone personal tile", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is called by the controller game to deliver to the client of an error on a move
     * @param playerName is the nickname of the player
     */
    public void deliverError(String playerName) {

        for(AbstractConnectionPlayer player : players){
            if((player.getNickname().equals(playerName))&& !disconnectedPlayers.contains(player)){
                try{
                    player.deliverErrorMove();
                }
                catch (NetworkException e){
                    Debug.printError("cannot deliver the error move to " + playerName);
                    LOGGER.log(Level.SEVERE, "failed to deliver the error", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is called by the client to inform the server that had discrded a leader card
     * @param nameCard the name of the card the player had discarded
     * @param resourceGet the resource obtained
     */
    public void receiveDiscardLeaderCard(String nameCard, HashMap<String, Integer> resourceGet, AbstractConnectionPlayer player) {

        try{
            controllerGame.discardLeaderCard(player.getNickname(), nameCard, resourceGet);
            floodDiscardLeaderCard(nameCard, resourceGet, player.getNickname());
        }
        catch (IllegalMoveException e){
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }
    }

    /**
     * this method is used to deliver to the other players the action
     * @param nameCard the name of the leader card discarded
     * @param resourceGet the type of resources gotten
     * @param nickname the nickname o the player that had discrded the card
     */
    private void floodDiscardLeaderCard(String nameCard, HashMap<String, Integer> resourceGet, String nickname) {

        for(AbstractConnectionPlayer player : players){
            if((!player.getNickname().equals(nickname))&& !disconnectedPlayers.contains(player)){
                try{
                    player.deliverDiscardLeaderCard(nameCard, nickname, resourceGet);
                }

                catch (NetworkException e){
                    Debug.printError("cannot deliver the leader discarded to " + player.getNickname());
                    LOGGER.log(Level.SEVERE, "failed to deliver the the discard of leader card", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is used to deliver the information that a player has disconnected
     * @param player the player disconnected
     */
    public void disconnectedPlayer(AbstractConnectionPlayer player) {

        controllerGame.disconnectedPlayer(player.getNickname());
        players.remove(player);
        for(AbstractConnectionPlayer playerIter : players){
            try {
                playerIter.deliverDisconnectionPlayer(player.getNickname());
            }
            catch (NetworkException e){
                Debug.printError("cannot deliver the disconnection to the player " + playerIter.getNickname());
                LOGGER.log(Level.SEVERE, "failed to deliver the disconnection to the player", e);
                addToDisconnectedPlayers(player);
                floodPlayerDisconnected(player);
            }
        }
    }

    /**
     * this method is called by the network when a player had played a leader card
     * @param nameCard the name of the leader card played
     * @param choicesOnCurrentActionString the choices did while playing the card
     * @param player the player that had played the card
     * @param choicesOnCurrentAction are the choices on current action
     */
    public void playLeaderCard(String nameCard, HashMap<String, String> choicesOnCurrentActionString,
                               AbstractConnectionPlayer player, HashMap<String, Integer> choicesOnCurrentAction) {

        try{
            controllerGame.playLeaderCard(nameCard, choicesOnCurrentActionString, player, choicesOnCurrentAction);
            floodPlayLeaderCard(nameCard, choicesOnCurrentActionString, player.getNickname(), choicesOnCurrentAction);
        }
        catch (IllegalMoveException e){
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }

    }

    /**
     * this method is used to deliver to all the other players the information that a player had played a leader card
     * @param nameCard the name of the leader card played
     * @param choicesOnCurrentActionString the choices done on the leader card
     * @param nickname the nickname of the player that had played the leader card
     * @param choicesOnCurrentAction are the choices on current action
     */
    private void floodPlayLeaderCard(String nameCard, HashMap<String, String> choicesOnCurrentActionString,
                                     String nickname, HashMap<String, Integer> choicesOnCurrentAction) {

        for(AbstractConnectionPlayer player : players){
            if(!player.getNickname().equals(nickname)&& !disconnectedPlayers.contains(player) ){
                try {
                    player.deliverPlayLeaderCard(nameCard, choicesOnCurrentActionString, nickname,
                            choicesOnCurrentAction);
                }
                catch (NetworkException e){
                    Debug.printError("cannot deliver the leader card played by " + nickname + " to " + player.getNickname(),e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the leader card played", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is called by the network to inform the server that a player had activated a leader card's ability
     * @param nameCard the name of the leader card
     * @param resourceGet the resources gotten
     * @param player the player that had activated the leader
     */
    public void receiveActivatedLeader(String nameCard, HashMap<String, Integer> resourceGet, AbstractConnectionPlayer player) {
        try{
            controllerGame.activateLeaderCard(nameCard, resourceGet, player);
            floodActivatedLeaderCard(nameCard, resourceGet, player.getNickname());
        }
        catch (IllegalMoveException e){
            LOGGER.log(Level.SEVERE, ILLEGAL_MOVE_MESSAGE, e);
            handleErrorMove(player);
        }
    }

    /**
     * this method is used to deliver to the othe players the information that a player had activated a leader card's ability
     * @param nameCard the name of the leader card activated
     * @param resourceGet the resources gotten
     * @param nickname the player that had activated the leader
     */
    private void floodActivatedLeaderCard(String nameCard, HashMap<String, Integer> resourceGet, String nickname) {

        for(AbstractConnectionPlayer player : players){
            if(!player.getNickname().equals(nickname) && !disconnectedPlayers.contains(player)){
                try {
                    player.deliverActivatedLeaderCard(nameCard, resourceGet, nickname);
                }
                catch (NetworkException e){
                    Debug.printError("cannot deliver the leader card activated by " + nickname + " to " + player.getNickname(),e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the leader card activated", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
        }
    }

    /**
     * this method is called by controller game to deliver the end of the game to the players and th result of the game
     * @param playerPositionEndGames the results of the game(the winners, the victory points, the positions)
     */
    public void deliverEndGame(ArrayList<PlayerPositionEndGamePacket> playerPositionEndGames) {

        for(AbstractConnectionPlayer player : players){
            try {
                player.deliverEndGame(playerPositionEndGames);
            }
            catch (NetworkException e){
                Debug.printError("cannot deliver the end game results to " + player.getNickname(),e);
                LOGGER.log(Level.SEVERE, "failed to deliver the end game results", e);
                addToDisconnectedPlayers(player);
                floodPlayerDisconnected(player);
            }
        }
    }

    /**
     * this method is used to deliver the player excommunicated to the clients
     * @param nicknamePlayerExcommunicated the nickname of the player excommunicated
     */
    public void deliverExcommunication(ArrayList<String> nicknamePlayerExcommunicated, int numTile) {

        for(AbstractConnectionPlayer player : players){
            if(!disconnectedPlayers.contains(player)) {
                try {
                    player.deliverExcommunication(nicknamePlayerExcommunicated, numTile);
                } catch (NetworkException e) {
                    Debug.printError("cannot deliver the excommunication to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the excommunication to", e);
                    addToDisconnectedPlayers(player);
                    floodPlayerDisconnected(player);
                }
            }
            //if he's suspended we reply for him
            if(suspendedPlayers.contains(player) || disconnectedPlayers.contains(player))
                receiveExcommunicationChoice("NO", player);
        }
    }

    private void floodPlayerDisconnected(AbstractConnectionPlayer player) {
        for(AbstractConnectionPlayer playerIter : players) {
            if(!disconnectedPlayers.contains(playerIter)) {
                try {
                    playerIter.deliverDisconnectionPlayer(player.getNickname());
                } catch (NetworkException e) {
                    Debug.printError("cannot deliver the disconenction to " + player.getNickname(), e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the disconenction", e);
                    addToDisconnectedPlayers(playerIter);
                    floodPlayerDisconnected(playerIter);
                }
            }
        }
    }

    /**
     * this method is called by the network to deliver the choice on the excommunication
     * @param response the choice of the player on the excommunication
     * @param player the player tha had done the choice on the excommunication
     */
    public synchronized void receiveExcommunicationChoice(String response, AbstractConnectionPlayer player) {

        int numTile = controllerGame.getNumberOfRound()/2 - 1;
        controllerGame.receiveExcommunicationChoice(response, player.getNickname(), numTile);
        floodExcommunicationChoice(response, player.getNickname(), numTile);
    }

    /**
     * this method is used to deliver to the other players the excommunication choice
     * @param response the response of the player on the excommunication choice
     * @param nickname the nickname of the player that had done the choice
     * @param numTile the num of tile to take if the excommunication choice is to take the excommunication
     */
    private void floodExcommunicationChoice(String response, String nickname, int numTile) {

        for(AbstractConnectionPlayer playerIter : players){
            if(!playerIter.getNickname().equals(nickname) && !disconnectedPlayers.contains(playerIter)){
                try{
                    playerIter.deliverExcommunicationChoice(response,nickname, numTile);
                }
                catch (NetworkException e){
                    Debug.printError("cannot deliver the excommunication choice to " + nickname,e);
                    LOGGER.log(Level.SEVERE, "failed to deliver the excommunication choice to", e);
                    addToDisconnectedPlayers(playerIter);
                    floodPlayerDisconnected(playerIter);
                }
            }
        }
    }

    public void receiveReconnectPlayer(AbstractConnectionPlayer player) {
        suspendedPlayers.remove(player);

        for(AbstractConnectionPlayer playerIter : players){
            try {
                if(playerIter != player)
                    playerIter.deliverNotificationAnotherPlayerReconnected(player.getNickname());
            }
            catch (NetworkException e){
                Debug.printError("cannot deliver the reconnection to " + player.getNickname(),e);
                LOGGER.log(Level.SEVERE, "cannot deliver the reconnection", e);
                handleErrorMove(player);
                addToDisconnectedPlayers(player);
                floodPlayerDisconnected(player);
            }
        }
    }

    /**
     * This method is used by the player connections to signal that they failed to receive something
     * an error on the netowork occurred
     * @param player the player which encountered the error
     */
    public void signalConnectionError(AbstractConnectionPlayer player) {
        addToDisconnectedPlayers(player);
        floodPlayerDisconnected(player);
    }
}

