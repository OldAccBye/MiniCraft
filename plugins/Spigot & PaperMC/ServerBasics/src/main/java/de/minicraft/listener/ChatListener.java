package de.minicraft.listener;

import de.minicraft.Configs;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import de.minicraft.ServerBasics;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

        PlayerData pData = PlayerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        if (e.getMessage().contains("@")) {
            String tName = e.getMessage().substring(e.getMessage().indexOf("@") + 1).split(" ")[0];
            Player t = ServerBasics.plugin.getServer().getPlayerExact(tName);

            if (t == null)
                p.sendMessage("§c[FEHLER]: §fSpieler nicht gefunden!");
            else if (tName.equals(p.getName())) {
                p.sendMessage("§c[FEHLER]: §fDu kannst dich nicht selber markieren!");
            } else {
                t.sendMessage("§3§l[§2SERVER§3§l] §aDu wurdest von " + p.getName() + " erwähnt.");

                String beforeAt = e.getMessage().substring(0, e.getMessage().indexOf("@")),
                        afterAt = e.getMessage().substring(e.getMessage().lastIndexOf(tName) + tName.length());

                TextComponent mainComponent = new TextComponent(Configs.prefix.getString(pData.group) + p.getName() + ": " + beforeAt);
                TextComponent subComponent = new TextComponent("§b@" + t.getName() + "§r");
                subComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Profil anzeigen")));
                subComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minicraft.cf/p/" + t.getName()));
                mainComponent.addExtra(subComponent);
                mainComponent.addExtra(afterAt);

                for (Player players : p.getWorld().getPlayers())
                    players.spigot().sendMessage(mainComponent);

                return;
            }
        }

        for (Player players : p.getWorld().getPlayers())
            players.sendMessage(Configs.prefix.getString(pData.group) + p.getName() + ": " + e.getMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = PlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) {
            e.setCancelled(true);
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        String[] commandSplit = { e.getMessage() };

        if (e.getMessage().contains(" "))
             commandSplit = e.getMessage().split(" ", 2);

        if (!Configs.commandList.getKeys(true).contains(commandSplit[0].replace("/", ""))) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDieser Befehl existiert nicht!");
            return;
        }

        if (!Configs.permissionsList.getStringList(pData.group).contains(Configs.commandList.getString(commandSplit[0].replace("/", "")))) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDu kannst diesen Befehl nicht ausführen!");
        }
    }

    @EventHandler
    public void onCommandTabSend(PlayerCommandSendEvent e) {
        e.getCommands().clear();

        PlayerData pData = PlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) return;

        for (String command : Configs.commandList.getKeys(false))
            if (Configs.permissionsList.getStringList(pData.group).contains(Configs.commandList.getString(command)))
                e.getCommands().add(command);
    }
}