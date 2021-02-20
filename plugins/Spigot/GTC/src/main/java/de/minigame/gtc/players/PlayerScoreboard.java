package de.minigame.gtc.players;

import de.minigame.gtc.GTC;
import de.minigame.gtc.WorldData;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class PlayerScoreboard {
    public static void set(Player p) {
        ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
        if (manager == null) return;
        Scoreboard board;
        Objective obj;

        board = manager.getNewScoreboard();

        if (!GTC.playerList.get(p.getUniqueId()).inRound) {
            obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>§7> §3GTC §7<§8<");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("§e=-=-=-=-=-=-=-=").setScore(3);
            obj.getScore("§a§lGewonnen: §r§6" + GTC.api.getPlayer(p.getUniqueId()).gtcData.won + "§rx").setScore(2);
            obj.getScore("§f§lSpieler: §r§6" + GTC.plugin.getServer().getOnlinePlayers().size()).setScore(1);
            obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);
            p.setScoreboard(board);
            return;
        }

        Optional<Map.Entry<UUID, PlayerData>> topPlayerData = GTC.playerList.entrySet().stream().max((Comparator.comparing((data) -> data.getValue().kills)));
        if (topPlayerData.isEmpty()) return;

        Player topPlayer = GTC.plugin.getServer().getPlayer(topPlayerData.get().getKey());
        if (topPlayer == null) return;

        WorldData.topPlayerUUID = topPlayerData.get().getKey();

        obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>§7> §3GTC §7<§8<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(3);
        obj.getScore("§a§lKills: §r§6" + GTC.playerList.get(p.getUniqueId()).kills).setScore(2);
        obj.getScore("§f§lSpieler: §r§6" + GTC.plugin.getServer().getOnlinePlayers().size()).setScore(1);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);

        p.setScoreboard(board);
    }
}
