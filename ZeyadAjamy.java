import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

class Cell {
    int x, y;
    String content, status;
    int f = Integer.MAX_VALUE;
    Cell parent = null;


    public Cell(Cell other) {
        this.x = other.x;
        this.y = other.y;
        this.content = other.content; // non, kraken, dutchman, rock, jack, kraken&rock, treasure, preKraken,or
                                // tortuga
        this.status = other.status; // (empty) for safe cells, (killed or intact) for kraken, (immortal) for rock
                              // and dutchman, or (rum) for jack
    }

    public Cell(int x, int y, String content, String status) {
        this.x = x;
        this.y = y;
        this.content = content; // non, kraken, dutchman, rock, jack, kraken&rock, treasure, preKraken,or
                                // tortuga
        this.status = status; // (empty) for safe cells, (killed or intact) for kraken, (immortal) for rock
                              // and dutchman, or (rum) for jack
    }

    public int getNumParent() {
        Cell temp = this;
        int count = 0;
        while (temp.parent != null) {
            count++;
            temp = temp.parent;
        }
        return count;
    }

    @Override
    public String toString() {
        return "Cell [x=" + x + ", y=" + y + "]";
    }
}

public class ZeyadAjamy {
    
    public static Boolean existInClosedSet(Cell[] closed, int x, int y) {
        for (Cell cell : closed) {
            if (cell != null && cell.x == x && cell.y == y) {
                return true;
            }
        }
        return false;
    }
    
    static int minCost = Integer.MAX_VALUE;
    static String op = "";

    public static Cell[][] handlekraken(Cell[][] map, Cell kraken){
        int x = kraken.x;
        int y = kraken.y;

        // Get all the precpetions of the kraken
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
                    if (!(i == x || j == y)) continue; // only horizontal and vertical
                    if (i < 0 || i > 8 || j < 0 || j > 8) continue; // out of bounds
                    if(kraken.content.equals("kraken")){
                        map[x][y].content = "non";
                        map[i][j].status = "killed";
                        map[i][j].content = "non";
                    } else if(kraken.content.equals("kraken&rock")){
                        map[x][y].content = "rock";
                        map[i][j].status = "immortal";
                        map[i][j].content = "non";
                    } else if (kraken.content.equals("kraken&preDutchman")){
                        map[x][y].content = "preDutchman";
                        map[i][j].status = "immortal";
                        map[i][j].content = "non";
                    }

                }
            }
        }

        return map;
    }
    
    public static String backtrack(Cell[][] map, Set<Cell> closed ,Cell treasure, Cell jack, boolean rum) {
        // we got at most 8 possible moves
        Cell[] moves = new Cell[8];
        int i = 0; // number of moves available
        if(jack.x == 4 && jack.y == 4){
            String s = "";
        }
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                if(x == 0 && y == 0) continue;
                if(jack.x + x >= 0 && jack.x + x < map.length && jack.y + y >= 0 && jack.y + y < map[0].length) {
                    moves[i++] = map[jack.x + x][jack.y + y];
                }
            }
        }

       
        int min = Integer.MAX_VALUE;
        Cell minCell = null;

        for(Cell cell : moves) {
            if(cell == null || !(cell.content.equals("non") || cell.content.equals("tortuga") || cell.content.equals("treasure") || cell.content.equals("kraken")
            || cell.content.equals("kraken&rock") || cell.content.equals("kraken&preDutchman")) ){
                continue;
            }
            // convert Set to Cell[]
            Cell[] closedArray = closed.toArray(new Cell[closed.size()]);
            if(cell.content.equals("kraken") || cell.content.equals("kraken&rock") || cell.content.equals("kraken&preDutchman")) {
                if(!rum) continue;
                handlekraken(map, cell);
                // update the moves array
                i = 0;
                for(int x = -1; x <= 1; x++) {
                    for(int y = -1; y <= 1; y++) {
                        if(x == 0 && y == 0) continue;
                        if(jack.x + x >= 0 && jack.x + x < map.length && jack.y + y >= 0 && jack.y + y < map[0].length) {
                            moves[i++] = map[jack.x + x][jack.y + y];
                        }
                    }
                }
            }

            if(existInClosedSet(closedArray, cell.x, cell.y)){
                continue;
            }
            if(cell.content.equals(treasure.content)){
                min = 0;
                minCell = cell;
                break;
            }
            // Calculate the dx and dy of the current cell and the treasure
            int dx = Math.abs(cell.x - treasure.x);
            int dy = Math.abs(cell.y - treasure.y);
            int temp = Math.max(dx, dy);
            if(temp <= min) {
                min = temp;
                minCell = map[cell.x][cell.y]; // update the minCell
            } 
        }
        if (minCell == null) {
            return null;
        }

        // add the rest of the moves to the closed set
        for(int j = 0; j < moves.length; j++) {
            Cell tmp = moves[j];
            if(tmp == null) continue;
            if(tmp.x == minCell.x && tmp.y == minCell.y) continue;
            closed.add(moves[j]);
        }

        minCell.parent = jack;
        if (minCell.content.equals(treasure.content)) {
            // trace back all the parents of the cell
            Cell temp = minCell;
            String tempPath = "";
            while (temp.parent != null) {
                tempPath +=  "["+temp.x + "," + temp.y + "] ";
                temp = temp.parent;
            }
            tempPath += "["+temp.x + "," + temp.y + "] ";
            String[] tempArr = tempPath.split(" ");
            Collections.reverse(Arrays.asList(tempArr));
            tempPath = "";
            for (String string : tempArr) {
                tempPath += string + " ";
            }
            op = tempPath.trim();
            return tempPath.trim();
        }
        // Check if we can move to the cell
        if (minCell.content.equals("non") || minCell.content.equals("treasure")) {
            closed.add(minCell);
            backtrack(map, closed, treasure, minCell, rum);
        } else if (minCell.content.equals("tortuga")){
            rum = true;
            closed.add(minCell);
            backtrack(map, closed, treasure, minCell, rum);
        }
        return op;
    }

    public static Boolean existInOpenSet(ArrayList<Cell> openSet, int x, int y) {
        for (Cell c : openSet) {
            if (c.x == x && c.y == y) {
                return true;
            }
        }
        return false;
    }

    public static Cell getCellFromOpenSet(ArrayList<Cell> openSet, int x, int y) {
        for (Cell c : openSet) {
            if (c.x == x && c.y == y) {
                return c;
            }
        }
        return null;
    }

    public static int calculateHeuristic(Cell[][] map, int x, int y, Cell treasure) {
        int xTreasure = treasure.x;
        int yTreasure = treasure.y;
        int xDiff = Math.abs(xTreasure - x);
        int yDiff = Math.abs(yTreasure - y);
        int max = Math.max(xDiff, yDiff);
        return max;
    }
    
    public static boolean addKraken(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8) return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "kraken";
            for(int i = x - 1; i <= x + 1; i++) {
                for(int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y)) continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8) continue;
                    if (map[i][j].content.equals("non")) {
                        map[i][j].content = "preKraken";
                    }
                }
            }

            return true;
        } else if (map[x][y].content.equals("rock")) {
            map[x][y].content = "kraken&rock";
            for(int i = x - 1; i <= x + 1; i++) {
                for(int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y)) continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8) continue;
                    if (map[i][j].content.equals("non")) {
                        map[i][j].content = "preKraken";
                    }
                }
            }
            return true;
        } else if (map[x][y].content.equals("preDutchman")) {
            map[x][y].content = "kraken&preDutchman";
            for(int i = x - 1; i <= x + 1; i++) {
                for(int j = y - 1; j <= y + 1; j++) {
                    if (!(i == x || j == y)) continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8) continue;
                    if (map[i][j].content.equals("non")) {
                        map[i][j].content = "preKraken";
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean flyingDutchman(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8) return false;
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "dutchman";
            // Add preceptions zone
            for(int i = x - 1; i <= x + 1; i++) {
                for(int j = y - 1; j <= y + 1; j++) {
                    if (i == x && j == y) continue;
                    if (i < 0 || i > 8 || j < 0 || j > 8) continue;
                    if (map[i][j].content.equals("non")) {
                        map[i][j].content = "preDutchman";
                    }
                }
            }
            return true;
        } 
        return false;
    }

    public static boolean addRock(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8) return false;
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

    public static boolean addTortuga(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8) return false;
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

    public static boolean addTreasure(Cell[][] map, int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y > 8) return false;
        if(x == 0 && y == 0) return false; // cannot be in the origin
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "treasure";
            return true;
        }
        return false;
    }

    public static boolean addJack(Cell[][] map, int x, int y) {
        if (x != 0 && y != 0) {
            return false;
        }
        if (map[x][y].content.equals("non")) {
            map[x][y].content = "jack";
        }
        return true;
    }
    
    public static String aStar(Cell[][] map, Cell jack, Cell treasure, int x, int y, Set<Cell> open, Set<Cell> closed, boolean rum) {

        // add the current cell to the closed list
        ArrayList<Cell> openSet = new ArrayList<Cell>(open);
        ArrayList<Cell> closedSet = new ArrayList<Cell>(closed);
        Cell[] array = new Cell[closed.size()];
        // Check all the neighbors of the current cell using for loops
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Check if the neighbor is valid
                if (i >= 0 && i < 9 && j >= 0 && j < 9) {
                    // Check if the neighbor is not in the closed list
                    if (!existInClosedSet(closedSet.toArray(array) , i, j)) {

                        String content = map[i][j].content;
                        int h = calculateHeuristic(map, i, j, treasure);
                        int g = map[x][y].getNumParent() + 1;
                        int f = h + g;

                        if (existInOpenSet(openSet, i, j)) {
                            Cell old = getCellFromOpenSet(openSet, i, j);
                            if (old.f > f) {
                                old.f = f;
                                map[i][j].parent = map[x][y];
                            }
                        } else {
                            map[i][j].f = f;
                            map[i][j].parent = map[x][y];
                        }

                        if (!existInOpenSet(openSet, i, j) && (content.equals("non") || content.equals("treasure"))) {
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
                                        if (!(k == i || l == j)) continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8) continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed 
                                        for(Cell c: closed){
                                            if(c.x == k && c.y == l){
                                                closed.remove(c);
                                                break;
                                            }
                                        }
                                        
                                    }
                                }
                                
                                // If one of the neighbors of the kraken is the neighbor of the current cell, then we have to add it to the open set
                                for(Cell neighbor : neighbors){
                                    for(int k = x - 1; k <= x + 1; k++){
                                        for(int l = y - 1; l <= y + 1; l++){
                                            if(k == neighbor.x && l == neighbor.y){
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
                        } else if(content.equals("kraken&rock")){ 
                            if(rum){
                                map[i][j].content = "rock";
                                map[i][j].status = "immortal";
                                // Remove the kraken from the map and it's preceptions zone.
                                Set<Cell> neighbors = new HashSet<>();
                                for (int k = i - 1; k <= i + 1; k++) {
                                    for (int l = j - 1; l <= j + 1; l++) {
                                        if (!(k == i || l == j)) continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8) continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed 
                                        for(Cell c: closed){
                                            if(c.x == k && c.y == l){
                                                closed.remove(c);
                                                break;
                                            }
                                        }
                                        
                                    }
                                }
                                
                                // If one of the neighbors of the kraken is the neighbor of the current cell, then we have to add it to the open set
                                for(Cell neighbor : neighbors){
                                    for(int k = x - 1; k <= x + 1; k++){
                                        for(int l = y - 1; l <= y + 1; l++){
                                            if(k == neighbor.x && l == neighbor.y){
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
                            if(rum){
                                map[x][y].content = "preDutchman";
                                map[x][y].status = "immortal";

                                // Remove the kraken from the map and it's preceptions zone.
                                Set<Cell> neighbors = new HashSet<>();
                                for (int k = i - 1; k <= i + 1; k++) {
                                    for (int l = j - 1; l <= j + 1; l++) {
                                        if (!(k == i || l == j)) continue;
                                        if (k < 0 || k > 8 || l < 0 || l > 8) continue;
                                        if (map[k][l].content.equals("preKraken")) {
                                            map[k][l].content = "non";
                                            neighbors.add(map[k][l]);
                                        }
                                        // Remove the preKraken from the closed 
                                        for(Cell c: closed){
                                            if(c.x == k && c.y == l){
                                                closed.remove(c);
                                                break;
                                            }
                                        }
                                        
                                    }
                                }
                                closed.add(map[x][y]);
                                closed.add(map[i][j]);

                            }
                        } else if(!(content.equals("non") || content.equals("treasure"))) {
                            closed.add(map[i][j]);
                        }
                    }
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
            return aStar(map, jack, treasure, next.x, next.y, open, closed, rum);
        }
    }

    public static void printOutput(String output, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(output);
        writer.close();
    }

    public static Cell[][] makeMapCopy(Cell[][] map) {
        Cell[][] copy = new Cell[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                copy[i][j] = new Cell(map[i][j].x, map[i][j].y, map[i][j].content, map[i][j].status);
            }
        }
        return copy;
    }

    public static void main(String[] args) throws IOException {
        Cell[][] cells = new Cell[9][9];
        Cell jack = null, treasure = null, tortuga = null;
        int variant = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new Cell(i, j, "non", "empty");
            }
        }

        try (// Ask the user if he/ she wants to generate a random map or input one or read from input.txt
        Scanner scanner = new Scanner(System.in)) {
            System.out.println("Please enter the number of the variant you want to run:");
            System.out.println("1. Random map");
            System.out.println("2. Input map and preception' senario");
            System.out.println("3. Read from input.txt");
            int userVariant = scanner.nextInt();
            if( userVariant < 1 || userVariant > 3){
                System.out.println("Invalid input");
                return;
            }
            if(userVariant == 1){
                // Add jack to the map
                // rand is the random number between 1 and 2
                variant = (int) (Math.random() * 2) + 1;
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
                // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure, jack
                int kr_x = (int) (Math.random() * 7) + 3;
                int kr_y = (int) (Math.random() * 7) + 3;
                while (!addKraken(cells, kr_x, kr_y)) {
                    kr_x = (int) (Math.random() * 7) + 3;
                    kr_y = (int) (Math.random() * 7) + 3;
                } 

                // Add the rocks to the map 
                // Rand number between 1 and 7 and atleast 1 cells away from tortuga, treasure, jack
                int ro_x = (int) (Math.random() * 7) + 1;
                int ro_y = (int) (Math.random() * 7) + 1;
                while (!addRock(cells, ro_x, ro_y)) {
                    ro_x = (int) (Math.random() * 7) + 1;
                    ro_y = (int) (Math.random() * 7) + 1;
                }

                // Add the Flying Dutchman to the map
                // Rand number between 3 and 7 and atleast 2 cells away from tortuga, treasure, jack
                int fd_x = (int) (Math.random() * 7) + 3;
                int fd_y = (int) (Math.random() * 7) + 3;
                while (!flyingDutchman(cells, fd_x, fd_y)) {
                    fd_x = (int) (Math.random() * 7) + 3;
                    fd_y = (int) (Math.random() * 7) + 3;
                }
            }

            if(userVariant == 2){
                // Read the input from the user and add the map to the cells
                // Scanner userInput = new Scanner(System.in);
                System.out.println("Please enter the map in one line:");
                // Read the input from the user console
                try{
                    // read the line with spaces
                    String line1 = scanner.nextLine();
                    line1 = scanner.nextLine();
                    String[] map = line1.split(" ");
                    // Add the map to the cells
                    if (map.length > 6 || map.length < 6) {
                        System.out.println("Invalid input: Please enter 6 postions!");
                        return;
                    }
                    for (int j = 0; j < 6; j++) {
                        String point = map[j].trim();
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
                        if( j == 0){
                            check = addJack(cells, x, y);
                            jack = cells[x][y];
                        }
                        
                        if (j == 1){
                            check = flyingDutchman(cells, x, y);
                        }
                        
                        if (j == 2){
                            check = addKraken(cells, x, y);
                        }
                        
                        if (j == 3){
                            check = addRock(cells, x, y);
                        }
                        
                        if (j == 4){
                            check = addTreasure(cells, x, y);
                            treasure = cells[x][y];
                        }
                        
                        if (j == 5){
                            check = addTortuga(cells, x, y);
                            tortuga = cells[x][y];
                        }
                        if (!check) {
                            System.out.println("Invalid input: Please enter valid positions!");
                            return;
                        }
                    }
                    
                    // Read the second line
                    System.out.println("Please enter the senario in one line:");
                    line1 = scanner.nextLine();

                    try { 
                        int senario = Integer.parseInt(String.valueOf(line1.trim()));
                        // Add the senario to the cells
                        if (senario < 1 || senario > 2) {
                            System.out.println("Invalid input: Please enter 6 postions!");
                            return;
                        }
                    } catch(NumberFormatException e){
                        System.out.println("Invalid input: Please enter a number!");
                        return;
                    }
                } catch(Exception e){
                    System.out.println("Invalid input: Please enter a number!");
                    return;
                }
            }

            if(userVariant == 3 ){
                try {
                    BufferedReader buffer = new BufferedReader(new FileReader("input.txt"));
                    for (int i = 0; i < 2; i++) {
                        if (i == 0) {
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
                                if( j == 0){
                                    check = addJack(cells, x, y);
                                    jack = cells[x][y];
                                }
                                
                                if (j == 1){
                                    check = flyingDutchman(cells, x, y);
                                }
                                
                                if (j == 2){
                                    check = addKraken(cells, x, y);
                                }
                                
                                if (j == 3){
                                    check = addRock(cells, x, y);
                                }
                                
                                if (j == 4){
                                    check = addTreasure(cells, x, y);
                                    treasure = cells[x][y];
                                }
                                
                                if (j == 5){
                                    check = addTortuga(cells, x, y);
                                    tortuga = cells[x][y];
                                }
                                if (!check) {
                                    System.out.println("Invalid input: Please enter valid positions!");
                                    return;
                                }
                            }
                        } else {
                            String input = buffer.readLine();

                            if (input.equals("") || input == null) {
                                System.out.println("Invalid input: Please enter valid variant!");
                                return;
                            }

                            // Parse the input
                            variant = Integer.parseInt(input);
                            buffer.close();

                            // Validate the input
                            if (variant < 1 || variant > 3) {
                                System.out.println("Invalid input: Please enter valid variant!");
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Invalid input: Please enter valid input!");
                    return;
                }
            }
            scanner.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // Print the initial state
        System.out.println("2D map:");
        System.out.println("-------------------");
        System.out.println("  0 1 2 3 4 5 6 7 8");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 9; j++) {
                if (cells[j][i].content.equals("jack")) {
                    System.out.print("J ");
                } else if (cells[i][j].content.equals("dutchman")) {
                    System.out.print("D ");
                } else if (cells[i][j].content.equals("kraken")) {
                    System.out.print("K ");
                } else if (cells[i][j].content.equals("rock")) {
                    System.out.print("R ");
                } else if (cells[i][j].content.equals("treasure")) {
                    System.out.print("T ");
                } else if (cells[i][j].content.equals("tortuga")) {
                    System.out.print("O ");
                } else if (cells[i][j].content.equals("kraken&rock")) {
                    System.out.print("KR");
                } else if (cells[i][j].content.equals("preKraken")) {
                    System.out.print("! ");
                } else if (cells[i][j].content.equals("preDutchman")){
                    System.out.print("! ");
                } else if (cells[i][j].content.equals("kraken&preDutchman")){
                    System.out.print("K!");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
        
        // Backtracking algorithm
        boolean jackHasRum = false;
        if ((tortuga == null && jack.status.equals("rum")) || (jack.x == tortuga.x && jack.y == tortuga.y)) {
            jackHasRum = true;
        }
        double startBackTTime = System.nanoTime();
        
        Set<Cell> visited = new HashSet<Cell>();
        visited.add(jack);
        backtrack(makeMapCopy(cells), visited, treasure, jack, jackHasRum);
        if (op.equals("")) {
            Set<Cell> visited2 = new HashSet<Cell>();
            visited2.add(jack);
            backtrack(makeMapCopy(cells), visited2, tortuga, jack, jackHasRum);
            String pathToTortuga = op;
            if (pathToTortuga == "") {
                // write to file that there is no path
                printOutput("Lose", "outputBacktracking.txt");
            } else {
                pathToTortuga = pathToTortuga.trim();
                Set<Cell> visited3 = new HashSet<Cell>();
                visited3.add(tortuga);
                visited3.add(jack);
                op = "";
                backtrack(makeMapCopy(cells), visited3, treasure, tortuga, true);
                String pathToTreasure = op;
                if (pathToTreasure.trim().equals("") || pathToTreasure == null
                        || pathToTreasure.trim().equals("[" + jack.x + "," + jack.y + "]")) {
                    printOutput("Lose", "outputBacktracking.txt");
                } else {
                    ArrayList<String> temp = new ArrayList<>(Arrays.asList(pathToTreasure.split(" ")));
                    temp.remove(0); // remove the first element
                    pathToTreasure = String.join(" ", temp);
                    op = pathToTortuga + " " + pathToTreasure;
                }
            }
        }
        double endBackTTime = System.nanoTime();
        double d = (endBackTTime - startBackTTime) / 1000000;

        // Write the output to the file
        if (op.equals("")) {
            printOutput("Lose", "outputBacktracking.txt");
        } else {
            String output = "Win\n";
            output += String.valueOf(op.split("]").length - 1) + "\n";
            output += op + "\n";
            // Write 2d map to the file
            output += "-------------------\n";
            output += "  0 1 2 3 4 5 6 7 8\n";
            // Convert the string path to 2d array
            String[] pathArray = op.split(" ");
            String[][] path2dArray = new String[9][9];
            for (int i = 0; i < pathArray.length; i++) {
                String[] coordinates = pathArray[i].split(",");
                int x = Integer.parseInt(coordinates[0].substring(1));
                int y = Integer.parseInt(coordinates[1].substring(0, 1));
                if(path2dArray[x][y] != null && path2dArray[x][y].equals("*")){
                    path2dArray[x][y] = "<>";
                } else {
                    path2dArray[x][y] = "*";
                }
            }

            for (int i = 0; i < 9; i++) {
                output += i + " ";
                for (int j = 0; j < 9; j++) {
                    if (path2dArray[i][j] == null) {
                        output += "- ";
                        continue;
                    }
                    if (path2dArray[i][j].equals("*")) {
                        output += "* ";
                    } else if (path2dArray[i][j].equals("<>")) {
                        output += "<>";
                    } else {
                        output += "- ";
                    }
                }
                output += "\n";
            }

            output += "-------------------\n";
            output += String.valueOf(d) + " ms";

            printOutput(output, "outputBacktracking.txt");
        }

        // A* algorithm
        Set<Cell> open = new HashSet<Cell>();
        Set<Cell> closed = new HashSet<Cell>();

        closed.add(jack);
        // Calculate time for the A* algorithm

        long startTime = System.nanoTime();
        String path2 = aStar(makeMapCopy(cells), jack, treasure, jack.x, jack.y, open, closed, jackHasRum);
        // If path is null then the treasure is not reachable mayber because of the kraken
        if (path2 == null) {
            // Find the shortest path to the Tortuga
            open = new HashSet<Cell>();
            closed = new HashSet<Cell>();
            closed.add(jack);

            path2 = aStar(makeMapCopy(cells), jack, tortuga, jack.x, jack.y, open, closed, false);
            if (path2 == null) {
                // Lost case
                printOutput("Lose", "outputAStar.txt");
                return;
            } else {
                // Find shortest path to the treasure from the Tortuga
                open = new HashSet<Cell>();
                closed = new HashSet<Cell>();
                closed.add(tortuga);
                String pathFromTor = aStar(makeMapCopy(cells), tortuga, treasure, tortuga.x, tortuga.y, open, closed,true);
                if (pathFromTor == null) {
                    printOutput("Lose", "outputAStar.txt");
                    return;
                }
                path2 += " " + pathFromTor.trim();
            }
        } else {
            open = new HashSet<Cell>();
            closed = new HashSet<Cell>();
            closed.add(tortuga);

            String path3 = "", path4 = "", path5 = "";
            path3 = path2;
            
            open = new HashSet<Cell>();
            closed = new HashSet<Cell>();
            closed.add(jack);
            path4 = aStar(makeMapCopy(cells), jack, tortuga, jack.x, jack.y, open, closed, false);
            if(path4 != null) {
                open = new HashSet<Cell>();
                closed = new HashSet<Cell>();
                closed.add(tortuga);
                path5 = aStar(makeMapCopy(cells), tortuga, treasure, tortuga.x, tortuga.y, open, closed, true);
            }
            if (path3 != null && path4 != null && path5 != null) {
                String pathTotal2 = aStar(makeMapCopy(cells), tortuga, treasure, tortuga.x, tortuga.y, open, closed,true);
                // compare the paths
                if (pathTotal2 != null && pathTotal2.split(" ").length < path2.split(" ").length) {
                    path2 = pathTotal2;

                }
            }
        }
        // Compare the two paths
        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;
        // Write the output to the file
        if (path2.trim().equals("")) {
            printOutput("Lose", "outputAStar.txt");
        } else {
            String output = "Win\n";
            output += String.valueOf(path2.split(" ").length) + "\n";
            output += "[" + jack.x + "," + jack.y + "] " + path2 + "\n";

            // Convert the string path of [0-8] [0-8] to 2d map
            String[] winningPathArray = path2.split(" ");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    for (int k = 0; k < path2.split(" ").length; k++) {
                        String[] coordinates = winningPathArray[k].split(",");
                        int x = Integer.parseInt(coordinates[0].substring(1));
                        int y = Integer.parseInt(coordinates[1].substring(0, 1));
                        if (i == x && j == y) {
                            if(cells[i][j].content.equals("jack")){
                                cells[i][j].content = "<>";
                            } else {
                                cells[i][j].content = "jack";
                            }
                        }
                    }
                }
            }

            // Write 2d map to the file
            output += "-------------------\n";
            output += "  0 1 2 3 4 5 6 7 8\n";

            for (int i = 0; i < 9; i++) {
                output += i + " ";
                for (int j = 0; j < 9; j++) {
                    if (cells[i][j].content.equals("jack")) {
                        output += "* ";
                    } else if (cells[i][j].content.equals("<>")){
                        output += "<>";
                    } else {
                        output += "- ";
                    }
                }
                output += "\n";
            }
            output += "-------------------\n";
            output += String.valueOf(duration) + " ms";
            printOutput(output, "outputAStar.txt");
        }
    }
}