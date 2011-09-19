package me.pirogoeth.Waypoint.Core;

import java.lang.Float;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Waypoint;

public class Warps {
    public static Waypoint plugin;
    public static Config c;
    public static Configuration config;
    public static Logger log = Logger.getLogger("Minecraft");
    public List<String> groups;
    public Warps (Waypoint instance)
    {
        plugin = instance;
        c = plugin.config;
        config = c.getWarp();
    }
    public void LoadGroups ()
    {
        groups = c.getMain().getStringList("warp.permissions", null);
        Iterator<String> i = groups.iterator();
        StringBuffer s = new StringBuffer(i.next());
        while (i.hasNext()) { s.append(", ").append(i.next()); }
        log.info(String.format("[Waypoint] Warps: loaded permission groups: %s", s.toString()));
        return;
    }
    public String WarpNode (String warpname, String subnode)
    {
        String a = "warps." + warpname + "." + subnode;
        return a;
    }
    public String WarpBase (String warpname)
    {
        String a = "warps." + warpname;
        return a;
    }
    public boolean checkperms (Player p, String pnode)
    {
        String permission = String.format("waypoint.warp.access.%s", pnode);
        return plugin.permissions.has(p, permission);
    }
    public void CreateWarp (Player p, String warpname)
    {
        Location l = p.getLocation();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
    	double yaw = (double)l.getYaw();
		double pitch = (double)l.getPitch();
        String worldname = p.getWorld().getName().toString();
        String owner = p.getName().toString();
        config.setProperty(WarpNode(warpname, "coord.X"), x);
        config.setProperty(WarpNode(warpname, "coord.Y"), y);
        config.setProperty(WarpNode(warpname, "coord.Z"), z);
		config.setProperty(WarpNode(warpname, "coord.yaw"), yaw);
		config.setProperty(WarpNode(warpname, "coord.pitch"), pitch);
        config.setProperty(WarpNode(warpname, "world"), worldname);
        config.setProperty(WarpNode(warpname, "owner"), owner);
        String permission = (String)groups.get(0);
        config.setProperty(WarpNode(warpname, "permission"), (String)permission);
        config.save();
        return;
    }
    public boolean DeleteWarp (String warpname)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            return false;
        }
        config.removeProperty(WarpBase(warpname));
        config.save();
        return true;
    }
    public boolean CheckGroup (String group)
    {
        return groups.contains((String)group);
    }
    public boolean SetWarpProp (String warpname, String key, String value)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            return false;
        }
        if (key.equalsIgnoreCase("permission") && CheckGroup(value) || key.equalsIgnoreCase("owner"))
        {
            config.setProperty(WarpNode(warpname, key), value);
            config.save();
            return true;
        }
        else if (key.equalsIgnoreCase("permission") && !CheckGroup(value))
        {
            return false;
        }
    return false;
    }
    public boolean PlayerToWarp (Player p, String warpname)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            p.sendMessage(ChatColor.RED + "[Waypoint] Warp " + warpname + " does not exist.");
            return false;
        }
        String permission = (String) config.getProperty(WarpNode(warpname, "permission"));
        if (checkperms(p, permission) == false)
        {
            p.sendMessage(ChatColor.RED + "[Waypoint] You do not have permissions to access this warp.");
            return true;
        }
        String worldname = (String) config.getProperty(WarpNode(warpname, "world"));
        if (!p.getWorld().toString().equals(worldname) && ((String) plugin.config.getMain().getProperty("warp.traverse_world_only")).equals("true"))
        {
            p.sendMessage(ChatColor.RED + "[Waypoint] You are not allowed to warp between worlds.");
            return true;
        }
        double x = (Double) config.getProperty(WarpNode(warpname, "coord.X"));
        double y = (Double) config.getProperty(WarpNode(warpname, "coord.Y"));
        double z = (Double) config.getProperty(WarpNode(warpname, "coord.Z"));
		double pitch = 0;
		double yaw = 0;
		boolean basic = false;
		
		try
		{
			pitch = (Double) config.getProperty(WarpNode(warpname, "coord.pitch"));
			yaw = (Double) config.getProperty(WarpNode(warpname, "coord.yaw"));
		}
		catch (Exception e)
		{
			basic = true;
		}
		
        World w = plugin.getServer().getWorld(worldname);
		Location l;
		if (basic)
		{
			l = new Location(w, x, y, z);
		}
		else
		{
			l = new Location(w, x, y, z, (float) yaw, (float) pitch);
		}
        p.teleport(l);
        p.sendMessage(ChatColor.AQUA + "Welcome to " + warpname + ", " + p.getName().toString());
        return true;
    }
}