package me.pirogoeth.Waypoint;

import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class WaypointCommandParser {
    public static Waypoint plugin;
    public static Configuration config;
    public static Logger log = Logger.getLogger("Minecraft");
    public WaypointCommandParser (Waypoint instance)
    {
    	plugin = instance;
    	config = plugin.config;
    }
    public static String UserNodeChomp(Player p, String targetname, String sub)
    {
    	String a = "users." + p.getName().toString() + "." + targetname + "." + sub;
    	return a;
    }
    public static boolean CommandParser(Player player, String command, String[] args)
    {
	if (command.equalsIgnoreCase("wp") || command.equalsIgnoreCase("waypoint"))
    	{
	        if (!(Waypoint).permissionHandler.has(player, "waypoint.basic")) { player.sendMessage(ChatColor.ORANGE + "You do not have the permissions to use this command."); return true; };
	        String subc = "";
    		try {
    	        subc = args[0];
    		}
    		catch (java.lang.ArrayIndexOutOfBoundsException e) {
    			player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
    			return true;
    		};
    		subc = subc.toLowerCase().toString();
    		String arg = null;
    		try {
    			arg = args[1];
    		}
    		catch (Exception e) {
    			arg = null;
    		};
            if (subc.equalsIgnoreCase("add"))
            {
            	if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!(Waypoint).permissionHandler.has(player, "waypoint.basic.add")) { player.sendMessage(ChatColor.ORANGE + "You do not have the permissions to use this command."); return true; };
            	// code
            	if (config.getProperty(UserNodeChomp(player, arg, "world")) != null)
            	{
            		player.sendMessage("[Waypoint] Point '" + arg + "' already exists!");
            		return true;
            	}
            	config.setProperty(UserNodeChomp(player, arg, "coord.X"), player.getLocation().getX());
            	config.setProperty(UserNodeChomp(player, arg, "coord.Y"), player.getLocation().getY());
            	config.setProperty(UserNodeChomp(player, arg, "coord.Z"), player.getLocation().getZ());
            	// config.setProperty(UserNodeChomp(player, arg, "coord.pitch"), player.getLocation().getPitch());
            	// config.setProperty(UserNodeChomp(player, arg, "coord.yaw"), player.getLocation().getYaw());
            	config.setProperty(UserNodeChomp(player, arg, "world"), player.getLocation().getWorld().getName().toString());
            	config.save();
            	player.sendMessage(ChatColor.GREEN + "[Waypoint] Set point '" + arg + "' in world '" + player.getLocation().getWorld().getName().toString() + "'.");
            	return true;
            }
            else if (subc.equalsIgnoreCase("del") || subc.equalsIgnoreCase("delete") || subc.equalsIgnoreCase("remove"))
            {
            	if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!(Waypoint).permissionHandler.has(player, "waypoint.basic.delete")) { player.sendMessage(ChatColor.ORANGE + "You do not have the permissions to use this command."); return true; };
            	// code
            	if (config.getProperty(UserNodeChomp(player, arg, "world")) == null)
            	{
            		player.sendMessage(ChatColor.RED + "[Waypoint] Point '" + arg + "' does not exist!");
            		return true;
            	}
            	config.removeProperty("users." + player.getName().toString() + "." + arg);
            	player.sendMessage("[Waypoint] Point '" + arg + "' has been deleted.");
            	config.save();
            	return true;
            }
            else if (subc.equalsIgnoreCase("tp") || subc.equalsIgnoreCase("teleport"))
            {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!(Waypoint).permissionHandler.has(player, "waypoint.basic.teleport")) { player.sendMessage(ChatColor.ORANGE + "You do not have the permissions to use this command."); return true; };
            	// code
            	if (config.getProperty(UserNodeChomp(player, arg, "world")) == null)
            	{
            		player.sendMessage(ChatColor.RED + "[Waypoint] Point '" + arg + "' does not exist!");
            		return true;
            	}
            	World w = plugin.getServer().getWorld(config.getProperty(UserNodeChomp(player, arg, "world")).toString());
            	double x = (Double) config.getProperty(UserNodeChomp(player, arg, "coord.X"));
                double y = (Double) config.getProperty(UserNodeChomp(player, arg, "coord.Y"));
    		double z = (Double) config.getProperty(UserNodeChomp(player, arg, "coord.Z"));
    		// final float pitch = (Float) config.getProperty(UserNodeChomp(player, arg, "coord.pitch"));
    		// final float yaw = (Float) config.getProperty(UserNodeChomp(player, arg, "coord.yaw"));
    		Location l = new Location(w, x, y, z);
                boolean su = player.teleport(l);
                if (su == true)
                {
                	player.sendMessage("[Waypoint] Successfully teleported to '" + arg + "'.");
                	return true;
                } else if (su == false)
                {
                	player.sendMessage("[Waypoint] Error while teleporting to '" + arg + "'.");
                	return true;
                }
                return true;
            }
            else if (subc.equalsIgnoreCase("list"))
            {
	        if (!(Waypoint).permissionHandler.has(player, "waypoint.basic.list")) { player.sendMessage(ChatColor.ORANGE + "You do not have the permissions to use this command."); return true; };
            	player.sendMessage(ChatColor.YELLOW + "====[Waypoint] Point list:====");
            	Map<String, ConfigurationNode> a = config.getNodes("users." + player.getName());
            	if (a.size() == 0)
            	{
            	    player.sendMessage(ChatColor.AQUA + " - No points have beeb created.");
            	    return true;
            	}
            	for (Map.Entry<String, ConfigurationNode> entry : a.entrySet())
            	{
                    player.sendMessage(ChatColor.GREEN + " - " + entry.getKey());
            	}
            	return true;
    		}
            else if (subc.equalsIgnoreCase("help"))
            {
            	// code
            	return true;
            }
            else
            {
            	player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            	return true;
            }
    	}
		return false;
    }
};