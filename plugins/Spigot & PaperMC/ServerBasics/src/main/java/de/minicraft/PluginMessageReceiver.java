package de.minicraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.player.PlayerApi;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if (channel.equalsIgnoreCase("bungeesystem:player"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

            switch (in.readUTF()) {
                case "Login" -> {
                    PlayerApi.preLogin(p, in.readUTF());
                    p.updateCommands();
                }
                case "Update" -> {
                    switch (in.readUTF()) {
                        case "Group" -> {
                            PlayerApi.get(p.getUniqueId()).group = in.readUTF();
                            p.updateCommands();
                        }
                    }
                }
            }
        }
    }
}
