package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class StoryChatController {

  @FXML private Pane aiDialogue;
  @FXML private TextArea aiTextArea;
  @FXML private TextField userTextField;
  @FXML private Button talkButton;
  @FXML private Button aiOkButton;
  @FXML private Label loadingLabel;

  private ChatCompletionRequest chatCompletionRequest;

  /**
   * Initializes the story chat view, loading the storyline.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {

    Task<Void> initializeStoryTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Edit AI behaviour here (temperature, topP, maxTokens)
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(1.2)
                    .setTopP(1)
                    .setMaxTokens(200);
            runGpt(new ChatMessage("user", GptPromptEngineering.getStorylineAndInstructions()));
            return null;
          }
        };

    // Task to convert text to speech
    Task<Void> textToSpeechTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            TextToSpeech.main(new String[] {aiTextArea.getText()});
            return null;
          }
        };

    // Play sleeping music while story is loading and inform user that story is loading
    MediaPlayer sleepingPlayer =
        new MediaPlayer(
            new Media(new File("src/main/resources/sounds/sleeping.mp3").toURI().toString()));
    StartPageController.getMainMusicPlayer().pause();
    sleepingPlayer.play();

    initializeStoryTask.setOnSucceeded(
        e -> {
          // Hide loading label and show story
          loadingLabel.setVisible(false);
          aiDialogue.setStyle("-fx-background-color: #ffff;");
          aiTextArea.setVisible(true);
          aiOkButton.setVisible(true);
          // Stop sleeping music and play story
          sleepingPlayer.stop();
          StartPageController.getMainMusicPlayer().play();
          Thread textToSpeechThread = new Thread(textToSpeechTask, "textToSpeechThread");
          textToSpeechThread.start();
        });

    Thread initializeStoryThread = new Thread(initializeStoryTask, "initializeStoryThread");
    initializeStoryThread.start();
  }

  @FXML
  private void onClickAiOk() {
    App.setUi(AppUi.LIGHT_ROOM);

    LightRoomController.playTimer();
    DarkRoomController.playTimer();
    ChatController.playTimer();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    if (msg.getRole().equals("user")) {
      aiTextArea.appendText("You: " + msg.getContent() + "\n\n");
    } else if (msg.getRole().equals("assistant")) {
      aiTextArea.appendText(msg.getContent() + "\n\n");
    }
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
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // TODO handle exception appropriately
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onClickTalk(ActionEvent event) throws ApiProxyException, IOException {
    String message = userTextField.getText();
    talkButton.setDisable(true); // disable send button while processing message

    if (message.trim().isEmpty()) { // if user sends empty message, do nothing
      talkButton.setDisable(false);
      return;
    }

    Task<Void> onClickTalkTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            userTextField.clear();
            ChatMessage msg = new ChatMessage("user", message);
            appendChatMessage(msg);
            runGpt(msg);
            return null;
          }
        };

    onClickTalkTask.setOnSucceeded(
        e -> {
          talkButton.setDisable(false);
        });

    onClickTalkTask.setOnFailed(
        e -> {
          talkButton.setDisable(false);
        });

    Thread onClickTalkThread = new Thread(onClickTalkTask, "onClickTalkThread");
    onClickTalkThread.start();
  }
}
