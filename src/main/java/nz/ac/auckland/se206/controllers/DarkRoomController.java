package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class DarkRoomController {

  private static Timeline timeline;
  private static final Integer START_TIME_MIN = 2;
  private static final Integer START_TIME_SEC = 00;

  public static void playTimer() {
    timeline.play();
  }

  public static void stopTimer() {
    timeline.stop();
  }

  @FXML private Rectangle riddleBook;
  @FXML private Rectangle window;
  @FXML private Rectangle vinyl;
  @FXML private Rectangle door;
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
  private AtomicBoolean hasBookSpoken = new AtomicBoolean(false);
  private ChatCompletionRequest chatCompletionRequest;
  private ChatMessage doorLockedInteraction;
  private ChatMessage openBlindsInteraction;
  private ChatMessage collectVinylInteraction;
  private ChatMessage bookAfterRiddleSolvedInteraction;

  @FXML
  private void initialize() {
    startTimer();
    Glow glow = new Glow();
    glow.setLevel(1.0);
    riddleBook.setEffect(glow);

    Task<Void> interactionsTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            // Edit AI behaviour here (temperature, topP, maxTokens)
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(1.2)
                    .setTopP(0.8)
                    .setMaxTokens(30);
            doorLockedInteraction =
                runGpt(
                    new ChatMessage(
                        "user", GptPromptEngineering.getInteraction("the door is locked")));
            openBlindsInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction("if they want to open the blinds")));
            bookAfterRiddleSolvedInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "they already solved the riddle and answer was 'vinyl'")));
            collectVinylInteraction =
                runGpt(
                    new ChatMessage(
                        "user",
                        GptPromptEngineering.getInteraction(
                            "they can't see where the vinyl is because it's too dark")));
            return null;
          }
        };

    Thread interactionsThread = new Thread(interactionsTask);
    interactionsThread.start();
  }

  @FXML
  private void onClickDoor() throws URISyntaxException {
    // itemLabel.setText("   The door is locked!");
    appendChatMessage(doorLockedInteraction, false);
    MediaPlayer lockedDoorPlayer =
        new MediaPlayer(
            new Media(getClass().getResource("/sounds/doorLocked.mp3").toURI().toString()));
    lockedDoorPlayer.play();
    gameDialogue.setVisible(true);
  }

  @FXML
  private void onClickBook() throws IOException, URISyntaxException {

    // Open book and play sound if riddle is not solved
    if (!GameState.isRiddleResolved) {
      MediaPlayer bookOpeningPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/bookOpening.mp3").toURI().toString()));
      bookOpeningPlayer.play();

      App.setUi(AppUi.CHAT);

      // Task gets current text from chat and sends it to text to speech
      Task<Void> textToSpeechTask =
          new Task<Void>() {
            @Override
            protected Void call() throws Exception {
              ChatController chatController =
                  (ChatController) SceneManager.getController(AppUi.CHAT);
              String textWithoutRole = chatController.getChatText().substring(5);
              TextToSpeech.main(new String[] {textWithoutRole});
              return null;
            }
          };

      // Only do text to speech once
      if (hasBookSpoken.compareAndSet(false, true)) {
        Thread textToSpeechThread = new Thread(textToSpeechTask, "textToSpeechThread");
        textToSpeechThread.start();
      }

      // Riddle view not available if riddle is solved, so show dialogue
    } else if (GameState.isRiddleResolved) {
      // itemLabel.setText("   You already solved the riddle! The answer was 'vinyl'.");
      appendChatMessage(bookAfterRiddleSolvedInteraction, false);
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickWindow() {
    // itemLabelYesNo.setText("   Open the blinds?");
    appendChatMessage(openBlindsInteraction, true);
    yesButton.setText("Open blinds");
    noButton.setText("Go back");
    gameDialogueYesNo.setVisible(true);
  }

  @FXML
  private void onClickVinyl() {
    if (GameState.isRiddleResolved) {
      // itemLabel.setText("   You can't see where the vinyl is . . .");
      appendChatMessage(collectVinylInteraction, false);
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
