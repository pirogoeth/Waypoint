package me.pirogoeth.waypoint.util;

// bukkit imports
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

// java imports
import java.util.Map;
import java.util.HashMap;

// internals imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Config;

public enum ConfigInventory {

    MAIN(Config.main),

    WARP(Config.warps),

    USERS(Config.users),

    SPAWN(Config.spawn),

    HOME(Config.home),

    WORLD(Config.world),

    LINKS(Config.links);

    public final YamlConfiguration config;
    private final static Map<ConfigInventory, YamlConfiguration> store = new HashMap<ConfigInventory, YamlConfiguration>();

    ConfigInventory (final YamlConfiguration config) {
        this.config = config;
    }

    public YamlConfiguration getConfig () {
        return this.config;
    }

    public static YamlConfiguration getByConstant (final ConfigInventory ci) {
        return store.get(ci);
    }

    static {
        for (ConfigInventory ci : ConfigInventory.values()) {
            store.put(ci, ci.getConfig());
        }
    }
}
