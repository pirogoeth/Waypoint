package me.pirogoeth.Waypoint.Core;

import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import me.pirogoeth.Waypoint.Waypoint;

public class Parser {
    public static Waypoint plugin;
    public static Configuration config;
    public static Logger log = Logger.getLogger("Minecraft");
    public Parser (Waypoint instance)
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
    public static String HomeNodeChomp(Player p, World world, String sub)
    {
        String a = "home." + p.getName().toString() + "." + world.getName().toString() + "." + sub;
        return a;
    }
    public static String InviteNodeChomp(Player p, String node)
    {
        String a = "invites." + p.getName().toString() + "." + node;
        return a;
    }
    public static void TransferNode(String path, ConfigurationNode node)
    {
        Map<String, Object> a = node.getAll();
        for (Map.Entry<String, Object> entry : a.entrySet())
        {
            config.setProperty(path + "." + entry.getKey(), entry.getValue());
            // debug
            // log.info("Key: " + entry.getKey() + " Value: " + (String)entry.getValue().toString());
        }
        return;
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
    		catch (java.lang.ArrayIndexOutOfBoundsException e) {
    			arg = null;
    		};
            if (subc.equalsIgnoreCase("add"))
            {
            	if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!plugin.permissions.has(player, "waypoint.basic.add")) {
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
	        if (!plugin.permissions.has(player, "waypoint.basic.delete")) { 
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
	        if (!plugin.permissions.has(player, "waypoint.basic.teleport")) { 
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
	        if (!plugin.permissions.has(player, "waypoint.basic.invite")) { 
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
                if (p == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] Player " + arg + " is offline. Please resend the invitation when he/she is online.");
                    return true;
                }
                if (CheckPointExists(player, point) == false)
                {
                    player.sendMessage(ChatColor.RED + "You do not own a point named '" + point + "'.");
                    return true;
                }
                ConfigurationNode node = config.getNode(BaseNodeChomp(player, point));
                TransferNode(InviteNodeChomp(p, point), node);
                p.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + player.getName().toString() + " has invited you to use their waypoint '" + point + "'.");
                p.sendMessage(ChatColor.GREEN + "Type /wp accept " + point + " to accept their invite.");
                p.sendMessage(ChatColor.GREEN + "Or type /wp decline " + point + " to decline the invite.");
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Sent invite to " + p.getName().toString() + ".");
                config.save();
                return true;
	    }
	    else if (subc.equalsIgnoreCase("accept"))
	    {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!plugin.permissions.has(player, "waypoint.basic.invite.accept")) {
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
	        TransferNode(BaseNodeChomp(player, arg), n);
                player.sendMessage(ChatColor.AQUA + "[Waypoint] The point '" + arg + "' is now available in your collection.");
	        config.save();
	    }
	    else if (subc.equalsIgnoreCase("decline"))
	    {
            	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!plugin.permissions.has(player, "waypoint.basic.invite.decline")) { 
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
	        if (!plugin.permissions.has(player, "waypoint.basic.list")) {
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
	        }
	        if (config.getProperty("users." + player.getName().toString()) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You do not have any points to list.");
	            return true;
	        }
            	player.sendMessage(ChatColor.YELLOW + "====[Waypoint] Point list:====");
            	Map<String, ConfigurationNode> a = config.getNodes("users." + player.getName());
            	if (a.size() == 0)
            	{
            	    player.sendMessage(ChatColor.AQUA + " - No points have been created.");
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
	        if (!plugin.permissions.has(player, "waypoint.debug.config_node_test")) {
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
	        }
                // testing node crap
            	double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();
                config.setProperty(UserNodeChomp(player, "testnode", "coord.X"), x);
                config.setProperty(UserNodeChomp(player, "testnode", "coord.Y"), y);
                config.setProperty(UserNodeChomp(player, "testnode", "coord.Z"), z);
                config.save();
                ConfigurationNode n = config.getNode(UserNodeChomp(player, "testnode", "coord"));
                TransferNode(UserNodeChomp(player, "testnode", "coord"), n);
                config.save();
                config.removeProperty(BaseNodeChomp(player, "testnode"));
                config.save();
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
            	return true;
            }
            else
            {
            	player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            	return true;
            }
    	}
        else if (command.equalsIgnoreCase("home") || command.equalsIgnoreCase("wphome"))
        {
            if (!plugin.permissions.has(player, "waypoint.home")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
  	    String subc = "";
    	    try {
    	        subc = args[0];
    	    }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                if (config.getProperty(HomeNodeChomp(player, player.getWorld(), "coord.X")) != null)
                {
                    World w = player.getWorld();
                    double x = (Double)config.getProperty(HomeNodeChomp(player, w, "coord.X"));
                    double y = (Double)config.getProperty(HomeNodeChomp(player, w, "coord.Y"));
                    double z = (Double)config.getProperty(HomeNodeChomp(player, w, "coord.Z"));
                    Location l = new Location(w, x, y, z);
                    player.setCompassTarget(l);
                    player.teleport(l);
                    player.sendMessage(ChatColor.GREEN + "Welcome home, " + player.getName().toString() + ".");
                    return true;
                }
                else if (config.getProperty(HomeNodeChomp(player, player.getWorld(), "coord.X")) == null)
                {
                    World w = player.getWorld();
                    config.setProperty(HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
                    config.setProperty(HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
                    config.setProperty(HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
                    player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home point for world " + w.getName().toString() + " was not set, so it was automatically set to the point you are currently at now.");
                    config.save();
                    return true;
                }
            };
            subc = subc.toLowerCase().toString();
            if (subc.equalsIgnoreCase("set"))
            {
                World w = player.getWorld();
                if (!plugin.permissions.has(player, "waypoint.home.set")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                config.setProperty(HomeNodeChomp(player, w, "world"), player.getWorld().getName().toString());
                config.setProperty(HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
                config.setProperty(HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
                config.setProperty(HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home location has been set.");
                config.save();
                return true;
            }
            else if (subc.equalsIgnoreCase("help"))
            {
            	player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            	player.sendMessage(ChatColor.GREEN + "/home:");
            	player.sendMessage(ChatColor.RED + "   with args, will teleport you to your home.");
            	player.sendMessage(ChatColor.RED + "   set - sets your home to the location you are currently standing at.");
            	player.sendMessage(ChatColor.GREEN + "ways to set your home:");
            	if (config.getProperty("set_home_at_bed") == "true") { player.sendMessage(ChatColor.RED + "getting into a bed will set your home to the position of that bed."); };
            	player.sendMessage(ChatColor.RED + "typing /home without having a home already set.");
            	player.sendMessage(ChatColor.RED + "typing /home set.");
            	return true;
            };
        }
        else if (command.equalsIgnoreCase("setspawn") || command.equalsIgnoreCase("wpsetspawn"))
        {
            if (!plugin.permissions.has(player, "waypoint.admin.spawn.set")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /setspawn");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            World w = player.getWorld();
            plugin.spawnManager.SendPlayerToSpawn(w, player);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Spawn for world " + player.getWorld().getName() + " has been set.");
            return true;
        }
        else if (command.equalsIgnoreCase("spawn") || command.equalsIgnoreCase("wpspawn"))
        {
            if (!plugin.permissions.has(player, "waypoint.spawn")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
  	    String subc = "";
    	    try {
    	        subc = args[0];
    	    }
    	    catch (ArrayIndexOutOfBoundsException e) {
    	        World w = player.getWorld();
                plugin.spawnManager.SendPlayerToSpawn(w, player);
    	        return true;
    	    }
    	    World w = plugin.getServer().getWorld((String)subc);
    	    if (w == null)
    	    {
    	        player.sendMessage("[Waypoint] World '" + subc + "' does not exist.");
    	        return true;
    	    }
            plugin.spawnManager.SendPlayerToSpawn(w, player);
            return true;
    	}
    	else if (command.equalsIgnoreCase("spawnadmin") || command.equalsIgnoreCase("wpspawnadmin"))
        {
            if (!plugin.permissions.has(player, "waypoint.admin.spawn")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /spawnadmin <save|load;set> <world>");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            String arg = null;
            try {
            arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            if (subc.equalsIgnoreCase("save"))
            {
                if (!plugin.permissions.has(player, "waypoint.admin.spawn.save")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                if (arg == null)
                {
                    World w = plugin.getServer().getWorld(player.getWorld().getName().toString());
                    plugin.spawnManager.SaveWorldSpawnLocation((World)w);
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
                    return true;
                }
                World w = plugin.getServer().getWorld(arg);
                if (w == null)
                { player.sendMessage(ChatColor.RED + "[Waypoint] Invalid world: " + arg); return true; }
                plugin.spawnManager.SaveWorldSpawnLocation(w);
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
                return true;
            }
            else if (subc.equalsIgnoreCase("load"))
            {
                if (!plugin.permissions.has(player, "waypoint.admin.spawn.load")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                if (arg == null)
                {
                    plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
                    return true;
                }
                plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (!plugin.permissions.has(player, "waypoint.admin.spawn.set")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                plugin.spawnManager.SetSpawnFromPlayer(player.getWorld(), player);
                player.getWorld().save();
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Set spawn point for world: " + player.getWorld().getName());
                return true;
            }
        }
        else if (command.equalsIgnoreCase("tp") || command.equalsIgnoreCase("wptp"))
        {
            if (!plugin.permissions.has(player, "waypoint.teleport.teleport")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /tp <target> [user]");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            String arg = null;
            try {
            arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            if (arg == null)
            {
                Player target = plugin.getServer().getPlayer((String)subc);
                if (target == null) 
                {
                    player.sendMessage("[Waypoint] Player " + subc + " is not logged in.");
                    return true;
                }
                Location l = target.getLocation();
                player.teleport(l);
                return true;
            }
            else if (arg != null)
            {
                Player p = plugin.getServer().getPlayer((String)subc);
                Player target = plugin.getServer().getPlayer((String)arg);
                if (p == null)
                {
                    player.sendMessage("[Waypoint] Player " + subc + " is not online.");
                    return true;
                }
                else if (target == null)
                {
                    player.sendMessage("[Waypoint] Player " + arg + " is not online.");
                    return true;
                }
                Location l = target.getLocation();
                p.teleport(l);
                p.sendMessage("[Waypoint] You have been teleported by " + player.getName().toString() + ".");
                return true;
            }
        }
        else if (command.equalsIgnoreCase("tphere") || command.equalsIgnoreCase("wptphere"))
        {
            if (!plugin.permissions.has(player, "waypoint.teleport.here")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /tp <target> [user]");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            Player target = plugin.getServer().getPlayer((String)subc);
            if (target == null)
            {
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + subc + " is not online.");
                return true;
            }
            Location l = player.getLocation();
            target.teleport(l);
            target.sendMessage(ChatColor.GREEN + "[Waypoint] You Have been teleported to " + player.getName().toString() + ".");
            return true;
        }
        else if (command.equalsIgnoreCase("setwarp") || command.equalsIgnoreCase("wpsetwarp"))
        {
            if (!plugin.permissions.has(player, "waypoint.warp.create")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /setwarp <name>");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            plugin.warpManager.CreateWarp(player, subc);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + subc + " has been created.");
            return true;
        }
        else if (command.equalsIgnoreCase("warpadmin") || command.equalsIgnoreCase("wpwarpadmin"))
        {
            if (!plugin.permissions.has(player, "waypoint.admin.warp"))
            {
                player.sendMessage(ChatColor.BLUE + "You do not have the permission to use this command.");
                return true;
            }
            String subc = "";
            String arg = "";
            String k = "";
            String v = "";
    	    try {
                subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /warpadmin <del|set> [key] [value]");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            try {
                k = args[2];
                v = args[3];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        k = null;
    	        v = null;
            }
            if (subc.equalsIgnoreCase("del"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warpadmin del <warpname>");
                    return true;
                }
                if (config.getProperty(plugin.warpManager.WarpBase(arg)) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                    return true;
                }
                if (!plugin.permissions.has(player, "waypoint.admin.warp.delete"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.DeleteWarp(arg);
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Warp " + arg + " has been deleted.");
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (k == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (v == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                // XXX - remove this for:
                //   custom properties?
                //
                // else if (!k.equalsIgnoreCase("owner") || !k.equalsIgnoreCase("permission"))
                // {
                //    player.sendMessage(ChatColor.GREEN + "Settable warp options:");
                //    player.sendMessage(ChatColor.BLUE + " - owner -- the owner of the warp.");
                //    player.sendMessage(ChatColor.BLUE + " - permission -- who can use this warp.");
                //    return true;
                // }
                String owner = (String) config.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                if (!owner.equals(player.getName().toString()))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission modify this warp.");
                    return true;
                }
                k = k.toLowerCase().toString();
                v = v.toLowerCase().toString();
                if (config.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                    return true;
                }
                boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
                if (!f)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                    return true;
                }
                else if (f)
                {
                    player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                    return true;
                }
                return true;
            }
        }
        else if (command.equalsIgnoreCase("warp") || command.equalsIgnoreCase("wpwarp"))
        {
            if (!plugin.permissions.has(player, "waypoint.warp")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
	    String arg = "";
	    String k = "";
	    String v = "";
    	    try {
                subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /warp <add|del|set|list|<warp name>> [key] [value]");
                return true;
    	    };
            String origc = subc;
            subc = subc.toLowerCase().toString();
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            try {
                k = args[2];
                v = args[3];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        k = null;
    	        v = null;
            }
            if (subc.equalsIgnoreCase("add"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp add <warpname>");
                    return true;
                }
                if (!plugin.permissions.has(player, "waypoint.warp.create"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.CreateWarp(player, arg);
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been created.");
                return true;
            }
            else if (subc.equalsIgnoreCase("del"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp del <warpname>");
                    return true;
                }
                if (config.getProperty(plugin.warpManager.WarpBase(arg)) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                    return true;
                }
                String owner = (String)config.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                if (!plugin.permissions.has(player, "waypoint.warp.delete") || !owner.equals(player.getName().toString()))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.DeleteWarp(arg);
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been deleted.");
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (k == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (v == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                // XXX - remove this for:
                //   custom properties?
                //
                // else if (!k.equalsIgnoreCase("owner") || !k.equalsIgnoreCase("permission"))
                // {
                //    player.sendMessage(ChatColor.GREEN + "Settable warp options:");
                //    player.sendMessage(ChatColor.BLUE + " - owner -- the owner of the warp.");
                //    player.sendMessage(ChatColor.BLUE + " - permission -- who can use this warp.");
                //    return true;
                // }
                String owner = (String) config.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                if (!owner.equals(player.getName().toString()))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission modify this warp.");
                    return true;
                }
                // WOW I'M SUCH A FREAKING IDIOT. IT TOOK 5 BUILDS 
                // TO FIGURE THIS SHIT OUT.
                //
                // k = k.toLowerCase().toString();
                // v = v.toLowerCase().toString();
                if (config.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                    return true;
                }
                boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
                if (!f)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                    return true;
                }
                else if (f)
                {
                    player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                    return true;
                }
                return true;
            }
            else if (subc.equalsIgnoreCase("list"))
            {
                if (!plugin.permissions.has(player, "waypoint.warp.list"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have the permission to access this command.");
                    return true;
                }
                Map<String, ConfigurationNode> a;
                a = config.getNodes("warps");
                if (a == null || a.size() == 0)
                {
                   player.sendMessage(ChatColor.YELLOW + "[Waypoint] There are currently no warps to be displayed.");
                   return true;
                }
                player.sendMessage(ChatColor.GREEN + "====[Waypoint] Warps available to you:====");
                for (Map.Entry<String, ConfigurationNode> entry : a.entrySet())
                {
                    ConfigurationNode node = entry.getValue();
                    String warppermission = (String) node.getProperty("permission");
                    if (warppermission == null)
                    {
                       player.sendMessage(ChatColor.RED + "[Waypoint] Internal error during iteration! Please report this to the author.");
                       return true;
                    }
                    if (plugin.warpManager.checkperms(player, warppermission))
                    {
                        player.sendMessage(ChatColor.AQUA + " - " + entry.getKey());
                    }
                }
                return true;
            }
            else if (config.getProperty(plugin.warpManager.WarpBase(origc)) != null)
            {
                plugin.warpManager.PlayerToWarp(player, (String)subc);
                return true;
            }
            else if (config.getProperty(plugin.warpManager.WarpBase(origc)) == null)
            {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "[Waypoint] Warp " + subc + " does not exist.");
                return true;
            }
        }
        return false;
    };
};