// src/main/java/com/example/tilesumgame/ScoreDisplay.java
package com.example.tilesumgame;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/*
 * Displays the current score and animates score changes.
 */
public class ScoreDisplay {
    private final HBox scoreBox;
    private final Text scoreText;
    private int currentScore;

    /**
     * Creates a new ScoreDisplay with an initial score of 0.
     */
    public ScoreDisplay() {
        scoreBox = new HBox();
        scoreBox.setAlignment(Pos.TOP_LEFT);
        scoreBox.setPadding(new Insets(10, 20, 10, 20));
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font(24));
        scoreBox.getChildren().add(scoreText);
        currentScore = 0;
    }

    /**
     * Gets the HBox containing the score display.
     *
     * @return the HBox containing the score display
     */
    public HBox getScoreBox() {
        return scoreBox;
    }

    /**
     * Updates the score display with the new score and animates the score change.
     *
     * @param newScore the new score to display
     */
    public void updateScore(int newScore) {
        int scoreChange = newScore - currentScore;
        currentScore = newScore;
        scoreText.setText("Score: " + currentScore);

        // Change score color based on value
        final int topScore = 10_000; // Score at which the color is fully red
        double ratio = (double) topScore / 255; // Clip score to 255 for RGB color value
        int redValue = Math.min(255, (int) (currentScore / ratio));
        scoreText.setFill(Color.rgb(redValue, 0, 0));

        if (scoreChange > 0) {
            animateScoreChange(scoreChange);
        }
    }

    /**
     * Animates the score change by scaling the score text and displaying the score change above it.
     *
     * @param scoreChange the change in score to animate
     */
    private void animateScoreChange(int scoreChange) {
        // Calculate the scaling factor and color intensity based on scoreChange
        double scaleFactor = 1 + Math.min(scoreChange / 100.0, 0.5); // Limit the scale factor to a maximum of 1.5
        int redValue = Math.min(255, scoreChange * 2); // Limit the red value to a maximum of 255

        // Animate the score change by scaling the text
        ScaleTransition st = new ScaleTransition(Duration.millis(200), scoreText);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(scaleFactor);
        st.setToY(scaleFactor);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();

        // Display the score change above the score text
        Text scoreChangeText = new Text("+" + scoreChange);
        scoreChangeText.setFont(Font.font(24));
        scoreChangeText.setFill(Color.rgb(redValue, 0, 0));

        scoreBox.getChildren().add(scoreChangeText);

        // Animate the score change text with a fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), scoreChangeText);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(event -> scoreBox.getChildren().remove(scoreChangeText));
        fadeTransition.play();
    }
}