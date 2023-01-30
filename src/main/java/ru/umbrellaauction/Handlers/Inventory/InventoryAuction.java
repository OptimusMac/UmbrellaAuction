package ru.umbrellaauction.Handlers.Inventory;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Util.UtilAuction;

import java.util.*;

public class InventoryAuction implements InventoryHolder {

    private Inventory inventory;
    public int page = 0;
    private Player player;


    public InventoryAuction(Player player, int page) {
        this.page = page;
        this.inventory = Bukkit.createInventory(this, 9 * 6, String.valueOf(ChatColor.GOLD + "Аукционы " + ChatColor.RED + "[" + page + "/" + (Math.max(1, AuctionHandler.getAllItems().size() / 45) + 1)) + "]");
        initFirst();
        setItems();
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }


    public void initFirst() {
        clear();
        if (AuctionHandler.getAllItems().size() > 0) {
            int c = 45 * page;
            for (int i = c - 45; i < (Math.min(AuctionHandler.getAllItems().size(), c)); i++) {
                ItemStack itemStack = AuctionHandler.getAllItems().get(i);
                NBTItem nbtItem = new NBTItem(itemStack);
                if (nbtItem.getLong("time") < System.currentTimeMillis()) continue;
                replaceTime(itemStack, nbtItem.getLong("time"));
                getInventory().addItem(itemStack);
            }
        }
    }

    private void replaceTime(ItemStack itemStack, long ms) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore1 = meta.getLore();
        int index = 0;
        for (String str : lore1) {
            if (str.contains("§6⟳ §fИстекает: ")) {
                index = lore1.indexOf(str);
                break;
            }
        }
        long setTime = ms;
        int totalSecond = (int) ((setTime - System.currentTimeMillis()) / 1000);


        int hours = totalSecond / 3600;
        int minutes = (totalSecond % 3600) / 60;
        int seconds = totalSecond % 60;

        String time = String.format("§6⟳ §fИстекает: §6%02d ч %02d м %02d с", hours, minutes, seconds);
        if (index == 0) return;
        lore1.set(index, time);
        meta.setLore(lore1);
        itemStack.setItemMeta(meta);
    }


    private void setItems() {
        getInventory().setItem(46, UtilAuction.createItemStack(Material.ENDER_CHEST, 1, "§6[ฅ] §eХранилище", Arrays.asList("§fЗдесь хранятся товары,", "§fкоторвые Вы продаете")));
        getInventory().setItem(49, UtilAuction.createItemStack(Material.NETHER_STAR, 1, "§6[⟳] §eОбновить", Arrays.asList("§fЭта кнопка позволяет", "§fобновить список товаров")));
        getInventory().setItem(48, UtilAuction.createItemStack(Material.SPECTRAL_ARROW, 1, "§6[«] §eПредыдущая страница", null));
        getInventory().setItem(50, UtilAuction.createItemStack(Material.SPECTRAL_ARROW, 1, "§6[»] §eСледующая страница", null));
        getInventory().setItem(52, UtilAuction.createItemStack(Material.CHEST, 1, "§6[☆] §eКатегории", Arrays.asList("§fВ этом меню можно","§fудобно отсортировать","§fтовары по категориям")));
    }

    public void openCategory() {
        InventoryCategory inventoryCategory = new InventoryCategory(player);
        player.openInventory(inventoryCategory.getInventory());
    }


    public void refresh() {
        initFirst();
    }

    public void nextPage() {
        if (getItems() >= 49) {
            InventoryAuction inventoryAuction = new InventoryAuction(player, page + 1);
            player.openInventory(inventoryAuction.getInventory());
        }
    }

    public void backPage() {
        if (page > 1) {
            InventoryAuction inventoryAuction = new InventoryAuction(player, page - 1);
            player.openInventory(inventoryAuction.getInventory());
        }
    }


    public void clear() {
        for (int i = 0; i < 45; i++) {
            getInventory().setItem(i, null);
        }
    }

    public int getItems() {
        return (int) Arrays.stream(getInventory().getContents()).filter(Objects::nonNull).count();
    }


    public void buyItem(ItemStack customStack) {
        InventoryAccept inventoryAccept = new InventoryAccept(customStack, page);
        player.openInventory(inventoryAccept.getInventory());
    }

}
