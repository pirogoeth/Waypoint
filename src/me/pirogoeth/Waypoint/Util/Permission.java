package me.pirogoeth.Waypoint.Util;

// java imports
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

// bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

// internal imports
import me.pirogoeth.Waypoint.Waypoint;

public class Permission {

    private Waypoint plugin;
    private static HandlerType handler = HandlerType.NONE;
    private static Plugin permissions;
    public static boolean loaded = false;

    public Permission (Waypoint instance) {
        plugin = instance;
        Logger log = Logger.getLogger("Minecraft");
        log.info("[Waypoint] Searching for a suitable permissions plugin..");
        permissions = null;
        if (plugin.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
            handler = HandlerType.PERMISSIONS_EX;
            permissions = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            loaded = true;
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("PermissionsBukkit")) {
            handler = HandlerType.SUPERPERMS;
            loaded = true;
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("bPermissions")) {
            handler = HandlerType.SUPERPERMS;
            loaded = true;
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("Permissions")) {
            handler = HandlerType.PERMISSIONS;
            permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
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
            log.info("[Waypoint] No Permissions plugin detected. Using OP");
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
