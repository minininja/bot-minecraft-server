package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class HelpProcessor implements CommandProcessor {
    protected Collection<String> commands;
    protected Set<String> unhelpfulPlayers;

    protected static List<String> helpMessages = List.of(
            "Yes, clearly you need help.",
            "No, no help for you.",
            "I don't know how to help you.",
            "I can't help you now but perhaps if you ask again later.",
            "I can't help but wonder why you're asking me?",
            "We all feel lost and confused at times and it's good to have a friend to help you out.  That's not me though.",
            "It's too early to help, I need coffee.",
            "I'd say I wish there was something I could do, but that'd be lying."
    );

    public HelpProcessor(Collection<String> commands, Set<String> unhelpfulPlayers) {
        this.commands = commands;
        this.unhelpfulPlayers = unhelpfulPlayers;
    }

    @Override
    public Set<String> requiredRoles() {
        return Set.of();
    }

    @Override
    public void handleMessage(Message message) {
        boolean answered = false;
        if (message.getAuthor().isPresent()){
            if (!unhelpfulPlayers.contains(message.getAuthor().get().getUsername().toLowerCase())) {
                reply(message, "Supported commands: " + String.join(", ", commands));
                answered = true;
            }
        }

        if (!answered) {
            int offset = Math.abs(new SecureRandom().nextInt(helpMessages.size())) % helpMessages.size();
            reply(message, helpMessages.get(offset));
        }
    }
}
