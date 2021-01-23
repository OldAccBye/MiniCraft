package de.minicraft.players.commands;

import de.minicraft.config;
import de.minicraft.players.playerApi;
import de.minicraft.players.playerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class setlanguage implements TabCompleter, CommandExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length != 1) return Collections.emptyList();

        return Collections.singletonList(config.language.getKeys(false).toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) return false;

        Player p = (Player) sender;

        playerData pData = playerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return false;
        }

        if (!config.language.getKeys(true).contains(args[0])) {
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "languageNotFound"));
            return false;
        }

        pData.language = args[0];
        p.sendMessage(config.getLanguageText(p.getUniqueId(), "languageChanged"));

        return true;
    }
}