package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class DarkRoomController {

  @FXML private Rectangle riddleBook;
  @FXML private Rectangle window;
  @FXML private Rectangle vinyl;
  @FXML private Pane gameDialogue;
  @FXML private Pane gameDialogueYesNo;
  @FXML private Label itemLabel;
  @FXML private Label itemLabelYesNo;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  @FXML
  private void onClickBook() throws IOException, URISyntaxException {
    if (!GameState.isRiddleResolved) {
      MediaPlayer bookOpeningPlayer =
          new MediaPlayer(
              new Media(getClass().getResource("/sounds/bookOpening.mp3").toURI().toString()));
      bookOpeningPlayer.play();
      App.setUi(AppUi.CHAT);
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
      itemLabel.setText("   You can't see where the vinyl is!");
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickYes() throws IOException, URISyntaxException {
    gameDialogueYesNo.setVisible(false);
    MediaPlayer blindsPlayer =
        new MediaPlayer(new Media(getClass().getResource("/sounds/blinds.mp3").toURI().toString()));
    blindsPlayer.play();
    App.setRoot("lightRoom");
  }

  @FXML
  public void onClickNo() {
    gameDialogueYesNo.setVisible(false);
  }

  @FXML
  public void onClickOk() {
    gameDialogue.setVisible(false);
  }
}
