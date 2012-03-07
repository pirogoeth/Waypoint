package me.pirogoeth.waypoint;

// Bukkit imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

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
import me.pirogoeth.waypoint.util.AutoUpdate;
import me.pirogoeth.waypoint.util.Config;
import me.pirogoeth.waypoint.util.Cooldown;
import me.pirogoeth.waypoint.util.EconomyHandler;
import me.pirogoeth.waypoint.util.Governor;
import me.pirogoeth.waypoint.util.Limits;
import me.pirogoeth.waypoint.util.LogHandler;
import me.pirogoeth.waypoint.util.Permission;
import me.pirogoeth.waypoint.util.WarpLimits;

// basic listeners
import me.pirogoeth.waypoint.events.EventListener;

// core support classes
import me.pirogoeth.waypoint.core.Spawn;
import me.pirogoeth.waypoint.core.Warps;
import me.pirogoeth.waypoint.core.Worlds;
import me.pirogoeth.waypoint.core.Links;

// bundled imports
import com.sk89q.bukkit.util.CommandRegistration; // dynamic command registry
import com.sk89q.util.*;
import com.sk89q.minecraft.util.commands.*; // command framework

@SuppressWarnings("unused")
public class Waypoint extends JavaPlugin {
    /** Base class handling all plugin initialisation.
     *
     * This handles all variables relating to later function of the plugin, as well as the onEnable() and onDisable() methods.
     */

    /**
     * Instance of out configuration manager.
     */
    public Config config = new Config(this);

    /**
     * Initialise our logger instance.
     */
    private final LogHandler log = new LogHandler();

    /**
     * Instance of our global limit provider, the governor.
     */
    public final Governor limitProvider = new Governor(this);

    /**
     * Instance of our teleportation cooldown manager.
     */
    public final Cooldown cooldownManager = new Cooldown(this);

    /**
     * Instance of our economy and transaction handler.
     */
    public final EconomyHandler economy = new EconomyHandler(this);

    /**
     * Manages commands.
     */
    private CommandsManager<CommandSender> commands;

    /**
     * Dynamically registers commands.
     */
    private CommandRegistration dynamicCommandRegistry;

    /**
     * These are instances of core support classes that may or may not still be here once 1.7 has progressed farther.
     */
    public final me.pirogoeth.waypoint.core.Spawn spawnManager = new me.pirogoeth.waypoint.core.Spawn(this);
    public final Warps warpManager = new Warps(this);
    public final Worlds worldManager = new Worlds(this);
    public final Links linkManager = new Links(this);

    /**
     * Instance of the automatic update manager.
     */
    private final AutoUpdate updateManager = new AutoUpdate(this);

    /**
     * Handle loading of the rest of the plugin.
     */
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
        // register our command manager.
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return Permission.has((Player) player, perm);
            }
        };

        // setup instance injector
        this.commands.setInjector(new SimpleInjector(this));

        // register listener(s)
        this.registerEvents(new OAListener(this));

        // register command classes.
        this.registerCommands(this.commands.registerAndReturn(OACommands.OAParentCommand.class));

    	// check for updates
    	this.updateManager.doUpdate();
        // load limits
        this.limitProvider.initialiseLimits();
        /**
         * The following methods may be condensed into something different in the future of 1.7, most likely before the full release a new
         * global support class containing a bunch of methods and group of things to run all at once will be created. Perhaps? Perhaps.
         */
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

    @Override
    public File getFile () {
        /** returns the file for this plugin
         * @class Waypoint
         *
         * returns the file for this plugin. this is a wrapper for deeper classes for access.
         */

        return super.getFile();
    }

    /**
     * Register commands with the magical dynamic handler.
     */
    protected void registerCommands(List<com.sk89q.minecraft.util.commands.Command> commands) {
        this.dynamicCommandRegistry.registerAll(commands);
    }

    /**
     * Looks for a command inside of a string array.
     */
    public String[] detectCommands(String[] split) {
        split[0] = split[0].substring(1);

        String search = split[0].toLowerCase();

        // detect the command.
        if (this.commands.hasCommand(search)) {
        } else if (split[0].length() >= 2 && split[0].charAt(0) == '/'
                   && this.commands.hasCommand(search.substring(1))) {
            split[0] = split[0].substring(1);
        }

        return split;
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

    public boolean onCommand(CommandSender sender, Command cmd,
        String cmdlabel, String args[]) {

        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (com.sk89q.minecraft.util.commands.CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }
}
