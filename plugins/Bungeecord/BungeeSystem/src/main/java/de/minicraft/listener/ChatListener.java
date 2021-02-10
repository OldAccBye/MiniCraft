package de.minicraft.listener;

import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import de.minicraft.player.PlayerData;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;

public class ChatListener implements Listener {
    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.isCancelled() || !(e.getSender() instanceof ProxiedPlayer) || !e.isProxyCommand() || !e.isCommand()) return;

        for (String command : Configs.commandList.getKeys()) {
            if (!e.getMessage().startsWith("/" + command)) continue;

            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
            if (pData == null) return;

            if (!Configs.permissionsList.getStringList(pData.group).contains(Configs.commandList.getString(command))) {
                e.setCancelled(true);
                p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst diesen Befehl nicht ausführen!"));
            }
        }
    }

    @EventHandler
    public void onProxyDefineCommands(ProxyDefineCommandsEvent e) {
        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) return;

        HashMap<String, Command> commandHashMap = new HashMap<>();

        for (String command : Configs.commandList.getKeys()) {
            if (Configs.permissionsList.getStringList(pData.group).contains(Configs.commandList.getString(command)))
                BungeeSystem.plugin.getProxy().getPluginManager().getCommands()
                        .stream()
                        .filter(cmdEntry -> command.equalsIgnoreCase(cmdEntry.getValue().getName()))
                        .forEach(cmdEntry -> commandHashMap.put(cmdEntry.getKey(), cmdEntry.getValue()));
        }

        e.getCommands().values().removeIf(cmd -> !commandHashMap.containsValue(cmd));
    }
}
