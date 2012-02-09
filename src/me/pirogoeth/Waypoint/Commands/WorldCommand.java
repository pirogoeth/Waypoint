package me.pirogoeth.Waypoint.Commands;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.MinorUtils.*;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Boolean;
import java.io.File;

class WorldCommand extends Command {
    public Configuration main;

    public WorldCommand (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        try {
            setCommand("world");
            addAlias("wpworld");
            register();
        } catch (CommandException e) {
            e.printStackTrace();
        }
    };

    public static boolean delete_r (File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delete_r(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    };

    @Override
    public boolean run (Player player, String[] args)
      throws CommandException {
        if (registered == false)
            throw new CommandException("Command is not registered.");
        // begin World module code
        if (!permissions.has(player, "waypoint.world")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        }
        String subc = "";
        String arg = "";
        try {
            subc = args[0];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            subc = null;
        }
        if (subc == null) {
            String worldname = (String) player.getLocation().getWorld().getName().toString();
            player.sendMessage(ChatColor.BLUE + "You are currently in world: " + worldname);
            String x = (String) Double.toString(player.getLocation().getX());
            String y = (String) Double.toString(player.getLocation().getY());
            String z = (String) Double.toString(player.getLocation().getZ());
            String chunk = String.format("%s,%s", player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
            player.sendMessage(ChatColor.BLUE + "Your current position is: " + x + "," + y + "," + z);
            player.sendMessage(ChatColor.BLUE + "Current chunk: " + chunk);
            return true;
        }
        else if (subc.equalsIgnoreCase("list")) {
            if (!permissions.has(player, "waypoint.world.list")) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permissions to use this command.");
                return true;
            }
            // @accepts [0 args]
            List<org.bukkit.World> w = plugin.getServer().getWorlds();
            player.sendMessage(ChatColor.GREEN + "World List: ");
            Iterator i = w.iterator();
            org.bukkit.World wx;
            while (i.hasNext())
            {
                wx = (org.bukkit.World) i.next();
                player.sendMessage(ChatColor.GREEN + " - " + wx.getName());
            }
            return true;
        }
        else if (subc.equalsIgnoreCase("import")) {
            if (!permissions.has(player, "waypoint.admin.world.import")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [2 args]: worldname, environment
            String worldname;
            try { worldname = args[1]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] Please specify a world name.");
                player.sendMessage(ChatColor.BLUE +
                    "Usage: /world <worldname> <environment>");
                return true;
            };
            String environ;
            try { environ = args[2]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
            if (environ == null) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                return true;
            }
            org.bukkit.World wx = plugin.worldManager.Import(worldname, ((String) environ).toUpperCase());
            if (wx == null) {
                player.sendMessage(ChatColor.RED + "World import failed.");
                return true;
            }
            plugin.config.getWorld().setProperty("worlds." + worldname + ".env", environ.toUpperCase());
            plugin.config.save();
            player.sendMessage(String.format("%s[Waypoint] Loaded world: { %s [ENV:%s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
            return true;
        }
        else if (subc.equalsIgnoreCase("unload")) {
            if (!permissions.has(player, "waypoint.admin.world.unload")) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [2 args]: worldname, (bool) save
            String worldname;
            boolean save = true;
            try { worldname = args[1]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { worldname = null; };
            try { save = Boolean.parseBoolean(args[2]); }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Save argument not specified; assuming true");
                save = true;
            };
            if (worldname == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Please specify a world to unload.");
                return true;
            }
            if (!plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(worldname))) {
                player.sendMessage(ChatColor.RED + "[Waypoint] World " + worldname + " is not loaded.");
                return true;
            }
            boolean result = plugin.getServer().unloadWorld(worldname, save);
            if (result == false) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Error unloading world: " + worldname);
                return true;
            } else if (result == true) {
                plugin.config.getWorld().removeProperty("worlds." + worldname);
                plugin.config.save();
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Successfully unloaded world: " + worldname);
                return true;
            };
            return true;
        }
        else if (subc.equalsIgnoreCase("delete")) {
            if (!permissions.has(player, "waypoint.admin.world.delete")) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [1 arg]: worldname
            String worldname;
            try { worldname = args[1]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { worldname = null; };
            if (worldname == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Please specify a world to delete.");
                return true;
            }
            if (!plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(worldname))) {
                player.sendMessage(ChatColor.RED + "[Waypoint] World " + worldname + " is not loaded.");
                return true;
            }
            boolean result = plugin.getServer().unloadWorld(worldname, false);
            if (result == false) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Error deleting world: " + worldname + " [err: 1]");
                return true;
            } else if (result == true) {
                boolean d_result = this.delete_r(new File(worldname));
                if (d_result == false) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Could not delete world " + worldname);
                    return true;
                } else if (d_result == true) {
                    plugin.config.getWorld().removeProperty("worlds." + worldname);
                    plugin.config.save();
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Successfully deleted world " + worldname);
                    return true;
                }
            };
            return true;
        }
        else if (subc.equalsIgnoreCase("create")) {
            if (!permissions.has(player, "waypoint.admin.world.create")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [2-4 args]: worldname, environment [, gamemode, pvp]
            String worldname = args[1];
            String environ;
            try { environ = args[2]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
            int gm;
            boolean pvp;
            org.bukkit.World wx = null;
            // try to get gamemode
            try {
                gm = Integer.parseInt(args[3]);
            } catch (ArrayIndexOutOfBoundsException e) {
                gm = 0;
            }
            // try to get pvp value
            try {
                pvp = Boolean.getBoolean(args[4].toLowerCase());
            } catch (ArrayIndexOutOfBoundsException e) {
                pvp = false;
            }
            // check environment value
            if (environ == null) {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                return true;
            }
            wx = plugin.worldManager.Create(worldname, environ.toUpperCase(), gm, pvp);
            if (wx == null) {
                player.sendMessage(String.format("%s[Waypoint] Could not create world: %s", ChatColor.RED, worldname));
                return true;
            }
            plugin.config.getWorld().setProperty("worlds." + worldname + ".env", environ.toUpperCase());
            plugin.config.save();
            player.sendMessage(String.format("%s[Waypoint] Successfully created world: { %s [ENV: %s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
            return true;
        }
        else if (subc != null) {
            org.bukkit.World w = plugin.getServer().getWorld(subc);
            if (w == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] World " + subc + " does not exist.");
                return true;
            }
            if (!permissions.has(player, "waypoint.world.teleport")) {
                player.sendMessage("[Waypoint] You do not have permission to use this command.");
                return true;
            }
            Location wsl = w.getSpawnLocation();
            boolean su = player.teleport(wsl);
            if (su == false) {
                return true;
            }
            player.getLocation().getChunk().load();
            player.sendMessage(ChatColor.GREEN +
                "[Waypoint] You have been taken to the spawn of world '" + (String) w.getName().toString() + "'.");
            return true;
        };
        return true;
    };
};
