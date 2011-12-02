package me.pirogoeth.Waypoint.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Limits;
import java.util.Map;
import java.util.ArrayList;

public class WarpLimits extends Limits {
    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint plugin;
    private Configuration source;

    // this constructor represents a limiter with a custom threshold.
    WarpLimits(Waypoint instance, String path, Configuration input, int limit) {
        super(instance, path, input, limit);
    };
    // this constructor represents a limiter with the default threshold (10)
    WarpLimits(Waypoint instance, String path, Configuration input) {
        super(instance, path, input);
    };
    // this constructor represents a disabled limiter.
    WarpLimits(Waypoint instance) {
        super(instance);
    };
    @Override
    public boolean playerReachedLimit (Player player) {
        Map<String, ConfigurationNode> node_map = source.getNodes(base_path);
        int size = 0;
        for (Map.Entry<String, ConfigurationNode> entry : node_map.entrySet()) {
            if (((entry.getValue()).getString("owner")).equals(player.getName().toString()))
                size++;
            else
                continue;
        }
        if (size < threshold)
            return false;
        else if (size == threshold)
            return true;
        else if (size > threshold)
            return true;
        else
            return true;
    };
};
