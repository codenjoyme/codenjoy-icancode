package com.codenjoy.dojo.icancode.services;

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


import com.codenjoy.dojo.services.event.DoubleValueEvent;

public class Event extends DoubleValueEvent<Event.Type, Integer, Boolean> {

    public enum Type {
        WIN,
        LOSE,
        KILL_ZOMBIE,
        KILL_HERO;
    }

    public Event(Type type, int count, boolean multiple) {
        super(type, count, multiple);
    }

    public Event(Type type, boolean multiple) {
        super(type, 0, multiple);
    }

    public boolean isMultiple() {
        return value2();
    }

    public int goldCount() {
        return value();
    }

    public int killCount() {
        return value();
    }

    @Override
    public String toString() {
        String multiplayer = isMultiple() ? "multiple" : "single";

        String count;
        if (type() == Type.LOSE) {
            count = "";
        } else {
            if (type() == Type.WIN) {
                count = "gold=" + goldCount();
            } else {
                count = "kill=" + killCount();
            }
            count += ", ";
        }

        return String.format("%s(%s%s)", type(), count, multiplayer);
    }
}
