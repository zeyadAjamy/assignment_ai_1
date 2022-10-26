import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Represents a cell in the graph.
 * 
 * @author Zeyad Ajamy
 */
class Cell {
    /**
     * Represents the x coordinate of the cell.
     */
    int x, y;
    /**
     * Represents the content of the cell.
     * Expected values:
     * non: empty cell
     * kraken: kraken cell
     * dutchman: dutchman cell
     * treasure: treasure cell
     * tortuga: tortuga cell
     * jack: jack cell
     * preKraken: one of the kraken's preception cells
     * preDutchman: one of the dutchman's preception cells
     * rock: rock cell
     * kraken&rock: kraken and rock cell
     * kraken&preDutchman: kraken and one of the dutchman's preception cells
     */
    String content;
    /**
     * Represents the state of the cell.
     * 
     * Expected values:
     * rum: for jack if he has rum.
     * safe: safe to move to.
     * intact: healthy kraken cell.
     * immortal: for rock and dutchman and the cells that cannot be moved to.
     */
    String status;
    /**
     * Represents the f in the f = g + h formula where g is the cost of the path
     * from the start to the current cell and h is the heuristic cost of the path
     * from the current cell to the end.
     * 
     * @intialValue Max value of an integer.
     */
    int f = Integer.MAX_VALUE;

    /**
     * Represents the parent of the current cell.
     * 
     * @initialValue null.
     */
    Cell parent = null;

    /**
     * Creates a cell with the given coordinates and content.
     * 
     * @param x       The x coordinate of the cell.
     * @param y       The y coordinate of the cell.
     * @param content The content of the cell.
     * @param status  The status of the cell.
     */

    public Cell(int x, int y, String content, String status) {
        this.x = x;
        this.y = y;
        this.content = content;
        this.status = status;
    }

    /**
     * Gets the number of parents of the current cell.
     * 
     * @return The number of parents of the current cell.
     */
    public int getNumParent() {
        Cell temp = this;
        int count = 0;
        while (temp.parent != null) {
            count++;
            temp = temp.parent;
        }
        return count;
    }

    /**
     * Gets the coordinates of the current cell in the form of [x, y] string.
     * 
     * @return The coordinates of the current cell in the form of [x, y] string.
     */
    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}

/**
 * This program is a solution to the problem of finding the shortest path
 * between two points in a graph using A* and backtracking algorithms.
 * 
 * @author Zeyad Ajamy
 * @version 1.0
 * @since 2022-10-26
 */
public class statistics {

    public static Boolean existInSet(Cell[] closed, int x, int y) {
        for (Cell cell : closed) {
            if (cell != null && cell.x == x && cell.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kill the kraken and remove its preception cells from the graph.
     * 
     * @param map    The graph of type Cell[][].
     * @param kraken The kraken cell of type Cell.
     * @return The graph after killing the kraken.
     */
    public static Cell[][] handlekraken(Cell[][] map, Cell kraken) {
        int x = kraken.x;
        int y = kraken.y;

        // Get all the precpetions of the kraken
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
                    if (!(i == x || j == y))
                        continue; // only horizontal and vertical
                    if (i < 0 || i > 8 || j < 0 || j > 8)
                        continue; // out of bounds
                    if (map[i][j].content.equals("preKraken")) {
                        map[i][j].content = "non";
                        map[i][j].status = "safe";
                    }
                }
            }
        }

        if (kraken.content.equals("kraken")) {
            map[x][y].content = "non";
            map[x][y].status = "safe";
        } else if (kraken.content.equals("kraken&rock")) {
            map[x][y].content = "rock";
            map[x][y].status = "immortal";
        } else if (kraken.content.equals("karaken&preDutchman")) {
            map[x][y].content = "preDutchman";
            map[x][y].status = "immortal";
        }

        return map;
    }

    /**
     * Gets the path from the start to the end cell.
     * 
     * @note this method uses heuristics and backtracking to minimize the time and
     *       space complexity.
     *       however, it is not guaranteed to be the shortest path.
     * @param map        The graph of type Cell[][].
     * @param closed     The list of visited cells of type Set<Cell>.
     * @param target     The end cell of type Cell.
     * @param current    The current cell of type Cell.
     * @param block_list The list of cells that cannot be moved to of type
     *                   Set<Cell>.
     * @param rum        boolean value that determines whether jack has rum or not
     *                   of type boolean.
     * @return The path from the start to the end cell or null if there is no path.
     */
    public static String backtrack(Cell[][] map, Set<Cell> closed, Cell target, Cell current, Set<Cell> block_list,
            boolean rum) {
        // add the current to the closed set
        closed.add(current);

        // find all possible moves from the current cell, at most 8 moves
        ArrayList<Cell> moves = new ArrayList<Cell>();
        int x = current.x;
        int y = current.y;

        boolean kraken_flag = false;
        int kraken_x = 0;
        int kraken_y = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
                    if (i == x && j == y) {
                        continue; // skip the current cell
                    }
                    if (i < 0 || i > 8 || j < 0 || j > 8) {
                        continue; // out of bounds
                    }
                    if (map[i][j].content.equals("kraken") || map[i][j].content.equals("kraken&rock")
                            || map[i][j].content.equals("kraken&preDutchman")) {
                        kraken_flag = true;
                        kraken_x = i;
                        kraken_y = j;
                    }
                    moves.add(map[i][j]);
                }
            }
        }

        // if the kraken exists, handle it
        if (kraken_flag && rum) {
            map = handlekraken(map, map[kraken_x][kraken_y]);
            // update the moves list
            moves = new ArrayList<Cell>();
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
                        if (i == x && j == y) {
                            continue; // skip the current cell
                        }
                        if (i < 0 || i > 8 || j < 0 || j > 8) {
                            continue; // out of bounds
                        }
                        moves.add(map[i][j]);
                    }
                }
            }
        }
        // find the optimal move from the possible moves
        Cell optimal = null;
        int min_hueristic = Integer.MAX_VALUE;

        // Optimal list
        for (Cell move : moves) {
            if ((move.content.equals("non") || move.content.equals("tortuga") || move.content.equals(target.content))
                    && !existInSet(closed.toArray(new Cell[closed.size()]), move.x, move.y)) {
                int dx = Math.abs(move.x - target.x);
                int dy = Math.abs(move.y - target.y);
                if (optimal == null) {
                    min_hueristic = dx + dy;
                    optimal = move;
                } else {
                    int d = dx + dy;
                    if (d < min_hueristic) {
                        min_hueristic = d;
                        optimal = move;
                    }
                }
            }
        }
        // add all the moves to the closed list except the optimal one
        for (Cell move : moves) {
            if (move != optimal) {
                closed.add(move);
            }
        }

        // if the optimal move is null, then we have reached the end of the path
        if (optimal == null) {
            // Remove all the moves from the closed list except the optimal one and those in
            // the block list
            for (Cell move : moves) {
                if (move != optimal && !block_list.contains(move)) {
                    closed.remove(move);
                }
            }

            // Backtrack using the parent of the current cell
            if (current.parent != null) {
                return backtrack(map, closed, target, current.parent, block_list, rum);
            } else {
                return null; // no path found
            }

        } else {
            // if the optimal is not our target, then continue the search
            if (!optimal.content.equals(target.content)) {
                // add the current to the parent of the optimal move
                optimal.parent = current;

                // add the optimal move to the block list
                block_list.add(optimal);

                // continue the search
                return backtrack(map, closed, target, optimal, block_list, rum);
            } else {
                // trace all the parents of the optimal move to get the path and add these cells
                // to arraylist
                ArrayList<Cell> path = new ArrayList<Cell>();
                // Add the optimal move to the path
                path.add(optimal);
                path.add(current);

                // Add the rest of the parents to the path
                Cell temp = current;
                while (temp.parent != null) {
                    path.add(temp.parent);
                    temp = temp.parent;
                }
                // reverse the path
                Collections.reverse(path);
                // convert it to a string
                String path_str = "";
                for (Cell cell : path) {
                    path_str += cell.toString() + " ";
                }
                // return the path
                return path_str.trim();
            }
        }
    }

    /**
     * Gets the cell of the given x, y coordinates, if it exists in the given set.
     * 
     * @param searchingSet The set of cells to search in of type ArrayList<Cell>.
     * @param x            The x coordinate of the cell of type int.
     * @param y            The y coordinate of the cell of type int.
     * @return True if the cell exists in the closed set, false otherwise.
     */
    public static Cell getCellFromSet(ArrayList<Cell> searchingSet, int x, int y) {
        for (Cell c : searchingSet) {
            if (c.x == x && c.y == y) {
                return c;
            }
        }
        return null;
    }

    /**
     * Calculates the cell huristic value.
     * 
     * @param map      The map of the game.
     * @param x        The x coordinate of the cell of type int.
     * @param y        The y coordinate of the cell of type int.
     * @param treasure The treasure cell of type Cell.
     * @return The huristic value of the cell.
     */
    public static int calculateHeuristic(Cell[][] map, int x, int y, Cell treasure) {
        int xTreasure = treasure.x;
        int yTreasure = treasure.y;
        int xDiff = Math.abs(xTreasure - x);
        int yDiff = Math.abs(yTreasure - y);
        int max = Math.max(xDiff, yDiff);
        return max;
    }

    /**
     * Applies the A* algorithm to the given map. to find the optimal path to the
     * given target.
     * 
     * @param map      The map of the game of type Cell[][].
     * @param jack     The jack cell of type Cell.
     * @param treasure The treasure cell of type Cell.
     * @param x        The x coordinate of the cell of type int.
     * @param y        The y coordinate of the cell of type int.
     * @param open     The open set of type Set<Cell>.
     * @param closed   The closed set of type Set<Cell>.
     * @param rum      The rum flag of type boolean.
     * @param variant  The variant of the algorithm of type int.
     * @return The optimal path to the target, or null if no path is found.
     */
    public static String aStar(Cell[][] map, Cell jack, Cell treasure, int x, int y, Set<Cell> open, Set<Cell> closed,
            boolean rum, int variant) {
        
        // add the current cell to the closed list
        ArrayList<Cell> openSet = new ArrayList<Cell>(open);
        ArrayList<Cell> closedSet = new ArrayList<Cell>(closed);
        Cell[] array = new Cell[closed.size()];
        Cell[] array2 = new Cell[open.size()];
        // Check all the neighbors of the current cell
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Check if the neighbor is valid
                if (i >= 0 && i < 9 && j >= 0 && j < 9) {
                    // Check if the neighbor is not in the closed list
                    if (!existInSet(closedSet.toArray(array), i, j)) {

                        String content = map[i][j].content;
                        int h = calculateHeuristic(map, i, j, treasure);
                        int g = map[x][y].getNumParent() + 1;
                        int f = h + g;

                        if (existInSet(openSet.toArray(array2), i, j) && map[i][j].parent != null) {
                            Cell old = getCellFromSet(openSet, i, j);
                            if (old.f > f) {
                                old.f = f;
                                map[i][j].parent = map[x][y];
                            }
                        } else {
                            map[i][j].f = f;
                            map[i][j].parent = map[x][y];
                        }

                        if (!existInSet(openSet.toArray(array2), i, j)
                                && (content.equals("non") || content.equals("treasure"))) {
                            open.add(map[i][j]);
                        } else if (content.equals("tortuga")) {
                            open.add(map[i][j]);
                            rum = true;
                        } else if (content.equals("kraken")) {
                            if (rum) {
                                map[i][j].content = "non";
                                map[i][j].status = "killed";
                                open.add(map[i][j]);
                                // Remove the kraken from the map and it's preceptions zone.
                                Set<Cell> neighbors = new HashSet<>();
                                for (int k = i - 1; k <= i + 1; k++) {
                                    for (int l = j - 1; l <= j + 1; l++) {
                                        if (!(k == i || l == j))
                                            continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8)
                                            continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed
                                        for (Cell c : closed) {
                                            if (c.x == k && c.y == l) {
                                                closed.remove(c);
                                                break;
                                            }
                                        }

                                    }
                                }

                                // If one of the neighbors of the kraken is the neighbor of the current cell,
                                // then we have to add it to the open set
                                for (Cell neighbor : neighbors) {
                                    for (int k = x - 1; k <= x + 1; k++) {
                                        for (int l = y - 1; l <= y + 1; l++) {
                                            if (k == neighbor.x && l == neighbor.y) {
                                                map[k][l].parent = map[x][y];
                                                int h1 = calculateHeuristic(map, neighbor.x, neighbor.y, treasure);
                                                int g1 = map[k][l].getNumParent() + 1;
                                                int f1 = h1 + g1;
                                                map[k][l].f = f1;
                                                open.add(map[k][l]);
                                            }
                                        }
                                    }
                                }

                                closed.add(map[x][y]);
                            }
                        } else if (content.equals("kraken&rock")) {
                            if (rum) {
                                map[i][j].content = "rock";
                                map[i][j].status = "immortal";
                                // Remove the kraken from the map and it's preceptions zone.
                                Set<Cell> neighbors = new HashSet<>();
                                for (int k = i - 1; k <= i + 1; k++) {
                                    for (int l = j - 1; l <= j + 1; l++) {
                                        if (!(k == i || l == j))
                                            continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8)
                                            continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed
                                        for (Cell c : closed) {
                                            if (c.x == k && c.y == l) {
                                                closed.remove(c);
                                                break;
                                            }
                                        }

                                    }
                                }

                                // If one of the neighbors of the kraken is the neighbor of the current cell,
                                // then we have to add it to the open set
                                for (Cell neighbor : neighbors) {
                                    for (int k = x - 1; k <= x + 1; k++) {
                                        for (int l = y - 1; l <= y + 1; l++) {
                                            if (k == neighbor.x && l == neighbor.y) {
                                                map[k][l].parent = map[x][y];
                                                int h1 = calculateHeuristic(map, neighbor.x, neighbor.y, treasure);
                                                int g1 = map[k][l].getNumParent() + 1;
                                                int f1 = h1 + g1;
                                                map[k][l].f = f1;
                                                open.add(map[k][l]);
                                            }
                                        }
                                    }
                                }

                                closed.add(map[x][y]);
                                closed.add(map[i][j]);
                            }
                        } else if (content.equals("kraken&preDutchman")) {
                            if (rum) {
                                map[x][y].content = "preDutchman";
                                map[x][y].status = "immortal";

                                // Remove the kraken from the map and it's preceptions zone.
                                Set<Cell> neighbors = new HashSet<>();
                                for (int k = i - 1; k <= i + 1; k++) {
                                    for (int l = j - 1; l <= j + 1; l++) {
                                        if (!(k == i || l == j))
                                            continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8)
                                            continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed
                                        for (Cell c : closed) {
                                            if (c.x == k && c.y == l) {
                                                closed.remove(c);
                                                break;
                                            }
                                        }

                                    }
                                }
                                closed.add(map[x][y]);
                                closed.add(map[i][j]);

                            }
                        } else if (!(content.equals("non") || content.equals("treasure"))) {
                            closed.add(map[i][j]);
                        }
                    }
                }
            }
        }

        if (variant == 2) {
            // Will add some more preceptions to jack
            ArrayList<Cell> extraPreceptions = new ArrayList<>();
            if (x + 2 < 9) {
                extraPreceptions.add(map[x + 2][y]);
            }
            if (x - 2 >= 0) {
                extraPreceptions.add(map[x - 2][y]);
            }
            if (y + 2 < 9) {
                extraPreceptions.add(map[x][y + 2]);
            }
            if (y - 2 >= 0) {
                extraPreceptions.add(map[x][y - 2]);
            }

            for (Cell cell : extraPreceptions) {
                if (existInSet(array, x, y)) {
                    continue;
                }
                String content = cell.content;
                if (content.equals("non") || content.equals("treasure") || content.equals("tortuga")) {
                    open.add(cell);
                }
            }
        }

        // Convert the Set to an array
        openSet = new ArrayList<Cell>(open);
        ArrayList<Cell> closedArray = new ArrayList<Cell>(closed);

        for (int i = 0; i < closedArray.size(); i++) {
            for (int j = 0; j < openSet.size(); j++) {
                if (closedArray.get(i).x == openSet.get(j).x && closedArray.get(i).y == openSet.get(j).y) {
                    open.remove(openSet.get(j));
                }
            }
        }

        // Find the next cell to move to
        int min = 100000;
        Cell next = null;
        for (int i = 0; i < openSet.size(); i++) {
            if (openSet.get(i).f < min) {
                min = openSet.get(i).f;
                next = openSet.get(i);
            }
        }

        // If next is the Turtoga, change rum to true
        if (next != null && next.content.equals("tortuga")) {
            rum = true;
        }
        // If the kraken in the way and the rum is not found yet return null
        if (next == null) {
            return null;
        }
        // Add the next cell to the closed set
        closed.add(next);

        // Remove the current cell from the open set and add it to the closed set
        for (int i = 0; i < openSet.size(); i++) {
            if (openSet.get(i).x == x && openSet.get(i).y == y) {
                openSet.remove(i);
            }
        }

        // Update the current cell
        open = new HashSet<Cell>(openSet);

        // If the next cell is the treasure, we're done :)
        if (next.content.equals(treasure.content)) {
            // Get all its parents' coordinates
            ArrayList<Cell> path = new ArrayList<Cell>();
            Cell current = next;
            while (current.parent != null) {
                path.add(current);
                current = current.parent;
            }
            // reverse the path
            Collections.reverse(path);

            // print the path
            String pathString = "";
            for (int i = 0; i < path.size(); i++) {
                pathString += "[" + path.get(i).x + "," + path.get(i).y + "] ";
            }
            return pathString.trim();
        } else {
            // otherwise, move to the next cell
            return aStar(map, jack, treasure, next.x, next.y, open, closed, rum, variant);
        }
    }

    /**
     * Prints out a string to specific file
     * 
     * @param output   the string to be printed out. Type String
     * @param fileName the name of the file. Type String.
     * @throws IOException
     */
    public static void printOutput(String output, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(output);
        writer.close();
    }

    /**
     * Add the kraken to the map and it's preceptions zone.
     * 
     * @param map the map. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the kraken is allowed to be added according to the task,
     *         false otherwise.
     */
    public static boolean addKraken(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8)
            return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "kraken";
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y))
                        continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8)
                        continue;
                    if (map[i][j].content.equals("non") || map[i][j].content.equals("jack")) {
                        map[i][j].content = "preKraken";
                    }
                }
            }

            return true;
        } else if (map[x][y].content.equals("rock")) {
            map[x][y].content = "kraken&rock";
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y))
                        continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8)
                        continue;
                    if (map[i][j].content.equals("non") || map[i][j].content.equals("jack") ) {
                        map[i][j].content = "preKraken";
                    }
                }
            }
            return true;
        } else if (map[x][y].content.equals("preDutchman")) {
            map[x][y].content = "kraken&preDutchman";
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y))
                        continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8)
                        continue;
                    if (map[i][j].content.equals("non") || map[i][j].content.equals("jack") ) {
                        map[i][j].content = "preKraken";
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Add the Dutchman to the map and it's preceptions zone.
     * 
     * @param map the map to be added. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the Dutchman is allowed to be added according to the task,
     *         false otherwise.
     */
    public static boolean flyingDutchman(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8)
            return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "dutchman";
            // Add preceptions zone
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i == x && j == y)
                        continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8)
                        continue;
                    if (map[i][j].content.equals("non") || map[i][j].content.equals("jack") || map[i][j].content.equals("preKraken")) {
                        map[i][j].content = "preDutchman";
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Add the rock to the map.
     * 
     * @param map the map to be added. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the rock is allowed to be added according to the task, false
     *         otherwise.
     */
    public static boolean addRock(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8)
            return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "rock";
        } else if (map[x][y].content.equals("kraken")) {
            map[x][y].content = "kraken&rock";
        } else if (map[x][y].content.equals("preDutchman")) {
            map[x][y].content = "preDutchman";
            map[x][y].status = "immortal";

        } else if (map[x][y].content.equals("preKraken")) {
            map[x][y].content = "rock";
            map[x][y].status = "immortal";
        }
        return true;
    }

    /**
     * Add the tortuga to the map.
     * 
     * @param map the map to be added. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the tortuga is allowed to be added according to the task,
     *         false otherwise.
     */
    public static boolean addTortuga(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8)
            return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "tortuga";
            return true;
        } else if (map[x][y].content.equals("jack")) {
            map[x][y].content = "jack";
            map[x][y].status = "rum";
            return true;
        }
        return false;
    }

    /**
     * Add the tresure cell to the map.
     * 
     * @param map the map to be added. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the tresure is allowed to be added according to the task,
     *         false otherwise.
     */
    public static boolean addTreasure(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8)
            return false;
        if (x == 0 && y == 0)
            return false; // cannot be in the origin
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "treasure";
            return true;
        }
        return false;
    }

    /**
     * Add the jack to the map.
     * 
     * @param map the map to be added. Type Cell[][]
     * @param x   the x coordinate of the cell to be added. Type int
     * @param y   the y coordinate of the cell to be added. Type int
     * @return true if the jack is allowed to be added according to the task, false
     *         otherwise.
     */
    public static boolean addJack(Cell[][] map, int x, int y) {
        if (x != 0 && y != 0) {
            return false;
        }
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "jack";
        }
        return true;
    }

    /**
     * Makes a copy of the map. To avoid changing the original map.
     * 
     * @param map the map to be copied. Type Cell[][]
     * @return the copy of the map
     */
    public static Cell[][] makeMapCopy(Cell[][] map) {
        Cell[][] copy = new Cell[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                copy[i][j] = new Cell(map[i][j].x, map[i][j].y, map[i][j].content, map[i][j].status);
            }
        }
        return copy;
    }

    /**
     * Generates a random valid map.
     * 
     * @param cells    map to be generated on. Type Cell[][]
     * @param jack     the jack cell. Type Cell
     * @param treasure the treasure cell. Type Cell
     * @param tortuga  the tortuga cell. Type Cell
     * @return the generated map
     * @returnType type Cell[][]
     */
    public static Cell[][] genrateRandomMap(Cell[][] cells, Cell jack, Cell treasure, Cell tortuga) {
        cells[0][0].content = "jack";
        addJack(cells, 0, 0);
        jack = cells[0][0];
        // Add tortuga to the map
        // Rand number between 1 and 7
        int to_x = (int) (Math.random() * 7) + 1;
        int to_y = (int) (Math.random() * 7) + 1;

        addTortuga(cells, to_x, to_y);
        tortuga = cells[to_x][to_y];
        // Add treasure to the map
        // Rand number between 1 and 7 and not equal to tortuga
        int tr_x = (int) (Math.random() * 7) + 1;
        int tr_y = (int) (Math.random() * 7) + 1;
        while (tr_x == to_x && tr_y == to_y) {
            tr_x = (int) (Math.random() * 7) + 1;
            tr_y = (int) (Math.random() * 7) + 1;
        }
        addTreasure(cells, tr_x, tr_y);
        treasure = cells[tr_x][tr_y];
        // Add kraken to the map
        // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure,
        // jack
        int kr_x = (int) (Math.random() * 7) + 3;
        int kr_y = (int) (Math.random() * 7) + 3;
        while (!addKraken(cells, kr_x, kr_y)) {
            kr_x = (int) (Math.random() * 7) + 3;
            kr_y = (int) (Math.random() * 7) + 3;
        }

        // Add the rocks to the map
        // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure,
        // jack
        int ro_x = (int) (Math.random() * 7) + 1;
        int ro_y = (int) (Math.random() * 7) + 1;
        while (!addRock(cells, ro_x, ro_y)) {
            ro_x = (int) (Math.random() * 7) + 1;
            ro_y = (int) (Math.random() * 7) + 1;
        }

        // Add the Flying Dutchman to the map
        // Rand number between 3 and 7 and atleast 2 cells away from tortuga, treasure,
        // jack
        int fd_x = (int) (Math.random() * 7) + 3;
        int fd_y = (int) (Math.random() * 7) + 3;
        while (!flyingDutchman(cells, fd_x, fd_y)) {
            fd_x = (int) (Math.random() * 7) + 3;
            fd_y = (int) (Math.random() * 7) + 3;
        }

        return cells;
    }

    /**
     * Takes the input from the user in one of 3 variants.
     * Variant 1: random map
     * Variant 2: map from user console
     * Variant 3: map from file
     * Then it calls the methods to construct the map. After that, it
     * calls backtracking method to find the path which is not guranateed to be the
     * shortest.
     * Then it calls the method to find the shortest path
     * 
     * @param args
     * @throws IOException in case of any file reading or writing errors
     */
    public static void main(String[] args) throws IOException {
        // Take the input from the user
        ArrayList<Double> durationAstar = new ArrayList<>();
        ArrayList<Double> durationBacktrack = new ArrayList<>();
        int winAstar = 0;
        int winBack = 0;
        int lostAstar = 0;
        int lostBack = 0;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader("maps.txt"));
            for (int i = 0; i < 1000; i++) {

                // Construct empty 9*9 map
                Cell[][] cells = new Cell[9][9];
                Cell jack = new Cell(0, 0, "jack", "non");
                Cell treasure = null;
                Cell tortuga = null;
                int variant = 2;
                for (int h = 0; h < 9; h++) {
                    for (int j = 0; j < 9; j++) {
                        cells[h][j] = new Cell(h, j, "non", "empty");
                    }
                }

                String[] input = buffer.readLine().split(" ");
                if (input.length > 6 || input.length < 6) {
                    System.out.println("Invalid input: Please enter 6 postions!");
                    return;
                }
                for (int j = 0; j < 6; j++) {
                    String point = input[j].trim();
                    // remove the first and last characters
                    point = point.substring(1, point.length() - 1);

                    // Validate the input
                    if (!point.matches("[0-8],[0-8]")) {
                        System.out.println("Invalid input: Please enter valid positions!");
                        return;
                    }

                    String[] position = point.split(",");
                    int x = Integer.parseInt(position[0]);
                    int y = Integer.parseInt(position[1]);
                    boolean check = true;
                    if (j == 0) {
                        check = addJack(cells, x, y);
                        jack = cells[x][y];
                    }

                    if (j == 1) {
                        check = flyingDutchman(cells, x, y);
                    }

                    if (j == 2) {
                        check = addKraken(cells, x, y);
                    }

                    if (j == 3) {
                        check = addRock(cells, x, y);
                    }

                    if (j == 4) {
                        check = addTreasure(cells, x, y);
                        treasure = cells[x][y];
                    }

                    if (j == 5) {
                        check = addTortuga(cells, x, y);
                        tortuga = cells[x][y];
                    }
                    if (!check) {
                        System.out.println("Invalid input: Please enter valid positions!");
                        return;
                    }
                }
                
                // Got the ans
                // Check if jack is in the same cell as the treasure
                boolean jackHasRum = false;
                if ((tortuga == null && jack.status.equals("rum")) || (jack.x == tortuga.x && jack.y == tortuga.y)) {
                    jackHasRum = true;
                }

                // Backtracking call
                double startBackTTime = System.nanoTime();
                if(jack.content.equals("preKraken") || jack.content.equals("preDutchman")) {
                    // instant death
                    lostAstar++;
                    lostBack++;
                    durationAstar.add((double) 0);
                    durationBacktrack.add((double) 0);
                    continue;
                }
                Set<Cell> visited = new HashSet<Cell>();
                Set<Cell> blocked = new HashSet<Cell>();
                // the direct path from the jack to the treasure without forcing the jack to go
                // through the tortuga
                String directPath = backtrack(makeMapCopy(cells), visited, treasure, jack, blocked, jackHasRum);

                String alternativePath = "";
                String pathToTortuga = backtrack(makeMapCopy(cells), new HashSet<Cell>(), tortuga, jack, new HashSet<>(),
                        jackHasRum);
                if (pathToTortuga != null) { // if the jack can't reach the tortuga
                    pathToTortuga = pathToTortuga.trim();
                    String pathToTreasure = backtrack(makeMapCopy(cells), new HashSet<Cell>(), treasure, tortuga,
                            new HashSet<>(), true);
                    if (pathToTreasure != null) {
                        ArrayList<String> temp = new ArrayList<>(Arrays.asList(pathToTreasure.split(" ")));
                        temp.remove(0); // remove the first element
                        pathToTreasure = String.join(" ", temp);
                        alternativePath = pathToTortuga + " " + pathToTreasure;
                    }
                }

                if (directPath == null && alternativePath.equals("")) {
                    directPath = null;
                } else if (directPath == null && !alternativePath.equals("")) {
                    directPath = alternativePath;
                } else if (!directPath.equals("") && alternativePath.equals("")) {
                    // no need to do anything
                } else {
                    // compare the two paths and choose the shorter one
                    if (directPath.split(" ").length > alternativePath.split(" ").length) {
                        directPath = alternativePath;
                    }
                }

                double endBackTTime = System.nanoTime();
                double d = (endBackTTime - startBackTTime) / 1000000;

                // Write the output of the backtracking algorithm to the file
                if (directPath == null || directPath.equals("")) {
                    lostBack++;
                    durationBacktrack.add((double) 0);
                } else {
                    winBack++;
                    durationBacktrack.add(d);
                }

                // A* algorithm
                Set<Cell> open = new HashSet<Cell>();
                Set<Cell> closed = new HashSet<Cell>();

                closed.add(jack);
                // Calculate time for the A* algorithm

                long startTime = System.nanoTime();
                String directPathAStart = aStar(makeMapCopy(cells), jack, treasure, jack.x, jack.y, open, closed, jackHasRum,
                        variant);
                // If path is null then the treasure is not reachable mayber because of the
                String alternativePathAStart = "";
                open = new HashSet<Cell>();
                closed = new HashSet<Cell>();
                closed.add(jack);

                String pathToToutugaAStar = aStar(makeMapCopy(cells), jack, tortuga, jack.x, jack.y, open, closed, false,
                        variant);
                if (pathToToutugaAStar != null) {
                    // Find shortest path to the treasure from the Tortuga
                    open = new HashSet<Cell>();
                    closed = new HashSet<Cell>();
                    closed.add(tortuga);
                    String pathFromTor = aStar(makeMapCopy(cells), tortuga, treasure, tortuga.x, tortuga.y, open, closed,
                            true, variant);
                    if (pathFromTor != null) {
                        alternativePathAStart = pathToToutugaAStar.trim() + " " + pathFromTor.trim();
                    }
                }

                // Compare the two paths
                if (directPathAStart == null && alternativePathAStart.equals("")) {
                    directPathAStart = null;
                } else if (directPathAStart == null && !alternativePathAStart.equals("")) {
                    directPathAStart = alternativePathAStart;
                } else if (!directPathAStart.equals("") && alternativePathAStart.equals("")) {
                    // no need to do anything
                } else {
                    // compare the two paths and choose the shorter one
                    if (directPathAStart.split(" ").length > alternativePathAStart.split(" ").length) {
                        directPathAStart = alternativePathAStart;
                    }
                }
                long endTime = System.nanoTime();

                long duration = (endTime - startTime) / 1000000;
                // Write the output to the file
                if (directPathAStart == null || directPathAStart.trim().equals("")) {
                    lostAstar++;
                    durationAstar.add((double) 0);
                } else {
                    winAstar++;
                    durationAstar.add((double) duration);
                }

                if(lostAstar != lostBack) {
                    System.out.println("Something is wrong");
                }
            }
        } catch (IOException e) {
            System.out.println("Invalid input: Please enter valid input!");
            return;
        }

        // Mean and standard deviation of the A* algorithm
        double meanAstar = 0;
        double varianceAstar = 0;
        for (int i = 0; i < durationAstar.size(); i++) {
            meanAstar += durationAstar.get(i);
        }
        meanAstar /= durationAstar.size();

        for (int i = 0; i < durationAstar.size(); i++) {
            varianceAstar += Math.pow(durationAstar.get(i) - meanAstar, 2);
        }
        varianceAstar /= durationAstar.size();
        
        double stdAstar = Math.sqrt(varianceAstar);

        // Mean and standard deviation of the backtracking algorithm
        double meanBack = 0;
        double varianceBack = 0;
        for (int i = 0; i < durationBacktrack.size(); i++) {
            meanBack += durationBacktrack.get(i);
        }

        meanBack /= durationBacktrack.size();

        for (int i = 0; i < durationBacktrack.size(); i++) {
            varianceBack += Math.pow(durationBacktrack.get(i) - meanBack, 2);
        }
        varianceBack /= durationBacktrack.size();
        double stdBack = Math.sqrt(varianceBack);

        // Calculate the percentage of the wins
        double winPercentageAstar = (double) winAstar / (winAstar + lostAstar) * 100;
        double winPercentageBack = (double) winBack / (winBack + lostBack) * 100;

        // Calculate the percentage of the lost
        double lostPercentageAstar = (double) lostAstar / (winAstar + lostAstar) * 100;
        double lostPercentageBack = (double) lostBack / (winBack + lostBack) * 100;

        // Calculate the mode of the A* algorithm
        double modeAstar = 0;
        int maxCount = 0;
        for (int i = 0; i < durationAstar.size(); i++) {
            int count = 0;
            for (int j = 0; j < durationAstar.size(); j++) {
                if (durationAstar.get(j) == durationAstar.get(i)) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                modeAstar = durationAstar.get(i);
            }
        }

        // Calculate the mode of the backtracking algorithm
        double modeBack = 0;
        maxCount = 0;
        for (int i = 0; i < durationBacktrack.size(); i++) {
            int count = 0;
            for (int j = 0; j < durationBacktrack.size(); j++) {
                if (durationBacktrack.get(j) == durationBacktrack.get(i)) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                modeBack = durationBacktrack.get(i);
            }
        }

        // write the results to the file
        try {
            FileWriter fw = new FileWriter("resultsA2B2.txt");
            String output = "A* algorithm Variant 2: \n";
            output += "Mean: " + meanAstar + "\n";
            output += "Standard deviation: " + stdAstar + "\n";
            output += "Mode: " + modeAstar + "\n";
            output += "Win percentage: " + winPercentageAstar + "\n";
            output += "Lost percentage: " + lostPercentageAstar + "\n";
            output += "Backtracking algorithm: \n";
            output += "Mean: " + meanBack + "\n";
            output += "Standard deviation: " + stdBack + "\n";
            output += "Mode: " + modeBack + "\n";
            output += "Win percentage: " + winPercentageBack + "\n";
            output += "Lost percentage: " + lostPercentageBack + "\n";
            fw.write(output);
            fw.close();
        } catch (IOException e) {
            System.out.println("Invalid input: Please enter valid input!");
            return;
        }
    }
}