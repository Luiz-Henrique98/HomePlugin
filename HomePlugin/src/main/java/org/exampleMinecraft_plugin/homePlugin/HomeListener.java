package org.exampleMinecraft_plugin.homePlugin;


import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HomeListener implements Listener {
    private final JavaPlugin plugin;

    public HomeListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
