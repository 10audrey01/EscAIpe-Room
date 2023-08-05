package nz.ac.auckland.se206;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether a game has started. */
  public static boolean isGameStarted = false;

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  /** Indicates whether the vinyl has been found. */
  public static boolean isVinylFound = false;

  /** Indicates whether the vinyl has been played. */
  public static boolean isVinylPlaying = false;

  /** Indicates whether the user escaped. */
  public static boolean isEscaped = false;
}
