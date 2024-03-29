package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.effects.permanentEffects.AbstractPermanentEffect;
import it.polimi.ingsw.model.resource.Resource;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * This object is a collector for the Character cards, it contains the cards and offers methods to
 * perform queries on them and obtain description of effects
 */
public class CharacterCardCollector implements Serializable{

    private LinkedList<CharacterCard> characterCards;


    public CharacterCardCollector() {
        characterCards = new LinkedList<CharacterCard>();
    }

    public CharacterCardCollector(LinkedList<CharacterCard> characterCards) {
        this.characterCards = characterCards;
    }

    public void setCharacterCards(LinkedList<CharacterCard> characterCards) {
        this.characterCards = characterCards;
    }

    public LinkedList<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    public void add(CharacterCard card) {
        characterCards.add(card);
    }
    /**
     * This method is used to get the discount when placing a family member on a certain tower
     * It passes all the cards and returns the corresponding LinkedList of resources
     * @param color the color of the tower / card
     * @return the LinkedList of resources discounted, empty if there's no discount
     */
    public LinkedList<Resource> getDiscountOnTower(CardColorEnum color)
    {
        LinkedList<Resource> discount = new LinkedList<>();
        Resource res;
        for(CharacterCard cardIter : characterCards) {
            for(AbstractPermanentEffect effectIter : cardIter.getPermanentEffects()) {
                res = effectIter.getDiscountOnTower(color);
                if(res != null)
                    discount.add(res);
            }
        }
        return discount;
    }

    /**
     * This method is used to get the discount when placing a family member on a certain tower
     * @param color the color of the tower / card
     * @return the bonus on the dice of the family member placed on that tower
     */
    public int getBonusOnDice(CardColorEnum color) {
        int diceValue = 0;
        for(CharacterCard cardIter : characterCards) {
            for(AbstractPermanentEffect effectIter : cardIter.getPermanentEffects()) {
                diceValue += effectIter.getBonusOnDice(color);
            }
        }

        return diceValue;
    }

    /**
     * This method returns the bonus on the dice when the player performs a harvest action
     *
     * @return the bonus on the dice
     */
    public int getBonusOnHarvest()
    {

        int diceValue = 0;
        for(CharacterCard cardIter : characterCards) {
            for(AbstractPermanentEffect effectIter : cardIter.getPermanentEffects()) {
                diceValue += effectIter.getBonusOnHarvest();
            }
        }

        return diceValue;
    }

    /**
     * This method returns the bonus on the dice when the player performs a build action
     * This method is overriden by {@link it.polimi.ingsw.model.effects.permanentEffects.BonusOnBuildEffect} to return the right value
     * @return the bonus on the dice
     */
    public int getBonusOnBuild()
    {
        int diceValue = 0;
        for(CharacterCard cardIter : characterCards) {
            for(AbstractPermanentEffect effectIter : cardIter.getPermanentEffects()) {
                diceValue += effectIter.getBonusOnBuild();
            }
        }

        return diceValue;
    }

    /**
     * This method returns if the player has immediate effects disabled on a certain level of towers (over a certain dice value required)
     * @param requiredDiceValue the dicerequired dice value of the action space of the tower floor to check if at that level the effects are disabled
     * @return true if the effects are disabled over that tower level
     */
    public boolean isImmediateEffectDisabled(int requiredDiceValue)
    {
        for(CharacterCard cardIter : characterCards) {
            for(AbstractPermanentEffect effectIter : cardIter.getPermanentEffects()) {
                if(effectIter.isImmediateEffectDisabled(requiredDiceValue))
                    return true;
            }
        }
        return false;
    }

}
