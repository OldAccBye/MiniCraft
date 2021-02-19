package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.miniapi.player.PlayerData;
import de.minicraft.lobby.Lobby;
import org.bukkit.*;
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

import java.util.*;

public class PlayerListener implements Listener {
    public final static HashMap<UUID, Long> lastAction = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        lastAction.put(e.getPlayer().getUniqueId(), new Date().getTime());
        e.getPlayer().sendTitle("§3Willkommen", "auf dem §aMiniCraft Netzwerk§r!", 10, 70, 20);

        // Inventory
        {
            e.getPlayer().getInventory().clear();
            ItemStack i;
            ItemMeta im;

            // Navigator
            {
                i = new ItemStack(Material.COMPASS);
                im = i.getItemMeta();
                if (im != null) {
                    im.setDisplayName("§3§lNavigator");
                    i.setItemMeta(im);
                    e.getPlayer().getInventory().setItem(4, i);
                }
            }

            // Emotes
            {
                i = new ItemStack(Material.ENDER_EYE);
                im = i.getItemMeta();
                if (im != null) {
                    im.setDisplayName("§3§lEmote");
                    i.setItemMeta(im);
                    e.getPlayer().getInventory().setItem(0, i);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || !(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        ItemMeta im = e.getItem().getItemMeta();
        if (im == null) return;
        Inventory i;
        ItemStack is;

        switch (im.getDisplayName()) {
            case "§3§lNavigator" -> {
                 i = Bukkit.createInventory(null, 9, "§3§lNavigator");

                // FFA ITEM
                is = new ItemStack(Material.DIAMOND_SWORD);
                im = is.getItemMeta();
                if (im == null) return;
                im.setDisplayName("§c§lFFA");
                im.setLore(Collections.singletonList("§eFREE-FOR-ALL"));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(0, is); // Set item

                // GTC ITEM
                is = new ItemStack(Material.CHICKEN_SPAWN_EGG);
                im = is.getItemMeta();
                if (im == null) return;
                im.setDisplayName("§c§lGTC");
                im.setLore(Collections.singletonList("§eGET-THE-CHICKEN"));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(1, is); // Set item

                e.getPlayer().openInventory(i);
            }
            case "§3§lEmote" -> {
                i = Bukkit.createInventory(null, 9, "§3§lEmote");

                // LIEBE ITEM
                is = new ItemStack(Material.MUSIC_DISC_CHIRP);
                im = is.getItemMeta();
                if (im == null) return;
                im.setDisplayName("§4§lLiebe");
                im.setLore(Collections.singletonList("§eZeig etwas Herz <3"));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(0, is); // Set item

                // WUT ITEM
                is = new ItemStack(Material.MUSIC_DISC_BLOCKS);
                im = is.getItemMeta();
                if (im == null) return;
                im.setDisplayName("§c§lWut");
                im.setLore(Collections.singletonList("§eSei wütend >.<"));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(1, is); // Set item

                // FREUDE ITEM
                is = new ItemStack(Material.MUSIC_DISC_CAT);
                im = is.getItemMeta();
                if (im == null) return;
                im.setDisplayName("§a§lFreude");
                im.setLore(Collections.singletonList("§eSei fröhlich :D"));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(2, is); // Set item

                e.getPlayer().openInventory(i);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) { if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true); }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        e.setCancelled(true);

        ItemMeta im = e.getCurrentItem().getItemMeta();
        if (im == null) return;

        Player p = (Player) e.getWhoClicked();
        PlayerData pData = Lobby.api.getPlayer(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        long currentDateTime = new Date().getTime();

        if (currentDateTime <= (lastAction.get(p.getUniqueId()) + 2000)) { // 2000 = 2 Sekunden
            p.sendMessage("§c[FEHLER]: §fWarte einen Augenblick bevor du eine neue Aktion ausführst!");
            return;
        }

        lastAction.computeIfPresent(p.getUniqueId(), (k, v) -> v = currentDateTime);

        switch (e.getView().getTitle()) {
            case "§3§lNavigator" -> {
                String iName = im.getDisplayName();
                if (iName.contains("§c§l")) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(iName.replace("§c§l", ""));

                    switch (iName.replace("§c§l", "").toLowerCase(Locale.ROOT)) {
                        case "gtc" -> out.writeInt(16);
                        case "ffa" -> out.writeInt(50);
                    }

                    p.sendPluginMessage(Lobby.plugin, "bungeesystem:lobby", out.toByteArray());
                }
            }
            case "§3§lEmote" -> {
                if (pData.group.equals("default")) {
                    p.sendMessage("§c[FEHLER]: §fDu musst Premium, Donator oder ein Teammitglied sein!");
                    return;
                }

                switch (im.getDisplayName()) {
                    case "§4§lLiebe" -> p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                    case "§c§lWut" -> p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                    case "§a§lFreude" -> p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                }
            }
        }

        p.closeInventory();
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.ADVENTURE) return;

        e.setCancelled(true);

        e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(1));
        e.getPlayer().setAllowFlight(false);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid()) return;

        if (!e.getPlayer().getAllowFlight() && e.getPlayer().getLocation().getY() <= 71.00000)
            e.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() != GameMode.ADVENTURE) return;

        e.getPlayer().setAllowFlight(true);
    }
}
