package minesweeper;

import java.util.*;

public class Minesweeper {
    private final String[][] field;
    private final boolean[][] isMarked;
    private final boolean[][] isExplored;
    private final ArrayList<String> mines;
    private int minesCount;
    private boolean revealMines = false;
    private String queueOut;
    private final static String EMPTY_SYMBOL = ".";
    private final static String MINE_SYMBOL = "X";
    private final static String MARKED_SYMBOL = "*";
    private final static String EXPLORED_SYMBOL = "/";

    public Minesweeper() {
        field = new String[9][9];
        isMarked = new boolean[9][9];
        isExplored = new boolean[9][9];
        mines = new ArrayList<>();
        minesCount = 0;
        queueOut = "";
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                field[i][j] = EMPTY_SYMBOL;
                isMarked[i][j] = false;
                isExplored[i][j] = false;
            }
        }
    }

    public void placeMine(int row, int col) {
        field[row][col] = MINE_SYMBOL;
        mines.add(row + "" + col);
    }

    public boolean isMinePresent(int row, int col) {
        return Objects.equals(field[row][col], MINE_SYMBOL);
    }

    public void populateMinesRandomly(int minesCount) {
        this.minesCount = minesCount;
        int i = 0;
        while (i < this.minesCount) {
            int randomRow = getRandomInt();
            int randomCol = getRandomInt();

            if (!isMinePresent(randomRow, randomCol)) {
                placeMine(randomRow, randomCol);
                i++;
            }
        }

        populateFieldNumbers();
    }

    public void populateFieldNumbers() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                int mineCount = 0;
                boolean isCorner = isCorner(i, j);
                boolean isSide = isSide(i, j);
                if (isCorner) mineCount = getCornerCount(i, j);
                if (isSide) mineCount = getSideCount(i, j);
                if (!isCorner && !isSide) mineCount = getMiddleCount(i, j);
                field[i][j] = mineCount > 0 && !isMinePresent(i, j) ? String.valueOf(mineCount) : field[i][j];
            }
        }
    }

    public boolean isCorner(int row, int col) {
        return (row == 0 || row == 8) && (col == 0 || col == 8);
    }

    public int getCornerCount(int row, int col) {
        int mineCount = 0;

        if (row == 0) {
            if (col == 0) {
                if (isMinePresent(0, 1)) mineCount++;
                if (isMinePresent(1, 0)) mineCount++;
                if (isMinePresent(1, 1)) mineCount++;
            }

            if (col == 8) {
                if (isMinePresent(0, 7)) mineCount++;
                if (isMinePresent(1, 7)) mineCount++;
                if (isMinePresent(1, 8)) mineCount++;
            }
        } else {
            if (col == 0) {
                if (isMinePresent(7, 0)) mineCount++;
                if (isMinePresent(7, 1)) mineCount++;
                if (isMinePresent(8, 1)) mineCount++;
            }

            if (col == 8) {
                if (isMinePresent(7, 8)) mineCount++;
                if (isMinePresent(7, 7)) mineCount++;
                if (isMinePresent(8, 7)) mineCount++;
            }
        }

        return mineCount;
    }

    public boolean isSide(int row, int col) {
        return (row > 0 && row < 8) && (col == 0 || col == 8);
    }

    public int getSideCount(int row, int col) {
        int mineCount = 0;

        if (col == 0) {
            if (isMinePresent(row - 1, col)) mineCount++;
            if (isMinePresent(row - 1, col + 1)) mineCount++;
            if (isMinePresent(row, col + 1)) mineCount++;
            if (isMinePresent(row + 1, col + 1)) mineCount++;
            if (isMinePresent(row + 1, col)) mineCount++;
        } else {
            if (isMinePresent(row - 1, col)) mineCount++;
            if (isMinePresent(row - 1, col - 1)) mineCount++;
            if (isMinePresent(row, col - 1)) mineCount++;
            if (isMinePresent(row + 1, col - 1)) mineCount++;
            if (isMinePresent(row + 1, col)) mineCount++;
        }

        return mineCount;
    }

    public int getMiddleCount(int row, int col) {
        int mineCount = 0;

        if (row > 0 && isMinePresent(row - 1, col - 1)) mineCount++;
        if (row > 0 && isMinePresent(row - 1, col)) mineCount++;
        if (row > 0 && isMinePresent(row - 1, col + 1)) mineCount++;
        if (isMinePresent(row, col - 1)) mineCount++;
        if (isMinePresent(row, col + 1)) mineCount++;
        if (row < 8 && isMinePresent(row + 1, col + 1)) mineCount++;
        if (row < 8 && isMinePresent(row + 1, col)) mineCount++;
        if (row < 8 && isMinePresent(row + 1, col - 1)) mineCount++;

        return mineCount;
    }

    public int getRandomInt() {
        Random rand = new Random();

        return rand.nextInt(0, 9);
    }

    public boolean isGameOn() {
        if (revealMines) return false;

        int minesMarked = 0;
        int exploredCount = 0;

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                if (!field[i][j].equals("X") && isMarked[i][j]) {
                    return true;
                }

                if (mines.contains(i + "" + j) && isMarked[i][j]) {
                    minesMarked++;
                }

                if (!field[i][j].equals("X") && isExplored[i][j]) {
                    exploredCount++;
                }
            }
        }

        if (minesMarked == minesCount || (minesMarked == 0 && 81 - exploredCount == minesCount)) {
            System.out.println("Congratulations! You found all the mines!");
        } else {
            return true;
        }

        return false;
    }

    public void exploreField(int row, int col) {
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        isExplored[row][col] = true;
        queue.add(new int[]{row, col});

        while (!queue.isEmpty()) {
            queueOut += "\n";
            for (int[] elem : queue) {
                queueOut += "-> (" + elem[0] + ", " + elem[1] + ")";
            }
            int[] coords = queue.poll();
            int x = coords[0];
            int y = coords[1];

            boolean isCorner = isCorner(x, y);
            boolean isSide = isSide(x, y);
            boolean isMiddle = !isCorner && !isSide;

            if (isCorner) exploreCorner(x, y, queue);
            if (isSide) exploreSide(x, y, queue);
            if (isMiddle) exploreMiddle(x, y, queue);
        }
    }

    public void exploreNeighbors(int[][] neighbors, ArrayDeque<int[]> queue) {
        boolean explorationValid = true;

        for (int[] neighbor : neighbors) {
            if (null != neighbor) {
                if (isMinePresent(neighbor[0], neighbor[1])) {
                    explorationValid = false;
                    break;
                }
            }
        }

        if (explorationValid) {
            for (int[] neighbor : neighbors) {
                if (null != neighbor) {
                    if (!isExplored[neighbor[0]][neighbor[1]]) {
                        queue.add(new int[]{neighbor[0], neighbor[1]});
                        isExplored[neighbor[0]][neighbor[1]] = true;
                        isMarked[neighbor[0]][neighbor[1]] = false;
                    }
                }
            }
        }
    }

    public void exploreCorner(int row, int col, ArrayDeque<int[]> queue) {
        int[][] neighbors;
        if (row == 0) {
            if (col == 0) {
                neighbors = new int[][]{{0, 1}, {1, 0}, {1, 1}};
                exploreNeighbors(neighbors, queue);
            }

            if (col == 8) {
                neighbors = new int[][]{{0, 7}, {1, 7}, {1, 8}};
                exploreNeighbors(neighbors, queue);
            }
        } else {
            if (col == 0) {
                neighbors = new int[][]{{7, 0}, {7, 1}, {8, 1}};
                exploreNeighbors(neighbors, queue);
            }

            if (col == 8) {
                neighbors = new int[][]{{7, 8}, {7, 7}, {8, 7}};
                exploreNeighbors(neighbors, queue);
            }
        }
    }

    public void exploreSide(int row, int col, ArrayDeque<int[]> queue) {
        int[][] neighbors;

        if (col == 0) {
            neighbors = new int[][]{{row - 1, col}, {row - 1, col + 1}, {row, col + 1}, {row + 1, col + 1}, {row + 1,
                    col}};
            exploreNeighbors(neighbors, queue);
        } else {
            neighbors = new int[][]{{row - 1, col}, {row - 1, col - 1}, {row, col - 1}, {row + 1, col - 1}, {row + 1,
                    col}};
            exploreNeighbors(neighbors, queue);
        }
    }

    public void exploreMiddle(int row, int col, ArrayDeque<int[]> queue) {
        int[][] neighbors = new int[8][2];

        if (row > 0) neighbors[0] = new int[]{row - 1, col - 1};
        else neighbors[0] = null;
        if (row > 0) neighbors[1] = new int[]{row - 1, col};
        else neighbors[1] = null;
        if (row > 0) neighbors[2] = new int[]{row - 1, col + 1};
        else neighbors[2] = null;
        neighbors[3] = new int[]{row, col - 1};
        neighbors[4] = new int[]{row, col + 1};
        if (row < 8) neighbors[5] = new int[]{row + 1, col + 1};
        else neighbors[5] = null;
        if (row < 8) neighbors[6] = new int[]{row + 1, col};
        else neighbors[6] = null;
        if (row < 8) neighbors[7] = new int[]{row + 1, col - 1};
        else neighbors[7] = null;

        exploreNeighbors(neighbors, queue);
    }

    public void processUserInput(int row, int col, String input) {
        if (input.equals("mine")) {
            isMarked[row][col] = !isMarked[row][col];
        } else {
            if (field[row][col].equals("X")) {
                revealMines = true;
            } else {
                exploreField(row, col);
            }
        }

        System.out.println("\n" + this + "\n");
        if (revealMines) System.out.println("You stepped on a mine and failed!");
    }

    public void processFirstFree(int row, int col) {
        if (field[row][col].equals("X")) {
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field.length; j++) {
                    field[i][j] = EMPTY_SYMBOL;
                }
            }
            populateMinesRandomly(minesCount);
            processFirstFree(row, col);
        }
    }

    @Override
    public String toString() {
        StringBuilder fieldStr = new StringBuilder();
        fieldStr.append("""
                 |123456789|
                -|---------|
                """);

        for (int i = 0; i < field.length; i++) {
            fieldStr.append(i + 1).append("|");
            for (int j = 0; j < field.length; j++) {
                if (isMarked[i][j])
                    fieldStr.append(MARKED_SYMBOL);
                else if (isMinePresent(i, j) && !revealMines)
                    fieldStr.append(EMPTY_SYMBOL);
                else if (isExplored[i][j] && field[i][j].equals(EMPTY_SYMBOL))
                    fieldStr.append(EXPLORED_SYMBOL);
                else if (isExplored[i][j])
                    fieldStr.append(field[i][j]);
                else
                    fieldStr.append(EMPTY_SYMBOL);
            }
            fieldStr.append("|").append("\n");
        }

        fieldStr.append("-|---------|");

        return fieldStr.toString();
    }
}
