package me.pirogoeth.waypoint.util;

// Bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

// Java imports
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

// Core imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Permission;
import me.pirogoeth.waypoint.util.Registry;
import me.pirogoeth.waypoint.util.Config;
import me.pirogoeth.waypoint.util.PlayerUtil;
import me.pirogoeth.waypoint.util.CommandException;
import me.pirogoeth.waypoint.util.RegistryException;

public class Command {
    public Logger log = Logger.getLogger("Minecraft");
    public Config configuration;
    public Permission permissions;
    public Registry registry;
    public Waypoint plugin;

    // miscellaneous other variables for runtime
    public boolean registered = false;
    public String[] aliases;
    public String description = "";
    public String usage = "";
    public String[] permissions;

    // constructor without command specification
    public Command (Waypoint instance) {
        plugin = instance;
        permissions = plugin.permissions;
        configuration = plugin.config;
        registry = plugin.registry;
        command = null;
    };

    // constructor with command specification
    public Command (Waypoint instance, String command_root) {
        plugin = instance;
        permissions = plugin.permissions;
        configuration = plugin.config;
        registry = plugin.registry;
        command = command_root;
    };

    // methods
    public void addAlias (String alias) {
        /**
         * Registers an alias for the command.
         */
        this.aliases += alias;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public boolean isRegistered () {
        /**
         * Returns whether or not this command is already in the global registry.
         */
        return (Boolean) registered;
    }

    public boolean register ()
      throws CommandException {
        /**
         * Register this command with the core registry and mark this
         * instance registered to prevent re-registration.
         */
        if (registered == true) {
            throw new CommandException("Command instance cannot be re-registered.");
        }
        try {
            registry.registerCommand(new DynamicPluginCommand(
                this.getAliases(), this.description,
                this.usage, this.plugin, this), this
            );
        } catch (java.lang.Exception e) {
            log.info("[Waypoint{Registry}] Could not register command '" + command + "'.");
            registered = false;
            return false;
        }
        registered = true;
        return true;
    }

    public boolean run (Player player, String[] args)
      throws CommandException {
        /**
         * This is where the code to be run when this command is process will be placed.
         * This must be overridden in another class file containing the command definition.
         * This method must not process subcommands, subcommands (eg., /<root> <subc>) must be processed with
         *     subcommand handler.
         */
        if (registered == false) {
            throw new CommandException("Command is not registered.");
        }
        return false;
    }
}
