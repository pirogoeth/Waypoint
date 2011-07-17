package me.pirogoeth.Waypoint;

import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.util.config.Configuration;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

import me.pirogoeth.Waypoint.WaypointPermission;
import me.pirogoeth.Waypoint.WaypointPlayerListener;
import me.pirogoeth.Waypoint.WaypointSTP;
import me.pirogoeth.Waypoint.WaypointSpawn;
import me.pirogoeth.Waypoint.WaypointCommandParser;
import me.pirogoeth.Waypoint.WaypointWarps;

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    // permission stuff
    public WaypointPermission permissions;
    // unused due to fail : public WaypointOpHandler opHandler = new WaypointOpHandler(this);
    private final WaypointPlayerListener playerListener = new WaypointPlayerListener(this);
    // file stuff
    public static String maindir = "plugins/Waypoint";
    public static File configfile = new File (maindir + File.separator + "config.yml");
    // logger
    Logger log = Logger.getLogger("Minecraft");
    // configuration
    public Configuration config = new Configuration(configfile);
    // command parsing
    private final WaypointCommandParser commandParser = new WaypointCommandParser(this);
    // additional stuff
    public final WaypointSpawn spawnManager = new WaypointSpawn(this);
    public final WaypointWarps warpManager = new WaypointWarps(this);
    // plug-in code
    public void onEnable () {
    	new File(maindir).mkdir();
    	config.load();
    	if (config.getProperty("set_home_at_bed") == null)
    	{
    		config.setProperty("set_home_at_bed", "false");
    		config.setProperty("users", "");
    		config.setProperty("home", "");
    		config.setProperty("spawn", "");
    		config.setProperty("warps", "");
    		// warp access group config
                List<String> warpgroups = new ArrayList<String>();
                warpgroups.add("general");
                warpgroups.add("mod");
                warpgroups.add("admin");
                // end warp access group config
    		config.setProperty("warp.groups", warpgroups);
    		config.setProperty("invites", "");
    		config.save();
    		spawnManager.ConfigWriteSpawnLocations();
    	}
     	if ((String)config.getString("set_home_at_bed") == "true");
    	{
    	    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BED_LEAVE, playerListener, Event.Priority.Normal, this);
    	    log.info("[Waypoint] Set home to bed is enabled.");
    	}
    	// XXX - replace setupPermissions() with a custom class to
    	//  handle all types of permissions limiting.
    	// setupPermissions();
    	permissions = new WaypointPermission(this);
    	log.info("[Waypoint] Enabled version " + this.getDescription().getVersion());
    	config.save();
    }
    public void onDisable () {
    	log.info("[Waypoint] Disabled version " + this.getDescription().getVersion());
    }

    /*
      private void setupPermissions () {
      if (permissionHandler != null) {
          return;
      }

      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

      if (permissionsPlugin == null) {
           log.info("[Waypoint] Permission system not detected, defaulting to OP");
           return;
       }
      permissionHandler = ((Permissions) permissionsPlugin).getHandler();
      log.info("[Waypoint] Found and will use permissions system: "+((Permissions)permissionsPlugin).getDescription().getFullName());
    }
    */
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String args[])
    {
        if (sender.getClass().getName().toString() == "org.bukkit.craftbukkit.command.ColouredConsoleSender")
        {
    		// this is a console sender *WTF*!
    		sender.sendMessage("[Waypoint] You need to be a player to use this plugin.");
    		return true;
        }
    	Player player = WaypointSTP.getPlayerFromSender(sender);
    	return WaypointCommandParser.CommandParser(player, cmdlabel, args);
    }
 }
