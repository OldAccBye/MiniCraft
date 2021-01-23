package de.minigame.ffa.listener;

import de.minigame.ffa.data;
import de.minigame.ffa.players.inventory;
import de.minigame.ffa.players.playerApi;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class player implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!playerApi.login(e.getPlayer().getUniqueId())) {
            p.kickPlayer("");
            return;
        }

        //p.teleport(data.loc);
        p.sendTitle("$3FFA", "§Welcome!", 10, 70, 20);
        inventory.getPlayerStandard(p);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) { e.setCancelled(true); }
}
