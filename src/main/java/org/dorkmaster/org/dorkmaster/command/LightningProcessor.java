package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;
import org.dorkmaster.SilentCommandProcessor;

import java.util.Set;

public class LightningProcessor implements SilentCommandProcessor {
    MinecraftServer server;

    public LightningProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin", "mc-thunder-god");
    }

    @Override
    public void handleMessage(Message message) {
        server.say("/execute at @a run summon lightning_bolt");
        message.delete().block();
    }
}
