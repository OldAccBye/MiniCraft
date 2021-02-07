package de.minicraft.players.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.ServerBasics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        String p1Name = args[0];
        String p2Name = args[1];

        Player p1 = ServerBasics.plugin.getServer().getPlayerExact(p1Name);
        Player p2 = ServerBasics.plugin.getServer().getPlayerExact(p2Name);
        if (p1 != null && p2 != null) {
            p1.teleport(p2.getLocation());
            p1.sendMessage("§3§l[§2SERVER§3§l] §aDu wurdest zu §6" + p2Name + " §ateleportiert!");
            p2.sendMessage("§3§l[§2SERVER§3§l] §6" + p1Name + " §awurde zu dir teleportiert!");
            return true;
        }

        ByteArrayDataOutput get = ByteStreams.newDataOutput();
        get.writeUTF("tp");
        get.writeUTF(p1Name);
        get.writeUTF(p2Name);
        p.sendPluginMessage(ServerBasics.plugin, "basics:command", get.toByteArray());
        return true;
    }
}