package com.codenjoy.dojo.icancode.model.perks;

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

import com.codenjoy.dojo.icancode.model.AbstractGameTest;
import com.codenjoy.dojo.icancode.model.ICanCode;
import com.codenjoy.dojo.icancode.model.Level;
import com.codenjoy.dojo.icancode.model.items.Floor;
import com.codenjoy.dojo.icancode.model.items.Gold;
import com.codenjoy.dojo.icancode.model.items.ZombiePot;
import com.codenjoy.dojo.icancode.model.items.perks.DeathRayPerk;
import com.codenjoy.dojo.icancode.model.items.perks.Perk;
import com.codenjoy.dojo.icancode.model.items.perks.UnlimitedFirePerk;
import com.codenjoy.dojo.icancode.model.items.perks.UnstoppableLaserPerk;
import org.junit.Test;

import java.util.Optional;

import static com.codenjoy.dojo.icancode.model.Elements.*;
import static com.codenjoy.dojo.icancode.model.ICanCode.TRAINING;
import static com.codenjoy.dojo.services.Direction.UP;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PerkTest extends AbstractGameTest {

    @Test
    public void perkAppear_afterZombieDie() {
        // given
        ZombiePot.TICKS_PER_NEW_ZOMBIE = 4;
        givenZombie().thenReturn(UP);

        givenFl("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║....│" +
                "║.Z..│" +
                "└────┘");

        generateMale();
        game.tick();
        game.tick();
        game.tick();
        game.tick();

        assertE("------" +
                "--☺---" +
                "------" +
                "------" +
                "--♂---" +
                "------");

        // when
        hero.down();
        hero.fire();
        game.tick();

        assertE("------" +
                "--☺---" +
                "--↓---" +
                "------" +
                "--♂---" +
                "------");

        // then
        game.tick();

        assertE("------" +
                "--☺---" +
                "------" +
                "--✝---" +
                "------" +
                "------");

        // then
        game.tick();

        assertL("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║.l..│" +
                "║.Z..│" +
                "└────┘");
    }

    @Test
    public void heroTakesPerk() {
        // given
        givenFl("╔════┐" +
                "║.S..│" +
                "║.l..│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        hero.down();
        game.tick();

        // then
        assertE("------" +
                "------" +
                "--☺---" +
                "------" +
                "------" +
                "------");

        assertL("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║....│" +
                "║....│" +
                "└────┘");

        has(UnstoppableLaserPerk.class);
    }

    @Test
    public void perkAvailabilityTest() {
        // given
        settings.perkAvailability(3);

        givenFl("╔════┐" +
                "║.S..│" +
                "║.l..│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        game.tick();

        // then
        assertL("╔════┐" +
                "║.S..│" +
                "║.l..│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        game.tick();

        // then
        assertL("╔════┐" +
                "║.S..│" +
                "║.l..│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        game.tick();

        // then
        assertL("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║....│" +
                "║....│" +
                "└────┘");
    }

    @Test
    public void perkActivityTest() {
        // given
        settings.perkActivity(3);

        givenFl("╔════┐" +
                "║.S..│" +
                "║.l..│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        hero.down();
        game.tick();

        assertE("------" +
                "------" +
                "--☺---" +
                "------" +
                "------" +
                "------");

        assertL("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║....│" +
                "║....│" +
                "└────┘");

        has(UnstoppableLaserPerk.class);

        // then
        game.tick();

        has(UnstoppableLaserPerk.class);

        game.tick();

        has(UnstoppableLaserPerk.class);

        game.tick();

        hasNot(UnstoppableLaserPerk.class);
    }

    @Test
    public void doNotDropNextPerk() {
        // given
        game = new ICanCode(mock(Level.class), dice, TRAINING);
        settings.perkDropRatio(0);
        dice(100);

        // when
        Optional<Perk> nextPerk = game.dropNextPerk();

        // then
        assertFalse(nextPerk.isPresent());
    }

    @Test
    public void doDropNextPerk() {
        // given
        game = new ICanCode(mock(Level.class), dice, TRAINING);
        dice(0, 1);

        // when
        Optional<Perk> nextPerk = game.dropNextPerk();

        // then
        assertTrue(nextPerk.isPresent());
    }

    @Test
    public void firePerkShouldBeOnBoard() {
        //given
        givenFl("╔════┐" +
                "║Sa..│" +
                "║....│" +
                "║....│" +
                "║....│" +
                "└────┘");

        // when
        game.tick();

        // then
        assertE("------" +
                "-☺----" +
                "------" +
                "------" +
                "------" +
                "------");
    }

    @Test
    public void pickPerk() {
        // given
        givenFl("╔════┐" +
                "║.S..│" +
                "║....│" +
                "║....│" +
                "║.l..│" +
                "└────┘");

        // when then
        assertTrue(game.perkAt(pt(2, 1)).isPresent());
        assertFalse(game.perkAt(pt(2, 4)).isPresent());
    }

    @Test
    public void perksOnBoard() {
        // given
        givenFl("╔═════════┐" +
                "║Srlf.rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        // when
        game.tick();

        // then
        assertEquals(6, game.perks().size());

        assertL("╔═════════┐" +
                "║Srlf.rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        assertE("-----------" +
                "-☺---------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");
    }

    @Test
    public void shouldDrawFloorUnderPerks_whenGotIt() {
        // given
        perksOnBoard();

        assertL("╔═════════┐" +
                "║Srlf.rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        // when
        hero.right();
        game.tick();

        // then
        assertL("╔═════════┐" +
                "║S.lf.rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        assertE("-----------" +
                "--☺--------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        // when
        hero.right();
        game.tick();

        // then
        assertL("╔═════════┐" +
                "║S..f.rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        assertE("-----------" +
                "---☺-------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        // when
        hero.right();
        game.tick();

        // then
        assertL("╔═════════┐" +
                "║S....rlf.│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        assertE("-----------" +
                "----☺------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        // when
        hero.right();
        game.tick();

        hero.right();
        game.tick();

        hero.right();
        game.tick();

        hero.right();
        game.tick();

        // then
        assertL("╔═════════┐" +
                "║S........│" +
                "└─────────┘" +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           " +
                "           ");

        assertE("-----------" +
                "--------☺--" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");
    }

    @Test
    public void goldState_withUnstoppableLaserPerk() {
        assertEquals(UNSTOPPABLE_LASER_PERK,
                new Gold().state(player, new UnstoppableLaserPerk()));
    }

    @Test
    public void goldState_withDeathRayPerk() {
        assertEquals(DEATH_RAY_PERK,
                new Gold().state(player, new DeathRayPerk()));
    }

    @Test
    public void goldState_withUnlimitedFirePerk() {
        assertEquals(UNLIMITED_FIRE_PERK,
                new Gold().state(player, new UnlimitedFirePerk()));
    }

    @Test
    public void floorState_withUnstoppableLaserPerk() {
        assertEquals(UNSTOPPABLE_LASER_PERK,
                new Floor().state(player, new UnstoppableLaserPerk()));
    }

    @Test
    public void floorState_withDeathRayPerk() {
        assertEquals(DEATH_RAY_PERK,
                new Floor().state(player, new DeathRayPerk()));
    }

    @Test
    public void floorState_withUnlimitedFirePerk() {
        assertEquals(UNLIMITED_FIRE_PERK,
                new Floor().state(player, new UnlimitedFirePerk()));
    }
}
