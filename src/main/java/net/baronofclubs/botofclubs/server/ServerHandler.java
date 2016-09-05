package net.baronofclubs.botofclubs.server;

import net.baronofclubs.Debug;
import net.baronofclubs.botofclubs.server.channel.Channel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baron on 8/26/16.
 */
public class ServerHandler {

    private Map<String, Server> serverList = new HashMap<>();

    public ServerHandler() {
        loadAllFromDisk();
    }

    public void addServer(String serverName) {
        debug("Adding Server: " + serverName);
        if(!serverList.containsKey(serverName)) {
            serverList.put(serverName, new Server(serverName));
        } else {
            debug("Server Already Exists");
        }

    }

    public void addServer(Server server) {
        debug("Adding Server: " + server.getServerName());
        if(!serverList.containsKey(server.getServerName())) {
            serverList.put(server.getServerName(), server);
        } else {
            debug("Server Already Exists");
        }
    }

    public void overwriteServer(String serverName) {
        if(serverList.containsKey(serverName)) {
            debug("Server Doesn't Exist");
        } else {
            serverList.put(serverName, new Server(serverName));
        }
    }

    public void overwriteServer(Server server) {
        debug("Adding Server: " + server.getServerName());
        if(!serverList.containsKey(server.getServerName())) {
            debug("Server Doesn't Exist");
        } else {
            serverList.put(server.getServerName(), server);
        }
    }

    public Server getServer(String serverName){
        if(serverList.containsKey(serverName)) {
            return serverList.get(serverName);
        }
        return null;
    }

    public void startAll() {
        debug("Starting all servers");
        for(Server server : serverList.values()) {
            if(server.isAutoConnect()) {
                debug("Starting server: " + server.getServerName());
                server.start();
            }
        }
    }

    public void loadAllFromDisk() {
        File[] files = new File("server" + File.separator).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String serverName = file.getName().substring(0, file.getName().indexOf('.'));

                addServer(serverName);
            }
        }
    }


    private void debug(String line) {
        Debug.print(line, "ServerHandler", Debug.Level.LIGHT);
    }

}
