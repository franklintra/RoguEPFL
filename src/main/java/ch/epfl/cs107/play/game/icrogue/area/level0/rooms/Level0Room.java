package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;
import ch.epfl.cs107.play.game.actor.GraphicsEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.area.ConnectorInRoom;
import ch.epfl.cs107.play.game.icrogue.constants;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.*;

import static ch.epfl.cs107.play.game.icrogue.constants.levelName;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public class Level0Room extends ICRogueRoom {
    private GraphicsEntity bar = new GraphicsEntity(new Vector(0, 0), getAdvancementBar());
    public enum Level0Connectors implements ConnectorInRoom {
        W(new DiscreteCoordinates(0, 4), new DiscreteCoordinates(8, 5), Orientation.LEFT),
        S(new DiscreteCoordinates(4, 0), new DiscreteCoordinates(5, 8), Orientation.DOWN),
        E(new DiscreteCoordinates(9, 4), new DiscreteCoordinates(1, 5), Orientation.RIGHT),
        N(new DiscreteCoordinates(4, 9), new DiscreteCoordinates(5, 1), Orientation.UP);


        private final DiscreteCoordinates destination;
        private final DiscreteCoordinates position;
        private final Orientation orientation;
        Level0Connectors(DiscreteCoordinates position, DiscreteCoordinates destination, Orientation orientation) {
            this.destination = destination;
            this.position = position;
            this.orientation = orientation;
        }

        @Override
        public int getIndex() {
            return this.ordinal();
        }

        @Override
        public DiscreteCoordinates getDestination() {
            //return first value of this. for example W.getDestination() will return new DiscreteCoordinates(0, 4)
            return this.destination;
        }

        public DiscreteCoordinates getPosition() {
            return this.position;
        }

        public static List<Orientation> getAllConnectorsOrientation() {
            List<Orientation> orientations = new ArrayList<>();
            for (Level0Connectors connector : Level0Connectors.values()) {
                orientations.add(connector.orientation);
            }
            return orientations;
        }

        public static List<DiscreteCoordinates> getAllConnectorsPosition() {
            List<DiscreteCoordinates> positions = new ArrayList<>();
            for (Level0Connectors connector : Level0Connectors.values()) {
                positions.add(connector.getPosition());
            }
            return positions;
        }
    }

    public static DiscreteCoordinates getDestination(Connector connector) {
        if (connector.getCurrentCells().contains(Level0Connectors.W.position)) {
            return Level0Connectors.W.getDestination();
        }
        if (connector.getCurrentCells().contains(Level0Connectors.N.position)) {
            return Level0Connectors.N.getDestination();
        }
        if (connector.getCurrentCells().contains(Level0Connectors.E.position)) {
            return Level0Connectors.E.getDestination();
        }
        if (connector.getCurrentCells().contains(Level0Connectors.S.position)) {
            return Level0Connectors.S.getDestination();
        }
        return new DiscreteCoordinates(5, 5);
    }


    //Constructor just to make sure
    public Level0Room(DiscreteCoordinates roomCoordinates) {
        super(Level0Connectors.getAllConnectorsPosition(), Level0Connectors.getAllConnectorsOrientation() , constants.behaviour, roomCoordinates);
    }

    @Override
    public String getTitle() {
        return "icrogue/level0" + getRoomCoordinates().x + getRoomCoordinates().y;
        //return levelName;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(constants.playerCoords[0], constants.playerCoords[1]);
    }

    @Override
    public boolean isOn() {
        return hasBeenVisited();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public float getIntensity() {
        //implement intensity system to know how much the room has been explored
        return isOn()? 1.0f : 0.0f;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        unregisterActor(bar);
        bar = new GraphicsEntity(new Vector(0, 0), getAdvancementBar());
        registerActor(bar);
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this, levelName));
        //registerActor(new Staff(this, Orientation.DOWN, new DiscreteCoordinates(4, 3)));
        //registerActor(new Cherry(this, Orientation.DOWN, new DiscreteCoordinates(6, 3)));
        for (Connector connector: getConnectors()) {
            //register the connector to the area
            registerActor(connector);
        }
        //Register the advancement bar if it exists (only for Level0Room)
        if (getAdvancementBar()!= null) {
            registerActor(bar);
        }
    }
}