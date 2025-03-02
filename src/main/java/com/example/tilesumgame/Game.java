package com.example.tilesumgame;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.logging.Level;

public class Game extends Application {
    // Logging
    GameLogger logger;

    // Game board dimensions
    private static final int PADDING = 20;
    private static final int TILE_GAP = 5;
    private static final int BOTTOM_PADDING = 50 + PADDING;
    private static final Insets BUTTON_PADDING = new Insets(10, 20, 10, 20);
    private static final int TILE_SIZE = 100;
    private int gridSize = 4;

    // Game board components
    private static Board board;
    private Tile[][] tiles;
    private final GridPane gridPane = new GridPane();

    // Game over and victory components
    private final Text winText = new Text("You win!");
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");

    // Score components
    private final ScoreDisplay scoreDisplay = new ScoreDisplay();

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
        initializeLogger();
        Scene mainMenuScene = createMainMenu(stage);

        stage.setTitle("2048 Game");
        stage.setScene(mainMenuScene);
        stage.show();
    }

    /**
     * Initializes the game logger with the specified log level from the command line arguments.
     */
    private void initializeLogger() {
        String logLevel = getParameters().getNamed().getOrDefault("logLevel", "FINE");
        GameLogger.initialize(Level.parse(logLevel));
        logger = GameLogger.getInstance();
    }

    /**
     * Creates the game scene with the game board and controls.
     *
     * @param stage the primary stage of the application
     * @return the game scene
     */
    private Scene createGameScene(Stage stage) {
        VBox root = new VBox(); // Create a new VBox instance
        int windowSize = TILE_SIZE * gridSize + PADDING;
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(TILE_GAP);
        gridPane.setVgap(TILE_GAP);
        Scene scene = new Scene(root, windowSize, windowSize + BOTTOM_PADDING);
        configureGameComponents(root);
        configureSolverTimeline(stage);
        configureEvents(stage, scene);

        return scene;
    }

    /**
     * Configures the game components for display.
     */
    private void configureGameComponents(VBox root) {
        gameOverText.setFont(Font.font(32));
        winText.setFont(Font.font(32));
        restartButton.setPadding(BUTTON_PADDING);
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(scoreDisplay.getScoreBox(), gridPane, solverButton);
    }

    /**
     * Configures the solver timeline for automatic moves.
     *
     * @param stage the primary stage of the application
     */
    private void configureSolverTimeline(Stage stage) {
        solverTimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            boolean moved = performAutoMove();
            updateBoard(moved, stage);
        }));
        solverTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Configures the event handling for the game scene.
     *
     * @param stage the primary stage of the application
     * @param scene the game scene
     */
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

        restartButton.setOnAction(_ -> restartGame(stage));
        solverButton.setOnAction(_ -> toggleSolver());
    }

    /**
     * Restarts the game with a new board and grid.
     *
     * @param stage the primary stage of the application
     */
    private void restartGame(Stage stage) {
        board = new Board(gridSize);
        gridPane.getChildren().clear();
        initializeGrid();
        updateBoard(true, stage);
        stage.setScene(createGameScene(stage)); // Create a new scene with a new VBox root
        stage.show();
    }

    /**
     * Toggles the solver timeline to start or pause the automatic solver.
     */
    private void toggleSolver() {
        if (solverRunning) {
            solverTimeline.stop();
            solverButton.setText("Start Solver");
        } else {
            solverTimeline.play();
            solverButton.setText("Pause Solver");
        }
        solverRunning = !solverRunning;
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
        startButton.setPadding(BUTTON_PADDING);

        HBox sizeSelectionBox = createSizeSelectionBox();
        configureStartButton(stage, startButton, sizeSelectionBox);

        Button highScoresButton = new Button("High Scores");
        highScoresButton.setPadding(BUTTON_PADDING);
        highScoresButton.setOnAction(_ -> stage.setScene(createHighScoresMenu(stage)));

        mainMenuBox.getChildren().addAll(titleText, sizeSelectionBox, startButton, highScoresButton);

        return new Scene(mainMenuBox, 400, 300);
    }

    /**
     * Creates the board size selection box with buttons for 4x4, 5x5, and 6x6.
     *
     * @return the HBox containing the board size selection buttons
     */
    private HBox createSizeSelectionBox() {
        HBox sizeSelectionBox = new HBox();
        sizeSelectionBox.setAlignment(Pos.CENTER);
        sizeSelectionBox.setSpacing(10);
        Text sizeText = new Text("Board Size:");
        sizeText.setFont(Font.font(16));
        Button size4x4Button = new Button("4x4");
        Button size5x5Button = new Button("5x5");
        Button size6x6Button = new Button("6x6");
        sizeSelectionBox.getChildren().addAll(sizeText, size4x4Button, size5x5Button, size6x6Button);

        final int[] selectedSize = {4};
        sizeSelectionBox.setUserData(selectedSize);

        size4x4Button.setOnAction(_ -> selectedSize[0] = 4);
        size5x5Button.setOnAction(_ -> selectedSize[0] = 5);
        size6x6Button.setOnAction(_ -> selectedSize[0] = 6);

        return sizeSelectionBox;
    }

    /**
     * Configures the start button to initialize the game board and grid.
     *
     * @param stage            the primary stage of the application
     * @param startButton      the start button to configure
     * @param sizeSelectionBox the box containing the board size selection buttons
     */
    private void configureStartButton(Stage stage, Button startButton, HBox sizeSelectionBox) {
        startButton.setOnAction(_ -> {
            int[] selectedSize = (int[]) sizeSelectionBox.getUserData(); // Retrieve user data
            gridSize = selectedSize[0];
            board = new Board(gridSize);
            tiles = new Tile[gridSize][gridSize];
            initializeGrid();
            updateBoard(true, stage);
            stage.setScene(createGameScene(stage));
        });
    }

    /**
     * Creates the high scores menu scene with a list of high scores.
     *
     * @param stage the primary stage of the application
     * @return the high scores menu scene
     */
    private Scene createHighScoresMenu(Stage stage) {
        VBox highScoresBox = new VBox();
        highScoresBox.setAlignment(Pos.CENTER);
        highScoresBox.setSpacing(10);

        Text titleText = new Text("High Scores");
        titleText.setFont(Font.font(32));

        List<String> allScores = ScoreManager.loadScores();
        // Format the scores for display as a numbered list in format "1. score: date"
        StringBuilder formattedScores = new StringBuilder();
        for (int i = 0; i < allScores.size(); i++) {
            String[] parts = allScores.get(i).split("\\|");
            String score = parts[0];
            String date = parts[1];
            formattedScores.append(String.format("%-4s %-10s: %s\n", (i + 1) + ".", score, date));
        }

        // Display the scores in a text area
        TextArea scoresArea = new TextArea(formattedScores.toString());
        scoresArea.setFont(Font.font("Courier New", 14)); // Set monospace font
        scoresArea.setEditable(false);
        scoresArea.setPrefHeight(400);

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(_ -> stage.setScene(createMainMenu(stage)));

        highScoresBox.getChildren().addAll(titleText, scoresArea, backButton);

        return new Scene(highScoresBox, 400, 500);
    }

    /**
     * Performs an automatic move based on the current board state.
     *
     * @return true if a move was performed, false otherwise
     */
    private boolean performAutoMove() {
        boolean moved;
        Direction direction = board.canMerge();
        if (direction != null) {
            logger.log(Level.FINE, "Can merge to: " + direction);
            moved = moveBoard(direction);
        } else {
            moved = tryAllDirections();
        }
        return moved;
    }

    /*
     * Moves the board in the specified direction and returns true if the board was moved.
     *
     * @param direction the direction to move the board
     * @return true if the board was moved, false otherwise
     */
    private boolean moveBoard(Direction direction) {
        return switch (direction) {
            case LEFT -> board.moveLeft();
            case RIGHT -> board.moveRight();
            case UP -> board.moveUp();
            case DOWN -> board.moveDown();
        };
    }

    /**
     * Tries all directions to move the board and returns true if the board was moved.
     *
     * @return true if the board was moved, false otherwise
     */
    private boolean tryAllDirections() {
        boolean moved = false;
        for (Direction dir : Direction.values()) {
            logger.log(Level.FINE, "Cannot merge, moving to: " + dir);
            moved = moveBoard(dir);
            if (moved) {
                break;
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
        ScoreManager.saveScore(board.getScore());
        VBox gameOverBox = new VBox();
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setSpacing(10);
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(_ -> stage.setScene(createMainMenu(stage)));
        gameOverBox.getChildren().addAll(gameOverText, scoreDisplay.getScoreBox(), restartButton, mainMenuButton);
        displayTopScores(gameOverBox);
        Scene gameOverScene = new Scene(gameOverBox, TILE_SIZE * gridSize + PADDING, TILE_SIZE * gridSize + PADDING);
        stage.setScene(gameOverScene);
        stage.show();
    }

    /**
     * Displays the win screen with the 2048 tile and a restart button.
     *
     * @param stage the primary stage of the application
     */
    private void displayWin(Stage stage) {
        ScoreManager.saveScore(board.getScore());
        VBox winBox = new VBox();
        winBox.setAlignment(Pos.CENTER);
        winBox.setSpacing(10);
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(_ -> stage.setScene(createMainMenu(stage)));

        // Display the 2048 tile
        Tile winTile = new Tile(2048, TILE_SIZE);

        winBox.getChildren().addAll(winText, winTile.getStack(), scoreDisplay.getScoreBox(), restartButton, mainMenuButton);
        displayTopScores(winBox);
        Scene winScene = new Scene(winBox, TILE_SIZE * gridSize + PADDING, TILE_SIZE * gridSize + PADDING);
        stage.setScene(winScene);
        stage.show();
    }

    /**
     * Displays the top scores in the given VBox.
     *
     * @param box the VBox to display the top scores in
     */
    private void displayTopScores(VBox box) {
        List<String> topScores = ScoreManager.getTopScores(3);
        StringBuilder formattedScores = new StringBuilder();
        for (int i = 0; i < topScores.size(); i++) {
            String[] parts = topScores.get(i).split("\\|");
            formattedScores.append(String.format("%d. %-10s: %s\n", i + 1, parts[0], parts[1]));
        }
        Text topScoresTitle = new Text("Top Scores");
        topScoresTitle.setFont(Font.font(14)); // Use normal font for title
        Text topScoresText = new Text(formattedScores.toString());
        topScoresText.setFont(Font.font("Courier New", 14)); // Use monospace font for scores
        box.getChildren().addAll(topScoresTitle, topScoresText);
    }

    /**
     * Initializes the visual game tiles based on the current board state.
     */
    private void initializeGrid() {
        gridPane.getChildren().clear(); // Clear the GridPane before adding new tiles
        int[][] boardState = board.getBoardState();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Tile tile = new Tile(boardState[row][col], TILE_SIZE);
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
            scoreDisplay.updateScore(board.getScore());
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