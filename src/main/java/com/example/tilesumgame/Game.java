package com.example.tilesumgame;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Level;

public class Game extends Application {
    // Logging
    GameLogger logger;

    // Game board dimensions
    private int gridSize;
    private static final int tileSize = 100;
    private static final int padding = 20;
    private static final int tileGap = 5;
    private static final int bottomPadding = 50;

    // Game board components
    private static Board board;
    private Tile[][] tiles;
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

        // Set up the main menu scene
        Scene mainMenuScene = createMainMenu(stage);

        stage.setTitle("2048 Game");
        stage.setScene(mainMenuScene);
        stage.show();
    }

    /**
     * Creates the game scene with the game board and controls.
     *
     * @param stage the primary stage of the application
     * @return the game scene
     */
    private Scene createGameScene(Stage stage) {
        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(tileGap);
        gridPane.setVgap(tileGap);
        Scene scene = new Scene(root, windowSize, windowSize + bottomPadding);
        gameOverText.setFont(Font.font(32));
        winText.setFont(Font.font(32));
        restartButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);

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

        // Set up event handling
        configureEvents(stage, scene);

        return scene;
    }

    private void configureEvents(Stage stage, Scene scene) {
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
    }

    /**
     * Creates the main menu scene with a start button and board size selection.
     *
     * @param stage the primary stage of the application
     * @return the main menu scene
     */
    private Scene createMainMenu(Stage stage) {
        VBox mainMenuBox = new VBox();
        mainMenuBox.setAlignment(Pos.CENTER);
        mainMenuBox.setSpacing(10);

        Text titleText = new Text("2048 Game");
        titleText.setFont(Font.font(32));

        Button startButton = new Button("Start Game");
        startButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));

        // Add board size selection
        HBox sizeSelectionBox = new HBox();
        sizeSelectionBox.setAlignment(Pos.CENTER);
        sizeSelectionBox.setSpacing(10);
        Text sizeText = new Text("Board Size:");
        sizeText.setFont(Font.font(16));
        Button size4x4Button = new Button("4x4");
        Button size5x5Button = new Button("5x5");
        Button size6x6Button = new Button("6x6");
        sizeSelectionBox.getChildren().addAll(sizeText, size4x4Button, size5x5Button, size6x6Button);

        // Set default board size
        final int[] selectedSize = {4};

        size4x4Button.setOnAction(event -> selectedSize[0] = 4);
        size5x5Button.setOnAction(event -> selectedSize[0] = 5);
        size6x6Button.setOnAction(event -> selectedSize[0] = 6);

        startButton.setOnAction(event -> {
            gridSize = selectedSize[0];
            board = new Board(selectedSize[0]);
            tiles = new Tile[gridSize][gridSize];
            initializeGrid();
            updateBoard(true, stage);
            stage.setScene(createGameScene(stage));
        });

        mainMenuBox.getChildren().addAll(titleText, sizeSelectionBox, startButton);

        int mainMenuWidth = 400;
        int mainMenuHeight = 300;
        return new Scene(mainMenuBox, mainMenuHeight, mainMenuWidth);
    }

    /**
     * Performs an automatic move based on the current board state.
     *
     * @return true if a move was performed, false otherwise
     */
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
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(event -> stage.setScene(createMainMenu(stage)));
        gameOverBox.getChildren().addAll(gameOverText, scoreText, restartButton, mainMenuButton);
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
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(event -> stage.setScene(createMainMenu(stage)));

        // Display the 2048 tile
        Tile winTile = new Tile(2048, tileSize);

        winBox.getChildren().addAll(winText, winTile.getStack(), scoreText, restartButton, mainMenuButton);
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
            updateScore();
        }
        // Check win/lose conditions
        if (board.hasWon()) {
            displayWin(stage);
        } else if (!board.canMove()) {
            displayGameOver(stage);
        }
    }

    /**
     * Updates the score display based on the current game state.
     */
    private void updateScore() {
        int previousScore = Integer.parseInt(scoreText.getText().split(": ")[1]);
        int currentScore = board.getScore();
        // Animate the score change by scaling the text twice
        if (currentScore > previousScore) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), scoreText);
            st.setFromX(1);
            st.setFromY(1);
            st.setToX(1.5);
            st.setToY(1.5);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        }
        scoreText.setText("Score: " + board.getScore());

       // Change score color based on value
        final int topScore = 1000; // Score at which the color is fully red, approximated by the time the player is nearing 2048
        double ratio = (double) topScore / 255; // Clip score to 255 for RGB color value
        int redValue = Math.min(255, (int) (board.getScore() / ratio));
        scoreText.setFill(Color.rgb(redValue, 0, 0));
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