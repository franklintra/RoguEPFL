package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class Arrow extends Projectile implements ICRogueInteractionHandler{
    public final static float DAMAGE = 4;
    private final ICRogueArrowInteractionHandler handler;
    public Arrow(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, DAMAGE);
        this.handler = new ICRogueArrowInteractionHandler();
        setSprite(new Sprite("zelda/Arrow", 1f, 1f, this, new RegionOfInterest(32*orientation.ordinal(), 0, 32, 32), new Vector(0, 0)));
    }

    @Override
    public void draw(Canvas canvas) {
        getSprite().draw(canvas);
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    public void launch(Orientation orientation) {
        this.orientate(orientation);
        this.setCurrentPosition(this.getPosition().add(orientation.toVector().div(4))); // make the arrow appear in front of the thing that shoots it
        //this.move(5);
    }

    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setOwnerArea(area);
        //check if position.toVector().add(getOrientation().toVector()) is in the area and the cell is not occupied
        //vector to discrete coordinates
        setCurrentPosition(position.toVector());
        changePosition(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));

    }

    public void consume() {
        super.consume();
        this.getOwnerArea().unregisterActor(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //Todo: Damage the player if he is in the same cell
        this.launch(getOrientation());
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    public class ICRogueArrowInteractionHandler implements ICRogueInteractionHandler {
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (cell.getType() == ICRogueBehavior.ICRogueCellType.WALL || cell.getType() == ICRogueBehavior.ICRogueCellType.HOLE) {
                if (getFieldOfViewCells().contains(cell.getCurrentCells().get(0))) {
                    consume();
                }
            }
        }
        public void interactWith(Turret turret, boolean isCellInteraction) {
        }
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            if (!isConsumed()) {
                player.hurt(DAMAGE);
                consume();
            }
        }
    }
}
