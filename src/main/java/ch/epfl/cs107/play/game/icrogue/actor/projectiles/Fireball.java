package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Boss;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Fireball extends Projectile implements ICRogueInteractionHandler {
    private final ICRogueFireballInteractionHandler handler;

    private final Sprite[] sprites;
    int animationIndex = 0;
    public Fireball(Area area, Orientation orientation, DiscreteCoordinates position) {
        //Projectile(Area area, Orientation orientation, DiscreteCoordinates position, float damage, int range)
        super(area, orientation, position, 1, 35);
        sprites = Sprite.extractSprites("zelda/fire", 7, 1f, 1f, this, 16, 16);
        this.handler = new ICRogueFireballInteractionHandler();
        setSprite(sprites[0]);
    }

    @Override
    public void draw(Canvas canvas) {
        getSprite().draw(canvas);
    }

    /**
     * @see Fireball#update(float)
     * @param orientation
     * This method is used to orientate the fireball in the direction of the player and fire it
     */
    public void launch(Orientation orientation) {
        this.orientate(orientation);
        this.setCurrentPosition(this.getPosition().add(orientation.toVector().div(4))); //make the fireball appear in front of the player
        //this.move(5);
    }

    @Override
    public void consume() {
        super.consume();
        this.getOwnerArea().unregisterActor(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animate();
        //Todo: Damage the player if he is in the same cell
        this.launch(getOrientation());
    }
    private void animate(){
        animationIndex = (animationIndex + 1) % sprites.length;
        setSprite(sprites[animationIndex]);
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * @param area (Area): the area where the fireball is
     * @param position (DiscreteCoordinates): the position of the fireball at the beginning
     * This method is used to make the fireball be able to enter the area and be able to be fired
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        changePosition(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }
    @Override
    public boolean isViewInteractable() {
        return false;
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

    /**
     * This class is used to handle the interaction between the fireball and any other interactable
     */
    public class ICRogueFireballInteractionHandler implements ICRogueInteractionHandler {
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
            if (cell.getType() == ICRogueBehavior.ICRogueCellType.WALL || cell.getType() == ICRogueBehavior.ICRogueCellType.HOLE) {
                consume();
            }
        }
        public void interactWith(Turret turret, boolean isCellInteraction){
            if (isCellInteraction) {
                turret.die();
                consume();
            }
        }
        public void interactWith(Boss boss, boolean isCellInteraction){
            if (isCellInteraction) {
                boss.damage(50);
                boss.die();
                consume();
            }
        }
    }
}