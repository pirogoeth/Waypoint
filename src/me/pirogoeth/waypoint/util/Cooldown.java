package me.pirogoeth.waypoint.util;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Config;

// bukkit imports
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.Location;

// java utility imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Cooldown {

    /**
     * This is a very small implementation of teleportation-event cooldowns.
     */
    public Waypoint controller;
    public Logger log;
    private Config configProvider;
    public Configuration configuration;
    private ArrayList<String> cooling = new ArrayList<String>();
    private boolean enabled;
    public long cooldown;

    public Cooldown (Waypoint instance) {
        /**
         * Cooldown constructor
         * @params: instance
         */
        this.controller = instance;
        this.log = Logger.getLogger("Minecraft");
        this.configProvider = instance.config;
        this.configuration = this.configProvider.getMain();
        this.enabled = this.configuration.getBoolean("cooldown.enabled", false);
    }

    public long getDuration () {
        /**
         * getDuration()
         * @params: none
         * @returns (1): long
         */
        this.cooldown = this.configuration.getInt("cooldown.duration", 0); // defaults to 0 -- disabled state
        return (long) (this.cooldown * 20);
    }

    public void holdUser (Player player) {
        /**
         * holdUser()
         * @params (1): Player
         * @returns: none
         * @calls: this.runTimer((Player));
         */
        if (!(this.enabled)) return;
        this.cooling.add(player.getName().toString());
        this.runTimer(player);
    }

    protected void releaseUser (Player player) {
        /**
         * unholdUser()
         * @params (1): Player
         * @returns: none
         */
        this.cooling.remove(player.getName().toString());
    }

    public boolean checkUser (Player player) {
        /**
         * checkUser()
         * @params (1): Player
         * @returns (1): boolean (true if in this.cooling, false if not in this.cooling)
         */
        if (!(this.enabled)) return false;
        return this.cooling.contains(player.getName().toString());
    }

    protected void runTimer (final Player player) {
        /**
         * runTimer()
         * @params (1): Player
         * @returns: none
         * @calls: unholdUser((Player))
         */
        this.controller.getServer().getScheduler().scheduleSyncDelayedTask(
            this.controller,
            new Runnable () {
                public void run () {
                    releaseUser(player);
                }
            }, this.getDuration());
    }
}
