package de.minicraft.players.commands;

import de.minicraft.players.playerApi;
import de.minicraft.players.playerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class build implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player p = (Player) sender;

        playerData pData = playerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return false;
        }

        pData.permissions.setPermission("minicraft.blockbreak", !p.hasPermission("minicraft.blockbreak"));

        return true;
    }
}