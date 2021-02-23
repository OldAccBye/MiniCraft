package de.minicraft.lobby.player;

import de.miniapi.player.PlayerData;
import de.minicraft.lobby.Lobby;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerScoreboard {
    private static void set(Player p) {
        Scoreboard board = Lobby.plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective obj;

        obj = board.registerNewObjective("Lobby-Scoreboard", "dummy", Component.text("§a§lMiniCraft"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§l§m↽↽↽↽↽↽↽↽↽⇁⇁⇁⇁⇁⇁⇁⇁⇁").setScore(5);
        obj.getScore("§aGruppe").setScore(4);
        obj.getScore("§7↪ §rdefault").setScore(3);
        obj.getScore("").setScore(2);
        obj.getScore("§aCookies").setScore(1);
        obj.getScore("§7↪ §r0").setScore(0);
        p.setScoreboard(board);
    }

    public static void update(Player p) {
        new BukkitRunnable() {
            String lastGroup = "default";
            int lastCookies = 0;

            @Override
            public void run() {
                if (p == null) {
                    cancel();
                    return;
                }

                Objective obj = p.getScoreboard().getObjective("Lobby-Scoreboard");
                if (obj == null) {
                    set(p);
                    return;
                }

                PlayerData pData = Lobby.api.getPlayer(p.getUniqueId());
                if (pData == null) return;

                if (pData.group.equals(lastGroup) && pData.cookies.equals(lastCookies)) return;

                if (obj.getScoreboard() != null) {
                    obj.getScoreboard().resetScores("§7↪ §r" + lastGroup);
                    obj.getScoreboard().resetScores("§7↪ §r" + lastCookies);

                    obj.getScore("§7↪ §r" + pData.group).setScore(3);
                    obj.getScore("§7↪ §r" + pData.cookies).setScore(0);

                    lastGroup = pData.group;
                    lastCookies = pData.cookies;
                }
            }
        }.runTaskTimer(Lobby.plugin, 20L, 20L);
    }
}
