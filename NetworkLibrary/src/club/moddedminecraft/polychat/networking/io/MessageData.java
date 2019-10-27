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

public final class MessageData {
    private final AbstractMessage message;
    private final MessageBus messageBus;

    public MessageData(AbstractMessage message, MessageBus messageBus) {
        this.message = message;
        this.messageBus = messageBus;
    }

    public AbstractMessage getMessage() {
        return message;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }
}
