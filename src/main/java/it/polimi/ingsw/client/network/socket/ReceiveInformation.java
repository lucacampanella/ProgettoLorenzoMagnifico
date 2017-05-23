package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.network.socket.packet.PacketType;
import it.polimi.ingsw.client.network.socket.protocol.ReadServerPacketProtocol;
import it.polimi.ingsw.utils.Debug;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * this thread is used by the client to take the game's updates
 */
public class ReceiveInformation extends Thread {

    ObjectInputStream inStream;
    ReadServerPacketProtocol readPacket;

    public ReceiveInformation(ObjectInputStream inStream, ReadServerPacketProtocol readPacket){
        this.inStream=inStream;
        this.readPacket=readPacket;
    }

    /**
     * this method is used by the client to be always ready to receive updates
     */
    public void run(){
        while(true){
            try {
                PacketType packet = (PacketType) inStream.readObject();
                readPacket.doMethod(packet);
            }
            catch(IOException | ClassNotFoundException e){
                Debug.printError("ERROR : controller had failed to deliver new information");
            }
        }
    }
}