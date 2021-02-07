package de.minigame.gtc.players;

import de.minigame.gtc.GTC;
import de.minigame.gtc.WorldData;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class PlayerScoreboard {
    public static void set(Player p) {
        UUID topPlayerUUID = Collections.max(GTC.playerList.entrySet(), Map.Entry.comparingByValue()).getKey();

        Player topPlayer = getServer().getPlayer(topPlayerUUID);
        if (topPlayer == null) return;

        WorldData wData = GTC.worldLists.get(p.getWorld().getName());
        wData.topPlayer = topPlayerUUID;

        ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
        if (manager == null) return;
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>§7> §3GTC §7<§8<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§a§lKills: §r§6" + GTC.playerList.get(p.getUniqueId())).setScore(3);
        obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(2);
        obj.getScore("§f§lPlayers: §r§6" + GTC.plugin.getServer().getOnlinePlayers().size()).setScore(1);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);

        p.setScoreboard(board);
    }
}
