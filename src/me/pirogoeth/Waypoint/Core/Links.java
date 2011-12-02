package me.pirogoeth.Waypoint.Core;

import java.util.logging.Logger;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class Links
{
  public Waypoint plugin;
  public Logger log = Logger.getLogger("Minecraft");
  protected Permission permission;
  public Config config;
  public Configuration links;

  public Links(Waypoint instance)
  {
    this.plugin = instance;
    this.config = this.plugin.config;
    this.permission = this.plugin.permissions;
    this.links = Config.getLinks(); } 
  public void CreateLink(Player player, Block sign_b, String[] lines) { BlockState sign_st = sign_b.getState();
    Sign sign = (Sign)sign_b.getState();

    sign.update();
    String network;
    String name;
    String target;
    try { network = lines[1];
      network = network.split("\\:")[1];
      name = lines[2];
      try {
        target = lines[3];
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        target = null;
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      int id = new Integer("63").intValue();
      sign.getBlock().setTypeId(0);
      ItemStack sign_d = new ItemStack(id);
      sign.getBlock().getLocation().getWorld().dropItemNaturally(sign.getBlock().getLocation(), sign_d);
      player.sendMessage(ChatColor.RED + "[Waypoint] Incorrect sign syntax.");

      e.printStackTrace();
      return;
    }
    Location sign_l = sign.getBlock().getLocation();
    if (this.links.getProperty(String.format("links.%s.%s", new Object[] { network, name })) != null)
    {
      player.sendMessage(ChatColor.BLUE + "[Waypoint] A sign in the network " + network + " already exists by this name.");
      return;
    }
    if (this.links.getProperty(String.format("links.%s.%s", new Object[] { network, name })) == null)
    {
      this.links.setProperty(String.format("links.%s.%s", new Object[] { network, name }), "");
      this.links.setProperty(String.format("links.%s.%s.coord.X", new Object[] { network, name }), Double.valueOf(sign_l.getX()));
      this.links.setProperty(String.format("links.%s.%s.coord.Y", new Object[] { network, name }), Double.valueOf(sign_l.getY()));
      this.links.setProperty(String.format("links.%s.%s.coord.Z", new Object[] { network, name }), Double.valueOf(sign_l.getZ()));
      this.links.setProperty(String.format("links.%s.%s.world", new Object[] { network, name }), sign_l.getWorld().getName().toString());
      if (target != null)
      {
        this.links.setProperty(String.format("links.%s.%s.target", new Object[] { network, name }), target);
      }
      else
      {
        this.links.setProperty(String.format("links.%s.%s.target", new Object[] { network, name }), null);
      }
      this.links.save();
      player.sendMessage(ChatColor.GREEN + String.format("[Waypoint] Sign %s has been created in network %s.", new Object[] { name, network }));
      return;
    } } 
  public void PlayerBetweenNetwork(Player player, Sign sign, String[] lines) {
    String network = lines[1].split("\\:")[1];
    String name = lines[2];
    String target;
    try {
      target = lines[3];
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      target = null;
    }
    if ((network == null) || (name == null) || (target == null))
    {
      return;
    }
    if ((network != null) && (name != null) && (target == null))
    {
      return;
    }
    if (this.links.getProperty(String.format("links.%s.%s.world", new Object[] { network, target })) == null)
    {
      player.sendMessage(ChatColor.BLUE + "[Waypoint] The target listed on the sign does not exist.");
      sign.setLine(3, ChatColor.RED + target);
      sign.update();
      return;
    }
    if (this.links.getProperty(String.format("links.%s.%s.world", new Object[] { network, target })) != null)
    {
      double target_x = Double.valueOf(this.links.getDouble(String.format("links.%s.%s.coord.X", new Object[] { network, target }), 1.0D)).doubleValue();
      double target_y = Double.valueOf(this.links.getDouble(String.format("links.%s.%s.coord.Y", new Object[] { network, target }), 90.0D)).doubleValue();
      double target_z = Double.valueOf(this.links.getDouble(String.format("links.%s.%s.coord.Z", new Object[] { network, target }), 1.0D)).doubleValue();
      World target_w = this.plugin.getServer().getWorld(this.links.getString(String.format("links.%s.%s.world", new Object[] { network, target })));
      Location target_l = new Location(target_w, target_x, target_y, target_z);
      player.teleport(target_l);
      player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported through the network " + ChatColor.LIGHT_PURPLE + network + ChatColor.GREEN + " to sign " + ChatColor.LIGHT_PURPLE + target + ChatColor.LIGHT_PURPLE + ".");
      return;
    }
  }

  public void DeleteSign(Sign sign, String[] lines)
  {
    if (lines[0] == null)
    {
      return;
    }
    if (lines[0].equalsIgnoreCase("[Waypoint]"))
    {
      String network = lines[1].split("\\:")[1];
      String name = lines[2];
      this.links.removeProperty(String.format("links.%s.%s", new Object[] { network, name }));
      Location sign_l = sign.getBlock().getLocation();
      int id = new Integer("63").intValue();
      ItemStack sign_d = new ItemStack(id);
      sign.getBlock().setTypeId(0);
      sign_l.getWorld().dropItemNaturally(sign_l, sign_d);
      return;
    }
  }
}

/* Location:           C:\Users\CJ\Desktop\Waypoint.jar
 * Qualified Name:     me.pirogoeth.Waypoint.Core.Links
 * JD-Core Version:    0.6.0
 */
