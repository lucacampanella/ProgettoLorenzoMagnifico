package it.polimi.ingsw.model.leaders.leadersabilities.PermanentLeaderAbility;

import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.leaders.leadersabilities.AbstractLeaderAbility;
import it.polimi.ingsw.model.leaders.leadersabilities.LeaderAbilityTypeEnum;
import it.polimi.ingsw.model.resource.Resource;

import java.util.Optional;

/**
 * This class describes how a leader ability should be formed.
 * Each single ability will override a particular method,
 * in order to return the correct value for that ability and not the value corresponding to not having that ability
 */
public abstract class AbstractPermanentLeaderAbility extends AbstractLeaderAbility{


    public AbstractPermanentLeaderAbility() {
        super();
    }

    @Override
    public LeaderAbilityTypeEnum getAbilityType() {
        return LeaderAbilityTypeEnum.PERMANENT;
    }

    /**
     * This method will usually be overridden by "Ludovico Ariosto" ability, in {@link CanPlaceFMInOccupiedASLeaderAbility}
     * @return false
     */
    public boolean canPlaceFamilyMemberInOccupiedActionSpace() {
        return false;
    }

    /**
     * This method will usually be overridden by "Filippo Bunelleschi" ability, in {@link NotToSpendForOccupiedTowerLeaderAbility}
     * @return false
     */
    public boolean hasNotToSpendForOccupiedTower() {
        return false;
    }

    /**
     * This method will usually be overridden by "Sigismondo Malatesta" ability, in {@link BonusNeutralFMLeaderAbility}
     * @return 0
     */
    public int getBonusNeutralFM(){
        return 0;
    }

    /**
     * This method will usually be overridden by "Ludovico il Moro" ability, in {@link FixedFamilyMembersValueLeaderAbility}
     * @return empty Optional
     */
    public Optional<Integer> getFixedFamilyMembersValue() {
        return Optional.empty();
    }

    /**
     * This method will usually be overridden by "Lucrezia Borgia" ability to return the right bonus value, in {@link BonusColoredFamilyMembersLeaderAbility}
     * @return 0
     */
    public int getBonusColoredFamilyMembers() {
        return 0;
    }

    /**
     * This method will usually be overridden by "Sisto IV" ability, in {@link GainFaithPointsSupportingChurchLeaderAbility}
     * @return 0
     */
    public int getAdditionalFaithPointsForSupportingChurch() {
        return 0;
    }

    /**
     * This method will usually be overridden by "Cesare Borgia" ability, in {@link NoMilitaryPointsNeededForTerritoryCardsLeaderAbility}
     * @return false
     */
    public boolean noMilitaryPointsNeededForTerritoryCards() {
        return false;
    }

    /**
     * This method will usually be overridden by "Santa Rita" ability, in {@link AddMoreTimesResourcesOnImmediateCardEffectAbility}
     * It tells how many times more we need to give resources to the player
     * @param cardColor
     */
    public int getMoreTimesResourcesOnImmediateEffects(CardColorEnum cardColor){return 0;}


    /**
     * This method will usually be overridden by "Pico della Mirandola" ability,
     * in {@link DiscountOnCardCostLeaderAbility}
     * It tells if the user has a discount on a certain cost of a certain card
     * @param cardColor the color of the card we should check if there's a discount on
     * @return null
     */
    public Resource getDiscountOnCardCost(CardColorEnum cardColor) {
        return null;
    }

}
