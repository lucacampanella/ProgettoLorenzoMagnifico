package it.polimi.ingsw.model.excommunicationTiles;

import it.polimi.ingsw.model.board.CardColorEnum;

import java.util.ArrayList;

/**
 * If you have this excommunication effect, whenever you want to take a card with a dice value, that dice
 * has malusDice less value
 */
public class MalusDiceOnTowerColorEffect extends AbstractExcommunicationTileEffect{
    private int malusDice;
    //It's one color. We assume excommunication tiles aren't settable from file.
    private CardColorEnum towerColorsMalus;
}
