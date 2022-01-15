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


import com.codenjoy.dojo.client.ElementsMap;
import com.codenjoy.dojo.games.icancode.Element;
import com.codenjoy.dojo.icancode.model.items.perks.PerkUtils;
import com.codenjoy.dojo.icancode.services.GameSettings;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.field.AbstractLevel;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.client.AbstractLayeredBoard.Layers.LAYER1;

public class Level extends AbstractLevel {
    
    private static final ElementsMap<Element> elements = new ElementsMap<>(Element.values());
    
    private Cell[] cells;
    private GameSettings settings;

    public Level(String map, GameSettings settings) {
        super(map);
        cells = new Cell[map.length()];
        this.settings = settings;
        if (size() * size() != map.length()) {
            throw new IllegalArgumentException("map must be square! " + size() + "^2 != " + map.length());
        }

        fillMap(map);
    }

    private void fillMap(String map) {
        int indexChar = 0;

        for (int y = size() - 1; y > -1; --y) {
            for (int x = 0; x < size(); ++x) {

                CellImpl cell = new CellImpl(x, y);
                Element element = elements.get(map.charAt(indexChar));
                BaseItem item = create(element, settings);

                if (element.getLayer() != LAYER1
                    || element == Element.GOLD
                    || PerkUtils.isPerk(element))
                {
                    Element atBottom = Element.FLOOR;
                    cell.add(create(atBottom, settings));
                }

                cell.add(item);
                cells[this.map.xy().length(x, y)] = cell;
                ++indexChar;
            }
        }
    }

    private BaseItem create(Element element, GameSettings settings) {
        BaseItem item = ElementMapper.get(element);
        if (Customizable.class.isAssignableFrom(item.getClass())) {
            ((Customizable)item).init(settings);
        }
        return item;
    }

    public Cell cell(int x, int y) {
        return cells[map.xy().length(x, y)];
    }

    public Cell cell(Point pt) {
        return cell(pt.getX(), pt.getY());
    }

    public Cell[] cells() {
        return cells.clone();
    }

    public boolean isBarrier(Point pt) {
        return pt.isOutOf(size())
                || !cell(pt).passable();
    }

    public <T extends Item> List<T> items(Class clazz) {
        List<T> result = new LinkedList<>();
        List<T> items;

        for (int i = 0; i < cells.length; ++i) {
            items = cells[i].items();

            for (int j = 0; j < items.size(); ++j) {
                if (clazz.isInstance(items.get(j))) {
                    result.add(items.get(j));
                }
            }
        }

        return result;
    }

    public void field(Field field) {
        List<FieldItem> items = items(FieldItem.class);

        for (int i = 0; i < items.size(); ++i) {
            items.get(i).setField(field);
        }
    }
}
