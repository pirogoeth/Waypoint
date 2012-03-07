package me.pirogoeth.waypoint.util;

// java imports
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

// bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.controller.Plugin;

// permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

// internal imports
import me.pirogoeth.waypoint.Waypoint;

public class Permission {

    /**
     * This is a static implementation of a plugin Permissions handler for checking permissions of users
     * and initialising support for specific plugins or different permission types.
     */

    private Waypoint controller;
    private static HandlerType handler = HandlerType.NONE;
    private static Plugin permissions;
    public static boolean loaded = false;

    public Permission (Waypoint instance) {
        controller = instance;
        Logger log = Logger.getLogger("Minecraft");
        log.info("[Waypoint] Searching for a suitable permissions plugin..");
        permissions = null;
        if (controller.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
            handler = HandlerType.PERMISSIONS_EX;
            permissions = controller.getServer().getPluginManager().getPlugin("PermissionsEx");
            loaded = true;
        } else if (controller.getServer().getPluginManager().isPluginEnabled("PermissionsBukkit")) {
            handler = HandlerType.SUPERPERMS;
            loaded = true;
        } else if (controller.getServer().getPluginManager().isPluginEnabled("bPermissions")) {
            handler = HandlerType.SUPERPERMS;
            loaded = true;
        } else if (controller.getServer().getPluginManager().isPluginEnabled("Permissions")) {
            handler = HandlerType.PERMISSIONS;
            permissions = controller.getServer().getPluginManager().getPlugin("Permissions");
            loaded = true;
        } else {
            handler = HandlerType.OP;
        }
        // process the handler type
        if (handler == HandlerType.PERMISSIONS) {
            log.info("[Waypoint] Permissions detected. Using [" + permissions.getDescription().getFullName() + "].");
        } else if (handler == HandlerType.PERMISSIONS_EX) {
            log.info("[Waypoint] Using PermissionsEx [" + permissions.getDescription().getVersion() + "].");
        } else if (handler == HandlerType.SUPERPERMS) {
            log.info("[Waypoint] Using Bukkit SuperPerms.");
        } else if (handler == HandlerType.OP) {
            log.info("[Waypoint] No Permissions controller detected. Using OP");
        }
    }

    private enum HandlerType {
        PERMISSIONS,
        PERMISSIONS_EX,
        SUPERPERMS,
        OP,
        NONE
    }

    public static boolean has (Player p, String node) {
        switch (handler) {
            case PERMISSIONS:
                return ((Permissions) permissions).getHandler().has(p, node);
            case PERMISSIONS_EX:
                return PermissionsEx.getPermissionManager().has(p, node);
            case OP:
                return p.isOp();
            case SUPERPERMS:
                return p.hasPermission(node);
            case NONE:
                return false;
        }
        return true;
    }

    public static boolean has (Player p, String node, boolean def) {
        switch (handler) {
            case PERMISSIONS:
                return ((Permissions) permissions).getHandler().has(p, node);
            case PERMISSIONS_EX:
                return PermissionsEx.getPermissionManager().has(p, node);
            case OP:
                return def ? true : p.isOp();
            case SUPERPERMS:
                return p.hasPermission(node);
            case NONE:
                return false;
        }
        return def;
    }
}
