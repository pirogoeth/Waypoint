package me.pirogoeth.Waypoint.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import me.pirogoeth.Waypoint.Waypoint;
import java.util.Map;
import java.util.ArrayList;

public class Limits {
    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint plugin;
    private Configuration source;

    // this constructor represents a limiter with a custom threshold.
    Limits(Waypoint instance, String path, Configuration input, int limit) {
        plugin = instance;
        base_path = path;
        threshold = limit;
        source = input;
    };
    // this constructor represents a limiter with the default threshold (10)
    Limits(Waypoint instance, String path, Configuration input) {
        plugin = instance;
        base_path = path;
        threshold = 10;
        source = input;
    };
    // this constructor represents a disabled limiter.
    Limits(Waypoint instance) {
        plugin = instance;
        base_path = ".";
        threshold = 0;
    };
    public boolean isEnabled () {
        if (base_path.equals("."))
            return false;
        else if (!(base_path.equals(".")))
            return true;
        else
            return false;
    };
    public boolean playerReachedLimit (Player player) {
        Map<String, ConfigurationNode> node_map = source.getNodes(String.format("%s.%s", base_path, player.getName().toString()));
        if (node_map.size() < threshold)
            return false;
        else if (node_map.size() == threshold)
            return true;
        else if (node_map.size() > threshold)
            return true;
        else
            return true;
    };
};