package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

public class LightRoomController {

  @FXML private Rectangle window;
  @FXML private Rectangle vinyl;
  @FXML private Pane gameDialogue;
  @FXML private Pane gameDialogueYesNo;
  @FXML private Label itemLabel;
  @FXML private Label itemLabelYesNo;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  private enum Item {
    WINDOW,
    VINYL
  }

  private Item currentItem;

  @FXML
  private void onClickWindow() {
    currentItem = Item.WINDOW;
    itemLabelYesNo.setText("   Draw the blinds?");
    gameDialogueYesNo.setVisible(true);
  }

  @FXML
  private void onClickVinyl() {
    if (GameState.isRiddleResolved && !GameState.isVinylFound) {
      currentItem = Item.VINYL;
      itemLabel.setText("   You collected a vinyl!");
      GameState.isVinylFound = true;
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickYes() throws IOException {
    switch (currentItem) {
      case WINDOW:
        gameDialogueYesNo.setVisible(false);
        App.setRoot("darkRoom");
        break;
      case VINYL:
        System.out.println("Play vinyl");
        break;
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
}
