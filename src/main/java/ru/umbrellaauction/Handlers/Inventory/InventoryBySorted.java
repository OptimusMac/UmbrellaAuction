package ru.umbrellaauction.Handlers.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Util.UtilAuction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class InventoryBySorted implements InventoryHolder {


    private Inventory inventory;
    private Player player;
    private ArrayList<ItemStack> itemStacks;
    private int page;

    private String bySort;
    private Player toPlayer;
    public InventoryBySorted(ArrayList<ItemStack> itemStacks, Player player, int page, String bySort) {
        this.itemStacks = itemStacks;
        this.player = player;
        this.page = page;
        this.bySort = bySort;
        this.inventory = Bukkit.createInventory(this, 9 * 6, String.valueOf(ChatColor.GOLD + nameBySort(bySort) + ChatColor.RED + " [" + page + "/" + (Math.max(1, itemStacks.size() / 45) + 1)) + "]");
        initItems();
    }

    public InventoryBySorted(ArrayList<ItemStack> itemStacks, Player player, int page, Player toName) {
        this.itemStacks = itemStacks;
        this.player = player;
        this.page = page;
        this.toPlayer = toName;
        this.inventory = Bukkit.createInventory(this, 9 * 6, String.valueOf(ChatColor.GOLD + toPlayer.getName() + ChatColor.RED + " [" + page + "/" + Math.max(1, itemStacks.size() / 45)) + "]");
        initItems();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getItems() {
        return (int) Arrays.stream(getInventory().getContents()).filter(Objects::nonNull).count();
    }

    public void backPage() {
        if (page > 1) {
            InventoryAuction inventoryAuction = new InventoryAuction(player, page - 1);
            player.openInventory(inventoryAuction.getInventory());
        }
    }

    public void buyItem(ItemStack customStack){
        InventoryAccept inventoryAccept = new InventoryAccept(customStack, page);
        player.openInventory(inventoryAccept.getInventory());
    }

    public void nextPage() {
        if (getItems() >= 49) {
            InventoryBySorted inventoryAuction = new InventoryBySorted(itemStacks, player, page + 1, bySort);
            player.openInventory(inventoryAuction.getInventory());
        }
    }
    public void clear() {
        for (int i = 0; i < 45; i++) {
            getInventory().setItem(i, null);
        }
    }

    public void refresh() {
        clear();
        initItems();
    }

    private void initItems() {

        if (itemStacks.size() > 0) {
            int c = 45 * page;
            for (int i = c - 45; i < (Math.min(itemStacks.size(), c)); i++) {
                getInventory().addItem(itemStacks.get(i));
            }
        }
        setItems();
    }

    public void openCategory(){
        InventoryCategory inventoryCategory = new InventoryCategory(player);
        player.openInventory(inventoryCategory.getInventory());
    }

    private void setItems() {
        getInventory().setItem(46, UtilAuction.createItemStack(Material.ENDER_CHEST, 1, "§6[ฅ] §eХранилище", Arrays.asList("§fЗдесь хранятся товары,", "§fкоторвые Вы продаете")));
        getInventory().setItem(49, UtilAuction.createItemStack(Material.NETHER_STAR, 1, "§6[⟳] §eОбновить", Arrays.asList("§fЭта кнопка позволяет", "§fобновить список товаров")));
        getInventory().setItem(48, UtilAuction.createItemStack(Material.SPECTRAL_ARROW, 1, "§6[«] §eПредыдущая страница", null));
        getInventory().setItem(50, UtilAuction.createItemStack(Material.SPECTRAL_ARROW, 1, "§6[»] §eСледующая страница", null));
        getInventory().setItem(52, UtilAuction.createItemStack(Material.CHEST, 1, "§6[☆] §eКатегории", Arrays.asList("§fВ этом меню можно","§fудобно отсортировать","§fтовары по категориям")));
    }

    private String nameBySort(String sort){
        switch (sort){
            case "BLOCKS":
                return "Блоки";
            case "ALL":
                return "Всё";
            case "TOOLS":
                return "Инструменты";
            case "WEAPONS":
                return "Оружие";
            case "BountyResources":
                return "Ценные ресурсы";
            case "Resources":
                return "Прочие ресурсы";
            case "FOOD":
                return "Еда";
            case "POTIONS":
                return "Зелья";
            case "ENCHANTMENTS":
                return "Зачарования";
            case "SPAWNS":
                return "Яйца & Спавнера";
            case "TALISMANS":
                return "Талисманы";
            case "REALLYITEMS":
                return "Настоящие вещи";
            case "CREATEPOTIONS":
                return "Зельеварение";
            case "GRIEF":
                return "Для Грифа";
        }
        return null;
    }

}
