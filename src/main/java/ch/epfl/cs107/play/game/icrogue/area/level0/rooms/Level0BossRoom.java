package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Boss;
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
public class Level0BossRoom extends Level0EnemyRoom{
    private List<Enemy> enemies;
    public Level0BossRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        enemies = new ArrayList<>(Arrays.asList(
                new Boss(this, Orientation.DOWN, new DiscreteCoordinates(5, 5)),
                new Turret(this, Orientation.DOWN, new DiscreteCoordinates(1, 8)),
                new Turret(this, Orientation.DOWN, new DiscreteCoordinates(8, 1))
        )); // This is the list of enemies in the Level0TurretRoom
        setEnemies(enemies);
    }

    public void update(float deltaTime) {
        super.update(deltaTime);
        if (((Boss)enemies.get(0)).isAttacking()) {
            System.out.println("Boss is attacking");
            addEnemy(new Turret(this, Orientation.DOWN, enemies.get(0).getCurrentCells().get(0).jump((int) (2*Math.random()-1), (int) (2*Math.random()-1))));
            ((Boss)enemies.get(0)).doneAttacking();
        }
    }
}