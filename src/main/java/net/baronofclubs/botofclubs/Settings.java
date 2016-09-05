package net.baronofclubs.botofclubs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.baronofclubs.Debug;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * DESCRIPTION: Class containing global settings for use throughout bot operations
 * DETAIL:
 */
public class Settings extends PropertiesFile {

    public static final transient String APPLICATION_NAME = "BotOfClubs";
    public static final transient String VERSION = "0.5b.0";
    public static final transient UUID SESSION_ID = UUID.randomUUID();

    public static Charset encoding;
    public static TimeZone timeZone;
    public static Locale locale;
    public static Debug.Level debugLevel;
    public static IrcDefaults ircDefaults;
    public static DefaultPrefixes defaultPrefixes;
    public static boolean consoleListener;

    public static Charset getEncoding() {
        return encoding;
    }

    public static void setEncoding(Charset encoding) {
        Settings.encoding = encoding;
    }

    public static TimeZone getTimeZone() {
        return timeZone;
    }

    public static void setTimeZone(TimeZone timeZone) {
        Settings.timeZone = timeZone;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        Settings.locale = locale;
    }

    public static Debug.Level getDebugLevel() {
        return debugLevel;
    }

    public static void setDebugLevel(Debug.Level debugLevel) {
        Settings.debugLevel = debugLevel;
    }

    public static boolean consoleListenerOn() {
        return consoleListener;
    }

    public static void setConsoleListener(boolean consoleListener) {
        Settings.consoleListener = consoleListener;
    }

    private final static transient String fileName = "settings.json";
    private final static transient Path filePath = Paths.get("settings/");

    public Settings() {
        super(fileName, filePath);
    }

    public void setDefaults() {
        debug("Setting defaults");
        encoding = StandardCharsets.UTF_8;
        timeZone = TimeZone.getDefault();
        locale = Locale.US;
        debugLevel = Debug.Level.ALL;
        ircDefaults = new IrcDefaults();
        defaultPrefixes = new DefaultPrefixes();
        consoleListener = true;
    }

    public JsonObject getAsJsonObject() {
        debug("Creating Json Object");
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject json = new JsonObject();

        json.addProperty("encoding", encoding.displayName());
        json.addProperty("time_zone", timeZone.getID());
        json.addProperty("language", locale.getLanguage());
        json.addProperty("country", locale.getCountry());
        json.addProperty("debug_level", debugLevel.name());
        json.addProperty("console_listener", consoleListener);
        JsonElement ircDefaultsElement = parser.parse(gson.toJson(ircDefaults));
        json.add("irc_defaults", ircDefaultsElement);
        JsonElement defaultPrefixesElement = parser.parse(gson.toJson(defaultPrefixes));
        json.add("default_prefixes", defaultPrefixesElement);
        debug(json.toString());
        return json;
    }

    public void fromJsonObject(JsonObject jsonObject) {
        debug("Getting from Json Object");
        Gson gson = new Gson();
        encoding = Charset.forName(jsonObject.get("encoding").getAsString());
        timeZone = TimeZone.getTimeZone(jsonObject.get("time_zone").getAsString());
        String language = jsonObject.get("language").getAsString();
        String country = jsonObject.get("country").getAsString();
        locale = new Locale(language, country);
        debugLevel = Debug.Level.valueOf(jsonObject.get("debug_level").getAsString());
        consoleListener = jsonObject.get("console_listener").getAsBoolean();
        JsonElement ircDefaultsElement = jsonObject.get("irc_defaults");
        ircDefaults = gson.fromJson(ircDefaultsElement, IrcDefaults.class);
        JsonElement defaultPrefixesElement = jsonObject.get("default_prefixes");
        defaultPrefixes = gson.fromJson(defaultPrefixesElement, DefaultPrefixes.class);
    }

    public class IrcDefaults {

        public String defaultNick = "BotOfClubs";
        public String defaultAltNick = "BotOfClubs_";
        public char[] nickModifiers = {'_', ']', '}', '`'};
        public boolean useNickModifiers = false;

        public double messageInterval = 1.5;

    }

    public class DefaultPrefixes {

        public char commandPrefix = '~';
        public char moderatorPrefix = '+';
        public char adminPrefix = '!';

    }

    private void debug(String status) {
        Debug.print(status, "Settings", Debug.Level.LIGHT);
    }
}
