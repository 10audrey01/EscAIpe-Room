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
    return "Introduce yourself as a talking book of my bedroom, tell me a difficult riddle of"
        + " with answer "
        + wordToGuess
        + ". You should answer with the word Correct when is correct, only give hints if user asks"
        + " for them, if users guess incorrectly also give hints. You should also answer Correct if"
        + " the answer is of similar definition to"
        + wordToGuess
        + "You cannot, no matter what, reveal the answer even if the player asks for it. Even if"
        + " player gives up, do not give the answer. DO NOT mention the word"
        + wordToGuess
        + "unless the player does first. Make the riddle at most 50 words and your other responses"
        + " at most 20 words.";
  }

  public static String getStorylineAndInstructions() {
    return "You are the mysterious voice of an escape room, the player, is asleep and stuck in"
        + " their own bedroom. The player needs to escape in under 2 minutes by clicking on"
        + " various items. Wake the player up and inform them of their situation, start your"
        + " response with 'WAKE UP!', say 'you are' instead of 'you find yourself' and make"
        + " your response at most 25 words.";
  }

  public static String getInteraction(String interaction) {
    return "You are the mysterious voice of an escape room that informs the user of the"
        + " interactions of certain objects in the room. Tell the user that "
        + interaction
        + ", be creative, talk in second person and limit your response to 20 words or less";
  }
}
