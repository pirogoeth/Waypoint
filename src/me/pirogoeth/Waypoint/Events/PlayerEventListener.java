package me.pirogoeth.Waypoint.Events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockState;
import org.bukkit.ChatColor;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Core.Warps;
import me.pirogoeth.Waypoint.Core.Links;

public class PlayerEventListener extends PlayerListener {
    public static Waypoint plugin;
    public static Config config;
    public static Warps warpManager;
    public static Links linkManager;
    public Permission permissions;
    Logger log = Logger.getLogger("Minecraft");
    public PlayerEventListener (Waypoint instance)
    {
        plugin = instance;
        permissions = plugin.permissions;
        config = plugin.config;
        warpManager = plugin.warpManager;
        linkManager = plugin.linkManager;
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
        if ((clicked_b.getTypeId() == 63 || clicked_b.getTypeId() == 68) && event.getAction() == Action.RIGHT_CLICK_BLOCK)
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
            if (signtype.split("\\:")[0].equalsIgnoreCase("link"))
            {
                if (!permissions.has(player, "waypoint.sign.link.use"))
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                    return;
                }
                try {
                    linkManager.PlayerBetweenNetwork(player, clicked_s, (String[]) ((Sign) clicked_b.getState()).getLines());
                }
                catch (NullPointerException e) {
                    // this block is probably being broken right now.
                    return;
                }
                return;
            }
            try {
                target = (String) clicked_s.getLine(2);
            }
            catch (java.lang.IndexOutOfBoundsException e)
            {
                target = null;
            }
            target_o = target;
            target = ((String) target).replaceAll("\\p{Cntrl}", "");;
            if (signtype.equalsIgnoreCase("warp") && target_o != null)
            {
                if (!permissions.has(player, "waypoint.sign.warp"))
                {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                boolean result = warpManager.PlayerToWarp(player, target);
                if (result == false)
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The warp name listed on this sign is invalid.");
                    final int id = new Integer("63");
                    ItemStack sign_dr = new ItemStack(id);
                    clicked_b.setTypeId(0);
                    clicked_b.getLocation().getWorld().dropItemNaturally(clicked_b.getLocation(), sign_dr);
                    return;
                }
                return;
            }
            else if (signtype.equalsIgnoreCase("world") && target_o != null)
            {
                if (!permissions.has(player, "waypoint.sign.world"))
                {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                World world_t = plugin.getServer().getWorld(target);
                Location world_l = null;
                if (world_t == null)
                {
                    // try a match in the worlds list.
                    List<World> wlist = plugin.getServer().getWorlds();
                    Iterator witer = wlist.iterator();
                    World w;
                    while (witer.hasNext())
                    {
                        w = (World) witer.next();
                        if (w.getName().toString().contains(target))
                        {
                            target = w.getName().toString();
                            world_t = plugin.getServer().getWorld(target);
                            player.teleport(world_t.getSpawnLocation());
                            player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                            return;
                        }
                    }
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The world name listed on this sign is invalid.");
                    final int id = new Integer("63");
                    ItemStack sign_dr = new ItemStack(id);
                    clicked_b.setTypeId(0);
                    clicked_b.getLocation().getWorld().dropItemNaturally(clicked_b.getLocation(), sign_dr);
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