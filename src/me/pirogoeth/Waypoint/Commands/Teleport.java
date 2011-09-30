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
import org.bukkit.entity.Vehicle;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import java.lang.Boolean;

class Teleport extends Command {
    public Configuration main;

    public Teleport (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        try {
            setCommand("tp");
            addAlias("teleport");
            addAlias("wptp");
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
        // begin Teleport module code
        if (!permissions.has(player, "waypoint.teleport.teleport")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        }
        String subc = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /tp <target> [user]");
            return true;
        };
        // I don't think thise should be here.
        //
        //   subc = subc.toLowerCase().toString();
        String arg = null;
        try {
            arg = args[1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        };
        if (arg == null)
        {
            Player target = plugin.getServer().getPlayer((String)subc);
            if (target == null)
            {
                player.sendMessage("[Waypoint] Player " + subc + " is not logged in.");
                return true;
            }
            Location l = target.getLocation();
            player.teleport(l);
            return true;
        }
        else if (arg != null)
        {
            Player p = plugin.getServer().getPlayer((String)subc);
            Player target = plugin.getServer().getPlayer((String)arg);
            if (p == null)
            {
                player.sendMessage("[Waypoint] Player " + subc + " is not online.");
                return true;
            }
            else if (target == null)
            {
                player.sendMessage("[Waypoint] Player " + arg + " is not online.");
                return true;
            }
            Location l = target.getLocation();
            p.teleport(l);
            p.sendMessage("[Waypoint] You have been teleported by " + player.getName().toString() + ".");
            return true;
        };
        return true;
    };
};

class TeleLocation extends Command {
    public Configuration main;

    public TeleLocation (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        try {
            setCommand("tploc");
            addAlias("teleportlocation");
            addAlias("wptploc");
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
        // begin TeleLocation module code
        if (!permissions.has(player, "waypoint.teleport.location")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        }
        String subc = "";
        String arg = null;
        try {
            if (args.length == 1) {
                arg = args[0];
            } else if (args.length == 3) {
                arg = String.format("%s,%s,%s", args[0], args[1], args[2]);
            } else {
                player.sendMessage(ChatColor.BLUE + "/tploc <x,y,z|x y z>");
                return true;
            }
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        };
        if (arg != null)
        {
            World w;
            double x;
            double y;
            double z;
            try {
                String[] d = arg.split("\\,");
                w = player.getLocation().getWorld();
                x = new Double(d[0]);
                y = new Double(d[1]);
                z = new Double(d[2]);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Invalid Coordinates.");
                return true;
            };
            Location l = new Location((World) w, (Double) x, (Double) y, (Double) z);
            player.teleport(l);
            return true;
        }
        else if (arg == null)
        {
            player.sendMessage(ChatColor.RED + "[Waypoint] You need to provide a set of coordinates.");
            return true;
        };
        return true;
    };
};

class TeleportHere extends Command {
    public Configuration main;

    public TeleportHere (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        try {
            setCommand("tphere");
            addAlias("teleporthere");
            addAlias("wptphere");
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
        // begin TeleportHere module code
        if (!permissions.has(player, "waypoint.teleport.here")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        }
        String subc = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /tp <target> [user]");
            return true;
        };
        subc = subc.toLowerCase().toString();
        Player target = plugin.getServer().getPlayer((String)subc);
        if (target == null)
        {
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + subc + " is not online.");
            return true;
        }
        Location l = player.getLocation();
        Vehicle target_veh = target.getVehicle();
        if (target_veh != null) { target_veh.eject(); };
        target.teleport(l);
        target.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to " + player.getName().toString() + ".");
        return true;
    };
};