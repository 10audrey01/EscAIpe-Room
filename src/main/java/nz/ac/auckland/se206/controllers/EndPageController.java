package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

public class EndPageController {

  @FXML private Label endLabel;
  @FXML private Button playAgainButton;
  @FXML private Button returnToMainMeuButton;

  private MediaPlayer escapePlayer;

  @FXML
  public void initialize() throws URISyntaxException {
    if (LightRoomController.getVinylMediaPlayer() != null) {
      LightRoomController.getVinylMediaPlayer().stop();
    }

    if (GameState.isEscaped) {
      endLabel.setText("You escaped!");
      escapePlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/escape.mp3").toURI().toString()));
      escapePlayer.setOnEndOfMedia(
          new Runnable() {
            public void run() {
              escapePlayer.seek(Duration.ZERO);
            }
          });
      escapePlayer.play();
    } else {
      endLabel.setText("You failed to escape . . .");
    }
  }

  @FXML
  public void onClickPlayAgain() throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlayed = false;
    GameState.isEscaped = false;

    if (escapePlayer != null) {
      escapePlayer.stop();
    }
    StartPageController.getMusicPlayer().play();

    App.setRoot("lightRoom");
  }

  @FXML
  public void onClickReturnToMainMenu() throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlayed = false;
    GameState.isEscaped = false;

    if (escapePlayer != null) {
      escapePlayer.stop();
    }
    App.setRoot("startPage");
  }
}
