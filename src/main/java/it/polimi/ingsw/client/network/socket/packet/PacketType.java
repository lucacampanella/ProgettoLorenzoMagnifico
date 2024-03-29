package it.polimi.ingsw.client.network.socket.packet;

import java.io.Serializable;

/**
 * type of packet that can be send
 */
public enum  PacketType implements Serializable {

    LOGIN, REGISTER, MOVE_IN_TOWER, MOVE_IN_MARKET, HARVEST, BUILD, DISCARD_LEADER, PLAY_LEADER, CHAT, END_PHASE,
    DICE, GAME_BOARD, START_TURN, ORDER_PLAYERS, NICKNAME, LEADER_CHOICES, CARD_TO_PLACE, MOVE_IN_COUNCIL, ERROR_MOVE,
    CHOSE_TILES, FLOOD_PERSONAL_TILE, DISCONNECTION_PLAYER, CHOSEN_LEADER, ACTIVATE_LEADER, END_GAME, EXCOMMUNICATION,
    EXCOMMUNICATION_CHOICE, RECONNECT_PLAYER_SUSPENDED, PLAYER_RECONNECTED, SERVER_DISCONNECTED, PLAYER_SUSPENDED

}
