package de.minicraft.listener;

import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.List;

public class ChatListener implements Listener {
    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.isCancelled() || !(e.getSender() instanceof ProxiedPlayer) || !e.isProxyCommand() || !e.isCommand()) return;

        String command = e.getMessage().contains(" ") ? e.getMessage().split(" ", 2)[0] : e.getMessage();

        if (!Configs.commandList.getKeys().contains(command.replace("/", ""))) return;

        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            e.setCancelled(true);
            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        if (!pData.permissions.contains(Configs.commandList.getString(command.replace("/", "")))) {
            e.setCancelled(true);
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst diesen Befehl nicht ausführen!"));
        }
    }

    @EventHandler
    public void onProxyDefineCommands(ProxyDefineCommandsEvent e) {
        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

        List<String> pPermissions = PlayerApi.getAllPermissions(p);
        if (pPermissions == null) return;

        HashMap<String, Command> commandHashMap = new HashMap<>();

        for (String command : Configs.commandList.getKeys()) {
            if (pPermissions.contains(Configs.commandList.getString(command)))
                BungeeSystem.plugin.getProxy().getPluginManager().getCommands()
                        .stream()
                        .filter(cmdEntry -> command.equalsIgnoreCase(cmdEntry.getValue().getName()))
                        .forEach(cmdEntry -> commandHashMap.put(cmdEntry.getKey(), cmdEntry.getValue()));
        }

        e.getCommands().values().removeIf(cmd -> !commandHashMap.containsValue(cmd));
    }
}
