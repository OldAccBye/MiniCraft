package de.minicraft.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private String tempMsg;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        e.setCancelled(true);

        tempMsg = e.getMessage();

        p.getWorld().getPlayers().stream().filter(players -> tempMsg.contains("@" + p.getName())).forEach(players -> {
            //if (players.getName() != p.getName())
            players.sendMessage(config.getLanguageText("playerHasBeenMarked").replace("%username%", p.getName()));

            tempMsg = tempMsg.replace("@" + players.getName(), "§b@" + players.getName() + "§r");
        });

        for (Player t : p.getWorld().getPlayers())
            t.sendMessage(config.getLanguageText("prefix." + playerApi.getPlayer(p.getUniqueId()).group) + p.getName() + ": " + tempMsg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        for (String cmd : config.commandList.getKeys(false)) {
            if (!e.getMessage().startsWith("/" + cmd)) continue;

            if (!p.hasPermission(Objects.requireNonNull(config.commandList.getString(cmd)))) {
                e.setCancelled(true);
                p.sendMessage(config.getLanguageText("noPermission"));
            }

            return;
        }

        e.setCancelled(true);
        p.sendMessage(config.getLanguageText("cmdNotExists"));
    }

    @EventHandler
    public void onCommandTabSend(PlayerCommandSendEvent e) {
        // Normal commands
        List<String> blockedCommands = Arrays.asList("?", "about", "help", "icanhasbukkit", "list", "me", "msg", "pl", "plugins", "teammsg", "tell", "tm", "trigger", "ver", "version", "w");
        e.getCommands().removeAll(blockedCommands);

        // Minecraft commands
        List<String> blockedMinecraftCommands = Arrays.asList("minecraft:help", "minecraft:list", "minecraft:me", "minecraft:msg", "minecraft:teammsg", "minecraft:tell", "minecraft:tm", "minecraft:trigger", "minecraft:w");
        e.getCommands().removeAll(blockedMinecraftCommands);

        // Bukkit commands
        List<String> blockedBukkitCommands = Arrays.asList("bukkit:?", "bukkit:about", "bukkit:help", "bukkit:pl", "bukkit:plugins", "bukkit:ver", "bukkit:version");
        e.getCommands().removeAll(blockedBukkitCommands);

        // "serverbasics:*" commands
        for (String cmd : config.commandList.getKeys(false))
            e.getCommands().remove("serverbasics:" + cmd);
    }
}