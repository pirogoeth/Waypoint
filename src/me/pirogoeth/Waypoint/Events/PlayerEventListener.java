package me.pirogoeth.Waypoint.Events;

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
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Cooldown;
import me.pirogoeth.Waypoint.Core.Warps;
import me.pirogoeth.Waypoint.Core.Links;
import me.pirogoeth.Waypoint.Util.EconomyHandler;
import me.pirogoeth.Waypoint.Util.EconomyCost;
import me.pirogoeth.Waypoint.Util.ConfigInventory;

public class PlayerEventListener implements Listener {
    public static Waypoint plugin;
    public static Config config;
    private Cooldown cooldownManager;
    public static Warps warpManager;
    public static Links linkManager;
    public Permission permissions;
    public final EconomyHandler economy;
    Logger log = Logger.getLogger("Minecraft");

    public PlayerEventListener (Waypoint instance) {
        plugin = instance;
        economy = instance.getEconomy();
        permissions = plugin.permissions;
        config = plugin.config;
        warpManager = plugin.warpManager;
        linkManager = plugin.linkManager;
        cooldownManager = instance.getCooldownManager();
    }

    public static String UserNodeChomp (Player p, String arg, String sub) {
        String a = "users." + p.getName().toString() + "." + arg + "." + sub;
        return a;
    }

    public static String HomeNodeChomp (Player p, World w, String sub) {
        String a = "home." + p.getName().toString() + "." + w.getName().toString() + "." + sub;
        return a;
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
        if (cooldownManager.checkUser(player) == false) {
            cooldownManager.holdUser(player);
        } else if (cooldownManager.checkUser(player) == true) {
            player.sendMessage("[Waypoint] Please wait, cooling down...");
            event.setCancelled(true);
            return;
        }
        if (!(permissions.has(player, "waypoint.cost_exempt.teleport"))) {
            // the player is allowed to teleport for free because of this node.
            // we have to charge the player since they don't have this node
            EconomyCost cost = EconomyCost.TELEPORT;
            if(!(economy.debitPlayer(player.getName().toString(), cost.getValue()))) {
                event.setCancelled(true);
            } else {
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (plugin.config.getMain().getBoolean("home.set_home_at_bed", false) == false) { return; };
        if (!permissions.has(player, "waypoint.home.set_on_bed_leave")) { return; };
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        World w = player.getLocation().getWorld();
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.X"), x);
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.Y"), y);
        plugin.config.getUsers().setProperty(HomeNodeChomp(player, w, "coord.Z"), z);
        plugin.config.getUsers().save();
        if (player != null) {
            player.sendMessage(ChatColor.AQUA + "[Waypoint] " + player.getName().toString() + ", your home for world " + player.getWorld().getName().toString() + " has been set to the bed you just entered.");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String worldname = player.getLocation().getWorld().getName().toString();
        if (plugin.config.getWorld().getProperty(String.format("worlds.%s.env", worldname)) == null)
            return;
        else {
            GameMode mode = GameMode.getByValue(plugin.config.getWorld().getInt(String.format("worlds.%s.mode", worldname), 0));
            player.setGameMode(mode);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn (PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String worldname = event.getRespawnLocation().getWorld().getName().toString();
        if (plugin.config.getWorld().getProperty(String.format("worlds.%s.env", worldname)) == null)
            return;
        else {
            GameMode mode = GameMode.getByValue(plugin.config.getWorld().getInt(String.format("worlds.%s.mode", worldname), 0));
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
        if (!permissions.has(player, String.format("waypoint.world.access.%s", player.getLocation().getWorld().getName().toString()))) {
            World player_prev_w = event.getFrom();
            Location prev_sl = player_prev_w.getSpawnLocation();
            player.teleport(prev_sl);
            player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to access this world.");
            return;
        }
        GameMode mode = GameMode.getByValue(plugin.config.getWorld().getInt(String.format("worlds.%s.mode", player.getLocation().getWorld().getName().toString()), 0));
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
         *
         * New sign format as of 1.6.3:
         *   [WP:(WARP/WORLD/GAMEMODE)]
         *   <world/warpname/gamemode>
         *   <description>
         *   <description>
         */
        Block clicked_b = event.getClickedBlock();
        if (clicked_b == null) { return; }; // prevents spewage of NullPointerException everywhere
        if ((clicked_b.getTypeId() == 63 || clicked_b.getTypeId() == 68) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign clicked_s = (Sign) clicked_b.getState();
            if (!((String) clicked_s.getLine(0)).split("\\:")[0].equalsIgnoreCase("[WP")) {
                return;
            }
            String signtype = (String) clicked_s.getLine(0).split("\\:")[1].split("]")[0];
            String target = null;
            String target_o = null;
            if (signtype.equalsIgnoreCase("link")) {
                if (!permissions.has(player, "waypoint.sign.link.use")) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                    return;
                }
                try {
                    linkManager.PlayerBetweenNetwork(player, clicked_s, (String[]) ((Sign) clicked_b.getState()).getLines());
                } catch (NullPointerException e) {
                    // this block is probably being broken right now.
                    return;
                }
                return;
            }
            try {
                target = (String) clicked_s.getLine(1);
            }
            catch (java.lang.IndexOutOfBoundsException e) {
                return;
            }
            target_o = target;
            target = ((String) target).replaceAll("\\p{Cntrl}", "");;
            if (signtype.equalsIgnoreCase("warp") && target_o != null) {
                if (!permissions.has(player, "waypoint.sign.warp")) {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                boolean result = warpManager.PlayerToWarp(player, target);
                if (result == false) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The warp name listed on this sign is invalid.");
                    final int id = new Integer("63");
                    ItemStack sign_dr = new ItemStack(id);
                    clicked_b.setTypeId(0);
                    clicked_b.getLocation().getWorld().dropItemNaturally(clicked_b.getLocation(), sign_dr);
                    return;
                }
                return;
            }
            else if (signtype.equalsIgnoreCase("world") && target_o != null) {
                if (!permissions.has(player, "waypoint.sign.world")) {
                   player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to use this sign.");
                   return;
                }
                if (!permissions.has(player, String.format("waypoint.world.access.%s", target))) {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] You do not have permission to access this world.");
                    return;
                }
                World world_t = plugin.getServer().getWorld(target);
                Location world_l = null;
                if (world_t == null) {
                    // try a match in the worlds list.
                    List<World> wlist = plugin.getServer().getWorlds();
                    Iterator witer = wlist.iterator();
                    World w;
                    while (witer.hasNext()) {
                        w = (World) witer.next();
                        if (w.getName().toString().contains(target)) {
                            target = w.getName().toString();
                            world_t = plugin.getServer().getWorld(target);
                            player.teleport(world_t.getSpawnLocation());
                            player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                            return;
                        }
                    }
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] The world name listed on this sign is invalid.");
                    final int id = new Integer("63");
                    ItemStack sign_dr = new ItemStack(id);
                    clicked_b.setTypeId(0);
                    clicked_b.getLocation().getWorld().dropItemNaturally(clicked_b.getLocation(), sign_dr);
                    return;
                }
                else if (world_t != null) {
                    world_l = world_t.getSpawnLocation();
                }
                player.teleport(world_l);
                player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to the spawn of " + target);
                return;
            } else if (signtype.equalsIgnoreCase("gamemode")) {
                if (!permissions.has(player, "waypoint.sign.gamemode")) {
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
                    final int s_id = new Integer("63");
                    ItemStack sign_dr = new ItemStack(s_id);
                    clicked_b.setTypeId(0);
                    clicked_b.getLocation().getWorld().dropItemNaturally(
                        clicked_b.getLocation(),
                        sign_dr
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
