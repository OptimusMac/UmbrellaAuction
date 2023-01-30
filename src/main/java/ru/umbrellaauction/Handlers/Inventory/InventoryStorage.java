package ru.umbrellaauction.Handlers.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.umbrellaauction.Main;
import ru.umbrellaauction.Util.UtilAuction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventoryStorage implements InventoryHolder {

    private Inventory inventory;
    private Player player;


    public InventoryStorage(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9 * 6, ChatColor.GOLD + "Хранилище" + ChatColor.RED + " [" + UtilAuction.getLoot(player.getName()).getCount() + "/" + Main.getInstance().maxLoot + "]");
        initItems();
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    private void initItems() {
        clear();
        getInventory().setItem(49, UtilAuction.createItemStack(Material.SPECTRAL_ARROW, 1, "§6➥ " + ChatColor.YELLOW + "Вернуться назад", null));
        for (ItemStack itemStack : UtilAuction.getLoot(player.getName()).getItems()) {
            int first = getInventory().firstEmpty();
            if (first - 1 > 43) return;

            getInventory().addItem(itemStack);
        }
    }

    public void clear() {
        for (int i = 0; i < 45; i++) {
            getInventory().setItem(i, null);
        }
    }

    public void back() {
        InventoryAuction inventoryAuction = new InventoryAuction(player, 1);
        player.openInventory(inventoryAuction.getInventory());
    }





    public void refresh() {
        initItems();
    }

}
