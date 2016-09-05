package net.baronofclubs.botofclubs.user;

import com.google.gson.JsonObject;
import net.baronofclubs.botofclubs.server.Server;
import net.baronofclubs.botofclubs.server.channel.Channel;

import java.util.*;

/**
 * Created by baron on 8/26/16.
 */
public class UserHandler {

    Server server;
    Map<String, User> userList;

    public UserHandler(Server server) {
        this.server = server;
        userList = new HashMap<>();
    }

    public UserHandler(Channel channel) {

    }

    public void addUser(User user) {

    }

    public User getUser(String realName) {
        if(contains(realName)) {
            return userList.get(realName);
        } else {
            User user = new User(realName, server);
            addUser(user);
            return user;
        }
    }

    public boolean contains(String realName) {
        return userList.containsKey(realName);
    }

    public JsonObject getAsJsonObject() {
        return null;
    }

    public void fromJsonObject(JsonObject jsonObject) {

    }

}
