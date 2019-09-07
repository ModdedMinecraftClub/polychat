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

public class CommandOutputMessage extends Message {

    protected static final short MESSAGE_TYPE_ID = 7;
    private final String command;
    private final String commandOutput;
    private final String channel;
    private final String serverID;

    public CommandOutputMessage(String serverID, String command, String commandOutput, String channel) {
        this.serverID = serverID;
        this.command = command;
        this.commandOutput = commandOutput;
        this.channel = channel;
    }

    public CommandOutputMessage(DataInputStream istream) throws IOException {
        this.serverID = istream.readUTF();
        this.command = istream.readUTF();
        this.commandOutput = istream.readUTF();
        this.channel = istream.readUTF();
    }

    public String getServerID() {
        return serverID;
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    public String getCommand() {
        return command;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    protected void send(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(MESSAGE_TYPE_ID);
        dataOutputStream.writeUTF(serverID);
        dataOutputStream.writeUTF(command);
        dataOutputStream.writeUTF(commandOutput);
        dataOutputStream.writeUTF(channel);
    }

}
