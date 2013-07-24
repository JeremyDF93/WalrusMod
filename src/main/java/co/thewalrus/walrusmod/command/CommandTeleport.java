package co.thewalrus.walrusmod.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import co.thewalrus.walrusmod.WalrusMod;
import co.thewalrus.walrusmod.command.exception.WrongUsageException;

public class CommandTeleport extends CommandBase {
	public CommandTeleport(WalrusMod plugin) {
		super(plugin);
		this.addPermission(this.getPermission("other", PermissionDefault.OP));
		this.addPermission(this.getPermission("pos", PermissionDefault.OP));
	}

	@Override
	public String getName() {
		return "tp";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			Player player;
			if (args.length != 2 && args.length != 4) {
				player = this.getPlayerFromCommandSender(sender);
			} else {
				if (sender.hasPermission(this.getPermissionName() + ".other")) {
					player = this.getPlayerFromCommandSender(sender, args[0]);
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			}

			if (args.length != 3 && args.length != 4) {
				if (args.length == 1 || args.length == 2) {
					Player target = this.getPlayerFromCommandSender(sender, args[args.length - 1]);
					if (target.getWorld() != player.getWorld()) {
						this.notifyAdmins(sender, "Unable to teleport because players are not in the same dimension");
						return;
					}

					player.teleport(target.getLocation());
					this.notifyAdmins(sender, String.format("Teleported %s to %s", player.getName(), target.getName()));
				}
			} else if (player.getWorld() != null) {
				if (player.hasPermission(this.getPermissionName() + ".pos")) {
					int i = args.length - 3;
					Location location = player.getLocation();
					double x = this.getCoordinate(sender, location.getX(), args[i++]);
					double y = this.getCoordinate(sender, location.getY(), args[i++], 0, 0);
					double z = this.getCoordinate(sender, location.getZ(), args[i++]);

					player.teleport(new Location(player.getWorld(), x, y, z));
					this.notifyAdmins(sender, String.format("Teleported %s to %.2f,%.2f,%.2f", player.getName(), x, y, z));
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			}
		}
	}

	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, this.getAllUsernames());
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
