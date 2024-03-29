package it.polimi.ingsw.client.network.socket;


import it.polimi.ingsw.client.controller.NetworkControllerClientInterface;
import it.polimi.ingsw.client.exceptions.*;
import it.polimi.ingsw.client.network.AbstractClientType;
import it.polimi.ingsw.client.network.socket.packet.*;
import it.polimi.ingsw.client.network.socket.protocol.ReadServerPacketProtocol;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.FamilyMember;
import it.polimi.ingsw.model.player.PersonalTile;
import it.polimi.ingsw.model.player.PersonalTileEnum;
import it.polimi.ingsw.utils.Debug;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SocketClient is the class of client that communicates to the server using the socket
 */
public class SocketClient extends AbstractClientType {
    /**
     *socket connected to the server
     */
    private Socket socketClient;
    /**
     * Information getted from the server
     */
    private ObjectInputStream inStream;
    /**
     * Informations delivered to the server
     */
    private ObjectOutputStream outStream;

    /**
     * the protocol used to read packet sedf by the server
     */
    private ReadServerPacketProtocol readPacket;

    /**
     * this is the parallel thread to receive packet from the server
     */
    private ReceiveInformation receiveInformation;

    /**
     *Initialization of the attributes on the superclass
     */
    public SocketClient (NetworkControllerClientInterface controllerMain, String serverAddress, int port) {
        super(controllerMain, serverAddress, port);
        Debug.printVerbose("New SocketClient created");
    }
    /**
     * override of the superMethod
     * @throws ClientConnectionException if the connection had failed
     */
    @Override
    public void connect() throws ClientConnectionException {
        try{
            socketClient = new Socket(getServerAddress(),getPort());
        }
        catch(IOException e){
            Debug.printError("Cannot connect socket client",e);
            throw new ClientConnectionException(e);
        }

        try {
            inStream = new ObjectInputStream(new BufferedInputStream(socketClient.getInputStream()));
            outStream = new ObjectOutputStream(new BufferedOutputStream(socketClient.getOutputStream()));
            outStream.flush();
            outStream.reset();

        } catch (IOException e) {
            Debug.printError("Cannot open socket streams", e);
            throw new ClientConnectionException("Cannot open socket streams", e);
        }
        readPacket= new ReadServerPacketProtocol(this);

        receiveInformation = new ReceiveInformation(inStream, readPacket);

    }
    /**
     * this method is used when an user already exists and decides to login with his username and password
     *
     * @param nickname
     * @param password
     * @throws NetworkException if something goes wrong during the connection
     * @throws LoginException   if username doesn't exist or if password is wrong
     */
    @Override
    public void loginPlayer(String nickname, String password) throws NetworkException, LoginException {

        LoginErrorEnum loginResponse;

        try {
            synchronized (this){
                outStream.writeObject(PacketType.LOGIN);
                outStream.writeObject(new LoginOrRegisterPacket(nickname, password));
                outStream.flush();
                outStream.reset();
            }

            loginResponse = (LoginErrorEnum) inStream.readObject();
        }
        catch(IOException | ClassNotFoundException e){
            Debug.printError("Connection not available",e);
            throw new NetworkException(e);
        }
        if(loginResponse==LoginErrorEnum.ALREADY_LOGGED_TO_ROOM ||
                loginResponse==LoginErrorEnum.NOT_EXISTING_USERNAME ||
                loginResponse==LoginErrorEnum.WRONG_PASSWORD){
            throw new LoginException(loginResponse);
        }
        // start the thread to read the packet delivered by the server
        receiveInformation.start();
    }

    /**
     * this method is used when the user has never played and wants to create an account
     *
     * @param nickname to register in the server DB
     * @param password to register in the server DB
     * @throws NetworkException if something goes wrong during the connection
     */
    @Override
    public void registerPlayer(String nickname, String password) throws NetworkException,UsernameAlreadyInUseException {
        RegisterErrorEnum response;
        try{
            synchronized (this){
                outStream.writeObject(PacketType.REGISTER);
                outStream.writeObject(new LoginOrRegisterPacket(nickname,password));
                outStream.flush();
                outStream.reset();
            }

            response =(RegisterErrorEnum)inStream.readObject();
        }
        catch(IOException | ClassNotFoundException e){
            Debug.printError("connection not available",e);
            throw new NetworkException(e);
        }
        if(response == RegisterErrorEnum.ALREADY_EXISTING_USERNAME){
            throw new UsernameAlreadyInUseException("Username already in use");
        }
        // start the thread to read the packet delivered by the server
        receiveInformation.start();
    }

    /**
     * this method is used to deliver a leader card that the client wants to play
     * @throws NetworkException if something goes wrong during the connection
     */
    @Override
    public void playLeaderCard(String nameLeader, HashMap<String, String> choicesOnCurrentActionString,
                               HashMap<String, Integer> choicesOnCurrentAction) throws NetworkException{
        try{
            synchronized (this){
                outStream.writeObject(PacketType.PLAY_LEADER);
                outStream.writeObject(new PlayLeaderCardPacket(nameLeader, choicesOnCurrentActionString,
                        choicesOnCurrentAction));
                outStream.flush();
                outStream.reset();
            }

        }
        catch(IOException e){
            Debug.printError("network is not available",e);
            throw new NetworkException(e);

        }
    }



    /**
     * this method is used to discard a leader card
     * @param nameLeader is the name of the card
     * @param resourceChoose is the resource chose to obtain when the leader is sacrificed
     */
    @Override
    public void discardLeaderCard(String nameLeader, HashMap<String, Integer> resourceChoose) throws NetworkException{

        try {
            synchronized (this){
                outStream.writeObject(PacketType.DISCARD_LEADER);
                outStream.writeObject(new DiscardAndActivateLeaderCardPacket(nameLeader, resourceChoose));
                outStream.flush();
                outStream.reset();
            }

        }
        catch (IOException e) {
            Debug.printError("Cannot write object to output socket stream", e);
            throw new NetworkException("Cannot write object to output on socket stream", e);
        }

    }

    /**
     * this method is used to move the family member on the towers
     * @param familyMember the family member used on the action
     * @param numberTower number of the tower
     * @param floorTower floor of the tower
     */
    @Override
    public void placeOnTower(FamilyMember familyMember,
                             int numberTower, int floorTower, HashMap<String, Integer> playerChoices)
            throws NetworkException{

        try{
            synchronized (this){
                outStream.writeObject(PacketType.MOVE_IN_TOWER);
                outStream.writeObject(new PlaceOnTowerPacket(familyMember.getColor(),numberTower,floorTower, playerChoices));
                outStream.flush();
                outStream.reset();
            }


        }
        catch (IOException e){
            Debug.printError("Connection not available",e);
            throw new NetworkException(e);

        }
    }

    /**
     * this method is used when the family member in moved on a generic market space
     */
    @Override
    public void placeOnMarket(FamilyMember familyMember, int marketIndex, HashMap<String, Integer> playerChoices)
            throws NetworkException{

        try{
            synchronized (this){
                outStream.writeObject(PacketType.MOVE_IN_MARKET);
                outStream.writeObject(new PlaceOnMarketPacket(familyMember.getColor(), marketIndex, playerChoices));
                outStream.flush();
                outStream.reset();
            }


        }
        catch(IOException e){
            Debug.printError("network is not available",e);
            throw new NetworkException(e);
        }

    }

    /**
     * this method is called by the client to deliver a move on a council place
     * @param familyMember the family member moved
     * @param playerChoices the choices of the resource to get
     * @throws NetworkException if something goes wrong with the connection
     */
    @Override
    public void placeOnCouncil(FamilyMember familyMember, HashMap<String, Integer> playerChoices) throws NetworkException {

        try{
            synchronized (this){
                outStream.writeObject(PacketType.MOVE_IN_COUNCIL);
                outStream.writeObject(new PlaceOnCouncilPacket(familyMember.getColor(), playerChoices));
                outStream.flush();
                outStream.reset();
            }

        }

        catch (IOException e){
            Debug.printError("network is not available",e);
            throw new NetworkException(e);
        }
    }

    /**
     * this method is called when the family member is moved on the harvest space
     */
    @Override
    public void harvest (FamilyMember familyMember, int servantUsed, HashMap<String, Integer> playerChoices) throws NetworkException {

        try{

            synchronized (this){
                outStream.writeObject(PacketType.HARVEST);
                outStream.writeObject(new BuildOrHarvest(familyMember.getColor(),servantUsed,playerChoices));
                outStream.flush();
                outStream.reset();
            }


        }

        catch (IOException e){

            Debug.printError("network is not available", e);
            throw new NetworkException(e);

        }

    }

    /**
     * this method is called when a player want to place a family member on the build
     * @param playerChoices this is a map that contains all the choices of the client when an effect asks
     * @throws NetworkException
     */
    @Override
    public void build (FamilyMember familyMember, int servantUsed, HashMap<String, Integer> playerChoices)
            throws NetworkException{

        try{

            synchronized (this){
                outStream.writeObject(PacketType.BUILD);
                outStream.writeObject(new BuildOrHarvest(familyMember.getColor(),servantUsed,playerChoices));
                outStream.flush();
                outStream.reset();
            }


        }

        catch (IOException  e){

            Debug.printError("network is not available", e);
            throw new NetworkException(e);

        }

    }

    /**
     * this method is called by the client to deliver to the server the tile chosen by the player
     * @param tileChosen the tile chosen by the player to play
     * @throws NetworkException if something goes wrong with the network
     */
    @Override
    public void deliverTileChosen(PersonalTile tileChosen) throws NetworkException {
        try{
            Debug.printVerbose("Delivered personal tile");
            synchronized (this){
                outStream.writeObject(PacketType.CHOSE_TILES);
                outStream.writeObject(tileChosen);
                outStream.flush();
                outStream.reset();
            }

        }
        catch (IOException e){
            throw new NetworkException(e);
        }
    }

    /**
     * this method is called by the client main to deliver to the server that this player
     * has activated the effect of a leader card
     * @param leaderName the name of the leader card
     * @param choicesOnCurrentAction the choices done on the effect
     * @throws NetworkException
     */
    @Override
    public void activateLeaderCard(String leaderName, HashMap<String, Integer> choicesOnCurrentAction) throws NetworkException {

        try{
            synchronized (this){
                outStream.writeObject(PacketType.ACTIVATE_LEADER);
                outStream.writeObject(new DiscardAndActivateLeaderCardPacket(leaderName, choicesOnCurrentAction));
                outStream.flush();
                outStream.reset();
            }

        }
        catch (IOException e){
            throw new NetworkException(e);
        }
    }

    /**
     * this method is called by the client to deliver to the server the choices one on the excommunication
     * @param response the choice of the client
     * @throws NetworkException if omething goes wrong with the network
     */
    @Override
    public void excommunicationChoice(String response) throws NetworkException {

        try{

            synchronized (this){
                outStream.writeObject(PacketType.EXCOMMUNICATION_CHOICE);
                outStream.writeObject(response);
                outStream.flush();
                outStream.reset();
            }

            Debug.printVerbose("delivered the excommunication choice");

        }

        catch (IOException e){

            Debug.printError("network is not available", e);
            throw new NetworkException(e);

        }
    }

    /**
     * This method is called by the controller when the player suspended makes a new input and thus wants to be
     * reconnected
     * @throws NetworkException if something goes wrong during the connection
     */
    @Override
    public void reconnectPlayer() throws NetworkException {
        try{

            synchronized (this){
                outStream.writeObject(PacketType.RECONNECT_PLAYER_SUSPENDED);
            }
            outStream.flush();

            Debug.printVerbose("delivered the reconnection of player");

        }

        catch (IOException e){

            Debug.printError("network is not available", e);
            throw new NetworkException(e);

        }
    }


    /**
     * this method is used to inform the room that the player had ended his phase
     */
    @Override
    public void endPhase() throws NetworkException{

        try{

            synchronized (this){
                outStream.writeObject(PacketType.END_PHASE);
                outStream.flush();
                outStream.reset();
            }

            Debug.printVerbose("delivered the end phase");

        }

        catch (IOException e){

            Debug.printError("network is not available", e);
            throw new NetworkException(e);

        }
    }

    /**
     * This method is used to send chat message to all players in the room
     * @throws NetworkException
     */
    @Override
    public void sendChatMsg(String msg) throws NetworkException {
        try{
            synchronized (this){
                outStream.writeObject(PacketType.CHAT);
                outStream.writeObject(msg);
                outStream.flush();
                outStream.reset();
            }

        }
        catch (IOException e){
            Debug.printError("network is not available", e);
            throw new NetworkException(e);
        }
    }

    /**
     * this method is used to receive message from the other players
     */
    public void receiveChatMsg(){

        try{

            ReceiveChatPacket packet = (ReceiveChatPacket)inStream.readObject();
            getControllerMain().receiveChatMsg(packet.getNickname(), packet.getMessage());

        }
        catch(IOException | ClassNotFoundException e){

            Debug.printError("network is not available",e);

        }
    }

    /**
     * this method is used to receive moves on tower from other players
     */
    public void receivePlaceOnTower(){

        try {

            ReceivePlaceOnTowerPacket packet = (ReceivePlaceOnTowerPacket)inStream.readObject();
            getControllerMain().receivedPlaceOnTower(packet.getNickname(),
                    packet.getFamilyMemberColor(),
                    packet.getTowerIndex(),
                    packet.getFloorIndex(),
                    packet.getPlayersChoices());

        }

        catch (ClassNotFoundException | IOException e){

            Debug.printError("cannot receive the end of the phase of another player");
        }

    }

    /**
     * this method is used to receive moves on market from other players
     */
    public void receivePlaceOnMarket(){

        try {

            ReceivePlaceOnMarketPacket packet = (ReceivePlaceOnMarketPacket)inStream.readObject();
            getControllerMain().receivedPlaceOnMarket(packet.getNickname(),
                    packet.getFamilyMemberColor(),
                    packet.getMarketIndex(),
                    packet.getPlayerChoices());

        }

        catch (ClassNotFoundException | IOException e){

            Debug.printError("cannot receive the market move of another player");
        }
    }

    /**
     * this method is used to receive moves on harvest from other players
     */
    public void receiveHarvest(){

        try {

            ReceiveBuildOrHarvestPacket packet = (ReceiveBuildOrHarvestPacket)inStream.readObject();
            getControllerMain().receivedHarvest(packet.getNickname(),
                    packet.getFamilyMemberColor(),
                    packet.getServantUsed(),
                    packet.getPlayerChoices());

        }

        catch (ClassNotFoundException | IOException e){

            Debug.printError("cannot receive the harvest move of another player");
        }

    }

    /**
     * this method is used to receive moves on build from other players
     */
    public void receiveBuild(){

        try {

            ReceiveBuildOrHarvestPacket packet = (ReceiveBuildOrHarvestPacket)inStream.readObject();
            getControllerMain().receivedBuild(packet.getNickname(),
                    packet.getFamilyMemberColor(),
                    packet.getServantUsed(),
                    packet.getPlayerChoices());

        }

        catch (ClassNotFoundException | IOException e){

            Debug.printError("cannot receive the build move of another player");
        }

    }

    /**
     * this method is used to receive the end of turn of the other players
     */
    public void receiveEndPhase(){

        try {

            EndPhasePacket packet = (EndPhasePacket)inStream.readObject();
            getControllerMain().receiveEndPhase(packet.getNickname());

        }

        catch (ClassNotFoundException | IOException e){

            Debug.printError("cannot receive the end of the phase of another player");
        }
    }

    /**
     * this method is used to receive the new dices loaded by the server
     */
    public void receiveDices(){
        Debug.printVerbose("receiveDices called");
        try{

            DicesPacket packet = (DicesPacket)inStream.readObject();
            Debug.printVerbose("receivedDices packet");
            getControllerMain().receivedDices(packet.getDices());
            Debug.printVerbose("called");
        }
        catch (IOException | ClassNotFoundException e){

            Debug.printError("Error: cannot receive the dices loaded by the server", e);
        }
    }

    /**
     * this method is used to receive the initial board loaded by the server
     */
    public void receiveStartGameBoard(){

        try{

            Board board = (Board)inStream.readObject();
            getControllerMain().receivedStartGameBoard(board);
        }

        catch (IOException | ClassNotFoundException e){

            Debug.printError("Error: cannot receive the initial board", e);
        }
    }

    /**
     * this method is called by the server to inform the client that can start his turn
     */
    public void startTurn(){
        getControllerMain().receivedStartTurnNotification();
    }

    /**
     * this method is called by the server to deliver the new order of players
     */
    public void receiveOrderPlayers(){

        try {
            ArrayList<String> orderPlayers = (ArrayList<String>)inStream.readObject();
            getControllerMain().receivedOrderPlayers(orderPlayers);
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("Error: cannot receive the order of the players", e);
        }
    }

    /**
     * this method is called by the server to deliver to the client the nickname used to play the game
     */
    public void receiveClientNickname(){

        try{
            String nickname = (String)inStream.readObject();
            returnNickname(nickname);
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("Error: cannot receive the nickname of the client", e);
        }
    }

    /**
     * this method is used to receive the leader cards at the beginning of the game, and the client has to choose one of them
     */
    public void receiveLeaderCards(){
        Debug.printVerbose("receiveLeaderCards() called");
        try{
            LeaderChoicePacket packet = (LeaderChoicePacket)inStream.readObject();
            getControllerMain().receivedLeaderCards(packet.getLeaderCards());
        }
        catch(IOException | ClassNotFoundException e){
            Debug.printError("ERROR: client can not receive the leader cards",e);
        }
    }

    /**
     * this method is used by the client to deliver to the server the leader that wants to have
     */
    @Override
    public void deliverLeaderChose(LeaderCard leaderCard) throws NetworkException{

        try{
            synchronized (this){
                outStream.writeObject(PacketType.LEADER_CHOICES);
                outStream.writeObject(new ChosenLeaderPacket(leaderCard));
                outStream.flush();
                outStream.reset();
            }

            Debug.printVerbose("Packet on leader choice sent " + leaderCard.getName());
        }
        catch (IOException e){
            Debug.printError("the client cannot deliver the leader he has chosen",e);
            throw new NetworkException(e);
        }
    }

    /**
     * this method is called by the server to place the cards on the board
     */
    public void receiveCardToPlace(){

        try{
            CardToPlacePacket packet = (CardToPlacePacket)inStream.readObject();
            getControllerMain().receiveCardsToPlace(packet.getCards());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the cards delivered by the server",e);
        }
    }

    /**
     * this method is called by the server to inform the client that anothe player had placed a family member on the council
     */
    public void receivePlaceOnCouncil(){
        try{
            ReceivePlaceOnCouncilPacket packet = (ReceivePlaceOnCouncilPacket)inStream.readObject();
            getControllerMain().receivedPlaceOnCouncil(packet.getNicknamePlayer(),
                    packet.getFamilyMemberColor(),
                    packet.getPlayerChoices());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives place on council from the server",e);
        }
    }

    /**
     * this method is called when the move delivered to the server caused errors
     */
    public void receiveError(){
        //TODO
    }

    /**
     * this method is called by the server, receives the personal tiles the client can choose
     */
    public void receivePersonalTiles(){
        try{
            ArrayList<PersonalTile> personalTiles = (ArrayList<PersonalTile>)inStream.readObject();
            PersonalTile standardPersonalTile = null;
            PersonalTile specialPersonalTile = null;
            for(PersonalTile personalTile : personalTiles){
                if(personalTile.getPersonalTileEnum() == PersonalTileEnum.STANDARD)
                    standardPersonalTile = personalTile;
                else
                    specialPersonalTile = personalTile;
            }
            getControllerMain().receivedPersonalTiles(standardPersonalTile, specialPersonalTile);
        }

        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the personal tiles delivered from the server",e);
        }
    }

    /**
     * this method is called by the server to receive the personal tile chosen by players
     */
    public void receiveFloodPersonalTile(){
        try{
            ReceiveChosenPersonalTilePacket packet = (ReceiveChosenPersonalTilePacket)inStream.readObject();
            getControllerMain().receiveFloodPersonalTile(packet.getNickname(), packet.getPersonalTile());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("cannot receive the personal tile from other player",e);
        }
    }

    /**
     * this method iscused to receive the leader cards from other players
     */
    public void receiveDiscardLeaderCard(){

        try{
            ReceiveDiscardOrActivatedLeaderCardPacket packet = (ReceiveDiscardOrActivatedLeaderCardPacket)inStream.readObject();
            getControllerMain().receivedDiscardLeaderCard(packet.getNickname(), packet.getNameCard(), packet.getResourceGet());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the discard card receive from the server",e);
        }
    }

    /**
     * this method is called by the server to inform the client that a player had left the game
     */
    public void receiveDisconnectionPlayer(){

        try{
            String nicknamePlayerDisconnected = (String)inStream.readObject();
            getControllerMain().receivedPlayerDisconnected(nicknamePlayerDisconnected);
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the disconnection of a player",e);
        }

    }

    /**
     * this method is used to receive the leader cards played by the other players
     */
    public void receivePlayLeaderCard(){

        try{
            ReceivePlayLeaderCardPacket packet = (ReceivePlayLeaderCardPacket)inStream.readObject();
            getControllerMain().receivePlayLeaderCard(packet.getNameCard(), packet.getChoicesOnCurrentActionString(),
                    packet.getNickname(), packet.getChoicesOnCurrentAction());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the leader card played by another player",e);
        }
    }

    /**
     * this method is used to receive the chosen leader cards of the other players
     */
    public void receiveChosenLeaderCard(){

        try{
            ReceiveChosenLeaderPacket packet = (ReceiveChosenLeaderPacket)inStream.readObject();
            getControllerMain().receiveChosenLeader(packet.getNickname(), packet.getLeaderCard());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the leader card played by another player",e);
        }
    }

    /**
     * this method is called by the server to inform the client that anothe player had activated a leader card's effect
     */
    public void receiveActivatedLeaderCard() {

        try{
            ReceiveDiscardOrActivatedLeaderCardPacket packet = (ReceiveDiscardOrActivatedLeaderCardPacket)inStream.readObject();
            getControllerMain().receiveActivateLeaderCard(packet.getNickname(), packet.getNameCard(), packet.getResourceGet());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the leader card activated by another player",e);
        }
    }

    /**
     * this method is called by the server to deliver the results of the end of the game
     */
    public void receiveEndGame(){

        try{
            ArrayList<PlayerPositionEndGamePacket> packet = (ArrayList<PlayerPositionEndGamePacket>)inStream.readObject();
            getControllerMain().receiveEndGame(packet);
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the results of the end of the game",e);
        }
    }

    /**
     * this method is called by the server to deliver the excommunicated players
     */
    public void receiveExcommunicatedPlayers(){

        try{
            ExcommunicationPacket packet = (ExcommunicationPacket)inStream.readObject();
            getControllerMain().receiveExcommunicatedPlayers(packet.getNicknamePlayersExcommunicated(), packet.getNumTile());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the players excommunicated",e);
        }
    }

    /**
     * this method is called by the server to deliver to the client the choice done by another player
     */
    public void receiveExcommunicationChoice(){

        try{
            ReceiveExcommunicationChoicePacket packet = (ReceiveExcommunicationChoicePacket)inStream.readObject();
            getControllerMain().manageExcommunicationChoice(packet.getNickname(), packet.getResponse(), packet.getNumTile());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the excommunication choice of another player",e);
        }

    }

    /**
     * this method is called by the server to deliver to the client the suspension of a player caused by timout on move
     */
    public void receiveNotificationPlayerSuspended() {

        try{
            ReceivePlayerNicknamePacket packet = (ReceivePlayerNicknamePacket)inStream.readObject();
            getControllerMain().receivedNotificationSuspendedPlayer(packet.getNickname());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the excommunication choice of another player",e);
        }

    }

    /**
     * this method is called by the server to norify to the client the reconnection of a suspended player
     */
    public void receivePlayerReconnected() {
        try{
            ReceivePlayerNicknamePacket packet = (ReceivePlayerNicknamePacket)inStream.readObject();
            getControllerMain().receivedNotificationReconnectedPlayer(packet.getNickname());
        }
        catch (IOException | ClassNotFoundException e){
            Debug.printError("the client cannot receives the excommunication choice of another player",e);
        }
    }

    /**
     * this method is called when the server is disconnected
     */
    public void receiveDisconnectionServer(){

        getControllerMain().receivedDisconnectionServer();

    }
}
