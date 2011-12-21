package me.pirogoeth.Waypoint.Commands;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.MinorUtils.*;
import me.pirogoeth.Waypoint.Util.Governor;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.util.config.Configuration;
import org.bukkit.ChatColor;
import java.util.Map;
import java.lang.Boolean;

class Warp extends Command {
    public Configuration main;
    public Configuration warp;
    public Governor limitProvider;

    public Warp (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        warp = configuration.getWarp();
        this.limitProvider = instance.limitProvider;
        try {
            setCommand("warp");
            addAlias("wpwarp");
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
        // begin Warp module code
        if (!permissions.has(player, "waypoint.warp")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        };
        String subc = "";
        String arg = "";
        String k = "";
        String v = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /warp <add|del|set|list|<warp name>> [key] [value]");
            return true;
        };
        String origc = subc;
        // again, this is a case sensitive situation.
        // subc = subc.toLowerCase().toString();
        try {
            arg = args[1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        };
        try {
            k = args[2];
            v = args[3];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            k = null;
            v = null;
        };
        if (subc.equalsIgnoreCase("add"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warp add <warpname>");
                return true;
            };
            if (!permissions.has(player, "waypoint.warp.create"))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                return true;
            };
            if (this.limitProvider.getWarp().playerReachedLimit(player) == true) {
                player.sendMessage(ChatColor.RED + "[Waypoint] You have reached the maximum number of warps one user can have.");
                return true;
            }
            plugin.warpManager.CreateWarp(player, arg);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been created.");
            return true;
        }
        else if (subc.equalsIgnoreCase("del"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warp del <warpname>");
                return true;
            };
            if (warp.getProperty(plugin.warpManager.WarpBase(arg)) == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                return true;
            };
            String owner = (String)warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
            if (!permissions.has(player, "waypoint.warp.delete") || !owner.equals(player.getName().toString()))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                return true;
            };
            plugin.warpManager.DeleteWarp(arg);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been deleted.");
            return true;
        }
        else if (subc.equalsIgnoreCase("remote"))
        {
            if (arg == null || k == null)
            {
                player.sendMessage(ChatColor.RED + "/warp remote <player> <warpname>");
                return true;
            };
            if (!permissions.has(player, "waypoint.warp.remote"))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                return true;
            };
            Player target = plugin.getServer().getPlayer((String) arg);
            if (target == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] Player " + arg + " is not online.");
                return true;
            };
            boolean remote = plugin.warpManager.RemoteWarp(target, player, (String) k);
            return true;
        }
        else if (subc.equalsIgnoreCase("set"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            else if (k == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            else if (v == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            // XXX - remove this for:
            //   custom properties?
            //
            // else if (!k.equalsIgnoreCase("owner") || !k.equalsIgnoreCase("permission"))
            // {
            //    player.sendMessage(ChatColor.GREEN + "Settable warp options:");
            //    player.sendMessage(ChatColor.BLUE + " - owner -- the owner of the warp.");
            //    player.sendMessage(ChatColor.BLUE + " - permission -- who can use this warp.");
            //    return true;
            // }
            String owner = (String) warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
            if (!owner.equals(player.getName().toString()))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission modify this warp.");
                return true;
            };
            // WOW I'M SUCH A FREAKING IDIOT. IT TOOK 5 BUILDS 
            // TO FIGURE THIS SHIT OUT.
            //
            // k = k.toLowerCase().toString();
            // v = v.toLowerCase().toString();
            if (warp.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                return true;
            };
            boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
            if (!f)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                return true;
            }
            else if (f)
            {
                player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                return true;
            };
            return true;
        }
        else if (subc.equalsIgnoreCase("list"))
        {
            if (!permissions.has(player, "waypoint.warp.list"))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have the permission to access this command.");
                return true;
            };
            int x = 0;
            Map<String, ConfigurationNode> a;
            a = warp.getNodes("warps");
            if (a == null || a.size() == 0)
            {
                player.sendMessage(ChatColor.YELLOW + "[Waypoint] There are currently no warps to be displayed.");
                return true;
            };
            player.sendMessage(ChatColor.GREEN + "====[Waypoint] Warps available to you:====");
            for (Map.Entry<String, ConfigurationNode> entry : a.entrySet())
            {
                ConfigurationNode node = entry.getValue();
                String warppermission = (String) node.getProperty("permission");
                if (warppermission == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You have not set any warp permission groups to your configuration.");
                    return true;
                };
                if (plugin.warpManager.checkperms(player, warppermission))
                {
                    if (!player.getWorld().toString().equals((String)node.getProperty("world")) && ((String) plugin.config.getMain().getProperty("warp.list_world_only")).equals("true"))
                    {
                        continue;
                    }
                    else
                    {
                        player.sendMessage(ChatColor.AQUA + " - " + entry.getKey());
                    };
                };
                x++;
            };
            if (x == 0)
            {
                player.sendMessage(ChatColor.YELLOW + "[Waypoint] There are currently no warps to be displayed.");
            };
            return true;
        }
        // TODO: case sensitivity check
        else if (warp.getProperty(plugin.warpManager.WarpBase(origc)) != null)
        {
            plugin.warpManager.PlayerToWarp(player, (String)origc);
            player.getLocation().getChunk().load();
            return true;
        }
        else if (warp.getProperty(plugin.warpManager.WarpBase(origc)) == null)
        {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[Waypoint] Warp " + origc + " does not exist.");
            return true;
        };
    return false;
    };
};

class SetWarp extends Command {
    public Configuration main;

    public SetWarp (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        try {
            setCommand("setwarp");
            addAlias("wpsetwarp");
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
        // being SetWarp module code
        if (!permissions.has(player, "waypoint.warp.create")) {
            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
            return true;
        };
        String subc = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /setwarp <name>");
            return true;
        };
        plugin.warpManager.CreateWarp(player, subc);
        player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + subc + " has been created.");
        return true;
    };
};

class WarpAdmin extends Command {
    public Configuration main;
    public Configuration warp;

    public WarpAdmin (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        warp = configuration.getWarp();
        try {
            setCommand("warpadmin");
            addAlias("wpwarpadmin");
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
        // begin WarpAdmin module code
        if (!permissions.has(player, "waypoint.admin.warp"))
        {
            player.sendMessage(ChatColor.BLUE + "You do not have the permission to use this command.");
            return true;
        }
        String subc = "";
        String arg = "";
        String k = "";
        String v = "";
        try {
            subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /warpadmin <del|set> [key] [value]");
            return true;
        };
        try {
            arg = args[1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        };
        try {
            k = args[2];
            v = args[3];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            k = null;
            v = null;
        };
        if (subc.equalsIgnoreCase("del"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warpadmin del <warpname>");
                return true;
            }
            if (warp.getProperty(plugin.warpManager.WarpBase(arg)) == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                return true;
            }
            if (!permissions.has(player, "waypoint.admin.warp.delete"))
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                return true;
            }
            plugin.warpManager.DeleteWarp(arg);
            player.sendMessage(ChatColor.BLUE + "[Waypoint] Warp " + arg + " has been deleted.");
            return true;
        }
        else if (subc.equalsIgnoreCase("read"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warp read <warpname>");
                return true;
            }
            ConfigurationNode a = warp.getNode(plugin.warpManager.WarpBase(arg));
            if (a == null)
            {
                player.sendMessage("[Waypoint] No warp by that name.");
                return true;
            }
            player.sendMessage(ChatColor.BLUE + "[Waypoint] Metadata for warp '" + arg + "':");
            Map<String, Object> b = a.getAll();
            for (Map.Entry<String, Object> entry : b.entrySet())
            {
                player.sendMessage(String.format("%s  %s -> %s%s", ChatColor.GREEN, (String) entry.getKey(), ChatColor.BLUE, (String) entry.getValue().toString()));
            }
            return true;
        }
        else if (subc.equalsIgnoreCase("set"))
        {
            if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            else if (k == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            else if (v == null)
            {
                player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                return true;
            }
            String owner = (String) warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
            if (warp.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                return true;
            }
            boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
            if (!f)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                return true;
            }
            else if (f)
            {
                player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                return true;
            }
            return true;
        };
        return true;
    };
};