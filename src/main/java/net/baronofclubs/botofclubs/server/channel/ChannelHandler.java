package net.baronofclubs.botofclubs.server.channel;

import net.baronofclubs.Debug;
import net.baronofclubs.botofclubs.server.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baron on 8/26/16.
 */
public class ChannelHandler {

    private Server server;
    private Map<String, Channel> channels;

    public ChannelHandler(Server server) {
        this.server = server;
        channels = new HashMap<>();

        loadAllFromDisk();
    }

    public void addChannel(String channelName) {
        if(!channels.containsKey(channelName)) {
            channels.put(channelName, new Channel(channelName, server));
        } else {
            debug("Channel Already exists!");
        }
    }

    public void addChannel(Channel channel) {
        if(!channels.containsValue(channel)) {
            channels.put(channel.getChannelName(), channel);
        } else {
            debug("Channel Already exists!");
        }
    }

    // TODO: implement removeChannel(String channelName)
    // TODO: implement removeChannel(Channel channel)

    public Channel getChannel(String channelName) {
        if(channels.containsKey(channelName)) {
            return channels.get(channelName);
        } else {
            debug("Channel doesn't exist!");
            return null;
        }
    }

    public void loadAllFromDisk() {
        File[] files = new File("server" + File.separator + server.getServerName() + File.separator + "channels" + File.separator).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String channelName = file.getName().substring(0, file.getName().indexOf('.'));
                Channel newChannel = new Channel(channelName, server);
                addChannel(newChannel);
            }
        }
    }

    public List<Channel> getAutoJoiners() {
        List<Channel> autoJoiners = new ArrayList<>();
        for(Channel channel : channels.values()) {
            if(channel.isAutoJoin()) {
                autoJoiners.add(channel);
            }
        }
        return autoJoiners;
    }

    private void debug(String line) {
        String actor = "ChannelHandler";
        Debug.Level level = Debug.Level.HEAVY;
        Debug.print(line, actor, level);
    }
}
