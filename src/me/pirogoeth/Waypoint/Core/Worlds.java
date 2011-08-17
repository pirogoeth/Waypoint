package me.pirogoeth.Waypoint.Core;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.util.logging.Logger;

import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;

public class Worlds {
    public Waypoint plugin;
    public Logger log = Logger.getLogger("Minecraft");
    protected Permission permission;
    public Config config;
    public Worlds (Waypoint instance)
    {
        plugin = instance;
        config = plugin.config;
        permission = plugin.permissions;
    }
    public void LoadWorlds ()
    {
        Configuration world = config.getWorld();
        Map<String, ConfigurationNode> worlds = world.getNodes("world");
        List<World> worldlist = plugin.getServer().getWorlds();
        List<String> worldnames = new ArrayList<String>();
        World wx;
        Iterator worldlist_i = worldlist.iterator();
        while (worldlist_i.hasNext())
        {
            wx = (World) worldlist_i.next();
            worldnames.add((String) wx.getName().toString());
        }
        String worldname;
        String env;
        ConfigurationNode e;
        for (Map.Entry<String, ConfigurationNode> entry : worlds.entrySet())
        {
            worldname = entry.getKey();
            e = entry.getValue();
            env = e.getString("env");
            if (!worldnames.contains(worldname))
            {
                this.Import(worldname, env);
            }
            else
            {
                log.info(String.format("[Waypoint] Loaded world: { %s [ENV: %s] }", worldname, env));
            }
        }
        return;
    }
    public World Import (String worldname, String env)
    {
        Environment environment = Environment.valueOf(env);
        if (new File(worldname).exists() && environment != null)
        {
            World wx = plugin.getServer().createWorld(worldname, environment);
            log.info(String.format("[Waypoint] Loaded world: { %s [ENV:%s] }", worldname, env.toUpperCase()));
            return wx;
        }
        else if (environment == null)
        {
            return null;
        }
        else
        {
            return null;
        }
    }
    public World Create (String worldname, String env)
    {
        Environment environment = Environment.valueOf(env);
        if (!(new File(worldname).exists()) && environment != null)
        {
            World wx = plugin.getServer().createWorld(worldname, environment);
            log.info(String.format("[Waypoint] Created world: { %s [ENV:%s] }", worldname, env.toUpperCase()));
            return wx;
        }
        else if (environment == null)
        {
            return null;
        }
        else
        {
            return null;
        }
    }
    public World Create (String worldname, String env, String seed_s)
    {
        long seed = new Long(seed_s);
        Environment environment = Environment.valueOf(env);
        if (!(new File(worldname).exists()) && environment != null)
        {
            World wx = plugin.getServer().createWorld(worldname, environment, seed);
            log.info(String.format("[Waypoint] Created world: { %s [ENV:%s] [SEED:%s] }", worldname, env.toUpperCase(), seed_s));
            return wx;
        }
        else if (environment == null)
        {
            return null;
        }
        else
        {
            return null;
        }
    }
}