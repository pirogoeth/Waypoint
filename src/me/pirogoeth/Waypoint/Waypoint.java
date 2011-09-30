package me.pirogoeth.Waypoint;

// Bukkit imports
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.config.Configuration;
// Java imports
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
// various core utilities
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.PlayerUtil;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.AutoUpdate;
import me.pirogoeth.Waypoint.Util.Command;
// basic listeners
import me.pirogoeth.Waypoint.Events.BedListener;
import me.pirogoeth.Waypoint.Events.PlayerEventListener;
import me.pirogoeth.Waypoint.Events.BlockEventListener;
// core support classes
import me.pirogoeth.Waypoint.Core.Spawn;
import me.pirogoeth.Waypoint.Core.Warps;
import me.pirogoeth.Waypoint.Core.Worlds;
import me.pirogoeth.Waypoint.Core.Links;
// command classes
import me.pirogoeth.Waypoint.Commands.CommandHandler;
// testing imports
import me.pirogoeth.Waypoint.Util.Test;

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    // permission stuff
    public Permission permissions;
    // configuration instantiation
    public Config config = new Config(this);
    // logger
    Logger log = Logger.getLogger("Minecraft");
    // registry instantiation
    public final Registry registry = new Registry(this);
    // additional stuff
    public final me.pirogoeth.Waypoint.Core.Spawn spawnManager = new me.pirogoeth.Waypoint.Core.Spawn(this);
    public final Warps warpManager = new Warps(this);
    public final Worlds worldManager = new Worlds(this);
    public final Links linkManager = new Links(this);
    // load commands
    public final CommandHandler commandHandler = new CommandHandler(this);
    // listeners
    private final BedListener bedListener = new BedListener(this);
    private final PlayerEventListener playerListener = new PlayerEventListener(this);
    private final BlockEventListener blockListener = new BlockEventListener(this);
    // updates
    private final AutoUpdate updateManager = new AutoUpdate(this);
    // RUN TESTS. XXX REMOVE (or command) THIS.
    //   public final Test test = new Test(this);
    // plug-in code
    public void onEnable () {
     	config.load();
        // bedListener
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BED_LEAVE, bedListener, Event.Priority.Normal, this);
        // player listener
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        // block listener
        getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
        // run permissions setup
    	permissions = new Permission(this);
    	log.info("[Waypoint] Enabled version " + this.getDescription().getVersion());
    	config.save();
    	// check for updates
    	updateManager.doUpdate();
    	// load warp permission groups
    	warpManager.LoadGroups();
    	// run the world manager world import routine
    	worldManager.LoadWorlds();
    }
    public void onDisable () {
    	updateManager.finalise();
    	log.info("[Waypoint] Disabled version " + this.getDescription().getVersion());
    }
    public File fileGet () {
        return this.getFile();
    }
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String cmdlabel, String args[])
    {
        if (sender.getClass().getName().toString() == "org.bukkit.craftbukkit.command.ColouredConsoleSender")
        {
    		// this is a console sender *WTF*!
    		sender.sendMessage("[Waypoint] You need to be a player to use this plugin.");
    		return true;
        }
        /**
         * Adjustment for 1.6
         *
         * Removes:
         *   Player player = PlayerUtil.getPlayerFromSender(sender);
         *   return Parser.CommandParser(player, cmdlabel, args);
         * Adds:
         *   Proposed interface for registry.
         */
    	 Player player = (Player) sender;
    	 me.pirogoeth.Waypoint.Util.Command command = (me.pirogoeth.Waypoint.Util.Command) registry.process(cmdlabel);
    	 if (command == null)
    	     return true;
    	 try {
    	     return command.run(player, args);
    	 } catch (me.pirogoeth.Waypoint.Util.CommandException e) {
    	     e.printStackTrace();
    	     return true;
    	 }
    }
 }
