package ru.umbrellaauction.Handlers.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.umbrellaauction.Util.CustomStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventoryAccept implements InventoryHolder {

    private Inventory inventory;
    private int[] green = {0, 1, 2, 9, 10, 11, 18, 19, 20};
    private int[] gray = {3, 4, 5, 12, 14, 21, 22, 23};
    private int[] red = {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private int[][] massive = {green, gray, red};
    public ItemStack itemstack;

    public int page;

    public InventoryAccept(ItemStack itemStack, int page) {
        this.inventory = Bukkit.createInventory(this, 3 * 9, ChatColor.GOLD + "Подтверждение покупки");
        this.itemstack = itemStack;
        initItems();
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void initItems() {
        getInventory().setItem(13, itemstack);
        for (int i = 0; i < massive.length; i++) {
            for (int j = 0; j < massive[i].length; j++) {
                getInventory().setItem(massive[i][j], getItem(massive[i][j]));
            }
        }
    }

    private ItemStack getItem(int j) {


        for (int i = 0; i < green.length; i++) {
            if(green[i] == j){
                return CustomPane((short) 5);
            }
        }
        for (int i = 0; i < gray.length; i++) {
            if(gray[i] == j){
                return CustomPane((short) 15);
            }
        }
        for (int i = 0; i < red.length; i++) {
            if(red[i] == j){
                return CustomPane((short) 14);
            }
        }
        return null;

    }

    private ItemStack CustomPane(short s) {
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE);
        itemStack.setDurability(s);
        return itemStack;
    }

}
