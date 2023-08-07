package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class EndPageController {

  @FXML private Label endLabel;
  @FXML private Button playAgainButton;
  @FXML private Button returnToMainMeuButton;

  private MediaPlayer escapePlayer;
  private MediaPlayer escapeFailPlayer;

  @FXML
  public void initialize() throws URISyntaxException {
    StartPageController.getMainMusicPlayer().stop();

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
      escapeFailPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/escapeFail.mp3").toURI().toString()));
      escapeFailPlayer.setOnEndOfMedia(
          new Runnable() {
            public void run() {
              escapeFailPlayer.seek(Duration.ZERO);
            }
          });
      escapeFailPlayer.play();
    }

    LightRoomController.stopTimer();
    DarkRoomController.stopTimer();
    ChatController.stopTimer();
  }

  @FXML
  private void onClickPlayAgain() throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlaying = false;
    GameState.isEscaped = false;

    if (escapePlayer != null) {
      escapePlayer.stop();
    } else if (escapeFailPlayer != null) {
      escapeFailPlayer.stop();
    }
    StartPageController.getMainMusicPlayer().play();

    SceneManager.addUi(AppUi.LIGHT_ROOM, App.loadFxml("lightRoom"));
    SceneManager.addUi(AppUi.DARK_ROOM, App.loadFxml("darkRoom"));

    FXMLLoader chatLoader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
    SceneManager.addUi(AppUi.CHAT, chatLoader.load());
    SceneManager.addController(AppUi.CHAT, chatLoader.getController());

    App.setRoot("storyChat");
  }

  @FXML
  private void onClickReturnToMainMenu() throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlaying = false;
    GameState.isEscaped = false;

    if (escapePlayer != null) {
      escapePlayer.stop();
    }
    App.setRoot("startPage");
  }
}
