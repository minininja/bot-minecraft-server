package org.dorkmaster.org.dorkmaster.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

public class ChuckProcessor implements CommandProcessor {
    ObjectMapper mapper = new ObjectMapper();
    List<String> categories = List.of("animal",
            "career",
//            "celebrity",
            "dev",
//            "explict",
            "fashion",
            "food",
            "history",
            "money",
            "movie",
            "music",
//            "political",
//            "religion",
            "science",
            "sport",
            "travel"
    );

    @Override
    public Set<String> requiredRoles() {
        return Set.of();
    }

    @Override
    public void handleMessage(Message message) {
        try {
            int offset = -1;
            while (offset < 0 || offset >= categories.size()) {
                offset = Math.abs(new SecureRandom().nextInt()) % categories.size();
            }
            String category = categories.get(offset);

            URL url = new URL("https://api.chucknorris.io/jokes/random?category=" + category);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String payload = in.readLine();
            in.close();

            String joke = mapper.readTree(payload).get("value").asText();
            if (joke != null && !joke.isBlank()) {
                reply(message, joke);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
