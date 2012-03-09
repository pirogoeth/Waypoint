package me.pirogoeth.waypoint.util;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Config;
import me.pirogoeth.waypoint.util.EconomyCost;
import me.pirogoeth.waypoint.util.LogHandler;

// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;

// java imports
import java.lang.Double;

// vault imports
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class EconomyHandler {

    /**
     * This implements a handler for economy controllers inside Waypoint.
     */

    public Waypoint controller;
    public static Config config;
    private Economy economy;
    private boolean enableable;
    private boolean enabled;
    private Logger log = new LogHandler();

    public EconomyHandler (Waypoint instance) {
        /**
         * Constructs the basic properties for the handler to work.
         */

        this.controller = instance;
        this.config = instance.config;
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
            this.log.info("Economy support disabled.");
            return false;
        }
        if (this.controller.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.log.info("Economy support disabled due to lack of the Vault controller.");
            return false;
        }
        RegisteredServiceProvider<Economy> economyProvider = this.controller.getServer().getServicesManager().getRegistration(Economy.class);
        this.economy = economyProvider.getProvider();
        this.enabled = true;
        return (economy != null);
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
                "%sEconomy services are not enabled at this time.", ChatColor.RED)); // debug
            return true;
        }
        if (this.economy.has(player, amount)) {
            // the player has the money, take it
            EconomyResponse response = this.economy.withdrawPlayer(player, amount);
            if (response.type == ResponseType.SUCCESS) {
                this.controller.getServer().getPlayer(player).sendMessage(String.format(
                    "%sYour balance has been debited %s.", ChatColor.GREEN, this.economy.format(response.amount)));
                return true;
            } else if (response.type == ResponseType.FAILURE) {
                this.controller.getServer().getPlayer(player).sendMessage(String.format(
                    "%sTransaction of %s has failed: %s", ChatColor.RED, this.economy.format(response.amount), response.errorMessage));
                return false;
            } else {
                // what else could've happened? O_O
                this.log.info(String.format(
                    "Something went wrong during the transaction [%f from %s]; received %s.", response.amount, player, response.type.toString()));
                return false;
            }
        } else if (!(this.economy.has(player, amount))) {
            // the player's balance is too low.
            this.controller.getServer().getPlayer(player).sendMessage(String.format(
                "%sYour balance is too low to make this transaction. [%s required]", ChatColor.RED, this.economy.format(amount)));
            return false;
        }
        this.log.exDebug(String.format(
            "EconomyHander.debitPlayer() returned at end of method."));
        return true;
    }
}
