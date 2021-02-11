package de.minicraft.player.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpServerCommand extends Command implements TabExecutor {
    public TpServerCommand() { super("tps"); }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length == 0 || args.length > 2) return Collections.emptyList();

        List<String> players = new ArrayList<>();

        for (ProxiedPlayer t : BungeeSystem.plugin.getProxy().getPlayers())
            players.add(t.getName());

        return players;
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length != 2) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu musst zwei Spieler angeben!"));
            return;
        }

        ProxiedPlayer p1 = BungeeSystem.plugin.getProxy().getPlayer(args[0]);
        if (p1 == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fSpieler §6" + args[0] + "§f nicht gefunden!"));
            return;
        }

        ProxiedPlayer p2 = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
        if (p2 == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fSpieler §6" + args[1] + "§f nicht gefunden!"));
            return;
        }

        String p1ServerName = p1.getServer().getInfo().getName();
        String p2ServerName = p2.getServer().getInfo().getName();

        if (p1ServerName.equals(p2ServerName)) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDiese Spieler befinden sich bereits auf dem selben Server!"));
            return;
        }

        if (p1ServerName.equals("GTC")) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDer Spieler §6" + args[0] + "§f befindet sich auf einem GTC Server!"));
            return;
        }

        p1.connect(p2.getServer().getInfo());
        p1.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aDu wurdest auf den Server von §6" + args[1] + "§a teleportiert!"));
    }
}
