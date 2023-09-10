package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;
import static byow.Core.RandomUtils.*;
import static byow.Core.FileSaving.*;


/** WorldGenerator generates a random world consisting of rooms & hallways according to the spec. It
 * has eight instance variables & six global constants. The functionality of each method is
 * explained in greater depth below. Note that asset refers to both rooms & hallways.
 *
 * @author om
 * */

public class WorldGenerator implements Serializable {
    /** Global instance variables. */
    private int WIDTH;
    private int HEIGHT;
    private TETile[][] worldFrame;
    private RoomTracker rooms;
    private Random rand;
    private String seed;
    private Position userLoc;
    private Deque<String> keyPress;
    private int orbsCollected;
    private long loggedTime;
    private long totalTime;
    private boolean isLoadedFromSave;

    /** World Assets constants. Determines how the World looks. */
    public static final int ORIGIN = 0;  // bottom left
    public static final int ROOMMIN = 7;
    public static final int ROOMMAX = 12;
    public static final int LINKBOUND = 1;
    public static final int HALLWAYWIDTHBOUND = 3;
    public static final int OFFSET = 1;
    public static final int TOTALORBS = 16;
    public static final long DEFAULTTIME = 80;


    /** Constructor for this class, which sets multiple global constants & fills worldFrame with
     * NOTHING tiles. */
    public WorldGenerator(TETile[][] frame, int width, int height, long s, long time, boolean isSave) {
        worldFrame = frame;
        WIDTH = width;
        HEIGHT = height;
        rooms = new RoomTracker();
        seed = Long.toString(s);
        rand = new Random(s);
        keyPress = new ArrayDeque<String>();
        keyPress.addLast(".");
        orbsCollected = 0;
        totalTime = time;
        isLoadedFromSave = isSave;

        fillWithNothingTiles();
        drawWorld();
    }


    /** Fills the world with NOTHING Tiles. */
    private void fillWithNothingTiles() {
        for (int i = ORIGIN; i < WIDTH; i++) {
            for (int j = ORIGIN; j < HEIGHT; j++) {
                worldFrame[i][j] = Tileset.NOTHING;
            }
        }
    }


    /** Draws a world filled with rooms & hallways. */
    public void drawWorld() {
        drawSectors();
        connectRooms();
    }


    /** Places orbs, the user on a random FLOOR tile, fixes edge tile placement cases & returns
     * worldFrame. */
    public TETile[][] getWorld() {
        fixEdgeCases();
        placeGate();
        placeOrbs();
        placeUser();
        return worldFrame;
    }

    /** Returns the seed. */
    public long getWorldSeed() {
        return Long.parseLong(seed);
    }


    /** Returns the description of a given tile with coordinates x & y. */
    public String getTileDescription(double xValue, double yValue) {
        int x = formatValue(xValue, WIDTH);
        int y = formatValue(yValue, HEIGHT);

        TETile tile = worldFrame[x][y];
        return tile.description();
    }

    /** Returns orbsCollected. */
    public String getOrbsCollected() {
        // for formatting purposes.
        if (orbsCollected < 10) {
            return "0" + orbsCollected;
        } else {
            return Integer.toString(orbsCollected);
        }
    }


    /** Formats the given value so that it is always in the range of 0 <= value < limit. */
    public int formatValue(double value, int limit) {
        if (value >= limit) {
            value = limit - 1;
        } else if (value < ORIGIN) {
            value = ORIGIN;
        }

        int finalValue = (int) value;

        return finalValue;
    }


    /** Splits the world into five sectors. Each sector has a maximum of two rooms. */
    public void drawSectors() {
        int sectorWidth = WIDTH / 5;
        int x = ORIGIN;
        int y = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);

        for (int s = 1; s < 6; s++) {
            drawRoom(x, y);

            y = randomY(y);

            if (drawSecondRoom()) {
                drawRoom(x + ROOMMIN, y);
            }

            x = sectorWidth * s;
            y = randomY(y);
        }
    }


    /** Using pseudorandomness to determine whether a second room in the sector should be drawn. */
    public boolean drawSecondRoom() {
        int outcome = uniform(rand, 0, 1);

        return (outcome == 0);
    }


    /** Returns a random y-value that is between the bounds of ORIGIN & HEIGHT. The new y-value is
     * also has an absolute difference greater than ROOMMIN so that rooms are spaced apart
     * sufficiently. */
    public int randomY(int prevY) {
        int newY = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);
        int absoluteDifference = Math.abs(newY - prevY);

        while (absoluteDifference <= ROOMMIN) {
            newY = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);
            absoluteDifference = Math.abs(newY - prevY);
        }
        return newY;
    }


    /** Draws a room (top-down) with a pseudorandom width & length. Slighty complex, but the
     * complexity arises from checking whether a tile can be drawn before it is drawn. */
    public void drawRoom(int x, int y) {
        int width = uniform(rand, ROOMMIN, ROOMMAX);
        int length = uniform(rand, ROOMMIN, ROOMMAX);
        Position roomLoc = new Position(x, y, width, length);
        rooms.addRoom(roomLoc);

        for (int dy = 0; dy < length; dy++) {
            if ((dy == 0) || (dy == (length - 1))) {
                for (int dx = 0; dx < width; dx++) {

                    if (((roomLoc.getxPos() + dx) >= (WIDTH)) || (roomLoc.getyPos()) >= (HEIGHT)) {
                        continue;
                    }

                    worldFrame[roomLoc.getxPos() + dx][roomLoc.getyPos()] = Tileset.WALL;
                }
                roomLoc.changeyPos(-1);
                continue;
            }

            worldFrame[roomLoc.getxPos()][roomLoc.getyPos()] = Tileset.WALL;
            for (int dx = 1; dx < width - 1; dx++) {
                if (((roomLoc.getxPos() + dx) >= (WIDTH - OFFSET - OFFSET)) || (roomLoc.getyPos()) >= (HEIGHT)) {
                    worldFrame[WIDTH - OFFSET - OFFSET][roomLoc.getyPos()] = Tileset.WALL;
                    continue;
                }


                worldFrame[roomLoc.getxPos() + dx][roomLoc.getyPos()] = Tileset.FLOOR;
            }
            if (((roomLoc.getxPos() + width - OFFSET) < (WIDTH - OFFSET)) && ((roomLoc.getyPos()) < (HEIGHT - OFFSET))) {
                worldFrame[roomLoc.getxPos() + width - OFFSET][roomLoc.getyPos()] = Tileset.WALL;
            }

            roomLoc.changeyPos(-1);
        }
    }


    /** Connects all the rooms. */
    public void connectRooms() {
        ArrayList<Position> roomList = rooms.getRoomList();
        int size = rooms.getRoomSize();

        for (int i = 0; i < (size - 1); i++) {
            Position roomA = roomList.get(i);
            Position roomB = roomList.get(i + 1);

            drawLink(roomA, roomB);
        }
    }


    /** Draws a 'L' shaped hallway between 2 rooms. */
    public void drawLink(Position roomA, Position roomB) {
        int aY = roomA.getMidy();
        int bY = roomB.getMidy();
        int difference = aY - bY;

        if (difference < -LINKBOUND) {
            drawUpLink(roomA, roomB);
        } else if (difference > LINKBOUND) {
            drawDownLink(roomA, roomB);
        } else {
            drawStraightLink(roomA, roomB, difference);
        }
    }


    /** Draws a 'L' shaped hallway between 2 rooms where roomA is higher than roomB (in terms of
     * y-coordinates). */
    public void drawUpLink(Position roomA, Position roomB) {
        int aX = roomA.getMidx() + roomA.getHalfWidth() - OFFSET;
        int aY = roomA.getMidy() + OFFSET;
        int bX = roomB.getMidx() - OFFSET;
        // +2 so that the link ends at least inside the room.
        int bY = roomB.getMidy() - roomB.getHalfLength() + OFFSET + 1;
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawL(aX, aY, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawL(aX, aY - i, bX + i, bY, Tileset.FLOOR);
        }
        drawL(aX, aY - i, bX + i, bY, Tileset.WALL);
    }



    /** Draws a 'L' shaped hallway between 2 rooms where roomA is lower than roomB (in terms of
     * y-coordinates). */
    public void drawDownLink(Position roomA, Position roomB) {
        int aX = roomA.getMidx() + roomA.getHalfWidth();
        int aY = roomA.getMidy() + OFFSET;
        int bX = roomB.getMidx() + OFFSET;
        int bY = roomB.getMidy() + roomB.getHalfLength();
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawL(aX, aY, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawL(aX - i, aY - i, bX - i, bY - i, Tileset.FLOOR);
        }
        drawL(aX, aY - i, bX - i, bY, Tileset.WALL);
    }


    /** Draws a horizontal 'I' shaped hallway between 2 rooms where roomA is equal to roomB
     * (in terms of y-coordinates). */
    public void drawStraightLink(Position roomA, Position roomB, int difference) {
        int aX = roomA.getMidx() + roomA.getHalfWidth();
        int bX = roomB.getMidx() + OFFSET;
        // + difference to position the hallway properly// between both rooms.
        int bY = roomB.getMidy() + OFFSET + difference;
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawI(aX, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawI(aX - i, bX - i, bY - i, Tileset.FLOOR);
        }
        drawI(aX, bX - i, bY - i, Tileset.WALL);

    }


    /** Draws a down 'L' shape of tile tileType. */
    public void drawL(int aX, int aY, int bX, int bY, TETile tileType) {
        for (int x = aX; x < bX; x++) {
            worldFrame[x][aY] = tileType;
        }

        if (aY >= bY) {
            for (int y = bY; y < aY + 1; y++) {
                worldFrame[bX][y] = tileType;
            }
        } else {
            for (int y = aY; y < bY; y++) {
                worldFrame[bX][y] = tileType;
            }
        }
    }


    /** Draws a horizontal 'I' shape of tile tileType. */
    public void drawI(int aX, int bX, int bY, TETile tileType) {
        for (int x = aX; x < bX; x++) {
            worldFrame[x][bY] = tileType;
        }
    }


    /** Commands the avatar based on the user's input. */
    public boolean command(String userInput) {
        int userX = userLoc.getxPos();
        int userY = userLoc.getyPos();

        boolean isColonTyped = previousWasColon();
        userInput = userInput.toLowerCase();
        keyPress.addLast(userInput);

        switch (userInput) {
            case "w":
                moveTo(userX, userY + 1);
                return false;

            case "a":
                moveTo(userX - 1, userY);
                return false;

            case "s":
                moveTo(userX, userY  - 1);
                return false;

            case "d":
                moveTo(userX + 1, userY);
                return false;

            case "q":
                return isColonTyped;

            default:
                return false;
        }
    }


    /** Moves the avatar to the given tile coordinates. */
    public void moveTo(int newX, int newY) {
        TETile tileToMoveTo = worldFrame[newX][newY];

        if (canMovetoTile(tileToMoveTo)) {
            worldFrame[userLoc.getxPos()][userLoc.getyPos()] = Tileset.FLOOR;
            worldFrame[newX][newY] = Tileset.AVATAR;
            userLoc.replacePos(newX, newY);
        }
    }


    /** Returns true if the tile that the user is moving to is a FLOOR or an ORB tile. Increments
     * orbsCollected by one if the tile is an ORB tile. */
    public boolean canMovetoTile(TETile tile) {
        if (tile.equals(Tileset.ORB)) {
            orbsCollected++;
            return true;
        } else if (tile.equals(Tileset.GATE) && (orbsCollected == TOTALORBS)) {
            Engine.showWinScreen(seed, getTimeTaken());
        }
        return tile.equals(Tileset.FLOOR);
    }


    /** Places sixteen Orbs around the World in random locations. */
    public void placeOrbs() {
        int orbsLeft = TOTALORBS;

        while (orbsLeft > 0) {
            int x = uniform(rand, ORIGIN, WIDTH);
            int y = uniform(rand, ORIGIN, HEIGHT);

            TETile tile = worldFrame[x][y];

            if (tile.equals(Tileset.FLOOR)) {
                worldFrame[x][y] = Tileset.ORB;
                orbsLeft--;
            }
        }
    }


    /** Places the user on a random FLOOR tile in the World & updates userLoc. */
    public void placeUser() {
        boolean spawn = true;

        while (spawn) {
            int x = uniform(rand, ORIGIN, WIDTH);
            int y = uniform(rand, ORIGIN, HEIGHT);

            TETile tile = worldFrame[x][y];

            if (tile.equals(Tileset.FLOOR)) {
                worldFrame[x][y] = Tileset.AVATAR;

                userLoc = new Position(x, y, 0, 0);

                spawn = false;
            }
        }
    }


    /** Places the GATE tile in the centre of a Room in the World. */
    public void placeGate() {
        int randomIndex = uniform(rand, 0, rooms.getRoomSize());
        ArrayList<Position> roomList = rooms.getRoomList();
        int randomRoomX = (roomList.get(randomIndex)).getMidx();
        int randomRoomY = (roomList.get(randomIndex)).getMidy();

        worldFrame[randomRoomX][randomRoomY] = Tileset.GATE;
    }


    /** Fixes edge cases of FLOOR & WALL tiles so that all rooms are accessible. */
    public void fixEdgeCases() {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                TETile tile = worldFrame[x][y];
                TETile tileBehind = worldFrame[x - 1][y];
                TETile tileInFront = worldFrame[x + 1][y];

                // this checks if the group of tiles is FLOOR > WALL > FLOOR horizontally.
                if ((tile.equals(Tileset.WALL)) && (tileBehind.equals(Tileset.FLOOR)) &&
                    (tileInFront.equals(Tileset.FLOOR))) {
                   worldFrame[x][y] = Tileset.FLOOR;
                }
            }
        }

        for (int yVer = 1; yVer < HEIGHT - 1; yVer++) {
            for (int xVer = 0; xVer < WIDTH; xVer++) {
                TETile tile = worldFrame[xVer][yVer];
                TETile tileBehind = worldFrame[xVer][yVer - 1];
                TETile tileInFront = worldFrame[xVer][yVer + 1];

                // this checks if the group of tiles is FLOOR > WALL > FLOOR vertically.
                if ((tile.equals(Tileset.WALL)) && (tileBehind.equals(Tileset.FLOOR)) &&
                        (tileInFront.equals(Tileset.FLOOR))) {
                    worldFrame[xVer][yVer] = Tileset.FLOOR;
                }
            }
        }
    }


    /** Returns true if the previous character typed was a colon ':'. */
    public boolean previousWasColon() {
        String character = keyPress.getLast();

        return character.equals(":");
    }


    /** Saves the world state to world_save.txt in the CWD.*/
    public void saveState() throws IOException {
        File worldSave = new File(SAVES);
        String saveData = seed + keyPress + getCurrentTime();

        if (worldSave.exists()) {
            writeToFile(worldSave, saveData);
        } else {
            worldSave.createNewFile();
            writeToFile(worldSave, saveData);
        }
    }


    /** Logs the current time. */
    public void logCurrentTime()  {
        loggedTime = System.currentTimeMillis();
    }


    /** Returns the time left, dependent on totalTime. */
    public String getCurrentTime()  {
        long timeNow = System.currentTimeMillis();
        long timeRemaining = timeNow - loggedTime;
        long timeRemainingInSeconds = totalTime - Math.round(timeRemaining / 1000);

        if (timeRemainingInSeconds <= 0) {
            Engine.showLoseScreen(seed);
        }

        // for formatting purposes.
        if (timeRemainingInSeconds < 10) {
            return "0" + timeRemainingInSeconds + "s";
        } else {
            return timeRemainingInSeconds + "s";
        }
    }


    /** Returns the time taken for completing a World. */
    public String getTimeTaken()  {
        long timeNow = System.currentTimeMillis();
        long timeRemaining = timeNow - loggedTime;
        long timeRemainingInSeconds;

        if (isLoadedFromSave) {
            timeRemainingInSeconds = (DEFAULTTIME - totalTime) + Math.round(timeRemaining / 1000);
        } else {
            timeRemainingInSeconds = Math.round(timeRemaining / 1000);
        }

        // for formatting purposes.
        if (timeRemainingInSeconds < 10) {
            return "0" + timeRemainingInSeconds + "s";
        } else {
            return timeRemainingInSeconds + "s";
        }
    }
}
