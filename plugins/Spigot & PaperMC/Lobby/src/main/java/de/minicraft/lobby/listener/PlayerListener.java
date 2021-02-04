package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final HashMap<UUID, Boolean> doubleJump = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Inventory
        {
            e.getPlayer().getInventory().clear();
            ItemStack i = new ItemStack(Material.COMPASS);
            ItemMeta im = i.getItemMeta();
            if (im != null) {
                im.setDisplayName("§3§lNavigator");
                i.setItemMeta(im);
                e.getPlayer().getInventory().setItem(4, i);
            }
        }

        // Double jump
        {
            doubleJump.put(e.getPlayer().getUniqueId(), false);
        }
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        e.setCancelled(true);

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD && meta.getDisplayName().equalsIgnoreCase("§c§lFFA")) {
            ByteArrayDataOutput get = ByteStreams.newDataOutput();
            get.writeUTF("getServerStatus");
            get.writeUTF("FFA");
            p.sendPluginMessage(Lobby.plugin, "lobby:getserverinfo", get.toByteArray());
        } else if (e.getCurrentItem().getType() == Material.CHICKEN_SPAWN_EGG && meta.getDisplayName().equalsIgnoreCase("§c§lGTC")) {
            ByteArrayDataOutput get = ByteStreams.newDataOutput();
            get.writeUTF("getServerStatus");
            get.writeUTF("GTC");
            p.sendPluginMessage(Lobby.plugin, "lobby:getserverinfo", get.toByteArray());
        }
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        e.setCancelled(true);

        if (doubleJump.get(e.getPlayer().getUniqueId())) return;
        e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(1));
        doubleJump.computeIfPresent(e.getPlayer().getUniqueId(), (k, v) -> v = true);
        e.getPlayer().setAllowFlight(false);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid()) return;

        doubleJump.computeIfPresent(e.getPlayer().getUniqueId(), (k, v) -> v = false);
        e.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() != GameMode.SURVIVAL) return;

        e.getPlayer().setAllowFlight(true);
    }
}
