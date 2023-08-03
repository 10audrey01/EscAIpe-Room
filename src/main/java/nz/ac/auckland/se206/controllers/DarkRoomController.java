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
  @FXML private Rectangle vinyl;
  @FXML private Pane gameDialogue;
  @FXML private Pane gameDialogueYesNo;
  @FXML private Label itemLabel;
  @FXML private Label itemLabelYesNo;
  @FXML private Button yesButton;
  @FXML private Button noButton;
  @FXML private Button okButton;

  private enum Item {
    BOOK,
    WINDOW,
    VINYL
  }

  private Item currentItem;

  @FXML
  private void onClickBook() throws IOException {
    if (!GameState.isRiddleResolved) {
      App.setUi(AppUi.CHAT);
    } else if (GameState.isRiddleResolved) {
      itemLabel.setText("   You already solved the riddle! The answer was 'vinyl'.");
      gameDialogue.setVisible(true);
    }
  }

  @FXML
  private void onClickWindow() {
    currentItem = Item.WINDOW;
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
  private void onClickYes() throws IOException {
    switch (currentItem) {
      case WINDOW:
        gameDialogueYesNo.setVisible(false);
        App.setRoot("lightRoom");
        break;
      case BOOK:
        break;
      case VINYL:
        break;
      default:
        break;
    }
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
