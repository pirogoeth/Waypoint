package me.pirogoeth.Waypoint.Commands;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Waypoint;

public class CommandHandler
{
  public Waypoint plugin;
  private Command home;
  private Command setSpawn;
  private Command setWarp;
  private Command spawnAdmin;
  private Command spawnCommand;
  private Command teleLocation;
  private Command teleport;
  private Command teleportHere;
  private Command warp;
  private Command waypoints;
  private Command warpAdmin;
  private Command worldCommand;

  public CommandHandler(Waypoint instance)
  {
    this.plugin = instance;

    this.home = new Home(this.plugin);
    this.setSpawn = new SetSpawn(this.plugin);
    this.setWarp = new SetWarp(this.plugin);
    this.spawnAdmin = new SpawnAdmin(this.plugin);
    this.spawnCommand = new SpawnCommand(this.plugin);
    this.teleLocation = new TeleLocation(this.plugin);
    this.teleport = new Teleport(this.plugin);
    this.teleportHere = new TeleportHere(this.plugin);
    this.warp = new Warp(this.plugin);
    this.waypoints = new Waypoints(this.plugin);
    this.warpAdmin = new WarpAdmin(this.plugin);
    this.worldCommand = new WorldCommand(this.plugin);
  }
}
