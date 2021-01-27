package de.minicraft.lobby.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.lobby;
import de.minicraft.lobby.lobbyData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Date;

public class pluginMessageReceived implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase("lobby:getserverinfo")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("getServerStatus")) {
                Date date = new Date();
                String serverName = in.readUTF();
                boolean serverStatus = in.readBoolean();

                lobbyData lData = lobby.lobbyLists.get(serverName);
                lData.lastUpdateTimestamp = date.getTime();
                lData.serverStatus = serverStatus;
            }
        }
    }
}
