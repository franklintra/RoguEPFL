package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.game.actor.GraphicsEntity;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.ShapeGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Circle;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public abstract class ICRogueRoom extends Area implements Logic{
    private final String behaviorName; // The name of the behavior map of the room
    private final List<Connector> connectors = new ArrayList<>(); // The list of connectors in the room
    private boolean hasBeenVisited = false; // Whether the player has visited the room or not
    //Todo: THIS SHOULD NOT BE DONE THIS WAY BEWARE PEASANTS
    private final DiscreteCoordinates roomCoordinates; // The coordinates of the room in the level

    private ImageGraphics advancementBar = new ImageGraphics("menuItem/advancementBar.png", 1, 1);

    private boolean hasWon; // THESE ARE USED FOR ANIMATION
    private boolean finished = false; // THESE ARE USED FOR ANIMATION

    /**
     * @param connectorsCoordinates The list of all the coordinates of the connectors in the room
     * @param connectorsOrientations The list of all the orientations of the connectors in the room with the same index as the coordinates
     * @param behaviorName The name of the behavior map of the room
     * @param roomCoordinates Self-explanatory
     * This constructor creates a room with the given connectors. Most of the time this room will be a non abstract Level0Room for the present game.
     */
    public ICRogueRoom(List<DiscreteCoordinates> connectorsCoordinates, List<Orientation> connectorsOrientations, String behaviorName, DiscreteCoordinates roomCoordinates) {
        super();
        this.roomCoordinates = roomCoordinates;
        this.behaviorName = behaviorName;
        for (int i=0; i<connectorsCoordinates.size(); i++) {
            connectors.add(new Connector(this, oppositeOrientation(connectorsOrientations.get(i)), connectorsCoordinates.get(i), this.getTitle()));
        }
    }

    /**
     * @return (DiscreteCoordinates) the coordinates of the room in the level
     */
    public DiscreteCoordinates getRoomCoordinates() {
        return roomCoordinates;
    }

    /**
     * @param orientation The orientation to be inverted
     * @return (Orientation) the opposite orientation of the given orientation
     */
    private Orientation oppositeOrientation(Orientation orientation) {
        return switch (orientation) {
            case UP -> Orientation.DOWN;
            case DOWN -> Orientation.UP;
            case LEFT -> Orientation.RIGHT;
            case RIGHT -> Orientation.LEFT;
        };
    }
    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    @Override
    public float getCameraScaleFactor() {
        return ICRogue.CAMERA_SCALE_FACTOR;
    }


    /**
     * @return (DiscreteCoordinates) the coordinates of the player's spawn point in the room
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            ICRogueBehavior behavior = new ICRogueBehavior(window, behaviorName);
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    /**
     * @return (List<Connector>) a copy of the list of connectors in the room
     */
    public List<Connector> getConnectors() {
        return List.copyOf(connectors);
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (finished) {
            if (hasWon) {
                animateVictory();
            }
            else {
                animateDeath();
            }
        }
        else {
            if (isOn()) {
                for (Connector connector : connectors) {
                    if (connector.getState() == Connector.connectorState.CLOSED) {
                        connector.setState(Connector.connectorState.OPEN);
                    }
                }
            }
        }
        //Warning : this is for debugging and testing purposes only
        /*if (getKeyboard().get(Keyboard.O).isPressed()) {
            for (Connector connector : connectors) {
                connector.setState(Connector.connectorState.OPEN);
            }
        }
        else if (getKeyboard().get(Keyboard.L).isPressed()){
            connectors.get(0).setState(Connector.connectorState.LOCKED);
            connectors.get(0).setKeyId(1);
        } else if (getKeyboard().get(Keyboard.T).isPressed()){
            for (Connector connector : connectors) {
                if (connector.getState() == Connector.connectorState.LOCKED) {
                    continue;
                }
                if (connector.getState() == Connector.connectorState.OPEN) {
                    connector.setState(Connector.connectorState.CLOSED);
                } else if (connector.getState() == Connector.connectorState.CLOSED) {
                    connector.setState(Connector.connectorState.OPEN);
                }
            }
        }*/
    }

    /**
     * This method is called when the player enters the room. It is used to know whether a room has been completed or not as a non visited room cannot be on. (method isOn()).
     */
    public void visitRoom() {
        hasBeenVisited = true;
    }

    /**
     * @return (boolean) true if the player has visited the room, false otherwise
     */
    public boolean hasBeenVisited() {
        return hasBeenVisited;
    }

    /**
     * @return (boolean) true if the player has completed the room, false otherwise
     * @see Logic
     */
    public abstract boolean isOn();

    /**
     * @return (boolean) true if the room isn't completed, false otherwise
     * @see Logic
     */
    public abstract boolean isOff();

    /**
     * @return (float) the advancement of the room
     * @see Logic
     */
    public abstract float getIntensity();



    //All the code below is for animation purposes only once the player has completed the level or died!
    private float zoomFactor = ICRogue.CAMERA_SCALE_FACTOR; // This is used to zoom in the
    private float counterAnimation = 0; // This is used to count the time of the animation
    private int frameCounter = 0; // This is used to count the number of frames of the video have been played

    /**
     * This method is called when the player has completed the whole game. It will animate the victory or the death of the player according to the boolean hasWon.
     * @param hasWon (boolean) true if the player has won, false if he is dead
     */
    public void finished(boolean hasWon) {
        this.hasWon = hasWon;
        finished = true;
    }

    /**
     * @return (Color) a random color to be used for the animation (blinking lights for example) Warning epilepsy
     */
    private Color randomColor(){
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }
    private GraphicsEntity animated = new GraphicsEntity(new Vector(5, 5), new ShapeGraphics(new Circle(0f), randomColor(), randomColor(), 0.5f)); // This is the entity that will be animated (circle for victory)
    private final GraphicsEntity trophy = new GraphicsEntity(new Vector(5, 5), new ImageGraphics("animation/cup.png", 1, 1)); // This is the trophy that will be animated in the victory animation

    /**
     * This method is called when the player dies. It will animate the death of the player.
     */
    private void animateDeath(){
        unregisterActor(animated);
        if (frameCounter < 279) { // 279 is the number of frames of the GTA V death animation
            animated = new GraphicsEntity(new Vector(0f, 2.5f), new ImageGraphics("animation/wasted/frame" + String.format("%04d", ++frameCounter) + ".png", 10, 5f));
        }
        else {
            System.exit("Game Over".hashCode());
        }
        registerActor(animated);
    }

    /**
     * This method is called when the player has completed the whole game. (game.isOn()) It will animate the victory of the player.
     */
    private void animateVictory(){
        registerActor(trophy);
        if (counterAnimation<5f) {
            counterAnimation += 0.05f;
            unregisterActor(animated);
            animated = new GraphicsEntity(new Vector(5, 5), new ShapeGraphics(new Circle(counterAnimation+4), Color.BLACK, randomColor(), 1f));
            registerActor(animated);
        }
        else {
            animated = new GraphicsEntity(new Vector(4.3f, 4.7f), new TextGraphics("You Win", 0.5f, randomColor()));
            registerActor(animated);
            if (zoomFactor > 0.1f) {
                zoomFactor -= 0.15f;
            }
            else {
                System.exit("You Win".hashCode());
            }
        }
    }


    /**
     *
     * @param advancementBar: the current Image of the advancementBar according to the intensity of the level
     */
    public void setAdvancementBar(ImageGraphics advancementBar) {
        this.advancementBar = advancementBar;
    }

    /**
     *
     * @return (ImageGraphics) the current Image of the advancementBar according to the intensity of the level
     */
    protected ImageGraphics getAdvancementBar() {
        return advancementBar;
    }
}
