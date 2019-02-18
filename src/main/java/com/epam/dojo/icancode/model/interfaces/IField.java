package com.epam.dojo.icancode.model.interfaces;

/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 EPAM
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


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.printer.layeredview.LayeredBoardReader;
import com.epam.dojo.icancode.model.Player;
import com.epam.dojo.icancode.model.items.BaseItem;
import com.epam.dojo.icancode.model.items.Zombie;

import java.util.List;

public interface IField extends GameField<Player> {

    boolean isBarrier(int x, int y);

    ICell getStartPosition();

    ICell getEndPosition();

    void move(IItem item, int x, int y);

    ICell getCell(int x, int y);

    IItem getIfPresent(Class<? extends BaseItem> clazz, int x, int y);

    boolean isAt(int x, int y, Class<? extends BaseItem>... clazz);

    void reset();

    boolean isMultiplayer();

    LayeredBoardReader layeredReader();

    Dice dice();

    ILevel getLevel();

    void fire(State owner, Direction direction, Point from);

    int size();

    List<Zombie> zombies();
}
