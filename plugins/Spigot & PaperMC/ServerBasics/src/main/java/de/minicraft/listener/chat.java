package de.minicraft.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import de.minicraft.config;
import de.minicraft.players.playerApi;

public class chat implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String tempMsg = e.getMessage();
        Player p = e.getPlayer();

        e.setCancelled(true);

        if (tempMsg.contains("@")) {
            String t = tempMsg.substring(tempMsg.indexOf("@") + 1).split(" ")[0];

            if (Bukkit.getPlayerExact(t) == null)
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "playerNotFound"));
            else if (!t.equals(p.getName())) {
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "playerHasBeenMarked").replace("%username%", p.getName()));
                tempMsg = tempMsg.replace("@" + p.getName(), "§b@" + p.getName() + "§r");
            }
        }

        for (Player t : p.getWorld().getPlayers())
            t.sendMessage(config.getLanguageText(p.getUniqueId(), "prefix." + playerApi.getPlayer(p.getUniqueId()).group) + p.getName() + ": " + tempMsg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        String firstWord = e.getMessage().split(" ")[0].replace("/", "");
        if (!config.commandList.getKeys(true).contains(firstWord)) {
            e.setCancelled(true);
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "cmdNotExists"));
            return;
        }

        if (!p.hasPermission(Objects.requireNonNull(config.commandList.getString(firstWord)))) {
            e.setCancelled(true);
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "noPermission"));
        }
    }

    @EventHandler
    public void onCommandTabSend(PlayerCommandSendEvent e) {
        e.getCommands().clear();

        for (String cmd : config.commandList.getKeys(false))
            e.getCommands().add(cmd);
    }
}