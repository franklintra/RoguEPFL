package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class Level0TurretRoom extends Level0EnemyRoom{
    public Level0TurretRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        List<Enemy> enemies = new ArrayList<>(Arrays.asList(
                new Turret(this, Orientation.UP, new DiscreteCoordinates(1, 8)),
                new Turret(this, Orientation.UP, new DiscreteCoordinates(8, 1))
        )); // This is the list of enemies in the Level0TurretRoom
        setEnemies(enemies);
    }
}