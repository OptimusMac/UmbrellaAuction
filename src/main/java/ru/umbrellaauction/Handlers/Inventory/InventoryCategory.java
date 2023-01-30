package ru.umbrellaauction.Handlers.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.umbrellaauction.Util.UtilAuction;

import java.util.Arrays;
import java.util.Collections;

public class InventoryCategory implements InventoryHolder {


    private Inventory inventory;
    private Player player;

    public InventoryCategory(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9 * 5, ChatColor.GOLD + "Категории");
        initItems();
    }


    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    private void initItems(){
        getInventory().setItem(11, UtilAuction.createItemStack(Material.HOPPER_MINECART, 1, ChatColor.YELLOW + "§8[§6Все товары§8]"));
        getInventory().setItem(12, UtilAuction.createItemStack(Material.DIRT, 1, ChatColor.YELLOW + "§8[§6Блоки§8]"));
        getInventory().setItem(13, UtilAuction.createItemStack(Material.DIAMOND_PICKAXE, 1, ChatColor.YELLOW + "§8[§6Инструменты§8]"));
        getInventory().setItem(14, UtilAuction.createItemStack(Material.DIAMOND_SWORD, 1, ChatColor.YELLOW + "§8[§6Оружие§8]"));
        getInventory().setItem(15, UtilAuction.createItemStack(Material.DIAMOND_CHESTPLATE, 1, ChatColor.YELLOW + "§8[§6Снаряжение§8]"));
        getInventory().setItem(20, UtilAuction.createItemStack(Material.IRON_INGOT, 1, ChatColor.YELLOW + "§8[§6Ценные ресурсы§8]"));
        getInventory().setItem(21, UtilAuction.createItemStack(Material.FERMENTED_SPIDER_EYE, 1, ChatColor.YELLOW + "§8[§6Прочие ресурсы§8]"));
        getInventory().setItem(22, UtilAuction.createItemStack(Material.BEETROOT, 1, ChatColor.YELLOW + "§8[§6Еда§8]"));
        getInventory().setItem(23, UtilAuction.createItemStack(Material.POTION, 1, ChatColor.YELLOW + "§8[§6Зелья§8]"));
        getInventory().setItem(24, UtilAuction.createItemStack(Material.ENCHANTED_BOOK, 1, ChatColor.YELLOW + "§8[§6Книга§8]"));
        getInventory().setItem(29, UtilAuction.createItemStackEGG(new ItemStack(Material.MONSTER_EGG), ChatColor.YELLOW + "§8[§cЯйца & Спавнеры§8]"));
        getInventory().setItem(30, UtilAuction.createItemStack(Material.TOTEM, 1, ChatColor.YELLOW + "§8[§cТалисманы§8]"));
        getInventory().setItem(31, UtilAuction.createItemStack(Material.GOLDEN_APPLE, 1, ChatColor.YELLOW + "§8[§aНастоящие вещи§8]"));
        getInventory().setItem(32, UtilAuction.createItemStack(Material.BREWING_STAND_ITEM, 1, ChatColor.YELLOW + "§8[§6Зельеварение§8]"));
        getInventory().setItem(33, UtilAuction.createItemStack(Material.REDSTONE_COMPARATOR, 1, ChatColor.YELLOW + "§8[§6Для Грифа§8]"));
    }

    public String getTypeBySlot(int slot){
        switch (slot){
            case 11:
                return "ALL";
            case 12:
                return "BLOCKS";
            case 13:
                return "TOOLS";
            case 14:
                return "WEAPONS";
            case 15:
                return "ARMOR";
            case 20:
                return "BountyResources";
            case 21:
                return "Resources";
            case 22:
                return "FOOD";
            case 23:
                return "POTIONS";
            case 24:
                return "ENCHANTMENTS";
            case 29:
                return "SPAWNS";
            case 30:
                return "TALISMANS";
            case 31:
                return "REALLYITEMS";
            case 32:
                return "CREATEPOTIONS";
            case 33:
                return "GRIEF";
        }
        return null;
    }

}
