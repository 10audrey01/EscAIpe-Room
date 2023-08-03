package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

public class EndPageController {

  @FXML private Label endLabel;
  @FXML private Button playAgainButton;
  @FXML private Button returnToMainMeuButton;

  @FXML
  public void initialize() {
    if (LightRoomController.getVinylMediaPlayer() != null) {
      LightRoomController.getVinylMediaPlayer().stop();
    }

    if (GameState.isEscaped) {
      endLabel.setText("You escaped the room!");
    } else {
      endLabel.setText("You failed to escape the room!");
    }
  }

  @FXML
  public void onClickPlayAgain() throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlayed = false;
    GameState.isEscaped = false;

    StartPageController.getMusicPlayer().play();

    App.setRoot("lightRoom");
  }

  @FXML
  public void onClickReturnToMainMenu() throws IOException {
    App.setRoot("startPage");
  }
}
