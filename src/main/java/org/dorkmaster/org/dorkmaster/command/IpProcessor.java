package org.dorkmaster.org.dorkmaster.command;

import discord4j.core.object.entity.Message;
import org.dorkmaster.CommandProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

public class IpProcessor implements CommandProcessor {
    @Override
    public Set<String> requiredRoles() {
        return Set.of();
    }

    @Override
    public void handleMessage(Message message) {
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String ipAddress = in.readLine();
            in.close();
            reply(message, "Minecraft server IP: " + ipAddress);
        } catch (IOException e) {
            reply(message, "Sorry, I can't do that right now");
        }
    }
}
