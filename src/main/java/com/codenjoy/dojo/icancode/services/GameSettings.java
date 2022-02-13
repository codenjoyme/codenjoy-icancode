package com.codenjoy.dojo.icancode.services;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
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


import com.codenjoy.dojo.icancode.model.items.ZombieBrain;
import com.codenjoy.dojo.icancode.services.levels.Level;
import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.multiplayer.Mode;
import com.codenjoy.dojo.services.multiplayer.MultiplayerSettings;
import com.codenjoy.dojo.services.settings.PropertiesKey;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.icancode.services.GameRunner.GAME_NAME;
import static com.codenjoy.dojo.icancode.services.GameSettings.Keys.*;

public class GameSettings extends SettingsImpl
        implements SettingsReader<GameSettings>,
                   MultiplayerSettings<GameSettings> {

    public enum Keys implements PropertiesKey {

        IS_TRAINING_MODE,
        VIEW_SIZE,
        LEVELS_COUNT,
        CHEATS,
        GUN_RECHARGE,
        GUN_SHOT_QUEUE,
        GUN_REST_TIME,
        TICKS_PER_NEW_ZOMBIE,
        COUNT_ZOMBIES_ON_MAP,
        WALK_EACH_TICKS,
        DEFAULT_PERKS,
        PERK_DROP_RATIO,
        PERK_AVAILABILITY,
        PERK_ACTIVITY,
        DEATH_RAY_PERK_RANGE,
        WIN_SCORE,
        GOLD_SCORE,
        KILL_ZOMBIE_SCORE,
        ENABLE_KILL_SCORE,
        KILL_HERO_SCORE,
        LOSE_PENALTY;

        private String key;

        Keys() {
            this.key = key(GAME_NAME);
        }

        @Override
        public String key() {
            return key;
        }
    }

    @Override
    public List<Key> allKeys() {
        return Arrays.asList(Keys.values());
    }

    public GameSettings() {
        initMultiplayer(5, Arrays.asList(
                Mode.TRAINING.key(),
                Mode.ALL_SINGLE.key(),
                Mode.ALL_IN_ROOMS.key(),
                Mode.TRAINING_FINAL_IN_ROOMS.key()));

        bool(IS_TRAINING_MODE, true);
        integer(VIEW_SIZE, 20);
        integer(LEVELS_COUNT, 0);
        bool(CHEATS, false);

        integer(GUN_RECHARGE, 2);
        integer(GUN_SHOT_QUEUE, 10);
        integer(GUN_REST_TIME, 10);

        integer(TICKS_PER_NEW_ZOMBIE, 20);
        integer(COUNT_ZOMBIES_ON_MAP, 4);
        integer(WALK_EACH_TICKS, 2);

        string(DEFAULT_PERKS, ",ajm");
        integer(PERK_DROP_RATIO, 50);
        integer(PERK_AVAILABILITY, 10);
        integer(PERK_ACTIVITY, 10);
        integer(DEATH_RAY_PERK_RANGE, 10);

        integer(WIN_SCORE, 25);
        integer(GOLD_SCORE, 10);
        integer(KILL_ZOMBIE_SCORE, 5);
        bool(ENABLE_KILL_SCORE, true);
        integer(KILL_HERO_SCORE, 10);
        integer(LOSE_PENALTY, -5);

        Levels.setup(this);
    }

    public String levelMap(int index) {
        String prefix = levelPrefix(index);
        return string(() -> prefix + "map");
    }

    public ZombieBrain zombieBrain() {
        return new ZombieBrain();
    }

    public GameSettings addLevel(int index, Level level) {
        integer(LEVELS_COUNT, index);

        String prefix = levelPrefix(index);
        multiline(() -> prefix + "map", level.map());
        multiline(() -> prefix + "help", level.help());
        multiline(() -> prefix + "default code", level.defaultCode());
        multiline(() -> prefix + "refactoring code", level.refactoringCode());
        multiline(() -> prefix + "win code", level.winCode());
        multiline(() -> prefix + "autocomplete", level.autocomplete().replace("'", "\""));
        multiline(() -> prefix + "befunge commands", String.join("\n", level.befungeCommands()));
        return this;
    }

    private String levelPrefix(int index) {
        return "Level" + index + " ";
    }

    public Calculator<Event> calculator() {
        return new Calculator<>(new Scores(this));
    }
}