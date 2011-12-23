package me.pirogoeth.Waypoint.Util;

// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
// bukkit imports
import org.bukkit.util.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.Location;
// java utility imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Cooldown {
    public Waypoint plugin;
    public Logger log = Logger.getLogger("Minecraft");
    private Config configProvider;
    public Configuration configuration;
    private ArrayList<String> cooling = new ArrayList<String>();
    public long cooldown;

    public Cooldown (Waypoint instance) {
        /**
         * Cooldown constructor
         * @params: instance
         */
        this.plugin = instance;
        this.configProvider = instance.config;
        this.configuration = this.configProvider.getMain();
    }

    public long getDuration () {
        /**
         * getDuration()
         * @params: none
         * @returns (1): long
         */
        this.cooldown = this.configuration.getInt("cooldown.duration", 0); // defaults to 0 -- disabled state
        return (long)this.cooldown;
    }

    public void holdUser (Player player) {
        /**
         * holdUser()
         * @params (1): Player
         * @returns: none
         * @calls: this.runTimer((Player));
         */
        this.cooling.add(player.getName().toString());
        this.log(String.format("[Waypoint] User %s has been held", player.getName().toString()));
        this.runTimer(player);
    }

    public void unholdUser (Player player) {
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
        return this.cooling.contains(player.getName().toString());
    }

    public void runTimer (Player player) {
        /**
         * runTimer()
         * @params (1): Player
         * @returns: none
         * @calls: unholdUser((Player))
         */
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            new Runnable () {
                public void run () {
                    this.unholdUser(player);
                }
            }, this.getDuration());
    }
}