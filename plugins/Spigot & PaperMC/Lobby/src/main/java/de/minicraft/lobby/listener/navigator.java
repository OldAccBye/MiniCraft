package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.lobby;
import de.minicraft.lobby.lobbyData;
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
        lobbyData lData;
        Date date = new Date();

        if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD && meta.getDisplayName().equalsIgnoreCase("§c§lFFA")) {
            if (!lobby.lobbyLists.containsKey("FFA")) lobby.lobbyLists.put("FFA", new lobbyData());

            lData = lobby.lobbyLists.get("FFA");
            ByteArrayDataOutput out;

            if (lData.lastUpdateTimestamp == null || date.getTime() > (lData.lastUpdateTimestamp + 30000)) {
                out = ByteStreams.newDataOutput();
                out.writeUTF("getServerStatus");
                out.writeUTF("FFA");
                p.sendPluginMessage(lobby.plugin, "lobby:getserverinfo", out.toByteArray());
            }

            if (!lData.serverStatus) {
                e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aServer offline!");
                return;
            }

            e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aConnecting to §6server§a...");

            out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("FFA");

            p.sendPluginMessage(lobby.plugin, "BungeeCord", out.toByteArray());
        } else if (e.getCurrentItem().getType() == Material.CHICKEN_SPAWN_EGG && meta.getDisplayName().equalsIgnoreCase("§c§lGTC")) {
            if (!lobby.lobbyLists.containsKey("GTC")) lobby.lobbyLists.put("GTC", new lobbyData());

            lData = lobby.lobbyLists.get("GTC");
            ByteArrayDataOutput out;

            if (lData.lastUpdateTimestamp == null || date.getTime() > (lData.lastUpdateTimestamp + 30000)) {
                out = ByteStreams.newDataOutput();
                out.writeUTF("getServerStatus");
                out.writeUTF("GTC");
                p.sendPluginMessage(lobby.plugin, "lobby:getserverinfo", out.toByteArray());
            }

            if (!lData.serverStatus) {
                e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aServer offline!");
                return;
            }

            e.getWhoClicked().sendMessage("§3§l[§2SERVER§3§l] §aConnecting to §6server§a...");

            out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("GTC");

            p.sendPluginMessage(lobby.plugin, "BungeeCord", out.toByteArray());
        }
    }
}
