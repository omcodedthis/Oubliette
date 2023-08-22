package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.StdDraw;
import java.awt.*;
import java.io.IOException;
import static byow.Core.HelperMethods.*;


/** Engine calls the relevant methods to create, interact & show the world to the user. The
 * functionality of each method is explained in greater depth below. Note that asset refers to both
 * rooms & hallways.
 *
 * @author om
 * */

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int ORIGIN = 0;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int HUDHEIGHT = 3;
    public static final int HUDSPACING = 3;
    public static final int WINDOWWIDTH = WIDTH - 1;
    public static final int WINDOWHEIGHT = HEIGHT + HUDHEIGHT;



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        boolean isKeyboard = true;

        String input = showHomescreen();

        if (loadFromSave(input)) {
            String saveData = FileSaving.readData();
            loadGameWithSave(finalWorldFrame, input, saveData, isKeyboard);
        } else {
            long seed = getSeedFromUserInput();
            loadGame(finalWorldFrame, seed, input, isKeyboard);
        }
    }



    /* Helper methods for the Engine class. */


    /** Loads the game with a save. */
    public TETile[][] loadGameWithSave(TETile[][] finalWorldFrame, String input, String saveData,
            boolean isKeyboard) throws IOException {
        long seed = parseSeed(saveData);
        finalWorldFrame = generateWorld(finalWorldFrame, seed, input, saveData, isKeyboard);
        return finalWorldFrame;
    }


    /** Loads the game without a save (new save). */
    public TETile[][] loadGame(TETile[][] finalWorldFrame, long seed, String input,
            boolean isKeyboard) throws IOException {
        if (!isKeyboard) {
            seed = parseSeed(input);
        }

        finalWorldFrame = generateWorld(finalWorldFrame, seed, input, null, isKeyboard);
        return finalWorldFrame;
    }


    /** Generates the world. */
    public TETile[][] generateWorld(TETile[][] finalWorldFrame, long seed, String input,
            String saveData, boolean isKeyboard) throws IOException {
        input =  input.toLowerCase();

        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
        finalWorldFrame = generator.getWorld();
        String validInput = parseValidInput(input);

        if (saveData != null) {
            loadSavedInputsToWorld(generator, saveData);
        }

        // This is for interactWithInputString(), interactWithKeyboard() takes in input from
        // gameLoop().
        for (int i = 0; i < validInput.length(); i++) {
            String ch = Character.toString(validInput.charAt(i));
            generator.command(ch);
        }

        if (input.contains(":q")) {
            generator.saveState();
        }

        generator.CurrentTime(); // EDIT THIS (FOR DEV ONLY)
        if (isKeyboard) {
            gameLoop(generator, finalWorldFrame);
            return null;
        } else {
            return finalWorldFrame;
        }

    }


    /** Gets the seed from the user's input. */
    public static long getSeedFromUserInput() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        String userSeed = "";
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            StdDraw.text(centerX, centerY, "Seed (type 'S' to indicate the end): " + userSeed);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char userInput = StdDraw.nextKeyTyped();

                if (stopScanning(userSeed, userInput)) {
                    break;
                } else if (Character.isDigit(userInput)) {
                    userSeed += userInput;
                }
            }
        }
        long seed = Long.parseLong(userSeed);

        return seed;
    }


    /** Gets the seed from the user's input. */
    public static String getUsername() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        String userString = "";
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            StdDraw.text(centerX, centerY, "Your Name (type '.' to indicate the end):  "
                + userString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char userInput = StdDraw.nextKeyTyped();

                if (userInput == '.') {
                    break;
                }
                userString += userInput;
            }
        }

        return userString;
    }


    /** Shows the homescreen. */
    public String showHomescreen() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "Catch-16 DEV BUILD");
        StdDraw.text(centerX, centerY, "New Game (N)");
        StdDraw.text(centerX, centerY - HUDSPACING, "Load Game (L)");
        StdDraw.text(centerX, centerY - (2 * HUDSPACING), "Quit (Q)");

        StdDraw.show();

        String keyPressed = null;
        while (keyPressed == null) {
            keyPressed = getUserInput();
        }

        return keyPressed;
    }


    /** Draws the HUD for the game. */
    public static void updateHUD(WorldGenerator generator, String username) {
        int textHeight = HEIGHT + 2;
        int lineHeight = HEIGHT + 1;

        // StdDraw does not have the ability to clear a specified region of the canvas, hence, a
        // black rectangle is drawn over the previous HUD.
        StdDraw.filledRectangle(WIDTH / 2, textHeight, WIDTH / 2,
            HUDHEIGHT / 2);

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        String tileDesc = generator.getTileDescription(mouseX, mouseY);


        // draws the HUD text
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, textHeight, "Tile: " + tileDesc);
        StdDraw.text(WIDTH / 2, textHeight, username + "'s Adventure");

        StdDraw.text((WIDTH / 2) + 10, textHeight,  generator.getCurrentTime());

        StdDraw.textRight(WIDTH - 2, textHeight, "Orbs Collected: "
            + generator.getOrbsCollected());
        StdDraw.line(ORIGIN, lineHeight, WIDTH, lineHeight);
    }


    /** Creates the game loop. */
    public void gameLoop(WorldGenerator generator, TETile[][] finalWorldFrame) throws IOException {
        boolean gameOver = false;
        String username = getUsername();

        while (!gameOver) {
            updateHUD(generator, username);
            ter.renderFrame(finalWorldFrame);
            gameOver = commandAvatar(generator);
        }
        long seed = generator.getWorldSeed();
        showEndScreen(seed);
        generator.saveState();
    }


    /** Shows the end screen. */
    public static void showEndScreen(long seed) {
        StdDraw.clear(new Color(0, 0, 0));
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "You have successfully saved your"
            + " progress.");
        StdDraw.text(centerX, centerY, "Seed: " + seed);

        StdDraw.show();
    }
}
