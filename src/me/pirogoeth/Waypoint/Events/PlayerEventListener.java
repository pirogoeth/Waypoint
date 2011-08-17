package me.pirogoeth.Waypoint.Events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockState;
import org.bukkit.ChatColor;
import java.util.logging.Logger;

import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Core.Warps;

public class PlayerEventListener extends PlayerListener {
    public static Waypoint plugin;
    public static Config config;
    public static Warps warpManager;
    public Permission permissions;
    Logger log = Logger.getLogger("Minecraft");
    public PlayerEventListener (Waypoint instance)
    {
        plugin = instance;
        permissions = plugin.permissions;
        config = plugin.config;
        warpManager = plugin.warpManager;
    }
    public void onPlayerInteract (PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        /**
         * Target IDs:
         * 63: Sign Post
         * 68: Wall sign
         */
        Block clicked_b = event.getClickedBlock();
        if (clicked_b == null) { return; }; // prevents spewage of NullPointerException everywhere
        if (clicked_b.getTypeId() == 63 || clicked_b.getTypeId() == 68)
        {
            Sign clicked_s = (Sign) clicked_b.getState();
            if (!((String) clicked_s.getLine(0)).equalsIgnoreCase("[Waypoint]"))
            {
                return;
            }
            String signtype = (String) clicked_s.getLine(1);
            String target = null;
            String target_o = null;
            String wrap = null;
            try {
                target = (String) clicked_s.getLine(2);
            }
            catch (java.lang.IndexOutOfBoundsException e)
            {
                target = null;
            }
            try {
                wrap = (String) clicked_s.getLine(3);
            }
            catch (java.lang.IndexOutOfBoundsException e)
            {
                wrap = null;
            }
            if (wrap != null)
            {
                target_o = target;
                target = target + wrap;
            }
            if (signtype.equalsIgnoreCase("warp") && target_o != null)
            {
                boolean result = warpManager.PlayerToWarp(player, target);
                if (result == false)
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The warp name listed on this sign is invalid.");
                    clicked_s.setLine(2, ChatColor.RED + target_o);
                    if (wrap != null)
                    {
                        clicked_s.setLine(3, ChatColor.RED + wrap);
                    }
                    clicked_s.update();
                    return;
                }
                return;
            }
            else if (signtype.equalsIgnoreCase("world") && target_o != null)
            {
                World world_t = plugin.getServer().getWorld(target);
                Location world_l = null;
                if (world_t == null)
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The world name listed on this sign is invalid.");
                    clicked_s.setLine(2, ChatColor.RED + target_o);
                    if (wrap != null)
                    {
                        clicked_s.setLine(3, ChatColor.RED + wrap);
                    }
                    clicked_s.update();
                    return;
                }
                else if (world_t != null)
                {
                    world_l = world_t.getSpawnLocation();
                }
                player.teleport(world_l);
                player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                return;
            }
            return;
        }
    }
}