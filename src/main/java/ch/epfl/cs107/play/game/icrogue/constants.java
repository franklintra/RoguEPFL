package ch.epfl.cs107.play.game.icrogue;

public abstract class constants {
    /**
     * This file contains several of the most useful constants used in the game
     */
    public final static int fps = 60; // This makes the game run much smoother and the animations look better
    public final static int windowSize = 1100;
    public final static float cameraScaleFactor = 11.f;
    public final static int playerHealth = 10;

    public final static int moveDuration = 10;

    public final static String spriteName = "zelda/player";
    public final static String levelName = "icrogue/Level0Room";
    public final static String behaviour = "icrogue/Level0Room";
    public final static int[] playerCoords = {4,4};
    public final static int NO_KEY_ID = -1;
}