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

package club.moddedminecraft.polychat.server.command;

import club.moddedminecraft.polychat.server.DiscordHandler;
import club.moddedminecraft.polychat.server.Main;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.TextChannel;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {
    private final ArrayList<Command> cmdList = new ArrayList<>();
    private ArrayList<String> channels = new ArrayList<>();
    private String prefix;
    private final ArrayList<String> allPrefixRoles = new ArrayList<>();

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void register(Command command) {
        cmdList.add(command);
    }

    public void register(ArrayList<Command> collection) {
        cmdList.addAll(collection);
    }

    public ArrayList<String> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<String> channels) {
        this.channels = channels;
    }

    public ArrayList<Command> getCommandList() {
        return cmdList;
    }

    public void addPrefixRoles(ArrayList<String> roles) {
        allPrefixRoles.addAll(roles);
    }

    public boolean isAllAuthorized(Member user) {
        if (allPrefixRoles.isEmpty()) {
            return true;
        }
        Flux<Role> userRoles = user.getRoles();
        for(Role role : userRoles.toIterable()){
            if(allPrefixRoles.contains(role.getName())){
                return true;
            }
        }
        return false;
    }

    public String run(Message message) {
        TextChannel textChannel = message.getChannel().ofType(TextChannel.class).block();

        // get message content and remove prefix
        String content = message.getContent().get().substring(prefix.length());
        // get first word (command name)
        String[] rawCommand = content.split(" ", 2);
        String cmdName = rawCommand[0];
        String[] args = new String[0];
        if (rawCommand.length > 1) {
            args = rawCommand[1].split(" ");
        }
        // get everything after first word (args)
        for (Command cmd : cmdList) {
            if (cmd.getName().equals(cmdName)) {
                if (cmd instanceof MinecraftCommand) {
                    boolean authorized = isAllAuthorized(message.getAuthorAsMember().block());
                    ((MinecraftCommand) cmd).setAllAuthorized(authorized);
                    return ((MinecraftCommand) cmd).verifyAndRun(message.getAuthorAsMember().block(), args, textChannel.getName());
                } else if (cmd instanceof RoleCommand) {
                    return ((RoleCommand) cmd).verifyAndRun(message.getAuthorAsMember().block(), args, textChannel.getName());
                } else {
                    return cmd.run(args, textChannel.getName());
                }
            }
        }
        return "";
    }
}
