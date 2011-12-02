package me.pirogoeth.Waypoint.Commands;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Waypoint;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

class Spawn extends Command
{
  public Configuration main;
  public Configuration spawn;

  public Spawn(Waypoint instance)
  {
    super(instance);
    this.main = Config.getMain();
    this.spawn = Config.getSpawn();
    try {
      setCommand("spawn");
      addAlias("wpspawn");
      register();
    } catch (CommandException e) {
      e.printStackTrace();
    }
  }

  public boolean run(Player player, String[] args)
    throws CommandException
  {
    if (!registered) {
      throw new CommandException("Command is not registered.");
    }
    if (!Permission.has(player, "waypoint.spawn")) {
      player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
      return true;
    }
    String subc = "";
    try {
      subc = args[0];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      World w = player.getWorld();
      this.plugin.spawnManager.SendPlayerToSpawn(w, player);
      return true;
    }
    World w = this.plugin.getServer().getWorld(subc);
    if (w == null)
    {
      player.sendMessage("[Waypoint] World '" + subc + "' does not exist.");
      return true;
    }
    this.plugin.spawnManager.SendPlayerToSpawn(w, player);
    return true;
  }
}
