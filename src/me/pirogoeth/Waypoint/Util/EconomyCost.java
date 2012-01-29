package me.pirogoeth.Waypoint.Util;

// internal imports
import me.pirogoeth.Waypoint.Util.EconomyHandler;
// java imports
import java.util.Map;
import java.util.HashMap;

public enum EconomyCost {
    TELEPORT(getCostValue("economy.cost.teleport"));

    public final double value;
    private final static Map<EconomyCost, Double> store = new HashMap<EconomyCost, Double>();

    EconomyCost (final double value) {
        this.value = value;
    }

    public double getValue () {
        return this.value;
    }

    public double getDoubleValue () {
        return (Double) this.value;
    }

    public static double getCostValue (String node) {
        return (Double) EconomyHandler.config.getMain().getDouble(node, 5);
    }

    public static double getByConstant (final EconomyCost co) {
        return store.get(co);
    }

    static {
        for (EconomyCost co : EconomyCost.values()) {
            store.put(co, co.getValue());
        }
    }
}

