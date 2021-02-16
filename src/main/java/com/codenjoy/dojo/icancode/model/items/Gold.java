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
import com.codenjoy.dojo.icancode.model.perks.AbstractPerk;

import static com.codenjoy.dojo.services.StateUtils.filterOne;

public class Gold extends BaseItem {

    private Cell dock;

    public Gold(Elements el) {
        super(el);
        dock = null;
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        AbstractPerk perk = filterOne(alsoAtPoint, AbstractPerk.class);
        if (perk != null) {
            return perk.state(player, alsoAtPoint);
        }

        return super.state(player, alsoAtPoint);
    }

    @Override
    public void action(Item item) {
        check(item, HeroItem.class)
                .ifPresent(heroItem -> {
                    Hero hero = heroItem.getHero();
                    if (!hero.isFlying()) {
                        hero.pickUp(this);
                        removeFromCell();
                    }
                });
    }

    @Override
    public void setCell(Cell cell) {
        if (dock == null && cell != null) {
            dock = cell;
        }
        super.setCell(cell);
    }

    public void reset() {
        dock.add(this);
    }

}
