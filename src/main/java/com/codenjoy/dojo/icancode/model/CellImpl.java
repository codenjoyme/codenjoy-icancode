package com.codenjoy.dojo.icancode.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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


import com.codenjoy.dojo.icancode.model.items.Air;
import com.codenjoy.dojo.services.PointImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CellImpl extends PointImpl implements Cell {

    private List<Item> items = new LinkedList<>();

    public CellImpl(int x, int y) {
        super(x, y);
    }

    @Override
    public void add(Item item) {
        item.leaveCell();

        items.add(item);
        item.joinCell(this);
    }

    @Override
    public void comeIn(Item input) {
        items().stream()
                .filter(item -> !item.equals(input))
                // TODO to use dice
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .forEach(item -> {
                    item.action(input);
                    input.action(item);
                });
    }

    @Override
    public boolean passable() {
        return items.stream()
                .allMatch(Item::passable);
    }

    @Override
    public <T extends Item> T item(int layer) {
        return (T) itemsStream(layer)
                .findFirst()
                .orElse(new Air());
    }

    @Override
    public <T extends Item> List<T> items() {
        return (List<T>) items;
    }

    @Override
    public <T extends Item> List<T> items(int layer) {
        return (List<T>) itemsStream(layer)
                .collect(toList());
    }

    private Stream<Item> itemsStream(int layer) {
        return items.stream()
                .filter(item -> item.layer() == layer);
    }

    @Override
    public void remove(Item item) {
        items.remove(item);
    }

    @Override
    public boolean only(int layer, Predicate<Item> predicate) {
        return itemsStream(layer)
                .allMatch(predicate);
    }

    @Override
    public String toString() {
        return String.format("Cell[%s,%s]=%s", x, y, items());
    }

}
