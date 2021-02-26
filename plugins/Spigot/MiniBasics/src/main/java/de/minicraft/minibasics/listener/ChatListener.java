package de.minicraft.minibasics.listener;

import de.miniapi.player.PlayerData;
import de.minicraft.minibasics.Configs;
import de.minicraft.minibasics.MiniBasics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();

        PlayerData pData = MiniBasics.api.getPlayer(p.getUniqueId());
        if (pData == null) {
            MiniBasics.plugin.getServer().getScheduler().runTask(MiniBasics.plugin, () -> p.kick(Component.text("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.")));
            return;
        }

        if (e.getMessage().contains("@")) {
            String tName = e.getMessage().substring(e.getMessage().indexOf("@") + 1).split(" ")[0];
            Player t = MiniBasics.plugin.getServer().getPlayerExact(tName);

            if (t == null)
                p.sendMessage("§c[FEHLER]: §fSpieler nicht gefunden!");
            else if (1 == 2) { // tName.equals(p.getName())
                p.sendMessage("§c[FEHLER]: §fDu kannst dich nicht selber markieren!");
            } else {
                t.sendMessage("§3§l[§2SERVER§3§l] §aDu wurdest von " + p.getName() + " erwähnt.");

                String beforeAt = e.getMessage().substring(0, e.getMessage().indexOf("@")),
                        afterAt = e.getMessage().substring(e.getMessage().lastIndexOf(tName) + tName.length());

                TextComponent textComponent = Component.text(pData.prefix + p.getName() + ": " + beforeAt)
                        .append(
                                Component.text("§b@" + t.getName() + "§r")
                                .hoverEvent(Component.text("§7<KLICK>§r Profil anzeigen"))
                                .clickEvent(ClickEvent.openUrl("https://minicraft.network/p/" + t.getUniqueId()))
                        )
                        .append(Component.text(afterAt));

                for (Player players : p.getWorld().getPlayers())
                    players.sendMessage(textComponent);

                return;
            }
        }

        for (Player players : p.getWorld().getPlayers())
            players.sendMessage(pData.prefix + p.getName() + ": " + e.getMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = MiniBasics.api.getPlayer(p.getUniqueId());
        if (pData == null) {
            e.setCancelled(true);
            p.kick(Component.text("[mb-oc-01] Es konnten keine Spielerdaten gefunden werden. Melde dies im Support, sollte dieser Fehler erneut auftauchen."));
            return;
        }

        String[] commandSplit = { e.getMessage() };

        if (e.getMessage().contains(" "))
             commandSplit = e.getMessage().split(" ", 2);

        if (!Configs.commandsList.getKeys(true).contains(commandSplit[0].replace("/", ""))) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDieser Befehl existiert nicht!");
            return;
        }

        if (!pData.permissions.contains(Configs.commandsList.getString(commandSplit[0].replace("/", "")))) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDu kannst diesen Befehl nicht ausführen!");
        }
    }

    @EventHandler
    public void onCommandTabSend(PlayerCommandSendEvent e) {
        e.getCommands().clear();

        PlayerData pData = MiniBasics.api.getPlayer(e.getPlayer().getUniqueId());
        if (pData == null) return;

        for (String command : Configs.commandsList.getKeys(false))
            if (pData.permissions.contains(Configs.commandsList.getString(command)))
                e.getCommands().add(command);
    }
}