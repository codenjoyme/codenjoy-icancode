package com.codenjoy.dojo.icancode.model.items;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.icancode.model.*;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

public class Laser extends FieldItem implements Tickable {

    private int ticks;
    private Direction direction;
    private State owner;
    private boolean unstoppable;
    private boolean deathRay;
    private boolean die;

    public Laser(Elements element) {
        super(element);
        this.direction = getDirection(element);
        die = false;
    }

    public Laser(State owner, Direction direction, Field field) {
        super(getElement(direction));
        this.owner = owner;
        this.direction = direction;
        this.field = field;
    }

    private static Elements getElement(Direction direction) {
        switch (direction) {
            case LEFT:
                return Elements.LASER_LEFT;
            case RIGHT:
                return Elements.LASER_RIGHT;
            case DOWN:
                return Elements.LASER_DOWN;
            case UP:
                return Elements.LASER_UP;
        }
        throw new IllegalStateException("Unexpected direction: " + direction);
    }

    private static Direction getDirection(Elements element) {
        switch (element) {
            case LASER_LEFT:
                return Direction.LEFT;
            case LASER_RIGHT:
                return Direction.RIGHT;
            case LASER_DOWN:
                return Direction.DOWN;
            case LASER_UP:
                return Direction.UP;
        }
        throw new IllegalStateException("Unexpected element: " + element);
    }

    @Override
    public void action(Item item) {
        HeroItem heroItem = getIf(item, HeroItem.class);
        if (heroItem != null) {
            Hero hero = heroItem.getHero();
            if (!hero.isFlying()) {
                if (shouldLaserAttackHero(hero)) {
                    if (!unstoppable) {
                        die();
                    }
                    hero.dieOnLaser();
                    field.dropTemporaryGold(hero);
                    addOwnerKillHeroScore();
                }
            }
        }

        Zombie zombie = getIf(item, Zombie.class);
        if (zombie != null) {
            if (!unstoppable) {
                die();
            }
            zombie.die();
            addOwnerKillZombieScore();
        }
    }

    private boolean shouldLaserAttackHero(Hero hero) {
        Direction heroDirection = hero.getDirection();
        if (die) {
            return false;
        }
        if (heroDirection == null) {
            return true;
        }
        if (heroDirection == direction.inverted()) {
            return !hero.isLandOn();
        }
        return false;
    }

    private void addOwnerKillZombieScore() {
        if (owner != null && owner instanceof Hero) {
            ((Hero) owner).addZombieKill();
        }
    }

    private void addOwnerKillHeroScore() {
        if (owner != null && owner instanceof Hero) {
            ((Hero) owner).addHeroKill();
        }
    }

    @Override
    public void tick() {
        Cell cell = getCell();
        if (getCell() == null) return; // TODO почему-то тут был NPE
        int newX = direction.changeX(getCell().getX());
        int newY = direction.changeY(getCell().getY());

        if (deathRay && ticks == 0) {
            cell.comeIn(this);
        } else if (deathRay) {
            removeFromCell();
        } else if (!field.isBarrier(newX, newY)) {
            field.move(this, newX, newY);
        } else if (field.isAt(newX, newY, Box.class) && unstoppable) {
            field.move(this, newX, newY);
        } else {
            removeFromCell();
        }

        ticks++;
    }

    public int getTicks() {
        return ticks;
    }

    public State owner() {
        return owner;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isUnstoppable() {
        return unstoppable;
    }

    public void setUnstoppable(boolean unstoppable) {
        this.unstoppable = unstoppable;
    }

    public boolean isDeathRay() {
        return deathRay;
    }

    public void setDeathRay(boolean deathRay) {
        this.deathRay = deathRay;
    }

    public void die() {
        die = true;
        removeFromCell();
    }
}
