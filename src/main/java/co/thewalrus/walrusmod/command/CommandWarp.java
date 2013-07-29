package co.thewalrus.walrusmod.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import co.thewalrus.walrusmod.WalrusMod;
import co.thewalrus.walrusmod.command.exception.WrongUsageException;

public class CommandWarp extends CommandBase {
	public CommandWarp(WalrusMod plugin) {
		super(plugin);
		this.addPermission(new Permission(this.getPermissionName() + ".add", PermissionDefault.OP));
		this.addPermission(new Permission(this.getPermissionName() + ".remove", PermissionDefault.OP));
		this.addPermission(new Permission(this.getPermissionName() + ".list", PermissionDefault.TRUE));
		this.addPermission(new Permission(this.getPermissionName() + ".other", PermissionDefault.OP));
	}

	@Override
	public String getName() {
		return "warp";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			Player player;
			if (args.length != 3) {
				if (sender instanceof ConsoleCommandSender) {
					throw new CommandException("This command cannot be ran from the console");
				}

				player = this.getPlayerFromCommandSender(sender);
			} else {
				if (sender.hasPermission(this.getPermissionName() + ".other")) {
					player = this.getPlayerByName(args[2]);
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			}

			if (args[0].equalsIgnoreCase("add")) {
				if (sender.hasPermission(this.getPermissionName() + ".add")) {
					if (args.length >= 2) {
						String name = args[1].toLowerCase();

						if (plugin.getWarpManager().addWarp(player.getWorld(), player.getLocation(), name)) {
							this.notifyAdmins(sender, String.format("Added new warp %s", name));
						} else {
							throw new CommandException(String.format("A warp with the name %s already exists", name));
						}
					} else {
						throw new WrongUsageException("/warp add <name>");
					}
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission(this.getPermissionName() + ".remove")) {
					if (args.length >= 2) {
						String name = args[1].toLowerCase();

						if (plugin.getWarpManager().removeWarp(player.getWorld(), name)) {
							this.notifyAdmins(sender, String.format("Removed warp %s", name));
						} else {
							throw new CommandException(String.format("No warp was found by the name %s", name));
						}
					} else {
						throw new WrongUsageException("/warp remove <name>");
					}
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission(this.getPermissionName() + ".list")) {
					sender.sendMessage("TODO: list"); // TODO List
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			} else {
				String name = args[0].toLowerCase();

				if (plugin.getWarpManager().isWarp(player.getWorld(), name)) {
					Location location = plugin.getWarpManager().getWarp(player.getWorld(), name);
					player.teleport(location);
					this.notifyAdmins(sender, String.format("Warped %s to %s", player.getName(), name));
				} else {
					throw new CommandException(String.format("No warp was found by the name %s", name));
				}
			}
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		Player player = this.getPlayerFromCommandSender(sender);
		List<String> list = plugin.getWarpManager().getWarpList(player.getWorld());
		if (args.length == 1) {
			list.addAll(Arrays.asList(new String[] { "add", "list", "remove" }));
			return this.getListOfStringsMatchingLastWord(args, list.toArray(new String[list.size()]));
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			return this.getListOfStringsMatchingLastWord(args, list.toArray(new String[list.size()]));
		} else {
			return null;
		}
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
