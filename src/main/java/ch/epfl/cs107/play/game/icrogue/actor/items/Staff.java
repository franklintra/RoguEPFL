package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class Staff extends Item implements Interactable, ICRogueInteractionHandler {
    Sprite[] sprites = Sprite.extractSprites("zelda/staff", 8, 1f, 1f, this, new Vector(0, 0), 32, 32);
    int animationIndex = 0;
    public Staff(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        setSprite(sprites[0]);
    }

    private void chooseSprite(){
        animationIndex = (animationIndex + 1) % 8;
        setSprite(sprites[animationIndex]);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        chooseSprite();
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public boolean takeCellSpace() {
        return !isCollected();
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }
}
