package com.example.tilesumgame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Game extends Application {
    private static final Logger logger = Logger.getLogger(Game.class.getName());

    private static final int gridSize = 4;
    private static final int tileSize = 100;
    private static final int padding = 20;
    private static final int tileGap = 5;

    private final Tile[][] tiles = new Tile[gridSize][gridSize];
    private final GridPane gridPane = new GridPane();
    private final VBox root = new VBox();
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");

    @Override
    public void start(Stage stage) throws IOException {
        // Set up logging
        logger.setLevel(Level.FINE);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        logger.addHandler(consoleHandler);

        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(tileGap);
        gridPane.setVgap(tileGap);
        Scene scene = new Scene(gridPane, windowSize, windowSize);

        initializeGrid();
        spawnTile();

        scene.setOnKeyPressed(event -> {
            boolean moved = false;
            if (event.getCode() == KeyCode.LEFT) {
                moved = moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                moved = moveRight();
            } else if (event.getCode() == KeyCode.UP) {
                moved = moveUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                moved = moveDown();
            }
            if (moved && !isBoardFull()) {
                spawnTile();
            }
            logger.fine(this::printGrid); // Log grid state after each move
        });

        restartButton.setOnAction(_ -> {
            root.getChildren().clear();
            gridPane.getChildren().clear();
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    tiles[row][col].setValue(0);
                }
            }
            root.getChildren().add(gridPane);
            initializeGrid();
            spawnTile();
            stage.show();
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();

        logger.fine(this::printGrid); // Log initial grid state
    }

    private void initializeGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Tile tile = new Tile(0, tileSize);
                tiles[row][col] = tile;
                gridPane.add(tile.getStack(), col, row);
            }
        }
    }

    private String printGrid() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                sb.append(tiles[row][col].getValue()).append("\t");
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private void spawnTile() {
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(gridSize);
            col = rand.nextInt(gridSize);
        } while (tiles[row][col].getValue() != 0);
        int value = rand.nextDouble() < 0.9 ? 2 : 4;
        tiles[row][col].setValue(value);
    }

    private boolean isBoardFull() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (tiles[row][col].getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean moveLeft() {
        // TODO: refactor this method to avoid code duplication between different directions
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = 0;
            // Move non-zero values to the left
            for (int col = 0; col < gridSize; col++) {
                if (tiles[row][col].getValue() != 0) {
                    newRow[position++] = tiles[row][col].getValue();
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
                if (tiles[row][col].getValue() != finalRow[col]) {
                    tiles[row][col].setValue(finalRow[col]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = gridSize - 1;
            // Move non-zero values to the right
            for (int col = gridSize - 1; col >= 0; col--) {
                if (tiles[row][col].getValue() != 0) {
                    newRow[position--] = tiles[row][col].getValue();
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
                if (tiles[row][col].getValue() != finalRow[col]) {
                    tiles[row][col].setValue(finalRow[col]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = 0;
            // Move non-zero values up
            for (int row = 0; row < gridSize; row++) {
                if (tiles[row][col].getValue() != 0) {
                    newCol[position++] = tiles[row][col].getValue();
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
                if (tiles[row][col].getValue() != finalCol[row]) {
                    tiles[row][col].setValue(finalCol[row]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = gridSize - 1;
            // Move non-zero values down
            for (int row = gridSize - 1; row >= 0; row--) {
                if (tiles[row][col].getValue() != 0) {
                    newCol[position--] = tiles[row][col].getValue();
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
                if (tiles[row][col].getValue() != finalCol[row]) {
                    tiles[row][col].setValue(finalCol[row]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    public static void main(String[] args) {
        launch();
    }

}