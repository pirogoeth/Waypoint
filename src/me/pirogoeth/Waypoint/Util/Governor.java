package me.pirogoeth.Waypoint.Util;

// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Limits;
import me.pirogoeth.Waypoint.Util.WarpLimits;
import me.pirogoeth.Waypoint.Util.Config;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

// java imports
import java.util.logging.Logger;

public class Governor {
    private Waypoint plugin;
    public Config configuration;
    public Limits wpLimits;
    public WarpLimits warpLimits;
    public Logger log;

    // constructor
    public Governor (Waypoint instance) {
        this.plugin = instance;
        this.log = Logger.getLogger("Minecraft");
        this.configuration = instance.config;
    }

    public void initialiseLimits () {
        /**
         * Base paths for Limits constructors:
         *
         * Waypoints: users. || Configuration: this.configuration.getUsers()
         * Warps: warps. || Configuration: this.configuration.getWarps()
         */
        Configuration main = this.configuration.getMain();
        boolean wp_limits_enabled = main.getBoolean("limits.waypoint.enabled", false);
        boolean warp_limits_enabled = main.getBoolean("limits.warp.enabled", false);
        if (wp_limits_enabled == true) {
            int wp_threshold = main.getInt("limits.waypoint.threshold", 10);
            String wp_path = "users";
            this.wpLimits = new Limits(this.plugin, wp_path, this.configuration.getUsers(), wp_threshold);
            this.log.info(String.format("[Waypoint] Limits enabled with threshold %d", wp_threshold));
        } else if (wp_limits_enabled == false) {
            this.wpLimits = new Limits(this.plugin);
            this.log.info(String.format("[Waypoint] Limits are disabled."));
        }
        if (warp_limits_enabled == true) {
            int warp_threshold = main.getInt("limits.warp.threshold", 10);
            String warp_path = "warps";
            this.warpLimits = new WarpLimits(this.plugin, warp_path, this.configuration.getWarp(), warp_threshold);
            this.log.info(String.format("[Waypoint] WarpLimits enabled with threshold %d", warp_threshold));
        } else if (warp_limits_enabled == false) {
            this.warpLimits = new WarpLimits(this.plugin);
            this.log.info(String.format("[Waypoint] WarpLimits are disabled."));
        };
    }

    public Limits getWaypoint () {
        /**
         * Returns the instantiated Limits() thing.
         */

        return this.wpLimits;
    };

    public WarpLimits getWarp() {
        /**
         * Returns the instantiated WarpLimits() thing.
         */

        return this.warpLimits;
    };
};