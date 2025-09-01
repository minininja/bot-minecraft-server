package org.dorkmaster;

import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    public record Tokens(String prefix, List<String> terms) {
    }

    public static Tokens tokenize(String prefix, String message) {
        String input = message.toLowerCase();
        if (input.startsWith(prefix)) {
            List<String> tokens = Arrays.asList(input.split("\\ "));
            return new Tokens(tokens.get(0), tokens.subList(1, tokens.size() - 1));
        } else{
            return null;
        }
    }

}
