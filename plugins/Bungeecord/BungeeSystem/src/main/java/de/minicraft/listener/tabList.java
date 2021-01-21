package de.minicraft.listener;

import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class tabList implements Listener {
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        try {
            ProxyServer.getInstance().getScheduler().schedule(BungeeSystem.plugin, () -> {
                String header = " §b§l§m------§7§l§m[--§6 §lMini§2§lCraft §7§l§m--]§b§l§m------§r \n"
                        + "§aServer: §7" + e.getPlayer().getServer().getInfo().getName();
                String footer =  " §r§7§l§m--------------------------§r \n " + ProxyServer.getInstance().getOnlineCount() + "/100 Online ";
                e.getPlayer().setTabHeader(new TextComponent(header), new TextComponent(footer));
            }, 1, TimeUnit.SECONDS);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}