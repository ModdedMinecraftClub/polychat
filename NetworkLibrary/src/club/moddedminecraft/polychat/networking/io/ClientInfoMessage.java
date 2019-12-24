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

package club.moddedminecraft.polychat.networking.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Sends data to main polychat server when the game launches
// Data is used when displaying info about all servers in discord
public class ClientInfoMessage extends AbstractMessage {
    protected static final short MESSAGE_TYPE_ID = 8;
    private final String serverID, serverName;

    public ClientInfoMessage(String serverID, String serverName) {
        this.serverID = serverID;
        this.serverName = serverName;
    }

    public ClientInfoMessage(DataInputStream istream) throws IOException {
        this.serverID = istream.readUTF();
        this.serverName = istream.readUTF();
    }

    public String getServerID() {
        return this.serverID;
    }

    public String getServerName() {
        return this.serverName;
    }

    @Override
    protected void send(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(MESSAGE_TYPE_ID);
        dataOutputStream.writeUTF(serverID);
        dataOutputStream.writeUTF(serverName);
    }
}
