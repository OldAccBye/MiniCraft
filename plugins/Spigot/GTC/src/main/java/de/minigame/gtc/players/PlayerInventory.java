package de.minigame.gtc.players;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInventory {
    public static void reset(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setHelmet(createItemStack(Material.LEATHER_HELMET, "§aHelm"));
        p.getInventory().setChestplate(createItemStack(Material.LEATHER_CHESTPLATE, "§aBrustpanzer"));
        p.getInventory().setLeggings(createItemStack(Material.LEATHER_LEGGINGS, "§aHose"));
        p.getInventory().setBoots(createItemStack(Material.LEATHER_BOOTS,  "§aSchuhe"));
        p.getInventory().setItem(0, createItemStack(Material.STONE_SWORD, "§aSchwert"));
        p.setHealth(20);
        p.setFoodLevel(20);
    }

    private static ItemStack createItemStack(Material m, String DM) {
        ItemStack i = new ItemStack(m);
        i.setAmount(1);
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(DM);
            im.setUnbreakable(true);
            i.setItemMeta(im);
        }

        return i;
    }
}
