package nz.ac.auckland.se206;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  private static MediaPlayer musicPlayer;

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  public static void setUi(AppUi newUi) {
    scene.setRoot(SceneManager.getUiRoot(newUi));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   * @throws URISyntaxException
   */
  @Override
  public void start(final Stage stage) throws IOException, URISyntaxException {
    SceneManager.addUi(AppUi.START_PAGE, loadFxml("startPage"));

    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/settingsPage.fxml"));
    SceneManager.addUi(AppUi.SETTINGS_PAGE, loader.load());
    SceneManager.addController(AppUi.SETTINGS_PAGE, loader.getController());

    Media mainMusic = new Media(App.class.getResource("/sounds/mainMusic.mp3").toURI().toString());
    musicPlayer = new MediaPlayer(mainMusic);
    musicPlayer.setOnEndOfMedia(
        new Runnable() {
          public void run() {
            musicPlayer.seek(Duration.ZERO);
          }
        });
    musicPlayer.play();

    scene = new Scene(SceneManager.getUiRoot(AppUi.START_PAGE), 600, 470);
    stage.setScene(scene);
    stage.show();
  }

  public static MediaPlayer getMusicPlayer() {
    return musicPlayer;
  }
}
