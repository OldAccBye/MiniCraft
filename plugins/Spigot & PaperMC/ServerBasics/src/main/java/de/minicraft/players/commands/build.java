package de.minicraft.players.commands;

import de.minicraft.players.playerApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class build implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player p = (Player) sender;

        if (p.hasPermission("minicraft.blockbreak"))
            playerApi.get(p.getUniqueId()).permissions.setPermission("minicraft.blockbreak", false);
        else
            playerApi.get(p.getUniqueId()).permissions.setPermission("minicraft.blockbreak", true);

        return true;
    }
}
