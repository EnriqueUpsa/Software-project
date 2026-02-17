package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Central logger configuration for console and local file output.
 */
public final class LoggerConfig {
    private static final Path LOG_DIR = Path.of("logs");
    private static final Path LOG_FILE = LOG_DIR.resolve("shelter.log");
    private static final Object LOCK = new Object();

    private LoggerConfig() {
    }

    public static Logger getLogger(Class<?> owner) {
        Logger logger = Logger.getLogger(owner.getName());
        synchronized (LOCK) {
            if (logger.getHandlers().length == 0) {
                logger.setUseParentHandlers(false);
                logger.setLevel(Level.INFO);

                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.INFO);
                consoleHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(consoleHandler);

                try {
                    Files.createDirectories(LOG_DIR);
                    FileHandler fileHandler = new FileHandler(LOG_FILE.toString(), true);
                    fileHandler.setLevel(Level.INFO);
                    fileHandler.setFormatter(new SimpleFormatter());
                    logger.addHandler(fileHandler);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "event=file_logging_unavailable reason=" + e.getMessage());
                }
            }
        }
        return logger;
    }
}
