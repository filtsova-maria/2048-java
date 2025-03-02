package com.example.tilesumgame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.logging.Level;

public class Game extends Application {
    private static final int gridSize = 4;
    private static final int tileSize = 100;
    private static final int padding = 20;
    private static final int tileGap = 5;

    private static Board board;

    private final Tile[][] tiles = new Tile[gridSize][gridSize];
    private final GridPane gridPane = new GridPane();
    private final Text winText = new Text("You win!");
    private final VBox root = new VBox();
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");
    private final Text scoreText = new Text("Score: 0");

    /**
     * Launches the JavaFX application.
     *
     * @param stage the primary stage of the application
     */
    @Override
    public void start(Stage stage) {
        // Set up logging from the command line arguments
        String logLevel = getParameters().getNamed().getOrDefault("logLevel", "FINE");
        GameLogger.initialize(Level.parse(logLevel));
        GameLogger logger = GameLogger.getInstance();
        board = new Board(gridSize);

        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(tileGap);
        gridPane.setVgap(tileGap);
        Scene scene = new Scene(root, windowSize, windowSize);

        initializeGrid();
        board.spawnTile();
        logger.log(Level.FINE, board::printGrid);  // Log initial grid state
        updateBoard();

        HBox scoreBox = new HBox();
        scoreBox.setAlignment(Pos.CENTER_LEFT);
        scoreText.setFont(Font.font(24));
        scoreBox.getChildren().add(scoreText);
        root.getChildren().addAll(scoreBox, gridPane);

        gameOverText.setFont(Font.font(32));
        winText.setFont(Font.font(32));

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
                updateBoard();
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
            updateBoard();
            stage.setScene(scene);
            stage.show();
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays the game over screen with a restart button.
     *
     * @param stage the primary stage of the application
     */
    private void displayGameOver(Stage stage) {
        VBox gameOverBox = new VBox();
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setSpacing(10);
        gameOverBox.getChildren().addAll(gameOverText, restartButton);
        Scene gameOverScene = new Scene(gameOverBox, tileSize * gridSize + padding, tileSize * gridSize + padding);
        stage.setScene(gameOverScene);
        stage.show();
    }

    /**
     * Displays the win screen with the 2048 tile and a restart button.
     *
     * @param stage the primary stage of the application
     */
    private void displayWin(Stage stage) {
        VBox winBox = new VBox();
        winBox.setAlignment(Pos.CENTER);
        winBox.setSpacing(10);

        // Display the 2048 tile
        Tile winTile = new Tile(2048, tileSize);

        winBox.getChildren().addAll(winText, winTile.getStack(), scoreText, restartButton);
        Scene winScene = new Scene(winBox, tileSize * gridSize + padding, tileSize * gridSize + padding);
        stage.setScene(winScene);
        stage.show();
    }

    /**
     * Initializes the visual game tiles based on the current board state.
     */
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

    /**
     * Updates the visuals based on the current board state.
     */
    private void updateBoard() {
        int[][] boardState = board.getBoardState();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                tiles[row][col].setValue(boardState[row][col]);
            }
        }
        scoreText.setText("Score: " + board.getScore());
    }

    // TODO: animations

    public static void main(String[] args) {
        launch();
    }

}