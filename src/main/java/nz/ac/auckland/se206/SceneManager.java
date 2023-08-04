package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {

  public enum AppUi {
    START_PAGE,
    SETTINGS_PAGE,
    CHAT,
    LIGHT_ROOM,
    DARK_ROOM
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, Object> controllerMap = new HashMap<AppUi, Object>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  public static void addController(AppUi appUi, Object controller) {
    controllerMap.put(appUi, controller);
  }

  public static Object getController(AppUi appUi) {
    return controllerMap.get(appUi);
  }
}
