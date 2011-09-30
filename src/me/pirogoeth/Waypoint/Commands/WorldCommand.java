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
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            subc = null;
        }
        if (subc == null)
        {
            String worldname = (String) player.getLocation().getWorld().getName().toString();
            player.sendMessage(ChatColor.BLUE + "You are currently in world: " + worldname);
            String x = (String) Double.toString(player.getLocation().getX());
            String y = (String) Double.toString(player.getLocation().getY());
            String z = (String) Double.toString(player.getLocation().getZ());
            player.sendMessage(ChatColor.BLUE + "Your current position is: " + x + "," + y + "," + z);
            return true;
        }
        else if (subc.equalsIgnoreCase("list"))
        {
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
        else if (subc.equalsIgnoreCase("import"))
        {
            if (!permissions.has(player, "waypoint.admin.world.import")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [2 args]: worldname, environment
            String worldname = args[1];
            String environ;
            try { environ = args[2]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
            if (environ == null)
            {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                return true;
            }
            org.bukkit.World wx = plugin.worldManager.Import(worldname, ((String) environ).toUpperCase());
            if (wx == null)
            {
                player.sendMessage(ChatColor.RED + "World import failed.");
                return true;
            }
            plugin.config.getWorld().setProperty("world." + worldname + ".env", environ.toUpperCase());
            plugin.config.save();
            player.sendMessage(String.format("%s[Waypoint] Loaded world: { %s [ENV:%s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
            return true;
        }
        else if (subc.equalsIgnoreCase("create"))
        {
            if (!permissions.has(player, "waypoint.admin.world.create")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            // @accepts [2-3 args]: worldname, environment [, seed]
            String worldname = args[1];
            String environ;
            try { environ = args[2]; }
            catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
            String seed;
            org.bukkit.World wx = null;
            /**
              * try {
              *     seed = args[3];
              * }
              * catch (java.lang.ArrayIndexOutOfBoundsException e)
              * {
              *   seed = null;
              * }
              */
            if (environ == null)
            {
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                return true;
            }
            /**
              * if (seed != null)
              * {
              *   wx = plugin.worldManager.Create(worldname, environ.toUpperCase(), seed);
              * }
              * else if (seed == null)
              * {
              *   wx = plugin.worldManager.Create(worldname, environ.toUpperCase());
              * }
              */
            wx = plugin.worldManager.Create(worldname, environ.toUpperCase());
            if (wx == null)
            {
                player.sendMessage(String.format("%s[Waypoint] Could not create world: %s", ChatColor.RED, worldname));
                return true;
            }
            plugin.config.getWorld().setProperty("world." + worldname + ".env", environ.toUpperCase());
            plugin.config.save();
            player.sendMessage(String.format("%s[Waypoint] Successfully created world: { %s [ENV: %s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
            return true;
        }
        else if (subc != null)
        {
            org.bukkit.World w = plugin.getServer().getWorld(subc);
            if (w == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] World " + subc + " does not exist.");
                return true;
            }
            if (!permissions.has(player, "waypoint.world.teleport"))
            {
                player.sendMessage("[Waypoint] You do not have permission to use this command.");
                return true;
            }
            Location wsl = w.getSpawnLocation();
            player.teleport(wsl);
            player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been taken to the spawn of world '" + (String) w.getName().toString() + "'.");
            return true;
        };
        return true;
    };
};