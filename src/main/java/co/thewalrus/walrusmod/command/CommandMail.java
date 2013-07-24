package co.thewalrus.walrusmod.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import co.thewalrus.walrusmod.MailManager;
import co.thewalrus.walrusmod.WalrusMod;
import co.thewalrus.walrusmod.command.exception.WrongUsageException;

public class CommandMail extends CommandBase {
	public CommandMail(WalrusMod plugin) {
		super(plugin);
		this.addPermission(this.getPermission("send", PermissionDefault.TRUE));
	}

	@Override
	public String getName() {
		return "mail";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			throw new CommandException("This command cannot be ran from the console");
		}

		MailManager mailManager = plugin.getMailManager();
		Player player = this.getPlayerFromCommandSender(sender);

		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			if (args[0].equalsIgnoreCase("send")) {
				if (sender.hasPermission(this.getPermissionName() + ".send")) {
					if (args.length >= 3) {
						mailManager.sendMail(player, args[1], this.getString(args, 2, false));
						player.sendMessage(ChatColor.YELLOW + "Mail sent to " + ChatColor.GREEN + args[1]);

						Player target = plugin.getServer().getPlayer(args[1]);
						if (target != null) {
							target.sendMessage(ChatColor.YELLOW + "You have just recieved new mail from " + ChatColor.GREEN + player.getName());
							target.sendMessage(ChatColor.YELLOW + "Type '/mail read' to read your new messages");
						}
					} else {
						throw new WrongUsageException("/mail send <name> <private message ...>");
					}
				} else {
					throw new CommandException("You do not have permission to use this command");
				}
			} else if (args[0].equalsIgnoreCase("read")) {
				mailManager.readMail(player);
			} else if (args[0].equalsIgnoreCase("clear")) {
				mailManager.clearMail(player);
			} else if (args[0].equalsIgnoreCase("check")) {
				mailManager.checkMail(player);
			} else {
				throw new WrongUsageException(command.getUsage());
			}
		}
	}

	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? this.getListOfStringsMatchingLastWord(args, new String[] { "send", "read", "clear", "check" }) : null;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
