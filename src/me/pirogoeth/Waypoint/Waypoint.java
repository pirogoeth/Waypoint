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
import java.io.File;

import me.pirogoeth.Waypoint.WaypointPlayerListener;
import me.pirogoeth.Waypoint.WaypointSTP;
import me.pirogoeth.Waypoint.WaypointCommandParser;

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    // player stuff
    public static PermissionHandler permissionHandler;
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
    // plug-in code
    public void onEnable () {
    	new File(maindir).mkdir();
    	config.load();
    	if (config.getProperty("enabled") == null)
    	{
    		config.setProperty("enabled", "true");
    		config.setProperty("set_home_to_bed", "false");
    		config.setProperty("users", "");
    		config.setProperty("invites", "");
    	}
    	if (config.getProperty("enabled") == "false") { return; } 
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BED_LEAVE, playerListener, Event.Priority.Normal, this);
    	setupPermissions();
    	log.info("[Waypoint] Enabled version " + this.getDescription().getVersion());
    	config.save();
    }
    public void onDisable () {
    	log.info("[Waypoint] Disabled");
    }
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
