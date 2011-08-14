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

// various core utilities
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.PlayerUtil;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.AutoUpdate;
// basic listeners
import me.pirogoeth.Waypoint.Events.BedListener;
// core support classes
import me.pirogoeth.Waypoint.Core.Parser;
import me.pirogoeth.Waypoint.Core.Spawn;
import me.pirogoeth.Waypoint.Core.Warps;

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    // permission stuff
    public Permission permissions;
    // configuration instantiation
    public Config config = new Config(this);
    // unused due to fail : public WaypointOpHandler opHandler = new WaypointOpHandler(this);
    private final BedListener bedListener = new BedListener(this);
    // logger
    Logger log = Logger.getLogger("Minecraft");
    // command parsing
    private final Parser commandParser = new Parser(this);
    // additional stuff
    public final Spawn spawnManager = new Spawn(this);
    public final Warps warpManager = new Warps(this);
    // updates
    private final AutoUpdate updateManager = new AutoUpdate(this);
    // plug-in code
    public void onEnable () {
     	config.load();
        // bedListener
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BED_ENTER, bedListener, Event.Priority.Normal, this);
    	// XXX - replace setupPermissions() with a custom class to
    	//  handle all types of permissions limiting.
    	// setupPermissions();
    	permissions = new Permission(this);
    	log.info("[Waypoint] Enabled version " + this.getDescription().getVersion());
    	config.save();
    	// check for updates
    	updateManager.doUpdate();
    	warpManager.LoadGroups();
    }
    public void onDisable () {
    	updateManager.finalise();
    	log.info("[Waypoint] Disabled version " + this.getDescription().getVersion());
    }
    public File fileGet () {
        return this.getFile();
    }
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String args[])
    {
        if (sender.getClass().getName().toString() == "org.bukkit.craftbukkit.command.ColouredConsoleSender")
        {
    		// this is a console sender *WTF*!
    		sender.sendMessage("[Waypoint] You need to be a player to use this plugin.");
    		return true;
        }
    	Player player = PlayerUtil.getPlayerFromSender(sender);
    	return Parser.CommandParser(player, cmdlabel, args);
    }
 }
