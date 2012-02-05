package me.pirogoeth.Waypoint.Commands;

// bukkit imports
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

// etCommon imports
import net.eisental.common.page.Pager;
import net.eisental.common.parsing.ParsingUtils;

// internal imports
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Util.Permission;

class WPager extends Command {

    public WPager (Waypoint instance) {
        super(instance);
        try {
            setCommand("wpage");
            addAlias("wppage");
            register();
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean run (Player player, String[] args)
      throws CommandException {
        if (registered == false)
            throw new CommandException("Command is not registered.");
        if (!(Permission.has(player, "waypoint.basic.pager"))) {
            player.sendMessage(ChatColor.RED +
                "[Waypoint] You do not have the permissions to use this command.");
            return true;
        }
        CommandSender sender = (CommandSender) player;
        String subcommand = "";
        try {
            subcommand = args[1];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            // there is no arg.
            if (Pager.hasPageInfo(sender))
                Pager.nextPage(sender);
            return true;
        }
        if (ParsingUtils.isNumber(subcommand))
            Pager.gotoPage(sender, Integer.valueOf(subcommand));
        else if ("previous".startsWith(subcommand))
            Pager.previousPage(sender);
        else if ("next".startsWith(subcommand))
            Pager.nextPage(sender);
        else if ("last".startsWith(subcommand))
            Pager.lastPage(sender);
        else {
            player.sendMessage(ChatColor.RED +
                "Usage: /wpage <page no.> or /wpage <next|prev|last>");
        }
        return true;
    }
}