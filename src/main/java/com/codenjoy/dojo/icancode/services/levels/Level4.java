package com.codenjoy.dojo.icancode.services.levels;

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

import java.util.List;

public class Level4 implements Level {
    
    @Override
    public String help() {
        return "Try to solve it by adding new IF. Now it should be easy !<br><br>\n" +

                "You can use this method to show data in console:<br>\n" +
                "<pre>var someVariable = \"someData\";\n" +
                "robot.log(someVariable);</pre>\n" +

                "Be careful ! The program should work for all previous levels too.";
    }

    @Override
    public String winCode() {
        return "function program(robot) {\n" +
                "    var scanner = robot.getScanner();\n" +
                "    if (robot.cameFrom() != null) {\n" +
                "        robot.go(robot.previousDirection());\n" +
                "    } else {\n" +
                "        if (scanner.atRight() != \"WALL\") {\n" +
                "            robot.goRight();\n" +
                "        } else if (scanner.atDown() != \"WALL\") {\n" +
                "            robot.goDown();\n" +
                "        } else if (scanner.atLeft() != \"WALL\") {\n" +
                "            robot.goLeft();\n" +
                "        } else {\n" +
                "            robot.goUp();\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @Override
    public String map() {
        return  "        \n" +
                "   ###  \n" +
                "   #E#  \n" +
                "   #.#  \n" +
                "   #.#  \n" +
                "   #S#  \n" +
                "   ###  \n" +
                "        \n";
    }

    @Override
    public String autocomplete() {
        return "{" +
                "    'robot.':{" +
                "	     'synonyms':[]," +
                "	     'values':['log()']" +
                "    }" +
                "}";
    }

    @Override
    public List<String> befungeCommands() {
        return Level.extendBefunge(new Level3(), "robot-up");
    }
}
