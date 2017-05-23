package it.polimi.ingsw.client.network.socket.packet;

import it.polimi.ingsw.model.player.DiceAndFamilyMemberColor;

import java.io.Serializable;

/**
 * Created by federico on 16/05/2017.
 */
public class MovePacket implements Serializable {
    private DiceAndFamilyMemberColor familyMemberColor;
    private int servantUsed;
    public MovePacket(DiceAndFamilyMemberColor familyMemberColor, int servantUsed){
        this.familyMemberColor=familyMemberColor;
        this.servantUsed=servantUsed;
    }

    public DiceAndFamilyMemberColor getFamilyMemberColor() {
        return familyMemberColor;
    }

    public int getServantUsed() {
        return servantUsed;
    }
}