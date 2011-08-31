package me.pirogoeth.Waypoint.Core;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;

public class Links {
    public Waypoint plugin;
    public Logger log = Logger.getLogger("Minecraft");
    protected Permission permission;
    public Config config;
    public Configuration links;
    public Links (Waypoint instance)
    {
        plugin = instance;
        config = plugin.config;
        permission = plugin.permissions;
        links = config.getLinks();
    }
    public void CreateLink (Player player, Block sign_b, String[] lines)
    {
        BlockState sign_st = (BlockState) sign_b.getState();
        Sign sign = (Sign) sign_b.getState();
        // String[] lines = ((Sign) sign_b.getState()).getLines();
        sign.update();
        String network;
        String name;
        String target;
        try {
            network = lines[1];
            network = network.split("\\:")[1];
            name = lines[2];
            try {
                target = lines[3];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e)
            {
                target = null;
            }
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e)
        {
            final int id = new Integer("63");
            sign.getBlock().setTypeId(0);
            ItemStack sign_d = new ItemStack(id);
            sign.getBlock().getLocation().getWorld().dropItemNaturally(sign.getBlock().getLocation(), sign_d);
            player.sendMessage(ChatColor.RED + "[Waypoint] Incorrect sign syntax.");
            // debug
            e.printStackTrace();
            return;
        }
        Location sign_l = sign.getBlock().getLocation();
        if (links.getProperty(String.format("links.%s.%s", network, name)) != null)
        {
            player.sendMessage(ChatColor.BLUE + "[Waypoint] A sign in the network " + network + " already exists by this name.");
            return;
        }
        else if (links.getProperty(String.format("links.%s.%s", network, name)) == null)
        {
            links.setProperty(String.format("links.%s.%s", network, name), "");
            links.setProperty(String.format("links.%s.%s.coord.X", network, name), sign_l.getX());
            links.setProperty(String.format("links.%s.%s.coord.Y", network, name), sign_l.getY());
            links.setProperty(String.format("links.%s.%s.coord.Z", network, name), sign_l.getZ());
            links.setProperty(String.format("links.%s.%s.world", network, name), sign_l.getWorld().getName().toString());
            if (target != null)
            {
                links.setProperty(String.format("links.%s.%s.target", network, name), target);
            }
            else
            {
                // this is an endpoint. bidirection tele is not allowed
                links.setProperty(String.format("links.%s.%s.target", network, name), null);
            }
            links.save();
            player.sendMessage(ChatColor.GREEN + String.format("[Waypoint] Sign %s has been created in network %s.", name, network));
            return;
        }
    }
    public void PlayerBetweenNetwork (Player player, Sign sign, String[] lines)
    {
        String network = lines[1].split("\\:")[1];
        String name = lines[2];
        String target;
        try {
            target = lines[3];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e)
        {
            target = null;
        }
        if (network == null || name == null || target == null)
        {
            // a PlayerInteractEvent was passed while a block was being broken.
            return;
        }
        else if((network != null && name != null) && target == null)
        {
            // teleportation from this sign is not allowed. (see also: endpoint)
            return;
        }
        if (links.getProperty(String.format("links.%s.%s.world", network, target)) == null)
        {
            player.sendMessage(ChatColor.BLUE + "[Waypoint] The target listed on the sign does not exist.");
            sign.setLine(3, ChatColor.RED + target);
            sign.update();
            return;
        }
        else if (links.getProperty(String.format("links.%s.%s.world", network, target)) != null)
        {
            double target_x = (Double) links.getDouble(String.format("links.%s.%s.coord.X", network, target), 1);
            double target_y = (Double) links.getDouble(String.format("links.%s.%s.coord.Y", network, target), 90);
            double target_z = (Double) links.getDouble(String.format("links.%s.%s.coord.Z", network, target), 1);
            World target_w = plugin.getServer().getWorld((String) links.getString(String.format("links.%s.%s.world", network, target)));
            Location target_l = new Location(target_w, target_x, target_y, target_z);
            player.teleport(target_l);
            player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported through the network " + ChatColor.LIGHT_PURPLE + network + ChatColor.GREEN + " to sign " + ChatColor.LIGHT_PURPLE + target + ChatColor.LIGHT_PURPLE + ".");
            return;
        }
    }
    public void DeleteSign (Sign sign, String[] lines)
    {
        Location sign_l;
        if (lines[0] == null)
        {
            // this has to be here just because some blocks get broken and this is called twice...?
            // easy enough
            return;
        }
        else if (lines[0].equalsIgnoreCase("[Waypoint]"))
        {
            String network = lines[1].split("\\:")[1];
            String name = lines[2];
            links.removeProperty(String.format("links.%s.%s", network, name));
            sign_l = sign.getBlock().getLocation();
            final int id = new Integer("63");
            ItemStack sign_d = new ItemStack(id);
            sign.getBlock().setTypeId(0);
            sign_l.getWorld().dropItemNaturally(sign_l, sign_d);
            return;
        }
    }
}