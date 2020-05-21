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

import club.moddedminecraft.polychat.networking.io.CommandMessage;
import club.moddedminecraft.polychat.server.Main;
import club.moddedminecraft.polychat.server.info.OnlineServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftCommand extends RoleCommand {

    private final String defaultcmd;
    private final int argCount;
    private String channel;

    public MinecraftCommand(String name, Map<String, Object> args) {
        super(name, args);
        this.defaultcmd = (String) args.get("command");
        this.argCount = calculateParameters(defaultcmd);
    }

    public int calculateParameters(String command) {
        Pattern pattern = Pattern.compile("(\\$\\d+)");
        Matcher matcher = pattern.matcher(command);
        return matcher.groupCount();
    }

    public String run(String[] inputArgs, String channel) {

        if (inputArgs.length < 1) {
            return "Error running command: Server prefix required";
        }
        String serverID = inputArgs[0];

        ArrayList<OnlineServer> executeServers = new ArrayList<>();
        if (serverID.equals("<all>")) {
            executeServers.addAll(Main.serverInfo.getServers());
        } else {
            OnlineServer server = Main.serverInfo.getServerNormalized(serverID);
            if (server == null) {
                return "Error running command: server prefix " + serverID + " does not exist.";
            }
            executeServers.add(server);
        }


        // process arguments into ArrayList (exclude the first element which is the prefix)
        ArrayList<String> args = new ArrayList<>(Arrays.asList(inputArgs).subList(1, inputArgs.length));

        for (OnlineServer server : executeServers) {
            server.getMessageBus().sendMessage(new CommandMessage(server.getServerID(), this.getName(), defaultcmd, args, channel));
        }

        return "";
    }

}
