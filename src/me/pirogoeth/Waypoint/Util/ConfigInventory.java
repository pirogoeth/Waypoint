package me.pirogoeth.Waypoint.Util;

// bukkit imports
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
// java imports
import java.util.Map;
import java.util.HashMap;
// internals imports
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Waypoint;

public enum ConfigInventory {
    MAIN(Config.main),

    WARP(Config.warps),

    USERS(Config.users),

    SPAWN(Config.spawn),

    HOME(Config.home),

    WORLD(Config.world),

    LINKS(Config.links);

    public final Configuration config;
    private final static Map<ConfigInventory, Configuration> store = new HashMap<ConfigInventory, Configuration>();

    ConfigInventory (final Configuration config) {
        this.config = config;
    }

    public Configuration getConfig () {
        return this.config;
    }

    public static Configuration getByConstant (final ConfigInventory ci) {
        return store.get(ci);
    }

    static {
        for (ConfigInventory ci : ConfigInventory.values()) {
            store.put(ci, ci.getConfig());
        }
    }
}
