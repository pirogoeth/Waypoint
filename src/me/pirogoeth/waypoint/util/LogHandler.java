package me.pirogoeth.waypoint.util;

// java imports
import java.util.logging.Logger;
import java.lang.Boolean;

// bukkit imports
import org.bukkit.ChatColor;

public class LogHandler {
    // main
    public final static Logger log = Logger.getLogger("Minecraft");
    public final static String prefix = "Waypoint";
    public final static ChatColor prefix_colour = ChatColor.BLUE;

    // dynamic
    protected static boolean ex_debug = false;

    // construct
    public LogHandler () {
        // now handling logs.
    }

    // extraneous debug methods

    public static boolean getExtraneousDebugging () {
        return ex_debug;
    }

    public static void setExtraneousDebugging (boolean b) {
        ex_debug = b;
        info(String.format("Extraneous debug is set to %s.", Boolean.toString(ex_debug)));
    }

    public static void exDebug (String message) {
        if (!getExtraneousDebugging()) return;
        String logged = String.format("%s[%s-debug] %s", prefix_colour, prefix, message);
        log.info(logged);
    }

    // normal methods to wrap Logger

    public static void info (String message) {
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
        log.info(logged);
    }

    public static void severe (String message) {
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
        log.severe(logged);
    }

    public static void warning (String message) {
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
        log.warning(logged);
    }
}
