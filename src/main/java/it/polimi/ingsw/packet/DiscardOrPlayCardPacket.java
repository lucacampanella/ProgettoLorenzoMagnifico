package it.polimi.ingsw.packet;

import java.io.Serializable;

/**
 * this packet is send when the player decides to use a leader card
 */
public class DiscardOrPlayCardPacket implements Serializable {
    private String nameLeaderCard;
    public DiscardOrPlayCardPacket(String nameLeaderCard){
        this.nameLeaderCard=nameLeaderCard;
    }
    public String getNameCard(){
        return nameLeaderCard;
    }
}
