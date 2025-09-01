package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;

import java.util.Set;

public class LogProcessor implements CommandProcessor {
    MinecraftServer server;

    public LogProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin");
    }

    @Override
    public void handleMessage(Message message) {
        reply(message, "Current log", String.join("\n", server.tail(100)));
    }
}
