package de.minigame.gtc.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class scoreboard {
    public static void set(Player p) {
        HashMap<UUID, Integer> players = new HashMap<>();

        for (playerData data : playerApi.playerList.values())
            if (data.inRound)
                players.put(data.pUUID, data.kills);

        UUID topPlayerUUID = Collections.max(players.entrySet(), Map.Entry.comparingByValue()).getKey();

        Player topPlayer = getServer().getPlayer(topPlayerUUID);
        if (topPlayer == null) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>> §GTC §8<<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§a§lKills: §r§6" + playerApi.playerList.get(p.getUniqueId()).kills).setScore(3);
        obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(2);
        obj.getScore("§f§lPlayers: §r§6" + Bukkit.getOnlinePlayers().size()).setScore(1);
        obj.getScore("§e=-=-=-=-=-=-=-=§r").setScore(0);
        p.setScoreboard(board);
    }
}
