/*
 *  This file is part of PolyChat Server.
 *  *
 *  * Copyright © 2018 DemonScythe45
 *  *
 *  * PolyChat Server is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU Lesser General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * PolyChat Server is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License
 *  * along with PolyChat Server. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package club.moddedminecraft.polychat.server;

import club.moddedminecraft.polychat.networking.io.ChatMessage;
import club.moddedminecraft.polychat.server.command.*;
import com.vdurmont.emoji.EmojiParser;
import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordHandler {

    private String discordPrefix;
    private CommandManager manager = new CommandManager();

    public void registerEventSubscribers(DiscordClient client){
        EventDispatcher eventDispatcher = client.getEventDispatcher();
        eventDispatcher.on(GuildCreateEvent.class).subscribe(this::onGuildCreate);
        eventDispatcher.on(MessageCreateEvent.class).subscribe(this::onMessageEvent);
    }

    public void onGuildCreate(GuildCreateEvent event){
        Guild guild = event.getGuild();
        if(guild.getName().equals(Main.config.getProperty("guild_name"))){
            Flux<GuildChannel> guildChannelFlux = guild.getChannels();
            for(GuildChannel channel : guildChannelFlux.toIterable()){
                if(channel.getName().equals(Main.config.getProperty("channel_name"))){
                    System.out.println("Established main message channel!");
                    Main.channel = channel;
                    Main.startServer();
                    discordPrefix = Main.config.getProperty("discord_prefix", "!");
                    manager.setPrefix(discordPrefix);
                    try{
                        registerCommands();
                    }catch(Exception e){
                        System.err.println("Error " + e.toString() + " encountered while registering commands, ignoring...");
                    }
                    return;
                }
            }
        }
        Main.startServer();
        System.out.println("Failed to establish message channel! Will not send messages...");
    }

    public void onMessageEvent(MessageCreateEvent event) {
        Guild guild = event.getGuild().block();

        if (guild.getName().equals(Main.config.getProperty("guild_name")) && event.getMessage().getContent().isPresent()) {
            String content = event.getMessage().getContent().get();
            if (content.startsWith(discordPrefix)) {
                // don't bother processing message further if command
                if (processCommand(event.getMessage())) return;
            }
            TextChannel textChannel = event.getMessage().getChannel().ofType(TextChannel.class).block();
            if (textChannel.getName().equals(Main.config.getProperty("channel_name")) && !event.getMessage().getAuthorAsMember().block().isBot()) {
                processMessage(event.getMessage());
            }

        }
    }

    public void registerCommands() throws FileNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        final HashMap<String, Class<? extends Command>> COMMAND_TYPES = new HashMap<String, Class<? extends Command>>() {
            {
                put("online", CommandOnline.class);
                put("help", CommandHelp.class);
                put("minecraft", MinecraftCommand.class);
            }
        };

        Yaml yaml = new Yaml();
        LinkedHashMap yamlObj = yaml.load(new FileInputStream(Main.yamlConfig));

        ArrayList<String> commandChannels = (ArrayList<String>) yamlObj.remove("channels");
        manager.setChannels(commandChannels);

        ArrayList<String> prefixRoles = (ArrayList<String>) yamlObj.remove("all_roles");
        manager.addPrefixRoles(prefixRoles);

        for (Object entryObj : (ArrayList) yamlObj.get("commands")) {
            LinkedHashMap entry = (LinkedHashMap) entryObj;
            HashMap<String, String> argMap = new HashMap<>();
            for (Object mapObj : (ArrayList) entry.values().iterator().next()) {
                argMap.putAll((HashMap<String, String>) mapObj);
            }

            String name = (String) entry.keySet().iterator().next(); // kinda ugly but way too deep down the rabbit hole
            String type = argMap.remove("type");

            Class<? extends Command> element = COMMAND_TYPES.get(type);
            Constructor constructor = element.getConstructor(String.class, Map.class);
            Command commandObj = (Command) constructor.newInstance(name, argMap);

            manager.register(commandObj);
        }
    }

    public CommandManager getManager() {
        return manager;
    }

    public boolean processCommand(Message message) {
        TextChannel textChannel = message.getChannel().ofType(TextChannel.class).block();
        if (textChannel != null && manager.getChannels().contains(textChannel.getName())) {
            String newMessage = manager.run(message);
            if (!newMessage.isEmpty()) {
                System.out.println(newMessage);
                message.getChannel().block().createMessage(newMessage).block();
            }
            return true;
        }
        return false;
    }

    public void processMessage(Message message) {
        String author = message.getAuthorAsMember().block().getDisplayName() + ":";
        ChatMessage discordMessage = new ChatMessage(author, formatMessage(message), "empty");
        System.out.println(String.format("[Discord] %s %s", author, discordMessage.getMessage()));
        Main.chatServer.sendMessage(discordMessage);
    }

    private String formatMessage(Message message) {
        String messageContent = message.getContent().get();
        messageContent = EmojiParser.parseToAliases(messageContent);

        Pattern emojiName = Pattern.compile("<(:\\w+:)\\d+>");
        Matcher nameMatch = emojiName.matcher(messageContent);
        while (nameMatch.find()) {
            for (int i = 0; i <= nameMatch.groupCount(); i++) {
                messageContent = messageContent.replaceFirst("(<:\\w+:\\d+>)", nameMatch.group(i));
            }
        }

        return messageContent;
    }

}
