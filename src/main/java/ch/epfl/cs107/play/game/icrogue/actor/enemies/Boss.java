package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Transform;

import java.util.Collections;
import java.util.List;

/**
 * @author Franklin Tranié
 * @project projet-2
 */
public class Boss extends Enemy implements Interactor, Interactable {

    private Sprite[][] bossSprite = new Sprite[][]{
            Sprite.extractSprites("boss/IDLE", state.IDLE.getNumberOfFrames(), 2.5f, 2.5f, this, 100, 96),
            Sprite.extractSprites("boss/INVULNERABLE", state.INVULNERABLE.getNumberOfFrames(), 2.5f, 2.5f, this, 100, 96),
            Sprite.extractSprites("boss/IDLE", state.REPRODUCE.getNumberOfFrames(), 2.5f, 2.5f, this, 100, 96),
            Sprite.extractSprites("boss/DYING", state.DYING.getNumberOfFrames(), 2.5f, 2.5f, this, 100, 96)
    };
    private float hp = 100;
    private ICRogueBossInteractionHandler handler = new ICRogueBossInteractionHandler();
    private state currentState = Boss.state.IDLE;
    private boolean canTakeDamage = true;
    private int animationStep = 0;
    private float size = 1;
    private int skipFrame;
    private boolean canReproduce = true;


    private enum state {
        IDLE(4),
        INVULNERABLE(8),
        REPRODUCE(7), //head
        DYING(14);
        private int numberOfFrames = 0;

        state(int numberOfFrames) {
            this.numberOfFrames = numberOfFrames;
        }

        public int getNumberOfFrames() {
            return numberOfFrames;
        }
    }

    public void damage(float damage) {
        if (canTakeDamage) {
            hp -= damage;
            if (hp <= 0) {
                currentState = state.DYING;
                canTakeDamage = false;
            }
        }
    }

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        setSprite(bossSprite[0][0]);
    }

    public Boss(Area area, Orientation orientation, DiscreteCoordinates position, float hp, float size, boolean canReproduce) {
        this(area, orientation, position);
        this.size = size;
        this.hp = hp;
        this.canReproduce = canReproduce;
    }

    private boolean nextAnimationStep(state state) {
        if (animationStep < state.getNumberOfFrames()) {
            Sprite current = bossSprite[state.ordinal()][animationStep];
            current.setRelativeTransform(new Transform(size, 0, -0.75f, 0, size, -0.8f));
            setSprite(current);
            animationStep = (animationStep + 1);
            return true;
        } else {
            currentState = generateRandomBehaviour();
            animationStep = 0;
            System.out.println("Boss is : " + currentState);
            //animationStep = currentState.getNumberOfFrames();
            return false;
        }
    }

    private void nextAnimationDeath() {
        if (animationStep < state.DYING.getNumberOfFrames()) {
            Sprite current = bossSprite[state.DYING.ordinal()][animationStep];
            current.setRelativeTransform(new Transform(size, 0, -0.75f, 0, size, -0.8f));
            setSprite(current);
            animationStep = (animationStep + 1);
        } else {
            getOwnerArea().unregisterActor(this);
        }
    }

    private state generateRandomBehaviour() {
        //return a random behaviour for the boss (attack, idle, invulnerable) having 10x more chance to be idle than the other
        int random = (int) (Math.random() * 100);
        if (random < 80) {
            return Boss.state.IDLE;
        } else if (random < 90) {
            return Boss.state.INVULNERABLE;
        } else {
            return Boss.state.REPRODUCE;
        }
    }

    private Orientation randomOrientation() {
        //choose random orientation between ORIENTATION.DOWN and ORIENTATION.LEFT and ORIENTATION.RIGHT and ORIENTATION.UP
        int random = (int) (Math.random() * 4);
        return Orientation.fromInt(random);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (currentState == state.REPRODUCE && !attacking && skipFrame == state.REPRODUCE.getNumberOfFrames() - 1) {
            reproduce();
        }
        if (skipFrame == 0) {
            if (currentState == state.DYING) {
                nextAnimationDeath();
                skipFrame = 20;
                return;
            }
            nextAnimationStep(currentState);
            orientate(randomOrientation());
            move(60);
            if (currentState == state.IDLE) {
                skipFrame = 5;
            } else {
                skipFrame = 20;
            }
        } else {
            skipFrame--;
        }
    }

    private boolean attacking = false;

    private void reproduce() {
        attacking = canReproduce;
    }

    public void doneAttacking(){
        attacking = false;
    }
    public boolean isAttacking() {
        return attacking;
    }

    @Override
    public void die() {
        nextAnimationDeath();
        if (currentState == state.DYING && animationStep == state.DYING.getNumberOfFrames()) {
            super.die();
        }
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    private class ICRogueBossInteractionHandler implements ICRogueInteractionHandler {
        @Override
        public void interactWith(ICRogueBehavior.ICRogueCell cell, boolean isCellInteraction) {
        }

        @Override
        public void interactWith(ICRoguePlayer player, boolean isCellInteraction) {
            player.hurt(1);
        }

        @Override
        public void interactWith(Turret turret, boolean isCellInteraction) {
        }
    }
}
