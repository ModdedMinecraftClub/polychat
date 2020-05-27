/* This file is part of PolyChat.
 *
 * Copyright © 2018 john01dav
 *
 * PolyChat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PolyChat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Polychat. If not, see <https://www.gnu.org/licenses/>.
 */
package club.moddedminecraft.polychat.server;

import club.moddedminecraft.polychat.networking.io.*;
import club.moddedminecraft.polychat.networking.util.ThreadedQueue;
import discord4j.core.object.entity.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintMessageQueue extends ThreadedQueue<MessageData> {
    private static final HashMap<String, Color> colorHashMap = new HashMap<String, Color>() {{
        put("0", new Color(0x000000));
        put("1", new Color(0x0000AA));
        put("2", new Color(0x00AA00));
        put("3", new Color(0x00AAAA));
        put("4", new Color(0xAA0000));
        put("5", new Color(0xAA00AA));
        put("6", new Color(0xFFAA00));
        put("7", new Color(0xAAAAAA));
        put("8", new Color(0x555555));
        put("9", new Color(0x5555FF));
        put("10", new Color(0x55FF55));
        put("11", new Color(0x55FFFF));
        put("12", new Color(0xFF5555));
        put("13", new Color(0xFF55FF));
        put("14", new Color(0xFFFF55));
        put("15", new Color(0xFFFFFF));
    }};

    @Override
    protected void init() {
        System.out.println("ready to print messages");
    }

    @Override
    protected void handle(MessageData messageData) {
        AbstractMessage rawMessage = messageData.getMessage();
        MessageChannel messageChannel = (MessageChannel)Main.channel;
        if(Main.channel != null){
            if(rawMessage instanceof ChatMessage){
                ChatMessage message = ((ChatMessage) rawMessage);
                System.out.println(message.getUsername() + " " + message.getMessage());
                messageChannel.createMessage("**`" + message.getUsername() + "`** " + formatMessage(message.getMessage())).block();
            }else if(rawMessage instanceof ServerInfoMessage){
                ServerInfoMessage infoMessage = ((ServerInfoMessage) rawMessage);
                Main.serverInfo.serverConnected(infoMessage.getServerID(),
                        infoMessage.getServerName(),
                        infoMessage.getServerAddress(),
                        infoMessage.getMaxPlayers(),
                        messageData.getMessageBus());
            }else if(rawMessage instanceof ServerStatusMessage){
                ServerStatusMessage serverStatus = ((ServerStatusMessage) rawMessage);
                switch(serverStatus.getState()){
                    case 1:
                        messageChannel.createMessage("**`" + serverStatus.getServerID() + " Server Online`**").block();
                        Main.serverInfo.serverOnline(serverStatus.getServerID());
                        break;
                    case 2:
                        messageChannel.createMessage("**`" + serverStatus.getServerID() + " Server Offline`**").block();
                        Main.serverInfo.serverOffline(serverStatus.getServerID());
                        break;
                    case 3:
                        messageChannel.createMessage("**`" + serverStatus.getServerID() + " Server Crashed`**").block();
                        Main.serverInfo.serverOffline(serverStatus.getServerID());
                        break;
                    default:
                        System.err.println("Unrecognized server state " + serverStatus.getState() + " received from " + serverStatus.getServerID());
                }
            }else if(rawMessage instanceof PlayerStatusMessage){
                String statusString;
                PlayerStatusMessage playerStatus = ((PlayerStatusMessage) rawMessage);
                if(playerStatus.getJoined()){
                    statusString = "**`" + playerStatus.getServerID() + " " + playerStatus.getUserName() + " has joined the game`**";
                    Main.serverInfo.playerJoin(playerStatus.getServerID(), playerStatus.getUserName());
                }else{
                    statusString = "**`" + playerStatus.getServerID() + " " + playerStatus.getUserName() + " has left the game`**";
                    Main.serverInfo.playerLeave(playerStatus.getServerID(), playerStatus.getUserName());
                }
                if(!playerStatus.getSilent()){
                    messageChannel.createMessage(statusString).block();
                }
            }else if(rawMessage instanceof PlayerListMessage){
                PlayerListMessage plMessage = (PlayerListMessage) rawMessage;
                Main.serverInfo.updatePlayerList(plMessage.getServerID(), plMessage.getPlayerList());
            }else if(rawMessage instanceof CommandOutputMessage){
                CommandOutputMessage message = (CommandOutputMessage) rawMessage;
                ((TextChannel) Main.getChannelByName(message.getChannel())).createEmbed(embedSpec -> {
                    if(!message.getCommand().isEmpty()){
                        embedSpec.setTitle(message.getServerID() + ": " + message.getCommand());
                    }
                    embedSpec.setDescription(message.getCommandOutput());
                    embedSpec.setColor(colorHashMap.get(message.getColor()));
                }).block();
            }
        }
    }

    private String formatMessage(String message) {
        Guild guild = Main.channel.getGuild().block();

        Pattern roleMentions = Pattern.compile("(@\\w+)");
        Matcher roleMentionMatcher = roleMentions.matcher(message);
        while (roleMentionMatcher.find()) {
            for (int i = 0; i <= roleMentionMatcher.groupCount(); i++) {
                String roleMention = roleMentionMatcher.group(i);
                String name = roleMention.substring(1);

                Role role = Main.getRoleByName(name);
                if (role != null) {
                    message = message.replace(roleMention, String.valueOf(role));
                }
            }
        }

        Pattern userMentions = Pattern.compile("(@\\(.+\\))");
        Matcher userMentionMatcher = userMentions.matcher(message);
        while (userMentionMatcher.find()) {
            for (int i = 0; i <= userMentionMatcher.groupCount(); i++) {
                String userMention = userMentionMatcher.group(i);

                // Remove @ and ()
                String name = userMention.substring(2, (userMention.indexOf(')')));

                Member member = Main.getMemberByName(name);
                if (member != null) {
                    message = message.replace(userMention, String.valueOf(member));
                }
            }
        }

        return message;
    }

}
