package de.minigame.gtc.players;

import de.minigame.gtc.GTC;
import de.minigame.gtc.WorldData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class PlayerScoreboard {
    public static void set(Player p) {
        ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", Component.text("§8>§7> §3GTC §7<§8<"));

        if (!GTC.playerList.get(p.getUniqueId()).inRound) {
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("§e=-=-=-=-=-=-=-=").setScore(3);
            obj.getScore("§a§lGewonnen: §r§6" + GTC.api.getPlayer(p.getUniqueId()).gameData.getInteger("won") + "§rx").setScore(2);
        } else {
            Optional<Map.Entry<UUID, PlayerData>> topPlayerData = GTC.playerList.entrySet().stream().max((Comparator.comparing((data) -> data.getValue().kills)));
            if (topPlayerData.isEmpty()) return;

            Player topPlayer = GTC.plugin.getServer().getPlayer(topPlayerData.get().getKey());
            if (topPlayer == null) return;

            WorldData.topPlayerUUID = topPlayerData.get().getKey();

            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("§e=-=-=-=-=-=-=-=").setScore(4);
            obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(3);
            obj.getScore("§a§lKills: §r§6" + GTC.playerList.get(p.getUniqueId()).kills).setScore(2);
        }

        obj.getScore("§f§lSpieler: §r§6" + GTC.plugin.getServer().getOnlinePlayers().size()).setScore(1);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);

        p.setScoreboard(board);
    }
}
