package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;

public class LightRoomController {

  @FXML private Rectangle window;

  @FXML
  public void clickWindow() throws IOException {
    System.out.println("Window clicked");
    App.setRoot("darkRoom");
  }
}
