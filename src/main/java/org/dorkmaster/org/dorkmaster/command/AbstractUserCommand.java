package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractUserCommand implements CommandProcessor {
    protected MinecraftServer server;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractUserCommand(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void handleMessage(Message message) {
        List<String> tokens = tokenize(message.getContent());
        if (tokens.size() >= 2) {
            tokens = tokens.subList(2, tokens.size());
            if (!tokens.isEmpty()) {
                execute(message, tokens);
            }
        }
    }

    public abstract void execute(Message message, List<String> names);
}
