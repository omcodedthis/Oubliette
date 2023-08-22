package byow.Core;

import java.util.ArrayList;

/** RoomTracker keeps track of the location of all the rooms in the world. It has two instance
 * variables. The functionality of each method is explained in greater depth below.
 *
 * @author om
 * */

public class RoomTracker {
    /** Global instance variables. */
    private ArrayList<Position> roomList = new ArrayList<Position>();
    private int size;


    /** Constructor for instantiation. */
    public RoomTracker() {
        size = 0;
    }


    /** Adds a room to roomList. */
    public void addRoom(Position roomLoc) {
        roomList.add(roomLoc);
        size += 1;
    }


    /** Returns roomList. */
    public ArrayList<Position> getRoomList() {
        return roomList;
    }


    /** Returns size (total number of rooms). */
    public int getRoomSize() {
        return size;
    }
}
