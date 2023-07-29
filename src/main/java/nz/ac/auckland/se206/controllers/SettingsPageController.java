package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsPageController {

  @FXML private Button settingsGoBackButton;

  @FXML
  private void onGoBackFromSettings() {
    System.out.println("Go back button clicked");
  }
}
