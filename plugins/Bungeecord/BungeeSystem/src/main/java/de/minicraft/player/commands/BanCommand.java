package de.minicraft.player.commands;

import de.minicraft.BungeeSystem;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BanCommand extends Command implements TabExecutor {
    public BanCommand() {
        super("ban");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length != 1) return Collections.emptyList();

        ProxiedPlayer p = (ProxiedPlayer) sender;

        List<String> playerList = new ArrayList<>();

        for (ProxiedPlayer players : p.getServer().getInfo().getPlayers())
            playerList.add(players.getName());

        return playerList;
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) cmdSender;

        if (args.length < 1) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fBenutze: /ban <PLAYER> <REASON> <TIMEFORMAT>"));
            return;
        } else if (args.length < 3) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fEs wurde keine Begründung oder Zeit angegeben!"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (String msg : args)
            message.append(args[args.length - 1].equals(msg) ? msg : msg + " ");

        String timeFormat = args[args.length - 1].substring(args[args.length - 1].length() - 1);
        long banTime;
        try {
            banTime = Long.parseLong(args[args.length - 1].replace(timeFormat, ""));
        } catch (Exception e) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fEs wird ein Format wie '3h' am Ende erwartet!"));
            return;
        }

        ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler konnte nicht gefunden werden!"));
            return;
        }
        PlayerData tData = BungeeSystem.playerList.get(t.getUniqueId());
        if (tData == null) {
            t.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            p.sendMessage(new TextComponent("§c[FEHLER]: §fSpieler " + args[0] + " besaß keine Spielerdaten und wurde nur gekickt!"));
            return;
        }

        String reason = message.toString().replace(args[0] + " ", "").replace(banTime + timeFormat, "");
        long currentDateTime = new Date().getTime();

        switch (timeFormat) {
            case "m" -> {
                tData.data.put("banned", true);
                tData.data.put("banSinceTimestamp", currentDateTime);
                tData.data.put("banExpiresTimestamp", currentDateTime + (banTime * 60000));
                tData.data.put("banReason", reason);
                tData.data.put("bannedFrom", p.getName());
            }
            case "h" -> {
                tData.data.put("banned", true);
                tData.data.put("banSinceTimestamp", currentDateTime);
                tData.data.put("banExpiresTimestamp", currentDateTime + (banTime * 3600000));
                tData.data.put("banReason", reason);
                tData.data.put("bannedFrom", p.getName());
            }
            case "d" -> {
                tData.data.put("banned", true);
                tData.data.put("banSinceTimestamp", currentDateTime);
                tData.data.put("banExpiresTimestamp", currentDateTime + (banTime * 86400000));
                tData.data.put("banReason", reason);
                tData.data.put("bannedFrom", p.getName());
            }
            default -> {
                p.sendMessage(new TextComponent("§c[FEHLER]: §fDie Zeit muss mit einem 'm', 'h' oder 'd' enden!"));
                return;
            }
        }

        p.disconnect(new TextComponent("§cDu wurdest aus diesem Netzwerk ausgeschlossen."));
        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler " + args[0] + " wurde erfolgreich ausgeschlossen!"));
    }
}
