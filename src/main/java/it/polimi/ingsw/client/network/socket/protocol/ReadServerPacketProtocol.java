package it.polimi.ingsw.client.network.socket.protocol;

import it.polimi.ingsw.client.exceptions.NetworkException;
import it.polimi.ingsw.client.network.socket.SocketClient;
import it.polimi.ingsw.client.network.socket.packet.PacketType;
import it.polimi.ingsw.server.network.socket.protocol.FunctionResponse;
import it.polimi.ingsw.utils.Debug;

import java.util.HashMap;

/**
 * this is the protocol that the client uses to read the placket delivered by the server
 */
public class ReadServerPacketProtocol {
    /**
     * the player socket that has this protocol
     */
    private SocketClient client;
    /**
     * the different instruction to read the packet
     */
    private HashMap<PacketType, FunctionResponse> instruction;
    /**
     * this is the attribute used to obtain the response by the lambda function
     */
    private FunctionResponse response;

    public ReadServerPacketProtocol(SocketClient client){
        this.client=client;
        instruction = new HashMap<>(12);
        putIstruction();
    }

    /**
     * this method is used to save all the response based on the packetType
     */
    private void putIstruction() {
        instruction.put(PacketType.CHAT, () -> client.receiveChatMsg());
        instruction.put(PacketType.DICE, () -> client.receiveDices());
        instruction.put(PacketType.MOVE_IN_TOWER, ()-> client.receivePlaceOnTower());
        instruction.put(PacketType.MOVE_IN_MARKET, ()-> client.receivePlaceOnMarket());
        instruction.put(PacketType.HARVEST, ()-> client.receiveHarvest());
        instruction.put(PacketType.BUILD, ()-> client.receiveBuild());
        instruction.put(PacketType.GAME_BOARD, ()->client.receiveStartGameBoard());
        instruction.put(PacketType.DISCARD_LEADER, ()-> client.receiveDiscardLeaderCard());
        instruction.put(PacketType.PLAY_LEADER, ()-> client.receivePlayLeaderCard());
        instruction.put(PacketType.END_PHASE, ()-> client.receiveEndPhase());
        instruction.put(PacketType.START_TURN, ()-> client.startTurn());
        instruction.put(PacketType.ORDER_PLAYERS, ()-> client.receiveOrderPlayers());
        instruction.put(PacketType.NICKNAME, ()-> client.receiveClientNickname());
        instruction.put(PacketType.LEADER_CHOICES, ()-> client.receiveLeaderCards());
        instruction.put(PacketType.CARD_TO_PLACE, ()-> client.receiveCardToPlace());
        instruction.put(PacketType.ERROR_MOVE, ()-> client.receiveError());
        instruction.put(PacketType.MOVE_IN_COUNCIL, ()-> client.receivePlaceOnCouncil());
        instruction.put(PacketType.CHOSE_TILES, ()-> client.receivePersonalTiles());
        instruction.put(PacketType.FLOOD_PERSONAL_TILE, ()-> client.receiveFloodPersonalTile());
        instruction.put(PacketType.DISCONNECTION_PLAYER, ()-> client.receiveDisconnectionPlayer());
        instruction.put(PacketType.CHOSEN_LEADER, ()-> client.receiveChosenLeaderCard());
        instruction.put(PacketType.ACTIVATE_LEADER, ()-> client.receiveActivatedLeaderCard());
        instruction.put(PacketType.END_GAME, ()-> client.receiveEndGame());
        instruction.put(PacketType.EXCOMMUNICATION, ()-> client.receiveExcommunicatedPlayers());
        instruction.put(PacketType.EXCOMMUNICATION_CHOICE, ()-> client.receiveExcommunicationChoice());
        instruction.put(PacketType.PLAYER_SUSPENDED, () -> client.receiveNotificationPlayerSuspended());
        instruction.put(PacketType.PLAYER_RECONNECTED, () -> client.receivePlayerReconnected());
        instruction.put(PacketType.SERVER_DISCONNECTED, () -> client.receiveDisconnectionServer());

    }

    /**
     * this method is used to find the response based on the Packet type
     * @param packetType is like a header of the true packet, is used to understand how deserialize the follow packet
     */
    public void doMethod(PacketType packetType){
        Debug.printVerbose("Inside do method called:, packetType = " + packetType);
        response=instruction.get(packetType);
        response.chooseMethod();

    }
}
