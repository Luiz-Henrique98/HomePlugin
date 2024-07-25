package org.exampleMinecraft_plugin.homePlugin;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeDatabase {
    private final JavaPlugin plugin;
    private Connection connection;

    public HomeDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            String url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + ":"
                    + plugin.getConfig().getInt("mysql.port") + "/"
                    + plugin.getConfig().getString("mysql.database");
            String user = plugin.getConfig().getString("mysql.username");
            String password = plugin.getConfig().getString("mysql.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Não foi possível conectar ao banco de dados MySQL.");
        }
    }

    private void createTable() {
        try (PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS homes ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "world VARCHAR(255),"
                + "x DOUBLE,"
                + "y DOUBLE,"
                + "z DOUBLE,"
                + "yaw FLOAT,"
                + "pitch FLOAT,"
                + "last_used TIMESTAMP)")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setHome(String uuid, Location loc) {
        try (PreparedStatement ps = connection.prepareStatement("REPLACE INTO homes (uuid, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, uuid);
            ps.setString(2, loc.getWorld().getName());
            ps.setDouble(3, loc.getX());
            ps.setDouble(4, loc.getY());
            ps.setDouble(5, loc.getZ());
            ps.setFloat(6, loc.getYaw());
            ps.setFloat(7, loc.getPitch());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getHome(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM homes WHERE uuid = ?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String world = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");
                    return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCooldown(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE homes SET last_used = CURRENT_TIMESTAMP WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getCooldown(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT TIMESTAMPDIFF(SECOND, last_used, CURRENT_TIMESTAMP) AS cooldown FROM homes WHERE uuid = ?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long cooldown = plugin.getConfig().getLong("cooldown");
                    long elapsed = rs.getLong("cooldown");
                    return cooldown - elapsed;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
