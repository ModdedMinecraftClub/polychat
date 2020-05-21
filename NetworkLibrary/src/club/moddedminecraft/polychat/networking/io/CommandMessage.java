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
import java.util.ArrayList;

public class CommandMessage extends AbstractMessage {

    protected static final short MESSAGE_TYPE_ID = 6;
    private final String serverID, name, defaultcmd, channel;
    private final int listSize;
    private final ArrayList<String> args;

    public CommandMessage(String serverID, String name, String defaultcmd, ArrayList<String> args, String channel) {
        this.serverID = serverID;
        this.name = name;
        this.defaultcmd = defaultcmd;
        this.args = args;
        this.listSize = args.size();
        this.channel = channel;
    }

    public CommandMessage(DataInputStream istream) throws IOException {
        this.serverID = istream.readUTF();
        this.name = istream.readUTF();
        this.defaultcmd = istream.readUTF();
        this.listSize = istream.readInt();
        this.args = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            args.add(istream.readUTF());
        }
        this.channel = istream.readUTF();
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public String getServerID() {
        return serverID;
    }

    public String getCommand() {
        return defaultcmd;
    }

    public String getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void send(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(MESSAGE_TYPE_ID);
        dataOutputStream.writeUTF(serverID);
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeUTF(defaultcmd);
        dataOutputStream.writeInt(this.args.size());
        for (String arg : this.args) {
            dataOutputStream.writeUTF(arg);
        }
        dataOutputStream.writeUTF(channel);
    }
}
