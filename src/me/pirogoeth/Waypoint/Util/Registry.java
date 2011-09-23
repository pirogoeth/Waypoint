package me.pirogoeth.Waypoint.Util;

// Java imports
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;

// Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

// Core imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.RegistryException;

public class Registry {
    // important core registry variables.
    public Map<String, Object> command = new HashMap<String, Object>();
    public final Logger log = Logger.getLogger("Minecraft");
    public Waypoint plugin;
    // constructor
    public Registry (Waypoint instance) {
        plugin = instance;
    }
    // core registry utilities
    public Map<String, Object> getCommandMap () {
        /**
         * Returns the command map for raw outside manipulation.
         *
         * [accepts: none; returns: Map<String, Object>]
         */
        return command;
    }
    public boolean containsKey (String K) {
        /**
         * Checks if key K is in the command map.
         *
         * [accepts: (String) K; returns boolean]
         */
        return (Boolean) command.containsKey((String) K);
    }
    public boolean containsValue (Object V) {
        /**
         * Checks if value V exists in the command map.
         *
         * [accepts: (Object) V; returns boolean]
         */
        return (Boolean) command.containsValue((Object) V);
    }
    public int size () {
        /**
         * Returns the size of the command map.
         *
         * [accepts: none; returns: int]
         */
        return command.size();
    }
    public Object get (String K) {
        /**
         * Gets key-value item corresponding to K from the
         *     command map.
         *
         * [accepts: (Object) K; returns: (Object) V]
         */
        return (Object) command.get((String) K);
    }
    public Object put (String K, Object V) {
        /**
         * Puts a key-value pair into the command map.
         * This is not meant to be a replacement for the
         *     `register` method.
         *
         * [accepts: (Object) K, (Object) V;
         *      returns: previous V or null]
         */
        return (Object) command.put((String) K, (Object) V);
    }
    public Object remove (Object K) {
        /**
         * Removes K from the command map and returns
         *     the previous value for K, or null, if
         *     no such value existed.
         *
         * [accepts: (Object) K; returns: Object]
         */
        return (Object) command.remove((Object) K);
    }
    public boolean isEmpty () {
        /**
         * Returns whether or not the command map is empty.
         *     useful for sanity checking.
         *
         * [accepts: none; returns: boolean]
         */
        return command.isEmpty();
    }
    public Set<String> getCommandSet () {
        /**
         * Returns a Set which contains the keys of the
         *     command map.
         *
         * [accepts: none; returns: Set<String>]
         */
        return (Set<String>) command.keySet();
    }
    // core registration methods
    public boolean registerCommand (String commandLabel, Command commandInst)
      throws RegistryException {
        /**
         * Allows for the registering of a command in the
         *     registry's internal map to be process by the main onCommand method.
         *
         * [accepts: (String) commandLabel, (Command) commandInst; returns: boolean]
         */
        // is this command already registered?
        if (containsKey((String) commandLabel))
            throw new RegistryException(String.format("Command [%s] is already registered.", commandLabel));
        // does the map return null when we place the new value?
        if (put((String) commandLabel, (Object) commandInst) == null)
            return true;
        else
            return false;
    }
    public boolean deregisterCommand (String commandLabel)
      throws RegistryException {
        /**
         * Allows for the deregistration of a command from the
         *     registry's internal map.
         *
         * [accepts: (String) commandLabel; returns: boolean]
         */
        // is the command actually registered?
        if (!(containsKey((String) commandLabel)))
            throw new RegistryException(String.format("Command [%s] is not registered.", commandLabel));
        // does the map actually return a value for commandLabel, or does it return null?
        if (remove((String) commandLabel) == null || remove((String) commandLabel) != null)
            return true;
        else
            return false;
    }
    public void deregisterAll () {
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
    public Command process (String commandLabel) {
        /**
         * Processes the incoming command and returns the
         *     value for commandLabel, if it exists.
         *
         * [accepts: (String) commandLabel; returns: Command]
         */
        // does this key even exist?
        if (!(containsKey((String) commandLabel)))
            return null;
        else if (containsKey((String) commandLabel))
            return (Command) get((String) commandLabel);
        else
            return null;
    }
}