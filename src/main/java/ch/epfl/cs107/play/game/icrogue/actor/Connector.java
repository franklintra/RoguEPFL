package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

import static ch.epfl.cs107.play.game.icrogue.constants.NO_KEY_ID;

public class Connector extends ICRogueActor implements Interactable, ICRogueInteractionHandler {
    public enum connectorState {
        OPEN, CLOSED, LOCKED, INVISIBLE
    }

    private int keyId = NO_KEY_ID;
    private final Orientation orientation;
    private String destinationArea;
    private connectorState state;
    private Sprite currentSprite;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area                      (Area): Owner area. Not null
     * @param orientation               (Orientation): Initial orientation of the entity. Not null
     * @param position                  (Coordinate): Initial position of the entity. Not null
     * @param destinationArea           (String): Name of the destination area. Not null
     */
    public Connector(Area area, Orientation orientation, DiscreteCoordinates position, String destinationArea) {
        super(area, orientation, position);
        this.orientation = orientation;
        this.destinationArea = destinationArea;
        this.state = connectorState.INVISIBLE;
        chooseSprite();
    }

    /**
     * This method is used to set the correct sprite for the connector depending on its state and orientation
     */
    public void chooseSprite() {
        if (state == connectorState.CLOSED) {
            currentSprite = new Sprite("icrogue/door_"+orientation.ordinal(), (orientation.ordinal()+1)%2+1, orientation.ordinal()%2+1, this);
        } else if (state == connectorState.LOCKED) {
            currentSprite = new Sprite("icrogue/lockedDoor_"+orientation.ordinal(), (orientation.ordinal()+1)%2+1, orientation.ordinal()%2+1,
                    this);
        } else if (state == connectorState.INVISIBLE) {
            currentSprite = new Sprite("icrogue/invisibleDoor_"+orientation.ordinal(), (orientation.ordinal()+1)%2+1, orientation.ordinal()%2+1, this);
        }
    }

    /**
     * @param state
     * this method is used to set the state of the connector and change its appearance accordingly
     */
    public void setState(connectorState state) {
        this.state = state;
        chooseSprite();
    }

    /**
     * @return connectorState : the state of the connector in question
     */
    public connectorState getState() {
        return state;
    }

    /**
     * @param keyId : the id of the key that can open the connector if it is locked
     */

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    /**
     * @return int : the id of the key that can open the connector if it is locked
     */
    public int getKeyId() {
        return keyId;
    }

    /**
     * @param destinationArea
     * this method is used to set the destination area of the connector (by name as it is how the game engine
     */
    public void setDestinationArea(String destinationArea) {
        this.destinationArea = destinationArea;
    }

    /**
     * @return String : the name of the destination area of the connector (by name as it is how the game engine
     */
    public String getDestinationArea() {
        return destinationArea;
    }


    @Override
    public void draw(Canvas canvas) {
        if (state == connectorState.OPEN) {
            return; // so that the connector is not drawn if it is open
        }
        currentSprite.draw(canvas);
    }

    @Override
    public boolean isCellInteractable() {
        return this.state==connectorState.OPEN;
    }

    @Override
    public boolean isViewInteractable() {
        return this.state!=connectorState.OPEN;
    }

    @Override
    public boolean takeCellSpace() {
        return state != connectorState.OPEN;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord, coord.jump(new Vector((getOrientation().ordinal()+1)%2, getOrientation().ordinal()%2)));
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }
}