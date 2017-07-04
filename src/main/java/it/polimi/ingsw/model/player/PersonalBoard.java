package it.polimi.ingsw.model.player;

import it.polimi.ingsw.choices.ChoicesHandlerInterface;
import it.polimi.ingsw.model.board.AbstractActionSpace;
import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.utils.Debug;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * the personal board of a single player
 */
public class PersonalBoard implements Serializable{

    private LinkedList<TerritoryCard> territoryCards;
    private LinkedList<BuildingCard> buildingCards;
    private CharacterCardCollector characterCardsCollector;
    private LinkedList<VentureCard> ventureCards;

    /**
     * This array is used to check if the player has enough military points
     * to place the territory card on his personal board
     */
    private int militaryPointsTerritoryRequirements[] = {0, 0, 3, 7, 12, 18};

    /**
     * This array is used to calculate the corresponding vicotry points on territory cards at the end of the game
     */
    private int victoryPointsTerritory[] = {0,0,1,4,10,20};

    /**
     * It represent the bonuses of the tiles near the personal board
     */
    private PersonalTile personalTile;


    public PersonalBoard() {
        territoryCards = new LinkedList<TerritoryCard>();
        buildingCards = new LinkedList<BuildingCard>();
        characterCardsCollector = new CharacterCardCollector();
        ventureCards = new LinkedList<VentureCard>();
    }

    public void addCard(AbstractCard card)
    {
        switch(card.getColor()) {
            case GREEN: addGreenTerritoryCard((TerritoryCard)card);
            break;
            case BLUE: addBlueCharacterCard((CharacterCard) card);
            break;
            case YELLOW: addYellowBuildingCard((BuildingCard)card);
            break;
            case PURPLE: addPurpleVentureCard((VentureCard)card);
            break;
        }
    }
    public void addGreenTerritoryCard(TerritoryCard card) {
        territoryCards.add(card);
    }

    public void addYellowBuildingCard(BuildingCard card) {
        buildingCards.add(card);
    }

    public void addBlueCharacterCard(CharacterCard card) {
        characterCardsCollector.add(card);
    }

    public void addPurpleVentureCard(VentureCard card) {
        ventureCards.add(card);
    }

    public LinkedList<BuildingCard> getYellowBuildingCards() {
        return buildingCards;
    }

    public void harvest(int realDiceValueNoBlueBonus, Player player, ChoicesHandlerInterface choicesController) {

        //we check if there is some blue card that has a bonus on harvest, in this case we should modify the value of the dice
        final int realDiceValueBlue = realDiceValueNoBlueBonus + characterCardsCollector.getBonusOnHarvest();

        territoryCards.forEach(card -> card.applyEffectsFromHarvestToPlayer(player, realDiceValueBlue, choicesController));

        personalTile.activateEffectsOnHarvest(player, choicesController);
    }

    public void build(int realDiceValueNoBlueBonus, Player player, ChoicesHandlerInterface choicesController) {

        //we check if there is some blue card that has a bonus on build, in this case we should modify the value of the dice
        final int realDiceValueBlue = realDiceValueNoBlueBonus + characterCardsCollector.getBonusOnBuild();
        buildingCards.forEach(card -> card.applyEffectsFromBuildToPlayer(player, realDiceValueBlue, choicesController));
        Debug.printVerbose("after foreach on build");

        //We add bonus tiles afterwards because the resources got from the bonus tiles should not count on the checks for the yellow cards
        personalTile.activateEffectsOnBuild(player, choicesController);
    }

    public void blueBonus(AbstractActionSpace space) {

        //TODO bonus
    }

    public void purplePoints(Player player) {

        /*LinkedList<AbstractCard> purpleCard = ownedCards.get(CardColorEnum.PURPLE);
        for (AbstractCard i : purpleCard) {
            //i.purplePoints(player);
        }*/
    }

    public int getNumberOfColoredCard(CardColorEnum color) {

        return getCardListByColor(color).size();
    }

    public LinkedList<? extends AbstractCard> getCardListByColor(CardColorEnum color) {
        switch(color) {
            case GREEN:
                return territoryCards;
            case YELLOW:
                return buildingCards;
            case BLUE:
                return characterCardsCollector.getCharacterCards();
            case PURPLE:
                return ventureCards;
        }

        return null;
    }

    public void setPersonalTile(PersonalTile personalTile){
        this.personalTile = personalTile;
        Debug.printVerbose("setted personal tile");
    }

    public PersonalTile getPersonalTile() {
        return personalTile;
    }


    public CharacterCardCollector getCharacterCardsCollector() {
        return characterCardsCollector;
    }

    @Deprecated
    public LinkedList<CharacterCard> getCharacterCards() {
        return characterCardsCollector.getCharacterCards();
    }

    /**
     * This method checks that there is enough space on the personal board to take a territory card and plus
     * checks if the requirement on military points is met
     * @param currentMilitaryPoints the number of actual military points the player has right now
     * @param noMilitaryPointsNeededForTerritoryCardsLeaderAbility should be true if the player has a leader with this ability
     * @return true if he can take the card
     */
    public boolean canAddTerritoryCard(int currentMilitaryPoints, boolean noMilitaryPointsNeededForTerritoryCardsLeaderAbility) {
        if(territoryCards.size() < 6 &&
                ((militaryPointsTerritoryRequirements[territoryCards.size()] <= currentMilitaryPoints) ||
                        (noMilitaryPointsNeededForTerritoryCardsLeaderAbility)))
            return true;
        return false;
    }

    public LinkedList<TerritoryCard> getTerritoryCards() {
        return territoryCards;
    }
}
