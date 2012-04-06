package me.pirogoeth.waypoint.util;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.ConfigInventory;
import me.pirogoeth.waypoint.util.LogHandler;
import me.pirogoeth.waypoint.util.Permission;

// java imports
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
<<<<<<< HEAD
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
=======
import org.bukkit.World
import org.bukkit.World.Environment;
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Helper {

    private final LogHandler log = new LogHandler();
    private final Server server;

<<<<<<< HEAD
    private List<String> warp_groups = (List<String>) ConfigInventory.MAIN.getConfig().get("warp.permissions", new ArrayList<String>(0));
=======
    private List<String> warp_groups = ConfigInventory.MAIN.getStringList(
        "groups");
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d

    public Helper(Server server) {
        this.server = server;
    }

    // warp helper methods
    public void loadWarpGroups() {
        Iterator i = this.warp_groups.iterator();
<<<<<<< HEAD
        StringBuffer s = new StringBuffer();
        this.log.info(Integer.toString(this.warp_groups.size()));
        while (i.hasNext()) {
            s.append(((String) i.next()) + ",");
            this.log.info(s.toString());
=======
        StringBuffer s = new StringBuffer((String) i.next());
        while (i.hasNext()) {
            s.append(", ").append((String) i.next());
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
        }
        this.log.info(String.format("Permission groups loaded: %s", s.toString()));
    };

    public Location getWarp(final String warpname) {
<<<<<<< HEAD
        Location l = (Location) ConfigInventory.WARP.getConfig().get(
=======
        Location l = ConfigInventory.WARPS.getConfig().get(
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            String.format("%s.location", warpname),
            null);
        return l;
    };

    public String generateWarpstring(final Player player, final String warpname,
        final Location loc) {

        if (ConfigInventory.MAIN.getConfig().getBoolean("warp.warpstring_enabled", false) == true) {
            String warpstring = ConfigInventory.MAIN.getConfig().getString(
                "warp.string",
                "welcome to %w, %p."
            );
            warpstring = warpstring.replaceAll("%w", warpname);
            warpstring = warpstring.replaceAll("%p", player.getName());
<<<<<<< HEAD
            warpstring = warpstring.replaceAll("%lX", Double.toString(loc.getX()));
            warpstring = warpstring.replaceAll("%lY", Double.toString(loc.getY()));
            warpstring = warpstring.replaceAll("%lZ", Double.toString(loc.getZ()));
=======
            warpstring = warpstring.replaceAll("%lX", loc.getX());
            warpstring = warpstring.replaceAll("%lY", loc.getY());
            warpstring = warpstring.replaceAll("%lZ", loc.getZ());
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            warpstring = warpstring.replaceAll("%W", loc.getWorld().getName());
            return warpstring;
        } else {
            return null;
        }
    };

    public void setWarp(final Player player, final String warpname,
        final Location loc) {

        // set location
<<<<<<< HEAD
        ConfigInventory.WARP.getConfig().set(String.format(
=======
        ConfigInventory.WARPS.set(String.format(
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            "%s.location", warpname),
            loc
        );
        // set world
<<<<<<< HEAD
        ConfigInventory.WARP.getConfig().set(String.format(
=======
        ConfigInventory.WARPS.set(String.format(
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            "%s.world", warpname),
            loc.getWorld().getName()
        );
        // set owner
<<<<<<< HEAD
        ConfigInventory.WARP.getConfig().set(String.format(
=======
        ConfigInventory.WARPS.set(String.format(
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            "%s.owner", warpname),
            player.getName()
        );
        // set permission group
<<<<<<< HEAD
        ConfigInventory.WARP.getConfig().set(String.format(
=======
        ConfigInventory.WARPS.set(String.format(
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            "%s.permission", warpname),
            (String) this.warp_groups.get(0)
        );
        return;
    };

    // world helper methods
    public void loadWorldProperties() {
<<<<<<< HEAD
        Map<String, Object> worlds = ConfigInventory.WORLD.getConfig().getConfigurationSection("worlds").getValues(true);
=======
        Map<String, ConfigurationSection> worlds = ConfigInventory.WORLD.getConfig().getConfigurationSection("worlds");
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
        if (worlds == null) {
            this.log.info("No world properties to load.");
            return;
        }
        List<World> worldlist = this.server.getWorlds();
        List<String> worldnames = new ArrayList<String>();
        Iterator worldlister = worldlist.iterator();
        while (worldlister.hasNext()) {
<<<<<<< HEAD
            worldnames.add((String) ((World) worldlister.next()).getName());
        }
        String worldname, env; boolean pvp; int mode; ConfigurationSection e;
        for (Map.Entry<String, Object> entry : worlds.entrySet()) {
            worldname = entry.getKey();
            e = (ConfigurationSection) entry.getValue();
            env = e.getString("env", Environment.NORMAL.toString());
            mode = e.getInt("mode", 0);
            pvp = e.getBoolean("pvp", true);
            if (!(worldnames.contains(worldname)))
                // this.importWorld(worldname, env, mode, pvp);
                return;
=======
            worldnames.append((String) worldlister.next().getName());
        }
        String name, env, boolean pvp, int mode, ConfigurationSection e;
        for (Map.Entry<String, ConfigurationSection> entry : worlds.entrySet()) {
            worldname = entry.getKey();
            e = entry.getValue();
            env = e.getString("env", NORMAL);
            mode = e.getInt("mode", 0);
            pvp = e.getBoolean("pvp", true);
            if (!(worldnames.contains(worldname)))
                this.importWorld(worldname, env, mode, pvp);
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
            else {
                this.log.info(String.format(
                    "Loaded world properties: %s { ENV: %s, MODE: %s, PVP: %s }",
                    worldname, env, Integer.toString(mode), Boolean.toString(pvp)
                ));
<<<<<<< HEAD
            };
        };
        return;
    };

    public World addWorld(String name, Environment env, String seed, WorldType type,
                            boolean generateStructures, String generator, boolean save) {
        long _seed = null;
        WorldCreator creator = new WorldCreator(name);
        if (seed && seed.length() > 0) {
            try {
                _seed = Long.parseLong(seed);
            } catch (java.lang.NumberFormatException e) {
                _seed = (long) seedString.hashCode();
            }
            creator.seed(_seed);
        }
        creator.generator(((generator != null) ? generator : null));
        creator.environment(env);
        if (type != null) creator.type(type);
        creator.generateStructures((generateStructures != null ? generateStructures : null));
        String message = String.format("Loading world: '%s' - [ENV: %s, TYPE %s]", name, env, type);
        if (seed) message += " {seed:" + seed + "}";
        if (generator) message += " {generator:" + generator + "}";
        this.log.info(message);

        World world;

        try { world = creator.createWorld(); }
        catch (java.lang.Exception e) {
            this.log.info("An error occurred while creating world " + name + ":");
            this.log.info(e.getMessage());
            return;
        }
        if (save) {
            String node = String.format("world.%s", name);
            YamlConfiguration c = ConfigInventory.WORLD.getConfig();
            c.set(node + ".env", env);
            c.set(node + ".seed", seed);
            c.set(node + ".type", type);
            c.set(node + ".structures", generateStructures);
            c.set(node + ".generator", generator);
        }
    }
};
=======
            }
        return;
    };
    
    // importWorld and its overloads
    
    // createWorld and its overloads
};
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
