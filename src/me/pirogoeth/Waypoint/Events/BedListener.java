package me.pirogoeth.Waypoint.Events;

import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.ChatColor;
import java.util.logging.Logger;

import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Waypoint;

public class BedListener extends PlayerListener {
    // core variables
    public static Waypoint plugin;
    Logger log = Logger.getLogger("Minecraft");
    public BedListener (Waypoint instance) {
    	plugin = instance;
    }
    public static String UserNodeChomp (Player p, String arg, String sub)
    {
        String a = "users." + p.getName().toString() + "." + arg + "." + sub;
        return a;
    }
    public static String HomeNodeChomp (Player p, World w, String sub)
    {
        String a = "home." + p.getName().toString() + "." + w.getName().toString() + "." + sub;
        return a;
    }
    public void onPlayerBedEnter(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.config.getMain().getBoolean("home.set_home_at_bed", false) == false) { return; };
        if (!plugin.permissions.has(player, "waypoint.home.set_on_bed_leave")) { return; };
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        World w = player.getLocation().getWorld();
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.X"), x);
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.Y"), y);
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.Z"), z);
        plugin.config.getUsers().save();
        if (player != null)
        {
            player.sendMessage(ChatColor.AQUA + "[Waypoint] " + player.getName().toString() + ", your home for world " + player.getWorld().getName().toString() + " has been set to the bed you just entered.");
        }
    }
}
