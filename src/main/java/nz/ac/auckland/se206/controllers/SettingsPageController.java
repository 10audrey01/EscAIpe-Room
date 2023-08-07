package nz.ac.auckland.se206.controllers;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsPageController {

  @FXML private Button settingsGoBackButton;
  @FXML private Slider musicVolumeSlider;

  private AppUi previousScene;

  @FXML
  public void onDragDetected() {
    musicVolumeSlider
        .valueProperty()
        .addListener(
            new InvalidationListener() {
              @Override
              public void invalidated(javafx.beans.Observable observable) {
                StartPageController.getMainMusicPlayer()
                    .setVolume(musicVolumeSlider.getValue() / 100);
              }
            });
  }

  public void setPreviousUi(AppUi previousScene) {
    this.previousScene = previousScene;
  }

  @FXML
  private void onGoBackFromSettings(ActionEvent event) {
    App.setUi(previousScene);
  }
}
