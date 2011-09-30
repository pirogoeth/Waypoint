package me.pirogoeth.Waypoint.Commands;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Commands.Home;
import me.pirogoeth.Waypoint.Commands.SetSpawn;
import me.pirogoeth.Waypoint.Commands.SetWarp;
import me.pirogoeth.Waypoint.Commands.SpawnAdmin;
import me.pirogoeth.Waypoint.Commands.SpawnCommand;
import me.pirogoeth.Waypoint.Commands.TeleLocation;
import me.pirogoeth.Waypoint.Commands.Teleport;
import me.pirogoeth.Waypoint.Commands.TeleportHere;
import me.pirogoeth.Waypoint.Commands.Warp;
import me.pirogoeth.Waypoint.Commands.Waypoints;
import me.pirogoeth.Waypoint.Commands.WarpAdmin;
import me.pirogoeth.Waypoint.Commands.WorldCommand;
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Registry;

public class CommandHandler {
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

    public CommandHandler (Waypoint instance) {
        plugin = instance;
        // instantiate commands.
        home = new Home(plugin);
        setSpawn = new SetSpawn(plugin);
        setWarp = new SetWarp(plugin);
        spawnAdmin = new SpawnAdmin(plugin);
        spawnCommand = new SpawnCommand(plugin);
        teleLocation = new TeleLocation(plugin);
        teleport = new Teleport(plugin);
        teleportHere = new TeleportHere(plugin);
        warp = new Warp(plugin);
        waypoints = new Waypoints(plugin);
        warpAdmin = new WarpAdmin(plugin);
        worldCommand = new WorldCommand(plugin);
    };
};