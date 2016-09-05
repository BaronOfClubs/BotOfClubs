package net.baronofclubs;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

/**
 * DESCRIPTION: Class for controlling system output for debugging purposes
 * DETAIL:
 *      Use:
 *      Set the logging level by calling Debug.setLoggingLevel(Debug.Level.ALL)
 *      Call Debug.print()
 *      example: Debug.print("Starting Process A", "Main", Debug.Level.HIGH)
 *
 */

public class Debug {

    public static final String DEBUG_PREFIX = "[>]";
    public static final Level FALLBACK_LL = Level.ALL;

    private static Level loggingLevel;
    private static boolean logToFile;
    private static Charset encoding;

    public static void setLoggingLevel(Level level) {
        loggingLevel = level;
    }
    public static void setLogToFile(boolean log) {
        logToFile = log;
    }

    public static void setEncoding(Charset charset) {
        encoding = charset;
    }

    public static void print(String line, String actor, Level level) {

        if(loggingLevel == null && level.isLessThan(FALLBACK_LL)) {
            printDebug(line, actor);
            if(logToFile) {
                String timeStamp = ZonedDateTime.now().toLocalTime().toString();
                writeToFile(timeStamp + "|" + actor + ": " + line);
            }
        } else if(level.isLessThan(loggingLevel)) {
            printDebug(line, actor);
            if(logToFile) {
                String timeStamp = ZonedDateTime.now().toLocalTime().toString();
                writeToFile(timeStamp + "|" + actor + ": " + line);
            }
        } else {
            // Do nothing.
        }
    }

    private static void printDebug(String line, String actor) {
        System.out.println(DEBUG_PREFIX + actor + ": " + line);
    }

    private static void writeToFile(String string) {
        Path logsPath = Paths.get("logs/");
        String fileName = ZonedDateTime.now().toLocalDate().toString() + ".log";
        Path path = Paths.get(logsPath + File.separator + fileName);

        if(Files.exists(path)) {
            Writer writer;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), true), encoding));
                writer.append(string);
                writer.append(System.lineSeparator());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File file = new File(path.toString());
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeToFile(string);
        }
    }

    public enum Level {
        NONE (0),
        SPARSE (1),
        LIGHT (2),
        MEDIUM (3),
        HEAVY (4),
        ALL (5);

        private final int levelCode;

        Level(int levelCode) {
            this.levelCode = levelCode;
        }

        public int getLevelCode() {
            return this.levelCode;
        }

        public boolean isLessThan(Level level) {
            return this.levelCode < level.getLevelCode();
        }

    }

}
