package me.pirogoeth.Waypoint;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;
import java.util.logging.Logger;

public class WaypointPlayerListener extends PlayerListener {
    public static Waypoint plugin;
    Logger log = Logger.getLogger("Minecraft");
    
    public WaypointPlayerListener (Waypoint instance) {
    	plugin = instance;
    }
	public void onPlayerChat(PlayerChatEvent event) {
	    // String[] split = event.getMessage().split(" ");
		// Player player = event.getPlayer();
		@SuppressWarnings("unused")
		Configuration config = WaypointPlayerListener.plugin.config;
	}
}
