package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class StartPageController {

  @FXML private Button playButton;
  @FXML private Button settingsButton;

  @FXML
  private void playGame(ActionEvent event) {
    System.out.println("Play button clicked");
    Button button = (Button) event.getSource(); // reference to the button that was clicked (event)
    Scene sceneButtonIsIn = button.getScene(); // gets scene the button is in
    try {
      sceneButtonIsIn.setRoot(App.loadFxml("room"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void openSettings() {
    System.out.println("Settings button clicked");
    App.setUi(AppUi.SETTINGS_PAGE);
  }
}
