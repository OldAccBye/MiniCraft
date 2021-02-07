package de.minicraft.listener;

import java.util.Objects;

import de.minicraft.Configs;
import de.minicraft.players.PlayerApi;
import de.minicraft.players.PlayerData;
import de.minicraft.ServerBasics;
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
        Player p = e.getPlayer();

        PlayerData pData = PlayerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        String tempMsg = e.getMessage();
        e.setCancelled(true);

        if (tempMsg.contains("@")) {
            String tName = tempMsg.substring(tempMsg.indexOf("@") + 1).split(" ")[0];
            Player t = ServerBasics.plugin.getServer().getPlayerExact(tName);

            if (t == null)
                p.sendMessage("§c[FEHLER]: §fSpieler nicht gefunden!");
            else if (!tName.equals(p.getName())) {
                t.sendMessage("§3§l[§2SERVER§3§l] §aDu wurdest von " + p.getName() + " erwähnt.");
                tempMsg = tempMsg.replace("@" + p.getName(), "§b@" + p.getName() + "§r");
            }
        }

        for (Player t : p.getWorld().getPlayers())
            t.sendMessage(Configs.prefix.getString(pData.group) + p.getName() + " >> " + tempMsg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        String firstWord = e.getMessage().split(" ")[0].replace("/", "");
        if (!Configs.commandList.getKeys(true).contains(firstWord)) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDieser Befehl existiert nicht!");
            return;
        }

        if (!p.hasPermission(Objects.requireNonNull(Configs.commandList.getString(firstWord)))) {
            e.setCancelled(true);
            p.sendMessage("§c[FEHLER]: §fDu kannst diesen Befehl nicht ausführen!");
        }
    }

    @EventHandler
    public void onCommandTabSend(PlayerCommandSendEvent e) {
        e.getCommands().clear();

        PlayerData pData = PlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) return;

        for (String cmd : Configs.commandList.getKeys(false)) {
            String cmdPerm = Configs.commandList.getString(cmd);
            if (cmdPerm == null) return;

            if (e.getPlayer().hasPermission(cmdPerm))
                e.getCommands().add(cmd);
        }
    }
}