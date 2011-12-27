package me.pirogoeth.Waypoint.Commands;

// internal imports
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.MinorUtils;
import me.pirogoeth.Waypoint.Util.Governor;
import me.pirogoeth.Waypoint.Waypoint;
// bukkit imports
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
// java imports
import java.util.Map;
import java.lang.Boolean;

class Waypoints extends Command {
    public Configuration main;
    public Configuration users;
    public Governor limitProvider;

    public Waypoints (Waypoint instance) {
        super(instance);
        main = configuration.getMain();
        users = configuration.getUsers();
        this.limitProvider = instance.limitProvider;
        try {
            setCommand("wp");
            addAlias("waypoint");
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
        // begin Waypoints module code
        String subc = "";
        try {
            subc = args[0];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            return true;
        };
        subc = subc.toLowerCase().toString();
        String arg = null;
        try {
            arg = args[1];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
        };
        if (subc.equalsIgnoreCase("add")) {
            if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.add")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty(MinorUtils.UserNodeChomp(player, arg, "world")) != null) {
                player.sendMessage("[Waypoint] Point '" + arg + "' already exists!");
                return true;
            }
            if (this.limitProvider.getWaypoint().playerReachedLimit(player) == true) {
                player.sendMessage(ChatColor.RED + "[Waypoint] You have reached the maximum number of waypoints allowed.");
                return true;
            }
            // position
            users.setProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.X"), player.getLocation().getX());
            users.setProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.Y"), player.getLocation().getY());
            users.setProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.Z"), player.getLocation().getZ());
            // eye direction
            // not needed anymore: users.setProperty(MinorUtils.UserNodeChomp(player, arg, "coord.pitch"), player.getLocation().getPitch());
            //  users.setProperty(MinorUtils.UserNodeChomp(player, arg, "coord.yaw"), player.getLocation().getYaw());
            users.setProperty(
                MinorUtils.UserNodeChomp(player, arg, "world"), player.getLocation().getWorld().getName().toString());
            users.save();
            player.sendMessage(ChatColor.GREEN +
                "[Waypoint] Set point '" + arg + "' in world '" +
                player.getLocation().getWorld().getName().toString() + "'.");
            return true;
        }
        else if (subc.equalsIgnoreCase("del") || subc.equalsIgnoreCase("delete") || subc.equalsIgnoreCase("remove")) {
            if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.delete")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty(MinorUtils.UserNodeChomp(player, arg, "world")) == null) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] Point '" + arg + "' does not exist!");
                return true;
            }
            users.removeProperty("users." + player.getName().toString() + "." + arg);
            player.sendMessage("[Waypoint] Point '" + arg + "' has been deleted.");
            users.save();
            return true;
        }
        else if (subc.equalsIgnoreCase("tp") || subc.equalsIgnoreCase("teleport")) {
            if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.teleport")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty(MinorUtils.UserNodeChomp(player, arg, "world")) == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] Point '" + arg + "' does not exist!");
                return true;
            }
            World w = plugin.getServer().getWorld(
                users.getProperty(MinorUtils.UserNodeChomp(player, arg, "world")).toString());
            if (!(plugin.getServer().getWorlds().contains(w))) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] World " + users.getProperty(
                        MinorUtils.UserNodeChomp(player, arg, "world")
                    ) + " is not loaded.");
                return true;
            }
            if (((String) configuration.getMain().getProperty("warp.traverse_world_only")).equals("true") && !(player.getWorld().toString().equals(w.getName().toString()))) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] You cannot warp to a point outside this world.");
                return true;
            }
            double x = (Double) users.getProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.X"));
            double y = (Double) users.getProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.Y"));
            double z = (Double) users.getProperty(
                MinorUtils.UserNodeChomp(player, arg, "coord.Z"));
            // no longer needed: final float p = new Float(users.getString(MinorUtils.UserNodeChomp(player, arg, "coord.pitch")));
            //  final float p = new Float("1");
            //  float yw = new Float(users.getString(MinorUtils.UserNodeChomp(player, arg, "coord.yaw")));
            Location l = new Location(w, x, y, z);
            player.teleport(l);
            player.sendMessage(ChatColor.BLUE +
                "[Waypoint] You have been teleported to '" + arg + "'.");
            return true;
        }
        else if (subc.equalsIgnoreCase("invite")) {
            if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.invite")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            }
            String point;
            try {
                point = (String) args[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED +
                    "Usage: /wp invite <playername> <point>");
                return true;
            }
            Player p = plugin.getServer().getPlayer((String)arg);
            if (p == null) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] Player " + arg + " is offline. Please resend the invitation when he/she is online.");
                return true;
            }
            if (MinorUtils.CheckPointExists(users, player, point) == false) {
                player.sendMessage(ChatColor.RED +
                    "You do not own a point named '" + point + "'.");
                return true;
            }
            ConfigurationNode node = users.getNode(MinorUtils.BaseNodeChomp(player, point));
            MinorUtils.TransferNode(users, MinorUtils.InviteNodeChomp(p, point), node);
            p.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + player.getName().toString() + " has invited you to use their waypoint '" + point + "'.");
            p.sendMessage(ChatColor.GREEN + "Type /wp accept " + point + " to accept their invite.");
            p.sendMessage(ChatColor.GREEN + "Or type /wp decline " + point + " to decline the invite.");
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Sent invite to " + p.getName().toString() + ".");
            users.save();
            return true;
        }
        else if (subc.equalsIgnoreCase("accept")) {
            if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.invite.accept")) {
                player.sendMessage(ChatColor.BLUE +
                    "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty(MinorUtils.InviteNodeChomp(player, arg)) == null) {
                player.sendMessage(ChatColor.RED +
                    "[Waypoint] You have no point invite by that name.");
                return true;
            }
            ConfigurationNode n = users.getNode(MinorUtils.InviteNodeChomp(player, arg));
            users.removeProperty(MinorUtils.InviteNodeChomp(player, arg));
            MinorUtils.TransferNode(users, MinorUtils.BaseNodeChomp(player, arg), n);
            player.sendMessage(ChatColor.AQUA +
                "[Waypoint] The point '" + arg + "' is now available in your collection.");
            users.save();
        }
        else if (subc.equalsIgnoreCase("decline")) {
            if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.invite.decline")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty(MinorUtils.InviteNodeChomp(player, arg)) == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] You have no point invite by that name.");
                return true;
            }
            users.removeProperty(
                MinorUtils.InviteNodeChomp(player, arg));
            player.sendMessage(ChatColor.AQUA +
                "[Waypoint] The point invitation '" + arg + "' has been declined.");
            users.save();
        }
        else if (subc.equalsIgnoreCase("list")) {
            if (!permissions.has(player, "waypoint.basic.list")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            if (users.getProperty("users." + player.getName().toString()) == null) {
                player.sendMessage(ChatColor.RED + "[Waypoint] You do not have any points to list.");
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "====[Waypoint] Point list:====");
            Map<String, ConfigurationNode> a = users.getNodes("users." + player.getName());
            if (a.size() == 0) {
                player.sendMessage(ChatColor.AQUA + " - No points have been created.");
                return true;
            }
            for (Map.Entry<String, ConfigurationNode> entry : a.entrySet()) {
                if (((String) configuration.getMain().getProperty("warp.list_world_only")).equals("true")
                    &&
                !(entry.getValue().getString("world").equals(player.getLocation().getWorld().getName().toString()))) {
                    continue;
                } else {
                    player.sendMessage(ChatColor.GREEN + " - " + entry.getKey());
                }
            }
            return true;
        }
        else if (subc.equalsIgnoreCase("test")) {
            if (!permissions.has(player, "waypoint.debug.config_node_test")) {
                return true;
            }
            // testing node crap
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();
            users.setProperty(
                MinorUtils.UserNodeChomp(player, "testnode", "coord.X"), x);
            users.setProperty(
                MinorUtils.UserNodeChomp(player, "testnode", "coord.Y"), y);
            users.setProperty(
                MinorUtils.UserNodeChomp(player, "testnode", "coord.Z"), z);
            users.save();
            ConfigurationNode n = users.getNode(MinorUtils.UserNodeChomp(player, "testnode", "coord"));
            MinorUtils.TransferNode(
                users,
                MinorUtils.UserNodeChomp(
                    player,
                    "testnode",
                    "coord"),
                n);
            users.save();
            users.removeProperty(
                MinorUtils.BaseNodeChomp(player, "testnode"));
            users.save();
            log.info("Configuration node nesting test successful.");
            return true;
        }
        else if (subc.equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            player.sendMessage(ChatColor.GREEN + "/wp:");
            player.sendMessage(ChatColor.RED + "   add - add a waypoint to your list.");
            player.sendMessage(ChatColor.RED + "   del - remove a waypoint from your list.");
            player.sendMessage(ChatColor.RED + "   tp - teleport to a waypoint in your list.");
            player.sendMessage(ChatColor.RED + "   list - list the waypoints in your list.");
            player.sendMessage(ChatColor.RED + "   help - this message.");
            return true;
        } else {
            player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            return true;
        };
        return true;
    };
};
