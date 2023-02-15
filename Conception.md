
# Conception

The game will print information that can be useful for the developer. One can therefore understand more clearly what is going on thanks to these console logs.
I chose to not print out too much information so that would have been counterproductive.
## Level and Level0

- In order to be able to add the areas properly from ICRogue, a getter for a given area at specific coordinates was introduced in [Level.java](). It is the
  In Level.java
```Java
protected ICRogueRoom getRoom(DiscreteCoordinates roomPosition) {
    return map[roomPosition.x][roomPosition.y]
}
```
In Level0.java
````Java
protected ICRogueRoom getRoom(DiscreteCoordinates roomPosition) {
    return map[roomPosition.x][roomPosition.y]
}
````


These two methods were created to keep the encapsulation and avoid leaking the `ICRogueRoom[][] map` to anyone.
## Random room generation
For the random room generation process, I chose to put the `private MapState[][] generateRandomRoomPlacement` in the [Level.java]() as the blueprint of the map is independant of the type of rooms. However the actual map generation is therefore made in a `protected void generateRandomMap()` method placed in [Level0.java](). This method adds the right room type using the `setRoom()` method in [Level.java]().


- For the random room generation, I chose to introduce an enum roomType in Level0.java with a parameter that can return a new room of this roomType.
- I also introduced a relativeCoords enum in Level.java to be able to check all the rooms next to the one currently being generated.
- I also added lots of intermediate private methods to simplify the comprehension of the `private MapState[][] generateRandomRoomPlacement` method. They return the number of free neighbour cells, a random free neighbour cell and many other useful things.
  The addition of these two enums and these private methods allowed me to keep the code as **clean and concise** as I could by avoiding the use of many if else or switch statements.
## ICRogueRoom
- Level0CherryRoom : I chose to implement the Cherry in all levels even randomly generated ones giving it a purpose. The more cherries the player collects, the faster he can move.

In order to make good looking animations for death and victory, I had to implement these in the [ICRogueRoom.java]() so that when the game is over (`isOn()`) or the player is dead(`isDead()`), the appropriate animation starts.
Therefore all the code at the bottom of this file was designed with this in mind.
## Advancement Bar

If you have not yet noticed, an advancement bar is at the bottom left of your screen indicating how close you are to ending the game.
This advancement bar corresponds to the value of the `Level.getIntensity()` method that calcultes how close you are to solving the level using the `ICRogueRoom.getIntensity` for each room in the level.
This makes it possible for the player to know if he missed a room after having killed the boss, for example if the victory animation doesn't play. It is also a good indicator to know how long you still have to play to finish the game.
## Boss

I decided to make a custom boss for the final challenge of the game. It was much more interesting to make a custom boss than just follow the instruction sets.
Therefore we downloaded on the internet an open source sprite for a boss and implemented its behaviour.

- __How to kill?__
    - The only way to kill a boss is by throwing fireballs at him. Therefore if a player joins the bossRoom without having collected a stick, he is completely doomed!

- __Behaviour__
    - The boss moves randomly throught the room.
    - If the player touches the boss, it will get damaged .3f and the boss will get damaged .01f (negligeable but can be changed to add some more mechanics) every frame
    - It will at random (Math.random) intervalls either move, protect itself or attack.
    - Its attack consists of spawning turrets in a cell next to him that has to be killed to solve the room. This turret will also attack the player!
    - Once it is down to 50hp, it grows by size and the probability that it will spawn a Turret every tick increases!