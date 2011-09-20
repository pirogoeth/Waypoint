package me.pirogoeth.Waypoint.Util;

// Bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Block;
import org.bukkit.command.Command;
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

private class CommandException extends Exception {
    protected String trace;
    private CommandException () {
        super();
        trace = "Unknown sub-exception handled.";
    }

    private CommandException (String error) {
        super(error);
        trace = error;
    }

    private static String getError () {
        return trace;
    }
}

private class Command {
    private Map<String, Object> subcommands;
    private Logger log = Logger.getLogger("Minecraft");
    private Config configuration;
    private Permission permissions;
    private final Registry registry;
    private Waypoint plugin;
    private static boolean provisioned;

    // miscellaneous other variables for runtime
    private boolean registered;
    private String command;

    // constructor without command specification
    public Command(Waypoint instance) {
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

    protected static void setRootCommand (String cmd) {
        /**
         * This is only for in situations when the command has yet to be determined.
         * This command instance -MUST NOT- already be registered with the registry.
         */
        if (registered == true) {
            throw new CommandException("Command root cannot be set with the command already registered.");
        }
        command = cmd;
    }

    protected static boolean register () {
        /**
         * Register this command with the core registry and mark this
         * instance registered to prevent re-registration.
         */
        if (registered == true) {
            throw new CommandException("Command instance cannot be re-registered.");
        }
        registry.addEntry(command);
        registered = true;
    }

    public Set<String> getSubcommands () {
        /**
         * Returns a set of subcommands that have been added to this instantiations
         *    map of subs.
         */
        return subcommands.keySet();
    }

    public boolean run() {
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