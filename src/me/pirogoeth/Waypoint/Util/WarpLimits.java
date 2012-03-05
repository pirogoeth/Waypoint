package me.pirogoeth.Waypoint.Util;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Limits;
// java imports
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WarpLimits {
    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint plugin;
    private Configuration source;
    private Logger log = Logger.getLogger("Minecraft");

    // this constructor represents a limiter with a custom threshold.
    WarpLimits(Waypoint instance, String path, Configuration input, int limit) {
        this.plugin = instance;
        this.base_path = path;
        this.source = input;
        this.threshold = limit;
    };
    // this constructor represents a limiter with the default threshold (10)
    WarpLimits(Waypoint instance, String path, Configuration input) {
        this.plugin = instance;
        this.base_path = path;
        this.source = input;
        this.threshold = 10;
    };
    // this constructor represents a disabled limiter.
    WarpLimits(Waypoint instance) {
        this.plugin = instance;
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
        Map<String, ConfigurationSection> node_map = this.source.getNodes(this.base_path);
        int size = 0;
        try {
            for (Map.Entry<String, ConfigurationSection> entry : node_map.entrySet()) {
                if (((entry.getValue()).getString("owner")).equals(player.getName().toString()))
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
