package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartPageController {

  @FXML private Button playButton;
  @FXML private Button settingsButton;

  @FXML
  private void playGame() {
    System.out.println("Play button clicked");
  }

  @FXML
  private void openSettings() {
    System.out.println("Settings button clicked");
  }
}
