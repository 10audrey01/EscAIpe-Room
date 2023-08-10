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
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class LightRoomController {

  private enum Item {
    WINDOW,
    VINYL_PLAYER
  }

  private static Timeline timeline;
  private static MediaPlayer vinylMediaPlayer;
  private static final Integer START_TIME_MIN = 2;
  private static final Integer START_TIME_SEC = 00;

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

  @FXML private TextArea itemTextArea;
  @FXML private TextArea itemTextAreaYesNo;
  @FXML private Label timerMinLabel;
  @FXML private Label timerSecLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  private Integer timeMinutes = START_TIME_MIN;
  private Integer timeSeconds = START_TIME_SEC;
  private Item currentItem;
  private ChatCompletionRequest chatCompletionRequest;
  private ChatMessage doorLockedInteraction;
  private ChatMessage drawBlindsInteraction;
  private ChatMessage vinylCollectInteraction;
  private ChatMessage playVinylInteraction;
  private ChatMessage stopVinylInteraction;
  private ChatMessage foundKeyInteraction;

  @FXML
  public void initialize() {
    startTimer();

    Task<Void> interactionsTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            // Edit AI behaviour here (temperature, topP, maxTokens)
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(1.2)
                    .setTopP(0.4)
                    .setMaxTokens(30);
            doorLockedInteraction =
                runGpt(
                    new ChatMessage(
                        "user", GptPromptEngineering.getInteraction("the door is locked")));

            drawBlindsInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "if they want to draw the blinds, maybe they will see something"
                                + " different")));
            vinylCollectInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "they collected a vinyl, maybe playing it will help them escape")));
            playVinylInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "if they want to play the vinyl they found")));
            stopVinylInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "if they want to stop playing the vinyl, even though the music is"
                                + " nice")));

            foundKeyInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction("they found a key in the guitar")));

            return null;
          }
        };

    Thread interactionsThread = new Thread(interactionsTask);
    interactionsThread.start();
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
      // itemLabel.setText("   The door is locked!");
      appendChatMessage(doorLockedInteraction, false);
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
    // itemLabelYesNo.setText("   Draw the blinds? Maybe you will see something different . . .");
    appendChatMessage(drawBlindsInteraction, true);
    yesButton.setText("Draw blinds");
    noButton.setText("Go back");
    gameDialogueYesNo.setVisible(true);
  }

  @FXML
  private void onClickVinyl() {
    if (GameState.isRiddleResolved && !GameState.isVinylFound) {
      // itemLabel.setText("   You collected a vinyl!");
      appendChatMessage(vinylCollectInteraction, false);
      GameState.isVinylFound = true;
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickVinylPlayer() {
    if (GameState.isVinylFound) {
      currentItem = Item.VINYL_PLAYER;
      appendChatMessage(playVinylInteraction, true);
      yesButton.setText("Play");
      noButton.setText("Go back");
      gameDialogueYesNo.setVisible(true);
    }

    if (GameState.isVinylPlaying) {
      currentItem = Item.VINYL_PLAYER;
      // itemLabelYesNo.setText("   Stop the vinyl player?");
      appendChatMessage(stopVinylInteraction, true);
      yesButton.setText("Stop");
      noButton.setText("Go back");
      gameDialogueYesNo.setVisible(true);
    }
  }

  @FXML
  private void onClickGuitar() {
    if (GameState.isVinylPlaying) {
      // itemTextArea.setText("   You found a key!");
      appendChatMessage(foundKeyInteraction, false);
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
          StartPageController.getMainMusicPlayer().play(); // if vinyl player is playing, stop it
        } else {
          gameDialogueYesNo.setVisible(false);
          Media vinylSong =
              new Media(App.class.getResource("/sounds/vinylSong.mp3").toURI().toString());
          vinylMediaPlayer = new MediaPlayer(vinylSong);
          StartPageController.getMainMusicPlayer().pause();
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

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws InterruptedException
   */
  public ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);

    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // TODO handle exception appropriately
      e.printStackTrace();
      return null;
    }
  }

  private void appendChatMessage(ChatMessage msg, boolean isYesOrNo) {
    if (isYesOrNo) {
      itemTextAreaYesNo.setText("Game Master: " + msg.getContent());
    } else {
      itemTextArea.setText("Game Master: " + msg.getContent());
    }
  }
}
