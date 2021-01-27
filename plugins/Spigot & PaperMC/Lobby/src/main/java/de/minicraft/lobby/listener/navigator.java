package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.lobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

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
        } else if (e.getCurrentItem().getType() == Material.CHICKEN_SPAWN_EGG && meta.getDisplayName().equalsIgnoreCase("§c§lGTC")) {
            ByteArrayDataOutput get = ByteStreams.newDataOutput();
            get.writeUTF("getServerStatus");
            get.writeUTF("GTC");
            p.sendPluginMessage(lobby.plugin, "lobby:getserverinfo", get.toByteArray());
        }
    }
}
