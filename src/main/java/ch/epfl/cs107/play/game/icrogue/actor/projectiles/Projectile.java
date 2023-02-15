package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public abstract class Projectile extends ICRogueActor implements Consumable, Interactor {
    private static final float DEFAULT_DAMAGE = 10f;
    private static final int DEFAULT_RANGE = 100;
    private boolean isConsumed = false;
    private final float damage;
    private int range;
    private Sprite sprite;

    //Constructors
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, float damage) {
        this(area, orientation, position, damage, DEFAULT_RANGE);
    }
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, int range) {
        this(area, orientation, position, DEFAULT_DAMAGE, range);
    }
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, float damage, int range) {
        super(area, orientation, position);
        this.damage = damage;
        this.range = range;
    }

    //Setters & getter
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public Sprite getSprite() {
        return sprite;
    }

    public void consume() {
        isConsumed = true;
    }

    public boolean isConsumed(){
        return isConsumed;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (range <= 0 && !isConsumed) {
            consume();
        }
        range--;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }
    @Override
    public boolean wantsViewInteraction() {
        return true;
    }
}