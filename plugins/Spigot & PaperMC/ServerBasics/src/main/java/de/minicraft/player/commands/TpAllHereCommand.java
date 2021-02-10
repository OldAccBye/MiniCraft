package de.minicraft.player.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.ServerBasics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllHereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        ByteArrayDataOutput get = ByteStreams.newDataOutput();
        get.writeUTF("tpallhere");
        p.sendPluginMessage(ServerBasics.plugin, "basics:command", get.toByteArray());
        return true;
    }
}
