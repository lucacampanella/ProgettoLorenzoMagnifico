package it.polimi.ingsw.client.gui.fxcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This control performs no callbacks, just shows a window that tells the user the game is waiting for something
 */
public class WaitingSceneControl extends CustomFxControl {

    @FXML
    Label messageLabel;

    /**
     * Sets the message to show the user
     * @param message the message
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
