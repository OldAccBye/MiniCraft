package de.minicraft.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.ServerBasics;
import de.minicraft.players.PlayerApi;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if (channel.equalsIgnoreCase( "bungeesystem:player"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput( bytes );
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase( "PlayerLogin"))
            {
                String group = in.readUTF();

                Document playerLoginDoc = PlayerApi.preLogin(p, group);
                if (!playerLoginDoc.getBoolean("boolean")) {
                    p.kickPlayer(playerLoginDoc.getString("reason"));
                    return;
                }

                PlayerApi.addAllPerm(p);
            }
        }
    }
}
