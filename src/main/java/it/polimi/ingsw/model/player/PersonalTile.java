package it.polimi.ingsw.model.player;

import it.polimi.ingsw.choices.ChoicesHandlerInterface;
import it.polimi.ingsw.model.effects.immediateEffects.GainResourceEffect;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is the personal tile a player can choose at the start of the game
 */
public class PersonalTile implements Serializable{
    //if it is the default tile this var is true
    private boolean defaultTile;
    //for game balancing, we don't allow players to customize their own dice tile
    private static final int DICEONHARVEST = 1;
    private static final int DICEONBUILD = 1;

    private String imgName;

    /**
     * this arrayList is called every time a user builds with a diceValue of 1
     */
    private ArrayList<GainResourceEffect> effectOnBuild;
    /**
     * this arrayList is called every time a user harvests with a diceValue of 1
     */
    private ArrayList<GainResourceEffect> effectOnHarvest;

    public ArrayList<GainResourceEffect> getEffectOnBuild() {
        return effectOnBuild;
    }

    public void setEffectOnBuild(ArrayList<GainResourceEffect> effectOnBuild) {
        this.effectOnBuild = effectOnBuild;
    }

    public ArrayList<GainResourceEffect> getEffectOnHarvest() {
        return effectOnHarvest;
    }

    public void activateEffectsOnBuild(Player player, ChoicesHandlerInterface choicesController) {
        effectOnBuild.forEach(effect -> effect.applyToPlayer(player, choicesController, "personalTile"));
    }

    public void activateEffectsOnHarvest(Player player, ChoicesHandlerInterface choicesController) {
        effectOnHarvest.forEach(effect -> effect.applyToPlayer(player, choicesController, "personalTile"));
    }

    public void setEffectOnHarvest(ArrayList<GainResourceEffect> effectOnHarvest) {
        this.effectOnHarvest = effectOnHarvest;
    }

    public PersonalTileEnum getPersonalTileEnum(){
        if(defaultTile)
            return PersonalTileEnum.STANDARD;
        return PersonalTileEnum.SPECIAL;
    }

    public String getDescription(){
        String description;
        description = "\n\tBonuses on harvest :\n\tDice : +" + DICEONHARVEST + "\n\t" ;
        for(GainResourceEffect effect : effectOnHarvest){
            description = description.concat(effect.getResource().getType().getFullName() + " : +" + effect.getResource().getValue() + "\n\t");
        }
        description = description.concat("\n\tBonuses on build :\n\tDice :" + DICEONBUILD + "\n\t");
        for(GainResourceEffect effect : effectOnBuild){
            description = description.concat(effect.getResource().getType().getFullName() + " : +" + effect.getResource().getValue() + "\n\t");
        }
        return  description;
    }

    public String getImgName() {
        return imgName;
    }
}
