package de.minicraft.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class player implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().clear();

        ItemStack i = new ItemStack(Material.COMPASS);
        ItemMeta im = i.getItemMeta();
        if (im == null) {
            e.getPlayer().kickPlayer("Something gone wrong! please try to login again.");
            return;
        }
        im.setDisplayName("§3§lNavigator");
        i.setItemMeta(im);
        e.getPlayer().getInventory().setItem(4, i);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || !(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        if (e.getItem().getType() == Material.COMPASS) {
            Inventory i = Bukkit.createInventory(null, 9, "§3§lNavigator");

            // FFA ITEM
            ItemStack FFA = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta FFAM = FFA.getItemMeta();
            if (FFAM == null) return;
            FFAM.setDisplayName("§c§lFFA");
            FFAM.setLore(Collections.singletonList("§eFREE-FOR-ALL"));
            FFAM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            FFA.setItemMeta(FFAM);

            // GTC ITEM
            ItemStack GTC = new ItemStack(Material.CHICKEN_SPAWN_EGG);
            ItemMeta GTCM = GTC.getItemMeta();
            if (GTCM == null) return;
            GTCM.setDisplayName("§c§lGTC");
            GTCM.setLore(Collections.singletonList("§eGET-THE-CHICKEN"));
            GTCM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            GTC.setItemMeta(GTCM);

            i.setItem(0, FFA);
            i.setItem(1, GTC);

            e.getPlayer().openInventory(i);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) { e.setCancelled(true); }
}
