package net.baronofclubs.botofclubs.server;

import net.baronofclubs.Debug;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * DESCRIPTION: Class for handling messages as they are received from the server.
 */
public class MessageHandler extends ListenerAdapter {

    private Server server;

    public MessageHandler(Server server) {
        this.server = server;
    }

    public void onMessage(MessageEvent event) {
        Message message = new Message(event, server);
        message.getUser().seen();
        // Tests
        debug(message.getUser().getRealName());
    }

    private void debug(String line) {
        String actor = "MessageHandler";
        Debug.Level level = Debug.Level.MEDIUM;
        Debug.print(line, actor, level);
    }

}
