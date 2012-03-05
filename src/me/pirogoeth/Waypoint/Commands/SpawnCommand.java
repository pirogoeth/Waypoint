package me.pirogoeth.Waypoint.Commands;

// internal imports
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.MinorUtils.*;
import me.pirogoeth.Waypoint.Waypoint;

// bukkit imports
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.configuration.Configuration;
import org.bukkit.ChatColor;
import java.lang.Boolean;

class SpawnCommand extends Command {
    public Configuration main;
    public Configuration spawn;

    public SpawnCommand (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        spawn = configuration.getSpawn();
        try {
            setCommand("spawn");
            addAlias("wpspawn");
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
        // begin Spawn module code
        if (!permissions.has(player, "waypoint.spawn")) {
            player.sendMessage(ChatColor.BLUE +
                "You do not have the permissions to use this command.");
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
        if (w == null) {
            player.sendMessage(
                "[Waypoint] World '" + subc + "' does not exist.");
            return true;
        }
        if (!permissions.has(player, String.format("waypoint.world.access.%s", w.getName().toString()))) {
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] You do not have permission to enter this world.");
            return true;
        }
        plugin.spawnManager.SendPlayerToSpawn(w, player);
        return true;
   };
};

class SetSpawn extends Command {
    public Configuration main;
    public Configuration spawn;

    public SetSpawn (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        spawn = configuration.getSpawn();
        try {
            setCommand("setspawn");
            addAlias("wpsetspawn");
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
        // begin SetSpawn module code
        if (!permissions.has(player, "waypoint.admin.spawn.set")) {
            player.sendMessage(ChatColor.BLUE +
                "You do not have the permissions to use this command.");
            return true;
        };
        String subc = "";
        World w = player.getWorld();
        Location l = player.getLocation();
        w.setSpawnLocation((int) l.getX(), (int) l.getY(), (int) l.getZ());
        player.sendMessage(ChatColor.AQUA +
            "[Waypoint] Spawn for world " + player.getWorld().getName() + " has been set.");
        return true;
    };
};

class SpawnAdmin extends Command {
    public Configuration main;
    public Configuration spawn;

    public SpawnAdmin (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        spawn = configuration.getSpawn();
        try {
            setCommand("spawnadmin");
            addAlias("wpspawnadmin");
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
        // begin SpawnAdmin module code
        if (!permissions.has(player, "waypoint.admin.spawn")) {
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] You do not have the permissions to use this command.");
            return true;
        };
        String subc = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /spawnadmin <save|load;set> <world>");
            return true;
        }
        subc = subc.toLowerCase().toString();
        String arg = null;
        try {
            arg = args[1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        }
        if (subc.equalsIgnoreCase("save")) {
            if (!permissions.has(player, "waypoint.admin.spawn.save")) {
                player.sendMessage(ChatColor.BLUE +
                    "[Waypoint] You do not have the permissions to use this command.");
                return true;
            };
            if (arg == null) {
                World w = plugin.getServer().getWorld(player.getWorld().getName().toString());
                plugin.spawnManager.SaveWorldSpawnLocation((World)w);
                player.sendMessage(ChatColor.BLUE +
                    "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
                return true;
            };
            World w = plugin.getServer().getWorld(arg);
            if (w == null) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] Invalid world: " + arg);
                return true;
            }
            plugin.spawnManager.SaveWorldSpawnLocation(w);
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
            return true;
        }
        else if (subc.equalsIgnoreCase("load")) {
            if (!permissions.has(player, "waypoint.admin.spawn.load")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            };
            if (arg == null)
            {
                plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
                player.sendMessage(ChatColor.BLUE +
                    "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
                return true;
            };
            plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
            return true;
        }
        else if (subc.equalsIgnoreCase("set")) {
            if (!permissions.has(player, "waypoint.admin.spawn.set")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            };
            plugin.spawnManager.SetSpawnFromPlayer(player.getWorld(), player);
            player.getWorld().save();
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] Set spawn point for world: " + player.getWorld().getName());
            return true;
        };
        return true;
    };
};
