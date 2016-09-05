package net.baronofclubs.botofclubs.server;

import net.baronofclubs.botofclubs.Settings;
import net.baronofclubs.botofclubs.server.channel.Channel;
import net.baronofclubs.botofclubs.user.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by baron on 8/26/16.
 */
public class Message {

    private Server server;
    private User user;
    private Channel channel;
    private String message;
    private ZonedDateTime timeStamp;

    public Message(MessageEvent event, Server server) {
        this.server = server;
        this.user = server.getUserHandler().getUser(event.getUser().getRealName());
        this.channel = server.getChannelHandler().getChannel(event.getChannel().getName());
        this.message = event.getMessage();
        this.timeStamp = convertTimeStamp(event.getTimestamp());
    }

    private ZonedDateTime convertTimeStamp(long l) {
        Instant i = Instant.ofEpochMilli(l);
        return ZonedDateTime.ofInstant(i, Settings.getTimeZone().toZoneId());
    }

    public Server getServer() {
        return server;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
