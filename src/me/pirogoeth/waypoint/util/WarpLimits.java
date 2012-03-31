package me.pirogoeth.waypoint.util;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Limits;

// java imports
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WarpLimits {

    /**
     * This is a small implementation of warp limitation.
     */

    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint controller;
    private ConfigurationSection source;
    private Logger log = Logger.getLogger("Minecraft");

    // this constructor represents a limiter with a custom threshold.
    WarpLimits(Waypoint instance, String path, YamlConfiguration input, int limit) {
        this.controller = instance;
        this.base_path = path;
        this.source = input.getConfigurationSection(path);
        this.threshold = limit;
    };

    // this constructor represents a limiter with the default threshold (10)
    WarpLimits(Waypoint instance, String path, YamlConfiguration input) {
        this.controller = instance;
        this.base_path = path;
        this.source = input.getConfigurationSection(path);
        this.threshold = 10;
    };

    // this constructor represents a disabled limiter.
    WarpLimits(Waypoint instance) {
        this.controller = instance;
        this.base_path = ".";
    };

    public boolean isEnabled () {
        if (this.base_path.equals("."))
            return false;
        else if (!(this.base_path.equals(".")))
            return true;
        else
            return false;
    };

    public boolean playerReachedLimit (Player player) {
        if (this.isEnabled() == false) {
            return false;
        }
        Map<String, Object> node_map = this.source.getValues(true);
        int size = 0;
        try {
            for (Map.Entry<String, Object> entry : node_map.entrySet()) {
                if ((((ConfigurationSection) (entry.getValue())).getString("owner")).equals(player.getName().toString()))
                    size++;
                else
                    continue;
            }
        } catch (java.lang.NullPointerException e) {
            // this means that there are no entries in the warp config.
            return false;
        }
        if (size < this.threshold)
            return false;
        else if (size == this.threshold)
            return true;
        else if (size > this.threshold)
            return true;
        else
            return true;
    };
};
