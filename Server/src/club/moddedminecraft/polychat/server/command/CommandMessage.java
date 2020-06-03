package club.moddedminecraft.polychat.server.command;

import club.moddedminecraft.polychat.server.Main;

import java.util.Map;

public class CommandMessage extends Command {
    private final String message;

    public CommandMessage(String name, Map<String, Object> args) {
        super(name, args);
        this.message = (String) args.get("message");
    }

    public String run(String[] args, String channel) {
        return this.message;
    }
}
