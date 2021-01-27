package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.lobby;
import de.minicraft.lobby.lobbyData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;

public final class navigator implements Listener {
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
            p.sendPluginMessage(lobby.plugin, "lobby:getserverinfo", get.toByteArray());

            Bukkit.getScheduler().scheduleSyncDelayedTask(lobby.plugin, () -> {
                lobbyData lData = lobby.lobbyLists.get("FFA");

                if (!lData.serverStatus) {
                    e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aServer offline!");
                    return;
                }

                e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aConnecting to §6server§a...");

                ByteArrayDataOutput con = ByteStreams.newDataOutput();
                con.writeUTF("Connect");
                con.writeUTF("FFA");

                p.sendPluginMessage(lobby.plugin, "BungeeCord", con.toByteArray());
            }, 10L);
        } else if (e.getCurrentItem().getType() == Material.CHICKEN_SPAWN_EGG && meta.getDisplayName().equalsIgnoreCase("§c§lGTC")) {
            ByteArrayDataOutput get = ByteStreams.newDataOutput();
            get.writeUTF("getServerStatus");
            get.writeUTF("GTC");
            p.sendPluginMessage(lobby.plugin, "lobby:getserverinfo", get.toByteArray());

            Bukkit.getScheduler().scheduleSyncDelayedTask(lobby.plugin, () -> {
                lobbyData lData = lobby.lobbyLists.get("GTC");

                if (!lData.serverStatus) {
                    e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aServer offline!");
                    return;
                }

                e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aConnecting to §6server§a...");

                ByteArrayDataOutput con = ByteStreams.newDataOutput();
                con.writeUTF("Connect");
                con.writeUTF("GTC");

                p.sendPluginMessage(lobby.plugin, "BungeeCord", con.toByteArray());
            }, 10L);
        }
    }
}
