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

import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameSettingsTest {

    @Test
    public void shouldGetAllKeys() {
        assertEquals("IS_TRAINING_MODE     =[Game] Is training mode\n" +
                    "VIEW_SIZE            =[Game] Map view size\n" +
                    "LEVELS_COUNT         =[Game] Levels count\n" +
                    "CHEATS               =[Game] Cheats enabled\n" +
                    "GUN_RECHARGE         =[Gun] Heroes gun recharge\n" +
                    "GUN_SHOT_QUEUE       =[Gun] Heroes gun need to relax after a series of shots\n" +
                    "GUN_REST_TIME        =[Gun] Heroes gun rest time(ticks)\n" +
                    "TICKS_PER_NEW_ZOMBIE =[Zombie] Ticks per new zombie\n" +
                    "COUNT_ZOMBIES_ON_MAP =[Zombie] Count zombies\n" +
                    "WALK_EACH_TICKS      =[Zombie] Zombie walks tick timeout\n" +
                    "DEFAULT_PERKS        =[Perk] Default hero perks on training and contest\n" +
                    "PERK_DROP_RATIO      =[Perk] Drop ratio\n" +
                    "PERK_AVAILABILITY    =[Perk] Availability\n" +
                    "PERK_ACTIVITY        =[Perk] Activity\n" +
                    "DEATH_RAY_PERK_RANGE =[Perk] Death-Ray perk range\n" +
                    "WIN_SCORE            =[Score] Win score\n" +
                    "GOLD_SCORE           =[Score] Gold score\n" +
                    "KILL_ZOMBIE_SCORE    =[Score] Kill zombie score\n" +
                    "ENABLE_KILL_SCORE    =[Score] Enable score for kill\n" +
                    "KILL_HERO_SCORE      =[Score] Kill hero score\n" +
                    "LOSE_PENALTY         =[Score] Lose penalty",
                TestUtils.toString(new GameSettings().allKeys()));
    }
}