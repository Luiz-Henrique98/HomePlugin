package org.exampleMinecraft_plugin.homePlugin;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeCommand implements CommandExecutor {
    private final HomePlugin plugin;
    private final HomeDatabase database;

    public HomeCommand(HomePlugin plugin, HomeDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
            return true;
        }

        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        if (command.getName().equalsIgnoreCase("sethome")) {
            Location loc = player.getLocation();
            database.setHome(uuid, loc);
            player.sendMessage("Home definida!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("home")) {
            if (database.getCooldown(uuid) > 0) {
                player.sendMessage("Você deve esperar " + database.getCooldown(uuid) + " segundos para usar este comando novamente.");
                return true;
            }

            Location loc = database.getHome(uuid);
            if (loc == null) {
                player.sendMessage("Home não definida.");
                return true;
            }

            FileConfiguration config = plugin.getConfig();
            if (config.getBoolean("showParticles")) {
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
            }

            player.teleport(loc);
            database.setCooldown(uuid);
            player.sendMessage("Teletransportado para home!");
            return true;
        }

        return false;
    }
}
