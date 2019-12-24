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

package club.moddedminecraft.polychat.server.info;

import club.moddedminecraft.polychat.networking.io.MessageBus;

import java.util.ArrayList;

public class Client {
    //Server id is the chat prefix and server name is the full name of the server, such as: [REV] / Revelation
    private final String serverID, serverName;
    //Since the mod establishes a socket with the main polychat server in preinit, the listing will report
    //  the server as starting until the server says its fully started
    private boolean started;
    private MessageBus messageBus;

    public Client(String serverID, String serverName, MessageBus messageBus) {
        this.serverID = serverID;
        this.serverName = serverName;
        this.started = false;
        this.messageBus = messageBus;
    }

    //Sets this server as started
    public void setStarted() {
        this.started = true;
    }

    //Gets the server ID
    public String getServerID() {
        return this.serverID;
    }

    //Gets the server name
    public String getServerName() {
        return this.serverName;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }
}
