package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class DarkRoomController {

  @FXML private Rectangle riddleBook;
  @FXML private Rectangle window;
  @FXML private Pane gameDialogue;
  @FXML private Label itemLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;

  @FXML
  public void onClickBook() {
    if (!GameState.isRiddleResolved) {
      App.setUi(AppUi.CHAT);
    }
  }

  @FXML
  public void onClickWindow() {
    itemLabel.setText("   Open the blinds?");
    gameDialogue.setVisible(true);
  }

  @FXML
  public void onClickYes() throws IOException {
    App.setRoot("lightRoom");
  }

  @FXML
  public void onClickNo() {
    gameDialogue.setVisible(false);
  }
}
