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

        ByteArrayDataOutput get = ByteStreams.newDataOutput();
        get.writeUTF("tp");
        get.writeUTF(p1Name);
        get.writeUTF(p2Name);
        p.sendPluginMessage(ServerBasics.plugin, "basics:command", get.toByteArray());
        return true;
    }
}