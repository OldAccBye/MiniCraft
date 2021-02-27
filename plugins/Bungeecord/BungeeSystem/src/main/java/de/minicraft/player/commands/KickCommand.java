package de.minicraft.player.commands;

import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KickCommand extends Command implements TabExecutor {
    public KickCommand() {
        super("kick");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length != 1) return Collections.emptyList();

        List<String> playerList = new ArrayList<>();

        BungeeSystem.plugin.getProxy().getPlayers().stream()
                .limit(10)
                .filter((target) -> target.getName().startsWith(args[0]))
                .forEach((target) -> playerList.add(target.getName()));

        return playerList;
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) cmdSender;

        if (args.length < 1) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fBenutze: /kick <PLAYER> <REASON>"));
            return;
        } else if (args.length < 2) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fEs wurde keine Begründung angegeben!"));
            return;
        }

        ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler konnte nicht gefunden werden!"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (String msg : args)
            message.append(args[args.length -1].equals(msg) ? msg : msg + " ");

        t.disconnect(new TextComponent("§cDu wurdest gekickt!\nGrund: §f" + message.toString().replace(args[0] + " ", "")));
        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler " + args[0] + " wurde erfolgreich gekickt!"));
    }
}
