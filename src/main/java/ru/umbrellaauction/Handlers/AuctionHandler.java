package ru.umbrellaauction.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import ru.umbrellaauction.Main;
import ru.umbrellaauction.Util.UtilAuction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class AuctionHandler {

    private static List<ItemStack> allItems = new ArrayList<>();


    public static void addItem(ItemStack itemStack) {
        allItems.add(itemStack);
    }

    public static List<ItemStack> getAllItems() {
        return allItems;
    }

    public static void removeItem(ItemStack item) {
        List<ItemStack> itemStacks = new ArrayList<>(allItems);


        for (ItemStack itemStack : itemStacks) {
            if (removeTimeLore(itemStack).equals(removeTimeLore(item))) {
                allItems.remove(itemStack);
                removeInDB(itemStack);
                return;
            }
        }
    }

    private static boolean removeInDB(ItemStack itemStack) {
        String query = "SELECT * FROM " + "Auction";
        try {
            Statement stmt = Main.getInstance().connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                String string = resultSet.getString(2);

                byte[] serializedObject = Base64.getDecoder().decode(string);

                ItemStack i = castItem(serializedObject);
                String is = codeItem(removeTimeLore(i));

                byte[] serializedObjectOutput = Base64.getDecoder().decode(is);

                byte[] stack = UtilAuction.getByteToStack(removeTimeLore(itemStack));

                if (Arrays.equals(serializedObjectOutput, stack)) {
                    String delete = "DELETE FROM Auction WHERE ItemStack='%str'".replace("%str", string);
                    stmt.executeUpdate(delete);
                    stmt.close();
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getLogger().warning("Item " + itemStack.toString() + " cant removing in database!!!");
        return false;
    }

    private static ItemStack castItem(byte[] b) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
        BukkitObjectInputStream bukkitObjectInputStream = null;
        try {
            bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            ItemStack item = (ItemStack) bukkitObjectInputStream.readObject();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private static String codeItem(ItemStack itemStack) {
        String encodedObject;
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = null;
            os = new BukkitObjectOutputStream(io);
            os.writeObject(itemStack);
            os.flush();
            byte[] serializedObject = io.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(serializedObject);
            return encodedObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static ItemStack removeTimeLore(ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore1 = meta.getLore();
        int index = 0;
        for (String str : lore1) {
            if (str.contains("§6⟳ §fИстекает: ")) {
                index = lore1.indexOf(str);
                break;
            }
        }
        lore1.remove(index);
        meta.setLore(lore1);
        item.setItemMeta(meta);
        return item;
    }

}
