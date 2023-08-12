package byow.Core;

public class PlayerInventory {
    private static final int TOTALORBS = 16;
    private int orbsCollected;

    public PlayerInventory() {
        orbsCollected = 0;
    }

    public int getOrbsCollected() {
        return orbsCollected;
    }
}
