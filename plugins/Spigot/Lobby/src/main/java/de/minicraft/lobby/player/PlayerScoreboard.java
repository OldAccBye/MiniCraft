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

        obj = board.registerNewObjective("Lobby-Scoreboard", "dummy", Component.text("§f§lMiniCraft"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("").setScore(8);
        obj.getScore("§aGruppe").setScore(7);
        obj.getScore("§7↪ §rdefault").setScore(6);
        obj.getScore("§r").setScore(5);
        obj.getScore("§aCookies").setScore(4);
        obj.getScore("§7↪ §r0").setScore(3);
        obj.getScore("§r§r").setScore(2);
        obj.getScore("§aWebseite").setScore(1);
        obj.getScore("§7↪ §rminicraft.network").setScore(0);
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

                if (pData.data.getString("group").equals(lastGroup) && pData.data.getInteger("cookies").equals(lastCookies)) return;

                if (obj.getScoreboard() != null) {
                    obj.getScoreboard().resetScores("§7↪ §r" + lastGroup);
                    obj.getScoreboard().resetScores("§7↪ §r" + lastCookies);

                    obj.getScore("§7↪ §r" + pData.data.getString("group")).setScore(6);
                    obj.getScore("§7↪ §r" + pData.data.getInteger("cookies")).setScore(3);

                    lastGroup = pData.data.getString("group");
                    lastCookies = pData.data.getInteger("cookies");
                }
            }
        }.runTaskTimer(Lobby.plugin, 0L, 20L);
    }
}
