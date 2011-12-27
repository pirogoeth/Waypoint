package me.pirogoeth.Waypoint.Core;

// bukkit imports
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
// java imports
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.util.logging.Logger;
// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;

public class Worlds {
    public Waypoint plugin;
    public Logger log = Logger.getLogger("Minecraft");
    protected Permission permission;
    public Config config;
    public Worlds (Waypoint instance) {
        plugin = instance;
        config = plugin.config;
        permission = plugin.permissions;
    }
    public void LoadWorlds () {
        Configuration world = config.getWorld();
        Map<String, ConfigurationNode> worlds = world.getNodes("worlds");
        if (worlds == null) {
            log.info("[Waypoint] No worlds to be loaded.");
            return;
        }
        List<World> worldlist = plugin.getServer().getWorlds();
        List<String> worldnames = new ArrayList<String>();
        World wx;
        Iterator worldlist_i = worldlist.iterator();
        while (worldlist_i.hasNext()) {
            wx = (World) worldlist_i.next();
            worldnames.add((String) wx.getName().toString());
        }
        String worldname;
        String env;
        boolean pvp;
        int mode;
        ConfigurationNode e;
        for (Map.Entry<String, ConfigurationNode> entry : worlds.entrySet()) {
            worldname = entry.getKey();
            e = entry.getValue();
            env = e.getString("env");
            mode = e.getInt("mode", 0);
            pvp = e.getBoolean("pvp", false);
            if (!worldnames.contains(worldname)) {
                this.Import(worldname, env, mode);
            } else {
                log.info(String.format("[Waypoint] Loaded properties for world: { %s [ENV: %s(MODE:%s)] } -- PVP: %s", worldname, env, Integer.toString(mode), Boolean.toString(pvp)));
            }
        }
        return;
    }

    public World Import (String worldname, String env) {
        Environment environment = Environment.valueOf(env);
        Configuration world = config.getWorld();
        if (new File(worldname).exists() && environment != null) {
            World wx = plugin.getServer().createWorld(worldname, environment);
            log.info(String.format("[Waypoint] Imported world: { %s [ENV:%s(MODE:%s)] } -- PVP: %s", worldname, env.toUpperCase(), "0", Boolean.toString(wx.getPVP())));
            world.setProperty("worlds." + worldname + ".env", env.toUpperCase());
            world.setProperty("worlds." + worldname + ".mode", 0);
            world.setProperty("worlds." + worldname + ".pvp", false);
            world.save();
            return wx;
        } else if (environment == null) {
            return null;
        } else {
            return null;
        }
    }

    public World Import (String worldname, String env, int mode) {
        if (mode != 0 && mode != 1) { mode = 0; };
        Environment environment = Environment.valueOf(env);
        Configuration world = config.getWorld();
        boolean pvp;
        if (new File(worldname).exists() && environment != null) {
            World wx = plugin.getServer().createWorld(worldname, environment);
            pvp = world.getBoolean("worlds." + worldname + ".pvp", false);
            wx.setPVP(pvp);
            log.info(String.format("[Waypoint] Imported world: { %s [ENV:%s(MODE:%s)] } -- PVP: %s", worldname, env.toUpperCase(), Integer.toString(mode), Boolean.toString(pvp)));
            world.setProperty("worlds." + worldname + ".env", env.toUpperCase());
            world.setProperty("worlds." + worldname + ".mode", mode);
            world.setProperty("worlds." + worldname + ".pvp", false);
            world.save();
            return wx;
        } else if (environment == null) {
            return null;
        } else {
            return null;
        }
    }

    public World Create (String worldname, String env) {
        Environment environment = Environment.valueOf(env);
        Configuration world = config.getWorld();
        if (!(new File(worldname).exists()) && environment != null) {
            World wx = plugin.getServer().createWorld(worldname, environment);
            log.info(String.format("[Waypoint] Created world: { %s [ENV:%s(MODE:%s)] } -- PVP: %s", worldname, env.toUpperCase(), "0", Boolean.toString(wx.getPVP())));
            world.setProperty("worlds." + worldname + ".env", env.toUpperCase());
            world.setProperty("worlds." + worldname + ".mode", "0");
            world.setProperty("worlds." + worldname + ".pvp", false);
            world.save();
            return wx;
        } else if (environment == null) {
            return null;
        } else {
            return null;
        }
    }

    public World Create (String worldname, String env, int mode) {
        if (mode != 0 && mode != 1) { mode = 0; };
        Environment environment = Environment.valueOf(env);
        Configuration world = config.getWorld();
        if (!(new File(worldname).exists()) && environment != null) {
            World wx = plugin.getServer().createWorld(worldname, environment);
            log.info(String.format("[Waypoint] Created world: { %s [ENV:%s(MODE:%s)] } -- PVP: %s", worldname, env.toUpperCase(), Integer.toString(mode), Boolean.toString(wx.getPVP())));
            world.setProperty("worlds." + worldname + ".env", env.toUpperCase());
            world.setProperty("worlds." + worldname + ".mode", mode);
            world.setProperty("worlds." + worldname + ".mode", false);
            world.save();
            return wx;
        } else if (environment == null) {
            return null;
        } else {
            return null;
        }
    }
}
