package com.example.tilesumgame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(4);
    }

    @Test
    public void testInitializeGrid() {
        int[][] grid = board.getBoardState();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                assertEquals(0, grid[row][col], "Grid should be initialized with zeros");
            }
        }
    }

    @Test
    void spawnTile() {
        board.spawnTile();
        int[][] grid = board.getBoardState();
        int nonZeroCount = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != 0) {
                    nonZeroCount++;
                    assertTrue(grid[row][col] == 2 || grid[row][col] == 4, "Spawned tile should be 2 or 4");
                }
            }
        }
        assertEquals(1, nonZeroCount, "Only one tile should be spawned");
    }

    @Test
    void isFull() {
        assertFalse(board.isFull(), "Board should not be full");
        int[][] grid = board.getBoardState();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = 2;
            }
        }
        assertTrue(board.isFull(), "Board should be full");
    }

    @Test
    void moveLeft() {
        board.getBoardState()[0][0] = 2;
        board.getBoardState()[0][1] = 2;
        boolean moved = board.moveLeft();
        assertEquals(4, board.getBoardState()[0][0], "First tile should be 4 after merge");
        assertEquals(0, board.getBoardState()[0][1], "Second tile should be 0 after merge");
    }

    @Test
    void moveRight() {
        board.getBoardState()[0][2] = 2;
        board.getBoardState()[0][3] = 2;
        boolean moved = board.moveRight();
        assertTrue(moved, "Board state should change after moveRight");
        assertEquals(4, board.getBoardState()[0][3], "Last tile should be 4 after merge");
        assertEquals(0, board.getBoardState()[0][2], "Second last tile should be 0 after merge");
    }

    @Test
    void moveUp() {
        board.getBoardState()[0][0] = 2;
        board.getBoardState()[1][0] = 2;
        boolean moved = board.moveUp();
        assertTrue(moved, "Board state should change after moveUp");
        assertEquals(4, board.getBoardState()[0][0], "First tile should be 4 after merge");
        assertEquals(0, board.getBoardState()[1][0], "Second tile should be 0 after merge");
    }

    @Test
    void moveDown() {
        board.getBoardState()[2][0] = 2;
        board.getBoardState()[3][0] = 2;
        boolean moved = board.moveDown();
        assertTrue(moved, "Board state should change after moveDown");
        assertEquals(4, board.getBoardState()[3][0], "Last tile should be 4 after merge");
        assertEquals(0, board.getBoardState()[2][0], "Second last tile should be 0 after merge");
    }

    @Test
    void canMove() {
        assertTrue(board.canMove(), "Board should be able to move");
        // Fill the board with numbers that cannot be merged
        for (int row = 0; row < board.getBoardState().length; row++) {
            for (int col = 0; col < board.getBoardState()[row].length; col++) {
                board.getBoardState()[row][col] = row + col + 1;
            }
        }
        assertFalse(board.canMove(), "Board should not be able to move");
    }

    @Test
    void canMergeHorizontally() {
        // Test merging two tiles horizontally
        board.getBoardState()[0][0] = 2;
        board.getBoardState()[0][1] = 2;
        Direction direction = board.canMerge();
        assertTrue(direction == Direction.LEFT || direction == Direction.RIGHT, "Board should be able to merge horizontally");
    }

    @Test
    void canMergeVertically() {
        // Test merging two tiles vertically
        board.getBoardState()[0][0] = 2;
        board.getBoardState()[1][0] = 2;
        Direction direction = board.canMerge();
        assertTrue(direction == Direction.UP || direction == Direction.DOWN, "Board should be able to merge vertically");
    }

    @Test
    void cannotMerge() {
        // Test when no tiles can be merged
        board.getBoardState()[0][0] = 2;
        board.getBoardState()[0][1] = 4;
        board.getBoardState()[1][0] = 4;
        board.getBoardState()[1][1] = 2;
        Direction direction = board.canMerge();
        assertNull(direction, "Board should not be able to merge");
    }
}