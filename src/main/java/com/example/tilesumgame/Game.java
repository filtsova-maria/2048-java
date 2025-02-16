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

public class Game extends Application {
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
        return false;
    }

    private boolean moveRight() {
        return false;

    }

    private boolean moveUp() {
        return false;

    }

    private boolean moveDown() {
        return false;

    }

    private boolean mergeUp(int[] values) {
        return false;

    }

    private boolean mergeDown(int[] values) {
        return false;

    }

    private boolean mergeLeft(int[] values) {
        return false;

    }

    private boolean mergeRight(int[] values) {
        return false;

    }

    public static void main(String[] args) {
        launch();
    }

}