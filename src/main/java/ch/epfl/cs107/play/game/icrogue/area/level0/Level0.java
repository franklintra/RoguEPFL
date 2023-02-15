package ch.epfl.cs107.play.game.icrogue.area.level0;

import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public class Level0 extends Level {
    //TODO: use this to create the rooms

    private static final int PART_1_KEY_ID = 2;
    private static final int BOSS_KEY_ID = 100;

    public Level0(boolean randomMap, DiscreteCoordinates startPosition, int[] roomsDistribution, int width, int height) {
        super(randomMap, startPosition, roomsDistribution, width, height);
        //is done in generateMap()
    }


    /**
     * Enumerates all the possible rooms in this level
     */
    public enum roomType {
        TurretRoom,
        StaffRoom,
        BossKeyRoom,
        SpawnRoom,
        DefaultRoom;

        ICRogueRoom getRoomFromType(DiscreteCoordinates roomCoords) {
            return switch (this) {
                case TurretRoom -> new Level0TurretRoom(roomCoords);
                case StaffRoom -> new Level0StaffRoom(roomCoords);
                case BossKeyRoom -> new Level0KeyRoom(roomCoords, BOSS_KEY_ID);
                case SpawnRoom -> new Level0Room(roomCoords);
                case DefaultRoom -> new Level0CherryRoom(roomCoords);
            };
        }
    }

    /**
     * This will add the all the rooms to the level at the correct coordinates
     * @param mapStates the blueprint on which the level will be built
     */
    protected void generateRandomMap(MapState[][] mapStates){
        {
            DiscreteCoordinates roomCoords = getRandomAvailableBossRoomPosition(mapStates);
            mapStates[roomCoords.x][roomCoords.y] = MapState.CREATED;
            System.out.println("Boss room at " + roomCoords.x + ", " + roomCoords.y);
            ICRogueRoom room = new Level0BossRoom(roomCoords);
            room.setAdvancementBar(getAdvancementBar());
            setRoom(roomCoords, room);
            setUpConnector(mapStates, room);
            addRoomCoordinates(roomCoords);
        }
        out:
        for (int i = 0; i < getRoomDistribution().length; i++) {
            for (int j = 0; j < getRoomDistribution()[i]; j++) {
                DiscreteCoordinates roomCoords = getRandomAvailableRoomPosition(mapStates);
                /* This is for testing purposes. It will avoid a crash if there are no more available positions
                if (roomCoords==null) {
                    System.out.println("No more room positions available");
                    break out;
                }
                 */
                mapStates[roomCoords.x][roomCoords.y] = MapState.CREATED;
                ICRogueRoom room = roomType.values()[i].getRoomFromType(roomCoords);
                room.setAdvancementBar(getAdvancementBar());
                setRoom(roomCoords, room);
                setUpConnector(mapStates, room);
                addRoomCoordinates(roomCoords);
                if (roomType.values()[i] == roomType.SpawnRoom) {
                    setStartRoom(roomCoords);
                }
            }
        }
    }

    /**
     * @param roomPosition the position of the room
     * @return the room at the given position
     * this method was introduced to keep the room encapsulated (FIXME: IT ISN'T THE RIGHT WAY TO DO IT)
     */
    public ICRogueRoom getRoom(DiscreteCoordinates roomPosition) {
        return super.getRoom(roomPosition);
    }

    /**
     * @return the title of the room in which the player starts
     */
    public String defaultRoom() {
        return getStartRoom();
    }


    /**
     *
     * @param mapStates the mapStates of the map
     * @param room the current room
     * This method is used to place the doors of the current room being generated in generateRandomMap() in the actual room
     */
    protected void setUpConnector(MapState[][] mapStates, ICRogueRoom room) {
        for (DiscreteCoordinates relativeCoords : relativeCoords.getAllPossibleCoords()) {
            DiscreteCoordinates absoluteCoords = room.getRoomCoordinates();
            DiscreteCoordinates destinationCoords = new DiscreteCoordinates(relativeCoords.x+absoluteCoords.x, relativeCoords.y+absoluteCoords.y);
            if (isInMap(mapStates, destinationCoords) && isRoomAvailable(mapStates, destinationCoords)) {
                setRoomConnector(absoluteCoords, room.getTitle(), Level.relativeCoords.getConnector(relativeCoords));
                setRoomConnectorDestination(absoluteCoords, "icrogue/level0"+destinationCoords.x + ""+destinationCoords.y, Level.relativeCoords.getConnector(relativeCoords));
            }
            if (getRoom(destinationCoords) instanceof Level0BossRoom) {
                lockRoomConnector(absoluteCoords, Level.relativeCoords.getConnector(relativeCoords), 100);
            }
        }
    }

    /**
     * Generates a map
     */
    protected void generateFixedMap() {
        generateFinalMap();
        //generateMap1();
        //generateMap2();
        //generateRandomMap();
    }


    /**
     * This is a test fixed map to ensure everything works properly
     */
    @SuppressWarnings("unused")
    private void generateMap1() {
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0KeyRoom(room00, PART_1_KEY_ID));
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);
        lockRoomConnector(room00, Level0Room.Level0Connectors.E,  PART_1_KEY_ID);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1, 0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level000", Level0Room.Level0Connectors.W);

        addRoomCoordinates(room00);
        addRoomCoordinates(room10);
    }

    /**
     * This is another test fixed map to ensure everything works properly
     */
    @SuppressWarnings("unused")
    private void generateMap2() {
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0Room(room00));
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1,0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level011", Level0Room.Level0Connectors.S);
        setRoomConnector(room10, "icrogue/level020", Level0Room.Level0Connectors.E);

        setRoomConnector(room10, "icrogue/level00", Level0Room.Level0Connectors.W);
        lockRoomConnector(room10, Level0Room.Level0Connectors.W,  BOSS_KEY_ID);

        DiscreteCoordinates room20 = new DiscreteCoordinates(2,0);
        setRoom(room20,  new Level0StaffRoom(room20));
        setRoomConnector(room20, "icrogue/level010", Level0Room.Level0Connectors.W);
        setRoomConnector(room20, "icrogue/level030", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room30 = new DiscreteCoordinates(3,0);
        setRoom(room30, new Level0KeyRoom(room30, BOSS_KEY_ID));
        setRoomConnector(room30, "icrogue/level020", Level0Room.Level0Connectors.W);

        DiscreteCoordinates room11 = new DiscreteCoordinates(1, 1);
        setRoom (room11, new Level0CherryRoom(room11));
        setRoomConnector(room11, "icrogue/level010", Level0Room.Level0Connectors.N);

        addRoomCoordinates(room00);
        addRoomCoordinates(room10);
        addRoomCoordinates(room20);
        addRoomCoordinates(room30);
        addRoomCoordinates(room11);
    }

    /**
     * This is a fixed map that is used for the final version of the game if we don't want to use the random map generator
     */
    @SuppressWarnings("unused")
    protected void generateFinalMap() {
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0BossRoom(room00));
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1,0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level011", Level0Room.Level0Connectors.S);
        setRoomConnector(room10, "icrogue/level020", Level0Room.Level0Connectors.E);

        setRoomConnector(room10, "icrogue/level000", Level0Room.Level0Connectors.W);
        lockRoomConnector(room10, Level0Room.Level0Connectors.W,  BOSS_KEY_ID);

        DiscreteCoordinates room20 = new DiscreteCoordinates(2,0);
        setRoom(room20,  new Level0StaffRoom(room20));
        setRoomConnector(room20, "icrogue/level010", Level0Room.Level0Connectors.W);
        setRoomConnector(room20, "icrogue/level030", Level0Room.Level0Connectors.E);

        DiscreteCoordinates room30 = new DiscreteCoordinates(3,0);
        setRoom(room30, new Level0KeyRoom(room30, BOSS_KEY_ID));
        setRoomConnector(room30, "icrogue/level020", Level0Room.Level0Connectors.W);

        DiscreteCoordinates room11 = new DiscreteCoordinates(1, 1);
        setRoom (room11, new Level0CherryRoom(room11));
        setRoomConnector(room11, "icrogue/level010", Level0Room.Level0Connectors.N);

        addRoomCoordinates(room00);
        addRoomCoordinates(room10);
        addRoomCoordinates(room20);
        addRoomCoordinates(room30);
        addRoomCoordinates(room11);
    }
}
