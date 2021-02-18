package de.minigame.ffa.players;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FFAInventory {
    public static void getPlayerStandard(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setHelmet(createItemStack(Material.IRON_HELMET, 1, "§aHelmet"));
        p.getInventory().setChestplate(createItemStack(Material.IRON_CHESTPLATE, 1, "§aChestplate"));
        p.getInventory().setLeggings(createItemStack(Material.IRON_LEGGINGS, 1, "§aLeggings"));
        p.getInventory().setBoots(createItemStack(Material.IRON_BOOTS, 1, "§aBoots"));
        p.getInventory().setItem(0, createItemStack(Material.STONE_SWORD, 1, "§aSword"));
        p.getInventory().setItem(1, createItemStack(Material.FISHING_ROD, 1, "§aFishing rod"));
        p.getInventory().setItem(2, createItemStack(Material.BOW, 1, "§aBow"));
        p.getInventory().setItem(3, createItemStack(Material.ARROW, 1, "§aArrow"));
        p.setHealth(20);
        p.setFoodLevel(20);
    }

    public static ItemStack createItemStack(Material m, int value, String DM) {
        ItemStack i = new ItemStack(m);
        i.setAmount(value);
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(DM);
            im.setUnbreakable(true);
            i.setItemMeta(im);
        }

        return i;
    }
}
