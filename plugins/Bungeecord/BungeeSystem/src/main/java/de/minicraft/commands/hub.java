package de.minicraft.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class hub extends Command {
    public hub() {
        super("hub", "minicraft.commands.hub", "lobby");
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;

        ProxiedPlayer p = (ProxiedPlayer) cmdSender;
        ServerInfo lobby = ProxyServer.getInstance().getServerInfo("lobby");

        if (p.getServer().getInfo().equals(lobby)) return;

        p.connect(lobby);
    }
}
