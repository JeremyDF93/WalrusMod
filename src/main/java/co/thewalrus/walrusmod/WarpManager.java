package co.thewalrus.walrusmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WarpManager {
	private final WalrusMod plugin;

	public WarpManager(WalrusMod plugin) {
		this.plugin = plugin;
	}

	public boolean isWarp(World world, String name) {
		JSONArray warps = this.readJsonArray(world);
		for (int i = 0; i < warps.size(); i++) {
			JSONObject warp = (JSONObject) warps.get(i);
			if (name.equalsIgnoreCase((String) warp.get("name"))) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean addWarp(World world, Location location, String name) {
		if (this.isWarp(world, name)) {
			return false;
		}

		JSONArray warps = this.readJsonArray(world);
		JSONObject warp = new JSONObject();
		warp.put("name", name);
		warp.put("x", location.getX());
		warp.put("y", location.getY());
		warp.put("z", location.getZ());
		warp.put("yaw", (long) location.getYaw());
		warp.put("pitch", (long) location.getPitch());
		warps.add(warp);
		this.writeJsonArray(world, warps);

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean removeWarp(World world, String name) {
		boolean removed = false;
		JSONArray warps = (JSONArray) this.readJsonArray(world);
		Iterator<JSONObject> iterator = warps.iterator();
		while (iterator.hasNext()) {
			JSONObject warp = iterator.next();
			if (name.equalsIgnoreCase((String) warp.get("name"))) {
				removed = true;
				iterator.remove();
				break;
			}
		}
		this.writeJsonArray(world, warps);

		return removed;
	}

	public List<String> getWarpList(World world) {
		JSONArray warps = this.readJsonArray(world);
		List<String> warpList = new ArrayList<String>();
		for (int i = 0; i < warps.size(); i++) {
			JSONObject warp = (JSONObject) warps.get(i);
			warpList.add((String) warp.get("name"));
		}

		return warpList;
	}

	public Location getWarp(World world, String name) {
		JSONArray warps = this.readJsonArray(world);
		for (int i = 0; i < warps.size(); i++) {
			JSONObject warp = (JSONObject) warps.get(i);
			if (name.equalsIgnoreCase((String) warp.get("name"))) {
				double x = (Double) warp.get("x");
				double y = (Double) warp.get("y");
				double z = (Double) warp.get("z");
				long yaw = (Long) warp.get("yaw");
				long pitch = (Long) warp.get("pitch");

				return new Location(world, x, y, z, yaw, pitch);
			}
		}

		return null;
	}

	private JSONArray readJsonArray(World world) {
		JSONParser jsonParser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(new File(plugin.getDataFolder(), String.format("warps_%s.json", world.getName())));
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

	private void writeJsonArray(World world, JSONArray jsonArray) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(plugin.getDataFolder(), String.format("warps_%s.json", world.getName())));
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
