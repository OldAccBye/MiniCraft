package de.minicraft.players.commands;

import de.minicraft.players.PlayerApi;
import de.minicraft.players.PlayerData;
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

        PlayerData pData = PlayerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return false;
        }

        if (p.hasPermission("minicraft.blockbreak")) {
            pData.permissions.setPermission("minicraft.blockbreak", false);
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage("BuildCommand deaktiviert!");
        } else {
            pData.permissions.setPermission("minicraft.blockbreak", true);
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage("BuildCommand aktiviert!");
        }

        return true;
    }
}