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

import com.codenjoy.dojo.icancode.model.items.*;
import com.codenjoy.dojo.icancode.model.perks.*;
import com.codenjoy.dojo.icancode.services.Events;
import com.codenjoy.dojo.icancode.services.Levels;
import com.codenjoy.dojo.icancode.services.SettingsWrapper;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.layeredview.LayeredBoardReader;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ICanCode implements Tickable, Field {

    public static final boolean TRAINING = false;
    public static final boolean CONTEST = true;

    private static final int MAX = 10;
    public static final int MAX_PERCENTS = 100;

    private Dice dice;
    private Level level;

    private List<Player> players;
    private boolean contest;
    private Shooter shooter;

    public ICanCode(Level level, Dice dice, boolean contest) {
        this.level = level;
        level.setField(this);
        this.dice = dice;
        this.contest = contest;
        players = new LinkedList();
        shooter = new Shooter(this);
    }

    @Override
    public void fire(Direction direction, Point from, FieldItem owner) {
        shooter.fire(direction, from, owner);
    }

    int priority(Object o) {
        if (o instanceof HeroItem) return 20;
        if (o instanceof DeathRayPerk) return 13;
        if (o instanceof UnstoppableLaserPerk) return 12;
        if (o instanceof UnlimitedFirePerk) return 11;
        if (o instanceof ZombiePot) return 10;
        if (o instanceof Zombie) return 8;
        if (o instanceof LaserMachine) return 6;
        if (o instanceof Box) return 5;
        if (o instanceof Laser) return 4;
        return 2;
    }

    @Override
    public void tick() {
        level.getItems(HeroItem.class).stream()
                .map(it -> (HeroItem)it)
                .forEach(HeroItem::tick);

        level.getItems(Tickable.class).stream()
                .filter(it -> !(it instanceof HeroItem))
                .sorted((o1, o2) -> Integer.compare(priority(o2), priority(o1)))
                .map(it -> (Tickable)it)
                .forEach(Tickable::tick);

        perks().stream()
                .filter(perk -> !perk.isAvailable())
                .forEach(BaseItem::removeFromCell);

        // после всех перемещений, если герой в полете его надо на 3й леер, иначе приземлить
        level.getItems(HeroItem.class).stream()
                .map(it -> (HeroItem)it)
                .forEach(HeroItem::fixLayer);

        for (Player player : players) {
            Hero hero = player.getHero();
            if (hero.getKillZombieCount() > 0) {
                player.event(Events.KILL_ZOMBIE(hero.getKillZombieCount(), contest));
                hero.resetZombieKillCount();
            }
            if (hero.getKillHeroCount() > 0) {
                player.event(Events.KILL_HERO(hero.getKillHeroCount(), contest));
                hero.resetHeroKillCount();
            }
            if (!hero.isAlive()) {
                player.event(Events.LOOSE());
            } else if (hero.isWin()) {
                player.event(Events.WIN(hero.getGoldCount(), contest));
                hero.die();
            }
        }
    }

    @Override
    public int size() {
        return level.getSize();
    }

    @Override
    public List<Zombie> zombies() {
        return level.getItems(Zombie.class);
    }

    @Override
    public List<Laser> lasers() {
        return level.getItems(Laser.class);
    }

    @Override
    public List<Gold> pickedGold() {
        return getHeroes().stream()
                    .flatMap(hero -> hero.gold().stream())
                    .collect(toList());
    }

    @Override
    public List<LaserMachine> laserMachines() {
        return level.getItems(LaserMachine.class);
    }

    @Override
    public List<ZombiePot> zombiePots() {
        return level.getItems(ZombiePot.class);
    }

    @Override
    public List<Floor> floor() {
        return level.getItems(Floor.class);
    }

    @Override
    public List<AbstractPerk> perks() {
        return level.getItems(AbstractPerk.class);
    }

    @Override
    public boolean isBarrier(int x, int y) {
        return level.isBarrier(x, y);
    }

    @Override
    public Cell getStartPosition() {
        List<Item> items = level.getItems(Start.class);
        int index = dice.next(items.size());
        return items.get(index).getCell();
    }

    @Override
    public Cell getEndPosition() {
        return level.getItems(Exit.class).get(0).getCell();
    }

    @Override
    public void move(Item item, int x, int y) {
        Cell cell = level.getCell(x, y);
        cell.add(item);
        cell.comeIn(item);
    }

    @Override
    public Optional<AbstractPerk> pickPerk(int x, int y) {
        Cell cell = level.getCell(x, y);
        return perks().stream()
                .filter(perk -> perk.getCell().equals(cell))
                .findAny();
    }

    @Override
    public Cell getCell(int x, int y) {
        return level.getCell(x, y);
    }

    @Override
    // TODO to make cast inside
    public Item getIfPresent(Class<? extends BaseItem> clazz, int x, int y) {
        for (Item item : getCell(x, y).items()) {
            if (item.getClass().equals(clazz)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean isAt(int x, int y, Class<? extends BaseItem>... classes) {
        for (Class clazz : classes) {
            if (getIfPresent(clazz, x, y) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        List<Gold> gold = pickedGold();

        if (contest) {
            setRandomGold(gold);
        } else {
            gold.forEach(Gold::reset);
        }

        if (!contest) {
            // TODO test me
            zombiePots().forEach(ZombiePot::reset);

            // TODO test me
            laserMachines().forEach(LaserMachine::reset);
        }
    }

    @Override
    public boolean isContest() {
        return contest;
    }

    private void setRandomGold(List<Gold> gold) {
        List<Floor> floor = floor().stream()
                .filter(item -> item.getCell().items().size() == 1)
                .collect(toList());

        if (floor.isEmpty() && !gold.isEmpty()) {
            System.out.println("Try solve this");
            return;
        }

        for (Gold item : gold) {
            int random = dice.next(floor.size());

            Floor place = floor.get(random);
            floor.remove(random);

            place.getCell().add(item);
        }
    }

    public List<Hero> getHeroes() {
        List<Hero> result = new LinkedList();
        for (Player player : players) {
            result.add(player.getHero());
        }
        return result;
    }

    @Override
    public Optional<AbstractPerk> dropNextPerk() {
        if (dice.next(MAX_PERCENTS) > SettingsWrapper.data.perkDropRatio()) {
            return Optional.empty();
        }

        return PerkUtils.random(dice);
    }

    @Override
    public void dropPickedGold(Hero hero) {
        Cell heroCell = hero.getItem().getCell();
        List<Cell> cells = neighbors(heroCell);
        List<Gold> gold = hero.gold();
        while (!gold.isEmpty() && !cells.isEmpty()) {
            int random = dice.next(cells.size());
            Cell place = cells.remove(random);

            if (place.isOutOf(size()) || !isAvailable(place)) {
                continue;
            }

            place.add(gold.remove(0));
        }

        if (!gold.isEmpty()) {
            setRandomGold(gold);
            gold.clear();
        }
    }

    // TODO refactoring needed
    private List<Cell> neighbors(Cell cell) {
        return new LinkedList<>(){{
            add(cell);
            add(getCell(cell.getX() - 1, cell.getY()));
            add(getCell(cell.getX() + 1, cell.getY()));

            add(getCell(cell.getX(), cell.getY() + 1));
            add(getCell(cell.getX() - 1, cell.getY() + 1));
            add(getCell(cell.getX() + 1, cell.getY() + 1));

            add(getCell(cell.getX(), cell.getY() - 1));
            add(getCell(cell.getX() - 1, cell.getY() - 1));
            add(getCell(cell.getX() + 1, cell.getY() - 1));
        }};
    }

    // TODO refactoring needed
    private boolean isAvailable(Cell cell) {
        Predicate<Item> floor = item -> item instanceof Floor;
        Predicate<Item> air = item -> item instanceof Air;
        Predicate<Item> deathHero = item -> item instanceof HeroItem && !((HeroItem) item).getHero().isAlive();
        return cell.items(0).stream().allMatch(floor)
                && cell.items(1).stream().allMatch(air.or(deathHero))
                && cell.items(2).stream().allMatch(air);
    }

    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    public void remove(Player player) {
        players.remove(player);
        Hero hero = player.getHero();
        if (hero != null) {
            hero.removeFromCell();
        }
    }


    @Override
    public BoardReader reader() {
        return new BoardReader() {
            @Override
            public int size() {
                return ICanCode.this.size();
            }

            @Override
            public Iterable<? extends Point> elements() {
                return null; // because layeredReader() implemented here
            }
        };
    }

    @Override
    public LayeredBoardReader layeredReader() {
        return new LayeredBoardReader() {
            @Override
            public int size() {
                return ICanCode.this.size();
            }

            @Override
            public int viewSize() {
                return Levels.size();
            }

            @Override
            public BiFunction<Integer, Integer, State> elements() {
                Cell[] cells = ICanCode.this.level.getCells();
                return (index, layer) -> cells[index].item(layer);
            }

            @Override
            public Point viewCenter(Object player) {
                return ((Player)player).getHero().getPosition();
            }

            @Override
            public Object[] itemsInSameCell(State item, int layer) {
                return ((Item) item).getItemsInSameCell(layer).toArray();
            }
        };
    }

    @Override
    public Dice dice() {
        return dice;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
