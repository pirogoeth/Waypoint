package me.pirogoeth.Waypoint.Core;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Cooldown;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class Warps {
    public static Waypoint plugin;
    public static Config c;
    public static Configuration config;
    public static Configuration main;
    public static Cooldown cooldownManager;
    public static Logger log = Logger.getLogger("Minecraft");
    public List<String> groups;

    public Warps(Waypoint instance) {
        plugin = instance;
        c = plugin.config;
        cooldownManager = instance.getCooldownManager();
        config = Config.getWarp();
        main = Config.getMain();
    }

    public void LoadGroups() {
        this.groups = Config.getMain().getStringList("warp.permissions", null);
        Iterator i = this.groups.iterator();
        StringBuffer s = new StringBuffer((String)i.next());
        while (i.hasNext()) s.append(", ").append((String)i.next());
        log.info(String.format("[Waypoint] Warps: loaded permission groups: %s", new Object[] { s.toString() }));
    }

    public String WarpNode(String warpname, String subnode) {
        String a = "warps." + warpname + "." + subnode;
        return a;
    }

    public String WarpBase(String warpname) {
        String a = "warps." + warpname;
        return a;
    }

    public boolean checkperms(Player p, String pnode) {
        if (Permission.has(p, "waypoint.warp.access.*")) return true;
        String permission = String.format("waypoint.warp.access.%s", new Object[] { pnode });
        return Permission.has(p, permission);
    }

    public void CreateWarp(Player p, String warpname) {
        Location l = p.getLocation();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        String worldname = p.getWorld().getName().toString();
        String owner = p.getName().toString();
        config.setProperty(WarpNode(warpname, "coord.X"), Double.valueOf(x));
        config.setProperty(WarpNode(warpname, "coord.Y"), Double.valueOf(y));
        config.setProperty(WarpNode(warpname, "coord.Z"), Double.valueOf(z));
        config.setProperty(WarpNode(warpname, "world"), worldname);
        config.setProperty(WarpNode(warpname, "owner"), owner);
        String permission = (String)this.groups.get(0);
        config.setProperty(WarpNode(warpname, "permission"), permission);
        config.save();
    }

    public boolean DeleteWarp(String warpname) {
        if (config.getProperty(WarpNode(warpname, "world")) == null) {
              return false;
        }
        config.removeProperty(WarpBase(warpname));
        config.save();
        return true;
    }

    public boolean CheckGroup(String group) {
        return this.groups.contains(group);
    }

    public boolean SetWarpProp(String warpname, String key, String value) {
        if (config.getProperty(WarpNode(warpname, "world")) == null) {
              return false;
        }
        if (((key.equalsIgnoreCase("permission")) && (CheckGroup(value))) || (key.equalsIgnoreCase("owner"))) {
              config.setProperty(WarpNode(warpname, key), value);
              config.save();
              return true;
        }
        return (!key.equalsIgnoreCase("permission")) || (CheckGroup(value));
    }

    public boolean PlayerToWarp(Player p, String warpname) {
        if (config.getProperty(WarpNode(warpname, "world")) == null) {
            p.sendMessage(ChatColor.RED + "[Waypoint] Warp " + warpname + " does not exist.");
            return false;
        }
        String permission = (String)config.getProperty(WarpNode(warpname, "permission"));
        if (!checkperms(p, permission)) {
            p.sendMessage(ChatColor.RED + "[Waypoint] You do not have permissions to access this warp.");
            return true;
        }
        String worldname = (String)config.getProperty(WarpNode(warpname, "world"));
        if ((!p.getWorld().toString().equals(worldname)) && (((String)Config.getMain().getProperty("warp.traverse_world_only")).equals("true"))) {
            p.sendMessage(ChatColor.RED + "[Waypoint] You are not allowed to warp between worlds.");
            return true;
        }
        double x = ((Double)config.getProperty(WarpNode(warpname, "coord.X"))).doubleValue();
        double y = ((Double)config.getProperty(WarpNode(warpname, "coord.Y"))).doubleValue();
        double z = ((Double)config.getProperty(WarpNode(warpname, "coord.Z"))).doubleValue();
        World w = plugin.getServer().getWorld(worldname);
        if (!plugin.getServer().getWorlds().contains(w)) {
            p.sendMessage(String.format("%s[Waypoint] World %s (referenced from warp %s) does not exist.", ChatColor.RED, worldname, warpname));
            return true;
        }
        if (cooldownManager.checkUser(p)) {
            p.sendMessage(String.format("%s[Waypoint] Cooling down, please wait...", ChatColor.BLUE));
            return true;
        }
        Location l = new Location(w, x, y, z);
        p.teleport(l);
        if (((String)main.getString("warp.warpstring_enabled")).equalsIgnoreCase("true") || main.getBoolean("warp.warpstring_enabled", false) == true) {
            String warpstring = (String)main.getString("warp.string");
            if (warpstring == null) {
                log.warning("[Waypoint] Warp string is null! Please set the warp.string value in config.yml");
                return true;
            }
            warpstring = warpstring.replaceAll("%w", "" + warpname);
            warpstring = warpstring.replaceAll("%p", "" + p.getName().toString());
            p.sendMessage(String.format("%s%s", new Object[] { ChatColor.BLUE, warpstring }));
            return true;
        }
        return true;
    }

    public boolean RemoteWarp(Player target, Player sender, String warpname) {
        if (config.getProperty(WarpNode(warpname, "world")) == null) {
              sender.sendMessage(ChatColor.RED + "[Waypoint] Warp " + warpname + " does not exist.");
              return false;
        }
        String permission = (String)config.getProperty(WarpNode(warpname, "permission"));
        if (!checkperms(sender, permission)) {
              sender.sendMessage(ChatColor.RED + "[Waypoint] You do not have permissions to access this warp.");
              return true;
        }
        String worldname = (String)config.getProperty(WarpNode(warpname, "world"));
        if ((!target.getLocation().getWorld().toString().equals(worldname)) && (((String)c.getMain().getProperty("warp.traverse_world_only")).equals("true"))) {
              sender.sendMessage(ChatColor.RED + "[Waypoint] You are not allowed to warp others between worlds.");
              return true;
        }
        double x = ((Double)config.getProperty(WarpNode(warpname, "coord.X"))).doubleValue();
        double y = ((Double)config.getProperty(WarpNode(warpname, "coord.Y"))).doubleValue();
        double z = ((Double)config.getProperty(WarpNode(warpname, "coord.Z"))).doubleValue();
        World w = plugin.getServer().getWorld(worldname);
        Location l = new Location(w, x, y, z);
        target.teleport(l);
        sender.sendMessage(ChatColor.GREEN + String.format("[Waypoint] %s has been warped to %s", target.getName().toString(), warpname));
        if (((String)main.getProperty("warp.warpstring_enabled")).equalsIgnoreCase("true")) {
              String warpstring = (String)main.getProperty("warp.string");
              if (warpstring == null) {
                  log.warning("[Waypoint] Warp string is null! Please set the warp.string value in config.yml");
                  return true;
              }
              warpstring = warpstring.replaceAll("%w", "" + warpname);
              warpstring = warpstring.replaceAll("%p", "" + target.getName().toString());
              target.sendMessage(String.format("%s%s", ChatColor.BLUE, warpstring));
              return true;
        }
        return true;
    }
}
