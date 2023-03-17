package xyz.jeremynoesen.trouttoss;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * TroutToss plugin
 *
 * @author Jeremy Noesen
 */
public class TroutToss extends JavaPlugin {

    /**
     * Instance of plugin itself
     */
    private static TroutToss plugin;

    /**
     * Initialize listener
     */
    @Override
    public void onEnable() {
        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new TossListener(), plugin);
    }

    /**
     * Disable plugin
     */
    @Override
    public void onDisable() {
        plugin = null;
    }

    /**
     * Get the plugin instance
     *
     * @return Plugin instance
     */
    public static TroutToss getInstance() {
        return plugin;
    }
}
