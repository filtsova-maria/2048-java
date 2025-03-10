package com.example.tilesumgame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;

/*
 * Represents the logical game board for the 2048 game of given size.
 * */
public class Board {
    private final int[][] grid;
    private final int gridSize;
    private int score;
    // Logger
    private static final GameLogger logger = GameLogger.getInstance();
    // Audio
    private Clip mergeSound;

    public Board(int size) {
        this.grid = new int[size][size];
        this.gridSize = size;
        this.score = 0;
        initializeGrid();
        loadSoundEffect();
    }

    /**
     * Initializes game sound effects.
     */
    private void loadSoundEffect() {
        try {
            URL soundURL = getClass().getResource("/sounds/boop.wav");
            if (soundURL != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                this.mergeSound = AudioSystem.getClip();
                mergeSound.open(audioInputStream);
            } else {
                System.err.println("Sound file not found: /sounds/boop.wav");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading sound effect: " + e.getMessage());
        }
    }

    /**
     * Returns a copy of the current state of the game board.
     *
     * @return values of the tiles on the board.
     */
    public int[][] getBoardState() {
        int[][] copy = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, gridSize);
        }
        return copy;
    }

    /**
     * Returns the current score of the game.
     *
     * @return the current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Initializes the game board with zeros.
     */
    private void initializeGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = 0;
            }
        }
    }

    /**
     * A method to print the game board.
     *
     * @return a string representation of the game board as a grid of numbers.
     */
    public String printGrid() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                sb.append(String.format("%4d", grid[row][col])).append("\t");
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Spawns a new tile on the game board.
     */
    public void spawnTile() {
        if (isFull()) {
            return;
        }
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(gridSize);
            col = rand.nextInt(gridSize);
        } while (grid[row][col] != 0);
        int value = rand.nextDouble() < 0.9 ? 2 : 4;
        grid[row][col] = value;
    }

    /**
     * Checks if the game board is full.
     *
     * @return true if the board is full, false otherwise.
     */
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

    /**
     * Checks if the game board can move in any direction.
     *
     * @return true if the board can move, false otherwise.
     */
    public boolean canMove() {
        if (!isFull()) {
            return true;
        }
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = grid[row][col];
                boolean canMerge = (col < gridSize - 1 && value == grid[row][col + 1]) || (row < gridSize - 1 && value == grid[row + 1][col]);
                if (canMerge) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the game board can merge any tiles and returns the direction.
     *
     * @return the direction in which the board can merge, or null if no merge is possible.
     */
    public Direction canMerge() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = grid[row][col];
                if (value == 0) {
                    continue;
                }
                if (col < gridSize - 1 && value == grid[row][col + 1]) {
                    return Direction.RIGHT;
                }
                if (row < gridSize - 1 && value == grid[row + 1][col]) {
                    return Direction.DOWN;
                }
                if (col > 0 && value == grid[row][col - 1]) {
                    return Direction.LEFT;
                }
                if (row > 0 && value == grid[row - 1][col]) {
                    return Direction.UP;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the player got the 2048 tile and therefore has won the game.
     *
     * @return true if the player has won, false otherwise.
     */
    public boolean hasWon() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Moves the tiles on the game board to the left.
     *
     * @return true if any tiles were moved, false otherwise.
     */
    public boolean moveLeft() {
        return move((row, col) -> grid[row][col], (row, col, value) -> grid[row][col] = value);
    }

    /**
     * Moves the tiles on the game board to the right.
     *
     * @return true if any tiles were moved, false otherwise.
     */
    public boolean moveRight() {
        return move((row, col) -> grid[row][gridSize - 1 - col], (row, col, value) -> grid[row][gridSize - 1 - col] = value);
    }

    /**
     * Moves the tiles on the game board up.
     *
     * @return true if any tiles were moved, false otherwise.
     */
    public boolean moveUp() {
        return move((row, col) -> grid[col][row], (row, col, value) -> grid[col][row] = value);
    }

    /**
     * Moves the tiles on the game board down.
     *
     * @return true if any tiles were moved, false otherwise.
     */
    public boolean moveDown() {
        return move((row, col) -> grid[gridSize - 1 - col][row], (row, col, value) -> grid[gridSize - 1 - col][row] = value);
    }

    /**
     * Functional interface to get the value at a given row and column.
     */
    @FunctionalInterface
    private interface ValueGetter {
        int getValue(int row, int col);
    }

    /**
     * Functional interface to set the value at a given row and column.
     */
    @FunctionalInterface
    private interface ValueSetter {
        void setValue(int row, int col, int value);
    }

    /**
     * Moves the tiles on the game board in a given direction.
     *
     * @param getter the function to get the value at a given row and column
     * @param setter the function to set the value at a given row and column
     * @return true if any tiles were moved, false otherwise.
     */
    private boolean move(ValueGetter getter, ValueSetter setter) {
        boolean moved = false;
        // Move each row of the grid to the direction given by getter and setter
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = 0;
            for (int col = 0; col < gridSize; col++) {
                int value = getter.getValue(row, col);
                if (value != 0) {
                    newRow[position++] = value;
                }
            }
            // Merge adjacent tiles with the same value
            for (int col = 0; col < gridSize - 1; col++) {
                if (newRow[col] != 0 && newRow[col] == newRow[col + 1]) {
                    newRow[col] *= 2;
                    newRow[col + 1] = 0;
                    score += newRow[col]; // Increment score by the merged value
                    moved = true;
                    // Play merge sound
                    if (mergeSound != null) {
                        mergeSound.setFramePosition(0); // Rewind to the beginning
                        mergeSound.start();
                    }
                }
            }
            position = 0;
            int[] finalRow = new int[gridSize];
            for (int col = 0; col < gridSize; col++) {
                if (newRow[col] != 0) {
                    finalRow[position++] = newRow[col];
                }
            }
            // Update the grid with the new row if it is different from the original row
            for (int col = 0; col < gridSize; col++) {
                if (getter.getValue(row, col) != finalRow[col]) {
                    setter.setValue(row, col, finalRow[col]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    @Override
    public String toString() {
        return printGrid();
    }
}
