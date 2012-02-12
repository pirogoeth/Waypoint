package me.pirogoeth.Waypoint.Util;

// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.EconomyCost;
// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;
// java imports
import java.util.logging.Logger;
import java.lang.Double;
// vault imports
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class EconomyHandler {

    /**
     * This implements a handler for economy plugins inside Waypoint.
     */

    public Waypoint plugin;
    public static Config config;
    private Economy economy;
    private boolean enableable;
    private boolean enabled;
    private Logger log;

    public EconomyHandler (Waypoint instance) {
        /**
         * Constructs the basic properties for the handler to work.
         */

        this.plugin = instance;
        this.config = instance.config;
        this.log = Logger.getLogger("Minecraft");
        this.enableable = this.configured();
    }

    private static boolean configured () {
        /**
         * This checks the configuration values set to see if the economy functions are enableable.
         */

        boolean e = config.getMain().getBoolean("economy.enabled", false);
        return e;
    }

    public Economy getEconomy () {
        /**
         * Returns this economy (vault) instance.
         */

        return (Economy) this.economy;
    }

    public boolean setupEconomy () {
        /**
         * Handles the setup of Vault for Economy access.
         */

        if (!enableable) {
            this.log.info("[Waypoint] Economy support disabled.");
            return false;
        }
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.log.info("[Waypoint] Economy support disabled due to lack of the Vault plugin.");
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        this.economy = economyProvider.getProvider();
        this.enabled = true;
        return economy != null;
    }

    public boolean debitPlayer (String player, EconomyCost co) {
        /**
         * Wrapper for debitPlayer(String, double)
         */

        return this.debitPlayer(player, co.getDoubleValue());
    }

    public boolean debitPlayer (String player, double amount) {
        /**
         * Charges player the certain amount to perform a specific function.
         */

        if (this.enabled == false) {
            // this generally means that the economy support is not enabled in the config or the server is missing Vault.
            // lets go ahead and return true.
            this.log.info(String.format(
                "[Waypoint] Economy services are not enabled at this time.")); // debug
            return true;
        }
        if (this.economy.has(player, amount)) {
            // the player has the money, take it
            EconomyResponse response = this.economy.withdrawPlayer(player, amount);
            if (response.type == ResponseType.SUCCESS) {
                this.plugin.getServer().getPlayer(player).sendMessage(String.format(
                    "%s[Waypoint] Your balance has been debited %s.", ChatColor.GREEN, this.economy.format(response.amount)));
                return true;
            } else if (response.type == ResponseType.FAILURE) {
                this.plugin.getServer().getPlayer(player).sendMessage(String.format(
                    "%s[Waypoint] Transaction of %s has failed: %s", ChatColor.RED, this.economy.format(response.amount), response.errorMessage));
                return false;
            } else {
                // what else could've happened? O_O
                this.log.info(String.format(
                    "[Waypoint] Something went wrong during the transaction [%f from %s]; received %s.", response.amount, player, response.type.toString()));
                return false;
            }
        } else if (!(this.economy.has(player, amount))) {
            // the player's balance is too low.
            this.plugin.getServer().getPlayer(player).sendMessage(String.format(
                "%s[Waypoint] Your balance is too low to make this transaction. [%s required]", ChatColor.RED, this.economy.format(amount)));
            return false;
        }
        this.log.info(String.format(
            "[Waypoint] EconomyHander.debitPlayer() returned at end of method."));
        return true;
    }
}