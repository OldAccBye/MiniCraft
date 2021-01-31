package de.minicraft.commands;

import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class hub extends Command {
    public hub() {
        super("hub", "", "lobby");
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;

        ProxiedPlayer p = (ProxiedPlayer) cmdSender;
        ServerInfo lobby = BungeeSystem.plugin.getProxy().getServerInfo("lobby");

        if (p.getServer().getInfo().equals(lobby)) return;

        p.connect(lobby);
    }
}
