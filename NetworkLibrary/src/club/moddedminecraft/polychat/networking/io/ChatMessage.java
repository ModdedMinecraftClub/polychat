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
package club.moddedminecraft.polychat.networking.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ChatMessage extends AbstractMessage {
    protected static final short MESSAGE_TYPE_ID = 1;
    private final String username, message, formattedMessage;

    protected ChatMessage(DataInputStream dataInputStream) throws IOException {
        username = dataInputStream.readUTF();
        message = dataInputStream.readUTF();
        formattedMessage = dataInputStream.readUTF();
    }

    public ChatMessage(String username, String message, String formattedMessage) {
        this.username = username;
        this.message = message;
        this.formattedMessage = formattedMessage;
    }

    public String getUsername() {
                                      return username;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    @Override
    protected void send(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(MESSAGE_TYPE_ID);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(message);
        dataOutputStream.writeUTF(formattedMessage);
    }

}
