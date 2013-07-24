package co.thewalrus.walrusmod.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import co.thewalrus.walrusmod.WalrusMod;
import co.thewalrus.walrusmod.command.exception.WrongUsageException;

public class CommandHandler implements CommandExecutor, TabCompleter {
	private WalrusMod plugin;

	private Map<String, CommandBase> commandMap = new HashMap<String, CommandBase>();

	public CommandHandler(WalrusMod plugin) {
		this.plugin = plugin;
	}

	public void registerCommand(CommandBase commandBase) {
		String commandName = commandBase.getName();
		commandMap.put(commandName, commandBase);
		plugin.getCommand(commandName).setExecutor(this);
		plugin.getCommand(commandName).setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (commandMap.containsKey(command.getName())) {
				CommandBase commandBase = commandMap.get(command.getName());
				if (sender.hasPermission(commandBase.getPermissionName())) {
					commandBase.performCommand(sender, command, args);
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			}
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		} catch (WrongUsageException e) {
			sender.sendMessage(ChatColor.RED + "Usage: " + e.getMessage());
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (commandMap.containsKey(command.getName())) {
			CommandBase commandBase = commandMap.get(command.getName());
			return commandBase.addTabCompletionOptions(sender, command, args);
		}

		return null;
	}
}
