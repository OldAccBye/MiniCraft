package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.miniapi.player.PlayerData;
import de.minicraft.lobby.Lobby;
import de.minicraft.lobby.player.PlayerScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        PlayerScoreboard.update(e.getPlayer());

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
                    im.displayName(Component.text("§3§lNavigator"));
                    i.setItemMeta(im);
                    e.getPlayer().getInventory().setItem(0, i);
                }
            }

            // Emotes
            {
                i = new ItemStack(Material.ENDER_EYE);
                im = i.getItemMeta();
                if (im != null) {
                    im.displayName(Component.text("§3§lEmote"));
                    i.setItemMeta(im);
                    e.getPlayer().getInventory().setItem(4, i);
                }
            }

            // Premium
            {
                i = new ItemStack(Material.GOLD_INGOT);
                im = i.getItemMeta();
                if (im != null) {
                    im.displayName(Component.text("§6§lPremium"));
                    i.setItemMeta(im);
                    e.getPlayer().getInventory().setItem(3, i);
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

        Component displayNameComponent = im.displayName();
        if (displayNameComponent == null) return;
        String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(displayNameComponent);

        switch (displayName) {
            case "§3§lNavigator" -> {
                i = Bukkit.createInventory(null, 9, Component.text("§3§lNavigator"));

                // FFA ITEM
                is = new ItemStack(Material.DIAMOND_SWORD);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("§c§lFFA"));
                im.lore(Collections.singletonList(Component.text("§eFREE-FOR-ALL")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(0, is); // Set item

                // GTC ITEM
                is = new ItemStack(Material.CHICKEN_SPAWN_EGG);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("§c§lGTC"));
                im.lore(Collections.singletonList(Component.text("§eGET-THE-CHICKEN")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(1, is); // Set item

                e.getPlayer().openInventory(i);
            }
            case "§3§lEmote" -> {
                i = Bukkit.createInventory(null, 9, Component.text("§3§lEmote"));

                // LIEBE ITEM
                is = new ItemStack(Material.MUSIC_DISC_CHIRP);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("§4§lLiebe"));
                im.lore(Collections.singletonList(Component.text("§eZeig etwas Herz <3")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(0, is); // Set item

                // WUT ITEM
                is = new ItemStack(Material.MUSIC_DISC_BLOCKS);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("§c§lWut"));
                im.lore(Collections.singletonList(Component.text("§eSei wütend >.<")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(1, is); // Set item

                // FREUDE ITEM
                is = new ItemStack(Material.MUSIC_DISC_CAT);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("§a§lFreude"));
                im.lore(Collections.singletonList(Component.text("§eSei fröhlich :D")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(2, is); // Set item

                e.getPlayer().openInventory(i);
            }
            case "§6§lPremium" -> {
                i = Bukkit.createInventory(null, 9, Component.text("§6§lPremium"));

                // KAUFEN
                is = new ItemStack(Material.DIAMOND);
                im = is.getItemMeta();
                if (im == null) return;
                im.displayName(Component.text("1 Monat §6§lPremium §rfür §n950 Cookies"));
                im.lore(Arrays.asList(Component.text("§aAlle Vorteile als Premium findest du auf:"), Component.text("§nhttps://minicraft.network/premium")));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                is.setItemMeta(im);

                i.setItem(4, is); // Set item

                e.getPlayer().openInventory(i);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) { if (e.getPlayer().getGameMode() == GameMode.ADVENTURE) e.setCancelled(true); }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();
        if (p.getGameMode() != GameMode.ADVENTURE) return;

        PlayerData pData = Lobby.api.getPlayer(p.getUniqueId());
        if (pData == null) {
            p.kick(Component.text("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        e.setCancelled(true);

        ItemMeta im = e.getCurrentItem().getItemMeta();
        if (im == null || e.getClickedInventory() == p.getInventory()) return;

        long currentDateTime = new Date().getTime();

        if (currentDateTime <= (lastAction.get(p.getUniqueId()) + 2000)) { // 2000 = 2 Sekunden
            p.sendMessage("§c[FEHLER]: §fWarte einen Augenblick bevor du eine neue Aktion ausführst!");
            p.closeInventory();
            return;
        }

        lastAction.computeIfPresent(p.getUniqueId(), (k, v) -> v = currentDateTime);

        Component displayNameComponent = im.displayName();
        if (displayNameComponent == null) return;
        String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(displayNameComponent),
                viewTitle = LegacyComponentSerializer.legacyAmpersand().serialize(e.getView().title());

        switch (viewTitle) {
            case "§3§lNavigator" -> {
                if (displayName.contains("§c§l")) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("connect");
                    out.writeUTF(displayName.replace("§c§l", "").toLowerCase());
                    p.sendPluginMessage(Lobby.plugin, "bungeesystem:lobby", out.toByteArray());
                }
            }
            case "§3§lEmote" -> {
                if (pData.group.equals("default"))
                    p.sendMessage("§c[FEHLER]: §fDu musst Premium, Donator oder ein Teammitglied sein!");
                else {
                    switch (displayName) {
                        case "§4§lLiebe" -> p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                        case "§c§lWut" -> p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                        case "§a§lFreude" -> p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 2.25, 0), 10, 0.25f, 0.25f, 0.25f);
                    }
                }
            }
            case "§6§lPremium" -> {
                if (displayName.equals("1 Monat §6§lPremium §rfür §n950 Cookies")) {
                    if (pData.group.equals("premium"))
                        p.sendMessage("§c[FEHLER]: §fDu besitzt bereits Mitglied der Gruppe Premium!");
                    else if (!pData.group.equals("default"))
                        p.sendMessage("§c[FEHLER]: §fDu bist bereits Mitglied einer Gruppe mit all den selben Funktionen!");
                    else if (pData.cookies < 950)
                        p.sendMessage("§c[FEHLER]: §fDu besitzt nicht genügend Cookies!");
                    else {
                        pData.cookies -= 950;
                        pData.group = "premium";
                        p.updateCommands();
                        p.sendMessage("§3§l[§2SERVER§3§l] §aDu bist nun Mitglied der Gruppe §6Premium§a!");
                    }
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
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.ADVENTURE) return;

        e.getPlayer().setAllowFlight(e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()
                && e.getPlayer().getLocation().getY() <= 71.00000);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() != GameMode.ADVENTURE) return;

        e.getPlayer().setAllowFlight(true);
    }
}
