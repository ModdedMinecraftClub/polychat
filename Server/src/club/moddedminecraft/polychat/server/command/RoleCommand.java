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

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Map;

public abstract class RoleCommand extends Command {

    private final ArrayList<String> roles;

    public RoleCommand(String name, Map<String, Object> args) {
        super(name, args);
        this.roles = (ArrayList<String>) args.get("roles");
    }

    public boolean verifyRole(Member user, ArrayList<String> roles) {
        Flux<Role> userRoles = user.getRoles();
        if (roles == null) {
            return true;
        }
        for(Role role : userRoles.toIterable()){
            if(roles.contains(role.getName())){
                return true;
            }
        }
        return false;
    }

    public String verifyAndRun(Member user, String[] args, String channel) {
        if (!verifyRole(user, roles)) {
            return "User does not have permission to perform this command";
        }
        return run(args, channel);
    }

}
