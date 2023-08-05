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
import nz.ac.auckland.se206.SceneManager.AppUi;

public class LightRoomController {

  private static final Integer START_TIME_MIN = 2;
  private static final Integer START_TIME_SEC = 00;
  private static MediaPlayer vinylMediaPlayer;
  private static Timeline timeline;

  public static MediaPlayer getVinylMediaPlayer() {
    return vinylMediaPlayer;
  }

  public static void playTimer() {
    timeline.play();
  }

  public static void stopTimer() {
    timeline.stop();
  }

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

  private Integer timeMinutes = START_TIME_MIN;
  private Integer timeSeconds = START_TIME_SEC;
  private Item currentItem;

  @FXML
  public void initialize() {
    startTimer();
  }

  @FXML
  private void onClickDoor() throws IOException, URISyntaxException {
    if (GameState.isKeyFound) {
      GameState.isEscaped = true;
      MediaPlayer doorOpenPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/doorOpen.mp3").toURI().toString()));
      doorOpenPlayer.play();
      App.setRoot("endPage");
    } else {
      itemLabel.setText("   The door is locked!");
      MediaPlayer lockedDoorPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/doorLocked.mp3").toURI().toString()));
      lockedDoorPlayer.play();
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

      if (GameState.isVinylPlaying) {
        currentItem = Item.VINYL_PLAYER;
        itemLabelYesNo.setText("   Stop the vinyl player?");
        gameDialogueYesNo.setVisible(true);
      }
    }
  }

  @FXML
  private void onClickGuitar() {
    if (GameState.isVinylPlaying) {
      itemLabel.setText("   You found a key!");
      gameDialogue.setVisible(true);
      GameState.isKeyFound = true;
    }
  }

  @FXML
  private void onClickYes() throws IOException, URISyntaxException {
    switch (currentItem) { // switch statement to handle different items
      case WINDOW:
        gameDialogueYesNo.setVisible(false);
        MediaPlayer blindsPlayer =
            new MediaPlayer(
                new Media(getClass().getResource("/sounds/blinds.mp3").toURI().toString()));
        blindsPlayer.play();
        App.setUi(AppUi.DARK_ROOM); // if window is clicked and yes is clicked, go to dark room
        break;
      case VINYL_PLAYER: // if vinyl player is clicked and yes is clicked
        if (GameState.isVinylPlaying) {
          gameDialogueYesNo.setVisible(false);
          vinylMediaPlayer.stop();
          GameState.isVinylPlaying = false;
          StartPageController.getMusicPlayer().play(); // if vinyl player is playing, stop it
        } else {
          gameDialogueYesNo.setVisible(false);
          Media vinylSong =
              new Media(App.class.getResource("/sounds/vinylSong.mp3").toURI().toString());
          vinylMediaPlayer = new MediaPlayer(vinylSong);
          StartPageController.getMusicPlayer().pause();
          vinylMediaPlayer.play();
          GameState.isVinylPlaying = true; // if vinyl player is not playing, play it
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

  public void startTimer() {
    timerMinLabel.setText(timeMinutes.toString());
    timerSecLabel.setText(": 00");
    timeline = new Timeline(); // create a timeline for the timer
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1), // handler is called every second
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    timeSeconds--;
                    if (timeSeconds < 0
                        && timeMinutes > 0) { // decrement minutes if seconds reach 0
                      timeMinutes--;
                      timeSeconds = 59;
                    }
                    timerMinLabel.setText(timeMinutes.toString());
                    if (timeSeconds < 10) {
                      timerSecLabel.setText(": 0" + timeSeconds.toString()); // aesthetic purposes
                    } else {
                      timerSecLabel.setText(": " + timeSeconds.toString());
                    }
                    if (timeMinutes <= 0 && timeSeconds <= 0) {
                      timeline.stop();
                      try {
                        App.setRoot("endPage"); // go to end page if time runs out
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                }));
  }
}
