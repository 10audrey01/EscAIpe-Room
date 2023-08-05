package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
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
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class DarkRoomController {

  private static final Integer START_TIME_MIN = 2;
  private static final Integer START_TIME_SEC = 00;
  private static Timeline timeline;

  public static void playTimer() {
    timeline.play();
  }

  public static void stopTimer() {
    timeline.stop();
  }

  @FXML private Rectangle riddleBook;
  @FXML private Rectangle window;
  @FXML private Rectangle vinyl;
  @FXML private Pane gameDialogue;
  @FXML private Pane gameDialogueYesNo;
  @FXML private Label itemLabel;
  @FXML private Label itemLabelYesNo;
  @FXML private Label timerMinLabel;
  @FXML private Label timerSecLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  private Integer timeMinutes = START_TIME_MIN;
  private Integer timeSeconds = START_TIME_SEC;

  @FXML
  private void initialize() {
    startTimer();
  }

  @FXML
  private void onClickBook() throws IOException, URISyntaxException {
    if (!GameState.isRiddleResolved) {
      MediaPlayer bookOpeningPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/bookOpening.mp3").toURI().toString()));
      bookOpeningPlayer.play();

      App.setUi(AppUi.CHAT);

      Task<Void> textToSpeechTask =
          new Task<Void>() {
            @Override
            protected Void call() throws Exception {
              ChatController chatController =
                  (ChatController) SceneManager.getController(AppUi.CHAT);
              TextToSpeech.main(new String[] {chatController.getChatText()});
              return null;
            }
          };

      Thread textToSpeechThread = new Thread(textToSpeechTask, "textToSpeechThread");
      textToSpeechThread.start();
    } else if (GameState.isRiddleResolved) {
      itemLabel.setText("   You already solved the riddle! The answer was 'vinyl'.");
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickWindow() {
    itemLabelYesNo.setText("   Open the blinds?");
    gameDialogueYesNo.setVisible(true);
  }

  @FXML
  private void onClickVinyl() {
    if (GameState.isRiddleResolved) {
      itemLabel.setText("   You can't see where the vinyl is . . .");
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickYes() throws IOException, URISyntaxException {
    gameDialogueYesNo.setVisible(false);
    MediaPlayer blindsPlayer =
        new MediaPlayer(new Media(getClass().getResource("/sounds/blinds.mp3").toURI().toString()));
    blindsPlayer.play();
    App.setUi(AppUi.LIGHT_ROOM);
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
