package co.thewalrus.walrusmod.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;

import co.thewalrus.walrusmod.WalrusMod;

public class CommandWalrusMod extends CommandBase {
	public CommandWalrusMod(WalrusMod plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "walrusmod";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		PluginDescriptionFile descriptionFile = plugin.getDescription();
		sender.sendMessage(ChatColor.RED + descriptionFile.getName() + ChatColor.RESET + " version " + ChatColor.GREEN + descriptionFile.getVersion());
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
