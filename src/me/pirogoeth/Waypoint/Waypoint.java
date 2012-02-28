package me.pirogoeth.Waypoint;

// Bukkit imports
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.util.config.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;

// Java imports
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

// vault classes
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

// various core utilities
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.PlayerUtil;
import me.pirogoeth.Waypoint.Util.Config;
import me.pirogoeth.Waypoint.Util.AutoUpdate;
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.Governor;
import me.pirogoeth.Waypoint.Util.Limits;
import me.pirogoeth.Waypoint.Util.WarpLimits;
import me.pirogoeth.Waypoint.Util.Cooldown;
import me.pirogoeth.Waypoint.Util.EconomyHandler;

// basic listeners
import me.pirogoeth.Waypoint.Events.EventListener;

// core support classes
import me.pirogoeth.Waypoint.Core.Spawn;
import me.pirogoeth.Waypoint.Core.Warps;
import me.pirogoeth.Waypoint.Core.Worlds;
import me.pirogoeth.Waypoint.Core.Links;

// command classes
import me.pirogoeth.Waypoint.Commands.CommandHandler;

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    /** Base class handling all plugin initialisation.
     * @class Waypoint
     * @package me.pirogoeth.Waypoint
     * @file Waypoint.java
     *
     * This handles all variables relating to later function of the plugin, as well as the onEnable() and onDisable() methods.
     */

    // permission stuff
    public Permission permissions;
    // configuration instantiation
    public Config config = new Config(this);
    //   public final Configuration strings = config.getStrings();
    // logger
    Logger log = Logger.getLogger("Minecraft");
    // registry instantiation
    public final Registry registry = new Registry(this);
    // limits
    public final Governor limitProvider = new Governor(this);
    // cooldown timer
    public final Cooldown cooldownManager = new Cooldown(this);
    // economy
    public final EconomyHandler economy = new EconomyHandler(this);
    // additional stuff
    public final me.pirogoeth.Waypoint.Core.Spawn spawnManager = new me.pirogoeth.Waypoint.Core.Spawn(this);
    public final Warps warpManager = new Warps(this);
    public final Worlds worldManager = new Worlds(this);
    public final Links linkManager = new Links(this);
    // load commands
    public final CommandHandler commandHandler = new CommandHandler(this);
    // updates
    private final AutoUpdate updateManager = new AutoUpdate(this);

    // plug-in code
    public void onEnable () {
        /** on plugin enable...
         * @class Waypoint
         *
         * handles setup of plugin events and various services
         */

        this.config.load();
        // event listener
        this.registerEvents(new EventListener(this));
        // run permissions setup
    	this.permissions = new Permission(this);
    	this.log.info("[Waypoint] Enabled version " + this.getDescription().getVersion());
    	this.config.save();
    	// setup economy
        this.economy.setupEconomy();
    	// check for updates
    	this.updateManager.doUpdate();
        // load limits
        this.limitProvider.initialiseLimits();
    	// load warp permission groups
    	this.warpManager.LoadGroups();
    	// run the world manager world import routine
    	this.worldManager.LoadWorlds();
    }

    public void onDisable () {
        /** on plugin disable...
         * @class Waypoint
         *
         * handles teardown of plugin
         */

        // finish updating
    	this.updateManager.finalise();

    	// shutdown all scheduler tasks
    	this.getServer().getScheduler().cancelTasks(this);

    	// alert about being disabled.
    	this.log.info("[Waypoint] Disabled version " + this.getDescription().getVersion());
    }

    public File fileGet () {
        /** returns the file for this plugin
         * @class Waypoint
         *
         * returns the file for this plugin. this is a wrapper for deeper classes for access.
         */

        return this.getFile();
    }

    public Cooldown getCooldownManager () {
        /** returns the cooldown manager.
         * @class Waypoint
         *
         * returns the global instance of the cooldown manager.
         */

        return this.cooldownManager;
    }

    public EconomyHandler getEconomy () {
        /** returns the economy handler.
         * @class Waypoint
         *
         * returns the global instance of the economy handler.
         */

        return this.economy;
    }

    public void reloadPermissions () {
        /**
         * This reloads the permission handler, most likely when a command is
         * run to call it.
         */
        if (!(Permission.loaded)) {
            this.permissions = new Permission(this);
        }
    }

    private void registerEvents(Listener listener) {
        this.getServer().getPluginManager().registerEvents(
            listener,
            (Plugin) this);
        return;
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String cmdlabel, String args[]) {
        /** processes commands.
         * @class Waypoint
         *
         * processes all commands against the command registry and runs the corresponding command.
         */

        if (sender.getClass().getName().toString() == "org.bukkit.craftbukkit.command.ColouredConsoleSender") {
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
