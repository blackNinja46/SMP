package live.blackninja.smp.util;

import live.blackninja.smp.Core;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class CommandUtils {

    public CommandUtils(String name, CommandExecutor executor, Core core) {
        core.getCommand(name).setExecutor(executor);
    }

    public CommandUtils(String name, CommandExecutor executor, TabCompleter tabCompletor, Core core) {
        core.getCommand(name).setExecutor(executor);
        core.getCommand(name).setTabCompleter(tabCompletor);
    }

}
