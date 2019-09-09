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

var util = require('util');
var WSocket = require('ws');

var log = function(string) {
    console.log(string);
    if (!!printBoardOnTextArea) {
        printLogOnTextArea(string);
    }
};

var printArray = function (array) {
   var result = [];
   for (var index in array) {
       var element = array[index];
       result.push(element.toString());
   }
   return "[" + result + "]";
};

var processBoard = function(boardString) {
    var boardJson = eval(boardString);
    var board = new Board(boardJson);
    if (!!printBoardOnTextArea) {
        printBoardOnTextArea(board.toString());
    }

    var logMessage = board + "\n\n";
    var answer = new DirectionSolver(board).get().toString();
    logMessage += "Answer: " + answer + "\n";
    logMessage += "-----------------------------------\n";
    
    log(logMessage);

    return answer;
};

// you can get this code after registration on the server with your email
var url = "http://codenjoy.com/codenjoy-contest/board/player/5q0uczbju6no468bg6w0?code=1725193577353578184&gameName=icancode";

url = url.replace("http", "ws");
url = url.replace("board/player/", "ws?user=");
url = url.replace("?code=", "&code=");

var ws;

function connect() {
    ws = new WSocket(url);
    log('Opening...');

    ws.on('open', function() {
        log('Web socket client opened ' + url);
    });

    ws.on('close', function() {
        log('Web socket client closed');

        setTimeout(function() {
            connect();
        }, 5000);
    });

    ws.on('message', function(message) {
        var answer = processBoard(message);
        ws.send(answer);
    });
}

connect();

var elements = [];
var elementsTypes = [];
var elementsByChar = {};
var elementsByType = {};

var el = function(char, type, direction) {
    var result = {
        char: char,
        type: type,
        direction: direction
    };

    elementsByChar[char] = result;

    if (!elementsByType[type]) {
        elementsByType[type] = [];
    }

    elementsByType[type].push(result);
    elements.push(result);

    if (elementsTypes.indexOf(type) == -1) {
        elementsTypes.push(type);
    }

    return result;
}

var D = function(index, dx, dy, name){

    var changeX = function(x) {
        return x + dx;
    };

    var changeY = function(y) {
        return y - dy;
    };

    var inverted = function() {
        switch (this) {
            case Direction.UP : return Direction.DOWN;
            case Direction.DOWN : return Direction.UP;
            case Direction.LEFT : return Direction.RIGHT;
            case Direction.RIGHT : return Direction.LEFT;
            default : return Direction.STOP;
        }
    };

    var toString = function() {
        return name;
    };

    return {
        changeX : changeX,

        changeY : changeY,

        inverted : inverted,

        toString : toString,

        getIndex : function() {
            return index;
        }
    };
};

var Direction = {
    UP : D(2, 0, 1, 'up'),                  // you can move
    DOWN : D(3, 0, -1, 'down'),
    LEFT : D(0, -1, 0, 'left'),
    RIGHT : D(1, 1, 0, 'right'),
    JUMP : D(4, 0, 0, 'act(1)'),            // jump
    PULL : D(5, 0, 0, 'act(2)'),            // pull box
    FIRE : D(6, 0, 0, 'act(3)'),            // fire
    STOP : D(5, 0, 0, '')                   // stay
};

Direction.values = function() {
   return [Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.JUMP, Direction.PULL, Direction.FIRE, Direction.STOP];
};

Direction.valueOf = function(index) {
    var directions = Direction.values();
    for (var i in directions) {
        var direction = directions[i];
        if (direction.getIndex() == index) {
             return direction;
        }
    }
    return Direction.STOP;
};

var Element = {
    EMPTY: el('-', 'NONE'),
    FLOOR: el('.', 'NONE'),

    ANGLE_IN_LEFT: el('╔', 'WALL'),
    WALL_FRONT: el('═', 'WALL'),
    ANGLE_IN_RIGHT: el('┐', 'WALL'),
    WALL_RIGHT: el('│', 'WALL'),
    ANGLE_BACK_RIGHT: el('┘', 'WALL'),
    WALL_BACK: el('─', 'WALL'),
    ANGLE_BACK_LEFT: el('└', 'WALL'),
    WALL_LEFT: el('║', 'WALL'),
    WALL_BACK_ANGLE_LEFT: el('┌', 'WALL'),
    WALL_BACK_ANGLE_RIGHT: el('╗', 'WALL'),
    ANGLE_OUT_RIGHT: el('╝', 'WALL'),
    ANGLE_OUT_LEFT: el('╚', 'WALL'),
    SPACE: el(' ', 'WALL'),

    LASER_MACHINE_CHARGING_LEFT: el('˂', 'LASER_MACHINE', Direction.LEFT),
    LASER_MACHINE_CHARGING_RIGHT: el('˃', 'LASER_MACHINE', Direction.RIGHT),
    LASER_MACHINE_CHARGING_UP: el('˄', 'LASER_MACHINE', Direction.UP),
    LASER_MACHINE_CHARGING_DOWN: el('˅', 'LASER_MACHINE', Direction.DOWN),

    LASER_MACHINE_READY_LEFT: el('◄', 'LASER_MACHINE_READY', Direction.LEFT),
    LASER_MACHINE_READY_RIGHT: el('►', 'LASER_MACHINE_READY', Direction.RIGHT),
    LASER_MACHINE_READY_UP: el('▲', 'LASER_MACHINE_READY', Direction.UP),
    LASER_MACHINE_READY_DOWN: el('▼', 'LASER_MACHINE_READY', Direction.DOWN),

    START: el('S', 'START'),
    EXIT: el('E', 'EXIT'),
    HOLE: el('O', 'HOLE'),
    BOX: el('B', 'BOX'),
    ZOMBIE_START: el('Z', 'ZOMBIE_START'),
    GOLD: el('$', 'GOLD'),

    ROBOT: el('☺', 'MY_ROBOT'),
    ROBOT_FALLING: el('o', 'MY_ROBOT'),
    ROBOT_FLYING: el('*', 'MY_ROBOT'),
    ROBOT_LASER: el('☻', 'MY_ROBOT'),

    ROBOT_OTHER: el('X', 'OTHER_ROBOT'),
    ROBOT_OTHER_FALLING: el('x', 'OTHER_ROBOT'),
    ROBOT_OTHER_FLYING: el('^', 'OTHER_ROBOT'),
    ROBOT_OTHER_LASER: el('&', 'OTHER_ROBOT'),

    LASER_LEFT: el('←', 'LASER_LEFT', Direction.LEFT),
    LASER_RIGHT: el('→', 'LASER_RIGHT', Direction.RIGHT),
    LASER_UP: el('↑', 'LASER_UP', Direction.UP),
    LASER_DOWN: el('↓', 'LASER_DOWN', Direction.DOWN),

    FEMALE_ZOMBIE: el('♀', 'ZOMBIE'),
    MALE_ZOMBIE: el('♂', 'ZOMBIE'),
    ZOMBIE_DIE: el('✝', 'ZOMBIE_DIE'),

    getElements: function () {
        return elements.slice(0);
    },

    getElement: function (char) {
        var el = elementsByChar[char];
        if (!el) {
            throw "Element not found for: " + char;
        }
        return el;
    },

    getElementsTypes: function () {
        var elements = [];
        elementsTypes.forEach(function(e) {
            if (Array.isArray(e)) {
                elements = elements.concat(e);
            } else {
                elements.push(e);
            }
        });

        var result = [];
        elements.forEach(function(e) {
            if (result.indexOf(e) < 0) {
                result.push(e);
            }
        });

        return result;
    },

    getElementsOfType: function (type) {
        return elementsByType[type];
    }

};

var Point = function (x, y, direction) {
    return {
        x: x,
        y: y,
        direction: direction,

        equals: function (o) {
            return o.getX() == x && o.getY() == y;
        },

        toString: function () {
            return '[' + x + ',' + y + (!!direction ? (',' + direction) : '') + ']';
        },

        isBad: function (boardSize) {
            return x >= boardSize || y >= boardSize || x < 0 || y < 0;
        },

        getX: function () {
            return x;
        },

        getY: function () {
            return y;
        },

        move: function (dx, dy) {
            x += dx;
            y += dy;
        }
    }
};

var pt = function (x, y) {
    return new Point(x, y);
};

var LAYER1 = 0;
var LAYER2 = 1;
var LAYER3 = 2;

var LengthToXY = function (boardSize) {
    var inversion = function (y) {
        return boardSize - 1 - y;
    }

    return {
        getXY: function (length) {
            if (length == -1) {
                return null;
            }
            return new Point(length % boardSize, Math.trunc(length / boardSize));
        },

        getLength: function (x, y) {
            return inversion(y) * boardSize + x;
        }
    };
};

var Board = function (board) {    
    var layersString = board.layers;
    var scannerOffset = board.offset;
    var heroPosition = board.heroPosition;
    var levelFinished = board.levelFinished;
    var size = Math.sqrt(layersString[LAYER1].length);
    var xyl = new LengthToXY(size);

    var parseLayer = function (layer) {
        var map = [];
        for (var x = 0; x < size; x++) {
            map[x] = [];
            for (var y = 0; y < size; y++) {
                map[x][y] = Element.getElement(layer.charAt(xyl.getLength(x, y)))
            }
        }
        return map;
    }

    var layers = [];
    for (var index in layersString) {
        layers.push(parseLayer(layersString[index]));
    }

    var isAt = function (x, y, layer, element) {
        if (pt(x, y).isBad(size) || getAt(x, y, layer) == null) {
            return false;
        }
        return getAt(x, y, layer).char == element.char;
    };

    var getAt = function (x, y, layer) {
        return layers[layer][x][y];
    };

    var findAll = function (element, layer) {
        var result = [];
        for (var x = 0; x < size; x++) {
            for (var y = 0; y < size; y++) {
                if (isAt(x, y, layer, element)) {
                    result.push(new Point(x, y));
                }
            }
        }
        return result;
    };

    var removeAllElements = function(array, element) {
        var index;
        while ((index = array.indexOf(element)) !== -1) {
            array.splice(index, 1);
        }
        return array;
    }

    var getWholeBoard = function() {
        var result = [];
        for (var x = 0; x < size; x++) {
            var arr = [];
            result.push(arr);
            for (var y = 0; y < size; y++) {
                var cell = [];
                cell.push(getAt(x, y, LAYER1).type);
                cell.push(getAt(x, y, LAYER2).type);
                cell.push(getAt(x, y, LAYER3).type);
                removeAllElements(cell, 'NONE');
                if (cell.length == 0) {
                    cell.push('NONE');
                }

                arr.push(cell);
            }
        }
        return result;
    }

    var findAllElements = function (elements, layer) {
        var result = [];
        for (var x = 0; x < size; x++) {
            for (var y = 0; y < size; y++) {
                for (var e in elements) {
                    var element = elements[e];
                    if (isAt(x, y, layer, element)) {
                        result.push(element.direction ? new Point(x, y, element.direction.name()) : new Point(x, y));
                    }
                }
            }
        }
        return result;
    };

    var isAnyOfAt = function (x, y, layer, elements) {
        for (var index in elements) {
            var element = elements[index];
            if (isAt(x, y, layer, element)) {
                return true;
            }
        }
        return false;
    };

    var isNear = function (x, y, layer, element) {
        if (pt(x, y).isBad(size)) {
            return false;
        }
        return isAt(x + 1, y, layer, element) || isAt(x - 1, y, layer, element)
            || isAt(x, y + 1, layer, element) || isAt(x, y - 1, layer, element);
    };

    var isBarrierAt = function (x, y) {
        if (!barriersMap) {
            getBarriers();
        }
        return barriersMap[x][y];
    };

    var isWallAt = function (x, y) {
        return getAt(x, y, LAYER1).type == 'WALL';
    };

    var countNear = function (x, y, layer, element) {
        if (pt(x, y).isBad(size)) {
            return 0;
        }
        var count = 0;
        if (isAt(x - 1, y, layer, element)) count++;
        if (isAt(x + 1, y, layer, element)) count++;
        if (isAt(x, y - 1, layer, element)) count++;
        if (isAt(x, y + 1, layer, element)) count++;
        return count;
    };

    var getOtherHeroes = function () {
        var elements = [Element.ROBOT_OTHER, Element.ROBOT_OTHER_FALLING, Element.ROBOT_OTHER_LASER];
        return findAllElements(elements, LAYER2)
            .concat(findAll(Element.ROBOT_OTHER_FLYING, LAYER3));
    };

    var getLaserMachines = function () {
        var elements = [Element.LASER_MACHINE_CHARGING_LEFT, Element.LASER_MACHINE_CHARGING_RIGHT,
            Element.LASER_MACHINE_CHARGING_UP, Element.LASER_MACHINE_CHARGING_DOWN,
            Element.LASER_MACHINE_READY_LEFT, Element.LASER_MACHINE_READY_RIGHT,
            Element.LASER_MACHINE_READY_UP, Element.LASER_MACHINE_READY_DOWN];
        return findAllElements(elements, LAYER1);
    };

    var getLasers = function () {
        var elements = [Element.LASER_LEFT, Element.LASER_RIGHT,
            Element.LASER_UP, Element.LASER_DOWN];
        return findAllElements(elements, LAYER2);
    };

    var getWalls = function () {
        var elements = [Element.ANGLE_IN_LEFT, Element.WALL_FRONT,
            Element.ANGLE_IN_RIGHT, Element.WALL_RIGHT,
            Element.ANGLE_BACK_RIGHT, Element.WALL_BACK,
            Element.ANGLE_BACK_LEFT, Element.WALL_LEFT,
            Element.WALL_BACK_ANGLE_LEFT, Element.WALL_BACK_ANGLE_RIGHT,
            Element.ANGLE_OUT_RIGHT, Element.ANGLE_OUT_LEFT,
            Element.SPACE];
        return findAllElements(elements, LAYER1);
    };

    var getBoxes = function () {
        return findAll(Element.BOX, LAYER2);
    };

    var getStart = function () {
        return findAll(Element.START, LAYER1);
    };

    var getZombieStart = function () {
        return findAll(Element.ZOMBIE_START, LAYER1);
    };

    var getExit = function () {
        return findAll(Element.EXIT, LAYER1);
    };

    var getGold = function () {
        return findAll(Element.GOLD, LAYER1);
    };

    var getHoles = function () {
        return findAll(Element.HOLE, LAYER1);
    };

    var isMyRobotAlive = function () {
        return layersString[LAYER2].indexOf(Element.ROBOT_LASER.char) == -1 &&
            layersString[LAYER2].indexOf(Element.ROBOT_FALLING.char) == -1;
    };

    var barriers = null;
    var barriersMap = null;
    var getBarriers = function () {
        if (!!barriers) {
            return barriers;
        }

        barriers = [];
        barriersMap = Array(size);
        for (var x = 0; x < size; x++) {
            barriersMap[x] = new Array(size);
            for (var y = 0; y < size; y++) {
                var element1 = getAt(x, y, LAYER1);
                var element2 = getAt(x, y, LAYER2);

                barriersMap[x][y] = (
                    element1.type == 'WALL' ||
                    element1 == Element.HOLE ||
                    element2 == Element.BOX ||
                    !!element1.direction
                );

                if (barriersMap[x][y]) {
                    barriers.push(pt(x, y));
                }
            }
        }
        return barriers;
    };

    var getFromArray = function(x, y, array, def) {
        if (x < 0 || y < 0 || x >= size || y >= size) {
            return def;
        }
        return array[x][y];
    }

    var isBarrier = function(x, y) {
        return getFromArray(x, y, barriersMap, true);
    }
    
    var getShortestWay = function (from, to) {
        if (from.getX() == to.getX() && from.getY() == to.getY()) {
            return [from];
        }
        if (!barriersMap) {
            getBarriers();
        }

        var mask = Array(size);
        for (var x = 0; x < size; x++) {
            mask[x] = new Array(size);
            for (var y = 0; y < size; y++) {
                mask[x][y] = (isWallAt(x, y)) ? -1 : 0;
            }
        }

        var getMask = function(x, y) {
            return getFromArray(x, y, mask, -1);
        }

        var current = 1;
        mask[from.getX()][from.getY()] = current;

        var isOutOf = function (x, y) {
            return (x < 0 || y < 0 || x >= size || y >= size);
        }

        var comeRound = function (x, y, onElement) {
            var dd = [[-1, 0], [1, 0], [0, -1], [0, 1]];
            for (var i in dd) {
                var dx = dd[i][0];
                var dy = dd[i][1];

                var xx = x + dx;
                var yy = y + dy;
                if (isOutOf(xx, yy)) continue;

                var stop = !onElement(xx, yy);

                if (stop) return;
            }
        }

        var done = false;
        while (!done) {
            var maskToString = function() {
                var string = '01234567890123456789\n';
                for (var y = 0; y < size; y++) {
                    for (var x = 0; x < size; x++) {
                        if (mask[x][y] == -1) {
                            var s = '*';
                        } else if (mask[x][y] == 0) {
                            if (getAt(x, y, LAYER1) == Element.HOLE) {
                                var s = 'O';
                            } else if (getAt(x, y, LAYER2) == Element.BOX) {
                                var s = 'B';
                            } else if (getAt(x, y, LAYER1) == Element.EXIT) {
                                var s = 'E';
                            } else {
                                var s = ' ';
                            }
                        } else {
                            var s = '' + mask[x][y];
                        }
                        if (s.length == 2) s = s[1];
                        string += s;
                    }
                    string += ' ' + y + '\n';
                }
                string += '01234567890123456789\n';
                console.log(string);
            }
            // s = 8;
            // if (s == -1 || current >= s - 1 && current <= s) maskToString();

            for (var x = 0; x < size; x++) {
                for (var y = 0; y < size; y++) {
                    if (mask[x][y] != current) continue;

                    comeRound(x, y, function (xx, yy) {
                        if (getMask(xx, yy) == 0) {
                            var dx = xx - x;
                            var dy = yy - y;

                            var px = x - dx;
                            var py = y - dy;

                            var fx = xx + dx;
                            var fy = yy + dy;

                            // путь px/py -> x/y -> xx/yy -> fx/fy

                            var can = true;
                            if (isBarrier(xx, yy) && isBarrier(fx, fy)) {
                                can = false;
                            }
                            if (isBarrier(x, y)) {
                                if (getMask(px, py) == -1) {
                                    can = false;
                                }
                                if (isBarrier(xx, yy)) {
                                    can = false;
                                }
                            }
                            // if (s == -1 || current >= s - 1 && current < s) {
                            //     console.log('px/py: ' + px + ' ' + py);
                            //     console.log('x/y: ' + x + ' ' + y);
                            //     console.log('xx/yy: ' + xx + ' ' + yy);
                            //     console.log('fx/fy: ' + fx + ' ' + fy);
                            //     console.log('mask[px][py]: ' + mask[px][py]);
                            //     console.log('isBarrier(px, py): ' + isBarrier(px, py));
                            //     console.log('isBarrier(x, y): ' + isBarrier(x, y));
                            //     console.log('isBarrier(xx, yy): ' + isBarrier(xx, yy));
                            //     console.log('isBarrier(fx, fy): ' + isBarrier(fx, fy));
                            //     console.log(((can) ? '+' : '-') + (current + 1) + ": [" + x + ":" + y + "] -> [" + xx + ":" + yy + "]");
                            // }

                            if (can) {
                                mask[xx][yy] = current + 1;
                                if (xx == to.getX() && yy == to.getY()) {
                                    done = true;
                                }
                            }
                        }
                        return true;
                    });
                }
            }

            current++;
            if (current > 200) {
                return [];
            }
        }
        var point = to;
        done = false;
        current = mask[point.getX()][point.getY()];
        var path = [];
        path.push(point);
        while (!done) {
            comeRound(point.getX(), point.getY(), function (xx, yy) {
                if (mask[xx][yy] == current - 1) {
                    point = pt(xx, yy);
                    current--;

                    path.push(point);

                    if (current == 1) {
                        done = true;
                    }
                    return false;
                }
                return true;
            });
        }

        return path.reverse();
    }

    var boardAsString = function (layer) {
        var result = "";
        for (var i = 0; i <= size - 1; i++) {
            result += layersString[layer].substring(i * size, (i + 1) * size);
            result += "\n";
        }
        return result;
    };

    var getHero = function() {
        return pt(heroPosition.x, heroPosition.y);
    }

    // thanks http://jsfiddle.net/queryj/g109jvxd/
    String.format = function () {
        // The string containing the format items (e.g. "{0}")
        // will and always has to be the first argument.
        var theString = arguments[0];

        // start with the second argument (i = 1)
        for (var i = 1; i < arguments.length; i++) {
            // "gm" = RegEx options for Global search (more than one instance)
            // and for Multiline search
            var regEx = new RegExp("\\{" + (i - 1) + "\\}", "gm");
            theString = theString.replace(regEx, arguments[i]);
        }
    }

    var toString = function () {
        return "Board layer 1:\n" +
            boardAsString(LAYER1) + "\n" +
            "Board layer 2:\n" +
            boardAsString(LAYER2) + "\n" +
            "Board layer 3:\n" +
            boardAsString(LAYER3) + "\n" +
            "Robot at: " + getHero() + "\n" +
            "Other robots at: " + printArray(getOtherHeroes()) + "\n" +
            "LaserMachine at: " + printArray(getLaserMachines()) + "\n" +
            "Laser at: " + printArray(getLasers()) + "";
    };

    return {
        size: function () {
            return size;
        },
        getHero: getHero,
        isLevelFinished: function() {
            return levelFinished;
        },
        getOtherHeroes: getOtherHeroes,
        getLaserMachines: getLaserMachines,
        getLasers: getLasers,
        getWalls: getWalls,
        getBoxes: getBoxes,
        getGold: getGold,
        getStart: getStart,
        getZombieStart: getZombieStart,
        getExit: getExit,
        getHoles: getHoles,
        isMyRobotAlive: isMyRobotAlive,
        isAt: isAt,
        getAt: getAt,
        toString: toString,
        layer1: function () {
            return boardAsString(LAYER1)
        },
        layer2: function () {
            return boardAsString(LAYER2)
        },
        layer3: function () {
            return boardAsString(LAYER3)
        },
        getWholeBoard: getWholeBoard,
        getBarriers: getBarriers,
        findAll: findAll,
        isAnyOfAt: isAnyOfAt,
        isNear: isNear,
        isBarrierAt: isBarrierAt,
        countNear: countNear,
        getShortestWay: getShortestWay,
        getScannerOffset: function () {
            return pt(scannerOffset.x, scannerOffset.y);
        }
    };
};

var random = function (n) {
    return Math.floor(Math.random() * n);
};

var direction;

var DirectionSolver = function(board){

    return {
        /**
         * @return next hero action
         */
        get : function() {
            var hero = board.getHero();

            // TODO your code here

            return Direction.JUMP;
        }
    };
};

