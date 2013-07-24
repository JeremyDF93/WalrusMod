package co.thewalrus.walrusmod.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import co.thewalrus.walrusmod.WalrusMod;

public class PlayerListener implements Listener {
	private WalrusMod plugin;

	public PlayerListener(WalrusMod plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.getMailManager().checkMail(player);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (plugin.isSandbox()) {
			Player player = event.getPlayer();
			Location location = event.getTo();

			int size = plugin.getSandboxTimer().getSize();
			if (location.getX() > size || location.getX() < -size || location.getZ() > size || location.getZ() < -size) {
				player.sendMessage(ChatColor.RED + "You cannot teleport here");
				event.setCancelled(true);
			}
		}
	}
}
