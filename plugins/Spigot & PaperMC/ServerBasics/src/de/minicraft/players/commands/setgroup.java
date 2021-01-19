package de.minicraft.players.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.minicraft.config;
import de.minicraft.players.playerApi;
import de.minicraft.players.playerPermissions;

public class setgroup implements TabCompleter, CommandExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0 || args.length > 2) return Collections.emptyList();

        Player p = (Player) sender;

        List<String> results = new ArrayList<>();

        if (args.length == 1)
            for (Player t : p.getWorld().getPlayers())
                results.add(t.getName());

        return args.length == 1 ? results : Collections.singletonList(config.permissionsList.getKeys(true).toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length != 2) return false;

        Player p = (Player) sender;

        if (!config.permissionsList.getKeys(true).contains(args[1])) {
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "groupNotExists"));
            return false;
        }

        Player t = Bukkit.getPlayerExact(args[0]);
        if (t == null) {
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "playerNotFound"));
            return false;
        }

        playerPermissions.removeAll(t.getUniqueId());
        playerApi.getPlayer(t.getUniqueId()).group = args[1];
        playerPermissions.add(t.getUniqueId());

        p.sendMessage(config.getLanguageText(p.getUniqueId(), "setPlayerRank").replace("%username%", t.getName()) + args[1] + ".");
        t.sendMessage(config.getLanguageText(p.getUniqueId(), "getPlayerRank") + args[1] + ".");
        return true;
    }
}