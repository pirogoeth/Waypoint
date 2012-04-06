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
<<<<<<< HEAD
    public final static ChatColor prefix_colour = ChatColor.BLUE;
=======
    public final static ChatColor prefix-colour = ChatColor.BLUE;
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d

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
<<<<<<< HEAD
        String logged = String.format("%s[%s-debug] %s", prefix_colour, prefix, message);
=======
        String logged = String.format("%s[%s-debug] %s", prefix-colour, prefix, message);
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
        log.info(logged);
    }

    // normal methods to wrap Logger

    public static void info (String message) {
<<<<<<< HEAD
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
=======
        String logged = String.format("%s[%s] %s", prefix-colour, prefix, message);
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
        log.info(logged);
    }

    public static void severe (String message) {
<<<<<<< HEAD
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
=======
        String logged = String.format("%s[%s] %s", prefix-colour, prefix, message);
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
        log.severe(logged);
    }

    public static void warning (String message) {
<<<<<<< HEAD
        String logged = String.format("%s[%s] %s", prefix_colour, prefix, message);
        log.warning(logged);
    }
}
=======
        String logged = String.format("%s[%s] %s", prefix-colour, prefix, message);
        log.warning(logged);
    }
}
>>>>>>> 565fb9551038c736793147bbcfb4fd1ac63f3d5d
