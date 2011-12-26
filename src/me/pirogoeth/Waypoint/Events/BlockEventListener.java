package me.pirogoeth.Waypoint.Events;

// java import
import java.util.logging.Logger;
// interal imports
import me.pirogoeth.Waypoint.Core.Links;
import me.pirogoeth.Waypoint.Core.Warps;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Waypoint;
// bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class BlockEventListener extends BlockListener {
  public static Waypoint plugin;
  public static Config config;
  public static Warps warpManager;
  public static Links linkManager;
  public Permission permission;
  Logger log = Logger.getLogger("Minecraft");

  public BlockEventListener(Waypoint instance) {
    plugin = instance;
    this.permission = plugin.permissions;
    config = plugin.config;
    warpManager = plugin.warpManager;
    linkManager = plugin.linkManager;
  }

  public void onSignChange(SignChangeEvent event) {
    Player player = event.getPlayer();
    Sign sign = (Sign)event.getBlock().getState();
    Location sign_l = sign.getBlock().getLocation();
    if (!event.getLine(0).equalsIgnoreCase("[Waypoint]")) return;
    if (!Permission.has(player, "waypoint.sign.link.create")) {
        player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to create a link sign.");
        int id = new Integer("63").intValue();
        ItemStack sign_st = new ItemStack(id);
        sign.getBlock().setTypeId(0);
        sign_l.getWorld().dropItemNaturally(sign_l, sign_st);
        return;
    }
    if (!event.getLine(1).split("\\:")[0].equalsIgnoreCase("link")) return;
    if (event.getLine(1).split("\\:")[0].equalsIgnoreCase("link")) {
      linkManager.CreateLink(player, event.getBlock(), (String[])event.getLines());
    }
  }

  public void onBlockBreak(BlockBreakEvent event) {
    if (((event.getBlock().getTypeId() == 63) || (event.getBlock().getTypeId() == 68)) && (((Sign)event.getBlock().getState()).getLine(0).equalsIgnoreCase("[Waypoint]"))) {
      Sign sign = (Sign)event.getBlock().getState();
      if ((sign.getLine(1).split("\\:")[0].equalsIgnoreCase("link")) && (Permission.has(event.getPlayer(), "waypoint.sign.link.delete"))) {
        linkManager.DeleteSign((Sign)event.getBlock().getState(), (String[])((Sign)event.getBlock().getState()).getLines());
        return;
      }
      if ((sign.getLine(1).split("\\:")[0].equalsIgnoreCase("link")) && (Permission.has(event.getPlayer(), "waypoint.sign.link.delete")) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "[Waypoint] You are not allowed to break this sign!");
        return;
      }
    }
  }
}
