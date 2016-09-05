package net.baronofclubs.botofclubs.server;

import com.google.gson.*;
import net.baronofclubs.Debug;
import net.baronofclubs.botofclubs.Properties;
import net.baronofclubs.botofclubs.PropertiesFile;
import net.baronofclubs.botofclubs.Settings;
import net.baronofclubs.botofclubs.server.channel.Channel;
import net.baronofclubs.botofclubs.server.channel.ChannelHandler;
import net.baronofclubs.botofclubs.user.UserHandler;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * DESCRIPTION: Class to hold and manage Server information, a PircBotX instance, and manage channels and Users accordingly.
 * DETAILS:
 *      Use:
 *      Create an instance of the class with a nickname of the server as an argument.
 *          This nickname will only be used internally to find the server in a multi-server environment.
 *      The server instance will create an instance of ServerSettings and IrcSettings which extend the Properties class
 *      to put all the settings in a file. These can be changed manually, or by calling the methods in this class to
 *      change settings. Settings will not automatically be saved to file after creation.
 *
 */

public class Server {

    private String name;
    private boolean autoConnect;
    private boolean enableConsoleCommands;

    private IrcSettings ircSettings;
    public ServerSettings serverSettings;

    private MessageHandler messageHandler;
    private ChannelHandler channelHandler;
    private UserHandler userHandler;

    private PircBotX pircBotX;

    private GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
    private Gson gson = gsonBuilder.create();

    private UserInput userInput;

    public Server(String name) {
        debug("Initializing Server");
        this.name = name.toLowerCase();
        ircSettings = new IrcSettings();
        serverSettings = new ServerSettings();

        messageHandler = new MessageHandler(this);

        channelHandler = new ChannelHandler(this);
        populateAutoJoiners();

        userHandler = new UserHandler(this);
        populateUsers();

        pircBotX = new PircBotX(serverConfig());
        userInput = new UserInput();

        if(autoConnect) {
            debug("Autoconnecting");
            start();
        }
    }

    public String getServerName() {
        return name;
    }

    public void enableConsoleCommands() {
        enableConsoleCommands = true;
    }

    public void disableConsoleCommands() {
        enableConsoleCommands = false;
    }

    public boolean consoleCommandsEnabled() {
        return enableConsoleCommands;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public String getNick() {
        return ircSettings.nick;
    }

    public void setNick(String nick) {
        ircSettings.nick = nick;
    }

    public String getLogin() {
        return ircSettings.login;
    }

    public void setLogin(String login) {
        ircSettings.login = login;
    }

    public String getPassword() {
        return ircSettings.password;
    }

    public void setPassword(String password) {
        ircSettings.password = password;
    }

    public boolean isAutoNickChange() {
        return ircSettings.autoNickChange;
    }

    public void setAutoNickChange(boolean autoNickChange) {
        ircSettings.autoNickChange = autoNickChange;
    }

    public Charset getEncoding() {
        return ircSettings.encoding;
    }

    public void setEncoding(Charset encoding) {
        ircSettings.encoding = encoding;
    }

    public int getMessageDelay() {
        return ircSettings.messageDelay;
    }

    public void setMessageDelay(int messageDelay) {
        ircSettings.messageDelay = messageDelay;
    }

    public String getRealName() {
        return ircSettings.realName;
    }

    public void setRealName(String realName) {
        ircSettings.realName = realName;
    }

    public URI getServerAddress() {
        return ircSettings.serverAddress;
    }

    public void setServerAddress(URI serverAddress) {
        ircSettings.serverAddress = serverAddress;
    }

    public int getPort() {
        return ircSettings.port;
    }

    public void setPort(int port) {
        ircSettings.port = port;
    }

    public boolean isOnJoinWhoEnabled() {
        return ircSettings.onJoinWhoEnabled;
    }

    public void setOnJoinWhoEnabled(boolean onJoinWhoEnabled) {
        ircSettings.onJoinWhoEnabled = onJoinWhoEnabled;
    }

    public boolean isCapEnabled() {
        return ircSettings.capEnabled;
    }

    public void setCapEnabled(boolean capEnabled) {
        ircSettings.capEnabled = capEnabled;
    }

    public boolean isAutoReconnect() {
        return ircSettings.autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        ircSettings.autoReconnect = autoReconnect;
    }

    public int getAutoReconnectDelay() {
        return ircSettings.autoReconnectDelay;
    }

    public void setAutoReconnectDelay(int autoReconnectDelay) {
        ircSettings.autoReconnectDelay = autoReconnectDelay;
    }

    public int getAutoReconnectAttempts() {
        return ircSettings.autoReconnectAttempts;
    }

    public void setAutoReconnectAttempts(int autoReconnectAttempts) {
        ircSettings.autoReconnectAttempts = autoReconnectAttempts;
    }

    public String[] getAutoJoinChannels() {
        return ircSettings.autoJoinChannels;
    }

    public void addAutoJoinChannel(String channelName) {
        debug("Adding autoJoinChannel: " + channelName);

        String channelToken;
        if(channelName.charAt(0) != '#') {
            channelToken = "#" + channelName;
        } else {
            channelToken = channelName;
        }

        String[] channels;
        if (ircSettings.autoJoinChannels != null) {
            channels = new String[ircSettings.autoJoinChannels.length + 1];
            for(int i = 0; i == ircSettings.autoJoinChannels.length; i++) {
                channels[i] = ircSettings.autoJoinChannels[i];
            }
            channels[channels.length - 1] = channelToken;
        } else {
            channels = new String[1];
            channels[0] = channelToken;
        }
        ircSettings.autoJoinChannels = channels;
    }

    public void populateAutoJoiners() {
        for(Channel channel : channelHandler.getAutoJoiners()) {
            addAutoJoinChannel(channel.getChannelToken());
        }
    }

    public String[] getCapHandlers() {
        return ircSettings.capHandlers;
    }

    public void addCapHandler(String capHandler) {
        String[] capHandlers = new String[ircSettings.capHandlers.length + 1];
        for(int i = 0; i == ircSettings.capHandlers.length; i++) {
            capHandlers[i] = ircSettings.capHandlers[i];
        }
        capHandlers[capHandlers.length - 1] = capHandler;
        ircSettings.capHandlers = capHandlers;
    }

    public void reconfigure() {
        stop();
        pircBotX = new PircBotX(serverConfig());
    }

    public void start() {
        if(enableConsoleCommands) {
            debug("Starting console listener");
            Thread listenerThread = new Thread(userInput);
            listenerThread.start();
        }

        debug("Connecting");
        try {
            pircBotX.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        debug("Stopping");
        pircBotX.close();
    }

    public void restart() {
        debug("Restarting");
        pircBotX.stopBotReconnect();
    }

    public void saveSettings() {
        serverSettings.save();
    }

    public boolean isConnected() {
        return pircBotX.isConnected();
    }

    public void joinChannel(String channelName) {
        channelHandler.addChannel(channelName);
        pircBotX.send().joinChannel(channelName);
    }

    public void partChannel(String channelName) {
        pircBotX.sendRaw().rawLine("PART " + channelHandler.getChannel(channelName).getChannelToken());
        channelHandler.getChannel(channelName).setAutoJoin(false);
    }

    private class UserInput implements Runnable {
        Scanner scanner = new Scanner(System.in);
        long listenerDelay = 5000;

        public void run() {
            try {
                sleep(listenerDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (isConnected()) {

                String line = scanner.nextLine();
                if(line.charAt(0) == '>') {
                    pircBotX.send().message(line.substring(1, line.indexOf(" ")), line.substring(line.indexOf(" ") + 1));
                } else {
                    pircBotX.sendRaw().rawLineNow(line);
                }
            }
        }

    }

    public void populateUsers() {
        // TODO: implement loading of users from disk
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    private class ServerSettings extends PropertiesFile implements Properties {

        public ServerSettings() {
            super(name + ".json", Paths.get("server" + File.separator));
        }

        @Override
        public JsonObject getAsJsonObject() {
            debug("Getting json");
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("name", name);
            jsonObject.addProperty("auto_connect", autoConnect);
            jsonObject.addProperty("enable_console_commands", enableConsoleCommands);
            JsonElement ircSettingsElement  = jsonParser.parse(gson.toJson(ircSettings));
            jsonObject.add("irc_settings", ircSettingsElement);

            return jsonObject;
        }

        @Override
        public void fromJsonObject(JsonObject jsonObject) {
            debug("Getting from Json Object");
            name = jsonObject.get("name").getAsString();
            autoConnect = jsonObject.get("auto_connect").getAsBoolean();
            enableConsoleCommands = jsonObject.get("enable_console_commands").getAsBoolean();
            JsonElement ircSettingsElement = jsonObject.get("irc_settings");
            ircSettings = gson.fromJson(ircSettingsElement, IrcSettings.class);
        }

        @Override
        public void setDefaults() {
            debug("Setting defaults");

            enableConsoleCommands = false;

            ircSettings.nick = "NICK";
            ircSettings.login = "LOGIN";
            ircSettings.password = "PASSWORD";
            ircSettings.autoNickChange = false;

            ircSettings.encoding = Settings.encoding;
            ircSettings.messageDelay = 0;
            ircSettings.realName = Settings.APPLICATION_NAME + " v" + Settings.VERSION;
            ircSettings.serverAddress = URI.create("localhost");
            ircSettings.port = 6667;
            ircSettings.onJoinWhoEnabled = false;
            ircSettings.capEnabled = false;
            ircSettings.capHandlers = null;
            ircSettings.autoReconnect = true;
            ircSettings.autoReconnectDelay = 5;
            ircSettings.autoReconnectAttempts = 12;
            ircSettings.autoJoinChannels = null;
        }
    }

    private class IrcSettings {

        // TODO: Charset is set as transient to avoid gson mishandling of the Charset class
        // TODO: Implement a custom Serializer and Deserializer for Charsets

        String nick;
        String login;
        String password;
        boolean autoNickChange;

        transient Charset encoding = Settings.encoding;
        int messageDelay;
        String realName;

        URI serverAddress;
        int port;

        boolean onJoinWhoEnabled;
        boolean capEnabled;
        String[] capHandlers;

        boolean autoReconnect;
        int autoReconnectDelay;
        int autoReconnectAttempts;

        String[] autoJoinChannels;

        public void toDefaults() {
            ircSettings.nick = "NICK";
            ircSettings.login = "LOGIN";
            ircSettings.password = "PASSWORD";
            ircSettings.autoNickChange = false;

            ircSettings.encoding = Settings.encoding;
            ircSettings.messageDelay = 0;
            ircSettings.realName = Settings.APPLICATION_NAME + " v" + Settings.VERSION;
            ircSettings.serverAddress = URI.create("localhost");
            ircSettings.port = 6667;
            ircSettings.onJoinWhoEnabled = false;
            ircSettings.capEnabled = false;
            ircSettings.capHandlers = null;
            ircSettings.autoReconnect = true;
            ircSettings.autoReconnectDelay = 5;
            ircSettings.autoReconnectAttempts = 12;
            ircSettings.autoJoinChannels = null;
        }

    }

    private Configuration serverConfig() {
        debug("Configuring PircBotX");
        Configuration.Builder configBuilder = new Configuration.Builder();

        if (ircSettings == null) {
            ircSettings.toDefaults();
        }

        if (ircSettings != null) {
            configBuilder.setName(ircSettings.nick);
            configBuilder.setLogin(ircSettings.login);
            configBuilder.setServerPassword(ircSettings.password);
            configBuilder.setAutoNickChange(ircSettings.autoNickChange);
            configBuilder.setEncoding(Settings.encoding); // TODO: This is coming up as null, and I don't know why.
            configBuilder.setMessageDelay(ircSettings.messageDelay);
            configBuilder.setRealName(ircSettings.realName);
            configBuilder.addServer(ircSettings.serverAddress.toString(), ircSettings.port);
            configBuilder.setOnJoinWhoEnabled(ircSettings.onJoinWhoEnabled);
            configBuilder.setCapEnabled(ircSettings.capEnabled);
            configBuilder.setAutoReconnect(ircSettings.autoReconnect);
            configBuilder.setAutoReconnectDelay(ircSettings.autoReconnectDelay);
            configBuilder.setAutoReconnectAttempts(ircSettings.autoReconnectAttempts);
        }

        if(ircSettings.capHandlers != null) {
            for(String capHandler : ircSettings.capHandlers) {
                configBuilder.addCapHandler(new EnableCapHandler(capHandler));
            }
        }

        if(ircSettings.autoJoinChannels != null) {
            for(String autoJoinChannel : ircSettings.autoJoinChannels) {
                configBuilder.addAutoJoinChannel(autoJoinChannel);
            }
        }

        configBuilder.addListener(messageHandler);

        return configBuilder.buildConfiguration();
    }

    private void debug(String line) {
        Debug.print(line, "Server", Debug.Level.LIGHT);
    }

}
