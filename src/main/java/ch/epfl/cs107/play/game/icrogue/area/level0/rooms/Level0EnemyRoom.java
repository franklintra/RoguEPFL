package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class Level0EnemyRoom extends Level0Room{
    private List<Enemy> enemies = new ArrayList<>();

    protected void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public Level0EnemyRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    @Override
    protected void createArea() {
        super.createArea();
        for (Enemy enemy : enemies) {
            registerActor(enemy);
        }
    }

    public void addEnemy(Enemy enemy){
        enemies.add(enemy);
        registerActor(enemy);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                unregisterActor(enemy);
            }
            else {
                enemy.update(deltaTime);
            }
        }
    }

    /**
     * @return (int) the number of enemies in the room
     */
    public int getNumberOfEnemies() {
        return enemies.size();
    }

    /**
     * @return (int) the number of enemies in the room that have been killed
     */
    public int getNumberOfDeadEnemies() {
        int numberOfDeadEnemies = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                numberOfDeadEnemies++;
            }
        }
        return numberOfDeadEnemies;
    }

    @Override
    public boolean isOn() {
        return getNumberOfDeadEnemies()==getNumberOfEnemies() && super.isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public float getIntensity() {
        //implement intensity system to know how much the room has been explored
        return ((float) getNumberOfDeadEnemies())/((float) getNumberOfEnemies());
    }
}
