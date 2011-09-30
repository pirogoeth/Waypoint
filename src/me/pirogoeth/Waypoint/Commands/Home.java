package me.pirogoeth.Waypoint.Commands;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.MinorUtils;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import java.util.Map;
import java.lang.Boolean;

class Home extends Command {
    public Configuration home;
    public Configuration main;

    public Home (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        home = configuration.getHome();
        try {
            setCommand("home");
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
        // begin Home module code
        if (!permissions.has(player, "waypoint.home")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        };
        String subc = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            if (home.getProperty(MinorUtils.HomeNodeChomp(player, player.getWorld(), "coord.X")) != null)
            {
                World w = player.getWorld();
                double x = (Double)home.getProperty(MinorUtils.HomeNodeChomp(player, w, "coord.X"));
                double y = (Double)home.getProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Y"));
                double z = (Double)home.getProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Z"));
                Location l = new Location(w, x, y, z);
                player.setCompassTarget(l);
                player.teleport(l);
                player.sendMessage(ChatColor.GREEN + "Welcome home, " + player.getName().toString() + ".");
                return true;
            }
            else if (home.getProperty(MinorUtils.HomeNodeChomp(player, player.getWorld(), "coord.X")) == null)
            {
                World w = player.getWorld();
                home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
                home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
                home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home point for world " + w.getName().toString() + " was not set, so it was automatically set to the point you are currently at now.");
                home.save();
                return true;
            };
        };
        subc = subc.toLowerCase().toString();
        if (subc.equalsIgnoreCase("set"))
        {
            World w = player.getWorld();
            if (!permissions.has(player, "waypoint.home.set")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            };
            home.setProperty(MinorUtils.HomeNodeChomp(player, w, "world"), player.getWorld().getName().toString());
            home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
            home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
            home.setProperty(MinorUtils.HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home location has been set.");
            home.save();
            return true;
        }
        else if (subc.equalsIgnoreCase("help"))
        {
            player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            player.sendMessage(ChatColor.GREEN + "/home:");
            player.sendMessage(ChatColor.RED + "   with args, will teleport you to your home.");
            player.sendMessage(ChatColor.RED + "   set - sets your home to the location you are currently standing at.");
            player.sendMessage(ChatColor.GREEN + "ways to set your home:");
            if (main.getProperty("home.set_home_at_bed") == "true") { player.sendMessage(ChatColor.RED + "getting into a bed will set your home to the position of that bed."); };
            player.sendMessage(ChatColor.RED + "typing /home without having a home already set.");
            player.sendMessage(ChatColor.RED + "typing /home set.");
            return true;
        };
        return true;
    };
};