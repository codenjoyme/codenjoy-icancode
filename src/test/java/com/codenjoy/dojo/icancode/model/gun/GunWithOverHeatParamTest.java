package com.codenjoy.dojo.icancode.model.gun;

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
import com.codenjoy.dojo.icancode.services.GameSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.codenjoy.dojo.icancode.services.GameSettings.Keys.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GunWithOverHeatParamTest {

    public static final String SYMBOL_FIRE = "f";
    public static final String SYMBOL_PAUSE = "-";

    private Gun gun;
    private GameSettings settings;
    
    @Parameterized.Parameter
    public Params params;
    
    @Before
    public void before() {
        settings = new TestGameSettings();
        gun = new GunWithOverHeat(){{
            settings = GunWithOverHeatParamTest.this.settings;
        }};
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Params> data() {
        return Arrays.asList(
                new Params(
                        "1. should shoot without recharge, pause and rest",
                        50,
                        "ffffffffffffffffffffffffffffffffffffffffffffffffff",
                        0,
                        0,
                        0),

                new Params(
                        "2. should make delay for recharge",
                        50,
                        "f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-f-",
                        1,
                        0,
                        0),

                new Params(
                        "3. should shoot without recharge and take a rest after long queue",
                        50,
                        "ffffffffff-----ffffffffff-----ffffffffff-----fffff",
                        0,
                        5,
                        10),

                new Params(
                        "4. should shoot with recharge and take a rest after long queue",
                        60,
                        "f---f---f---f---f----------f---f---f---f---f----------f---f-",
                        3,
                        10,
                        5),

                new Params(
                        "5. should shoot with recharge and ignore rest after long queue",
                        50,
                        "f-----f-----f-----f-----f-----f-----f-----f-----f-",
                        5,
                        3,
                        5));
    }

    @Test
    public void shouldMakeCorrectShotsInDifferentCases() {
        settings.integer(GUN_RECHARGE, params.gunRecharge);
        settings.integer(GUN_REST_TIME, params.gunRestTime);
        settings.integer(GUN_SHOT_QUEUE, params.gunShotQueue);

        String result = new String();
        for (int i = 0; i < params.shots; i++) {
            if (gun.canShoot()) {
                gun.shoot();
                result += SYMBOL_FIRE;
            } else {
                result += SYMBOL_PAUSE;
            }
            gun.tick();
        }
        assertEquals(params.expected, result);
    }

    static class Params {
        String name;
        int shots;
        String expected;
        int gunRecharge;
        int gunRestTime;
        int gunShotQueue;

        public Params(String name, int shots, String expected, int gunRecharge, int gunRestTime, int gunShotQueue) {
            this.name = name;
            this.shots = shots;
            this.expected = expected;
            this.gunRecharge = gunRecharge;
            this.gunRestTime = gunRestTime;
            this.gunShotQueue = gunShotQueue;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}