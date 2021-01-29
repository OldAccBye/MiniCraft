package de.minigame.gtc.players;

import de.minigame.gtc.GTC;
import de.minigame.gtc.worldData;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class scoreboard {
    public static void set(Player p) {
        UUID topPlayerUUID = Collections.max(GTC.playerList.entrySet(), Map.Entry.comparingByValue()).getKey();

        Player topPlayer = getServer().getPlayer(topPlayerUUID);
        if (topPlayer == null) return;

        worldData wData = GTC.worldLists.get(p.getWorld().getName());
        wData.topPlayer = topPlayerUUID;

        ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
        if (manager == null) return;
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>> §GTC §8<<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§a§lKills: §r§6" + GTC.playerList.get(topPlayerUUID)).setScore(3);
        obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(2);
        obj.getScore("§f§lPlayers: §r§6" + GTC.plugin.getServer().getOnlinePlayers().size()).setScore(1);
        obj.getScore("§e=-=-=-=-=-=-=-=§r").setScore(0);

        p.setScoreboard(board);
    }
}
