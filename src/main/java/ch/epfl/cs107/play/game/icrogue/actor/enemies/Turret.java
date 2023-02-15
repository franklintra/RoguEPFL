package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class Turret extends Enemy implements Interactable, ICRogueInteractionHandler {
    public static final float COOLDOWN = 4f;
    private float coolingDown;
    public Turret(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        setSprite(new Sprite("icrogue/static_npc", 1.5f, 1.5f, this, null, new Vector(-0.25f, 0))); // Set the sprite of the Turret
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (coolingDown >= COOLDOWN) {
            attack();
            coolingDown = 0;
        }
        else {
            coolingDown += deltaTime;
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    /**
     * @return (List<Arrow>): a list of arrows that the turret will shoot
     */
    private List<Arrow> newArrows() {
        List<Arrow> arrows = new ArrayList<>();
        for (Orientation orientation : Orientation.values()) {
            arrows.add(new Arrow(getOwnerArea(), orientation, getCurrentMainCellCoordinates().jump(orientation.toVector()).jump(orientation.toVector()).jump(orientation.toVector())));
        }
        return arrows;
    }

    /**
     * @see Turret#update(float)
     * This method is called when the turret is ready to attack and will shoot 4 arrows in the 4 directions
     */
    private void attack(){
        for (Arrow arrow : newArrows()) {
            arrow.enterArea(getOwnerArea(), getCurrentMainCellCoordinates());
        }
    }
}
