package me.pirogoeth.Waypoint.Util;

import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.World;

public class MinorUtils {
    public static String BaseNodeChomp(Player p, String node)
    {
        String a = "users." + p.getName().toString() + "." + node;
        return a;
    };
    public static String UserNodeChomp(Player p, String targetname, String sub)
    {
        String a = "users." + p.getName().toString() + "." + targetname + "." + sub;
        return a;
    };
    public static String HomeNodeChomp(Player p, World world, String sub)
    {
        String a = "home." + p.getName().toString() + "." + world.getName().toString() + "." + sub;
        return a;
    };
    public static String InviteNodeChomp(Player p, String node)
    {
        String a = "invites." + p.getName().toString() + "." + node;
        return a;
    };
    public static void TransferNode(Configuration users, String path, ConfigurationNode node)
    {
        Map<String, Object> a = node.getAll();
        for (Map.Entry<String, Object> entry : a.entrySet())
        {
            users.setProperty(path + "." + entry.getKey(), entry.getValue());
            // debug
            //  log.info("Key: " + entry.getKey() + " Value: " + (static String)entry.getValue().toString());
        }
        return;
    };
    public static boolean CheckPointExists(Configuration users, Player p, String point)
    {
        String a = "users." + p.getName().toString() + "." + point;
        if (users.getProperty(a + ".coord") == null) {
            return false;
        } else if (users.getProperty(a + ".coord") != null) {
            return true;
        };
        return true;
    };
};
