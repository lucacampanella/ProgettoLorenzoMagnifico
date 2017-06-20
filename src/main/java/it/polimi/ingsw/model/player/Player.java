package it.polimi.ingsw.model.player;

import it.polimi.ingsw.choices.ChoicesHandlerInterface;
import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.effects.immediateEffects.GainResourceEffect;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.leaders.PermanentLeaderCardCollector;
import it.polimi.ingsw.model.leaders.leadersabilities.AbstractLeaderAbility;
import it.polimi.ingsw.model.leaders.leadersabilities.ImmediateLeaderAbility.AbstractImmediateLeaderAbility;
import it.polimi.ingsw.model.leaders.leadersabilities.LeaderAbilityTypeEnum;
import it.polimi.ingsw.model.resource.Resource;
import it.polimi.ingsw.model.resource.ResourceCollector;
import it.polimi.ingsw.model.resource.ResourceTypeEnum;
import it.polimi.ingsw.utils.Debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The main player class, no network
 */
public class Player implements Serializable{

    private String nickname;

    private PersonalBoard personalBoard;

    //private HashMap<ResourceTypeEnum, Integer> resourcesMap;

    private ResourceCollector resourcesMap;

    private ArrayList<FamilyMember> notUsedFamilyMembers;

    private ArrayList<FamilyMember> usedFamilyMembers;

    //private PersonalTile personalTile = null; MOVED TO PERSONALBOARD

    /**
     * These are the leader cards the user chose at the beginning of the game and hasn't played yet
     * Players should be moved out of this list once played
     */
    private ArrayList<LeaderCard> leaderCards;

    /**
     * These are the leader cards the user decided to play,
     * but of which he still hasn't activated the once per round ability
     * only once per round leaders should fit in this list
     */
    private LinkedList<LeaderCard> playedOncePerRoundLeaderCards;

    /**
     * There are the leader cards the user has played and activated this round,
     * only once per round leaders should fit in this list
     */
    private LinkedList<LeaderCard> playedAndActivatedOncePerRoundLeaderCards;

    /**
     * This is the collector of leader cards who have a permanent ability and are played by the user
     * This collector is used during checks inside the model for bonuses and discounts
     */
    private PermanentLeaderCardCollector permanentLeaderCardCollector;

    //private ArrayList<ExcommuncationCard> excommuncationCard;

    public Player()
    {
        super();
        loadPlayer();
        leaderCards = new ArrayList<LeaderCard>(4);
        playedOncePerRoundLeaderCards = new LinkedList<LeaderCard>();
        playedAndActivatedOncePerRoundLeaderCards = new LinkedList<LeaderCard>();
    }

    public Player(String nickname)
    {
        loadPlayer();
        setNickname(nickname);
    }

    private void loadPlayer(){

        personalBoard = new PersonalBoard();
        //TODO CHOOSE TILES OF PERSONAL BOARD
        resourcesMap = new ResourceCollector();
        loadInitialResources();
        notUsedFamilyMembers = new ArrayList<>(4);
        usedFamilyMembers = new ArrayList<>(4);
        //excommunicanionCard = new ArrayList<>(3);
        //leaderCards = new ArrayList<>(3);

    }

    /**
     * you load all the resources needed by the player
     */
    private void loadInitialResources(){
        resourcesMap.addResource(new Resource(ResourceTypeEnum.WOOD, 2));
        resourcesMap.addResource(new Resource(ResourceTypeEnum.STONE, 2));
        resourcesMap.addResource(new Resource(ResourceTypeEnum.SERVANT, 3));

    }


    /**
     * this method is used to add a resource on the player
     * if {@link Resource#getValue()} < 0 subtracts the resource instead
     * @param resource the object of the resource, it contains the value and the type
     */
    public void addResource(Resource resource){

        resourcesMap.addResource(resource);

    }

    /**
     * this method is used to subtract a single resource,
     * {@link Resource#getValue()} should be positive to work as a subtractor
     * @param resource the object of the resource, it contains the value and the type
     */
    public void subResource(Resource resource) {
        resourcesMap.subResource(resource);
    }

    /**
     * this method is used to subtract resources,
     * {@link Resource#getValue()} should be positive to work as a subtractor
     * @param resources the List of the resource, it contains the value and the type
     */
    public void subResources(ResourceCollector resources) {
        resourcesMap.subResource(resources);
    }

    /**
     * this method is used to subtract resources
     * @param resources the resources the player has to pay
     */
    public void subResources(List<Resource> resources) {
        resourcesMap.subResource(resources);
    }

    /**
     * this method is used to add an array of resources on the player
     * @param resources the object of the resource, it contains the value and the type
     */
    public void addResources(List<Resource> resources) {

        resourcesMap.addResource(resources);
    }

    public int getResource(ResourceTypeEnum type){

        return resourcesMap.getResource(type);

    }

    //TODO: we need to put Cards Containers in Player and then implement this method.
    public int getNumberOfColoredCard(CardColorEnum color)
    {
        return personalBoard.getNumberOfColoredCard(color);
    }

    /*public void excommunication(ExcommunicationCard card){

        excommunicationCard.add(card);
    }*/


    public String getNickname()
    {
        return nickname;
    }

    protected void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFamilyMembers(ArrayList<Dice> dices){

        for(Dice i : dices) {
            Debug.printVerbose("Adding fm with value" + i.getValue());
            this.notUsedFamilyMembers.add(new FamilyMember(i, this));
        }

    }

    public void playFamilyMember(FamilyMember familyMember){

        this.notUsedFamilyMembers.remove(familyMember);
        this.usedFamilyMembers.add(familyMember);

    }

    /**
     * this method is used to return available the family member used on the previous turn
     */
    public void reloadFamilyMember(){

        notUsedFamilyMembers.addAll(usedFamilyMembers);
        usedFamilyMembers.clear();

        //align th family member with the value of the linked dice, to delete any changes of the family members' values
        notUsedFamilyMembers.forEach(FamilyMember::alignValue);

    }

    public void addCard(AbstractCard card){
        personalBoard.addCard(card);
    }

    public void addLeaderCard(LeaderCard leaderCard){

        this.leaderCards.add(leaderCard);
    }

    public ArrayList<LeaderCard> getLeaderCards(){

        return leaderCards;
    }
/*
    public void activateLeaderCard(LeaderCard leaderCards){

        this.leaderCards.remove(leaderCards);
        playedLeaderCard.add(leaderCards);

    }

    public void discardLeaderCard(LeaderCard leaderCards){

        this.leaderCards.remove(leaderCards);
        //TODO get bonus
    }*/

    /**
     * this method is called when a player harvests. It increments player's resources
     * @param realDiceValueNoBlueBonus the real value (not considered the bonus from blue cards) to perform the action with
     * @param choicesController the controller to make callback on choices
     */
    public void harvest(int realDiceValueNoBlueBonus, ChoicesHandlerInterface choicesController){
        personalBoard.harvest(realDiceValueNoBlueBonus, this, choicesController);

    }

    /**
     * this method is called when a player builds. It increments player's resources
     * @param realDiceValueNoBlueBonus the real value (not considered the bonus from blue cards) to perform the action with
     * @param choicesController the controller to make callback on choices
     */
    public void build(int realDiceValueNoBlueBonus, ChoicesHandlerInterface choicesController){
        personalBoard.build(realDiceValueNoBlueBonus, this, choicesController);
    }

    public void purplePoints(){

        personalBoard.purplePoints(this);
    }

    public ArrayList<FamilyMember> getNotUsedFamilyMembers(){
        return notUsedFamilyMembers;
    }


    /**
     * this method returns all the resources the user has.
     * @return the {@link ResourceCollector} of the resources the user has
     */
    public ResourceCollector getResourcesCollector() {
        return resourcesMap;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public FamilyMember getFamilyMemberByColor(DiceAndFamilyMemberColorEnum familyMemberColor){

        for(FamilyMember familyMember : notUsedFamilyMembers){

            if(familyMember.getColor() == familyMemberColor)
                return familyMember;

        }
        return null;
    }

    /**
     * This method performs all the actions needed to prepare the player class for a new round
     */
    public void prepareForNewRound() {
        reloadFamilyMember();

        //make once per round leaders able to be activated again
        playedOncePerRoundLeaderCards.addAll(playedAndActivatedOncePerRoundLeaderCards);
        playedOncePerRoundLeaderCards.clear();
    }

    /**
     * This method returns a list of LeaderCards not yet played, but playable,
     * i.e. they player meets their the requirements to be played
     * @return a list of playable LeaderCards, epty if none is playable (yet)
     */
    public List<LeaderCard> getPlayableLeaders() {
        ArrayList<LeaderCard> playableLeaders = new ArrayList<LeaderCard>(1);

        for(LeaderCard leaderIter : leaderCards) {
            if(leaderIter.isPlayable(this))
                playableLeaders.add(leaderIter);
        }

        return playableLeaders;
    }

    /**
     * getter for a list of all played leader, regardless of their ability type
     * @return a list of all played leader, regardless of their ability type
     */
    public List<LeaderCard> getPlayedLeaders(){
        ArrayList<LeaderCard> immediateAndPermanentPlayedLeaders = new ArrayList<>(
                playedAndActivatedOncePerRoundLeaderCards.size() +
                            playedOncePerRoundLeaderCards.size() +
                            permanentLeaderCardCollector.getPermanentLeaders().size());

        immediateAndPermanentPlayedLeaders.addAll(playedAndActivatedOncePerRoundLeaderCards);
        immediateAndPermanentPlayedLeaders.addAll(playedOncePerRoundLeaderCards);
        immediateAndPermanentPlayedLeaders.addAll(permanentLeaderCardCollector.getPermanentLeaders());

        return immediateAndPermanentPlayedLeaders;
    }

    /**
     * This method is called when the user wants to play a leader card,
     * if the card has a permanent ability it is automatically activated
     * if the card has a once per round ability the user is asked if he wants to activate it now or leave it for later
     * @param leaderToBePlayed the leader card to be played
     * @param choicesHandler for callbacks on activation
     */
    public void playLeader(LeaderCard leaderToBePlayed, ChoicesHandlerInterface choicesHandler) {

        //We assume that as long as the player decided to play this leader
        // he also wants to activate his permanent ability
        if(leaderToBePlayed.getAbility().getAbilityType() == LeaderAbilityTypeEnum.PERMANENT) {
            permanentLeaderCardCollector.addLeaderCard(leaderToBePlayed);
        }
        else { //we should ask the user if he also wants to activate the leader ability
            if(choicesHandler.callbackOnAlsoActivateLeaderCard()) { //we should activate the leader ability
                ((AbstractImmediateLeaderAbility) (leaderToBePlayed.getAbility())).applyToPlayer(this, choicesHandler, leaderToBePlayed.getName());
                playedAndActivatedOncePerRoundLeaderCards.add(leaderToBePlayed);
            } else {
                playedOncePerRoundLeaderCards.add(leaderToBePlayed);
            }
        }
    }

    /**
     * This method is called when the user wants to activate the ability of a leader card he's already played,
     * @param leaderCardToBeActivated the leader card to be activated
     * @param choicesHandler for callbacks on activation
     */
    public void activateLeaderCardAbility(LeaderCard leaderCardToBeActivated, ChoicesHandlerInterface choicesHandler) {
        playedOncePerRoundLeaderCards.remove(leaderCardToBeActivated);
        playedAndActivatedOncePerRoundLeaderCards.add(leaderCardToBeActivated);
        AbstractLeaderAbility leaderAbility = leaderCardToBeActivated.getAbility();
        if(leaderAbility.getAbilityType() == LeaderAbilityTypeEnum.ONCE_PER_ROUND) {
            ((AbstractImmediateLeaderAbility) (leaderAbility)).applyToPlayer(this, choicesHandler, leaderCardToBeActivated.getName());
        } else
            Debug.printError("activateLeaderCardAbility called with a PERMANENT ability (?)");
    }

    /**
     * This method allows to discard a leader card, the leader card passed as an argument should no be alredy played,
     * thet's against the rules
     * @param leaderCardToBeDiscarded the card to be discarded
     */
    public void discardLeader(LeaderCard leaderCardToBeDiscarded, ChoicesHandlerInterface choicesHandler) {
        leaderCards.remove(leaderCardToBeDiscarded);
        List<GainResourceEffect> resourceChoice = choicesHandler.callbackOnCouncilGift("discardLeader", 1);
        for(GainResourceEffect effectIter : resourceChoice)
            effectIter.applyToPlayer(this, choicesHandler, "discardLeaderInside");
    }

    public void setPersonalTile(PersonalTile personalTile){
        personalBoard.setPersonalTile(personalTile);
    }
}
