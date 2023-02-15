package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public abstract class Enemy extends ICRogueActor implements Interactable {
    private boolean alive = true;
    Sprite sprite;

    //public abstract void enterArea(Area area, DiscreteCoordinates position);


    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        sprite = new Sprite("zombie.1", 1, 1.f, this);
    }


    @Override
    public void draw(Canvas canvas) {
        if (isAlive()) {
            sprite.draw(canvas);
        }
        else {
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * This is used so that the sprite management is given to the abstract class
     * @param sprite
     * It sets the instance variable sprite to the given sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * This is used so that the sprite management is given to the abstract class
     * @return (Sprite): the sprite of the enemy
     */
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler)v).interactWith(this, isCellInteraction);
    }

    /**
     * This method is used to kill the enemy
     */
    public void die(){
        alive = false;
    }

    /**
     * This method is used to know if the enemy is alive
     * @return (boolean): true if the enemy is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * This method is used to know if the enemy is dead
     * @return (boolean): true if the enemy is dead, false otherwise
     */
    public boolean isDead() {
        return !isAlive();
    }
}
