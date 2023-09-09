package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.StdDraw;
import java.awt.*;
import java.io.File;
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
    public static final int TOTALORBS = 16;
    public static final long DEFAULTTIME = 80;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        String input = showHomescreen();

        if (loadFromSave(input)) {
            String saveData = FileSaving.readData();
            loadGameWithSave(finalWorldFrame, saveData);
        } else {
            long seed = getSeedFromUserInput();
            loadGame(finalWorldFrame, seed);
        }
    }


    /* Helper methods for the Engine class. */


    /** Loads the game without a save (new save). */
    public void loadGame(TETile[][] finalWorldFrame, long seed) throws IOException {

        generateWorld(finalWorldFrame, seed, null, DEFAULTTIME, false);
    }


    /** Loads the game with a save. */
    public void loadGameWithSave(TETile[][] finalWorldFrame, String saveData) throws IOException {
        long seed = parseSeed(saveData);
        long timeLeft = parseTime(saveData);

        generateWorld(finalWorldFrame, seed, saveData, timeLeft, true);
    }


    /** Generates the world. */
    public void generateWorld(TETile[][] finalWorldFrame, long seed, String saveData, long time, boolean isSave) throws IOException {
        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed, time, isSave);
        finalWorldFrame = generator.getWorld();

        if (saveData != null) {
            loadSavedInputsToWorld(generator, saveData);
        }

        gameLoop(generator, finalWorldFrame);
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

        Font font1 = new Font("Times New Roman", Font.BOLD, 60);
        StdDraw.setFont(font1);
        StdDraw.text(centerX, centerY + HUDSPACING, "Oubliette");
        StdDraw.setTitle("Oubliette");

        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(centerX, centerY - 2, "New Game (N)");
        StdDraw.text(centerX, (centerY - HUDSPACING) - 2, "Load Game (L)");
        StdDraw.text(centerX, (centerY - (2 * HUDSPACING)) - 2, "Save & Quit (:Q)");
        Font font3 = new Font("Arial", Font.BOLD, 15);
        StdDraw.setFont(font3);

        StdDraw.show();

        String keyPressed = null;
        while (keyPressed == null) {
            keyPressed = getUserInput();
        }

        return keyPressed;
    }


    /** Draws the HUD for the game. The specific numbers are added so that the HUD elements line up
     * properly. */
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



        double tileDesPos = 1;
        double advPos = WIDTH / 2;
        double orbsCollectedPos = WIDTH - 2;
        double timePos = orbsCollectedPos - 11.5;

        // draws the HUD text
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(tileDesPos, textHeight, "Tile: " + tileDesc);
        StdDraw.text(advPos, textHeight, username + "'s Adventure");
        StdDraw.textRight(timePos, textHeight,  "Time Left: " + generator.getCurrentTime());
        StdDraw.textRight(orbsCollectedPos, textHeight, "Orbs Collected: "
            + generator.getOrbsCollected() + " / " + TOTALORBS);
        StdDraw.line(9.8, HEIGHT + HUDHEIGHT, 9.8, lineHeight);
        StdDraw.line(WIDTH - 20.5, HEIGHT + HUDHEIGHT, WIDTH - 20.5, lineHeight);
        StdDraw.line(WIDTH - 12.9, HEIGHT + HUDHEIGHT, WIDTH - 12.9, lineHeight);
        StdDraw.line(ORIGIN, lineHeight, WIDTH, lineHeight);
    }


    /** Creates the game loop. */
    public void gameLoop(WorldGenerator generator, TETile[][] finalWorldFrame) throws IOException {
        boolean gameOver = false;
        String username = getUsername();

        generator.logCurrentTime();
        while (!gameOver) {
            updateHUD(generator, username);
            ter.renderFrame(finalWorldFrame);
            gameOver = commandAvatar(generator);
        }
        long seed = generator.getWorldSeed();
        showSaveScreen(seed);
        generator.saveState();
    }


    /** Shows the end screen. */
    public static void showSaveScreen(long seed) {
        StdDraw.clear(new Color(0, 0, 0));
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "You have successfully saved your"
                + " progress.");
        StdDraw.text(centerX, centerY, "Seed: " + seed);

        StdDraw.show();
    }


    /** Shows the Win screen. */
    public static void showWinScreen(String seed, String time) {
        StdDraw.clear(new Color(0, 0, 0));
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "Congratulations! You found all the Orbs in " + time + ".");
        StdDraw.text(centerX, centerY, "Seed: " + seed);

        StdDraw.show();
        StdDraw.pause(10000);
        System.exit(0);
    }

    /** Shows the Lose screen. */
    public static void showLoseScreen(String seed) {
        StdDraw.clear(new Color(0, 0, 0));
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "You did not find all the Orbs in time!");
        StdDraw.text(centerX, centerY, "Seed: " + seed);

        StdDraw.show();
        StdDraw.pause(10000);
        System.exit(0);
    }
}
