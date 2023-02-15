package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Key extends Item implements Interactable, ICRogueInteractionHandler {
    private final int keyId;
    public Key(Area area, Orientation orientation, DiscreteCoordinates position, int keyId) {
        super(area, orientation, position);
        this.keyId = keyId;
        setSprite(new Sprite("icrogue/key", 0.6f, 0.6f, this));
    }

    /**
     * Getter for the keyId
     * @return (int): the keyId that is given to the player when he picks up the key
     */
    public int getKeyId() {
        return keyId;
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
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }
}
