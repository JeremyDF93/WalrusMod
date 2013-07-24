package co.thewalrus.walrusmod;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SandboxTimer extends BukkitRunnable {
	private WalrusMod plugin;

	private boolean running;
	private int size;

	public SandboxTimer(WalrusMod plugin) {
		this.plugin = plugin;
	}

	public void start() {
		if (!running) {
			running = true;
		}
	}

	public void stop() {
		if (running) {
			running = false;
			this.cancel();
		}
	}

	@Override
	public void run() {
		if (running) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				Location location = player.getLocation();
				boolean flag = false;
				if (location.getX() > size) {
					location.setX(size);
					flag = true;
				} else if (location.getX() < -size) {
					location.setX(-size);
					flag = true;
				} else if (location.getZ() > size) {
					location.setZ(size);
					flag = true;
				} else if (location.getZ() < -size) {
					location.setZ(-size);
					flag = true;
				}

				if (flag) {
					player.sendMessage(ChatColor.RED + "You cannot go any further in this direction");
					player.teleport(location);
				}
			}
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
