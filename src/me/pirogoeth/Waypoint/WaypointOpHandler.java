package me.pirogoeth.Waypoint;

import org.bukkit.entity.Player;
import java.util.logging.Logger;

public class WaypointOpHandler {
    public static Waypoint plugin;
    public Logger log = Logger.getLogger("Minecraft");
    public WaypointOpHandler (Waypoint instance)
    {
    	plugin = instance;
    }
    public boolean hasOp (Player p)
    {
    	return p.isOp();
    }
    // this is an override for the normal Permissions system 'has'
    public boolean has(Player p, String node)
    {
    	return p.isOp();
    }
}
