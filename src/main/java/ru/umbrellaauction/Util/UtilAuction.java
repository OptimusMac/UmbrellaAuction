package ru.umbrellaauction.Util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import ru.umbrellaauction.Handlers.AuctionHandler;
import ru.umbrellaauction.Main;

import javax.swing.*;
import javax.swing.text.html.parser.Entity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UtilAuction {

    public static Info getLoot(String name) {
        int c = 0;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : AuctionHandler.getAllItems()) {
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.getString("owner").equals(name)) {
                c++;
                items.add(itemStack);
            }
        }
        return new Info(items, c);
    }


    public static ItemStack createItemStack(Material mat, int size, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(mat, size);
        ItemMeta meta = itemStack.getItemMeta();
        if (lore != null)
            meta.setLore(lore);
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material mat, int size, String name) {
        ItemStack itemStack = new ItemStack(mat, size);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public static ItemStack createItemStackEGG(ItemStack itemstack1, String name) {
        ItemStack itemStack = itemstack1;
        SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
        meta.setSpawnedType(EntityType.OCELOT);
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ArrayList<ItemStack> getSorted(String sorted) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        if (sorted.equals("BLOCKS")) {
            AuctionHandler.getAllItems().stream().filter(f -> f.getType().isBlock()).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("ARMOR")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isArmor).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("POTIONS")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isPotion).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("FOOD")) {
            AuctionHandler.getAllItems().stream().filter(e -> e.getType().isEdible() || e.getType().equals(Material.CAKE)).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("TOOLS")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isTool).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("WEAPONS")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isWeapon).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("BountyResources")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isBounty).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("Resources")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isResources).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("ENCHANTMENTS")) {
            AuctionHandler.getAllItems().stream().filter(e -> e.getType().equals(Material.ENCHANTED_BOOK)).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("SPAWNS")) {
            AuctionHandler.getAllItems().stream().filter(e -> e.getType().equals(Material.MONSTER_EGG) || e.getType().equals(Material.MOB_SPAWNER)).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("TALISMANS")) {
            AuctionHandler.getAllItems().stream().filter(e -> e.getType().equals(Material.TOTEM) && e.hasItemMeta() && e.getItemMeta().getLore().size() > 1).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("REALLYITEMS")) {
            AuctionHandler.getAllItems().stream().filter(e -> e.hasItemMeta() && Main.getInstance().list.contains(e.getItemMeta().getDisplayName())).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("CREATEPOTIONS")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isCreatePotions).forEach(itemStacks::add);
            return itemStacks;
        }
        if (sorted.equals("GRIEF")) {
            AuctionHandler.getAllItems().stream().filter(UtilAuction::isGrief).forEach(itemStacks::add);
            return itemStacks;
        }
        return itemStacks;
    }

    public static ArrayList<ItemStack> getSortedByPlayer(String sorted) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        AuctionHandler.getAllItems().stream().filter(e -> isOwner(e, sorted)).forEach(itemStacks::add);
        return itemStacks;
    }

    private static boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().name();
        if (!typeNameString.contains("_")) return false;
        if (typeNameString.split("_")[1].equals("HELMET")
                || typeNameString.split("_")[1].equals("CHESTPLATE")
                || typeNameString.split("_")[1].equals("LEGGINGS")
                || typeNameString.split("_")[1].equals("BOOTS")) {
            return true;
        }

        return false;
    }

    public static byte[] getByteToStack(ItemStack itemStack) {
        try {

            String encodedObject;

            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(itemStack);
            os.flush();
            return io.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean isOwner(ItemStack itemStack, String name) {
        NBTItem item = new NBTItem(itemStack);
        String owner = item.getString("owner");
        return owner.equals(name);
    }

    private static boolean isGrief(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        if (itemStack.getType().equals(Material.TNT) ||
                itemStack.getType().equals(Material.REDSTONE) ||
                itemStack.getType().equals(Material.POWERED_MINECART) ||
                itemStack.getType().equals(Material.PISTON_STICKY_BASE) ||
                itemStack.getType().equals(Material.FLINT_AND_STEEL) ||
                itemStack.getType().equals(Material.EXPLOSIVE_MINECART) ||
                itemStack.getType().equals(Material.REDSTONE_TORCH_ON)) {
            return true;
        }

        return false;
    }

    public static String name(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
    }

    private static boolean isBounty(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        if (itemStack.getType().equals(Material.DIAMOND) ||
                itemStack.getType().equals(Material.DIAMOND_ORE) ||
                itemStack.getType().equals(Material.LAPIS_ORE)) {
            return true;
        }

        return false;
    }

    private static boolean isCreatePotions(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        if (itemStack.getType().equals(Material.BLAZE_ROD) ||
                itemStack.getType().equals(Material.BLAZE_POWDER) ||
                itemStack.getType().equals(Material.CONCRETE_POWDER) ||
                itemStack.getType().equals(Material.NETHER_STALK) ||
                itemStack.getType().equals(Material.SUGAR) ||
                itemStack.getType().equals(Material.GOLDEN_CARROT) ||
                itemStack.getType().equals(Material.GLOWSTONE_DUST) ||
                itemStack.getType().equals(Material.GHAST_TEAR)) {
            return true;
        }

        return false;
    }


    private static boolean isResources(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        if (itemStack.getType().equals(Material.IRON_INGOT) ||
                itemStack.getType().equals(Material.GOLD_INGOT) ||
                itemStack.getType().equals(Material.COAL) ||
                itemStack.getType().equals(Material.REDSTONE) ||
                itemStack.getType().equals(Material.BONE)
        ) {
            return true;
        }

        return false;
    }

    private static boolean isWeapon(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().name();
        if (!typeNameString.contains("_")) return false;

        if (typeNameString.split("_")[1].equals("SWORD")
                || typeNameString.equals("BOW")) {
            return true;
        }

        return false;
    }

    private static boolean isPotion(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        if (itemStack.getType().equals(Material.POTION) || itemStack.getType().equals(Material.LINGERING_POTION) || itemStack.getType().equals(Material.LINGERING_POTION))
            return true;
        return false;
    }

    private static boolean isTool(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().name();
        if (!typeNameString.contains("_")) return false;
        if (typeNameString.split("_")[1].equals("AXE")
                || typeNameString.split("_")[1].equals("PICKAXE")
                || typeNameString.split("_")[1].equals("SPADE")
                || typeNameString.split("_")[1].equals("HOE")) {
            return true;
        }

        return false;
    }

    public static class Info {

        private List<ItemStack> items;
        private int count;

        public Info(List<ItemStack> items, int count) {
            this.count = count;
            this.items = items;
        }

        public int getCount() {
            return count;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }

}
