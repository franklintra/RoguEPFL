package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Cherry;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0CherryRoom extends Level0ItemRoom {
    private final Cherry cherry;
    public Level0CherryRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        cherry = new Cherry(this, Orientation.UP, new DiscreteCoordinates(5, 7));
        addItem(cherry);
    }
}
