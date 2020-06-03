package club.moddedminecraft.polychat.server.command;

import java.util.Map;

public class CommandAlias extends Command {
    private final String alias;
    private Command command;

    public CommandAlias(String name, Map<String, Object> args) {
        super(name, args);
        alias = (String) args.get("alias");
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getAlias() {
        return alias;
    }

    public String run(String[] args, String channel) {
        return command.run(args, channel);
    }
}
