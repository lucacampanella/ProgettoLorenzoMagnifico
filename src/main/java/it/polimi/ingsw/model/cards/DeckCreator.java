package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.client.CliPrinter;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.effects.immediateEffects.*;
import it.polimi.ingsw.model.effects.permanentEffects.*;
import it.polimi.ingsw.model.resource.Resource;
import it.polimi.ingsw.model.resource.ResourceType;
import it.polimi.ingsw.testingGSON.boardLoader.BoardCreator;
import it.polimi.ingsw.testingGSON.boardLoader.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.utils.Debug;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by higla on 24/05/2017.
 */
public class DeckCreator{
    public static void main(String[] args) throws Exception {
        Debug.instance(Debug.LEVEL_VERBOSE);
        GsonBuilder gsonBuilder = new GsonBuilder();
        /*gsonBuilder.registerTypeAdapter(TowerFloorAS.class, new TowerFloorDeserializer());
        gsonBuilder.registerTypeAdapter(MarketAS.class, new MarketASDeserializer());
        gsonBuilder.registerTypeAdapter(CouncilAS.class, new CouncilDeserializer());
        */
        //Gson gson = gsonBuilder.create();

        RuntimeTypeAdapterFactory<ImmediateEffectInterface> immediateEffectAdapter = RuntimeTypeAdapterFactory.of(ImmediateEffectInterface.class, "immediateEffect");
        immediateEffectAdapter.registerSubtype(NoEffect.class, "NoEffect");
        immediateEffectAdapter.registerSubtype(TakeOrPaySomethingEffect.class, "TakeOrPaySomethingEffect");
        immediateEffectAdapter.registerSubtype(GiveCouncilGiftEffect.class, "GiveCouncilGiftEffect");
        immediateEffectAdapter.registerSubtype(TakeCardNoFamilyMemberEffect.class, "TakeCardNoFamilyMemberEffect");
        immediateEffectAdapter.registerSubtype(DiscountEffect.class, "DiscountEffect");
        immediateEffectAdapter.registerSubtype(TakeOrPaySomethingConditionedOnCardEffect.class, "TakeOrPaySomethingConditionedOnCardEffect");
        immediateEffectAdapter.registerSubtype(HarvestNoFamilyMembersEffect.class, "HarvestNoFamilyMembersEffect");
        immediateEffectAdapter.registerSubtype(BuildNoFamilyMembersEffect.class, "BuildNoFamilyMembersEffect");
        immediateEffectAdapter.registerSubtype(TakeOrPaySomethingConditionedEffect.class, "TakeOrPaySomethingConditionedEffect");

        RuntimeTypeAdapterFactory<AbstractPermanentEffect> permanentEffectAdapter = RuntimeTypeAdapterFactory.of(AbstractPermanentEffect.class, "permanentEffect");
        permanentEffectAdapter.registerSubtype(BonusOnHarvestEffect.class, "BonusOnHarvestEffect");
        permanentEffectAdapter.registerSubtype(BonusOnBuildEffect.class, "BonusOnBuildEffect");
        permanentEffectAdapter.registerSubtype(BonusOnTowerEffect.class, "BonusOnTowerEffect");
        permanentEffectAdapter.registerSubtype(MalusDisabledImmediateEffectsEffect.class, "MalusDisabledImmediateEffectsEffect");
        permanentEffectAdapter.registerSubtype(NoPermanentEffect.class, "NoPermanentEffect");
        permanentEffectAdapter.registerSubtype(BonusOnTowerEffectChoice.class,"BonusOnTowerEffectChoice");

        Gson gson = gsonBuilder.setPrettyPrinting().registerTypeAdapterFactory(immediateEffectAdapter).registerTypeAdapterFactory(permanentEffectAdapter).create();
        /*
        Deck deckTest = getDeckForTest();

        String deckInJson = gson.toJson(deckTest);
        System.out.println(deckInJson);

        Deck deckFromJson = gson.fromJson(deckInJson, Deck.class);

        System.out.println(deckTest.toString());

        */
        ///*
        CliPrinter printer = new CliPrinter();

        // The JSON data
        try (Reader reader = new InputStreamReader(BoardCreator.class.getResourceAsStream("/DeckCFG.json"), "UTF-8")) {
            Deck deck = gson.fromJson(reader, Deck.class);
            //CliPrinter printer = new CliPrinter();
            printer.printDeck(deck);

        }
        //*/
    }

    private static Deck getDeckForTest(){
        Deck deck = new Deck();
        /*ArrayList<TerritoryCard> territoryCards = new ArrayList<TerritoryCard>();
        territoryCards.add(getTerritoryCard());
        territoryCards.add(getTerritoryCard());
        deck.setTerritoryCards(territoryCards);
        */
        ArrayList<CharacterCard> characterCards = new ArrayList<CharacterCard>();
        characterCards.add(getCharacterCard());
        characterCards.add(getCharacterCard());
        deck.setCharacterCards(characterCards);
        /*
        ArrayList<BuildingCard> buildingCards = new ArrayList<BuildingCard>();
        buildingCards.add(getBuildingCard());
        buildingCards.add(getBuildingCard());
        deck.setBuildingCards(buildingCards);

        ArrayList<VentureCard> ventureCards = new ArrayList<VentureCard>();
        ventureCards.add(getVentureCard());
        ventureCards.add(getVentureCard());
        deck.setVentureCards(ventureCards);
        */
        return deck;
    }
    public static VentureCard getVentureCard(){
        VentureCard ventureCard = new VentureCard();
        ArrayList<TakeOrPaySomethingEffect> cost = new ArrayList<TakeOrPaySomethingEffect>();
        cost.add(getTakeOrPaySomethingEffect(5));
        ventureCard.setCostChoiceMilitary(cost);
        ventureCard.setCostChoiceResource(cost);
        ventureCard.setName("Viola");
        ventureCard.setImmediateEffect(getImmediateEffect());
        ventureCard.setVictoryEndPoints(5);
        return ventureCard;
    }
    public static CharacterCard getCharacterCard(){
        CharacterCard characterCard = new CharacterCard();
        ArrayList<TakeOrPaySomethingEffect> cost = new ArrayList<TakeOrPaySomethingEffect>();
        cost.add(getTakeOrPaySomethingEffect(6));
        characterCard.setCost(cost);
        characterCard.setName("Noble");
        characterCard.setImmediateEffect(getImmediateEffect());
        characterCard.setPermanentEffect(getPermanentEffect());
        return characterCard;
    }
    public static TerritoryCard getTerritoryCard()
    {
        TerritoryCard territoryCard = new TerritoryCard();
        territoryCard.setHarvestEffectValue(2);
        territoryCard.setName("Bosco");
        territoryCard.setImmediateEffect(getImmediateEffect());
        territoryCard.setEffectsOnHarvest(getImmediateEffect());
        return territoryCard;
    }

    public static BuildingCard getBuildingCard()
    {
        BuildingCard buildingCard = new BuildingCard();
        buildingCard.setBuildEffectValue(2);
        buildingCard.setName("Palazzo");
        buildingCard.setImmediateEffect(getImmediateEffect());
        buildingCard.setEffectsOnBuilding(getImmediateEffect());

        ArrayList<TakeOrPaySomethingEffect> cost = new ArrayList<TakeOrPaySomethingEffect>();
        cost.add(getTakeOrPaySomethingEffect(6));
        buildingCard.setCost(cost);
        return buildingCard;
    }
    private static TakeOrPaySomethingEffect getTakeOrPaySomethingEffect(int value){
        Resource resource = new Resource(ResourceType.COIN, value);
        TakeOrPaySomethingEffect effect = new TakeOrPaySomethingEffect(resource);
        return effect;
    }
    private static ArrayList<Resource> getListOfResources()
    {
        Resource resource = new Resource(ResourceType.STONE, 2);
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(resource);
        resources.add(resource);
        return resources;
    }
    private static Resource getResource()
    {
        Resource resource = new Resource(ResourceType.SERVANT, 2);
        return resource;
    }
    private static ArrayList<ImmediateEffectInterface> getImmediateEffect(){
        ImmediateEffectInterface effect = getTakeOrPaySomethingEffect(2);
        ImmediateEffectInterface effect2 = getTakeOrPaySomethingEffect(3);
        ImmediateEffectInterface effect3 = new DiscountEffect(getListOfResources());
        ImmediateEffectInterface effect4 = new TakeOrPaySomethingConditionedOnCardEffect(getResource(), CardColorEnum.BLUE, 1);
        ImmediateEffectInterface effect5 = new BuildNoFamilyMembersEffect(2);
        ImmediateEffectInterface effect6 = new HarvestNoFamilyMembersEffect(3);
        ImmediateEffectInterface effect7 = new TakeOrPaySomethingConditionedEffect(getResource(), getResource());
        ArrayList<ImmediateEffectInterface> temp = new ArrayList<ImmediateEffectInterface>();
        /*temp.add(effect);
        temp.add(effect2);
        temp.add(effect3);
        temp.add(effect4);*/
        temp.add(effect5);
        temp.add(effect6);
        temp.add(effect7);
        return temp;
    }
    private static ArrayList<AbstractPermanentEffect> getPermanentEffect(){
        Resource resource = new Resource(ResourceType.COIN, 1);
        CardColorEnum colorEnum = CardColorEnum.GREEN;
        AbstractPermanentEffect effect = new BonusOnBuildEffect(2);
        AbstractPermanentEffect effect2 = new BonusOnHarvestEffect(2);
        AbstractPermanentEffect effect3 = new MalusDisabledImmediateEffectsEffect();
        AbstractPermanentEffect effect4 = new BonusOnTowerEffect(colorEnum , resource, 2);
        AbstractPermanentEffect effect5 = new BonusOnTowerEffectChoice(colorEnum , resource,resource, 2);

        ArrayList<AbstractPermanentEffect> temp = new ArrayList<AbstractPermanentEffect>();
        temp.add(effect);
        temp.add(effect2);
        temp.add(effect3);
        temp.add(effect4);
        temp.add(effect5);
        return temp;
    }
}