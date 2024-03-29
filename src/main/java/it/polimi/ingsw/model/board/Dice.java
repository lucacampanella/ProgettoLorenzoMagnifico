package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.player.DiceAndFamilyMemberColorEnum;

import java.io.Serializable;
import java.util.Random;

/**
 * this class has the value of different dices
 */
public class Dice implements Serializable{

    /**
     * color of the dice (and of the family member)
     */
    private DiceAndFamilyMemberColorEnum color;

    /**
     * random is an attribute to use random method
     */
    private Random random;

    /**
     * value of the dice
     */
    private int value;
    //this is the constructor
    public Dice(DiceAndFamilyMemberColorEnum color){
        this.color=color;
        random= new Random();
    }

    /**
     * throw the dice to obtain a new value (1-6)
     * @return the value of the dice
     */
    public void throwDice() {

        if(color == DiceAndFamilyMemberColorEnum.NEUTRAL)
            value=0;
        else
            value = random.nextInt(6)+1;

    }

    public DiceAndFamilyMemberColorEnum getColor(){
        return color;
    }

    public int getValue(){
        return  value;
    }

    /**
     * this method is called by the client to set the value of the dice delivered by the server
     */
    public void setValue(int value){
        this.value = value;
    }
}
