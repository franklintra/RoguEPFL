package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Boss;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Fireball;

@SuppressWarnings("unused")
public interface ICRogueInteractionHandler extends AreaInteractionVisitor {
    /*
    * I will not comment the methods of this interface, as they are self-explanatory.
    * The only thing to note is that the methods are already implemented in this interface, using the default keyword.
    * This makes it so that if an Interactable doesn't implement specific behavior for a specific interaction, it will not crash.
    * They should however be overriden most of the time as these interactions do not have any effect.
    */
    default void interactWith(Staff staff, boolean isCellInteraction) {
    }
    default void interactWith(Cherry cherry, boolean isCellInteraction) {

    }
    default void interactWith(Key key, boolean isCellInteraction) {
    }
    default void interactWith(Fireball fireball, boolean isCellInteraction) {
    }
    default void interactWith(ICRoguePlayer player, boolean isCellInteraction) {

    }

    default void interactWith(Connector connector, boolean isCellInteraction) {
    }
    default void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
    }

    default void interactWith(Turret turret, boolean isCellInteraction) {
    }
    default void interactWith(Arrow arrow, boolean isCellInteraction) {
    }

    default void interactWith(Boss boss, boolean isCellInteraction) {
    }
}
