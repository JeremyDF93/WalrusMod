package co.thewalrus.walrusmod.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import co.thewalrus.walrusmod.WalrusMod;
import co.thewalrus.walrusmod.command.exception.NumberInvalidException;

import com.google.common.primitives.Doubles;

public abstract class CommandBase {
	protected WalrusMod plugin;

	public CommandBase(WalrusMod plugin) {
		this.plugin = plugin;
		this.addPermission(this.getPermission());
	}

	public abstract String getName();

	public abstract void performCommand(CommandSender sender, Command command, String[] args);

	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return null;
	}

	public void addPermission(Permission permission) {
		plugin.addPermission(permission);
	}

	public Permission getPermission() {
		if (plugin.getConfig().getBoolean("no-op-permissions", false) && this.getPermissionDefault().equals(PermissionDefault.OP)) {
			return new Permission(this.getPermissionName(), PermissionDefault.FALSE);
		}

		return new Permission(this.getPermissionName(), this.getPermissionDefault());
	}

	public Permission getPermission(String name, PermissionDefault permissionDefault) {
		if (plugin.getConfig().getBoolean("no-op-permissions", false) && permissionDefault.equals(PermissionDefault.OP)) {
			return new Permission(this.getPermissionName() + "." + name, PermissionDefault.FALSE);
		}

		return new Permission(this.getPermissionName() + "." + name, permissionDefault);
	}

	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

	public String getPermissionName() {
		return "walrusmod." + this.getName();
	}

	public Player getPlayerByName(String name) {
		Player player = plugin.getServer().getPlayer(name);

		if (player != null) {
			return player;
		} else {
			throw new CommandException(String.format("Can't find player %s", name));
		}
	}

	public Player getPlayerFromCommandSender(CommandSender sender, String name) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			return this.getPlayerByName(name);
		}
	}

	public Player getPlayerFromCommandSender(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			throw new CommandException("You must specify which player you wish to perform this action on");
		}
	}

	public String getString(String[] args, int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; ++i) {
			if (i > index) {
				builder.append(" ");
			}

			builder.append(args[i]);
		}

		return builder.toString();
	}

	public String getStringList(String[] args, int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; ++i) {
			if (i > index) {
				builder.append(", ");
			}

			builder.append(args[i]);
		}

		return builder.toString();
	}

	public boolean doesStringStartWith(String input, String prefix) {
		return prefix.regionMatches(true, 0, input, 0, input.length());
	}

	public List<String> getListOfStringsMatchingLastWord(String[] args, String... input) {
		String string = args[args.length - 1];
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < input.length; ++i) {
			if (doesStringStartWith(string, input[i])) {
				list.add(input[i]);
			}
		}

		return list;
	}

	public void notifyAdmins(CommandSender sender, String message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (sender == player) {
				player.sendMessage(ChatColor.GRAY + message);
			} else {
				if (player.isOp()) {
					player.sendMessage(ChatColor.GRAY + "[" + sender.getName() + "] " + message);
				}
			}
		}

		if (sender instanceof ConsoleCommandSender) {
			plugin.getLogger().info(message);
		} else {
			plugin.getLogger().info("[" + sender.getName() + "] " + message);
		}
	}

	public String[] getAllUsernames() {
		Player[] players = plugin.getServer().getOnlinePlayers();
		String[] usernames = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			usernames[i] = players[i].getName();
		}

		return usernames;
	}

	public double getCoordinate(CommandSender sender, double current, String input) {
		return this.getCoordinate(sender, current, input, -30000000, 30000000);
	}

	public double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
		boolean relative = input.startsWith("~");
		double result = relative ? current : 0;

		if (!relative || input.length() > 1) {
			boolean exact = input.contains(".");
			if (relative) {
				input = input.substring(1);
			}

			result += parseDouble(input);

			if (!exact && !relative) {
				result += 0.5f;
			}
		}

		if (min != 0 || max != 0) {
			if (result < min) {
				throw new NumberInvalidException(String.format("The number you have entered (%d) is too small, it must be at least %d", result, min));
			}

			if (result > max) {
				throw new NumberInvalidException(String.format("The number you have entered (%d) is too big, it must be at most %d", result, max));
			}
		}

		return result;
	}

	public int parseInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException var3) {
			throw new NumberInvalidException(String.format("%s is not a valid number", input));
		}
	}

	public int parseInt(String input, int min) {
		return this.parseInt(input, min, Integer.MAX_VALUE);
	}

	public int parseInt(String input, int min, int max) {
		int result = this.parseInt(input);

		if (result < min) {
			throw new NumberInvalidException(String.format("The number you have entered (%d) is too small, it must be at least %d", result, min));
		} else if (result > max) {
			throw new NumberInvalidException(String.format("The number you have entered (%d) is too big, it must be at most %d", result, max));
		} else {
			return result;
		}
	}

	public double parseDouble(String input) {
		try {
			double result = Double.parseDouble(input);

			if (!Doubles.isFinite(result)) {
				throw new NumberInvalidException(String.format("%s is not a valid number", input));
			} else {
				return result;
			}
		} catch (NumberFormatException e) {
			throw new NumberInvalidException(String.format("%s is not a valid number", input));
		}
	}
}
