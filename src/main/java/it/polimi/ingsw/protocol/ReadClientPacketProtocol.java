package it.polimi.ingsw.protocol;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import it.polimi.ingsw.packet.PacketType;
import it.polimi.ingsw.server.SocketPlayer;
import it.polimi.ingsw.utils.Debug;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by federico on 18/05/2017.
 */
public class ReadClientPacketProtocol {
    /**
     * the player socket that has this protocol
     */
    private SocketPlayer player;
    /**
     * the different instruction to read the packet
     */
    private HashMap<PacketType, FunctionResponse> instruction;
    /**
     * this is the attribute used to obtain the response by the lambda function
     */
    private FunctionResponse response;

    public ReadClientPacketProtocol(SocketPlayer player){
        this.player=player;
        //putIstruction();
    }

    /**
     * this method is used to save all the response based on the packetType
     */
    private void putIstruction(){
        instruction.put(PacketType.LOGIN, ()-> player.loginPlayer());
        instruction.put(PacketType.REGISTER, ()-> player.registerPlayer());
        instruction.put(PacketType.MOVE_IN_TOWER, ()-> player.moveInTower());
        instruction.put(PacketType.MOVE_IN_MARKET, ()-> player.moveInMarket());
        instruction.put(PacketType.HARVESTING, ()-> player.harvesting());
        instruction.put(PacketType.BUILDING, ()-> player.building());
        instruction.put(PacketType.DISCARD_LEADER, ()-> player.discardCard());
        instruction.put(PacketType.PLAY_LEADER, ()-> player.playCard());
        instruction.put(PacketType.CHAT, ()-> player.floodChatMsg());
        instruction.put(PacketType.END_PHASE, ()-> player.endPhase());
    }
    public void doMethod(PacketType packetType){

        response=instruction.get(packetType);
        try {
            response.chooseMethod();
        }
        catch(IOException | ClassNotFoundException e){
            Debug.printError("Network is not working",e);
        }

    }


}
