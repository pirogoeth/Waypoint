package me.pirogoeth.Waypoint;

import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import java.util.logging.Logger;

public class WaypointPlayerListener extends PlayerListener {
    public static Waypoint plugin;
    public static Configuration config;
    Logger log = Logger.getLogger("Minecraft");
    public WaypointPlayerListener (Waypoint instance) {
    	plugin = instance;
    	config = plugin.config;
    }
    public static String UserNodeChomp (Player p, String arg, String sub)
    {
        String a = "users." + p.getName().toString() + "." + arg + "." + sub;
        return a;
    }
    public void onPlayerBedLeave(PlayerBedLeaveEvent event)
    {
        Player player = event.getPlayer();
        Block bed = event.getBed();
        if (config.getProperty("set_home_to_bed") == "false") { return; }
        if (!plugin.permissionHandler.has(player, "waypoint.home.set_on_bed_leave")) { return; };
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        World w = player.getLocation().getWorld();
        config.setProperty(UserNodeChomp(player, "home", "coord.X"), x);
        config.setProperty(UserNodeChomp(player, "home", "coord.Y"), y);
        config.setProperty(UserNodeChomp(player, "home", "coord.Z"), z);
        config.setProperty(UserNodeChomp(player, "home", "world"), w.getName().toString());
        config.save();
        player.sendMessage(ChatColor.AQUA + "[Waypoint] " + player.getName().toString() + ", your home has been set to the bed you juist got out of.");
        return;
    }
}
