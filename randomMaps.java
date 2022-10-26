import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
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
        return "[" + this.x + "," + this.y + "]";
    }
}

public class randomMaps {
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
        if(x == 0 && y == 0)
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

    private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
    
    public static void main(String[] args) {
      

        String jack = "[0,0]", treasure = "", tortuga = "", kraken = "", rock = "", flyingDutchman = "";
        ArrayList<String> maps = new ArrayList<String>();
        while (maps.size() < 1000) {
            Cell[][] cells = new Cell[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    cells[i][j] = new Cell(0, 0, "non", "safe");
                }
            }
            cells[0][0].content = "jack";
            addJack(cells, 0, 0);
            // Add tortuga to the map
            // Rand number between 1 and 7

            // Add treasure to the map
            // Rand number between 1 and 7 and not equal to tortuga

            
            // Add kraken to the map
            // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure,
            // jack
            int fd_x = getRandomNumberInRange(0, 8);
            int fd_y = getRandomNumberInRange(0, 8);
            while (!flyingDutchman(cells, fd_x, fd_y)) {
                fd_x = getRandomNumberInRange(0, 8);
                fd_y = getRandomNumberInRange(0, 8);
            }
            flyingDutchman = "[" + fd_x + "," + fd_y + "]";

            int kr_x = getRandomNumberInRange(0, 8);
            int kr_y = getRandomNumberInRange(0, 8);
            while (!addKraken(cells, kr_x, kr_y)) {
                kr_x = getRandomNumberInRange(0, 8);
                kr_y = getRandomNumberInRange(0, 8);
            }
            kraken = "[" + kr_x + "," + kr_y + "]";

            // Add the rocks to the map
            // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure,
            // jack
            int ro_x = getRandomNumberInRange(0, 8);
            int ro_y = getRandomNumberInRange(0, 8);
            while (!addRock(cells, ro_x, ro_y)) {
                ro_x = getRandomNumberInRange(0, 8);
                ro_y = getRandomNumberInRange(0, 8);
            }
            rock = "[" + ro_x + "," + ro_y + "]";

            // Add the Flying Dutchman to the map
            // Rand number between 3 and 7 and atleast 2 cells away from tortuga, treasure,
            // jack
            int tr_x = getRandomNumberInRange(0, 8);
            int tr_y = getRandomNumberInRange(0, 8);
            while (!addTreasure(cells, tr_x, tr_y)) {
                tr_x = getRandomNumberInRange(0, 8);
                tr_y = getRandomNumberInRange(0, 8);
            }
            treasure = "[" + tr_x + "," + tr_y + "]";

            int to_x = getRandomNumberInRange(0, 8);
            int to_y = getRandomNumberInRange(0, 8);
            while(!addTortuga(cells, to_x, to_y)) {
                to_x = getRandomNumberInRange(0, 8);
                to_y = getRandomNumberInRange(0, 8);
            }
            tortuga = "[" + to_x + "," + to_y + "]";

            maps.add(jack+" "+flyingDutchman+" "+kraken+" "+rock+" "+treasure+" "+tortuga);
        }

        // Print the maps into the maps.txt file
        try {
            PrintWriter writer = new PrintWriter("maps.txt", "UTF-8");
            for (String map : maps) {
                writer.println(map);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error while writing to the file");
        }
    }
}
