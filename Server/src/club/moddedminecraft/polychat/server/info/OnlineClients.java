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
import java.util.HashMap;

public class OnlineClients {

    private ArrayList<Client> onlineClients;
    private HashMap<String, Client> clientMap;

    public OnlineClients() {
        this.onlineClients = new ArrayList<>();
        this.clientMap = new HashMap<>();
    }

    public ArrayList<Client> getServers() {
        return onlineClients;
    }

    public Client getServer(String serverID) {
        System.out.println(clientMap);
        return clientMap.getOrDefault(serverID, null);
    }

    public Client getServerNormalized(String serverID) {
        for (Client server : onlineClients) {
            String sID = server.getServerID();
            sID = sID.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
            if (sID.equals(serverID.toLowerCase())) {
                return server;
            }
        }
        return null;
    }

    //Adds a server to the list
    public void serverConnected(String serverID, String serverName, MessageBus messageBus) {
        Client toRemove = clientMap.get(serverID);
        if (toRemove != null) {
            onlineClients.remove(toRemove);
        }
        Client connected = new Client(serverID, serverName, messageBus);
        this.onlineClients.add(connected);
        this.clientMap.put(serverID, connected);
    }

    //Marks a server as online
    public void serverOnline(String serverID) {
        Client server = clientMap.get(serverID);
        if (server != null) {
            server.setStarted();
        }
    }

    //Removes a server as it went offline
    public void serverOffline(String serverID) {
        Client toRemove = clientMap.get(serverID);
        onlineClients.remove(toRemove);
        clientMap.remove(serverID);
    }

}
