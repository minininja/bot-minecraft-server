package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class StartProcessor implements CommandProcessor {
    protected MinecraftServer server;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public StartProcessor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin", "mc-start-stop");
    }

    @Override
    public void handleMessage(Message message) {
        try {
            boolean result = server.startServer();
            if (result){
                react(message, CommandProcessor.THUMB);
            } else {
                reply(message, "Server is already running");
            }
        } catch (IOException e) {
            logger.info("Unable to start server", e);
            reply(message, "Hmm, can't start the server right now.");
        }
    }
}
