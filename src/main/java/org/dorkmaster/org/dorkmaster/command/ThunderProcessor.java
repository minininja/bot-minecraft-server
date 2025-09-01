package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.MinecraftServer;
import org.dorkmaster.SilentCommandProcessor;

import java.util.Set;

public class ThunderProcessor implements SilentCommandProcessor {
    protected MinecraftServer server;

    public ThunderProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin", "mc-thunder-god");
    }

    @Override
    public void handleMessage(Message message) {
        server.say("/weather thunder");
        message.delete().block();
    }
}
