package org.dorkmaster;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface CommandProcessor {
    public static final String THUMB = "👍";

    Set<String> requiredRoles();
    void handleMessage(Message message);

    default void reply(Message message, String response, String fileContent){
        message.getChannel().block().createMessage(
                MessageCreateSpec.builder()
                        .content(response)
                        .addFile("log.txt", new ByteArrayInputStream(fileContent.getBytes()))
                        .build()
        ).subscribe();
    }

    default void reply(Message message, String response) {
        message.getChannel().block().createMessage(response).subscribe();
    }

    default void react(Message message, String reaction){
        message.addReaction(ReactionEmoji.unicode(reaction)).block();
    }

    default List<String> tokenize(String content){
        return Arrays.asList(content.split("\\ "));
    }
}
