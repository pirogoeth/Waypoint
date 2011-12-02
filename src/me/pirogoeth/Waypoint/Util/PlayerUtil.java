package me.pirogoeth.Waypoint.Util;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerUtil
{
  public static Player getPlayerFromSender(CommandSender sender)
  {
    Player p = (Player)sender;
    return p;
  }

  public static String getPlayerName(CommandSender sender) {
    Player p = (Player)sender;
    String n = p.getDisplayName();
    return n;
  }

  public static Location getPlayerLocation(CommandSender sender) {
    Player p = (Player)sender;
    Location l = p.getLocation();
    return l;
  }
}
