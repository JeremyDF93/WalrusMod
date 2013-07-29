package co.thewalrus.walrusmod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.thewalrus.walrusmod.command.CommandHandler;
import co.thewalrus.walrusmod.command.CommandMail;
import co.thewalrus.walrusmod.command.CommandTeleport;
import co.thewalrus.walrusmod.command.CommandWalrusMod;
import co.thewalrus.walrusmod.command.CommandWarp;
import co.thewalrus.walrusmod.listener.PlayerListener;
import co.thewalrus.walrusmod.listener.ServerListener;

public class WalrusMod extends JavaPlugin {
	private final CommandHandler commandHandler = new CommandHandler(this);
	private final PlayerListener playerListener = new PlayerListener(this);
	private final ServerListener serverListener = new ServerListener(this);

	private final Random random = new Random();

	private SandboxTimer sandboxTimer = new SandboxTimer(this);
	private MailManager mailManager = new MailManager(this);
	private WarpManager warpManager = new WarpManager(this);

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		if (this.isSandbox()) {
			sandboxTimer.runTaskTimer(this, 30, 30);
			sandboxTimer.setSize(this.getConfig().getInt("sandbox-size", 2000));
			sandboxTimer.start();
		}

		mailManager.cleanMail();

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(playerListener, this);
		pluginManager.registerEvents(serverListener, this);

		commandHandler.registerCommand(new CommandWalrusMod(this));
		commandHandler.registerCommand(new CommandMail(this));
		commandHandler.registerCommand(new CommandWarp(this));
		commandHandler.registerCommand(new CommandTeleport(this));
	}

	@Override
	public void onDisable() {
		sandboxTimer.stop();
	}

	public void addPermission(Permission permission) {
		if (this.getConfig().getBoolean("no-op-permissions", false) && permission.getDefault().equals(PermissionDefault.OP)) {
			permission.setDefault(PermissionDefault.FALSE);
		}

		this.getServer().getPluginManager().addPermission(permission);
	}

	public boolean isSandbox() {
		return this.getConfig().getBoolean("sandbox-world", false);
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public WarpManager getWarpManager() {
		return warpManager;
	}

	public SandboxTimer getSandboxTimer() {
		return sandboxTimer;
	}

	public String getMotd() {
		BufferedReader bufferedReader = null;
		try {
			List<String> list = new ArrayList<String>();
			bufferedReader = new BufferedReader(new FileReader(this.getDataFolder() + "/motd.txt"));

			String temp;
			while ((temp = bufferedReader.readLine()) != null) {
				temp = temp.trim();

				if (temp.length() > 0) {
					list.add(temp);
				}
			}

			if (list.size() > 0) {
				return list.get(random.nextInt(list.size()));
			}
		} catch (FileNotFoundException e) {
			return this.getServer().getMotd();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return this.getServer().getMotd();
	}
}
