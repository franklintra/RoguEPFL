package ch.epfl.cs107.play.game.icrogue.actor;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Boss;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Fireball;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.game.icrogue.constants;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.game.icrogue.constants.NO_KEY_ID;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public class ICRoguePlayer extends ICRogueActor implements Interactor, ICRogueInteractionHandler {
    private float hp; // Health points of the player
    private final TextGraphics message; // The hp text next to the player
    private final Sprite[][] sprites; // All the possible sprites for the player movements etc
    private Sprite currentSprite; // The sprite that is currently displayed
    private final ICRoguePlayerInteractionHandler handler; // Handler for the player interactions
    private boolean hasStaff; // This is a flag to know if the player has the staff or not.
    private int numberOfCherries = 0; // The number of cherries the player has collected. This will be used to determine how fast the player should be going.
    private final ArrayList<Integer> keys = new ArrayList<>(){{add(NO_KEY_ID);}};
    private boolean isSwitchingArea; // This is a boolean that is used to switch area correctly (see update method)
    private String destinationArea; // This is the destination area of the connector that the player is interacting with currently
    private DiscreteCoordinates positionInDestinationArea; // This is the position in the destination area of the connector that the player is interacting with currently (The player will be teleported to this position in the new level when he switches area)
    private final static int MOVE_DURATION = constants.moveDuration; // This is the duration of the move animation. It is used to make the player move smoothly (see update method). If the player has eaten cherries, the duration is divided by the nu


    private int walkingAnimation = 0; // This is a counter to animate the walking movements of the player
    private int skipFrame = 0; // This is used to make the player blink when he is damaged
    private int frameUpdate; // This is used to not update the player every frame (see update method and chooseSprite method)
    private boolean dead = false;

    /**
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param coordinates (DiscreteCoordinates): Initial position of the entity. Not null
     * @param spriteName (String): Name of the sprite
     */
    public ICRoguePlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(area, orientation, coordinates);
        this.handler = new ICRoguePlayerInteractionHandler();
        this.hp = constants.playerHealth;
        message = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));
        //This will load all the sprites for the player in a two-dimensional array of sprites
        sprites = Sprite.extractSprites(spriteName, 4, 1f, 1f, this, 16, 32, new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        currentSprite = sprites[0][0];
        resetMotion();
    }

    /**
     * Center the camera on the player but this isn't used in the game. I left it here in case we want to use it later for a menu or something.
     */
    @SuppressWarnings("unused")
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    @Override
    public void update(float deltaTime) {
        message.setText(Integer.toString((int)hp));
        Keyboard keyboard = getOwnerArea().getKeyboard();
        //This is used to make the player move according to the keyboard input
        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT).isDown() || keyboard.get(Keyboard.Q).isDown());
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP).isDown() || keyboard.get(Keyboard.Z).isDown());
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT).isDown() || keyboard.get(Keyboard.D).isDown());
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN).isDown() || keyboard.get(Keyboard.S).isDown());
        //This is used to launch a fireball
        if (keyboard.get(Keyboard.X).isPressed() && hasStaff) {
            Fireball fireball = new Fireball(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
            fireball.enterArea(getOwnerArea(), getCurrentMainCellCoordinates());
        }
        //Todo: check that placing the update below the die method doesn't cause any problems

        //If the player is weak (its hp are below 0), he dies
        if (isWeak()) {
            die();
        }
        super.update(deltaTime);
    }
    /**
     * Orientate and Move this player in the given orientation if the given button is down
     * @param orientation (Orientation): given orientation, not null
     * @param b (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, boolean b){
        if(b) {
            //If the player has collected cherries, the duration of the move animation is divided by the number of cherries he has collected hence making him move faster
            if (getOwnerArea().getKeyboard().get(Keyboard.A).isDown() && numberOfCherries>0 && !isDisplacementOccurs()) {
                orientate(orientation);
                currentSprite = sprites[orientation.ordinal()][1];
                move(MOVE_DURATION/(2*numberOfCherries));
            }
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                currentSprite = sprites[orientation.ordinal()][1];
                move(MOVE_DURATION);
            }
        }
    }

    /**
     *
     * @param area (Area): initial area, not null
     * @param position (DiscreteCoordinates): initial position, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
        ((ICRogueRoom) getOwnerArea()).visitRoom();
    }


    @Override
    public void draw(Canvas canvas) {
        if (skipFrame > 0) {
            skipFrame--;
            return;
        }
        currentSprite.draw(canvas);
        chooseSprite();
        message.draw(canvas);
    }

    private void chooseSprite() {
        if (frameUpdate == 0) {
            walkingAnimation = (walkingAnimation + 1) % 3;
            if (isDisplacementOccurs()) {
                currentSprite = sprites[getOrientation().ordinal()][walkingAnimation];
            } else {
                currentSprite = sprites[getOrientation().ordinal()][0];
            }
            frameUpdate = 3;
        }
        else {
            frameUpdate--;
        }
    }

    /**
     * @return (boolean): true if the player's hp are below 0 (he is weak) and false otherwise
     */
    public boolean isWeak() {
        return (hp <= 0.f);
    }

    /**
     * Give back all of the player's health points
     */
    @SuppressWarnings("unused")
    public void strengthen() {
        hp = constants.playerHealth;
    }

    /**
     * Hurt the player by the given amount of damage
     * @param damage (float): amount of damage to be inflicted
     */
    public void hurt(float damage) {
        skipFrame = 5; // This is used to make the player blink when he is damaged
        hp -= damage;
    }

    /**
     * Kill the player (set his hp to 0) and end the game
     */
    public void die() {
        dead = true;
        leaveArea();
    }

    /**
     * @return (boolean): true if the player is dead and false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    ///Ghost implements Interactable

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return getOwnerArea().getKeyboard().get(Keyboard.W).isPressed();
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * @return (boolean): true if the player is currently switching area and false otherwise
     * this method is required for a proper implementation of the connectors
     */
    public boolean isSwitchingArea() {
        return isSwitchingArea;
    }

    /**
     * @return (String): the name of the area the player will join once he has left the current area if he is currently switching area and null otherwise
     */
    public String destinationArea() {
        if (isSwitchingArea()) {
            return destinationArea;
        }
        return null;
    }

    /**
     * Is called once the player has joined the new area to stop it from switching area again.
     */
    public void doneSwitchingArea() {
        isSwitchingArea = false;
    }

    /**
     * @return (DiscreteCoordinates): the coordinates the player will join at in the destination area if he is currently switching area and null otherwise
     */
    public DiscreteCoordinates getPositionInDestinationArea() {
        if (isSwitchingArea()) {
            return positionInDestinationArea;
        }
        return null;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this, isCellInteraction);
    }

    /**
     * This class is used to handle the interactions between the player and any other interactable entity
     */
    private class ICRoguePlayerInteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
        }
        @Override
        public void interactWith(Cherry cherry, boolean isCellInteraction) {
            if (cherry.getCurrentCells().equals(getCurrentCells())) {
                if (!cherry.isCollected()) {
                    numberOfCherries++;
                    strengthen();
                }
                cherry.collect();
            }
        }

        /**
         *
         * @param key: the key to interact with
         * @param isCellInteraction: true if the interaction is with a cell
         */
        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (key.getCurrentCells().equals(getCurrentCells())) {
                key.collect();
                keys.add(key.getKeyId());
            }
        }
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            if (staff.getCurrentCells().equals(getFieldOfViewCells())) {
                staff.collect();
                hasStaff = true;
            }
        }
        @Override
        public void interactWith(Connector connector, boolean isCellInteraction) {
            if (!isCellInteraction && wantsViewInteraction()) {
                if (connector.getState() == Connector.connectorState.LOCKED) {
                    if (keys.contains(connector.getKeyId())) {
                        connector.setState(Connector.connectorState.OPEN);
                    }
                    else {
                        System.out.printf("Keys owned :  %s %nKey needed: %s%n", keys, connector.getKeyId());
                    }
                }
            }
            if (isCellInteraction && wantsCellInteraction()) {
                if (connector.getState() == Connector.connectorState.OPEN && !isDisplacementOccurs() && (connector.getCurrentCells().contains(getCurrentMainCellCoordinates()))) {
                    destinationArea = connector.getDestinationArea();
                    positionInDestinationArea = Level0Room.getDestination(connector);
                    isSwitchingArea = true;
                }
            }
        }
        @Override
        public void interactWith(Turret turret, boolean isCellInteraction){
            if (isCellInteraction && wantsCellInteraction()) {
                if (turret.getCurrentCells().contains(getCurrentMainCellCoordinates())) {
                    turret.die();
                }
            }
        }
        @Override
        public void interactWith(Boss boss, boolean isCellInteration){

        }
    }
}
