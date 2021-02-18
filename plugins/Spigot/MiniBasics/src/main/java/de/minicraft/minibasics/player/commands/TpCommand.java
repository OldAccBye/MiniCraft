package de.minicraft.minibasics.player.commands;

import de.minicraft.minibasics.MiniBasics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        if (args.length != 2) {
            p.sendMessage("§c[FEHLER]: §fDu musst zwei Spieler angeben!");
            return false;
        }

        Player p1 = MiniBasics.plugin.getServer().getPlayerExact(args[0]);
        if (p1 == null) {
            p.sendMessage("§c[FEHLER]: §fSpieler §6" + args[0] + "§f nicht gefunden!");
            return true;
        }

        Player p2 = MiniBasics.plugin.getServer().getPlayerExact(args[1]);
        if (p2 == null) {
            p.sendMessage("§c[FEHLER]: §fSpieler §6" + args[1] + "§f nicht gefunden!");
            return true;
        }

        p1.teleport(p2.getLocation());
        p1.sendMessage("§3§l[§2SERVER§3§l] §aDu wurdest zu §6" + args[1] + " §ateleportiert!");
        return true;
    }
}