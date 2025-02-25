package com.example.tilesumgame;

import java.util.Random;
import java.util.logging.Level;

/*
 * Represents the logical game board for the 2048 game of given size.
 * */
public class Board {
    private final int[][] grid;
    private final int gridSize;
    private final GameLogger logger;

    public Board(int size) {
        this.grid = new int[size][size];
        this.gridSize = size;
        this.logger = GameLogger.getInstance();
        initializeGrid();
    }

    public int[][] getBoardState() {
        return grid;
    }

    private void initializeGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = 0;
            }
        }
    }

    public String printGrid() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                sb.append(grid[row][col]).append("\t");
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    public void spawnTile() {
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(gridSize);
            col = rand.nextInt(gridSize);
        } while (grid[row][col] != 0);
        int value = rand.nextDouble() < 0.9 ? 2 : 4;
        grid[row][col] = value;
        this.logger.log(Level.FINE, this::printGrid);
    }

    public boolean isFull() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = 0;
            // Move non-zero values to the left
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != 0) {
                    newRow[position++] = grid[row][col];
                }
            }
            // Merge adjacent equal values
            for (int col = 0; col < gridSize - 1; col++) {
                if (newRow[col] != 0 && newRow[col] == newRow[col + 1]) {
                    newRow[col] *= 2;
                    newRow[col + 1] = 0;
                    moved = true;
                }
            }
            // Move non-zero values to the left again
            position = 0;
            int[] finalRow = new int[gridSize];
            for (int col = 0; col < gridSize; col++) {
                if (newRow[col] != 0) {
                    finalRow[position++] = newRow[col];
                }
            }
            // Update the row and check if it has changed
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != finalRow[col]) {
                    grid[row][col] = finalRow[col];
                    moved = true;
                }
            }
        }
        return moved;
    }

    public boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = gridSize - 1;
            // Move non-zero values to the right
            for (int col = gridSize - 1; col >= 0; col--) {
                if (grid[row][col] != 0) {
                    newRow[position--] = grid[row][col];
                }
            }
            // Merge adjacent equal values
            for (int col = gridSize - 1; col > 0; col--) {
                if (newRow[col] != 0 && newRow[col] == newRow[col - 1]) {
                    newRow[col] *= 2;
                    newRow[col - 1] = 0;
                    moved = true;
                }
            }
            // Move non-zero values to the right again
            position = gridSize - 1;
            int[] finalRow = new int[gridSize];
            for (int col = gridSize - 1; col >= 0; col--) {
                if (newRow[col] != 0) {
                    finalRow[position--] = newRow[col];
                }
            }
            // Update the row and check if it has changed
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != finalRow[col]) {
                    grid[row][col] = finalRow[col];
                    moved = true;
                }
            }
        }
        return moved;
    }

    public boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = 0;
            // Move non-zero values up
            for (int row = 0; row < gridSize; row++) {
                if (grid[row][col] != 0) {
                    newCol[position++] = grid[row][col];
                }
            }
            // Merge adjacent equal values
            for (int row = 0; row < gridSize - 1; row++) {
                if (newCol[row] != 0 && newCol[row] == newCol[row + 1]) {
                    newCol[row] *= 2;
                    newCol[row + 1] = 0;
                    moved = true;
                }
            }
            // Move non-zero values up again
            position = 0;
            int[] finalCol = new int[gridSize];
            for (int row = 0; row < gridSize; row++) {
                if (newCol[row] != 0) {
                    finalCol[position++] = newCol[row];
                }
            }
            // Update the column and check if it has changed
            for (int row = 0; row < gridSize; row++) {
                if (grid[row][col] != finalCol[row]) {
                    grid[row][col] = finalCol[row];
                    moved = true;
                }
            }
        }
        return moved;
    }

    public boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = gridSize - 1;
            // Move non-zero values down
            for (int row = gridSize - 1; row >= 0; row--) {
                if (grid[row][col] != 0) {
                    newCol[position--] = grid[row][col];
                }
            }
            // Merge adjacent equal values
            for (int row = gridSize - 1; row > 0; row--) {
                if (newCol[row] != 0 && newCol[row] == newCol[row - 1]) {
                    newCol[row] *= 2;
                    newCol[row - 1] = 0;
                    moved = true;
                }
            }
            // Move non-zero values down again
            position = gridSize - 1;
            int[] finalCol = new int[gridSize];
            for (int row = gridSize - 1; row >= 0; row--) {
                if (newCol[row] != 0) {
                    finalCol[position--] = newCol[row];
                }
            }
            // Update the column and check if it has changed
            for (int row = 0; row < gridSize; row++) {
                if (grid[row][col] != finalCol[row]) {
                    grid[row][col] = finalCol[row];
                    moved = true;
                }
            }
        }
        return moved;
    }
}
