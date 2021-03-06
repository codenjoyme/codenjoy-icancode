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

public class LevelC1 implements Level {
    
    @Override
    public String help() {
        return "In this case, we have Holes. Robot will fall down, if you won’t avoid it.<br>\n" +
                "You can use this method to detect Holes:<br>\n" +
                "<pre>var scanner = robot.getScanner();\n" +
                "if (scanner.at(\"LEFT\") == \"HOLE\") {\n" +
                "    // some statement here\n" +
                "}</pre>\n" +

                "Before you can jump take the \"JUMP_PERK\" near you.<br><br>\n" +

                "And these new methods for jumping through it:<br>\n" +
                "<pre>robot.jumpLeft();\n" +
                "robot.jumpRight();\n" +
                "robot.jumpUp();\n" +
                "robot.jumpDown();\n" +
                "robot.jump(\"LEFT\");</pre>\n" +

                "Also you can add new method to robot by:\n" +
                "<pre>robot.doSmthNew = function(parameter) {\n" +
                "    // some statement here\n" +
                "}</pre>\n" +

                "Remember ! Your program should work for all previous levels too.";
    }

    @Override
    public String defaultCode() {
        return new LevelB4().defaultCode();
    }

    @Override
    public String winCode() {
        return "function program(robot) {\n" +
                "    var scanner = robot.getScanner();\n" +
                "    var dest = destination(scanner);\n" +
                "    var to = scanner.getShortestWay(dest[0])[1];\n" +
                "    var from = scanner.getMe();\n" +
                "\n" +
                "    robot.goOverHole = function(direction) {\n" +
                "        if (scanner.at(direction) != \"HOLE\") {\n" +
                "            robot.go(direction);\n" +
                "        } else {\n" +
                "            robot.jump(direction);\n" +
                "        }\n" +
                "    };\n" +
                "    \n" +
                "    var dx = to.getX() - from.getX(); \n" +
                "    var dy = to.getY() - from.getY(); \n" +
                "    if (dx > 0) {\n" +
                "        robot.goOverHole(\"RIGHT\");\n" +
                "    } else if (dx < 0) {\n" +
                "        robot.goOverHole(\"LEFT\");\n" +
                "    } else if (dy > 0) {\n" +
                "        robot.goOverHole(\"UP\");\n" +
                "    } else if (dy < 0) {\n" +
                "        robot.goOverHole(\"DOWN\");\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function destination(scanner) {\n" +
                "    var result = scanner.getPerks();\n" +
                "    if (result.length > 0) {\n" +
                "        return result;\n" +
                "    }\n" +
                "    result = scanner.getGold();\n" +
                "    if (result.length > 0) {\n" +
                "        return result;\n" +
                "    }\n" +
                "    return scanner.getExit();\n" +
                "}";
    }

    @Override
    public String map() {
        return  "          \n" +
                " ######## \n" +
                " #S.O..$# \n" +
                " #j.....# \n" +
                " ####...# \n" +
                "    #..O# \n" +
                " ####...# \n" +
                " #...O.E# \n" +
                " ######## \n" +
                "          \n";
    }

    @Override
    public String autocomplete() {
        return "{" +
                "	'robot.':{" +
                "		'synonyms':[]," +
                "		'values':['goOverHole()', 'jump()', 'jumpLeft()', 'jumpRight()', 'jumpUp()', 'jumpDown()']" +
                "	}," +
                "	'scanner.':{" +
                "		'synonyms':['robot.getScanner().']," +
                "		'values':['getPerks()']" +
                "	}" +
                "	'.jump(':{" +
                "		'synonyms':[]," +
                "		'values':['\\\'RIGHT\\'', '\\'DOWN\\'', '\\'LEFT\\'', '\\'UP\\'']" +
                "	}," +
                "	' == ':{" +
                "		'synonyms':[' != ']," +
                "		'values':['\\'HOLE\\'']" +
                "	}" +
                "}";
    }

    @Override
    public List<String> befungeCommands() {
        return Level.extendBefunge(new LevelB3(),
                "value-hole",
                "value-jump-perk",
                "robot-jump-left", "robot-jump-right",
                "robot-jump-up", "robot-jump-down",
                "robot-jump");
    }
}
