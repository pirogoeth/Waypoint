package me.pirogoeth.Waypoint.Util;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import java.util.logging.Logger;
import me.pirogoeth.Waypoint.Waypoint;

public class PlayerUtil {
    public static Logger log = Logger.getLogger("Minecraft");
    public static Player getPlayerFromSender (CommandSender sender)
    {
        Player p = (Player) sender;
        return p;
    }
    public static String getPlayerName (CommandSender sender)
    {
    	Player p = (Player) sender;
    	String n = p.getDisplayName();
    	return n;
    }
    public static Location getPlayerLocation (CommandSender sender)
    {
    	Player p = (Player) sender;
    	Location l = p.getLocation();
    	return l;
    }
}
