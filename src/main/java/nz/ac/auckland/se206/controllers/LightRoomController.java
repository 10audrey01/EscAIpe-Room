package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;

public class LightRoomController {

  @FXML private Rectangle window;
  @FXML private Pane gameDialogue;
  @FXML private Label itemLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;

  @FXML
  public void onClickWindow() {
    itemLabel.setText("   Draw the blinds?");
    gameDialogue.setVisible(true);
  }

  @FXML
  public void onClickYes() throws IOException {
    App.setRoot("darkRoom");
  }

  @FXML
  public void onClickNo() {
    gameDialogue.setVisible(false);
  }
}
