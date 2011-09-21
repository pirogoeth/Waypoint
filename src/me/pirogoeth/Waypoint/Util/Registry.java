package me.pirogoeth.Waypoint.Util;

// Java imports
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Integer;
import java.util.logging.Logger;

// Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

// Core imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Command;

private class RegistryError extends Exception {
    protected String error;
    private RegistryError () {
        super();
        error = "An unknown error occurred.";
    }
    private RegistryError (String err) {
        super(err);
        error = err;
    }
    public static String getError () {
        return error;
    }
}

protected class Registry {
    // important core registry variables.
    private Map<String, Object> command;
    private final Logger log = Logger.getLogger("Minecraft");
    private Waypoint plugin;
    // constructor
    public Registry (Waypoint instance) {
        plugin = instance;
    }
    // core registry utilities
    private Map<String, Object> getCommandMap () {
        /**
         * Returns the command map for raw outside manipulation.
         *
         * [accepts: none; returns: Map<String, Object>]
         */
        return command;
    }
    public boolean containsKey (Object K) {
        /**
         * Checks if key K is in the command map.
         *
         * [accepts: (Object) K; returns boolean]
         */
        return (Boolean) command.containsKey((Object) K);
    }
    public boolean containsValue (Object V) {
        /**
         * Checks if value V exists in the command map.
         *
         * [accepts: (Object) V; returns boolean]
         */
        return (Boolean) command.containsValue((Object) V);
    }
    public static int size () {
        /**
         * Returns the size of the command map.
         *
         * [accepts: none; returns: int]
         */
        return (Integer) command.size();
    }
    public static Object get (Object K) {
        /**
         * Gets key-value item corresponding to K from the
         *     command map.
         *
         * [accepts: (Object) K; returns: (Object) V]
         */
        return (Object) command.get((Object) K);
    }
    protected static Object put (Object K, Object V) {
        /**
         * Puts a key-value pair into the command map.
         * This is not meant to be a replacement for the
         *     `register` method.
         *
         * [accepts: (Object) K, (Object) V;
         *      returns: previous V or null]
         */
        return (Object) command.put((Object) K, (Object) V);
    }
    private static Object remove (Object K) {
        /**
         * Removes K from the command map and returns
         *     the previous value for K, or null, if
         *     no such value existed.
         *
         * [accepts: (Object) K; returns: Object]
         */
        return (Object) command.remove((Object) K);
    }
    public static boolean isEmpty () {
        /**
         * Returns whether or not the command map is empty.
         *     useful for sanity checking.
         *
         * [accepts: none; returns: boolean]
         */
        return command.isEmpty();
    }
    private static Set<String> getCommandSet () {
        /**
         * Returns a Set which contains the keys of the
         *     command map.
         *
         * [accepts: none; returns: Set<String>]
         */
        return (Set<String>) command.keySet();
    }
    // core registration methods
    public static boolean registerCommand (String commandLabel, Command commandInst) {
        /**
         * Allows for the registering of a command in the
         *     registry's internal map to be process by the main onCommand method.
         *
         * [accepts: (String) commandLabel, (Command) commandInst; returns: boolean]
         */
        // is this command already registered?
        if (containsKey((Object) commandLabel))
            throw new RegistryError(String.format("Command [%s] is already registered.", commandLabel));
        // does the map return null when we place the new value?
        if (put((Object) commandLabel, (Object) commandInst) == null)
            return true;
        else
            return false;
    }
    public static boolean deregisterCommand (String commandLabel) {
        /**
         * Allows for the deregistration of a command from the
         *     registry's internal map.
         *
         * [accepts: (String) commandLabel; returns: boolean]
         */
        // is the command actually registered?
        if (!(containsKey((Object) commandLabel)))
            throw new RegistryError(String.format("Command [%s] is not registered.", commandLabel));
        // does the map actually return a value for commandLabel, or does it return null?
        if (remove((Object) commandLabel) == null || remove((Object) commandLabel != null)
            return true;
        else
            return false;
    }
    public static void deregisterAll () {
        /**
         * Allows for the deregistration of all registered
         *     commands in a quick and easy fashion.
         *
         * [accepts: none; returns: none]
         */
        // well, this is boring.
        command.clear();
        return;
    }
    public static Command processCommand (String commandLabel) {
        /**
         * Processes the incoming command and returns the
         *     value for commandLabel, if it exists.
         *
         * [accepts: (String) commandLabel; returns: Command]
         */
        // does this key even exist?
        if (!(containsKey((Object) commandLabel)))
            return null;
        else if (containsKey((Object) commandLabel))
            return (Command) get((Object) commandLabel);
        else
            return null;
    }
}