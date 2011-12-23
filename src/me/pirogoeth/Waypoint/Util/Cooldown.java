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
    public Logger log;
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
        this.log = Logger.getLogger("Minecraft");
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
        return (long) (this.cooldown * 20);
    }

    public void holdUser (Player player) {
        /**
         * holdUser()
         * @params (1): Player
         * @returns: none
         * @calls: this.runTimer((Player));
         */
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
        return this.cooling.contains(player.getName().toString());
    }

    protected void runTimer (Player player_arg) {
        /**
         * runTimer()
         * @params (1): Player
         * @returns: none
         * @calls: unholdUser((Player))
         */
        final Player player = player_arg;
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            new Runnable () {
                public void run () {
                    releaseUser(player);
                }
            }, this.getDuration());
    }
}
