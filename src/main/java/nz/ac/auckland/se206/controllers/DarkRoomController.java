package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class DarkRoomController {

  @FXML private Rectangle riddleBook;

  @FXML
  public void onClickBook() {
    if (!GameState.isRiddleResolved) {
      App.setUi(AppUi.CHAT);
    }
  }
}
