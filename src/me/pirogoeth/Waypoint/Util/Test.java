package me.pirogoeth.Waypoint.Util;

import me.pirogoeth.Waypoint.Util.Command;
import me.pirogoeth.Waypoint.Util.CommandException;
import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Registry;
import me.pirogoeth.Waypoint.Util.RegistryException;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

class TestCommand extends Command {
    public TestCommand (Waypoint instance) {
        super(instance);
    }
    public TestCommand (Waypoint instance, String command) {
        super(instance, command);
    }

    @Override
    public boolean run (Player player, String[] args)
      throws CommandException {
        /**
         * This is a test. So this won't actually do anything.
         * ...
         * Or will it..?
         * .....
         * I guess we'll find out soon....
         */
        log.info("{WPT16} TestCommand has been run.");
        return false;
    }
}

public class Test {
    private Waypoint plugin;
    private Logger log = Logger.getLogger("Minecraft");
    private Registry registry;
    public TestCommand commandTest;
    public int i = 0;
    public Test (Waypoint instance) {
        plugin = instance;
        registry = plugin.registry;
        log.info("{WPT16} Initialising testing classes.");
        commandTest = new TestCommand(plugin, "waypoint");
        log.info("{WPT16} [REGISTRY TESTS]");
        registry_size_Test();
        registry_emptyness_Test();
        registry_put_get_remove_Test();
        registry_process_Test();
        log.info("{WPT16} [COMMAND TESTS]");
        command_registration_Test();
        command_deregistration_Test();
        command_modification_Test();
        log.info("{WPT16} [MIXED TESTS]");
        command_registration_Test();
        registry_size_Test();
        registry_emptyness_Test();
        command_deregistration_Test();
        log.info(String.format("{WPT16} Tests passed: %d/11", i));
    }
    public void command_registration_Test () {
        try { commandTest.register(); }
        catch (me.pirogoeth.Waypoint.Util.CommandException e) {
            return;
        }
        log.info("{WPT16} Command registered.");
        i++;
    }
    public void command_deregistration_Test () {
        try { commandTest.deregister(); }
        catch (me.pirogoeth.Waypoint.Util.CommandException e) {
            return;
        }
        log.info("{WPT16} Command deregistered.");
        i++;
    }
    public void command_modification_Test () {
        TestCommand D = new TestCommand(plugin);
        try {
            D.setRootCommand("teleport");
            D.register();
            D.setRootCommand("teleport_d");
        } catch (me.pirogoeth.Waypoint.Util.CommandException e) {
            log.info("{WPT16} setRootCommand test passed.");
            try { D.deregister(); }
            catch (me.pirogoeth.Waypoint.Util.CommandException x) {
                return;
            }
            i++;
            return;
        }
        log.info("{WPT16} setRootCommand test failed.");
        try { D.deregister(); }
        catch (me.pirogoeth.Waypoint.Util.CommandException x) {
            return;
        }
    }
    public void registry_size_Test () {
        log.info(String.format("{WPT16} Registry size test: [%d]", registry.size()));
        i++;
    }
    public void registry_emptyness_Test () {
        log.info(String.format("{WPT16} Registry emptyness test: [%s]", (String) Boolean.toString(registry.isEmpty())));
        i++;
    }
    public void registry_put_get_remove_Test () {
        String K = "test";
        Object V = this;
        log.info(String.format("{WPT16} Putting K,V into registry."));
        registry.put((String) K, (Object) V);
        log.info(String.format("{WPT16} Getting K,V from registry."));
        if (registry.get((String) K) != this) {
            log.info(String.format("{WPT16} [REGISTRY_PUT_GET_REMOVE] FAILED"));
            return;
        }
        else {
            log.info(String.format("{WPT16} Removing K,V from registry."));
            registry.remove((Object) K);
            i++;
        }
        return;
    }
    public void registry_process_Test () {
        Command C = new TestCommand(plugin, "test_moar");
        log.info(String.format("{WPT16} Registering command and running process()"));
        try { C.register(); }
        catch (me.pirogoeth.Waypoint.Util.CommandException e) {
            return;
        }
        if (C.equals(registry.process("test_moar"))) {
            log.info(String.format("{WPT16} Test successful."));
            try { C.deregister(); }
            catch (me.pirogoeth.Waypoint.Util.CommandException e) {
                return;
            }
            i++;
        }
        else {
            log.info("{WPT16} Test [REGISTRY PROCESS] failed.");
            return;
        }
        return;
    }
}
