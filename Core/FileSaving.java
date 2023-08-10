package byow.Core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


/** FileSaving contains the relevant methods for file writing & reading. It has one global constant,
 *  which is the name of the file where the world save data goes to. The functionality of each
 *  method is explained in greater depth below.
 *
 * @author om
 * */

class FileSaving {
    /** Name of the file where the saved world data is written to. */
    public static final String SAVES = "world_save.txt";

    /** Writes a String to a file. */
    protected static void writeToFile(File filePointer, String contents) throws IOException {
        FileWriter writer = new FileWriter(filePointer);
        writer.write(contents);
        writer.close();
    }

    /** Reads the data (String type) from world_save.txt. */
    protected static String readData() throws IOException {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(SAVES)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
