package me.pirogoeth.waypoint.util;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

// internal improts
import me.pirogoeth.waypoint.Waypoint;

// java imports
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.lang.Integer;

public class Limits {

    /**
     * This is a small implementation of waypoint limitation.
     */

    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint controller;
    private Configuration source;
    private Logger log;

    // this constructor represents a limiter with a custom threshold.
    Limits(Waypoint instance, String path, Configuration input, int limit) {
        this.controller = instance;
        this.base_path = path;
        this.threshold = limit;
        this.source = input;
        this.log = Logger.getLogger("Minecraft");
    };
    // this constructor represents a limiter with the default threshold (10)
    Limits(Waypoint instance, String path, Configuration input) {
        this.controller = instance;
        this.base_path = path;
        this.threshold = 10;
        this.source = input;
        this.log = Logger.getLogger("Minecraft");
    };
    // this constructor represents a disabled limiter.
    Limits(Waypoint instance) {
        this.controller = instance;
        this.base_path = ".";
        this.threshold = 0;
        this.log = Logger.getLogger("Minecraft");
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
        Map<String, ConfigurationNode> node_map = this.source.getNodes(String.format("%s.%s", this.base_path, player.getName().toString()));
        if (((int) node_map.size()) < threshold)
            return false;
        else if (((int) node_map.size()) == threshold)
            return true;
        else if (((int) node_map.size()) > threshold)
            return true;
        else if (node_map == null)
            return false;
        else
            return true;
    };
};
