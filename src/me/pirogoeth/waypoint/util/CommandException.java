package me.pirogoeth.waypoint.util;

import java.util.logging.Logger;

public class CommandException extends Exception {
    protected static String trace;
    public Logger log = Logger.getLogger("Minecraft");
    public CommandException () {
        super();
        trace = "Unknown sub-exception handled.";
        log.warning("[Waypoint{Command}] " + trace);
    }

    public CommandException (String error) {
        super(error);
        trace = error;
        log.warning("[Waypoint{Command}] " + trace);
    }

    public static String getError () {
        return trace;
    }
}