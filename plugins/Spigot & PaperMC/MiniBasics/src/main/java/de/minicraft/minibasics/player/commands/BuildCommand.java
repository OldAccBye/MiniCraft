package de.minicraft.minibasics.player.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        if (p.getGameMode() == GameMode.ADVENTURE) {
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage("BuildCommand deaktiviert!");
        } else {
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage("BuildCommand aktiviert!");
        }

        return true;
    }
}