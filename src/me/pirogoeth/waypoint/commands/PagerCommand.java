package me.pirogoeth.waypoint.commands;

// bukkit imports
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

// etCommon imports
import net.eisental.common.page.Pager;
import net.eisental.common.parsing.ParsingUtils;

// internal imports
import me.pirogoeth.waypoint.util.Permission;

// command framework
import com.sk89q.minecraft.util.commands.*;

public class PagerCommand {

    private static Waypoint controller;

    public PagerCommand(Waypoint instance) {
        controller = instance;
    }

    public static class PagerParent {

        private final Waypoint controller;

        public PagerParent(Waypoint instance) {
            controller = instance;
        }

        @Command(aliases = {"wpage", "wppage"}, desc = "Interface to Waypoint's internal pager.")
        @CommandPermissions({ "waypoint.pager" })
        @NestedCommand({PagerCommand.class})
        public static void pager() {}
    }

    @Command(aliases = {"previous", "prev", "p"}, desc = "Go to the previous page.")
    public static void previous(CommandContext args, CommandSender sender) throws CommandException {
        if (Pager.hasPageInfo(sender)) Pager.previousPage(sender);
    }

    @Command(aliases = {"next", "n"}, desc = "Go to the next page.")
    public static void next(CommandContext args, CommandSender sender) throws CommandException {
        if (Pager.hasPageInfo(sender)) Pager.nextPage(sender);
    }

    @Command(aliases = {"last", "l"}, desc = "Go to the last page.")
    public static void last(CommandContext args, CommandSender sender) throws CommandException {
        if (Pager.hasPageInfo(sender)) Pager.lastPage(sender);
    }

    @Command(aliases = {"goto", "g"}, desc = "Go a specific page.", max = 1, min = 1)
    public static void goto(CommandContext args, CommandSender sender) throws CommandException {
        if (Pager.hasPageInfo(sender)) Pager.gotoPage(sender, Integer.valueOf(args.getString(0)));
    }
}