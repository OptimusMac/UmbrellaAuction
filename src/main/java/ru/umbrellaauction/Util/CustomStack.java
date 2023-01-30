package ru.umbrellaauction.Util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Main;

import java.util.ArrayList;
import java.util.List;

public class CustomStack {


    public static ItemStack customStack(ItemStack itemStack, int price, String owner) {
        ItemStack item = new ItemStack(itemStack.getType(), itemStack.getAmount());

        NBTItem nbtItem = new NBTItem(item);

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        long setTime = (System.currentTimeMillis() + ((60 * 1000) * 60) * Main.getInstance().aucTime);
        int totalSecond = (int) ((setTime - System.currentTimeMillis()) / 1000);


        int hours = totalSecond / 3600;
        int minutes = (totalSecond % 3600) / 60;
        int seconds = totalSecond % 60;

        String time = String.format("§6⟳ §fИстекает: §6%02d ч %02d м %02d с", hours, minutes, seconds);

        lore.add("");
        lore.add("§6➥ Нажми§f, что бы купить ");
        lore.add("§c__________________________");
        lore.add("§a$ §fЦена: §a" + price + "$");
        lore.add("§6⎈ §fПродавец: §6" + owner);
        lore.add(time);
        meta.setLore(lore);
        nbtItem.getItem().setItemMeta(meta);

        nbtItem.setInteger("price", price);
        nbtItem.setString("owner", owner);
        nbtItem.setLong("time", setTime);
        nbtItem.setInteger("slot", AuctionHandler.getAllItems().size() + 1);

        nbtItem.getItem().setDurability(itemStack.getDurability());
        nbtItem.getItem().addEnchantments(itemStack.getEnchantments());

        return nbtItem.getItem();
    }


    public static ItemStack giveItem(ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        List<String> result = new ArrayList<>();

        for (String str : lore) {
            if (str.contains("§6➥ Нажми§f, что бы купить ") || str.contains("§c__________________________") || str.contains("§a$ §fЦена: §a") || str.contains("――――――――――――") || str.equals("") ||
                    str.contains("§6⎈ §fПродавец: §6") || str.contains("§fИстекает:"))
                continue;
            result.add(str);
        }
        meta.setLore(result);
        item.setItemMeta(meta);

        return item;

    }


    public static int getPrice(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger("price");
    }

    public static String getOwner(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString("owner");
    }

    public static int getSlot(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger("slot");
    }

}
