package me.pirogoeth.Waypoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.util.config.Configuration;

public class WaypointPermission {

    private Waypoint plugin;
    // public static Permissions permissions;
    private static HandlerType handler = HandlerType.OP;
    private static PermissionHandler permissionPlugin;

    public WaypointPermission (Waypoint instance)
    {
        plugin = instance;
        Logger log = Logger.getLogger("Minecraft");
        Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (permissions != null)
        {
            permissionPlugin = ((Permissions)permissions).getHandler();
            handler = HandlerType.PERMISSIONS;
            log.info("[Waypoint] Permissions plugin detected. Using " + permissions.getDescription().getFullName());
        }
        else
        {
            log.info("[Waypoint] No Permissions plugin detected. Using OP");
        }
    }
    private enum HandlerType {
        PERMISSIONS,
        OP
    }
    public static boolean has (Player p, String node)
    {
        switch (handler) {
            case PERMISSIONS:
                return permissionPlugin.has(p, node);
            case OP:
                return p.isOp();
        }
        return true;
    }
    private static boolean hasPermission (Player p, String node, boolean def)
    {
        switch (handler) {
            case PERMISSIONS:
                return permissionPlugin.has(p, node);
            case OP:
                return def ? true : p.isOp();
        }
        return def;
    }
}