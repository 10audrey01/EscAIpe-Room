package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class StartPageController {

  private static MediaPlayer mainMusicPlayer;

  public static MediaPlayer getMainMusicPlayer() {
    return mainMusicPlayer;
  }

  @FXML private Button playButton;
  @FXML private Button settingsButton;
  @FXML private Button exitButton;

  @FXML
  public void initialize() throws URISyntaxException {
    Media mainMusic = new Media(App.class.getResource("/sounds/mainMusic.mp3").toURI().toString());
    mainMusicPlayer = new MediaPlayer(mainMusic);
    mainMusicPlayer.setOnEndOfMedia(
        new Runnable() {
          public void run() {
            mainMusicPlayer.seek(Duration.ZERO);
          }
        });
    mainMusicPlayer.play();
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

    FXMLLoader chatLoader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
    SceneManager.addUi(AppUi.CHAT, chatLoader.load());
    // Add controller for ChatController as reference for text to speech later
    SceneManager.addController(AppUi.CHAT, chatLoader.getController());

    App.setRoot("storyChat");
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
}
