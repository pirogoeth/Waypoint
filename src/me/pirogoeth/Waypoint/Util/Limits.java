package me.pirogoeth.Waypoint.Util;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
// internal improts
import me.pirogoeth.Waypoint.Waypoint;
// java imports
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.lang.Integer;

public class Limits {
    // this is the base path where our limiting will work.
    private String base_path;
    private int threshold;
    private Waypoint plugin;
    private Configuration source;
    private Logger log;

    // this constructor represents a limiter with a custom threshold.
    Limits(Waypoint instance, String path, Configuration input, int limit) {
        this.plugin = instance;
        this.base_path = path;
        this.threshold = limit;
        this.source = input;
        this.log = Logger.getLogger("Minecraft");
    };
    // this constructor represents a limiter with the default threshold (10)
    Limits(Waypoint instance, String path, Configuration input) {
        this.plugin = instance;
        this.base_path = path;
        this.threshold = 10;
        this.source = input;
        this.log = Logger.getLogger("Minecraft");
    };
    // this constructor represents a disabled limiter.
    Limits(Waypoint instance) {
        this.plugin = instance;
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
        if (node_map == null) { this.log.severe("Fuck configuration classes"); return false; };
        if (((int) node_map.size()) < threshold)
            return false;
        else if (((int) node_map.size()) == threshold)
            return true;
        else if (((int) node_map.size()) > threshold)
            return true;
        else
            return true;
    };
};