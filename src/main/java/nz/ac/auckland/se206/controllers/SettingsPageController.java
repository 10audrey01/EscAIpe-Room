package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsPageController {

  @FXML private Button settingsGoBackButton;

  private AppUi previousScene;

  public void setPreviousUi(AppUi previousScene) {
    this.previousScene = previousScene;
  }

  @FXML
  public void onGoBackFromSettings(ActionEvent event) {
    App.setUi(previousScene);
  }
}
