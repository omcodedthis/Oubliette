package byow.Core;

import byow.TileEngine.StdDraw;



/** HelperMethods contains all the relevant helper methods for the Engine class. It has one global
 * constant. The functionality of each method is explained in greater depth below.
 *
 * @author om
 * */

public class HelperMethods {
    /** Valid inputs for movement in the game. */
    public static final String VALIDMOVEINPUTS = "wasd";


    /* HELPER METHOD RELATED TO IN-GAME COMMANDS. */


    /** Gets the user's input & updates the world accordingly. */
    public static boolean commandAvatar(WorldGenerator generator) {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());
            return generator.command(userInput);
        } else {
            return false;
        }
    }


    /* HELPER METHODS RELATING TO PARSING DATA / GETTING INPUT. */


    /** Gets the user's input for menu based actions. */
    public static String getUserInput() {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());
            userInput = userInput.toLowerCase();

            switch (userInput) {
                case "n":
                    return "n";

                case "l":
                    return "l";

                case "q":
                    return "q";

                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    /** Parses the seed from the command line input. */
    public static long parseSeed(String input) {
        input = input.toLowerCase();
        String stringSeed = "";

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if ((ch == 's') || (ch == '[')) {
                break;
            }

            boolean isDigit = Character.isDigit(ch);
            if (isDigit) {
                stringSeed += ch;
            }
        }

        long seed = Long.parseLong(stringSeed);
        return seed;
    }


    /** Parses valid input (N/W/A/S/D) from the command line input. */
    public static String parseValidInput(String input) {
        input = input.toLowerCase();
        String stringInput = "";

        int startingIndex = 1;

        for (int i = startingIndex; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (Character.isAlphabetic(ch)) {
                stringInput += ch;
            }
        }
        return stringInput;
    }


    /** Returns true if the criteria to stop taking input for a seed has been met (final char is 's'
     *  & length of seed typed is greater than zero. */
    public static boolean stopScanning(String userSeed, char userInput) {
        int length = userSeed.length();
        return (((userInput == 's') || (userInput == 'S')) && (length > 0));
    }


    /* HELPER METHODS RELATED TO LOADING WORLDS. */


    /** Loads the saved inputs from saveData to the world. */
    public static void loadSavedInputsToWorld(WorldGenerator generator, String saveData) {
        for (int i = 0; i < saveData.length(); i++) {
            String ch = Character.toString(saveData.charAt(i));

            if (VALIDMOVEINPUTS.contains(ch)) {
                generator.command(ch);
            }
        }
    }


    /** Returns true if the first letter of the input string is 'l'. */
    public static boolean loadFromSave(String input) {
        input =  input.toLowerCase();
        char firstChar = input.charAt(0);

        return  (firstChar == 'l');
    }
}
