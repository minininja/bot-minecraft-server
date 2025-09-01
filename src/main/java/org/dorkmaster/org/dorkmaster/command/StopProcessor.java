package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;

import java.util.Set;

public class StopProcessor implements CommandProcessor {
    MinecraftServer server;

    public StopProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin", "mc-start-stop");
    }

    @Override
    public void handleMessage(Message message) {
        try {
            server.stopServer();
            react(message, CommandProcessor.THUMB);
        } catch (InterruptedException e) {
            reply(message, "Oops, can't stop the server right now.  Maybe it's not running?");
        }
    }
}
