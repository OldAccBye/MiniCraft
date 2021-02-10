package de.minicraft.player.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.ServerBasics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;

        StringBuilder message = new StringBuilder();
        for (String msg : args) {
            if (args[args.length -1].equals(msg))
                message.append(msg);
            else
                message.append(msg).append(" ");
        }

        ByteArrayDataOutput get = ByteStreams.newDataOutput();
        get.writeUTF("broadcast");
        get.writeUTF(String.valueOf(message));
        p.sendPluginMessage(ServerBasics.plugin, "basics:command", get.toByteArray());
        return true;
    }
}
