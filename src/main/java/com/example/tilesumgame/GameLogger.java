package com.example.tilesumgame;

import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for logging game events, e.g. board state.
 */
public class GameLogger {
    private static GameLogger instance;
    private final Logger logger;

    /**
     * Private constructor to set up the logger.
     * Configures the logger to use a ConsoleHandler with FINE level.
     *
     * @param level the logging level granularity to use
     */
    private GameLogger(Level level) {
        logger = Logger.getLogger(GameLogger.class.getName());
        logger.setLevel(level);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);
        logger.addHandler(consoleHandler);
    }

    /**
     * Initializes the singleton instance of GameLogger with the specified level.
     *
     * @param level the logging level granularity to use
     */
    public static void initialize(Level level) {
        if (instance == null) {
            instance = new GameLogger(level);
        }
    }

    /**
     * Returns the singleton instance of GameLogger.
     *
     * @return the singleton instance of GameLogger
     */
    public static GameLogger getInstance() {
        return instance;
    }

    /**
     * Logs a message at the specified level.
     *
     * @param level   the level at which the message should be logged
     * @param message the message to be logged
     */
    public void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Logs a message at the specified level using a Supplier.
     *
     * @param level           the level at which the message should be logged
     * @param messageSupplier a Supplier that provides the message to be logged
     */
    public void log(Level level, Supplier<String> messageSupplier) {
        logger.log(level, messageSupplier);
    }
}