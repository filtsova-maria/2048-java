package com.example.tilesumgame;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents a tile in the 2048 game grid.
 * Each tile has an integer value and a visual representation using JavaFX components.
 */
public class Tile {
    private int value;
    private final StackPane stack;
    private final Rectangle background;
    private final Text text;

    /**
     * Constructs a new Tile with the specified value and size.
     *
     * @param value    the initial value of the tile
     * @param tileSize the size of the tile in pixels
     */
    public Tile(int value, int tileSize) {
        this.value = value;
        this.stack = new StackPane();
        this.background = new Rectangle(tileSize - 5, tileSize - 5);
        this.background.setFill(Color.LIGHTGRAY);
        this.text = new Text(value == 0 ? "" : String.valueOf(value));
        this.text.setFont(Font.font(24));
        this.stack.getChildren().addAll(background, text);
    }

    /**
     * Returns the current value of the tile.
     *
     * @return the value of the tile
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets a new value for the tile and updates its appearance.
     *
     * @param value the new value of the tile
     */
    public void setValue(int value) {
        this.value = value;
        text.setText(value == 0 ? "" : String.valueOf(value));
        updateAppearance();
    }

    /**
     * Updates the color of the tile based on its value.
     */
    private void updateAppearance() {
        background.setFill(getColorForValue(value));
    }

    /**
     * Determines the color of the tile based on its value. The color gets more intense as the value gets higher.
     *
     * @param value the value of the tile
     * @return the corresponding color for the tile
     */
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

    /**
     * Returns the visual component representing this tile.
     *
     * @return the StackPane containing the tile's background and text
     */
    public StackPane getStack() {
        return stack;
    }
}
