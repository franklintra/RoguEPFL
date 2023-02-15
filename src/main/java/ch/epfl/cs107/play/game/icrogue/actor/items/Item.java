package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Item extends CollectableAreaEntity {
    private Sprite sprite;

    public Item(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            sprite.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }


    //Setter & getter to leave the management of the sprite to the abstract class
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public Sprite getSprite() {
        return sprite;
    }
}
