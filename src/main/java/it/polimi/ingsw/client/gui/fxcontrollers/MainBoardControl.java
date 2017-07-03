package it.polimi.ingsw.client.gui.fxcontrollers;

import it.polimi.ingsw.client.cli.CliPrinter;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.board.Dice;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.excommunicationTiles.ExcommunicationTile;
import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.model.player.DiceAndFamilyMemberColorEnum;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.resource.MarketWrapper;
import it.polimi.ingsw.model.resource.TowerWrapper;
import it.polimi.ingsw.utils.Debug;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by campus on 24/06/2017.
 */
public class MainBoardControl extends CustomFxControl {
    //todo: implement refresh of the leaders in LeaderOwnedControl
    private boolean[] isLeaderStageCreated = {false,false, false, false, false};

    private Stage secondStage = new Stage();
    private CustomFxControl currentFXControl;
    @FXML
    private AnchorPane towersCouncilFaith;

    @FXML
    private AnchorPane marketPane;

    @FXML
    private AnchorPane buildHarvestPane;

   /* @FXML
    private Button blueCardsButton;

    @FXML
    private Button purpleCardsButton;*/

    @FXML
    private HBox familyMembersPanel;

    @FXML
    private TextArea currentGameStateTextArea;

    @FXML
    private TabPane playersTabPersonalBoard;

    @FXML
    private PlayerTabSubControl thisPlayerTab;

    @FXML
    private PlayerTabSubControl player1Tab;

    @FXML
    private PlayerTabSubControl player2Tab;

    @FXML
    private PlayerTabSubControl player3Tab;

    /**
     * This hashmap is used to obtain the tab related to the player
     */
    HashMap<String, PlayerTabSubControl> playersTabMap;



    private Board board;

    private Player thisPlayer;

    private List<Player> otherPlayers;

    private List<Dice> dices;

    /**
     * Contructor, called when opened fxml
     */
    public MainBoardControl() {
        playersTabMap = new HashMap<String, PlayerTabSubControl>(3);
    }

    @FXML
    private ToggleGroup familyMembersToggleGroup = new ToggleGroup();

    public void displayCards() {
        Tower[] towers = board.getTowers();

        for(int col = 0; col < towers.length; col++) {
            for(int raw = 0; raw < 4; raw++) {
                ImageView imgView = ((ImageView) (towersCouncilFaith.lookup("#card"+col+raw)));
                Image cardImg  = new Image(getClass().getResourceAsStream("/imgs/Cards/" +
                        towers[col].getFloorByIndex(raw).getCard().getImgName()));
                imgView.setImage(cardImg);
                imgView.setPreserveRatio(true);
            }
        }
        //todo remove, this is just for debug
        CliPrinter.printBoard(board);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setThisPlayer(Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public void setOtherPlayers(List<Player> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    public void setDices(List<Dice> dices) {
        this.dices = dices;
    }

    public void displayDices() {
        for(Dice diceIter : dices) {
            if(diceIter.getColor() != DiceAndFamilyMemberColorEnum.NEUTRAL) {
                Text diceText = ((Text) (marketPane.lookup("#dice" + diceIter.getColor().getIntegerValue())));
                diceText.setText(String.valueOf(diceIter.getValue()));
            }
        }
        //todo remove, this is just for debug
        CliPrinter.printPersonalBoard(thisPlayer);
    }

    /**
     * This method is used ad startup when we have to setup all the personal boards of the players
     */
    public void setUpPlayersPersonalBoards() {
        ObservableList<Tab> tabs = playersTabPersonalBoard.getTabs();
        Tab currentTab = tabs.get(0); //this player tab
        currentTab.setText("You (" + thisPlayer.getPlayerColor().getStringValue() + ")");

        thisPlayerTab.setUpTab(getController(), thisPlayer, true);
        playersTabMap.put(thisPlayer.getNickname(), thisPlayerTab);

        for(int i = 1; i <= otherPlayers.size(); i++) {
            currentTab = tabs.get(i);
            currentTab.setText(otherPlayers.get(i-1).getNickname() + " (" + otherPlayers.get(i-1).getPlayerColor().getStringValue() + ")");
        }
        tabs.remove(otherPlayers.size()+1, tabs.size());

        if(otherPlayers.size() >= 1) {
            player1Tab.setUpTab(getController(), otherPlayers.get(0), false);
            playersTabMap.put(otherPlayers.get(0).getNickname(), player1Tab);
            if(otherPlayers.size() >= 2) {
                player2Tab.setUpTab(getController(), otherPlayers.get(1), false);
                playersTabMap.put(otherPlayers.get(1).getNickname(), player2Tab);
                if(otherPlayers.size() >= 3) {
                    player3Tab.setUpTab(getController(), otherPlayers.get(2), false);
                    playersTabMap.put(otherPlayers.get(2).getNickname(), player3Tab);
                }
            }
        }
    }

    public void displayFamilyMembers(/*List<FamilyMember> availableFMs*/) {
        for(Dice diceIter : dices) {
            ToggleButton fm = ((ToggleButton) (familyMembersPanel.lookup("#FM" + diceIter.getColor().getIntegerValue())));
                fm.setText(String.valueOf(diceIter.getValue()));
                fm.setStyle("-fx-border-color: " + thisPlayer.getPlayerColor().getStringValue() + ";");
                fm.setToggleGroup(familyMembersToggleGroup);
        }
    }

    public void displayExcommTiles() {
        List<ExcommunicationTile> tiles = board.getExcommunicationTiles();

        for(int i = 0; i < tiles.size(); i++) {
            ImageView imgView = ((ImageView) (towersCouncilFaith.lookup("#excomm" + i)));
            Image tileImg  = new Image(getClass().getResourceAsStream("/imgs/ExcommunicationTiles/" +
                    tiles.get(i).getImgName()));
            imgView.setImage(tileImg);
            imgView.setPreserveRatio(true);
        }
    }

    @FXML
    public void showPurpleCards() {
        showCards(thisPlayer.getPersonalBoard().getCardListByColor(CardColorEnum.PURPLE), "Purple cards");
    }

    @FXML
    public void showBlueCards() {
        showCards(thisPlayer.getPersonalBoard().getCardListByColor(CardColorEnum.BLUE), "Blue cards");
    }

    /**
     * owned cards leader
     */
    @FXML
    public void showLeaderCards() {
        if (!isLeaderStageCreated[0]) {

        Platform.runLater(() -> this.openNewWindow("LeaderOwnedScene.fxml", "Choose a leader", () -> this.showLeaders(
                thisPlayer.getLeaderCardsNotUsed(), thisPlayer.getPlayedLeaders(), thisPlayer.getPlayableLeaders(),
                thisPlayer.getPlayedNotActivatedOncePerRoundLeaderCards())));
        //todo: isLeaderStageCreated[0] = true;
        }

    }

    @FXML
    public void showOtherPlayerLeader1()
    {
        showOtherPlayerLeader(1);
    }
    @FXML
    public void showOtherPlayerLeader2()
    {
        showOtherPlayerLeader(2);
    }
    @FXML
    public void showOtherPlayerLeader3()
    {
        showOtherPlayerLeader(3);
    }

    @FXML
    private void showOtherPlayerLeader(int indexOfPlayerTab)
    {
        if(!isLeaderStageCreated[indexOfPlayerTab]) {
            //todo check index
            Player temp = otherPlayers.get(indexOfPlayerTab-1);
            Platform.runLater(() -> this.openNewWindow("LeaderOtherPlayersScene.fxml", "Choose a leader",
                    () -> this.showLeaders(
                            temp.getLeaderCardsNotUsed(),
                            temp.getPlayedLeaders(),
                            temp.getPlayableLeaders(),
                            temp.getPlayedNotActivatedOncePerRoundLeaderCards())));
            Debug.printVerbose("runLater loaded");
        }
    }

    private void showLeaders(ArrayList<LeaderCard> leaderNotUsed, List<LeaderCard> leaderActivated, List<LeaderCard> leadersPlayable, List<LeaderCard> leadersOPRNotActivated) {
        LeaderOwnedControl leaderOwnedControl = ((LeaderOwnedControl) (currentFXControl));
        leaderOwnedControl.setLeaders(leaderNotUsed,leaderActivated,leadersPlayable,leadersOPRNotActivated);
        return;
    }

    /**
     * Appends a message on the text area that displays the current state of the game
     * @param toAppend text to append
     */
    public void appendMessageOnStateTextArea(String toAppend) {
        String currentText = currentGameStateTextArea.getText();
        currentGameStateTextArea.setText(currentText + "\n" + "--> " + toAppend);
    }


    /**
     * This method opens a new window and shows it. It also sets the controller for the callbacks inside the custom fx controller
     * This method shoudl be passed as a parameter to the runLater fx method
     * @param fxmlFileName the fxml to start from
     * @param title the title of the window
     */
    private void openNewWindow(String fxmlFileName, String title, Runnable runBeforeShow) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/"+fxmlFileName));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            Debug.printError("Error in loading fxml", e);
        }

        currentFXControl = (fxmlLoader.getController());

        currentFXControl.setController(getController());

        secondStage.setTitle(title);
        secondStage.setScene(new Scene(root, -1, -1, true, SceneAntialiasing.BALANCED));

        if(runBeforeShow != null) //there is something to run
            runBeforeShow.run();

        secondStage.show();
    }


    @FXML
    public void familyMemberSelected(ActionEvent event) {
        ToggleButton buttonFM = ((ToggleButton) (event.getSource()));

        DiceAndFamilyMemberColorEnum colorEnum = DiceAndFamilyMemberColorEnum.valueOf(
                Character.getNumericValue(buttonFM.getId().charAt(2)));

        Platform.runLater(() -> getController().callbackFamilyMemberSelected(thisPlayer.getFamilyMemberByColor(colorEnum)));
    }
    //todo check this method
    @FXML
    private void harvestSelected(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Harvest");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");
        alert.showAndWait();
        //todo make the alert ask the user
        Platform.runLater(()->getController().callbackPlacedFMOnHarvest(5));
    }
    @FXML
    private void buildSelected(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Build");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");
        alert.showAndWait();

        Platform.runLater(()->getController().callbackPlacedFMOnBuild(5));
    }
    @FXML
    private void marketSelected(ActionEvent event)
    {
        //todo: this is for debug, remove
        Button actionSpace = ((Button) (event.getSource()));
        String id = actionSpace.getId();
        int marketIndex = Character.getNumericValue(id.charAt(8));
        Debug.printVerbose("Market placed" + marketIndex);
        Platform.runLater(()->getController().callbackPlacedFMOnMarket(marketIndex));

    }
    @FXML
    private void councilGiftSelected(ActionEvent event)
    {
        Button actionSpace = ((Button) (event.getSource()));
        //String id = actionSpace.getId();
        Platform.runLater(() -> getController().callbackPlacedFMOnCouncil());
    }
    @FXML
    private void towerFloorSelected(ActionEvent event) {
        Button actionSpace = ((Button) (event.getSource()));
        String id = actionSpace.getId();
        int towerIndex = Character.getNumericValue(id.charAt(7));
        int floorIndex = Character.getNumericValue(id.charAt(8));
        Platform.runLater(() -> getController().callbackPlacedFMOnTower(towerIndex, floorIndex));
    }

    /**
     * Shows a window with the list of cards passed as an argument
     * @param cards the cards to show to the user
     */
    private void showCards(List<? extends AbstractCard> cards, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        if(cards.isEmpty()) {
            alert.setHeaderText("No cards to show");
        } else {
            alert.setHeaderText(null);
            //alert.setContentText(errorDescription);

            HBox cardsContainer = new HBox();
            cardsContainer.setSpacing(5);
            cardsContainer.setAlignment(Pos.CENTER);

            for (AbstractCard cardIter : cards) {
                final Image cardImage = new Image(getClass().getResourceAsStream("/imgs/Cards/" + cardIter.getImgName()));
                final ImageView imgView = new ImageView();
                imgView.setImage(cardImage);
                imgView.setPreserveRatio(true);

                cardsContainer.getChildren().add(imgView);
                alert.setGraphic(cardsContainer);
            }
        }
        alert.initStyle(StageStyle.UTILITY);
        //alert.initOwner(currentStage);
        alert.show();
    }

    public void setActiveActionSpaces(Optional<Integer> servantsNeededHarvest,
                                      Optional<Integer> servantsNeededBuild,
                                      Optional<Integer> servantsNeededCouncil,
                                      List<MarketWrapper> activeMarketSpaces,
                                      List<TowerWrapper> activeTowerSpaces) {

        //we set all AS to disabled
        for(int col = 0; col < 4; col++) {
            for(int raw = 0; raw < 4; raw++) {
                Button activeTowersASButton = (Button) (towersCouncilFaith.lookup(("#towerAS" + col) + raw));
                activeTowersASButton.setDisable(true);
            }
        }
        for(int iterator = 0; iterator < 4; iterator++) {
            Button marketASButton = (Button) (marketPane.lookup("#marketAS" + iterator));
            marketASButton.setDisable(true);
        }

        //todo: disable build and harvest
        //first we need to disable build
        //we need to disable also harvest
        //servamts needed to build / harvest ?
        //todo: ci sono volte in cui non sempre è possibile piazzare un family member.. Com'è stato gestito? --Arto
        //setting council enabled
        Button activeCouncilASButton = (Button) (towersCouncilFaith.lookup("#councilGiftButton"));
        activeCouncilASButton.setDisable(false);
        //setting build and harvest enabled
        Button harvestSmallASButton = (Button) (buildHarvestPane.lookup("#harvestSmallActionSpace"));
        harvestSmallASButton.setDisable(false);
        Button harvestBigASButton = (Button) (buildHarvestPane.lookup("#harvestBigActionSpace"));
        harvestBigASButton.setDisable(false);
        Button buildSmallASButton = (Button) (buildHarvestPane.lookup("#buildSmallActionSpace"));
        buildSmallASButton.setDisable(false);
        Button buildBigASButton = (Button) (buildHarvestPane.lookup("#buildBigActionSpace"));
        buildBigASButton.setDisable(false);

        //setting harvest AS enable

        //we reactivate only the ones passed via parameters
        for(TowerWrapper towerWrapperIter : activeTowerSpaces) {
            Button activeTowersASButton = (Button) (towersCouncilFaith.lookup(("#towerAS" + towerWrapperIter.getTowerIndex()) + towerWrapperIter.getTowerFloor()));
            activeTowersASButton.setDisable(false);
        }
        //we reactivate only the AS passed via parameters -> problem here. Wrapper is not used correctly

        for(MarketWrapper marketIterator : activeMarketSpaces)
        {
                Button marketASButton = (Button) (marketPane.lookup("#marketAS" + marketIterator.getMarketIndex()));
                Debug.printVerbose("iterator on wrapper: " + marketIterator.getMarketIndex());
                marketASButton.setDisable(false);
        }

    }

    public void setOrderOfPlayers(List<Player> players) {
        //todo set circles
        Cylinder cylinder;

        for(int i = 0; i < players.size(); i++) {
            cylinder = (Cylinder) (towersCouncilFaith.lookup(("#orderCylinder" + i)));
            cylinder.setVisible(true);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.valueOf(players.get(i).getPlayerColor().getStringValue()));
            //material.setSpecularColor(Color.RED);
            cylinder.setMaterial(material);
        }
    }

}
