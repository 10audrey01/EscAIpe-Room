package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

public class LightRoomController {

  @FXML private Rectangle door;
  @FXML private Rectangle window;
  @FXML private Rectangle vinyl;
  @FXML private Rectangle vinylPlayer;
  @FXML private Rectangle guitar;
  @FXML private Pane gameDialogue;
  @FXML private Pane gameDialogueYesNo;
  @FXML private Label itemLabel;
  @FXML private Label itemLabelYesNo;
  @FXML private Label timerMinLabel;
  @FXML private Label timerSecLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  private enum Item {
    WINDOW,
    VINYL_PLAYER
  }

  private static MediaPlayer vinylMediaPlayer;
  private Timeline timeline;
  private Integer timeMinutes;
  private Integer timeSeconds;
  private Item currentItem;

  @FXML
  public void initialize() {
    startTimer();
  }

  @FXML
  private void onClickDoor() throws IOException {
    if (GameState.isKeyFound) {
      GameState.isEscaped = true;
      App.setRoot("endPage");
    } else {
      itemLabel.setText("   The door is locked!");
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickWindow() {
    currentItem = Item.WINDOW;
    itemLabelYesNo.setText("   Draw the blinds?");
    gameDialogueYesNo.setVisible(true);
  }

  @FXML
  private void onClickVinyl() {
    if (GameState.isRiddleResolved && !GameState.isVinylFound) {
      itemLabel.setText("   You collected a vinyl!");
      GameState.isVinylFound = true;
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickVinylPlayer() {
    if (GameState.isVinylFound) {
      currentItem = Item.VINYL_PLAYER;
      itemLabelYesNo.setText("   Play the vinyl?");
      gameDialogueYesNo.setVisible(true);
    }

    if (vinylMediaPlayer != null) {
      currentItem = Item.VINYL_PLAYER;
      itemLabelYesNo.setText("   Stop the vinyl player?");
      gameDialogueYesNo.setVisible(true);
    }
  }

  @FXML
  private void onClickGuitar() {
    if (GameState.isVinylPlayed) {
      itemLabel.setText("   You found a key!");
      gameDialogue.setVisible(true);
      GameState.isKeyFound = true;
    }
  }

  @FXML
  private void onClickYes() throws IOException, URISyntaxException {
    switch (currentItem) {
      case WINDOW:
        gameDialogueYesNo.setVisible(false);
        MediaPlayer blindsPlayer =
            new MediaPlayer(
                new Media(getClass().getResource("/sounds/blinds.mp3").toURI().toString()));
        blindsPlayer.play();
        App.setRoot("darkRoom");
        break;
      case VINYL_PLAYER:
        if (vinylMediaPlayer != null) {
          gameDialogueYesNo.setVisible(false);
          vinylMediaPlayer.stop();
          vinylMediaPlayer = null;
          StartPageController.getMusicPlayer().play();
        } else {

          gameDialogueYesNo.setVisible(false);
          Media vinylSong =
              new Media(App.class.getResource("/sounds/vinylSong.mp3").toURI().toString());
          vinylMediaPlayer = new MediaPlayer(vinylSong);
          StartPageController.getMusicPlayer().pause();
          vinylMediaPlayer.play();
          GameState.isVinylPlayed = true;
          break;
        }
    }
  }

  @FXML
  private void onClickNo() {
    gameDialogueYesNo.setVisible(false);
  }

  @FXML
  private void onClickOk() {
    gameDialogue.setVisible(false);
  }

  public static MediaPlayer getVinylMediaPlayer() {
    return vinylMediaPlayer;
  }

  public void startTimer() {
    timerMinLabel.setText("2");
    timerSecLabel.setText(": 00");
    timeline = new Timeline();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    timeSeconds--;
                    if (timeSeconds < 0 && timeMinutes > 0) {
                      timeMinutes--;
                      timeSeconds = 59;
                    }
                    timerMinLabel.setText(timeMinutes.toString());
                    if (timeSeconds < 10) {
                      timerSecLabel.setText(": 0" + timeSeconds.toString());
                    } else {
                      timerSecLabel.setText(": " + timeSeconds.toString());
                    }
                    if (timeMinutes <= 0 && timeSeconds <= 0) {
                      timeline.stop();
                      try {
                        App.setRoot("endPage");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                }));
    timeline.playFromStart();
  }
}
