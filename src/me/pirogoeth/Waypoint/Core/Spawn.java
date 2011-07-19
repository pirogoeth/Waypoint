package me.pirogoeth.Waypoint.Core;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.Location;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Logger;
import me.pirogoeth.Waypoint.Waypoint;

public class Spawn {
    public static Waypoint plugin;
    public static Configuration config;
    Logger log = Logger.getLogger("Minecraft");
    public Spawn (Waypoint instance)
    {
        plugin = instance;
        config = plugin.config;
    }
    public static String SNodeChomp (World world, String subnode)
    {
        String a = "spawn." + world.getName() + "." + subnode;
        return a;
    }
    public void ConfigWriteSpawnLocations ()
    {
        List<World> w = plugin.getServer().getWorlds();
        Iterator<World> wIter = w.iterator();
        while (wIter.hasNext())
        {
            World tw = (World)wIter.next();
            Location l = tw.getSpawnLocation();
            config.setProperty(SNodeChomp(tw, "coord.X"), l.getX());
            config.setProperty(SNodeChomp(tw, "coord.Y"), l.getY());
            config.setProperty(SNodeChomp(tw, "coord.Z"), l.getZ());
            log.info("[Waypoint] Wrote spawn info for world: " + tw.getName().toString());
            config.save();
        };
        log.info("[Waypoint] Wrote all world spawn locations.");
        return;
    }
    public void SetSpawnFromCoord (World world, double x, double y, double z)
    {
        world.setSpawnLocation((int)x, (int)y, (int)z);
        SaveWorldSpawnLocation(world);
        world.save();
        return;
    }
    public void SetSpawnFromPlayer (World world, Player player)
    {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        world.setSpawnLocation((int)x, (int)y, (int)z);
        SaveWorldSpawnLocation(world);
        world.save();
        return;
    }
    public void SaveWorldSpawnLocation (World world)
    {
        Location l = (Location) world.getSpawnLocation();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        config.setProperty(SNodeChomp(world, "coord.X"), x);
        config.setProperty(SNodeChomp(world, "coord.Y"), y);
        config.setProperty(SNodeChomp(world, "coord.Z"), z);
        config.save();
        log.info("[Waypoint] Forced save of spawn location for world: " + world.getName().toString());
        return;
    }
    public void LoadWorldSpawnLocation (World world)
    {
        double x = (Double) config.getProperty(SNodeChomp(world, "coord.X"));
        double y = (Double) config.getProperty(SNodeChomp(world, "coord.Y"));
        double z = (Double) config.getProperty(SNodeChomp(world, "coord.Z"));
        world.setSpawnLocation((int)x, (int)y, (int)z);
        world.save();
        log.info("[Waypoint] Forced reload of spawn location for " + world.getName().toString() + " to location " + x + "," + y + "," + z);
        return;
    }
    public void SendPlayerToSpawn (World world, Player player)
    {
        Location l = world.getSpawnLocation();
        player.teleport(l);
    }
}