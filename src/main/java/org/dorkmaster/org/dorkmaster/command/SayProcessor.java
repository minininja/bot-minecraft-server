package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SayProcessor implements CommandProcessor {
    protected MinecraftServer server;

    public SayProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin");
    }

    @Override
    public void handleMessage(Message message) {
        List<String> say = tokenize(message.getContent());
        String cmd = String.join(" ", say.subList(2, say.size()));
        reply(message, "Sending '" + cmd + "' to the server");
        server.say(cmd);
    }
}
