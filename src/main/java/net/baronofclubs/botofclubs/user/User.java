package net.baronofclubs.botofclubs.user;

import net.baronofclubs.botofclubs.Permissions;
import net.baronofclubs.botofclubs.server.Server;
import net.baronofclubs.botofclubs.server.channel.Channel;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by baron on 8/25/16.
 */
public class User {

    private String realName;
    private UUID userId;

    private Server server;
    Permissions.UserPermissions permissions;

    private ZonedDateTime lastSeen;

    public User(String realName, Server server) {
        this.realName = realName;
        this.server = server;
    }

    private class ChannelUser {

        Permissions.UserPermissions permissions;
        int messages;
        List<String> nicknames;


        private void chatted() {
            messages++;
        }
    }

    public String getRealName() {
        return realName;
    }

    public void seen() {
        lastSeen = ZonedDateTime.now();
    }

    private UUID generateUserId() {
        return UUID.randomUUID();
    }

}
