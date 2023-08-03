package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** Controller class for the chat view. */
public class ChatController {
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Label chatDialogueLabel;
  @FXML private Button sendButton;
  @FXML private Button goBackButton;

  private ChatCompletionRequest chatCompletionRequest;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {

    Task<Void> initializeTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            // Edit AI behaviour here (temperature, topP, maxTokens)
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.8)
                    .setTopP(0.8)
                    .setMaxTokens(100);
            runGpt(new ChatMessage("user", GptPromptEngineering.getRiddleWithGivenWord("vinyl")));
            return null;
          }
        };

    initializeTask.setOnRunning(
        e -> {
          chatDialogueLabel.setText(
              "Flipping through the pages . . ."); // let user know riddle is loading
        });

    initializeTask.setOnSucceeded(
        e -> {
          chatDialogueLabel.setText("You found a riddle!"); // let user know riddle is loaded
        });

    Thread initializeThread = new Thread(initializeTask, "initializeThread");
    initializeThread.start();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    if (msg.getRole().equals("user")) {
      chatTextArea.appendText("You: " + msg.getContent() + "\n\n");
    } else if (msg.getRole().equals("assistant")) {
      chatTextArea.appendText("Book: " + msg.getContent() + "\n\n");
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
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
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
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    // let user know checking if there answer is right
    chatDialogueLabel.setText("Looks like the book is checking if you are worthy . . .");
    String message = inputText.getText();
    sendButton.setDisable(true); // disable send button while processing message
    goBackButton.setDisable(true);

    if (message.trim().isEmpty()) { // if user sends empty message, do nothing
      sendButton.setDisable(false);
      goBackButton.setDisable(false);
      return;
    }

    Task<Void> onSendMessageTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            inputText.clear();
            ChatMessage msg = new ChatMessage("user", message);
            appendChatMessage(msg);
            ChatMessage lastMsg = runGpt(msg);
            // if the last message is from the assistant and starts with "Correct"
            if (lastMsg.getRole().equals("assistant")
                && lastMsg.getContent().startsWith("Correct")) {
              GameState.isRiddleResolved = true;
            }
            return null;
          }
        };

    onSendMessageTask.setOnSucceeded(
        e -> {
          sendButton.setDisable(false);
          goBackButton.setDisable(false);

          if (GameState.isRiddleResolved) {
            chatDialogueLabel.setText("You are worthy!"); // riddle is solved
          } else {
            chatDialogueLabel.setText(
                "You are not worthy, try again!"); // riddle is not solved, try again
          }
        });

    onSendMessageTask.setOnFailed(
        e -> {
          sendButton.setDisable(false);
          goBackButton.setDisable(false);
        });

    Thread onSendMessageThread = new Thread(onSendMessageTask, "onSendMessageThread");
    onSendMessageThread.start();
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("darkRoom");
  }
}
