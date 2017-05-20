package it.polimi.ingsw.client;

import it.polimi.ingsw.gamelogic.Board.Board;
import it.polimi.ingsw.gamelogic.Board.MarketAS;
import it.polimi.ingsw.gamelogic.Board.Tower;
import it.polimi.ingsw.gamelogic.Board.TowerFloorAS;

/**
 * Created by higla on 20/05/2017.
 */
public class CliPrinter {

    public void printTowersVerbose(Board board){
    int i;
    int k;
        for(i=0; i< board.getTowers().length; i++)
        {
            Tower tower = board.getTower(i);
            System.out.println("This is the " + board.getTowerColor(tower) + " tower:");
            for(k=0; k < tower.getFloors().length; k++)
            {
                TowerFloorAS floor = tower.getFloorByIndex(k);
                System.out.println("Floor " + k + " has this dice value " + floor.getDiceValue() + " and " + floor.getEffectDescription());
            }
        }

    }
    public void printTowers(Board board){
        int i;
        int k;
        TowerFloorAS[] temp = new TowerFloorAS[board.getNUMBER_OF_TOWERS()];
        for(k=0; k<board.getNUMBER_OF_TOWERS(); k++)
            System.out.print("      " + board.getTowerColor(board.getTower(k)) + "            ");
        System.out.println();
        for(i=0; i< board.getTowers().length; i++)
        {
            temp = board.getFloorLevel(i);
           /* for(k=0; k<board.getNUMBER_OF_TOWERS(); k++)
                System.out.print(" _____ ");*/

            for(k=0; k<board.getNUMBER_OF_TOWERS(); k++)
            System.out.print("||   " + "cardName" + "  *" + temp[k].getDiceValue() + "* " + temp[k].getEffectShortDescription());
            System.out.println("||");

        }

    }
    public void printMarket(Board board)
    {
        {
            int i;
            System.out.println("This is the market: ");
            for(i=0; i< board.getMarket().length; i++)
            {
                MarketAS market = board.getMarketSpaceByIndex(i);
                System.out.print("|" + i + "| *" + market.getDiceValue() + "* " + market.getEffectShortDescription() + " ");
            }
            System.out.println(" ");
        }
    }
    public void printMarketVerbose(Board board)
    {
        int i;
        System.out.println("This is the market: ");
        for(i=0; i< board.getMarket().length; i++)
        {
            MarketAS market = board.getMarketSpaceByIndex(i);
            System.out.println("Space " + i + " Dice " + market.getDiceValue() + " " + market.getEffectDescription());
        }
    }
    public void printVaticanReport(Board board)
    {
        int i;
        // System.out.println("Those are the Faith points you need to have to avoid scomunica ");
        System.out.print(" | ");
        for(i = 0; i< board.getVaticanFaithAge().length; i++) {
            System.out.print(board.getVaticanFaithAgeIndex(i));
            System.out.print(" | ");
        }
        System.out.println();
        //System.out.println("Those are the Victory points you get from vaticanReport ");
        printLine(199);
        System.out.print(" | ");
        for(i = 0; i< board.getVaticanVictoryPoints().length; i++) {
            System.out.print(board.getVictoryPointsByIndex(i));
            System.out.print(" | ");
        }
    }
    public void printBoard(Board board){
        //printing the towers
        printTowers(board);
        //printTowersVerbose(board);
        //printing the market
        printLine(199);
        printMarket(board);
        //printMarketVerbose(board)
        printLine(199);
        //printing build
        System.out.println("This is buildActionSpace " + board.getBuild().toString() + ". No effect yet available ");
        printLine(199);
        //printing harvest
        System.out.println("This is harvestActionSpace " + board.getHarvest().toString() + ". No effect yet available ");
        printLine(199);
        //printing council
        System.out.println("This is councilActionSpace " + board.getCouncil().toString() + ". Nothing to show yet ");
        //priting vaticanReport
        printLine(199);
        printVaticanReport(board);
    }
    public void printLine(int lineLenght){
        int i;
        for(i = 0; i < lineLenght; i++)
        System.out.print("-");
        System.out.println("");
    }
}