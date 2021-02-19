package com.codenjoy.dojo.icancode.model;

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


import com.codenjoy.dojo.icancode.model.gun.Gun;
import com.codenjoy.dojo.icancode.model.gun.GunWithOverHeat;
import com.codenjoy.dojo.icancode.model.items.*;
import com.codenjoy.dojo.icancode.model.items.perks.*;
import com.codenjoy.dojo.icancode.services.CodeSaver;
import com.codenjoy.dojo.icancode.services.SettingsWrapper;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Hero extends PlayerHero<Field> implements State<Elements, Player> {

    private boolean alive;
    private boolean win;
    private Direction direction;
    private boolean jump;
    private boolean pull;
    private boolean flying;
    private boolean reset;
    private boolean laser;
    private boolean fire;
    private boolean hole;
    private boolean landOn;
    private List<Gold> gold;
    private int killZombieCount;
    private int killHeroCount;
    private HeroItem item;
    private Gun gun;
    private List<Perk> perks;

    public boolean isLandOn() {
        return landOn;
    }

    public void removeFromCell() {
        item.removeFromCell();
    }

    public FieldItem getItem() {
        return item;
    }

    public Hero(Elements el) {
        item = new HeroItem(el);
        item.init(this);
        gun = new GunWithOverHeat();
        resetFlags();
    }

    private void resetFlags() {
        direction = null;
        win = false;
        jump = false;
        fire = false;
        pull = false;
        landOn = false;
        reset = false;
        flying = false;
        laser = false;
        alive = true;
        gold = new LinkedList<>();
        resetZombieKillCount();
        resetHeroKillCount();
        perks = new ArrayList<>();
        gun.reset();
    }

    public void resetZombieKillCount() {
        killZombieCount = 0;
    }

    public void resetHeroKillCount() {
        killHeroCount = 0;
    }

    @Override
    public void init(Field field) {
        super.init(field);
        item.setField(field);
        reset(field);
    }

    private void reset(Field field) {
        field.getStartPosition().add(this.item);
        field.reset();
        resetFlags();
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        if (player.getHero() == this || Arrays.asList(alsoAtPoint).contains(player.getHero().item)) {
            if (flying) {
                return Elements.ROBO_FLYING;
            }
            if (laser) {
                return Elements.ROBO_LASER;
            }
            if (hole) {
                return Elements.ROBO_FALLING;
            }
            return Elements.ROBO;
        } else {
            if (flying) {
                return Elements.ROBO_OTHER_FLYING;
            }
            if (laser) {
                return Elements.ROBO_OTHER_LASER;
            }

            if (hole) {
                return Elements.ROBO_OTHER_FALLING;
            }
            return Elements.ROBO_OTHER;
        }
    }

    @Override
    public void down() {
        if (!alive || flying) {
            return;
        }
        direction = Direction.DOWN;
    }

    @Override
    public void up() {
        if (!alive || flying) {
            return;
        }
        direction = Direction.UP;
    }

    @Override
    public void left() {
        if (!alive || flying) {
            return;
        }
        direction = Direction.LEFT;
    }

    @Override
    public void right() {
        if (!alive || flying) {
            return;
        }
        direction = Direction.RIGHT;
    }

    public void reset() {
        reset = true;
    }

    public void jump() {
        if (!canJump() || flying) {
            return;
        }

        jump = true;
    }

    public void pull() {
        if (!canMoveBoxes() || flying) {
            return;
        }

        pull = true;
    }

    public boolean canFire() {
        return SettingsWrapper.data.canFire() || has(FirePerk.class);
    }

    public boolean canJump() {
        return SettingsWrapper.data.canJump() || has(JumpPerk.class);
    }

    public boolean canMoveBoxes() {
        return SettingsWrapper.data.canMoveBoxes() || has(MoveBoxesPerk.class);
    }

    public void fire() {
        if (!canFire() || flying) {
            return;
        }

        fire = true;
    }

    public void nextLevel() {
        Cell to = field.getEndPosition();
        field.move(item, to);
    }

    @Override
    public void act(int... p) {
        if (!alive || flying) {
            return;
        }

        var is = new Act(p);
        if (is.act() || is.act(1)) {
            jump();
        } else if (is.act(2)) {
            pull();
        } else if (is.act(3)) { // TODO test me
            fire();
        } else if (is.act(0)) {
            reset();
        } else if (is.act(-1)) { // TODO test me
            nextLevel();
        }
    }

    @Override
    public void message(String command) {
        try {
            String[] parts = command.split("\\|\\$\\%\\&\\|");
            String user = parts[0];
            long date = Long.valueOf(parts[1]);
            int index = Integer.valueOf(parts[2]);
            int count = Integer.valueOf(parts[3]);
            String part = parts[4];
            CodeSaver.save(user, date, index, count, part);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void tick() {
        laser = false;
        hole = false;
        if (!alive) {
            return;
        }

        if (reset) {
            reset = false;
            reset(field);
            return;
        }

        if (flying) {
            flying = false;
            landOn = true;
        }

        if (jump) {
            flying = true;
            jump = false;
        }

        if (fire && direction != null) {
            if (has(UnlimitedFirePerk.class)) {
                field.fire(direction, item.getCell(), item);
                gun.unlimitedShoot();
            } else if (gun.canShoot()) {
                field.fire(direction, item.getCell(), item);
                gun.shoot();
            }
            fire = false;
            direction = null;
        }

        perks = perks.stream()
                .peek(Perk::tick)
                .filter(Perk::isActive)
                .collect(toList());

        if (direction != null) {
            Cell from = item.getCell();
            Point to = direction.change(from);

            if (flying && (field.isAt(to, Box.class)
                    || field.isAt(to, LaserMachine.class)))
            {
                Point next = direction.change(to);
                if (!field.isBarrier(next)) {
                    field.move(item, to);
                }
            } else {
                if (pull && !landOn) { // TODO test part && !landOn
                    if (tryPushBox(to)) {
                        pull = false;
                    }
                }

                if (field.isBarrier(to)) {
                    if (landOn) { // TODO test landOn
                        item.getCell().comeIn(item);
                    }
                } else {
                    if (pull) {
                        if (tryPullBox(from)) {
                            pull = false;
                        }
                    }

                    field.move(item, to);

                    if (!flying) {
                        field.perkAt(to).ifPresent(perk -> {
                            perk.removeFromCell();
                            perks.add(perk);
                        });
                    }
                }
            }
        }
        if (!flying) {
            direction = null;
        }
        landOn = false;
        pull = false;
        gun.tick();
    }

    public void fixLayer() {
        if (flying) {
            item.getCell().jump(item);
        } else {
            item.getCell().landOn(item);
        }
    }

    private boolean tryPullBox(Point pt) {
        Point box = direction.inverted().change(pt);

        Item item = field.getIf(Box.class, box);
        if (item == null) {
            return false;
        }

        if (field.isAt(pt, Start.class)) {
            return false;
        }

        field.move(item, pt);
        return true;
    }

    private boolean tryPushBox(Point pt) {
        Item item = field.getIf(Box.class, pt);

        if (item == null) {
            return false;
        }

        Point to = direction.change(pt);

        if (field.isBarrier(to)) {
            return false;
        }

        if (field.isAt(to, HeroItem.class)) {
            return false;
        }

        if (field.isAt(to, Zombie.class)) {
            return false;
        }

        if (field.isAt(to, Start.class)) {
            return false;
        }

        Gold gold = (Gold) field.getIf(Gold.class, to);
        if (gold != null) {
            return false;
        }

        field.move(item, to);
        return true;
    }

    public Point getPosition() {
        return item.getCell();
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setWin() {
        win = true;
    }

    public void die() {
        alive = false;
    }

    public boolean isWin() {
        return win;
    }

    public void pickUp(Gold item) {
        gold.add(item);
    }

    public void addZombieKill() {
        killZombieCount++;
    }

    public void addHeroKill() {
        killHeroCount++;
    }

    public int getGoldCount() {
        return gold.size();
    }

    public int getKillZombieCount() {
        return killZombieCount;
    }

    public int getKillHeroCount() {
        return killHeroCount;
    }

    public boolean isFlying() {
        return flying;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isPull() {
        return pull;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isFire() {
        return fire;
    }

    public void dieOnHole() {
        hole = true;
        die();
    }

    public void dieOnLaser() {
        laser = true;
        die();
    }

    public void dieOnZombie() {
        laser = true; // TODO может сделать зеленым его?
        die();
    }

    public boolean has(Class<? extends Perk> perkClass) {
        return perks.stream()
                .anyMatch(perk -> perk.getClass().equals(perkClass));
    }

    public List<Gold> gold() {
        return gold;
    }
}
