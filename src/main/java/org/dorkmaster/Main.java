package org.dorkmaster;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.dorkmaster.org.dorkmaster.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    protected static final Logger logger = LoggerFactory.getLogger(Main.class);
    protected static String DISCORD_TOKEN = System.getenv("TOKEN");
    protected static String UNHELP = System.getenv("UNHELP");
    protected static MinecraftServer server = new MinecraftServer();

    protected static Cache<Snowflake, Set<String>> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public static Set<String> getRoles(Member member) {
        Snowflake id = member.getId();
        if (cache.asMap().containsKey(id)) {
            return cache.getIfPresent(id);
        }

        final Set<String> roles = new HashSet<>();
        member.getRoles().map(Role::getName)
                .collectList()
                .subscribe(roleNames -> {
                    roles.addAll(roleNames);
                });
        cache.put(id, roles);
        return roles;
    }

    public static boolean checkRole(Set<String> reqRoles, Message message) {
        Member member = message.getAuthorAsMember().block();
        if (null != member) {
            Set<String> roles = getRoles(member);
            for (String reqRole : reqRoles) {
                if (roles.contains(reqRole)) {
                    return true;
                }
            }
        }

        return false;
    }

    public  static Set<String> splitToLowerSet(String values) {
        if (null == values || values.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for (String v : values.split(",")) {
            result.add(v.toLowerCase());
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Object[] commands = new Object[]{
                "thunder", new ThunderProcessor(server),
                "lightning", new LightningProcessor(server),
                "ip", new IpProcessor(),
                "log", new LogProcessor(server),
                "say", new SayProcessor(server),
                "start", new StartProcessor(server),
                "stop", new StopProcessor(server),
                "chuck", new ChuckProcessor(),
                "ban", new BanProcessor(server),
                "unban", new UnbanProcessor(server),
                "op", new OpProcessor(server),
                "deop", new DeopProcessor(server),
                "kick", new KickProcessor(server)
        };

        Map<String, CommandProcessor> processors = new HashMap<>();
        for (int i = 0; i < commands.length; i += 2) {
            processors.put((String) commands[i], (CommandProcessor) commands[i + 1]);
        }
        processors.put("help", new HelpProcessor(processors.keySet(), splitToLowerSet(UNHELP)));

        // Create a client and login to the Discord gateway
        GatewayDiscordClient discordClient = DiscordClient.create(DISCORD_TOKEN)
                .gateway()
                .setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(Intent.MESSAGE_CONTENT)))
                .login()
                .block();

        Snowflake botId = discordClient.getSelf().block().getId();

        // Add an event handler for when the bot is ready
        discordClient.getEventDispatcher().on(GuildCreateEvent.class)
                .subscribe(event -> {
                    System.out.println("Bot is online!");
                });

        discordClient.getEventDispatcher().on(GuildCreateEvent.class)
                .subscribe(event -> {
                    Set<String> roles = new HashSet<>();
                    Guild guild = event.getGuild();
                    guild.getRoles().map(Role::getName)
                            .collectList()
                            .subscribe(roleNames -> {
                                roles.addAll(roleNames);
                            });

                    for (String name : List.of("mc-start-stop", "mc-admin", "mc-thunder-god")) {
                        if (!roles.contains(name)) {
                            guild.createRole().withName(name).block();
                        }
                    }
                });

        // Add an event handler for when a message is created
        discordClient.getEventDispatcher()
                .on(MessageCreateEvent.class).subscribe(event -> {
                    try {
                        Message message = event.getMessage();
                        if (message.getAuthor().isPresent()) {
                            if (!botId.equals(message.getAuthor().get().getId())) {
                                String content = message.getContent().toLowerCase();
                                if (content.startsWith("!mc")) {
                                    logger.info("Message for me: {}", message.getContent());
                                    String[] terms = content.split("\\ ");
                                    if (terms.length > 1) {
                                        CommandProcessor processor = processors.get(terms[1]);
                                        if (null != processor) {
                                            if (processor.requiredRoles().isEmpty() || checkRole(processor.requiredRoles(), message)) {
                                                processor.handleMessage(message);
                                            } else if (!(processor instanceof SilentCommandProcessor)) {
                                                processor.reply(message, "Access not permitted");
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Throwable t) {
                        // There's a number of small annoying errors happening.  This is a stop gap to resolve those.
                        logger.warn("Unexpected exception: '{}'", t.getMessage(), t);
                    }
                });

        // wait until we're shutdown
        discordClient.onDisconnect().block();
    }
}