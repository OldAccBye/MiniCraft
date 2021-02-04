package de.minicraft.players.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minicraft.players.SBPlayerApi;
import de.minicraft.players.SBPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.minicraft.SBConfig;

public class SetgroupCommand implements TabCompleter, CommandExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0 || args.length > 2) return Collections.emptyList();

        Player p = (Player) sender;

        List<String> results = new ArrayList<>();

        if (args.length == 1)
            for (Player t : p.getWorld().getPlayers())
                results.add(t.getName());

        return args.length == 1 ? results : Collections.singletonList(SBConfig.permissionsList.getKeys(true).toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length != 2) return false;

        Player p = (Player) sender;

        if (!SBConfig.permissionsList.getKeys(true).contains(args[1])) {
            p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "groupNotExists"));
            return false;
        }

        Player t = Bukkit.getPlayerExact(args[0]);
        if (t == null) {
            p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "playerNotFound"));
            return false;
        }

        SBPlayerData pData = SBPlayerApi.get(t.getUniqueId());
        if (pData == null) {
            p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "error"));
            t.kickPlayer("Player data missing. Try to login again.");
            return false;
        }

        SBPlayerApi.removeAllPerm(t.getUniqueId());
        pData.group = args[1];
        SBPlayerApi.addAllPerm(t.getUniqueId());

        p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "setPlayerRank").replace("%username%", t.getName()) + args[1] + ".");
        t.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "getPlayerRank") + args[1] + ".");
        t.updateCommands();
        return true;
    }
}