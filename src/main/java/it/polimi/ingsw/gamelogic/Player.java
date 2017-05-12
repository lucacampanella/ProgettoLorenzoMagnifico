package it.polimi.ingsw.gamelogic;

/**
 * The main player class, no network
 */
public class Player {

    private String nickname;

    public Player(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return nickname;
    }
}