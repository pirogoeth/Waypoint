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
    public static String BaseNodeChomp(Player p, String node)
    {
        String a = "users." + p.getName().toString() + "." + node;
        return a;
    }
    public static String UserNodeChomp(Player p, String targetname, String sub)
    {
        String a = "users." + p.getName().toString() + "." + targetname + "." + sub;
        return a;
    }
    public static String InviteNodeChomp(Player p, String node)
    {
        String a = "invites." + p.getName().toString() + "." + node;
        return a;
    }
    public static boolean CheckPointExists(Player p, String point)
    {
        String a = "users." + p.getName().toString() + "." + point;
        if (config.getProperty(a + ".coord") == null)
        {
            return false;
        } else if (config.getProperty(a + ".coord") != null)
        {
            return true;
        } else
        {
            return false;
        }
    }
    public static boolean CommandParser(Player player, String command, String[] args)
    {
	if (command.equalsIgnoreCase("wp") || command.equalsIgnoreCase("waypoint"))
    	{
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
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.add")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command."); 
	            return true;
	        }
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
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.delete")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command."); 
	            return true;
	        }
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
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.teleport")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
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
            else if (subc.equalsIgnoreCase("invite"))
            {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.invite")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        String point;
                try {
                    point = (String) args[2];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    player.sendMessage(ChatColor.RED + "Usage: /wp invite <playername> <point>");
                    return true;
                }
                Player p = plugin.getServer().getPlayer((String)arg);
                if (CheckPointExists(player, point) == false)
                {
                    player.sendMessage(ChatColor.RED + "You do not own a point named '" + point + "'.");
                    return true;
                }
                ConfigurationNode node = config.getNode(BaseNodeChomp(player, point));
                config.setProperty(InviteNodeChomp(p, point), node);
                p.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + player.getName().toString() + " has invited you to use their waypoint '" + point + "'.");
                p.sendMessage(ChatColor.GREEN + "Type /wp accept " + player.getName().toString() + " to accept their invite.");
                p.sendMessage(ChatColor.GREEN + "Or type /wp decline " + player.getName().toString() + " to decline the invite.");
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Sent invite to " + p.getName().toString() + ".");
                config.save();
                return true;
	    }
	    else if (subc.equalsIgnoreCase("accept"))
	    {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.invite.accept")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        if (config.getProperty(InviteNodeChomp(player, arg)) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You have no point invite by that name.");
	            return true;
	        }
	        ConfigurationNode n = config.getNode(InviteNodeChomp(player, arg));
	        config.removeProperty(InviteNodeChomp(player, arg));
	        config.setProperty(BaseNodeChomp(player, arg), n);
	        player.sendMessage(ChatColor.AQUA + "[Waypoint] The point '" + arg + "' is now available in your collection.");
	        config.save();
	    }
	    else if (subc.equalsIgnoreCase("decline"))
	    {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.invite.accept")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        if (config.getProperty(InviteNodeChomp(player, arg)) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You have no point invite by that name.");
	            return true;
	        }
	        config.removeProperty(InviteNodeChomp(player, arg));
	        player.sendMessage(ChatColor.AQUA + "[Waypoint] The point invitation '" + arg + "' has been declined.");
	        config.save();
	    }
            else if (subc.equalsIgnoreCase("list"))
            {
	        if (!plugin.permissionHandler.has(player, "waypoint.basic.list")) {
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
	        }
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
            else if (subc.equalsIgnoreCase("test"))
            {
                // testing node crap
            	double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();
                config.setProperty(UserNodeChomp(player, "testnode", "coord.X"), x);
                config.setProperty(UserNodeChomp(player, "testnode", "coord.X"), y);
                config.setProperty(UserNodeChomp(player, "testnode", "coord.X"), z);
                ConfigurationNode n = config.getNode(UserNodeChomp(player, "testnode", "coord"));
                config.removeProperty(UserNodeChomp(player, "testnode", "coord"));
                config.setProperty(UserNodeChomp(player, "testnode", "coord"), n);
                config.removeProperty(BaseNodeChomp(player, "testnode"));
                log.info("Configuration node nesting test successful.");
                return true;
            }
            else if (subc.equalsIgnoreCase("help"))
            {
            	player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            	player.sendMessage(ChatColor.GREEN + "/wp:");
            	player.sendMessage(ChatColor.RED + "   add - add a waypoint to your list.");
            	player.sendMessage(ChatColor.RED + "   del - remove a waypoint from your list.");
            	player.sendMessage(ChatColor.RED + "   tp - teleport to a waypoint in your list.");
            	player.sendMessage(ChatColor.RED + "   list - list the waypoints in your list.");
            	player.sendMessage(ChatColor.RED + "   help - this message.");
            	player.sendMessage(ChatColor.GREEN + "/home:");
            	player.sendMessage(ChatColor.RED + "   with args, will teleport you to your home.");
            	player.sendMessage(ChatColor.RED + "   set - sets your home to the location you are currently standing at.");
            	player.sendMessage(ChatColor.GREEN + "ways to set your home:");
            	if (config.getProperty("set_home_at_bed") == "true") { player.sendMessage(ChatColor.RED + "getting into a bed will set your home to the position of that bed."); }
            	player.sendMessage(ChatColor.RED + "typing /home without having a home already set.");
            	player.sendMessage(ChatColor.RED + "typing /home set.");
            	return true;
            }
            else
            {
            	player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            	return true;
            }
    	}
        else if (command.equalsIgnoreCase("home"))
        {
            if (!plugin.permissionHandler.has(player, "waypoint.home")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
  	    String subc = "";
    	    try {
    	        subc = args[0];
    	    }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                if (config.getProperty(UserNodeChomp(player, "home", "world")) != null)
                {
                    String sw = (String)config.getProperty(UserNodeChomp(player, "home", "world"));
                    World w = plugin.getServer().getWorld(sw);
                    double x = (Double)config.getProperty(UserNodeChomp(player, "home", "coord.X"));
                    double y = (Double)config.getProperty(UserNodeChomp(player, "home", "coord.Y"));
                    double z = (Double)config.getProperty(UserNodeChomp(player, "home", "coord.Z"));
                    Location l = new Location(w, x, y, z);
                    player.setCompassTarget(l);
                    player.teleport(l);
                    player.sendMessage(ChatColor.GREEN + "Welcome home, " + player.getName().toString() + ".");
                    return true;
                }
                else if (config.getProperty(UserNodeChomp(player, "home", "world")) == null)
                {
                    config.setProperty(UserNodeChomp(player, "home", "world"), player.getWorld().getName().toString());
                    config.setProperty(UserNodeChomp(player, "home", "coord.X"), player.getLocation().getX());
                    config.setProperty(UserNodeChomp(player, "home", "coord.Y"), player.getLocation().getY());
                    config.setProperty(UserNodeChomp(player, "home", "coord.Z"), player.getLocation().getZ());
                    player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home point was not set, so it was automatically set to the point you are currently at now.");
                    config.save();
                    return true;
                }
            };
            subc = subc.toLowerCase().toString();
            String arg = null;
            try {
                arg = args[1];
            }
            catch (Exception e) {
                arg = null;
            };
            if (subc.equalsIgnoreCase("set"))
            {
                if (!plugin.permissionHandler.has(player, "waypoint.home.set")) { 
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                config.setProperty(UserNodeChomp(player, "home", "world"), player.getWorld().getName().toString());
                config.setProperty(UserNodeChomp(player, "home", "coord.X"), player.getLocation().getX());
                config.setProperty(UserNodeChomp(player, "home", "coord.Y"), player.getLocation().getY());
                config.setProperty(UserNodeChomp(player, "home", "coord.Z"), player.getLocation().getZ());
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home location has been set.");
                config.save();
            }
            else if (subc.equalsIgnoreCase("help"))
            {
            	player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            	player.sendMessage(ChatColor.GREEN + "/wp:");
            	player.sendMessage(ChatColor.RED + "   add - add a waypoint to your list.");
            	player.sendMessage(ChatColor.RED + "   del - remove a waypoint from your list.");
            	player.sendMessage(ChatColor.RED + "   tp - teleport to a waypoint in your list.");
            	player.sendMessage(ChatColor.RED + "   list - list the waypoints in your list.");
            	player.sendMessage(ChatColor.RED + "   help - this message.");
            	player.sendMessage(ChatColor.GREEN + "/home:");
            	player.sendMessage(ChatColor.RED + "   with args, will teleport you to your home.");
            	player.sendMessage(ChatColor.RED + "   set - sets your home to the location you are currently standing at.");
            	player.sendMessage(ChatColor.GREEN + "ways to set your home:");
            	if (config.getProperty("set_home_at_bed") == "true") { player.sendMessage(ChatColor.RED + "getting into a bed will set your home to the position of that bed."); };
            	player.sendMessage(ChatColor.RED + "typing /home without having a home already set.");
            	player.sendMessage(ChatColor.RED + "typing /home set.");
            	return true;
            };
        };
        return false;
    };
};