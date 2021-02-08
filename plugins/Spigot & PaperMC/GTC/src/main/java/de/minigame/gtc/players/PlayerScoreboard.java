package de.minigame.gtc.players;

import de.minigame.gtc.GTC;
import de.minigame.gtc.WorldData;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class PlayerScoreboard {
    public static void set(Player p) {
        WorldData wData = GTC.worldLists.get(p.getWorld().getName());
        World world = GTC.plugin.getServer().getWorld(p.getWorld().getName());
        if (world == null) return;
        ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
        if (manager == null) return;
        Scoreboard board;
        Objective obj;

        board = manager.getNewScoreboard();

        if (!wData.roundStarted) {
            obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>§7> §3GTC §7<§8<");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("§e=-=-=-=-=-=-=-=").setScore(2);
            obj.getScore("§f§lSpieler: §r§6" + world.getPlayers().size()).setScore(1);
            obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);

            p.setScoreboard(board);
            return;
        }

        UUID topPlayerUUID = Collections.max(GTC.playerList.entrySet(), Map.Entry.comparingByValue()).getKey();

        Player topPlayer = GTC.plugin.getServer().getPlayer(topPlayerUUID);
        if (topPlayer == null) return;

        wData.topPlayer = topPlayerUUID;

        obj = board.registerNewObjective("GTC-ScoreBoard", "dummy", "§8>§7> §3GTC §7<§8<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§a§lKills: §r§6" + GTC.playerList.get(p.getUniqueId())).setScore(3);
        obj.getScore("§c§lTOP: §r§6" + topPlayer.getName()).setScore(2);
        obj.getScore("§f§lSpieler: §r§6" + world.getPlayers().size()).setScore(1);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(0);

        p.setScoreboard(board);
    }
}
