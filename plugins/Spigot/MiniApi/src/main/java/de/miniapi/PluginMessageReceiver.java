package de.miniapi;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.miniapi.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if (!channel.equals("bungeesystem:miniapi")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        switch (in.readUTF()) {
            case "update" -> {
                PlayerData pData = MiniApi.playerList.get(p.getUniqueId());
                if (pData == null) {
                    p.kick(Component.text("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                    return;
                }

                switch (in.readUTF().toLowerCase()) {
                    case "group" -> {
                        String group = in.readUTF();
                        pData.data.replace("group", group);
                        pData.prefix = Configs.permissionsList.getString(group + ".prefix");
                        pData.updatePermissions();
                        p.updateCommands();
                    }
                    case "cookies" -> pData.data.replace("cookies", in.readInt());
                }
            }
        }
    }
}
