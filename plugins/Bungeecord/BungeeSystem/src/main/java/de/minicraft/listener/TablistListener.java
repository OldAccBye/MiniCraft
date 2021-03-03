package de.minicraft.listener;

import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class TablistListener implements Listener {
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        try {
            BungeeSystem.plugin.getProxy().getScheduler().schedule(BungeeSystem.plugin, () -> {
                int cPP = e.getPlayer().getPing();

                String cPPT;
                if (cPP >= 135) cPPT = "§c" + cPP;
                else if (cPP > 75) cPPT = "§e" + cPP;
                else cPPT = "§7" + cPP;

                String header = " §b§l§m------§7§l§m[--§6 §aMiniCraft §7§l§m--]§b§l§m------§r \n"
                        + "§aServer: §7" + e.getPlayer().getServer().getInfo().getName() + " §f| "
                        + "§aPing: " + cPPT;
                String footer =  " §r§7§l§m--------------------------§r \n " + BungeeSystem.plugin.getProxy().getOnlineCount() + "/100 Online ";
                e.getPlayer().setTabHeader(new TextComponent(header), new TextComponent(footer));
            }, 0, 1, TimeUnit.SECONDS);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
