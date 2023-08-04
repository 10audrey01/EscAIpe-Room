package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class StartPageController {

  @FXML private Button playButton;
  @FXML private Button settingsButton;
  @FXML private Button exitButton;

  private static MediaPlayer musicPlayer;

  @FXML
  public void initialize() throws URISyntaxException {
    Media mainMusic = new Media(App.class.getResource("/sounds/mainMusic.mp3").toURI().toString());
    musicPlayer = new MediaPlayer(mainMusic);
    musicPlayer.setOnEndOfMedia(
        new Runnable() {
          public void run() {
            musicPlayer.seek(Duration.ZERO);
          }
        });
    musicPlayer.play();
  }

  @FXML
  private void onClickPlayButton(ActionEvent event) throws IOException {
    GameState.isRiddleResolved = false;
    GameState.isKeyFound = false;
    GameState.isVinylFound = false;
    GameState.isVinylPlaying = false;
    GameState.isEscaped = false;

    SceneManager.addUi(AppUi.LIGHT_ROOM, App.loadFxml("lightRoom"));
    SceneManager.addUi(AppUi.DARK_ROOM, App.loadFxml("darkRoom"));
    SceneManager.addUi(AppUi.CHAT, App.loadFxml("chat"));

    LightRoomController.playTimer();
    DarkRoomController.playTimer();

    App.setUi(AppUi.LIGHT_ROOM);
  }

  @FXML
  private void onClickSettingsButton(ActionEvent event) throws IOException {

    SettingsPageController controller =
        (SettingsPageController) SceneManager.getController(AppUi.SETTINGS_PAGE);
    controller.setPreviousUi(AppUi.START_PAGE);

    App.setUi(AppUi.SETTINGS_PAGE);
  }

  @FXML
  private void onClickExitButton(ActionEvent event) {
    System.exit(0);
  }

  public static MediaPlayer getMusicPlayer() {
    return musicPlayer;
  }
}
