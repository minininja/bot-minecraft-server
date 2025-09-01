package org.dorkmaster;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MessageHandler {
    protected final String prefix = "!mc";
    protected final Snowflake myId;
    protected final Map<String, CommandProcessor> processors;
    protected final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(Snowflake myId, Map<String, CommandProcessor> processors) {
        this.myId = myId;
        this.processors = processors;
    }

    public void handle(Message message) {
        List<String> content = tokenize(message.getContent());
        Snowflake author = null;
        if (message.getAuthor().isPresent()) {
            author = message.getAuthor().get().getId();
        }
        Member member = message.getAuthorAsMember().block();

        try {
            if (!isMyMessage(author) && isCommand(content) && content.size() > 1) {
                CommandProcessor processor = processors.get(content.get(1));
                if (null != processor && hasRoles(processor.requiredRoles(), member)) {
                    processor.handleMessage(message);
                }
            }
        } catch(Throwable t){
            // over catch and log
            logger.warn("Unexpected error occurred", t);
        }

    }

    boolean isMyMessage(Snowflake author) {
        return myId.equals(author);
    }

    boolean isCommand(List<String> tokens) {
        if (!tokens.isEmpty()) {
            return prefix.equalsIgnoreCase(tokens.get(0));
        }
        return false;
    }

    List<String> tokenize(String content) {
        return Arrays.asList(content.split("\\ "));
    }

    boolean hasRoles(Set<String> reqRoles, Member member) {
        if (reqRoles.isEmpty()) {
            return true;
        }

        // TODO think about caching this
        Set<String> roles = new HashSet<>();
        member.getRoles().map(Role::getName)
                .collectList()
                .subscribe(roleNames -> {
                    roles.addAll(roleNames);
                });

        for (String reqRole : reqRoles) {
            if (roles.contains(reqRole)) {
                return true;
            }
        }

        return false;
    }
}
