package co.thewalrus.walrusmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MailManager {
	private WalrusMod plugin;

	public MailManager(WalrusMod plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	public void sendMail(Player sender, String recipient, String text) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();

		JSONArray mail = this.readJsonArray();
		JSONObject message = new JSONObject();
		message.put("sender", sender.getName());
		message.put("recipient", recipient);
		message.put("opened", false);
		message.put("date", dateFormat.format(now));
		message.put("text", text);
		mail.add(message);
		this.writeJsonArray(mail);
	}

	@SuppressWarnings("unchecked")
	public void readMail(Player player) {
		boolean hasMail = false;
		JSONArray mail = this.readJsonArray();
		for (int i = 0; i < mail.size(); i++) {
			JSONObject message = (JSONObject) mail.get(i);
			Player recipient = plugin.getServer().getPlayer((String) message.get("recipient"));
			if (player == recipient) {
				hasMail = true;
				message.put("opened", true);

				String sender = (String) message.get("sender");
				String text = (String) message.get("text");
				player.sendMessage(ChatColor.YELLOW + "From " + ChatColor.GREEN + sender + ChatColor.YELLOW + ": " + ChatColor.RESET + text);

				try {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date now = new Date();
					Date sent = dateFormat.parse((String) message.get("date"));

					long diff = now.getTime() - sent.getTime();
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					StringBuilder builder = new StringBuilder();
					builder.append(ChatColor.YELLOW + "Sent ");
					if (diffDays > 0) {
						builder.append(ChatColor.GREEN + String.valueOf(diffDays) + ChatColor.YELLOW + " days");
						if (diffHours > 0) {
							builder.append(", ");
						} else {
							builder.append(" and ");
						}
					}
					if (diffHours > 0) {
						builder.append(ChatColor.GREEN + String.valueOf(diffHours) + ChatColor.YELLOW + " hours and ");
					}
					builder.append(ChatColor.GREEN + String.valueOf(diffMinutes) + ChatColor.YELLOW + " minutes ago");
					player.sendMessage(builder.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		if (hasMail) {
			player.sendMessage(ChatColor.YELLOW + "Type '/mail clear' to clear your read messages");
		} else {
			player.sendMessage(ChatColor.YELLOW + "No messages were found");
		}

		this.writeJsonArray(mail);
	}

	@SuppressWarnings("unchecked")
	public void cleanMail() {
		int removed = 0;
		JSONArray mail = this.readJsonArray();
		Iterator<JSONObject> iterator = mail.iterator();
		while (iterator.hasNext()) {
			JSONObject message = iterator.next();
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = new Date();
				Date sent = dateFormat.parse((String) message.get("date"));

				long diff = now.getTime() - sent.getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000);

				if (diffDays >= 30) {
					removed++;
					iterator.remove();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		this.writeJsonArray(mail);

		if (removed > 0) {
			plugin.getLogger().info("Removed " + removed + " expired messages");
		}
	}

	@SuppressWarnings("unchecked")
	public void clearMail(Player player) {
		int removed = 0;
		JSONArray mail = this.readJsonArray();
		Iterator<JSONObject> iterator = mail.iterator();
		while (iterator.hasNext()) {
			JSONObject message = iterator.next();
			Player recipient = plugin.getServer().getPlayer((String) message.get("recipient"));
			if (player == recipient) {
				if ((Boolean) message.get("opened")) {
					removed++;
					iterator.remove();
				}
			}
		}
		this.writeJsonArray(mail);

		if (removed > 0) {
			player.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.GREEN + removed + ChatColor.YELLOW + " messages");
		} else {
			player.sendMessage(ChatColor.YELLOW + "No messages were found");
		}
	}

	public void checkMail(Player player) {
		int unread = 0;
		JSONArray mail = this.readJsonArray();
		for (int i = 0; i < mail.size(); i++) {
			JSONObject message = (JSONObject) mail.get(i);
			Player recipient = plugin.getServer().getPlayer((String) message.get("recipient"));
			if (player == recipient) {
				if (!(Boolean) message.get("opened")) {
					unread++;
				}
			}
		}

		if (unread > 0) {
			player.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + unread + ChatColor.YELLOW + " new messages");
			player.sendMessage(ChatColor.YELLOW + "Type '/mail read' to read your new messages");
		} else {
			player.sendMessage(ChatColor.YELLOW + "No new messages were found");
		}
	}

	private JSONArray readJsonArray() {
		JSONParser jsonParser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(new File(plugin.getDataFolder(), "mail.json"));
			return (JSONArray) jsonParser.parse(reader);
		} catch (FileNotFoundException e) {
			return new JSONArray();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new JSONArray();
	}

	private void writeJsonArray(JSONArray jsonArray) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(plugin.getDataFolder(), "mail.json"));
			writer.write(jsonArray.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
