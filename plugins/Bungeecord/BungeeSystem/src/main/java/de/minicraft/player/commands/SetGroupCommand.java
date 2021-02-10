package de.minicraft.player.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetGroupCommand extends Command implements TabExecutor {
    public SetGroupCommand() {
        super("setgroup");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length == 0 || args.length > 2) return Collections.emptyList();

        ProxiedPlayer p = (ProxiedPlayer) sender;

        List<String> results = new ArrayList<>();

        if (args.length == 1)
            for (ProxiedPlayer t : p.getServer().getInfo().getPlayers())
                results.add(t.getName());

        List<String> groups = new ArrayList<>(Configs.permissionsList.getKeys());

        return args.length == 1 ? results : groups;
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) ||args.length != 2) return;
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (!Configs.permissionsList.getKeys().contains(args[1])) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDiese Gruppe existiert nicht!"));
            return;
        }

        ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fSpieler nicht gefunden!"));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(t.getUniqueId());
        if (pData == null) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fEin Fehler ist aufgetreten."));
            t.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Update");
        out.writeUTF("Group");
        out.writeUTF(pData.group);
        t.getServer().getInfo().sendData("bungeesystem:player", out.toByteArray());

        pData.group = args[1];
        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler " + t.getName() + " erhielt die Gruppe" + args[1]));
        t.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aDu erhielst die Gruppe " + args[1]));
    }
}
