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
    // to be continued...
}