package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.resource.Resource;
import it.polimi.ingsw.model.resource.ResourceTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main player class, no network
 */
public abstract class Player {

    private String nickname;

    private PersonalBoard personalBoard;

    private HashMap<ResourceTypeEnum, Integer> resource;

    private ArrayList<FamilyMember> familyMembers;

    private ArrayList<FamilyMember> usedFamilyMembers;

    //private ArrayList<LeaderCard> leaderCard;

    //private ArrayList<LeaderCard> playedLeaderCard;

    //private ArrayList<ExcommuncationCard> excommuncationCard;

    public Player()
    {
        super();
        loadPlayer();
    }

    public Player(String nickname)
    {
        loadPlayer();
        setNickname(nickname);
    }

    private void loadPlayer(){

        personalBoard = new PersonalBoard();
        //TODO CHOOSE TILES OF PERSONAL BOARD
        resource = new HashMap<>();
        loadResource();
        familyMembers = new ArrayList<>(4);
        usedFamilyMembers = new ArrayList<>(4);
        //excommunicanionCard = new ArrayList<>(3);
        //leaderCard = new ArrayList<>(3);

    }

    /**
     * you load all the resources needed by the player
     */
    private void loadResource(){
        resource.put(ResourceTypeEnum.COIN, 0);
        resource.put(ResourceTypeEnum.WOOD, 0);
        resource.put(ResourceTypeEnum.STONE, 0);
        resource.put(ResourceTypeEnum.SERVANT, 0);
        resource.put(ResourceTypeEnum.FAITH_POINT, 0);
        resource.put(ResourceTypeEnum.MILITARY_POINT, 0);
        resource.put(ResourceTypeEnum.VICTORY_POINT, 0);
    }

    public void addResource(Resource resource){

        Integer valueNow = this.resource.get(resource.getType());
        this.resource.put(resource.getType(),this.resource.get(resource.getType())+resource.getValue());

    }
    //TODO: we need to put Cards Containers in Player and then implement this method.
    public int getNumberOfColoredCard(CardColorEnum color)
    {
        return personalBoard.getNumberOfColoredCard(color);
    }

    /*public void excommunication(ExcommunicationCard card){

        excommunicationCard.add(card);
    }*/

    public int getResource(ResourceTypeEnum type){

        return resource.get(type);

    }

    public String getNickname()
    {
        return nickname;
    }

    protected void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFamilyMembers(ArrayList<Dice> dice){

        for(Dice i : dice)
            this.familyMembers.add(new FamilyMember(i,this));

    }

    public void playFamilyMember(FamilyMember familyMember){

        this.familyMembers.remove(familyMember);
        this.usedFamilyMembers.add(familyMember);

    }

    public void resetFamilyMember(){

        familyMembers.addAll(usedFamilyMembers);
        usedFamilyMembers.clear();

    }

    public void addCard(AbstractCard card, CardColorEnum color){

        personalBoard.addCard(card, color);

    }

    /*public void addLeaderCard(LeaderCard leaderCard){

        this.leaderCard.add(leaderCard);
    }

    public ArrayList<LeaderCard> viewLeaderCard(){

        return leaderCard;
    }

    public void activateLeaderCard(LeaderCard leaderCard){

        this.leaderCard.remove(leaderCard);
        playedLeaderCard.add(leaderCard);

    }

    public void discardLeaderCard(LeaderCard leaderCard){

        this.leaderCard.remove(leaderCard);
        //TODO get bonus
    }*/

    public void harvest(int valueHarvest){

    }

    public void build(int valueBuild){

    }

    public ArrayList<FamilyMember> getFamilyMembers(){
        return familyMembers;
    }
}
