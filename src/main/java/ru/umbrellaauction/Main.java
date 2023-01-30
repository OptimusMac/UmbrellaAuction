package ru.umbrellaauction;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Handlers.Commands.Auction;
import ru.umbrellaauction.Handlers.Listeners.Event;
import ru.umbrellaauction.Util.UtilAuction;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Economy econ;
    public int maxLoot;
    public List<String> list;
    private String username;
    private String password;
    public int aucTime;
    private String url = "jdbc:mysql://%host:%port/%name";

    public Connection connection;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupEconomy();
        this.maxLoot = getInstance().getConfig().getInt("max-loot");
        this.aucTime = getInstance().getConfig().getInt("time-auction");
        this.list = getInstance().getConfig().getStringList("really-items");
        getCommand("auction").setExecutor(new Auction());
        this.username = getInstance().getConfig().getString("database.user");
        this.password = getInstance().getConfig().getString("database.password");
        this.url = this.url.replace("%host", getInstance().getConfig().getString("database.host"));
        this.url = this.url.replace("%port", getInstance().getConfig().getString("database.port"));
        this.url = this.url.replace("%name", getInstance().getConfig().getString("database.database"));
        Bukkit.getPluginManager().registerEvents(new Event(), this);

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
        initAllItems();



    }


    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Auction (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, ItemStack VARCHAR(10000));";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Table is created!");

    }

    public void dropTable() {
        String sql = "DROP TABLE Auction;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Table is dropped!");
    }

    private void initAllItems() {
        String query = "SELECT * FROM " + "Auction";

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                String string = resultSet.getString(2);

                byte[] serializedObject = Base64.getDecoder().decode(string);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedObject);
                BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                ItemStack item = (ItemStack) bukkitObjectInputStream.readObject();
                AuctionHandler.addItem(item);
            }

        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
