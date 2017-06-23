package it.polimi.ingsw.client.gui.fxcontrollers;

import it.polimi.ingsw.model.leaders.LeaderCard;
import it.polimi.ingsw.utils.Debug;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Created by campus on 23/06/2017.
 */
public class CustomControllerLeaderChoices extends CustomFxController {

    @FXML
    HBox leaderContainer;

    public void addLeader(LeaderCard leader) {

        final ToggleButton toggle = new ToggleButton();
       /* System.out.println(getClass());
        System.out.println(getClass().getResource("/LorenzoRitratto.jpg"));
        System.out.println(getClass().getResource("/Leaders/leaders_f_c_01"));*/
        final Image leaderImage  = new Image(getClass().getResourceAsStream("/imgs/Leaders/leaders_f_c_01.jpg"));
        final ImageView toggleImage = new ImageView();
        toggle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                .when(toggle.selectedProperty())
                .then(leaderImage)
                .otherwise(leaderImage) //this shoulb be unselected
        );
        toggleImage.setFitHeight(400);
        toggleImage.setPreserveRatio(true);
        //toggle.setMaxHeight(100);

        leaderContainer.getChildren().add(toggle);
        Debug.printVerbose("leader " + leader.getName() + "addedd scuccesfully to the window");
    }
}
