package net.baronofclubs.botofclubs.server.channel;

import com.google.gson.JsonObject;
import net.baronofclubs.Debug;
import net.baronofclubs.botofclubs.Properties;
import net.baronofclubs.botofclubs.PropertiesFile;
import net.baronofclubs.botofclubs.Settings;
import net.baronofclubs.botofclubs.command.CommandHandler;
import net.baronofclubs.botofclubs.server.Server;
import net.baronofclubs.botofclubs.user.UserHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by baron on 8/25/16.
 */
public class Channel {

    private String name;
    private UUID channelKey;

    private Server server;
    private boolean autoJoin;

    private char commandPrefix;
    private char modPrefix;
    private char adminPrefix;

    private ZonedDateTime firstJoined;
    private ZonedDateTime lastJoined;
    private TimeZone timeZone;

    private CommandHandler commandHandler;

    private UserHandler userHandler;
    private ChannelSettings channelSettings;
    public Channel(String channelName, Server server) {
        this.name = channelName;
        this.server = server;
        debug("Creating Channel");
        channelSettings = new ChannelSettings();
    }

    public String getChannelName() {
        return name;
    }

    public String getChannelToken() {
        return "#" + name;
    }

    public void setChannelName(String name) {
        this.name = name;
    }

    public UUID getChannelKey() {
        return channelKey;
    }

    public Server getServer() {
        return server;
    }

    public boolean isAutoJoin() {
        return autoJoin;
    }

    public void setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    public char getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(char commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public char getModPrefix() {
        return modPrefix;
    }

    public void setModPrefix(char modPrefix) {
        this.modPrefix = modPrefix;
    }

    public char getAdminPrefix() {
        return adminPrefix;
    }

    public void setAdminPrefix(char adminPrefix) {
        this.adminPrefix = adminPrefix;
    }

    public ZonedDateTime getFirstJoined() {
        return firstJoined;
    }

    public ZonedDateTime getLastJoined() {
        return lastJoined;
    }

    public void setLastJoined(ZonedDateTime lastJoined) {
        this.lastJoined = lastJoined;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public ChannelSettings getChannelSettings() {
        return channelSettings;
    }

    private class ChannelSettings extends PropertiesFile implements Properties {

        public ChannelSettings() {
            super(name + ".json", Paths.get("server" + File.separator + server.getServerName() + File.separator + "channels" + File.separator));
        }

        @Override
        public JsonObject getAsJsonObject() {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("name", name);
            jsonObject.addProperty("channel_key", channelKey.toString());
            jsonObject.addProperty("auto_join", autoJoin);
            jsonObject.addProperty("command_prefix", commandPrefix);
            jsonObject.addProperty("mod_prefix", modPrefix);
            jsonObject.addProperty("admin_prefix", adminPrefix);
            jsonObject.addProperty("first_joined", firstJoined.toString());
            jsonObject.addProperty("last_joined", lastJoined.toString());
            jsonObject.addProperty("time_zone", timeZone.getID());
            //jsonObject.add("commands", commandHandler.getAsJsonObject());
            //jsonObject.add("users", userHandler.getAsJsonObject());
            return jsonObject;
        }

        @Override
        public void fromJsonObject(JsonObject jsonObject) {
            name =          jsonObject.get("name").getAsString();
            channelKey =    UUID.fromString(jsonObject.get("channel_key").getAsString());
            autoJoin =      jsonObject.get("auto_join").getAsBoolean();
            commandPrefix = jsonObject.get("channel_key").getAsCharacter();
            modPrefix =     jsonObject.get("mod_prefix").getAsCharacter();
            adminPrefix =   jsonObject.get("admin_prefix").getAsCharacter();
            firstJoined =   ZonedDateTime.parse(jsonObject.get("first_joined").getAsString());
            lastJoined =    ZonedDateTime.parse(jsonObject.get("last_joined").getAsString());
            timeZone =      TimeZone.getTimeZone(jsonObject.get("time_zone").getAsString());
            //commandHandler.fromJsonObject(jsonObject.get("commands").getAsJsonObject());
            //userHandler.fromJsonObject(jsonObject.get("users").getAsJsonObject());
        }

        @Override
        public void setDefaults() {
            channelKey = UUID.randomUUID();
            autoJoin = true;
            commandPrefix = Settings.defaultPrefixes.commandPrefix;
            modPrefix = Settings.defaultPrefixes.moderatorPrefix;
            adminPrefix = Settings.defaultPrefixes.adminPrefix;
            firstJoined = ZonedDateTime.now();
            lastJoined = ZonedDateTime.now();
            timeZone = TimeZone.getDefault();
            //commandHandler = new CommandHandler();
            //userHandler = new UserHandler();
        }
    }

    private void debug(String line) {
        String actor = "Channel";
        Debug.Level level = Debug.Level.MEDIUM;
        Debug.print("(" + name + ") " + line, actor, level);
    }

}
