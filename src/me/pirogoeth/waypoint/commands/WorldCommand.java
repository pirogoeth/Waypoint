package me.pirogoeth.waypoint.commands;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.ConfigInventory;
import me.pirogoeth.waypoint.util.LogHandler;

// java imports
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

// bundled imports
import com.sk89q.minecraft.util.commands.*;

// etCommon imports
import net.eisental.common.page.Pager;

// bukkit
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.Location;

public class WorldCommand {

    private static Waypoint controller;
    private static Server server;
    private static LogHandler log = new LogHandler();

    public WorldCommand (Waypoint instance) {
        controller = instance;
        server = controller.getServer();
    }

    public static boolean delete_r (File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delete_r(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    };

    public static class WorldParent {

        private final Waypoint controller;

        public WorldParent(Waypoint instance) {
            controller = instance;
        }

        @Command(aliases = {"world", "w"}, desc = "Waypoint world management command",
                 min = 0, max = 1)
        @CommandPermissions({ "waypoint.world" })
        @NestedCommand({ WorldCommand.class })
        public static void world(CommandContext args, CommandSender sender) throws CommandException {}
    }

    @Command(aliases = {"info"}, desc = "Information about your current world and position",
             min = 0, max = 0)
    @CommandPermissions({ "waypoint.world.info" })
    public static void info(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;

        String worldname = player.getLocation().getWorld().getName();
        player.sendMessage(ChatColor.BLUE + "You are currently in world: " + worldname);
        String x = Double.toString(player.getLocation().getX()),
               y = Double.toString(player.getLocation().getY()),
               z = Double.toString(player.getLocation().getZ()),
           chunk = String.format("%s,%s",
               player.getLocation().getChunk().getX(),
               player.getLocation().getChunk().getZ());
        player.sendMessage(ChatColor.BLUE + "Your current position is: " + String.format("%s, %s, %s", x, y, z));
        player.sendMessage(ChatColor.BLUE + "Current chunk: " + chunk);
        return;
    }

    @Command(aliases = {"tp"}, desc = "Teleport to another world",
             max = 1)
    @CommandPermissions({ "waypoint.world.teleport", "waypoint.action.teleport" })
    public static void teleport(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;

        World world = controller.getServer().getWorld(args.getString(0));
        if (world == null) {
            player.sendMessage(ChatColor.RED + String.format("World %s does not exist.", world.getName()));
            return;
        } else if (world == player.getLocation().getWorld()) {
            player.sendMessage(ChatColor.RED + String.format("You are already in %s!", world.getName()));
            return;
        }
        Location targ_spawn = world.getSpawnLocation();
        boolean tr = player.teleport(targ_spawn);
        if (tr == false) return;
        player.sendMessage(ChatColor.GREEN +
            String.format("You have been taken to the spawn of '%s'.", world.getName()));
        return;
    }

    @Command(aliases = {"list"}, desc = "View a list of loaded worlds.",
             max = 0)
    @CommandPermissions({ "waypoint.world.list" })
    public static void list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;

        List<World> worldlist = controller.getServer().getWorlds();
        String list = "";
        Iterator wl_i = worldlist.iterator();
        while (wl_i.hasNext()) {
            list += String.format(" - %s\n", (wl_i.next()).getName());
        }
        Pager.beginPaging(
            sender,
            "====World List====",
            list,
            ChatColor.GREEN,
            ChatColor.RED);
        return;
    }
}