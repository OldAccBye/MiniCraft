package de.minicraft.players.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minicraft.Configs;
import de.minicraft.players.PlayerApi;
import de.minicraft.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SetgroupCommand implements TabCompleter, CommandExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0 || args.length > 2) return Collections.emptyList();

        Player p = (Player) sender;

        List<String> results = new ArrayList<>();

        if (args.length == 1)
            for (Player t : p.getWorld().getPlayers())
                results.add(t.getName());

        return args.length == 1 ? results : Collections.singletonList(Configs.permissionsList.getKeys(true).toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length != 2) return false;

        Player p = (Player) sender;

        if (!Configs.permissionsList.getKeys(true).contains(args[1])) {
            p.sendMessage("§c[FEHLER]: §fDiese Gruppe existiert nicht!");
            return false;
        }

        Player t = Bukkit.getPlayerExact(args[0]);
        if (t == null) {
            p.sendMessage("§c[FEHLER]: §fSpieler nicht gefunden!");
            return false;
        }

        PlayerData pData = PlayerApi.get(t.getUniqueId());
        if (pData == null) {
            p.sendMessage("§c[FEHLER]: §fEin Fehler ist aufgetreten.");
            t.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return false;
        }

        PlayerApi.removeAllPerm(t);
        pData.group = args[1];
        PlayerApi.addAllPerm(t);

        p.sendMessage("§3§l[§2SERVER§3§l] §aSpieler " + t.getName() + " erhielt die Gruppe" + args[1]);
        t.sendMessage("§3§l[§2SERVER§3§l] §aDu erhielst die Gruppe " + args[1]);
        t.updateCommands();
        return true;
    }
}