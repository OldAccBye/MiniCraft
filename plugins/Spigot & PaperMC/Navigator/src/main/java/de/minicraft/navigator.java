package de.minicraft;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;

public final class navigator extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {}

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

            ItemStack FFA = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta FFAM = FFA.getItemMeta();
            if (FFAM == null) {
                e.getPlayer().kickPlayer("Something gone wrong! please try to login again.");
                return;
            }
            FFAM.setDisplayName("§c§lFFA");
            FFAM.setLore(Collections.singletonList("§eFREE-FOR-ALL"));
            FFAM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            FFA.setItemMeta(FFAM);
            i.setItem(4, FFA);

            e.getPlayer().openInventory(i);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
            e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aConnecting to §6server§a...");

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("FFA");

            p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) { e.setCancelled(true); }
}
