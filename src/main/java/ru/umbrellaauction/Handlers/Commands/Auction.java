package ru.umbrellaauction.Handlers.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import ru.umbrellaauction.Handlers.Inventory.InventoryAuction;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Handlers.Inventory.InventoryBySorted;
import ru.umbrellaauction.Main;
import ru.umbrellaauction.Util.CustomStack;
import ru.umbrellaauction.Util.UtilAuction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Auction implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender.equals(Bukkit.getConsoleSender())){
            if(args[0].equalsIgnoreCase("clearAll")){
                Main.getInstance().dropTable();
                Main.getInstance().createTable();
            }
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                InventoryAuction inventoryAuction = new InventoryAuction(player, 1);
                player.openInventory(inventoryAuction.getInventory());
                return true;
            }

            if (args.length == 1 && !args[0].equals("sell")) {
                String name = args[0];
                Player toPlayer = Bukkit.getPlayer(name);
                if (toPlayer == null) {
                    sender.sendMessage(ChatColor.GOLD + "Игрок оффлайн!");
                    return true;
                }
                ArrayList<ItemStack> items = UtilAuction.getSortedByPlayer(name);
                InventoryBySorted inventory = new InventoryBySorted(items, player, 1, toPlayer);
                player.openInventory(inventory.getInventory());
                return true;

            }

            if (args[0].equalsIgnoreCase("sell")) {

                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Установите цену! /ah sell <price>");
                    return true;
                }

                if (UtilAuction.getLoot(player.getName()).getCount() + 1 > Main.getInstance().maxLoot) {
                    sender.sendMessage(ChatColor.GOLD + "У вас максимальное количество лотов!");
                    return true;
                }

                int price = Integer.parseInt(args[1]);
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType().equals(Material.AIR)) {
                    sender.sendMessage(ChatColor.GOLD + "Вы не можете продать воздух :)");
                    return true;
                }
                ItemStack customStack = CustomStack.customStack(item, price, sender.getName());
                AuctionHandler.addItem(customStack);
                addItemBase(customStack);

                player.getInventory().setItemInMainHand(null);
                sender.sendMessage(ChatColor.YELLOW + "Вы выставили " + ChatColor.GOLD + UtilAuction.name(customStack) + ChatColor.YELLOW + " за " + ChatColor.GOLD + price + "$");
                return true;
            }
        }
        return false;
    }


    private static void addItemBase(ItemStack itemStack){
        try {

            String encodedObject;

            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(itemStack);
            os.flush();
            byte[] serializedObject = io.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(serializedObject);
            Statement statement = Main.getInstance().connection.createStatement();
            statement.executeUpdate("INSERT Auction(ItemStack) VALUES ('%stack')".replace("%stack", encodedObject));

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
