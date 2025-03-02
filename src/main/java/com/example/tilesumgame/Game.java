package com.example.tilesumgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Level;

public class Game extends Application {
    // Logging
    GameLogger logger;

    // Game board dimensions
    private static final int gridSize = 4;
    private static final int tileSize = 100;
    private static final int padding = 20;
    private static final int tileGap = 5;
    private static final int bottomPadding = 50;

    // Game board components
    private static Board board;
    private final Tile[][] tiles = new Tile[gridSize][gridSize];
    private final GridPane gridPane = new GridPane();
    private final VBox root = new VBox();

    // Game over and victory components
    private final Text winText = new Text("You win!");
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");

    // Score components
    private final Text scoreText = new Text("Score: 0");

    // Solver components
    private final Button solverButton = new Button("Start Solver");
    private Timeline solverTimeline;
    private boolean solverRunning = false;

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
        logger = GameLogger.getInstance();

        // Set up the JavaFX display components
        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(tileGap);
        gridPane.setVgap(tileGap);
        Scene scene = new Scene(root, windowSize, windowSize + bottomPadding);
        gameOverText.setFont(Font.font(32));
        winText.setFont(Font.font(32));
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);

        // Initialize the game board
        board = new Board(gridSize);
        initializeGrid();
        updateBoard(true, stage);

        // Set up score display
        HBox scoreBox = new HBox();
        scoreBox.setAlignment(Pos.TOP_CENTER);
        scoreText.setFont(Font.font(24));
        scoreBox.getChildren().add(scoreText);

        // Add the game components to the scene
        root.getChildren().addAll(scoreBox, gridPane, solverButton);

        // Set up solver timeline logic
        solverTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            boolean moved = performAutoMove();
            updateBoard(moved, stage);
        }));
        solverTimeline.setCycleCount(Timeline.INDEFINITE);

        // Set up key event handling
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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
            updateBoard(moved, stage);
        });

        // Set up restart button
        restartButton.setOnAction(_ -> {
            board = new Board(gridSize);
            gridPane.getChildren().clear();
            initializeGrid();
            updateBoard(true, stage);
            stage.setScene(scene);
            stage.show();
        });

        // Set up solver button
        solverButton.setOnAction(event -> {
            if (solverRunning) {
                solverTimeline.stop();
                solverButton.setText("Start Solver");
            } else {
                solverTimeline.play();
                solverButton.setText("Pause Solver");
            }
            solverRunning = !solverRunning;
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();
    }

    private boolean performAutoMove() {
        boolean moved = false;
        Direction direction = board.canMerge();
        if (direction != null) {
            logger.log(Level.FINE, "Can merge to: " + direction);
            switch (direction) {
                case LEFT:
                    moved = board.moveLeft();
                    break;
                case RIGHT:
                    moved = board.moveRight();
                    break;
                case UP:
                    moved = board.moveUp();
                    break;
                case DOWN:
                    moved = board.moveDown();
                    break;
            }
        } else {
            for (Direction dir : Direction.values()) {
                logger.log(Level.FINE, "Cannot merge, moving to: " + dir);
                switch (dir) {
                    case LEFT:
                        moved = board.moveLeft();
                        break;
                    case RIGHT:
                        moved = board.moveRight();
                        break;
                    case UP:
                        moved = board.moveUp();
                        break;
                    case DOWN:
                        moved = board.moveDown();
                        break;
                }
                if (moved) {
                    break;
                }
            }
        }
        return moved;
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
    private void updateBoard(boolean displayChanged, Stage stage) {
        if (displayChanged) {
            int[][] preSpawnBoardState = board.getBoardState();
            logger.log(Level.FINE, board::printGrid);  // Log grid state before a new tile spawns
            board.spawnTile();
            logger.log(Level.FINE, board::printGrid);  // Log grid state after a new tile spawns

            int[][] postSpawnBoardState = board.getBoardState();
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    tiles[row][col].setValue(postSpawnBoardState[row][col]);
                    // Animate the new tile spawn
                    boolean isNewTile = postSpawnBoardState[row][col] != 0 && preSpawnBoardState[row][col] == 0;
                    if (isNewTile) {
                        tiles[row][col].animateSpawn();
                    }
                }
            }
            scoreText.setText("Score: " + board.getScore());
        }
        // Check win/lose conditions
        if (board.hasWon()) {
            displayWin(stage);
        } else if (!board.canMove()) {
            displayGameOver(stage);
        }
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}