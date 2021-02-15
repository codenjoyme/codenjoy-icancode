package com.codenjoy.dojo.icancode.model.gun;

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

import com.codenjoy.dojo.icancode.services.SettingsWrapper;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GunWithOverHeatUniqTest {

    public static final String SYMBOL_FIRE = "f";
    public static final String SYMBOL_PAUSE = "-";
    private Gun gun;

    @Before
    public void setup() {
        SettingsWrapper.setup(new SettingsImpl())
                .gunRecharge(1)
                .gunRestTime(10)
                .gunShotQueue(10);
        gun = new GunWithOverHeat();
    }

    @Test
    public void shouldMakeCorrectShotsWithLittleRest_betweenShotsLongerThanRecharge_case1() {
        String result = new String();

        // when shot 3 times with recharge
        for (int i = 0; i < 5; i++) {
            result += getResultOfAttemptShot();
        }

        // then
        assertEquals("f-f-f", result);

        // when don't shoot for 4 ticks. The gun must be a little cool down
        for (int i = 0; i < 5; i++) {
            gun.tick();
            result += SYMBOL_PAUSE;
        }

        // then
        assertEquals("f-f-f-----", result);

        // when make fire queue with 9 shots with
        // recharge and rest after it. And then full 10 shots queue
        for (int i = 0; i < 50; i++) result += getResultOfAttemptShot();
        //then
        assertEquals("f-f-f-----f-f-f-f-f-f-f-f-f----------f-f-f-f-f-f-f-f-f-f----", result);
    }

    @Test
    public void shouldMakeCorrectShotsWithLittleRest_betweenShotsLongerThanRecharge_case2() {
        String result = new String();

        // when shot 7 times with recharge
        for (int i = 0; i < 13; i++) {
            result += getResultOfAttemptShot();
        }

        // then
        assertEquals("f-f-f-f-f-f-f", result);

        // when don't shoot for 5 ticks. The gun must be a little cool down
        for (int i = 0; i < 5; i++) {
            gun.tick();
            result += SYMBOL_PAUSE;
        }

        // then
        assertEquals("f-f-f-f-f-f-f-----", result);

        // when make fire queue with 5 shots with recharge
        // and rest after it. And then full 10 shots queue after rest
        for (int i = 0; i < 51; i++) {
            result += getResultOfAttemptShot();
        }

        // then
        assertEquals("f-f-f-f-f-f-f-----f-f-f-f-f----------f-f-f-f-f-f-f-f-f-f----------f-f", result);
    }

    private String getResultOfAttemptShot() {
        if (gun.isCanShoot()) {
            gun.shoot();
            gun.tick();
            return SYMBOL_FIRE;
        }

        gun.tick();
        return SYMBOL_PAUSE;
    }
}