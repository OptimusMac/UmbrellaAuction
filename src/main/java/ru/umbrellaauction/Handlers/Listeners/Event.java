package ru.umbrellaauction.Handlers.Listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import ru.umbrellaauction.Handlers.Inventory.*;
import ru.umbrellaauction.Main;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Util.CustomStack;
import ru.umbrellaauction.Util.UtilAuction;

import javax.rmi.CORBA.Util;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Event implements Listener {


    @EventHandler
    public void onInventory(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryAuction) {
            InventoryAuction holder = (InventoryAuction) e.getInventory().getHolder();
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            if (slot == 49) {
                holder.refresh();
                return;
            }else if (slot == 48) {
                holder.backPage();
                return;
            }else if (slot == 50) {
                holder.nextPage();
                return;
            } else if (slot == 46) {
                InventoryStorage storage = new InventoryStorage(player);
                player.openInventory(storage.getInventory());
                return;
            } else if (slot == 52) {
                holder.openCategory();
                return;
            }

            if (slot <= 45) {

                if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
                int price = CustomStack.getPrice(e.getCurrentItem());
                String owner = CustomStack.getOwner(e.getCurrentItem());
                if (Main.econ.getBalance(player) >= price) {
                    if (!player.getName().equals(owner)) {
                        holder.buyItem(e.getCurrentItem());
                    } else {
                        player.sendMessage(ChatColor.RED + "Вы не можете купить сами у себя!");

                    }
                } else {
                    player.sendMessage(ChatColor.GOLD + "У вас недостаточно средств!");
                }
            }
        }
    }

    @EventHandler
    public void onBuy(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryAccept) {
            int slot = e.getRawSlot();
            e.setCancelled(true);
            InventoryAccept holder = (InventoryAccept) e.getInventory().getHolder();
            Player player = (Player) e.getWhoClicked();
            if (slot < 0 || slot > 3 * 9) return;
            ItemStack itemStack = holder.itemstack;

            if (itemStack == null || itemStack.getType().equals(Material.AIR))
                return;

            int price = CustomStack.getPrice(itemStack);
            Player owner = Bukkit.getPlayer(CustomStack.getOwner(itemStack));
            if (e.getCurrentItem().getDurability() == (short) 5) {
                if (Main.econ.getBalance(player) >= price) {
                    if (hasNull(player)) {
                        if (AuctionHandler.getAllItems().contains(itemStack)) {
                            player.getInventory().addItem(CustomStack.giveItem(itemStack));
                            AuctionHandler.removeItem(itemStack);
                            InventoryAuction inventoryAuction = new InventoryAuction(player, 1);
                            player.openInventory(inventoryAuction.getInventory());
                            inventoryAuction.refresh();
                            Main.econ.withdrawPlayer(player, price);
                            Main.econ.depositPlayer((OfflinePlayer) owner, price);
                            if(owner != null){
                                owner.sendMessage(ChatColor.YELLOW + "У вас купили " + ChatColor.GOLD + UtilAuction.name(itemStack) + ChatColor.YELLOW + " за " + ChatColor.GOLD + price + "$");
                            }
                            player.sendMessage(ChatColor.YELLOW + "Вы купили " + ChatColor.GOLD + UtilAuction.name(itemStack) + ChatColor.YELLOW + " за " + ChatColor.GOLD + price + "$");
                        } else {
                            player.sendMessage(ChatColor.RED + "Данный предмет уже купили!");
                        }
                    } else {
                        player.sendMessage(ChatColor.GOLD + "У вас недостаточно свободных слотов!");
                        player.closeInventory();
                    }

                } else {
                    player.sendMessage(ChatColor.GOLD + "У вас недостаточно средств!");
                    player.closeInventory();
                }
            } else if (e.getCurrentItem().getDurability() == (short) 14) {
                InventoryAuction inventoryAuction = new InventoryAuction(player, 1);
                player.openInventory(inventoryAuction.getInventory());
            }

        }
    }


    @EventHandler
    public void takeOnStorage(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryStorage) {
            InventoryStorage holder = (InventoryStorage) e.getInventory().getHolder();
            Player player = (Player) e.getWhoClicked();

            e.setCancelled(true);

            if (e.getRawSlot() == 49) {
                holder.back();
                return;
            }

            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
            if (hasNull(player)) {
                if (UtilAuction.getLoot(player.getName()).getItems().contains(itemStack)) {
                    AuctionHandler.removeItem(itemStack);
                    player.getInventory().addItem(CustomStack.giveItem(itemStack));
                    holder.refresh();
                } else {
                    player.sendMessage(ChatColor.RED + "Неизвестный предмет!");
                }
            } else {
                player.sendMessage(ChatColor.GOLD + "У вас недостаточно свободных слотов!");
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void openForCategory(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryCategory) {
            InventoryCategory holder = (InventoryCategory) e.getInventory().getHolder();
            int slot = e.getRawSlot();
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if (slot == 11) {
                InventoryAuction inventoryAuction = new InventoryAuction(player, 1);
                player.openInventory(inventoryAuction.getInventory());
                return;
            }
            if (holder.getTypeBySlot(slot) == null) return;
            ArrayList<ItemStack> sorted = UtilAuction.getSorted(holder.getTypeBySlot(slot));
            InventoryBySorted inventory = new InventoryBySorted(sorted, player, 1, holder.getTypeBySlot(slot));
            player.openInventory(inventory.getInventory());

        }
    }

    @EventHandler
    public void onSorted(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryBySorted) {
            InventoryBySorted holder = (InventoryBySorted) e.getInventory().getHolder();
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            if (slot == 49) {
                holder.refresh();
                return;
            }else if (slot == 48) {
                holder.backPage();
                return;
            } else if (slot == 50) {
                holder.nextPage();
                return;
            } else if (slot == 46) {
                InventoryStorage storage = new InventoryStorage(player);
                player.openInventory(storage.getInventory());
                return;
            } else if (slot == 52) {
                holder.openCategory();
                return;
            }
            if (slot <= 45) {

                if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
                int price = CustomStack.getPrice(e.getCurrentItem());
                String owner = CustomStack.getOwner(e.getCurrentItem());
                if (Main.econ.getBalance(player) >= price) {
                    if (!player.getName().equals(owner)) {
                        holder.buyItem(e.getCurrentItem());
                    } else {
                        player.sendMessage(ChatColor.RED + "Вы не можете купить сами у себя!");

                    }
                } else {
                    player.sendMessage(ChatColor.GOLD + "У вас недостаточно средств!");
                }
            }
        }
    }


    private boolean hasNull(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().getItem(i) == null)
                return true;
        }
        return false;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        BukkitRunnable run = new BukkitRunnable() {
            @Override
            public void run() {

            }
        };
        run.runTaskLater(Main.getInstance(), 20L);
    }

}
