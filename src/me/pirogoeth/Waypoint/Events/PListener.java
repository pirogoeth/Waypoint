package me.pirogoeth.Waypoint.Events;

import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.ChatColor;
import java.util.logging.Logger;
import me.pirogoeth.Waypoint.Waypoint;

public class PListener extends PlayerListener {
    public static Waypoint plugin;
    Logger log = Logger.getLogger("Minecraft");
    public PListener (Waypoint instance) {
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
    public void onPlayerBedLeave(PlayerBedLeaveEvent event)
    {
        Player player = event.getPlayer();
        if (!plugin.permissions.has(player, "waypoint.home.set_on_bed_leave")) { return; };
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        World w = player.getLocation().getWorld();
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.X"), x);
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.Y"), y);
        plugin.config.setProperty(HomeNodeChomp(player, w, "coord.Z"), z);
        plugin.config.save();
        if (player != null)
        {
            player.sendMessage(ChatColor.AQUA + "[Waypoint] " + player.getName().toString() + ", your home for world " + player.getWorld().getName().toString() + " has been set to the bed you just got out of.");
        }
    }
}
