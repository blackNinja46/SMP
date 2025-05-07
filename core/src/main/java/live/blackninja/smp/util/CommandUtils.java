package live.blackninja.smp.util;

import live.blackninja.smp.Core;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandUtils {

    public CommandUtils(String name, CommandExecutor executor, JavaPlugin plugin) {
        plugin.getCommand(name).setExecutor(executor);
    }

    public CommandUtils(String name, CommandExecutor executor, TabCompleter tabCompletor, JavaPlugin plugin) {
        plugin.getCommand(name).setExecutor(executor);
        plugin.getCommand(name).setTabCompleter(tabCompletor);
    }

}
