package com.example.tilesumgame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class test extends Application {
    private static final int gridSize = 4;
    private static final int tileSize = 100;
    private static final int padding = 20;

    private final Tile[][] tiles = new Tile[gridSize][gridSize];
    private final GridPane gridPane = new GridPane();
    private final VBox root = new VBox();
    private final Text gameOverText = new Text("You lose!");
    private final Button restartButton = new Button("Restart");

    @Override
    public void start(Stage stage) throws IOException {
        int windowSize = tileSize * gridSize + padding;
        gridPane.setAlignment(Pos.CENTER);
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
            if (moved) {
                if (!isBoardFull()) {
                    spawnTile();
                } else {
                    displayGameOver();
                }
            }
        });

        stage.setTitle("2048 Game");
        stage.setScene(scene);
        stage.show();
    }

    private void displayGameOver() {
        gameOverText.setFont(Font.font(36));
        gameOverText.setFill(Color.RED);
        root.getChildren().clear();
        VBox gameOverLayout = new VBox(10, gameOverText, restartButton);
        gameOverLayout.setAlignment(Pos.CENTER);
        root.getChildren().add(gameOverLayout);
    }

    private void resetGame(Stage stage) {
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
    }

    private void initializeGrid() {
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Tile tile = new Tile(0);
                tiles[row][col] = tile;
                gridPane.add(tile.getStack(), col, row);
            }
        }
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
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = 0;
            for (int col = 0; col < gridSize; col++) {
                if (tiles[row][col].getValue() != 0) {
                    newRow[position++] = tiles[row][col].getValue();
                }
            }
            if (mergeRow(newRow)) {
                moved = true;
            }
            if (updateRow(row, newRow)) {
                moved = true;
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < gridSize; row++) {
            int[] newRow = new int[gridSize];
            int position = gridSize - 1;
            for (int col = gridSize - 1; col >= 0; col--) {
                if (tiles[row][col].getValue() != 0) {
                    newRow[position--] = tiles[row][col].getValue();
                }
            }
            if (mergeRowReverse(newRow)) {
                moved = true;
            }
            if (updateRow(row, newRow)) {
                moved = true;
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = 0;
            for (int row = 0; row < gridSize; row++) {
                if (tiles[row][col].getValue() != 0) {
                    newCol[position++] = tiles[row][col].getValue();
                }
            }
            if (mergeRow(newCol)) {
                moved = true;
            }
            if (updateColumn(col, newCol)) {
                moved = true;
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < gridSize; col++) {
            int[] newCol = new int[gridSize];
            int position = gridSize - 1;
            for (int row = gridSize - 1; row >= 0; row--) {
                if (tiles[row][col].getValue() != 0) {
                    newCol[position--] = tiles[row][col].getValue();
                }
            }
            if (mergeRowReverse(newCol)) {
                moved = true;
            }
            if (updateColumn(col, newCol)) {
                moved = true;
            }
        }
        return moved;
    }

    private boolean mergeRow(int[] values) {
        boolean merged = false;
        for (int i = 0; i < gridSize - 1; i++) {
            if (values[i] != 0 && values[i] == values[i + 1]) {
                values[i] *= 2;
                values[i + 1] = 0;
                merged = true;
            }
        }
        compactValues(values);
        return merged;
    }

    private boolean mergeRowReverse(int[] values) {
        boolean merged = false;
        for (int i = gridSize - 1; i > 0; i--) {
            if (values[i] != 0 && values[i] == values[i - 1]) {
                values[i] *= 2;
                values[i - 1] = 0;
                merged = true;
            }
        }
        compactValues(values);
        return merged;
    }

    private void compactValues(int[] values) {
        int position = 0;
        int[] temp = new int[gridSize];
        for (int value : values) {
            if (value != 0) {
                temp[position++] = value;
            }
        }
        System.arraycopy(temp, 0, values, 0, gridSize);
    }

    private boolean updateRow(int row, int[] values) {
        boolean changed = false;
        for (int col = 0; col < gridSize; col++) {
            if (tiles[row][col].getValue() != values[col]) {
                tiles[row][col].setValue(values[col]);
                changed = true;
            }
        }
        return changed;
    }

    private boolean updateColumn(int col, int[] values) {
        boolean changed = false;
        for (int row = 0; row < gridSize; row++) {
            if (tiles[row][col].getValue() != values[row]) {
                tiles[row][col].setValue(values[row]);
                changed = true;
            }
        }
        return changed;
    }

    public static void main(String[] args) {
        launch();
    }

    private static class Tile {
        private int value;
        private final StackPane stack;
        private final Rectangle background;
        private final Text text;

        public Tile(int value) {
            this.value = value;
            this.stack = new StackPane();
            this.background = new Rectangle(tileSize - 5, tileSize - 5);
            this.background.setFill(Color.LIGHTGRAY);
            this.text = new Text(value == 0 ? "" : String.valueOf(value));
            this.text.setFont(Font.font(24));
            this.stack.getChildren().addAll(background, text);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
            text.setText(value == 0 ? "" : String.valueOf(value));
            updateAppearance();
        }

        private void updateAppearance() {
            background.setFill(getColorForValue(value));
        }

        private Color getColorForValue(int value) {
            return switch (value) {
                case 2 -> Color.BEIGE;
                case 4 -> Color.LIGHTYELLOW;
                case 8 -> Color.LIGHTGOLDENRODYELLOW;
                case 16 -> Color.GOLD;
                case 32 -> Color.ORANGE;
                case 64 -> Color.DARKORANGE;
                case 128 -> Color.LIGHTSALMON;
                case 256 -> Color.SALMON;
                case 512 -> Color.TOMATO;
                case 1024 -> Color.ORANGERED;
                case 2048 -> Color.RED;
                default -> Color.LIGHTGRAY;
            };
        }

        public StackPane getStack() {
            return stack;
        }
    }
}
