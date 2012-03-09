package me.pirogoeth.waypoint.Events;

// bukkit imports
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockState;
import org.bukkit.ChatColor;

// java imports
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

// internal imports
import me.pirogoeth.waypoint.Waypoint;
import me.pirogoeth.waypoint.util.Config;
import me.pirogoeth.waypoint.util.ConfigInventory;
import me.pirogoeth.waypoint.util.Cooldown;
import me.pirogoeth.waypoint.util.EconomyHandler;
import me.pirogoeth.waypoint.util.EconomyCost;
import me.pirogoeth.waypoint.util.LogHandler;
import me.pirogoeth.waypoint.util.Permission;
import me.pirogoeth.waypoint.core.Warps;
import me.pirogoeth.waypoint.core.Links;

public class EventListener implements Listener {

    public static Waypoint controller;
    public static Warps warpManager;
    public static Links linkManager;
    private static final LogHandler log = new LogHandler();

    public EventListener (Waypoint instance) {
        controller = instance;
        warpManager = controller.warpManager;
        linkManager = controller.linkManager;
    }

    public static String UserNodeChomp (Player p, String arg, String sub) {
        String a = "users." + p.getName().toString() + "." + arg + "." + sub;
        return a;
    }

    public static String HomeNodeChomp (Player p, World w, String sub) {
        String a = "home." + p.getName().toString() + "." + w.getName().toString() + "." + sub;
        return a;
    }

    /**
     * Called when a player tries to use a command.
     *
     * Most of this method is "borrowed" from WorldEdit.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;

        String[] split = event.getMessage().split(" ");

        if (split.length > 0) {
            split = this.controller.detectCommands(split);
            final String label = split[0];
            split[0] = "/" + split[0];
        }

        final String new_message = StringUtil.joinString(split, " ");
        if (!(new_message.equals(event.getMessage()))) {
            event.setMessage(new_message);
            this.controller.getServer().getPluginManager().callEvent(event);
            if (!(event.isCancelled())) {
                if (event.getMessage().length() > 0) {
                    this.controller.getServer().dispatchCommand(
                        event.getPlayer(),
                        event.getMessage().substring(1));
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        // this is probably just a temporary solution to get Waypoint's permission support fully locked on.
        this.controller.reloadPermissions();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        // this is for people who don't want to use Waypoint's world/teleportation services.
        if (ConfigInventory.MAIN.getConfig().getBoolean("external.teleport", true) == true) {
            return;
        }
        // cooldown implementation
        if (event.getCause().toString().equals("UNKNOWN")) {
            // this is the second teleport to get an absolute fix on the correct position. allow it.
            return;
        }
        if (this.controller.getCooldownManager().checkUser(player) == false) {
            this.controller.getCooldownManager().holdUser(player);
        } else if (this.controller.getCooldownManager().checkUser(player) == true) {
            player.sendMessage("[Waypoint] Please wait, cooling down...");
            event.setCancelled(true);
            return;
        }
        if (!(Permission.has(player, "waypoint.cost_exempt.teleport"))) {
            // the player is allowed to teleport for free because of this node.
            // we have to charge the player since they don't have this node
            EconomyCost cost = EconomyCost.TELEPORT;
            if(!(this.controller.getEconomy().debitPlayer(player.getName().toString(), cost.getValue()))) {
                event.setCancelled(true);
            } else {
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBedEnter(PlayerBedEnterEnter event) {
        Player player = event.getPlayer();

        /**
         * This will set a player's home location to the bed that
         * they just got into, if the server has the option enabled.
         */

        String template = String.format("users.%s", player.getName());

        if (this.controller.config.getMain().getBoolean("home.bedsetting", false) == false)
            return;
        double x = player.getLocation().getX(), y = player.getLocation().getY(), z = player.getLocation.getZ();
        World w = player.getLocation().getWorld();
        ConfigInventory.USERS.getConfig().set(
            String.format("%s.location", template),
            player.getLocation());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String worldname = player.getLocation().getWorld().getName().toString();
        if (ConfigInventory.WORLD.getConfig().getInt(String.format("worlds.%s.env", worldname)) == null)
            return;
        else {
            GameMode mode = GameMode.getByValue(ConfigInventory.WORLD.getConfig().getInt(String.format("worlds.%s.mode", worldname), 0));
            player.setGameMode(mode);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn (PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String worldname = event.getRespawnLocation().getWorld().getName().toString();
        if (ConfigInventory.WORLD.getConfig().getInt(String.format("worlds.%s.env", worldname)) == null)
            return;
        else {
            GameMode mode = GameMode.getByValue(ConfigInventory.WORLD.getConfig().getInt(String.format("worlds.%s.mode", worldname), 0));
            player.setGameMode(mode);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedWorld (PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        // this is for people who don't want to use Waypoint's world/teleportation services.
        if (ConfigInventory.MAIN.getConfig().getBoolean("external.worldmg", true) == true) {
            return;
        }

        /**
         * Watching for players, we should be able to cancel their world change if they do not have the permissions to change.
         */
        if (!Permission.has(player, String.format("waypoint.world.access.%s", player.getLocation().getWorld().getName()))) {
            World player_prev_w = event.getFrom();
            Location prev_sl = player_prev_w.getSpawnLocation();
            player.teleport(prev_sl);
            player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to access this world.");
            return;
        }
        GameMode mode = GameMode.getByValue(ConfigInventory.WORLD.getConfig().getInt(String.format("worlds.%s.mode", player.getLocation().getWorld().getName().toString()), 0));
        player.setGameMode(mode);
        return;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        /**
         * Target IDs:
         * 63: Sign Post
         * 68: Wall sign
         */
        Block clicked_b = event.getClickedBlock();

        if (clicked_b == null)
            return; // prevents spewage of NullPointerException everywhere

        if ((clicked_b.getTypeId() == 63 || clicked_b.getTypeId() == 68) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign clicked_s = (Sign) clicked_b.getState();
            if (!((String) clicked_s.getLine(0)).split("\\:")[0].equalsIgnoreCase("[WP"))
                return;
            String signtype = (String) clicked_s.getLine(0).split("\\:")[1].split("]")[0];
            String target = null;
            if (signtype.equalsIgnoreCase("warp")) {
                if (!Permission.has(player, "waypoint.sign.warp")) {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                Location loc = this.controller.getHelper().getWarp(target);
                if (loc == null) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The warp name listed on this sign is invalid.");
                    return;
                }
                player.teleport(loc);
                return;
            } else if (signtype.equalsIgnoreCase("world")) {
                if (!Permission.has(player, "waypoint.sign.world")) {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                if (!Permission.has(player, String.format("waypoint.world.access.%s", target))) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to access this world.");
                    return;
                }
                World world_t = this.controller.getServer().getWorld(target);
                Location world_l = null;
                if (world_t == null) {
                    // try a match in the worlds list.
                    List<World> wlist = this.controller.getServer().getWorlds();
                    Iterator witer = wlist.iterator();
                    World w;
                    while (witer.hasNext()) {
                        w = (World) witer.next();
                        if (w.getName().toString().contains(target)) {
                            target = w.getName().toString();
                            world_t = this.controller.getServer().getWorld(target);
                            player.teleport(world_t.getSpawnLocation());
                            player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                            return;
                        }
                    }
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The world name listed on this sign is invalid.");
                    return;
                }
                else if (world_t != null) {
                    world_l = world_t.getSpawnLocation();
                }
                player.teleport(world_l);
                player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                return;
            } else if (signtype.equalsIgnoreCase("gamemode")) {
                if (!Permission.has(player, "waypoint.sign.gamemode")) {
                    player.sendMessage(ChatColor.RED +
                        "[Waypoint] You do not have the permission to use this sign.");
                    return;
                }
                int gm;
                GameMode mode;
                try { gm = Integer.parseInt((String) clicked_s.getLine(1)); }
                catch (java.lang.Exception e) { return; };
                if (gm != 1 && gm != 0) {
                    player.sendMessage(ChatColor.RED +
                        "[Waypoint] This sign was created with an invalid game mode.");
                    );
                    return;
                }
                mode = GameMode.getByValue(gm);
                player.setGameMode(mode);
                return;
            }
            return;
        }
    }
}
