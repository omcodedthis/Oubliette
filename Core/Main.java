package byow.Core;

import java.io.IOException;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 2) {
                System.out.println("Can only have two arguments - the flag and input string");
                System.exit(0);
            } else if (args.length == 2 && args[0].equals("-s")) {
                Engine engine = new Engine();
                System.out.println(engine.toString());
            }
            else {
                Engine engine = new Engine();
                engine.interactWithKeyboard();
            }
        } catch (IOException e) {
            System.out.println("An IOException has occurred.");
        }
    }
}
