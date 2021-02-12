package de.minicraft.player.commands;

import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {
    public HubCommand() {
        super("hub", "", "lobby");
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) cmdSender;

        if (p.getServer().getInfo().getName().equals("lobby")) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu befindest dich bereits in der Lobby!"));
            return;
        }

        ServerInfo lobby = BungeeSystem.plugin.getProxy().getServerInfo("lobby");
        p.connect(lobby);
    }
}
