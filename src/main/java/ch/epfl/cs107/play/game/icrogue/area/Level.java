package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.advancementBar;
import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public abstract class Level implements Logic {
    private final ICRogueRoom[][] map;
    private String startRoom;
    private final List<DiscreteCoordinates> roomCoordinates = new ArrayList<>();
    private int nbRooms;
    private final int[] roomDistribution;
    private final advancementBar advancementBar = new advancementBar();

    public Level(boolean randomMap, DiscreteCoordinates startPosition, int[] roomsDistribution, int width, int height) {
        System.out.println("Generating map...");
        this.roomDistribution = roomsDistribution;
        if (randomMap) {
            System.out.println("Generating random map...");
            nbRooms = Arrays.stream(roomsDistribution).sum();
            map = new ICRogueRoom[nbRooms][nbRooms];
            System.out.println("Map size: " + nbRooms + "x" + nbRooms);
            System.out.println("Map generated");
            //store generateRandomRoomPlacement() in a variable
            MapState[][] mapstate = generateRandomRoomPlacement();
            printMap(mapstate);
            generateRandomMap(mapstate);
            //printMap(mapstate);
            printMap(map);
            setStartRoom(new DiscreteCoordinates(Arrays.stream(roomsDistribution).sum()/2, Arrays.stream(roomsDistribution).sum()/2));
        } else {
            map = new ICRogueRoom[height][width];
            generateFixedMap();
            setStartRoom(startPosition);
        }
    }

    protected void setRoomConnectorDestination(DiscreteCoordinates roomPosition, String destination, ConnectorInRoom connector){
        map[roomPosition.x][roomPosition.y].getConnectors().get(connector.getIndex()).setDestinationArea(destination);
    }
    protected void setRoomConnector(DiscreteCoordinates roomPosition, String destination, ConnectorInRoom connector) {
        setRoomConnectorDestination(roomPosition, destination, connector);
        map[roomPosition.x][roomPosition.y].getConnectors().get(connector.getIndex()).setState(Connector.connectorState.CLOSED);
    }
    protected void lockRoomConnector(DiscreteCoordinates roomPosition, ConnectorInRoom connector, int keyId) {
        map[roomPosition.x][roomPosition.y].getConnectors().get(connector.getIndex()).setState(Connector.connectorState.LOCKED);
        map[roomPosition.x][roomPosition.y].getConnectors().get(connector.getIndex()).setKeyId(keyId);
    }
    protected void setStartRoom(DiscreteCoordinates roomPosition) {
        startRoom = map[roomPosition.x][roomPosition.y].getTitle();
    }
    protected String getStartRoom() {
        return startRoom;
    }
    public int[] getRoomDistribution() {
        //return a copy of the array
        return Arrays.copyOf(roomDistribution, roomDistribution.length);
    }

    /**
     * TODO: I believe this is not the right way to do it. I shouldn't have access to the map from the outside (I don't really have access right now but I can make a copy of it with the setRoom and getRoom methods)
     * @param roomCoordinates the coordinates of the room
     */
    public void addRoomCoordinates(DiscreteCoordinates roomCoordinates) {
        this.roomCoordinates.add(roomCoordinates);
    }
    protected ICRogueRoom getRoom(DiscreteCoordinates roomPosition) {
        return map[roomPosition.x][roomPosition.y];
    }
    public List<DiscreteCoordinates> getRoomCoordinates() {
        return new ArrayList<>(roomCoordinates);
    }
    protected void setRoom(DiscreteCoordinates roomPosition, ICRogueRoom room) {
        map[roomPosition.x][roomPosition.y] = room;
    }


    /**
     * @return the total number of rooms in the level
     */
    public int getNumberOfRooms() {
        int count = 0;
        for (ICRogueRoom[] icRogueRooms : map) {
            for (ICRogueRoom icRogueRoom : icRogueRooms) {
                if (icRogueRoom != null) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return how many rooms have been completed by the player
     */
    public int getNumberOfCompletedRooms() {
        int count = 0;
        for (ICRogueRoom[] icRogueRooms : map) {
            for (ICRogueRoom icRogueRoom : icRogueRooms) {
                if (icRogueRoom != null && icRogueRoom.isOn()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return true if the level is completed false otherwise
     */
    public boolean isOn() {
        //return true if not all rooms are completed (i.e. not all rooms are room.isOn())
        return getNumberOfCompletedRooms() == getNumberOfRooms();
    }

    /**
     * @return true if the level is not completed false otherwise
     */
    public boolean isOff() {
        return !isOn();
    }
    /**
     * @return how far the player is from the end of the game from 0 to 1
     */
    public float getIntensity(){
        //return the intensity of every room and divide by the number of rooms
        float intensity = 0;
        for (ICRogueRoom[] icRogueRooms : map) {
            for (ICRogueRoom icRogueRoom : icRogueRooms) {
                if (icRogueRoom != null) {
                    intensity += icRogueRoom.getIntensity();
                }
            }
        }
        return intensity / getNumberOfRooms();
    }

    /**
     * @return the advancement bar of the level
     */
    public ImageGraphics getAdvancementBar(){
        return advancementBar.getCurrentBar(this.getIntensity());
    }

    /**
     * Enum that is used to generate a random map by first generating a random blueprint of the map
     */
    protected enum MapState {
        NULL, // Empty space
        PLACED, // The room has been placed but not yetexplored by the room placement algorithm
        EXPLORED, // The room has been placed and explored by the algorithm
        BOSS_ROOM, // The room is a boss room
        CREATED; // The room has been instantiated in the room map
        @Override
        public String toString() {
            return Integer.toString(ordinal()); }
    }
    /**
     * enumerates the possible position for a neighbour room in the map (relative to the current room). This also knows where the connector is located in the room if it exists.
     */
    protected enum relativeCoords {
        UP(new DiscreteCoordinates(0, 1), Level0Room.Level0Connectors.N),
        DOWN(new DiscreteCoordinates(0, -1), Level0Room.Level0Connectors.S),
        RIGHT(new DiscreteCoordinates(1, 0), Level0Room.Level0Connectors.E),
        LEFT(new DiscreteCoordinates(-1, 0), Level0Room.Level0Connectors.W);
        private final DiscreteCoordinates coords;
        private final Level0Room.Level0Connectors connectorOrientation;
        relativeCoords(DiscreteCoordinates coords, Level0Room.Level0Connectors connectorOrientation) {
            this.coords = coords;
            this.connectorOrientation = connectorOrientation;
        }

        /**
         * @return a DiscreteCoordinates array containing all the possible relative coordinates of a neighbour room
         */
        public static DiscreteCoordinates[] getAllPossibleCoords() {
            List<DiscreteCoordinates> coords = new ArrayList<>();
            for (relativeCoords coord : relativeCoords.values()) {
                coords.add(coord.coords);
            }
            return coords.toArray(new DiscreteCoordinates[0]);
        }

        /**
         * @return a Level0Connectors according to the relativeCoords of the neighbour room. For example, if the neighbour room is on the right of the current room, the connector will be on the east side of the current room.
         * @param neighbourCoords the relativeCoords of the neighbour room
         */
        public static Level0Room.Level0Connectors getConnector(DiscreteCoordinates neighbourCoords) {
            for (relativeCoords coord : relativeCoords.values()) {
                if (coord.coords.equals(neighbourCoords)) {
                    return coord.connectorOrientation;
                }
            }
            return null;
        }

    }

    /**
     * @return a MapState[][] containing the blueprint of the randomly generated map
     */
    private MapState[][] generateRandomRoomPlacement() {
        //1)Create a map of all possible rooms set to NULL
        int roomsToPlace = nbRooms-1; //The boss room is not counted in the number of rooms to place because it is placed at the end of the while loop: therefore roomsToPlace = nbRooms-1
        MapState[][] mapStates = new MapState[nbRooms][nbRooms];
        for (int i = 0; i < nbRooms; i++) {
            for (int j = 0; j < nbRooms; j++) {
                mapStates[i][j] = MapState.NULL;
            }
        }

        //2)Make the room in the middle of the map a placed room
        mapStates[nbRooms/2][nbRooms/2] = MapState.PLACED;
        DiscreteCoordinates currentPos;

        //3)While there are rooms to place
        while (roomsToPlace>0){
            //printMap(mapStates);
            //get random element in a list
            if (placedRoomsWithFreeNeighbour(mapStates).size() == 1){
                currentPos = placedRoomsWithFreeNeighbour(mapStates).get(0);
            }
            else {
                currentPos = placedRoomsWithFreeNeighbour(mapStates).get(RandomHelper.roomGenerator.nextInt(placedRoomsWithFreeNeighbour(mapStates).size()-1));
            }
            int freeSlots = numberOfFreeNeighbour(mapStates, currentPos);

            //random int between 0 and min(freeSlots, roomsToPlace) using RandomHelper.roomGenerator.nextInt(borneMin, borneMax)

            int toPlace;
            if (Math.min(freeSlots, roomsToPlace) == 1) {
                toPlace = 1;
            }
            else {
                toPlace = RandomHelper.roomGenerator.nextInt(Math.min(freeSlots - 1, roomsToPlace - 1)) + 1;
            }

            //c)Place a room in a random free slot next to a randomly selected already-placed room
            {
                DiscreteCoordinates pos = getRandomFreeNeigbour(mapStates, currentPos);
                for (int i = 0; i < toPlace; i++) {
                    assert pos != null;
                    mapStates[pos.x][pos.y] = MapState.PLACED;
                    pos = getRandomFreeNeigbour(mapStates, currentPos);
                }
            }

            //d) make the current room explored
            {
                mapStates[currentPos.x][currentPos.y] = MapState.EXPLORED;
                roomsToPlace -= toPlace;
            }
        }

        //4) place the boss room in a free slot next to a random already-placed room
        DiscreteCoordinates bossRoomPosition = getRandomFreeNeigbour(mapStates, placedRoomsWithFreeNeighbour(mapStates).get(RandomHelper.roomGenerator.nextInt(placedRoomsWithFreeNeighbour(mapStates).size())));
        assert bossRoomPosition != null;
        mapStates[bossRoomPosition.x][bossRoomPosition.y] = MapState.BOSS_ROOM;

        //5) return the mapStates
        return mapStates;
    }

    /**
     * @param mapStates the mapStates of the map
     * @param pos the position of the room that we want to verify is in the map
     * @return a list of all the placed rooms that have at least one free neighbour
     */
    protected boolean isInMap(MapState[][] mapStates, DiscreteCoordinates pos){
        return pos.x >= 0 && pos.x < mapStates.length && pos.y >= 0 && pos.y < mapStates[0].length;
    }


    /**
    * @param mapStates the map of the randomly generated rooms
    * @return a randomly selected room that is available to be a boss room
     */
    protected DiscreteCoordinates getRandomAvailableBossRoomPosition(MapState[][] mapStates){
        List<DiscreteCoordinates> roomsAvailable = new ArrayList<>();
        for (int i = 0; i < mapStates.length; i++) {
            for (int j = 0; j < mapStates[0].length; j++) {
                if (mapStates[i][j] == MapState.BOSS_ROOM){
                    roomsAvailable.add(new DiscreteCoordinates(i, j));
                }
            }
        }
        if (roomsAvailable.size() == 0) {
            return null;
        }
        return roomsAvailable.get(RandomHelper.roomGenerator.nextInt(roomsAvailable.size()));
    }

    /**
     * @param mapStates the map of the randomly generated rooms
     * @return a randomly selected room that is available to be any other room than a boss room
     */
    protected DiscreteCoordinates getRandomAvailableRoomPosition(MapState[][] mapStates) {
        List<DiscreteCoordinates> roomsAvailable = new ArrayList<>();
        for (int i = 0; i < mapStates.length; i++) {
            for (int j = 0; j < mapStates[0].length; j++) {
                if (mapStates[i][j] == MapState.PLACED || mapStates[i][j] == MapState.EXPLORED) {
                    roomsAvailable.add(new DiscreteCoordinates(i, j));
                }
            }
        }
        if (roomsAvailable.size() == 0) {
            return null;
        }
        return roomsAvailable.get(RandomHelper.roomGenerator.nextInt(roomsAvailable.size()));
    }


    /**
     * @param mapStates the mapStates of the map
     * @return a list of the coordinates of all the placed rooms that have at least one free neighbour
     */
    private List<DiscreteCoordinates> placedRoomsWithFreeNeighbour(MapState[][] mapStates){
        List<DiscreteCoordinates> placedRoomsWithFreeNeigbour = new ArrayList<>();
        for (int i=0; i<mapStates.length; i++){
            for (int j=0; j<mapStates.length; j++){
                for (DiscreteCoordinates position : relativeCoords.getAllPossibleCoords()){
                    if (isInMap(mapStates, new DiscreteCoordinates(i+position.x, j+position.y)) && mapStates[i][j] == MapState.PLACED && mapStates[i+position.x][j+position.y] == MapState.NULL){
                        placedRoomsWithFreeNeigbour.add(new DiscreteCoordinates(i, j));
                        break;
                    }
                }
            }
        }
        return placedRoomsWithFreeNeigbour;
    }

    /**
     * @param mapStates the mapStates of the map
     * @param position the position of the room of which we want to get a random free neighbour
     * @return a random free neighbour of the room at the given position if there is at least one, null otherwise
     */
    private DiscreteCoordinates getRandomFreeNeigbour(MapState[][] mapStates, DiscreteCoordinates position){
        List<DiscreteCoordinates> freeNeigbours = new ArrayList<>();
        for (DiscreteCoordinates relativePosition : relativeCoords.getAllPossibleCoords()){
            final var pos = new DiscreteCoordinates(position.x + relativePosition.x, position.y + relativePosition.y);
            if (isInMap(mapStates, pos) && mapStates[position.x+relativePosition.x][position.y+relativePosition.y] == MapState.NULL){
                freeNeigbours.add(pos);
            }
        }
        if (freeNeigbours.size()==0) {
            return null;
        }
        else {
            return freeNeigbours.get(RandomHelper.roomGenerator.nextInt(freeNeigbours.size()));
        }
    }

    /**
     * @param mapStates the mapStates of the map
     * @param position the position of the room that we want to know if it is meant to exist in the actual game
     */
    protected boolean isRoomAvailable(MapState[][] mapStates, DiscreteCoordinates position){
        return isInMap(mapStates, position) && (mapStates[position.x][position.y] == MapState.PLACED || mapStates[position.x][position.y] == MapState.EXPLORED || mapStates[position.x][position.y] == MapState.BOSS_ROOM || mapStates[position.x][position.y] == MapState.CREATED);
    }

    /**
     * @param mapStates the mapStates of the map
     * @param position the position of the room
     * @return the number of free neighbours of the room at the given position
     */
    private int numberOfFreeNeighbour(MapState[][] mapStates, DiscreteCoordinates position) {
        int count = 0;
        for (DiscreteCoordinates relativePosition : relativeCoords.getAllPossibleCoords()){
            if (isInMap(mapStates, new DiscreteCoordinates(position.x+relativePosition.x, position.y+relativePosition.y)) && mapStates[position.x+relativePosition.x][position.y+relativePosition.y] == MapState.NULL){
                count++;
            }
        }
        return count;
    }

    /**
     * @param mapStates the mapStates of the map
     * @print Prints beautifully the mapStates of the map in the console
     */
    protected void printMap(MapState[][] mapStates) { System.out.println("Generated map:");
        System.out.print("  | ");
        for (int j = 0; j < mapStates[0].length; j++) {
            System.out.print(j + " "); }
        System.out.println(); System.out.print("--|-");
        for (int j = 0; j < mapStates[0].length; j++) {
            System.out.print("--"); }
        System.out.println();
        for (int i = 0; i < mapStates.length; i++) { System.out.print(i + " | ");
            for (int j = 0; j < mapStates[i].length; j++) {
                System.out.print(mapStates[i][j] + " "); }
            System.out.println(); }
        System.out.println();
    }

    /**
     * @param map the map that will be actually used in the game
     * @print Prints beautifully the map of the game in the console
     */
    protected void printMap(ICRogueRoom[][]map){
        System.out.println("Generated map for the game engine:");
        System.out.print("  | ");
        for (int j = 0; j < map[0].length; j++) {
            System.out.print(j + " "); }
        System.out.println(); System.out.print("--|-");
        for (int j = 0; j < map[0].length; j++) {
            System.out.print("--"); }
        System.out.println();
        for (int i = 0; i < map.length; i++) { System.out.print(i + " | ");
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(((map[i][j] == null)? "O": "X") + " "); }
            System.out.println(); }
        System.out.println();
    }


    /**
     * @param mapStates the mapStates of the map
     * @param room the current room
     * This method is used to place the doors of the current room in the actual map
     */
    protected abstract void setUpConnector(MapState[][] mapStates, ICRogueRoom room);

    /**
     * This method is used to generate the fixed map of the game if randomMap is false
     */
    protected abstract void generateFixedMap();

    /**
     * This method is used to generate the random map of the game if randomMap is true
     * @param mapstate the blueprint of the map
     */
    protected abstract void generateRandomMap(MapState[][] mapstate);

    /**
     * This method will choose which map to generate
     */
    protected abstract void generateFinalMap();
}
