package de.minicraft.player.commands;

import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
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
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
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