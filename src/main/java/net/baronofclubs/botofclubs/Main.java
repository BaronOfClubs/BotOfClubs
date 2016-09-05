package net.baronofclubs.botofclubs;

import net.baronofclubs.Debug;
import net.baronofclubs.botofclubs.server.Server;
import net.baronofclubs.botofclubs.server.ServerHandler;
import net.baronofclubs.botofclubs.server.channel.Channel;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DESCRIPTION: Main bot class, for initializing settings and starting the several services.
 */
public class Main {

    public static void main(String[] args) {
        Debug.setLoggingLevel(Debug.Level.ALL);
        Debug.setEncoding(Charset.defaultCharset());
        Debug.setLogToFile(true);
        debug("Starting bot");

        if(isFirstRun()) {
            debug("First Run. Installing...");
            new Settings();
            // Install the bot.
        } else {
            // Initialize Settings
            debug("Initializing Settings");
            new Settings();
            // Start a server.
        }

        debug("CAUTION: Performing tests!");


        ServerHandler serverHandler = new ServerHandler();
        serverHandler.startAll();


        debug("This line should never print... right?");
    }

    private static boolean isFirstRun() {
        Path settingsPath = Paths.get("settings/settings.json");
        return !Files.exists(settingsPath);
    }

    private static void debug(String status) {
        Debug.print(status, "Main", Debug.Level.SPARSE);
    }

}
