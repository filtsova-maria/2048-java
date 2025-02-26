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
    private final Text winText = new Text("You win!");
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
        logger.log(Level.FINE, board::printGrid);  // Log initial grid state
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
            if (moved) {
                logger.log(Level.FINE, board::printGrid);  // Log grid state before a new tile spawns
                board.spawnTile();
                logger.log(Level.FINE, board::printGrid);  // Log grid state after a new tile spawns
                updateTiles();
            }
            if (board.hasWon()) {
                displayWin(stage);
            } else if (!board.canMove()) {
                displayGameOver(stage);
            }
        });

        restartButton.setOnAction(_ -> {
            board = new Board(gridSize);
            gridPane.getChildren().clear();
            initializeGrid();
            board.spawnTile();
            updateTiles();
            stage.setScene(scene);
            stage.show();
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();
    }

    private void displayGameOver(Stage stage) {
        VBox gameOverBox = new VBox();
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setSpacing(10);
        gameOverBox.getChildren().addAll(gameOverText, restartButton);
        Scene gameOverScene = new Scene(gameOverBox, tileSize * gridSize + padding, tileSize * gridSize + padding);
        stage.setScene(gameOverScene);
        stage.show();
    }

    private void displayWin(Stage stage) {
        VBox winBox = new VBox();
        winBox.setAlignment(Pos.CENTER);
        winBox.setSpacing(10);

        // Display the 2048 tile
        Tile winTile = new Tile(2048, tileSize);

        winBox.getChildren().addAll(winText, winTile.getStack(), restartButton);
        Scene winScene = new Scene(winBox, tileSize * gridSize + padding, tileSize * gridSize + padding);
        stage.setScene(winScene);
        stage.show();
    }

    private void initializeGrid() {
        int[][] boardState = board.getBoardState();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Tile tile = new Tile(boardState[row][col], tileSize);
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

    // TODO: calculate and display score
    // TODO: animations

    public static void main(String[] args) {
        launch();
    }

}