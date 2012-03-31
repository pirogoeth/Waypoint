package me.pirogoeth.waypoint.util;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Limits;
import me.pirogoeth.waypoint.util.WarpLimits;
import me.pirogoeth.waypoint.util.Config;
import me.pirogoeth.waypoint.util.ConfigInventory;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

// java imports
import java.util.logging.Logger;

public class Governor {

    private Waypoint controller;
    public Configuration configuration;
    public Limits wpLimits;
    public WarpLimits warpLimits;
    public Logger log;

    // constructor
    public Governor (Waypoint instance) {
        this.controller = instance;
        this.log = Logger.getLogger("Minecraft");
        this.configuration = ConfigInventory.MAIN.getConfig();
    }

    public void initialiseLimits () {
        /**
         * Base paths for Limits constructors:
         *
         * Waypoints: users. || Configuration: this.configuration.getUsers()
         * Warps: warps. || Configuration: this.configuration.getWarps()
         */
        boolean wp_limits_enabled = this.configuration.getBoolean("limits.waypoint.enabled", false);
        boolean warp_limits_enabled = this.configuration.getBoolean("limits.warp.enabled", false);
        if (wp_limits_enabled == true) {
            int wp_threshold = this.configuration.getInt("limits.waypoint.threshold", 10);
            String wp_path = "users";
            this.wpLimits = new Limits(this.controller, wp_path, ConfigInventory.USERS.getConfig(), wp_threshold);
            this.log.info(String.format("[Waypoint] Limits enabled with threshold %d", wp_threshold));
        } else if (wp_limits_enabled == false) {
            this.wpLimits = new Limits(this.controller);
            this.log.info(String.format("[Waypoint] Limits are disabled."));
        }
        if (warp_limits_enabled == true) {
            int warp_threshold = this.configuration.getInt("limits.warp.threshold", 10);
            String warp_path = "warps";
            this.warpLimits = new WarpLimits(this.controller, warp_path, ConfigInventory.WARP.getConfig(), warp_threshold);
            this.log.info(String.format("[Waypoint] WarpLimits enabled with threshold %d", warp_threshold));
        } else if (warp_limits_enabled == false) {
            this.warpLimits = new WarpLimits(this.controller);
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
