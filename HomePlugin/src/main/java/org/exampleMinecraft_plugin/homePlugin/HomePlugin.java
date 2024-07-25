package org.exampleMinecraft_plugin.homePlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class HomePlugin extends JavaPlugin {
    private HomeDatabase database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        database = new HomeDatabase(this);
        getCommand("home").setExecutor(new HomeCommand(this, database));
        getCommand("sethome").setExecutor(new HomeCommand(this, database));
        getServer().getPluginManager().registerEvents(new HomeListener(this), this);
        getLogger().info("HomePlugin ativado!");
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        getLogger().info("HomePlugin desativado!");
    }

    public HomeDatabase getDatabase() {
        return database;
    }
}

