
# Rogue-like game for EPFL

This project was made as a semester project for EPFL
It is meant to be a 2d areagame also known as rogue-gam  
Here is the [instructions set][8]
## Authors

- [@franklintra][1]
- [@mguillon][2]



## Deployment

To deploy this project compile it following the [recommandations][3].
You will find in ch.epfl.cs107.play package a [constants.java]() file.
This is used to configure how you want to play the game (random generation or not)
and also has a few other variables used for game updates and or debugging purposes.

We have made a custom animation/easter egg that sadly is too big for the archive. If you could download this [archive][5] and put it in the [src/main/res/animation/][6] and uncompress it it would make the game have one more very cool easter egg. (It should create a wasted subfolder in animation/)
## Keybinds

- __A__: Boost with cherry (cooldown)
- __W__: Interact at a distance with an object, a connector
- __X__: Launch a fireball with staff (cooldown)
- __R__: Reset the game to a default state (this is for debugging / testing purposes)
- __ZQSD / Arrows__: Move the player on the board


## Usage

__ITEMS__
- __Cherry__
  Once you have collected a cherry you can move and press A at the same time to go faster.
  The speed will increase linearly with the number of cherry the player has eaten,
  hence making it possible to have a very over-powered player if there are a lot of cherry rooms.
- __Staff__
  Once you have collected a staff, you will be able to launch fireballs with a cooldown that can be tuned in the [constants.java][7] file.
- __Key__
  The keys all allow the player to enter the boss room. He needs one in order to be able to enter still.

__Enemies__
- __Turret__ A turret is an enemy able to launch arrows in all directions. You can kill it by walking on it or by sending fireballs to it. The arrows are pretty dreadful so be careful.
- __Boss__ A boss is an enemy which only spawns in the boss room and randomly moves. Beware, if you were to touch him the issue would be fatal (you might be able to damage him a very little bit). The only real way to kill it is by sending fireballs to its main cell. He might randomly make a turret spawn every few seconds. Once he is weak, he will grow to twice his size and the turret spawns will increase.

__How to win?__  
The game might be randomly generated or generated using a fixed map. You can still win the game and finish it the same way.  
In order to win the game, you have to have visited every room, having collected all available items and killed all monsters. The turret that the boss creates have to be killed for victory even after the boss is dead. If you were to die in painful circumstances, you might see a cool **Easter Egg**.

[1]:	https://www.github.com/franklintra
[2]:	https://www.github.com/mguillon
[3]:	https://proginsc.epfl.ch/wwwhiver/mini-projet2/setupIntelliJ.pdf
[5]:	https://drive.google.com/drive/folders/1EjPB8lb1fAsxzsrDn60GKfeU3ojPhTh6?usp=sharing
[6]:	src/main/res/animation/
[7]:	#Deployment
[8]:    https://github.com/franklintra/RoguEPFL/blob/main/Instruction%20set.pdf