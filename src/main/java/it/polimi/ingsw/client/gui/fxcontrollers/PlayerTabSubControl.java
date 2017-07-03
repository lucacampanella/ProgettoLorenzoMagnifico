package it.polimi.ingsw.client.gui.fxcontrollers;

import it.polimi.ingsw.client.controller.ViewControllerCallbackInterface;
import it.polimi.ingsw.model.board.CardColorEnum;
import it.polimi.ingsw.model.cards.AbstractCard;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utils.Debug;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

/**
 * This class represents the controller with all the graphical informations related to a tab of a certain player
 */
public class PlayerTabSubControl extends CustomFxControl {

    Player player;
    PersonalBoard personalBoard;
    /**
     * true if the tab is linked with the player which controls the client
     */
    boolean isThisPlayer = false;

    @FXML
    ImageView thisPlayerPersonalTile;

    @FXML
    private Button blueCardsButton;

    @FXML
    private Button purpleCardsButton;

    @FXML
    private Button passTurnButton;

    @FXML
    private ToolBar buttonsToolBar;


    public PlayerTabSubControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PlayerTabSubScene.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try {
            Debug.printVerbose("before running load");
            fxmlLoader.load();
            Debug.printVerbose("after running load");
        } catch (IOException exception) {
            Debug.printError("Exception caught");
            Debug.printError(exception);
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    /**
     * This method is used at the beginning of the game when we have to setup a tab for each player with his information
     * @param controller the controller for callbacks
     * @param relatedPlayer the player related to the tab
     * @param isThisPlayer true if the tab is linked with the player which controls the client
     */
    public void setUpTab(ViewControllerCallbackInterface controller, Player relatedPlayer, boolean isThisPlayer) {
        this.isThisPlayer = isThisPlayer;
        setController(controller);
        setRelatedPlayer(relatedPlayer);
        personalBoard = player.getPersonalBoard();
        setPersonalTile();
        refresh();
        if(!isThisPlayer) {
            buttonsToolBar.getItems().remove(passTurnButton);
        }
    }

    /**
     * This method is used to refresh the tab after the player performed an action
     */
    public void refresh() {
        //todo set the cards of the player to the board
        //todo set the resources of the player to the board
        //we enable or disable the buttons to see blue and purple cards if the player has or has not some of them
        purpleCardsButton.setDisable((personalBoard.getNumberOfColoredCard(CardColorEnum.PURPLE) == 0));
        blueCardsButton.setDisable((personalBoard.getNumberOfColoredCard(CardColorEnum.BLUE) == 0));
    }

    /**
     * Sets the instance of the player related to this tab
     * @param player
     */
    private void setRelatedPlayer(Player player) {
        this.player = player;
    }

    /**
     * Sets the personal tile image of the related player
     */
    private void setPersonalTile() {
        Debug.printVerbose("setUpPersonalTIles called");
        Debug.printVerbose(player.getPersonalBoard().getPersonalTile().getImgName());
        Image tileImg  = new Image(getClass().getResourceAsStream("/imgs/PersonalBonusTiles/Long/" +
                        player.getPersonalBoard().getPersonalTile().getImgName()));
        thisPlayerPersonalTile.setImage(tileImg);
        thisPlayerPersonalTile.setPreserveRatio(true);
    }

    @FXML
    public void showPurpleCards() {
        showCards(player.getPersonalBoard().getCardListByColor(CardColorEnum.PURPLE), "Purple cards");
    }

    @FXML
    public void showBlueCards() {
        showCards(player.getPersonalBoard().getCardListByColor(CardColorEnum.BLUE), "Blue cards");
    }

    @FXML
    public void passTurn()
    {
        Platform.runLater(()-> getController().callBackPassTheTurn());
    }

    /**
     * owned cards leader
     */
    /*@FXML
    public void showLeaderCards() {
        if (!isLeaderStageCreated[0]) {

            Platform.runLater(() -> this.openNewWindow("LeaderOwnedScene.fxml", "Choose a leader", () -> this.showLeaders(
                    thisPlayer.getLeaderCardsNotUsed(), thisPlayer.getPlayedLeaders(), thisPlayer.getPlayableLeaders(),
                    thisPlayer.getPlayedNotActivatedOncePerRoundLeaderCards())));
            //todo: isLeaderStageCreated[0] = true;
        }

    }*/

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


}
