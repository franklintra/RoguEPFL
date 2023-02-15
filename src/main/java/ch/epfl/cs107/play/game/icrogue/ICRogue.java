package ch.epfl.cs107.play.game.icrogue;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.Level0;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

/**
 * @author Franklin Tranié
 * @project projet-2
 */

public class ICRogue extends AreaGame {
    //BEWARE YOU CAN'T PUT MORE THAN ONE BOSS ROOM IN THE SAME LEVEL
    private final int[] roomsDistribution = new int[]{1, 3, 3, 3, 3};
    //TurretRoom, StaffRoom, Boss_Key, CherryRoom, Level0Room
    Level0 game = new Level0(false, new DiscreteCoordinates(1, 1), roomsDistribution, 10, 10);
    public final static float CAMERA_SCALE_FACTOR = constants.cameraScaleFactor;
    private boolean animationRunning = false;
    private ICRoguePlayer player;

    /**
     * Default ICRogue constructor
     */
    public ICRogue() {
    }

    /**
     * Add all the areas
     */
    private void initLevel(){
        //Todo: implement conncectorsCoordinates and connectorsOrientations
        //game = new Level0(false, new DiscreteCoordinates(1, 1), roomsDistribution, 10, 10); // FiXME: make this be the same constructor as the one l23
        for (DiscreteCoordinates roomCoords : game.getRoomCoordinates()) {
            addArea(game.getRoom(roomCoords));
            System.out.println("Added room :" + game.getRoom(roomCoords).getClass().getSimpleName() + " : " + game.getRoom(roomCoords).getTitle());
        }
        setCurrentArea(game.defaultRoom(), false);
        player = new ICRoguePlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(constants.playerCoords[0], constants.playerCoords[1]), constants.spriteName);
        getCurrentArea().registerActor(player);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            initLevel();
            initArea(getCurrentArea().getTitle());
            return true;
        }
        return false;
    }


    /**
     * This method inits the current area and makes the player enter it
     * @param areaKey The title of the area to be initialized
     */
    private void initArea(String areaKey) {
        ICRogueRoom area = (ICRogueRoom)setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnPosition();
        player = new ICRoguePlayer(area, Orientation.DOWN, coords, constants.spriteName);
        player.enterArea(area, coords);
    }

    /**
     * This method is called when the player wants to change area (interact with a Connector aka a Door)
     * @param areaKey The title of the area to be initialized
     * @param position The position of the player in the new area
     */
    private void switchRoom(String areaKey, DiscreteCoordinates position) {
        player.leaveArea();
        setCurrentArea(areaKey, true);
        player.enterArea(getCurrentArea(), position);
        player.doneSwitchingArea();
        System.out.printf("Switched to %s at coordinates %s:%s%n", areaKey, position.x, position.y);
    }

    @Override
    public void update(float deltaTime) {
        if (getCurrentArea().getKeyboard().get(Keyboard.R).isPressed()) {
            getCurrentArea().unregisterActor(player);
            initLevel();
        }

        if (player.isSwitchingArea()) {
            switchRoom(player.destinationArea(), player.getPositionInDestinationArea());
        }

        if (!isAnimationRunning()) {
            if (player.isDead()) {
                gameOver(false);
            } else if (game.isOn()) {
                gameOver(true);
            }
        }
        /*
         * If the player is in a Level0Room the game can show an advancement bar at the bottom left of the screen. This is useful to update it.
         */
        if (getCurrentArea() instanceof Level0Room) {
            ((Level0Room)getCurrentArea()).setAdvancementBar(game.getAdvancementBar());
        }
        super.update(deltaTime);
    }

    private boolean isAnimationRunning(){
        return animationRunning;
    }

    public void gameOver(boolean hasWon){
        animationRunning = true;
        player.die();
        ((ICRogueRoom) getCurrentArea()).finished(hasWon);
    }

    @Override
    public void end() {
    }

    @Override
    public String getTitle() {
        return "ICRogue";
    }
}