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

import java.util.logging.Level;

public class Game extends Application {
    private static final int gridSize = 4;
    private static final int tileSize = 100;
    private static final int padding = 20;
    private static final int tileGap = 5;

    private static Board board;

    private final Tile[][] tiles = new Tile[gridSize][gridSize];
    private final GridPane gridPane = new GridPane();
    private final VBox root = new VBox();
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");

    @Override
    public void start(Stage stage) throws IOException {
        // Set up logging
        String logLevel = getParameters().getNamed().getOrDefault("logLevel", "FINE");
        GameLogger.initialize(Level.parse(logLevel));
        GameLogger logger = GameLogger.getInstance();
        board = new Board(gridSize);

        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(tileGap);
        gridPane.setVgap(tileGap);
        Scene scene = new Scene(gridPane, windowSize, windowSize);

        initializeGrid();
        board.spawnTile();
        updateTiles();

        scene.setOnKeyPressed(event -> {
            boolean moved = false;
            if (event.getCode() == KeyCode.LEFT) {
                moved = board.moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                moved = board.moveRight();
            } else if (event.getCode() == KeyCode.UP) {
                moved = board.moveUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                moved = board.moveDown();
            }
            logger.log(Level.FINE, board::printGrid);  // Log grid state before a new tile spawns
            if (moved && !board.isFull()) {
                board.spawnTile();
            }
            updateTiles();
            logger.log(Level.FINE, board::printGrid);  // Log grid state after a new tile spawns
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
            board.spawnTile();
            stage.show();
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();
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

    private void updateTiles() {
        int[][] boardState = board.getBoardState();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                tiles[row][col].setValue(boardState[row][col]);
            }
        }
    }

    // TODO: animations
    // TODO: win condition, screen
    // TODO: game over condition, screen
    // TODO: score

    public static void main(String[] args) {
        launch();
    }

}