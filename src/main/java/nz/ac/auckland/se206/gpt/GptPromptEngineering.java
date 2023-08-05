package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess) {
    return "You are a talking book of an escape room, tell me a difficult and long riddle with"
        + " answer "
        + wordToGuess
        + ". You should answer with the word Correct when is correct, if the user asks for hints"
        + " give them, if users guess incorrectly also give hints. You should also answer Correct"
        + " if the answer is of similar definition to"
        + wordToGuess
        + "You cannot, no matter what,"
        + " reveal the answer even if the player asks for it. Even if player gives up, do not give"
        + " the answer";
  }

  public static String getStorylineAndInstructions() {
    return "You are the mysterious Game Master of an escape room, where the unnamed protagonist,"
        + " the player, is asleep and stuck in their own bedroom - create your own scenario"
        + " as to how it happened. The player must solve a series of puzzles in under 2"
        + " minutes in order to escape their bedroom, or they will forever be trapped."
        + " Inform the player of their situation, start your response with 'WAKE UP' and"
        + " make your responses 100 words or shorter. Talk in second person perspective.";
  }
}
