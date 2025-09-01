package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;
import org.dorkmaster.MinecraftServer;

import java.util.List;
import java.util.Set;

public class OpProcessor extends AbstractUserCommand {
    public OpProcessor(MinecraftServer server) {
        super(server);
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of("mc-admin");
    }

    @Override
    public void execute(Message message, List<String> names) {
        for (String name: names) {
            String cmd = "/op "+ name;
            logger.info("sending '{}'", cmd);
            server.say(cmd);
        }
        react(message, CommandProcessor.THUMB);
    }
}
