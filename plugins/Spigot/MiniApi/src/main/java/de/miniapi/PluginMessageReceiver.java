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
            case "login" -> {
                PlayerData pData = new PlayerData(p.getName(), in.readUTF(), in.readInt());
                pData.prefix = Configs.permissionsList.getString(pData.group + ".prefix");
                switch (Configs.serverName) {
                    case "ffa" -> pData.ffaData = new PlayerData.FFA(in.readInt(), in.readInt());
                    case "gtc" -> pData.gtcData = new PlayerData.GTC(in.readInt());
                }
                pData.updatePermissions();
                MiniApi.playerList.put(p.getUniqueId(), pData);
                p.updateCommands();
            }
            case "update" -> {
                PlayerData pData = MiniApi.playerList.get(p.getUniqueId());
                if (pData == null) {
                    p.kick(Component.text("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                    return;
                }

                switch (in.readUTF().toLowerCase()) {
                    case "group" -> {
                        pData.group = in.readUTF();
                        pData.prefix = Configs.permissionsList.getString(pData.group + ".prefix");
                        pData.updatePermissions();
                        p.updateCommands();
                    }
                    case "cookies" -> pData.cookies = in.readInt();
                }
            }
        }
    }
}
