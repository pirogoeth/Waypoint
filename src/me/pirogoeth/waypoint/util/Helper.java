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
import org.bukkit.World
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Helper {

    private final LogHandler log = new LogHandler();
    private final Server server;

    private List<String> warp_groups = ConfigInventory.MAIN.getStringList(
        "groups");

    public Helper(Server server) {
        this.server = server;
    }

    // warp helper methods
    public void loadWarpGroups() {
        Iterator i = this.warp_groups.iterator();
        StringBuffer s = new StringBuffer((String) i.next());
        while (i.hasNext()) {
            s.append(", ").append((String) i.next());
        }
        this.log.info(String.format("Permission groups loaded: %s", s.toString()));
    };

    public Location getWarp(final String warpname) {
        Location l = ConfigInventory.WARPS.getConfig().get(
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
            warpstring = warpstring.replaceAll("%lX", loc.getX());
            warpstring = warpstring.replaceAll("%lY", loc.getY());
            warpstring = warpstring.replaceAll("%lZ", loc.getZ());
            warpstring = warpstring.replaceAll("%W", loc.getWorld().getName());
            return warpstring;
        } else {
            return null;
        }
    };

    public void setWarp(final Player player, final String warpname,
        final Location loc) {

        // set location
        ConfigInventory.WARPS.set(String.format(
            "%s.location", warpname),
            loc
        );
        // set world
        ConfigInventory.WARPS.set(String.format(
            "%s.world", warpname),
            loc.getWorld().getName()
        );
        // set owner
        ConfigInventory.WARPS.set(String.format(
            "%s.owner", warpname),
            player.getName()
        );
        // set permission group
        ConfigInventory.WARPS.set(String.format(
            "%s.permission", warpname),
            (String) this.warp_groups.get(0)
        );
        return;
    };

    // world helper methods
    public void loadWorldProperties() {
        Map<String, ConfigurationSection> worlds = ConfigInventory.WORLD.getConfig().getConfigurationSection("worlds");
        if (worlds == null) {
            this.log.info("No world properties to load.");
            return;
        }
        List<World> worldlist = this.server.getWorlds();
        List<String> worldnames = new ArrayList<String>();
        Iterator worldlister = worldlist.iterator();
        while (worldlister.hasNext()) {
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
            else {
                this.log.info(String.format(
                    "Loaded world properties: %s { ENV: %s, MODE: %s, PVP: %s }",
                    worldname, env, Integer.toString(mode), Boolean.toString(pvp)
                ));
            }
        return;
    };
    
    // importWorld and its overloads
    
    // createWorld and its overloads
};