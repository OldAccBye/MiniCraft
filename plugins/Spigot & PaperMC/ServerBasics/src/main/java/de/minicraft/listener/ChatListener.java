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
            p.kickPlayer("Player data missing. Try to login again.");
            return;
        }

        String tempMsg = e.getMessage();
        e.setCancelled(true);

        if (tempMsg.contains("@")) {
            String t = tempMsg.substring(tempMsg.indexOf("@") + 1).split(" ")[0];

            if (ServerBasics.plugin.getServer().getPlayerExact(t) == null)
                p.sendMessage(Configs.getLanguageText(p.getUniqueId(), "playerNotFound"));
            else if (!t.equals(p.getName())) {
                p.sendMessage(Configs.getLanguageText(p.getUniqueId(), "playerHasBeenMarked").replace("%username%", p.getName()));
                tempMsg = tempMsg.replace("@" + p.getName(), "§b@" + p.getName() + "§r");
            }
        }

        for (Player t : p.getWorld().getPlayers())
            t.sendMessage(Configs.getLanguageText(p.getUniqueId(), "prefix." + pData.group) + p.getName() + ": " + tempMsg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        String firstWord = e.getMessage().split(" ")[0].replace("/", "");
        if (!Configs.commandList.getKeys(true).contains(firstWord)) {
            e.setCancelled(true);
            p.sendMessage(Configs.getLanguageText(p.getUniqueId(), "cmdNotExists"));
            return;
        }

        if (!p.hasPermission(Objects.requireNonNull(Configs.commandList.getString(firstWord)))) {
            e.setCancelled(true);
            p.sendMessage(Configs.getLanguageText(p.getUniqueId(), "noPermission"));
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