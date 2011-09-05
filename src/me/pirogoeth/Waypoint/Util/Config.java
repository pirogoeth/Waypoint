package me.pirogoeth.Waypoint.Util;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.World;
import org.bukkit.World.Environment;
import java.io.File;
import java.util.logging.Logger;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import me.pirogoeth.Waypoint.Waypoint;

public class Config {
    // main vars
    public static Waypoint plugin;
    public static Logger log = Logger.getLogger("Minecraft");
    public static String maindir = "plugins/Waypoint";
    // quizzical vars
    public static boolean loaded = false;
    // Configuration variables
    public static Configuration main;
    public static Configuration warps;
    public static Configuration users;
    public static Configuration spawn;
    public static Configuration home;
    public static Configuration world;
    public static Configuration links;
    // File variables
    public static File mainf;
    public static File spawnf;
    public static File usersf;
    public static File homef;
    public static File warpsf;
    public static File worldf;
    public static File linksf;
    // constructor
    public Config (Waypoint instance)
    {
        plugin = instance;
        new File(maindir).mkdir();
        // do our configuration initialisation here.
        initialise();
    }
    // File() creator
    private static File getFile(String fname)
    {
        File f = new File(maindir + File.separator + (String)fname);
        return f;
    }
    // initialise the Configuration setup
    public static boolean initialise ()
    {
        log.info("[Waypoint] Initialising configurations.");
        // load all of the config files
        try {
            mainf = getFile("config.yml");
            warpsf = getFile("warps.yml");
            usersf = getFile("users.yml");
            spawnf = getFile("spawn.yml");
            homef = getFile("home.yml");
            worldf = getFile("world.yml");
            linksf = getFile("links.yml");
        }
        catch (Exception e) {
            return false;
        }
        // instantiate the Configuration objects
        try {
            main = new Configuration(mainf);
            warps = new Configuration(warpsf);
            users = new Configuration(usersf);
            spawn = new Configuration(spawnf);
            home = new Configuration(homef);
            world = new Configuration(worldf);
            links = new Configuration(linksf);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public static void load()
    {
        // load all of the configurations
        log.info("[Waypoint] Reading configurations.");
        main.load();
        warps.load();
        users.load();
        spawn.load();
        home.load();
        world.load();
        links.load();
        // world variables
        List<World> world_l;
        Iterator world_l_i;
        World world_o;
        String world_n;
        Environment world_e;
        // check if we need to convert from config format 1.4 to 1.5
        if (main.getProperty("warp.groups") != null)
        {
            // ok, we're going to convert the config to the new format. easy.
            ConfigurationNode u;
            Map<String, Object> a;
            log.info("[Waypoint] Configuration version 1.4 detected! Converting to new version 1.5");
            // start with the users.
            u = main.getNode("users");
            if (u != null)
            {
                a = u.getAll();
                for (Map.Entry<String, Object> entry : a.entrySet())
                {
                    users.setProperty(String.format("users.%s", entry.getKey()), entry.getValue());
                }
            }
            else
            {
               users.setProperty("users", "");
            }
            main.removeProperty("users");
            log.info("[Waypoint] Converted users.");
            // now invites
            u = main.getNode("invites");
            if (u != null)
            {
                a = u.getAll();
                for (Map.Entry<String, Object> entry : a.entrySet())
                {
                    users.setProperty(String.format("invites.%s", entry.getKey()), entry.getValue());
                }
            }
            else
            {
                users.setProperty("invites", "");
            }
            log.info("[Waypoint] Converted invites.");
            main.removeProperty("invites");
            // convert warps
            u = main.getNode("warps");
            if (u != null)
            {
                a = u.getAll();
                for (Map.Entry<String, Object> entry : a.entrySet())
                {
                    warps.setProperty(String.format("warps.%s", entry.getKey()), entry.getValue());
                }
            }
            else
            {
                warps.setProperty("warps", "");
            }
            log.info("[Waypoint] Converted warps.");
            main.removeProperty("warps");
            // convert homes
            u = main.getNode("home");
            if (u != null)
            {
                a = u.getAll();
                for (Map.Entry<String, Object> entry : a.entrySet())
                {
                    home.setProperty(String.format("home.%s", entry.getKey()), entry.getValue());
                }
            }
            else
            {
                home.setProperty("home", "");
            }
            log.info("[Waypoint] Converted homes.");
            main.removeProperty("home");
            // convert warp permissions
            List<String> p = main.getStringList("warp.groups", null);
            main.removeProperty("warp");
            main.setProperty("warp.permissions", p);
            // convert spawns
            u = main.getNode("spawn");
            if (u != null)
            {
                a = u.getAll();
                for (Map.Entry<String, Object> entry : a.entrySet())
                {
                    spawn.setProperty(String.format("spawn.%s", entry.getKey()), entry.getValue());
                }
            }
            else
            {
                spawn.setProperty("spawn", "");
            }
            log.info("[Waypoint] Converted spawn, saving configurations.");
            main.removeProperty("spawn");
            log.info("[Waypoint] Configuration successfully converted to version 1.5, writing main defaults.");
            // remove the old defaults
            main.removeProperty("enabled");
            main.removeProperty("set_home_at_bed");
            // set the new main defaults
            main.setProperty("version", "1.5.5");
            main.setProperty("autoupdate", "true");
            main.setProperty("home.set_home_at_bed", "false");
            main.setProperty("warp.traverse_world_only", "false");
            main.setProperty("warp.list_world_only", "false");
            main.setProperty("warp.warpstring_enabled", "true");
            main.setProperty("warp.string", "welcome to %w, %p");
            links.setProperty("links", "");
            links.save();
            // world base and currently loaded world settings
            world_l = plugin.getServer().getWorlds();
            world_l_i = world_l.iterator();
            while (world_l_i.hasNext())
            {
                world_o = (World) world_l_i.next();
                world_n = world_o.getName().toString();
                world_e = world_o.getEnvironment();
                log.info(String.format("[Waypoint] Processing world: { %s : [ENV:%s] }", world_n, (String) Integer.toString(world_e.getId())));
                world.setProperty("world." + world_n, "");
                switch (world_e)
                {
                    case NORMAL:
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                    case NETHER:
                        world.setProperty("world." + world_n + ".env", "NETHER");
                        break;
                    case SKYLANDS:
                        world.setProperty("world." + world_n + ".env", "SKYLANDS");
                        break;
                    default:
                        // default shall be NORMAL
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                }
            }
            world.save();
            log.info("[Waypoint] Main configuration defaults have been set.");
        }
        // check if we need to write the defaults.
        if ((String)main.getProperty("version") == null)
        {
            log.info("[Waypoint] Writing default config values.");
            // main
            main.setProperty("version", "1.6");
            main.setProperty("autoupdate", "true");
            // home settings
            main.setProperty("home.set_home_at_bed", "false");
            // warp permission groups
            List<String> warpgroups = new ArrayList<String>();
            warpgroups.add("Default");
            warpgroups.add("Mod");
            warpgroups.add("Admin");
            main.setProperty("warp.permissions", warpgroups);
            // write the spawn points to config file
            plugin.spawnManager.ConfigWriteSpawnLocations();
            // warp settings
            main.setProperty("warp.traverse_world_only", "false");
            main.setProperty("warp.list_world_only", "false");
            main.setProperty("warp.warpstring_enabled", "true");
            main.setProperty("warp.string", "welcome to %w, %p");
            links.setProperty("links", "");
            // world base and currently loaded world settings
            world_l = plugin.getServer().getWorlds();
            world_l_i = world_l.iterator();
            while (world_l_i.hasNext())
            {
                world_o = (World) world_l_i.next();
                world_n = world_o.getName().toString();
                world_e = world_o.getEnvironment();
                log.info(String.format("[Waypoint] Processing world: { %s : [ENV:%s] }", world_n, (String) Integer.toString(world_e.getId())));
                world.setProperty("world." + world_n, "");
                switch (world_e)
                {
                    case NORMAL:
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                    case NETHER:
                        world.setProperty("world." + world_n + ".env", "NETHER");
                        break;
                    case SKYLANDS:
                        world.setProperty("world." + world_n + ".env", "SKYLANDS");
                        break;
                    default:
                        // default shall be NORMAL
                        world.setProperty("world." + world_n + ".env", "NORMAL");
                        break;
                }
            }
            world.save();
            // TODO: actually implement case insensitive warps
            log.info("[Waypoint] Wrote defaults.");
            links.save();
            main.save();
        }
        else if (!((String)main.getProperty("version")).equals("1.6"))
        {
            // write values not entered in the 1.5.9 update, but were added during
            // the 1.6 alpha testing.
            log.info("[Waypoint] Finalising 1.6 configuration.");
            // set version
            main.setProperty("version", "1.6");
            // add config option added after 1.5-dev
            main.setProperty("warp.warpstring_enabled", "true");
            main.setProperty("warp.string", "welcome to %w, %p");
            main.save();
        }
        log.info("[Waypoint] Configuration succesfully loaded.");
        loaded = true;
        return;
    }
    public static void save ()
    {
        // use the save() method of all of the Configuration instances.
        main.save();
        users.save();
        warps.save();
        spawn.save();
        home.save();
        links.save();
        log.info("[Waypoint] Saved all configurations.");
    }
    public static Configuration getMain ()
    {
        // returns the Configuration object for the main config file
        return (Configuration) main;
    }
    public static Configuration getWarp ()
    {
        // returns the Configuration object for the warps config file
        return (Configuration) warps;
    }
    public static Configuration getUsers ()
    {
        // returns the Configuration object for the users config file
        return (Configuration) users;
    }
    public static Configuration getSpawn ()
    {
        // returns the Configuration object for the spawn config file
        return (Configuration) spawn;
    }
    public static Configuration getHome ()
    {
        // returns the Configuration object for the home config file
        return (Configuration) home;
    }
    public static Configuration getWorld()
    {
        // returns the Configuration object for the world config file
        return (Configuration) world;
    }
    public static Configuration getLinks()
    {
        // returns the Configuratio object for the links config file
        return (Configuration) links;
    }
}