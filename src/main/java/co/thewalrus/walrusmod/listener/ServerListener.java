package co.thewalrus.walrusmod.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import co.thewalrus.walrusmod.WalrusMod;

public class ServerListener implements Listener {
	private WalrusMod plugin;

	public ServerListener(WalrusMod plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd(plugin.getMotd());
	}
}
