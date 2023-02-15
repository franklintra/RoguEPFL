package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public abstract class Level0ItemRoom extends Level0Room{
    private final List<Item> items;
    public Level0ItemRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        items = new ArrayList<>();
    }

    /**
     * getter for the items
     * @return (List<Item>) a copy of the list of items
     */
    public List<Item> getItems() {
        return List.copyOf(items);
    }

    /**
     * Add an item to the room
     * @param item
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * @return (int) the number of items in the room that have been picked up
     */
    public int getNumberOfCollectedItems() {
        int numberOfCollectedItems = 0;
        for (Item item : items) {
            if (item.isCollected()) {
                numberOfCollectedItems++;
            }
        }
        return numberOfCollectedItems;
    }

    /**
     * @return (int): the number of items in the room
     */
    public int getNumberOfItems() {
        return items.size();
    }

    @Override
    public boolean isOn() {
        if (getNumberOfCollectedItems() == getNumberOfItems()) {
            return super.isOn();
        }
        return false;
    }


    @Override
    public void createArea() {
        super.createArea();
        for (Item item : items) {
            registerActor(item);
        }
    }

    @Override
    public boolean isOff() {
        return !this.isOn();
    }

    @Override
    public float getIntensity() {
        return ((float) getNumberOfCollectedItems())/((float) getNumberOfItems());
    }
}