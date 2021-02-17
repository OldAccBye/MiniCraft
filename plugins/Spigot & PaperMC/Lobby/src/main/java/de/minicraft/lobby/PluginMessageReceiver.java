package de.minicraft.lobby;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.lobby.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if (!channel.equals("bungeesystem:lobby")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        switch (in.readUTF()) {
            case "Login" -> {
                PlayerData pData = new PlayerData(in.readUTF(), in.readInt());
                Lobby.playerList.put(p.getUniqueId(), pData);
                p.updateCommands();
            }
            case "Update" -> {
                PlayerData pData = Lobby.playerList.get(p.getUniqueId());
                if (pData == null) {
                    p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
                    return;
                }

                switch (in.readUTF()) {
                    case "Group" -> pData.group = in.readUTF();
                }
            }
        }
    }
}
