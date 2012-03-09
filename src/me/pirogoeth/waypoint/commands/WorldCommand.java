package me.pirogoeth.waypoint.commands;

// internal imports
import me.pirogoeth.waypoint.Waypoint
import me.pirogoeth.waypoint.util.ConfigInventory;
import me.pirogoeth.waypoint.util.LogHandler;

// bundled imports
import com.sk89q.minecraft.util.*;

// bukkit
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.World;

public class WorldCommand {

    private static Waypoint controller;
    private static Server server;

    public WorldCommand (Waypoint instance) {
        controller = instance;
        server = controller.getServer();
    }

    public static class WorldParentCommand {

        private final Waypoint controller;

        public WorldParentCommand(Waypoint instance) {
            controller = instance;
        }
    }
}