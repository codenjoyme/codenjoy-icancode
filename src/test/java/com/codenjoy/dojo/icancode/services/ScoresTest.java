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


import com.codenjoy.dojo.icancode.TestGameSettings;
import com.codenjoy.dojo.services.event.EventObject;
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.utils.scorestest.AbstractScoresTest;
import org.junit.Test;

import static com.codenjoy.dojo.icancode.services.GameSettings.Keys.*;

public class ScoresTest  extends AbstractScoresTest {

    @Override
    public GameSettings settings() {
        return new TestGameSettings();
    }

    @Override
    protected Class<? extends ScoresMap> scores() {
        return Scores.class;
    }

    @Override
    protected Class<? extends EventObject> events() {
        return Event.class;
    }

    @Override
    protected Class<? extends Enum> eventTypes() {
        return Event.Type.class;
    }

    @Test
    public void shouldCollectScores_whenSingle() {
        assertEvents("100:\n" +
                "WIN,false > +25 = 125\n" +
                "WIN,false > +25 = 150\n" +
                "WIN,false > +25 = 175\n" +
                "WIN,false > +25 = 200\n" +
                "LOSE,false > -5 = 195");
    }

    @Test
    public void shouldDontCollectScores_whenMultiple() {
        assertEvents("100:\n" +
                "WIN,true > +0 = 100\n" +
                "WIN,true > +0 = 100\n" +
                "WIN,true > +0 = 100\n" +
                "WIN,true > +0 = 100");
    }

    @Test
    public void shouldCollectGoldScores_whenWin() {
        // given
        settings.integer(WIN_SCORE, 25)
                .integer(GOLD_SCORE, 10);

        // when then
        assertEvents("100:\n" +
                "WIN,0,false > +25 = 125\n" +
                "WIN,1,false > +35 = 160\n" +
                "WIN,2,false > +45 = 205");
    }

    @Test
    public void shouldCollectScores_whenLose() {
        // given
        settings.integer(LOSE_PENALTY, -5);

        // when then
        assertEvents("100:\n" +
                "LOSE,false > -5 = 95\n" +
                "LOSE,false > -5 = 90");
    }

    @Test
    public void shouldStillZeroAfterDead() {
        assertEvents("0:\n" +
                "LOSE,false > +0 = 0");
    }

    @Test
    public void shouldClearScore() {
        assertEvents("100:\n" +
                "WIN,false > +25 = 125\n" +
                "(CLEAN) > -125 = 0\n" +
                "WIN,false > +25 = 25");
    }

    @Test
    public void shouldNotCountZombieKill_whenSingle() {
        // given
        settings.bool(ENABLE_KILL_SCORE, true)
                .integer(KILL_ZOMBIE_SCORE, 5);

        // when then
        assertEvents("100:\n" +
                "KILL_ZOMBIE,2,false > +0 = 100\n" +
                "KILL_HERO,3,false > +0 = 100");
    }

    @Test
    public void shouldNotCountKills_whenDisabledKillsScore() {
        // given
        settings.bool(ENABLE_KILL_SCORE, false)
                .integer(KILL_ZOMBIE_SCORE, 5)
                .integer(KILL_HERO_SCORE, 10);

        // when then
        assertEvents("100:\n" +
                "KILL_ZOMBIE,2,true > +0 = 100\n" +
                "KILL_HERO,3,true > +0 = 100");
    }

    @Test
    public void shouldCountKills_whenEnabledKillsScore() {
        // given
        settings.bool(ENABLE_KILL_SCORE, true)
                .integer(KILL_ZOMBIE_SCORE, 5)
                .integer(KILL_HERO_SCORE, 10);

        assertEvents("100:\n" +
                "KILL_ZOMBIE,2,true > +10 = 110\n" +
                "KILL_HERO,3,true > +30 = 140");
    }
}