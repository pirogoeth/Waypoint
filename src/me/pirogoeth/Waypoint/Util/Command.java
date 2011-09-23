package me.pirogoeth.Waypoint.Util;

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
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.PlayerUtil;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.RegistryException;

public class Command {
    public Logger log = Logger.getLogger("Minecraft");
    public Config configuration;
    public Permission permissions;
    public Registry registry;
    public Waypoint plugin;

    // miscellaneous other variables for runtime
    public static boolean registered;
    public static String command;
    public static Map<String, Object> subcommands;
    public static ArrayList<String> aliases = new ArrayList<String>();

    // constructor without command specification
    public Command (Waypoint instance) {
        plugin = instance;
        permissions = plugin.permissions;
        configuration = plugin.config;
        registry = plugin.registry;
        command = null;
    }

    // constructor with command specification
    public Command (Waypoint instance, String command_root) {
        plugin = instance;
        permissions = plugin.permissions;
        configuration = plugin.config;
        registry = plugin.registry;
        command = command_root;
    }

    // methods
    public void initSubcommands () {
        /**
         * Must be overridden!
         * This will initialise the subcommands and add them to +my+ subcommands map.
         */
        return;
    }

    public boolean addAlias (String alias) {
        /**
         * Registers an alias for the command.
         */
        try {
            if (registry.registerCommand(alias, this) == true)
                return aliases.add((String) alias);
        } catch (RegistryException e) {
            log.info("[Waypoint{Registry}] Could not register alias '" + alias + "'.");
            return false;
        }
        return false;
    }

    public boolean deleteAlias (String alias)
      throws CommandException {
        /**
         * Deregisters an alias for the command.
         */
        try {
            if (registry.deregisterCommand(alias) == true)
                return aliases.remove((String) alias);
            else
                throw new CommandException(String.format("Alias [%s] does not exist.", alias));
        } catch (RegistryException e) {
            log.info("[Waypoint{Registry}] Could not deregister alias '" + alias + "'.");
            return false;
        }
    }

    public boolean isRegistered () {
        /**
         * Returns whether or not this command is already in the global registry.
         */
        return (Boolean) registered;
    }

    public String getRootCommand () {
        /**
         * Returns the command that this instantiation is created for.
         */
        return (String) command;
    }

    public void setRootCommand (String cmd)
      throws CommandException {
        /**
         * This is only for in situations when the command has yet to be determined.
         * This command instance -MUST NOT- already be registered with the registry.
         */
        if (registered == true) {
            throw new CommandException("Command root cannot be set with the command already registered.");
        }
        command = cmd;
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
            registry.registerCommand(command, this);
        } catch (RegistryException e) {
            log.info("[Waypoint{Registry}] Could not register command '" + command + "'.");
            registered = false;
            return false;
        }
        registered = true;
        return true;
    }

    public boolean deregister ()
      throws CommandException {
        /**
         * Deregister this command from the core registry/
         */
        if (registered == false)
            throw new CommandException("Command instance is not registered.");
        try {
            registry.deregisterCommand(command);
        } catch (RegistryException e) {
            registered = true;
            return false;
        }
        registered = false;
        return true;
    }

    public Set<String> getSubcommands () {
        /**
         * Returns a set of subcommands that have been added to this instantiations
         *    map of subs.
         */
        return subcommands.keySet();
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