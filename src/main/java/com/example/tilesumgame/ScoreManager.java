package com.example.tilesumgame;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages the scores of the players in the game.
 * The scores are saved to a file and can be loaded and displayed.
 */
public class ScoreManager {
    private static final String SCORE_FILE = "scores.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Saves the player's score to a file with the current date and time in the format "score|date".
     *
     * @param score the player's score to save
     */
    public static void saveScore(int score) {
        String dateTime = LocalDateTime.now().format(DATE_FORMATTER);
        String scoreEntry = score + "|" + dateTime;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SCORE_FILE), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(scoreEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the scores from the file and returns them as a list of strings sorted by score in descending order.
     *
     * @return a sorted list of strings representing the scores in the format "score|date" in descending order
     */
    public static List<String> loadScores() {
        List<String> scores = new ArrayList<>();
        Path scoreFilePath = Paths.get(SCORE_FILE);
        // Create the score file if it does not exist
        if (!Files.exists(scoreFilePath)) {
            try {
                Files.createFile(scoreFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            scores = Files.readAllLines(scoreFilePath);
            // Sort the scores in descending order
            scores.sort((a, b) -> Integer.compare(Integer.parseInt(b.split("\\|")[0]), Integer.parseInt(a.split("\\|")[0])));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Returns the top N scores from the list of scores.
     *
     * @param topN the number of top scores to return
     * @return a list of strings representing the top N scores in the format "score|date"
     */
    public static List<String> getTopScores(int topN) {
        List<String> scores = loadScores();
        return scores.subList(0, Math.min(topN, scores.size()));
    }
}